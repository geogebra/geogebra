package geogebra.server;

import geogebra.server.fromclient.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class StartPageServlet extends AuthServlet {
	@Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp)
	      throws IOException, ServletException {
	    // Deserialize the state in order to specify some values to the DrEdit
	    // JavaScript client below.
		Collection<String> ids = new ArrayList<String>();
	    // Assume an empty ID in the list if no IDs were set.
	    ids.add("");
	    if (req.getParameter("state") != null) {
	      State state = new State(req.getParameter("state"));
	      if (state.ids != null && state.ids.size() > 0) {
	        ids = state.ids;
	      }
	    }
	    req.setAttribute("ids", new Gson().toJson(ids).toString());
	    req.setAttribute("client_id", new Gson().toJson(getClientId(req, resp)));
	    req.setAttribute("email_address", new Gson().toJson(getUserEmail(req, resp)));
	    req.getRequestDispatcher("/index.jsp").forward(req, resp);
	  }
}
