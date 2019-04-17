package org.geogebra.desktop.main;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.util.HttpRequestD;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class GeoGebraServer {

	App app;
	GgbAPI api;
	String secret;

	public GeoGebraServer(String secret) {
		this.app = new AppDNoGui(new LocalizationD(3), false);
		api = app.getGgbApi();
		this.secret = secret;
	}

	public void start() {
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			server.createContext("/v0.1/json", new MyHandlerJSON());
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class MyHandlerJSON implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {

			app.reset();
			api.setRounding("10");

			boolean testing = false;

			String inputJSON = null;
			String result;
			try {
				inputJSON = HttpRequestD.readOutput(t.getRequestBody());

				
				
				if (inputJSON == null) {
					// ? syntax eg
					// http://localhost:8000/test?123=456
					inputJSON = t.getRequestURI().getQuery();
					testing = true;
				}

				Log.error(inputJSON);
				JSONObject topLevel = new JSONObject(inputJSON);
				if (secret != null) {
					Log.debug("secret = " + topLevel.get("secret"));

					if (!secret.equals(topLevel.get("secret"))) {
						writeError(t, "Wrong secret", testing);
						return;
					}

				}
				JSONArray json = topLevel.getJSONArray("commands");
				int i = 0;
				JSONArray results = new JSONArray();
				while (i < json.length()) {
					Object testVal = json.opt(i);
					if (!(testVal instanceof JSONObject)) {
						Log.debug("Invalid JSON:" + testVal);
						i++;
						continue;
					}
					JSONObject test = (JSONObject) testVal;


					
					String cmd = test.get("cmd").toString();
					String args = test.get("args").toString();
					Log.debug("cmd = " + cmd);
					Log.debug("args = " + args);

					// Log.error(api.evalCommandCAS("Expand[(x+1)^2]"));

					if ("evalCommand".equals(cmd)) {
						api.evalCommand(args);
					} else if ("evalLaTeX".equals(cmd)) {
						api.evalLaTeX(args, 0);
					} else if ("getValue".equals(cmd)) {
						results.put(api.getValue(args));
					} else if ("getValueString".equals(cmd)) {
						results.put(api.getValueString(args));
					} else if ("getLaTeXString".equals(cmd)) {
						results.put(api.getLaTeXString(args));
					} else if ("setRounding".equals(cmd)) {
						api.setRounding(args);
					} else if ("evalCommandCAS".equals(cmd)) {
						results.put(api.evalCommandCAS(args));
					} else if ("evalGeoGebraCAS".equals(cmd)) {
						results.put(app.getKernel().evaluateGeoGebraCAS(args,
								null, StringTemplate
										.fullFigures(StringType.GEOGEBRA)));
					} else if ("expressionEvaluatesToZero".equals(cmd)) {

						String answer = app.getKernel().evaluateGeoGebraCAS(
								"Simplify[" + args + "]", null,
								StringTemplate.defaultTemplate);

						results.put("0".equals(answer) ? "true" : "false");
					}

					i++;

				}
				result = results.toString();
			} catch (Throwable e) {

				e.printStackTrace();
				Log.debug(inputJSON);
				writeError(t, e.getMessage(), testing);
				return;
			}

			// StringBuilder result = new StringBuilder("[");

			writeOutput(t, result.toString(), testing);
			
		}
	}

	private void writeOutput(HttpExchange t, String message, boolean testing) {
		String encoding = "UTF-8";
		try {
			if (!testing) {
				t.getResponseHeaders().set("Content-type",
						"applcation/json; charset=" + encoding);
			}

			// http://stackoverflow.com/questions/6828076/how-to-correctly-compute-the-length-of-a-string-in-java
			t.sendResponseHeaders(200, message.getBytes(encoding).length);

			Writer out = new OutputStreamWriter(t.getResponseBody(), encoding);
			Log.debug("message = " + message);
			out.write(message);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeError(HttpExchange t, String message, boolean testing) {
		JSONObject error = new JSONObject();
		try {
			error.put("error", message + "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.debug("error = " + error);

		writeOutput(t, error.toString(), testing);

	}

}
