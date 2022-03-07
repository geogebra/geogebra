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

	public GeoGebraServer() {
		this.app = new AppDNoGui(new LocalizationD(3), false);
	}

	/**
	 * Starts the server on port 8000
	 */
	public void start() {
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			// server.createContext("/v0.1/json", new MyHandlerJSON()); TODO decide if we want this
			server.createContext("/v0.1/steps", new StepsHandlerJSON(app.getKernel().getParser()));
			server.start();
		} catch (IOException e) {
			Log.debug("Problem on server startup " + e);
		}
	}

	class MyHandlerJSON implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			app.reset();
			GgbAPI api = app.getGgbApi();
			api.setRounding("10");
			String inputJSON = null;
			String result;
			try {
				inputJSON = HttpRequestD.readOutput(t.getRequestBody());
				if (inputJSON == null) {
					// ? syntax eg
					// http://localhost:8000/test?123=456
					inputJSON = t.getRequestURI().getQuery();
				}

				Log.error(inputJSON);
				JSONObject topLevel = new JSONObject(inputJSON);

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
						results.put(api.getValueString(args, true));
					} else if ("getLaTeXString".equals(cmd)) {
						results.put(api.getLaTeXString(args));
					} else if ("setRounding".equals(cmd)) {
						api.setRounding(args);
					} else if ("evalCommandCAS".equals(cmd)) {
						results.put(api.evalCommandCAS(args, null));
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
				writeError(t, e.getMessage());
				return;
			}

			// StringBuilder result = new StringBuilder("[");

			writeOutput(t, result);
			
		}
	}

	/**
	 * @param httpExchange exchange
	 * @param responseBody response body
	 */
	public static void writeOutput(HttpExchange httpExchange, String responseBody) {
		String encoding = "UTF-8";
		try {
			httpExchange.getResponseHeaders().set("Content-type",
						"application/json; charset=" + encoding);

			// http://stackoverflow.com/questions/6828076/how-to-correctly-compute-the-length-of-a-string-in-java
			httpExchange.sendResponseHeaders(200, responseBody.getBytes(encoding).length);

			Writer out = new OutputStreamWriter(httpExchange.getResponseBody(), encoding);
			out.write(responseBody);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param httpExchange exchange
	 * @param message error message
	 */
	public static void writeError(HttpExchange httpExchange, String message) {
		JSONObject error = new JSONObject();
		try {
			error.put("error", message + "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.error("error = " + error);
		writeOutput(httpExchange, error.toString());
	}

}
