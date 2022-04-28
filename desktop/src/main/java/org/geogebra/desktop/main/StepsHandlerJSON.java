package org.geogebra.desktop.main;

import java.io.IOException;

import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.stepbystep.StepSolver;
import org.geogebra.common.kernel.stepbystep.StepSolverImpl;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.util.HttpRequestD;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class StepsHandlerJSON implements HttpHandler {

	private final Parser parser;

	public StepsHandlerJSON(Parser parser) {
		this.parser = parser;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String inputJSON = HttpRequestD.readOutput(httpExchange.getRequestBody());
		JSONTokener tokener = new JSONTokener(inputJSON);
		try {
			JSONObject input = new JSONObject(tokener);
			StepSolver solver = new StepSolverImpl();
			String steps = solver.getSteps(input.getString("expression"),
					input.getString("type"), parser);
			GeoGebraServer.writeOutput(httpExchange, steps);
		} catch (JSONException e) {
			Log.debug(e);
			GeoGebraServer.writeError(httpExchange, e.getMessage());
		}
	}
}
