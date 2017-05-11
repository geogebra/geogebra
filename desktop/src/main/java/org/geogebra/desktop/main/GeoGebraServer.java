package org.geogebra.desktop.main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.util.debug.Log;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class GeoGebraServer {
	App app;
	GgbAPI api;

	public GeoGebraServer(App app) {
		this.app = app;
		api = app.getGgbApi();
	}

	public void run() {
		HttpServer server;
		try {
			// setup http://localhost:8000/test?123=456
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			server.createContext("/test", new MyHandler());
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			Log.error("" + t.getRequestURI().getQuery());
			api.evalCommand(t.getRequestURI().getQuery());
			String[] names = api.getAllObjectNames();
			String response = api.getValueString(names[names.length - 1]);
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	public static void main(String[] args) {
		LocalizationD loc = new LocalizationD(3);
		AppDNoGui app = new AppDNoGui(loc, false);
		new GeoGebraServer(app).run();
	}
}