package org.geogebra.desktop.main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.util.HttpRequestD;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class GeoGebraServer {

	App app;
	GgbAPI api;
	private String secret;

	public GeoGebraServer(String secret) {
		this.app = new AppDNoGui(new LocalizationD(3), false);
		api = app.getGgbApi();
		this.secret = secret;

		HttpServer server;
		try {
			// setup http://localhost:8000/test?123=456
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			server.createContext("/v0.1/json", new MyHandlerJSON());
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class MyHandlerJSON implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			app.reset();

			String inputJSON = HttpRequestD.readOutput(t.getRequestBody());

			Log.error(inputJSON);

			JSONArray json;
			
			ArrayList results = new ArrayList();

			try {
				JSONObject topLevel = new JSONObject(inputJSON);
				if (secret != null) {
					Log.debug("secret = " + topLevel.get("secret"));

					if (!secret.equals(topLevel.get("secret"))) {
						writeOutput(t, "{\"error\":\"wrong secret\"}");
						return;
					}

				}
				json = topLevel.getJSONArray("commands");
				int i = 0;
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

					if ("evalCommand".equals(cmd)) {
						api.evalCommand(args);
					} else if ("evalLaTeX".equals(cmd)) {
						api.evalLaTeX(args, 0);
					} else if ("getValue".equals(cmd)) {
						results.add(api.getValue(args));
					} else if ("getValueString".equals(cmd)) {
						results.add(api.getValueString(args));
					} else if ("getLaTeXString".equals(cmd)) {
						results.add(api.getLaTeXString(args));
					}

					i++;

				}
			} catch (JSONException e) {

				e.printStackTrace();
				Log.debug(inputJSON);
				writeOutput(t, "{\"error\":\"" + e.getMessage() + "\"}");
				return;
			}

			StringBuilder result = new StringBuilder("[");
			for (int i = 0 ; i < results.size() ; i++) {
				Object obj = results.get(i);
				if (obj instanceof String) {
					result.append("'");
					result.append(obj.toString());
					result.append("'");				
				} else {
					result.append(obj.toString());					
				}
				result.append(",");
			}
			
			if (result.length() > 1) {
				result.setCharAt(result.length() - 1, ']');
			} else {
				result.append(']');
			}
			writeOutput(t, result.toString());
			
		}
	}

		private void writeOutput(HttpExchange t, String message) {
		try {
			t.getResponseHeaders().set("Content-type", "applcation/json");
			t.sendResponseHeaders(200, message.length());
			OutputStream os = t.getResponseBody();
			os.write(message.getBytes());
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

}
