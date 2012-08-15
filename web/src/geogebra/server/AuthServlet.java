package geogebra.server;

import geogebra.server.CredentialMediator.InvalidClientSecretsException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;

public class AuthServlet extends HttpServlet {
	
	protected static final HttpTransport TRANSPORT = new NetHttpTransport();
	protected static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	/**
	   * Default MIME type of files created or handled by GeoGebraWeb.
	   *
	   * This is also set in the Google APIs Console under the Drive SDK tab.
	   */
	public static final String DEFAULT_MIMETYPE = "application/vnd.geogebra.file";

	/**
	   * MIME type to use when sending responses back to GeoGebraWeb JavaScript client.
	   */
	public static final String JSON_MIMETYPE = "application/json";
	
	/**
	   * Path component under war/ to locate client_secrets.json file.
	   */
	public static final String CLIENT_SECRETS_FILE_PATH
	      = "/WEB-INF/client_secrets.json";
	  

	/**
	   * Scopes for which to request access from the user.
	   */
	public static final List<String> SCOPES = Arrays.asList(
	      // Required to access and manipulate files.
	      "https://www.googleapis.com/auth/drive.file",
	      // Required to identify the user in our data store.
	      "https://www.googleapis.com/auth/userinfo.email",
	      "https://www.googleapis.com/auth/userinfo.profile");
	
	protected void sendError(HttpServletResponse resp, int code, String message) {
	    try {
	      resp.sendError(code, message);
	    } catch (IOException e) {
	      throw new RuntimeException(message);
	    }
	  }
	
	protected InputStream getClientSecretsStream() {
	    return getServletContext().getResourceAsStream(CLIENT_SECRETS_FILE_PATH);
	}
	
	protected CredentialMediator getCredentialMediator(
		      HttpServletRequest req, HttpServletResponse resp) {
		    // Authorize or fetch credentials.  Required here to ensure this happens
		    // on first page load.  Then, credentials will be stored in the user's
		    // session.
		    CredentialMediator mediator;
		    
		    
		    
		    try {
		      mediator = new CredentialMediator(req, getClientSecretsStream(), SCOPES);
		      
		      if (req.getParameter("error") != null || req.getParameter("user_act") != null) {
		    	  /*somehow it not works :
		    	   * req.getParameter("user_act") == "logged_out" ????
		    	   * Ok, I found out, java is not javascript, 
		    	   * possibly req.getParameter("user_act").eq("logged_out") :-)
		    	   * it will do for now anyway...
		    	   */
		    	  //user denied access, or logged out
		    	  Collection<String> ids = new ArrayList<String>();
		    	  
		  	      // Assume an empty ID in the list if no IDs were set.
		  	      ids.add("");
		    	  req.setAttribute("ids", new Gson().toJson(ids).toString());
		    	  req.setAttribute("client_id", new Gson().toJson(""));
		    	  req.setAttribute("email_address", new Gson().toJson(""));
		    	  mediator.deleteActiveCredential();
		  	      try {
	                req.getRequestDispatcher("/index.jsp").forward(req, resp);
                } catch (ServletException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }
		      }
		      mediator.getActiveCredential();
		      return mediator;
		    } catch (CredentialMediator.NoRefreshTokenException e) {
		      try {
		        resp.sendRedirect(e.getAuthorizationUrl());
		      } catch (IOException ioe) {
		        throw new RuntimeException("Failed to redirect user for authorization");
		      }
		      throw new RuntimeException("No refresh token found. Re-authorizing.");
		    } catch (InvalidClientSecretsException e) {
		      String message = String.format(
		          "This application is not properly configured: %s", e.getMessage());
		      sendError(resp, 500, message);
		      throw new RuntimeException(message);
		    }
	}
	
	protected Credential getCredential(
		      HttpServletRequest req, HttpServletResponse resp) {
		    try {
		      CredentialMediator mediator = getCredentialMediator(req, resp);
		      return mediator.getActiveCredential();
		    } catch(CredentialMediator.NoRefreshTokenException e) {
		      try {
		        resp.sendRedirect(e.getAuthorizationUrl());
		      } catch (IOException ioe) {
		        ioe.printStackTrace();
		        throw new RuntimeException("Failed to redirect for authorization.");
		      }
		    }
		    return null;
	}
	
	protected String getClientId(
		      HttpServletRequest req, HttpServletResponse resp) {
		    return getCredentialMediator(req, resp).getClientSecrets().getWeb()
		        .getClientId();
	}
	
	protected String getUserEmail(HttpServletRequest req, HttpServletResponse resp) {
		return getCredentialMediator(req, resp).getUserEmail();
	}
	
	 protected void deleteCredential(HttpServletRequest req, HttpServletResponse resp) {
		    CredentialMediator mediator = getCredentialMediator(req, resp);
		    mediator.deleteActiveCredential();
	 }
	
	
}
