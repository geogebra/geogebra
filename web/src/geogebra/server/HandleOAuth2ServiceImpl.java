package geogebra.server;

import geogebra.server.HandleOAuth2ServiceImpl.GetCredentialsException.CodeExchangeException;
import geogebra.server.HandleOAuth2ServiceImpl.GetCredentialsException.NoRefreshTokenException;
import geogebra.server.HandleOAuth2ServiceImpl.GetCredentialsException.NoUserIdException;
import geogebra.web.asyncservices.HandleOAuth2Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author gabor
 * 
 * The server side implementation of oauth2, we have a client side also. 
 * Only one needed, but Google Drive not works yet from only client side.
 *
 */
public class HandleOAuth2ServiceImpl extends RemoteServiceServlet
implements HandleOAuth2Service {
	
	/**
     * 
     */
    private static final long serialVersionUID = -3350731385102830662L;

	private static final String CLIENTSECRETS_LOCATION = "client_secrets.json";

	private static final String REDIRECT_URI = "http://127.0.0.1:8888";

	private static final List<String> SCOPES = Arrays.asList(
		      "https://www.googleapis.com/auth/drive.file",
		      "https://www.googleapis.com/auth/userinfo.email",
		      "https://www.googleapis.com/auth/userinfo.profile");
	
	private static GoogleAuthorizationCodeFlow flow = null;
	
	/**
	   * Exception thrown when an error occurred while retrieving credentials.
	   */
	  public static class GetCredentialsException extends Exception {

	    /**
         * 
         */
        private static final long serialVersionUID = -2667855734268849048L;
		protected String authorizationUrl;

	    /**
	     * Construct a GetCredentialsException.
	     *
	     * @param authorizationUrl The authorization URL to redirect the user to.
	     */
	    public GetCredentialsException(String authorizationUrl) {
	      this.authorizationUrl = authorizationUrl;
	    }

	    /**
	     * Set the authorization URL.
	     */
	    public void setAuthorizationUrl(String authorizationUrl) {
	      this.authorizationUrl = authorizationUrl;
	    }
	    
	    /**
	     * Exception thrown when a code exchange has failed.
	     */
	    public static class CodeExchangeException extends GetCredentialsException {

	      /**
             * 
             */
            private static final long serialVersionUID = -383346433687491555L;

		/**
	       * Construct a CodeExchangeException.
	       *
	       * @param authorizationUrl The authorization URL to redirect the user to.
	       */
	      public CodeExchangeException(String authorizationUrl) {
	        super(authorizationUrl);
	      }

	    }

	    /**
	     * Exception thrown when no refresh token has been found.
	     */
	    public static class NoRefreshTokenException extends GetCredentialsException {

	      /**
             * 
             */
            private static final long serialVersionUID = -1074536487815168387L;

		/**
	       * Construct a NoRefreshTokenException.
	       *
	       * @param authorizationUrl The authorization URL to redirect the user to.
	       */
	      public NoRefreshTokenException(String authorizationUrl) {
	        super(authorizationUrl);
	      }

	    }

	    /**
	     * Exception thrown when no user ID could be retrieved.
	     */
	    public static class NoUserIdException extends Exception {

			/**
             * 
             */
            private static final long serialVersionUID = 6505550924931761808L;
	    }

	    /**
	     * @return the authorizationUrl
	     */
	    public String getAuthorizationUrl() {
	      return authorizationUrl;
	    }
	  }
	  
	  /**
	   * Retrieved stored credentials for the provided user ID.
	   *
	   * @param userId User's ID.
	   * @return Stored Credential if found, {@code null} otherwise.
	   */
	  static Credential getStoredCredentials(String userId) {
		  DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
		  Entity user = null;
		  Credential c = null;
		  
		  Query q = new Query("user");
		  q.addFilter("userId", FilterOperator.EQUAL, userId);
		  
		  PreparedQuery pq = dataStore.prepare(q);
		  
		  user = pq.asSingleEntity();
		  
		  if (user != null) {
			  c = new GoogleCredential();
			  c.setAccessToken((String) user.getProperty("accessToken"));
			  c.setRefreshToken((String) user.getProperty("refreshToken"));
		  }
		  return c;
	  }

	  /**
	   * Store OAuth 2.0 credentials in the application's database.
	   *
	   * @param userId User's ID.
	   * @param credentials The OAuth 2.0 credentials to store.
	   */
	  static void storeCredentials(String userId, Credential credentials) {
		  DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
		  Entity user = null;
		  
		  Query q = new Query("user");
		  q.addFilter("userId", FilterOperator.EQUAL, userId);
		  
		  PreparedQuery pq = dataStore.prepare(q);
		  if (pq.countEntities(FetchOptions.Builder.withLimit(1)) == 1) {
			  user = pq.asSingleEntity();
		  } else {
			  user = new Entity("user");
			  user.setProperty("userId", userId);
		  }
		  user.setProperty("accessToken", credentials.getAccessToken());
		  user.setProperty("refreshToken", credentials.getRefreshToken());
		  
		  dataStore.put(user);
	  }
	  
	  /**
	   * Build an authorization flow and store it as a static class attribute.
	   *
	   * @return GoogleAuthorizationCodeFlow instance.
	   * @throws IOException Unable to load client_secrets.json.
	   */
	  static GoogleAuthorizationCodeFlow getFlow() throws IOException {
	    if (flow == null) {
	      HttpTransport httpTransport = new NetHttpTransport();
	      JacksonFactory jsonFactory = new JacksonFactory();
	      InputStream is = HandleOAuth2ServiceImpl.class.getResourceAsStream(CLIENTSECRETS_LOCATION);
	      GoogleClientSecrets clientSecrets =
	          GoogleClientSecrets.load(jsonFactory,
	              is);
	      flow =
	          new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, SCOPES)
	              .setAccessType("offline").setApprovalPrompt("force").build();
	    }
	    return flow;
	  }

	  /**
	   * Exchange an authorization code for OAuth 2.0 credentials.
	   *
	   * @param authorizationCode Authorization code to exchange for OAuth 2.0
	   *        credentials.
	   * @return OAuth 2.0 credentials.
	   * @throws CodeExchangeException An error occurred.
	   */
	  static Credential exchangeCode(String authorizationCode)
	      throws CodeExchangeException {
	    try {
	      GoogleAuthorizationCodeFlow flow = getFlow();
	      GoogleTokenResponse response =
	          flow.newTokenRequest(authorizationCode).setRedirectUri(REDIRECT_URI).execute();
	      return flow.createAndStoreCredential(response, null);
	    } catch (IOException e) {
	      System.err.println("An error occurred: " + e);
	      throw new CodeExchangeException(null);
	    }
	  }
	  
	  static Credential getCredentialWithoutAuthorizationCode() {
		  return null;
	  }
	  

	  /**
	   * Send a request to the UserInfo API to retrieve the user's information.
	   *
	   * @param credentials OAuth 2.0 credentials to authorize the request.
	   * @return User's information.
	   * @throws NoUserIdException An error occurred.
	   */
	  static Userinfo getUserInfo(Credential credentials)
	      throws NoUserIdException {
	    Oauth2 userInfoService =
	        Oauth2.builder(new NetHttpTransport(), new JacksonFactory())
	            .setHttpRequestInitializer(credentials).build();
	    Userinfo userInfo = null;
	    try {
	      userInfo = userInfoService.userinfo().get().execute();
	    } catch (IOException e) {
	      System.err.println("An error occurred: " + e);
	    }
	    if (userInfo != null && userInfo.getId() != null) {
	      return userInfo;
	    } else {
	      throw new NoUserIdException();
	    }
	  }

	  /**
	   * Retrieve the authorization URL.
	   *
	   * @param emailAddress User's e-mail address.
	   * @param state State for the authorization URL.
	   * @return Authorization URL to redirect the user to.
	   * @throws IOException Unable to load client_secrets.json.
	   */
	  public static String getAuthorizationUrl(String emailAddress, String state) throws IOException {
	    GoogleAuthorizationCodeRequestUrl urlBuilder =
	        getFlow().newAuthorizationUrl().setRedirectUri(REDIRECT_URI).setState(state);
	    urlBuilder.set("user_id", emailAddress);
	    return urlBuilder.build();
	  }
	  
	  /**
	 * @return url to new users
	 * @throws IOException
	 */
	public static String getAutorizationrUrl() throws IOException {
		  GoogleAuthorizationCodeRequestUrl urlBuilder =
			        getFlow().newAuthorizationUrl().setRedirectUri(REDIRECT_URI);
		  return urlBuilder.build();
	  }

	  /**
	   * Retrieve credentials using the provided authorization code.
	   *
	   * This function exchanges the authorization code for an access token and
	   * queries the UserInfo API to retrieve the user's e-mail address. If a
	   * refresh token has been retrieved along with an access token, it is stored
	   * in the application database using the user's e-mail address as key. If no
	   * refresh token has been retrieved, the function checks in the application
	   * database for one and returns it if found or throws a NoRefreshTokenException
	   * with the authorization URL to redirect the user to.
	   *
	   * @param authorizationCode Authorization code to use to retrieve an access
	   *        token.
	   * @param state State to set to the authorization URL in case of error.
	   * @return OAuth 2.0 credentials instance containing an access and refresh
	   *         token.
	   * @throws NoRefreshTokenException No refresh token could be retrieved from
	   *         the available sources.
	   * @throws IOException Unable to load client_secrets.json.
	   */
	  public static Credential getCredentials(String authorizationCode, String state)
	      throws CodeExchangeException, NoRefreshTokenException, IOException {
	    String emailAddress = "";
	    try {
	      Credential credentials = exchangeCode(authorizationCode);
	      Userinfo userInfo = getUserInfo(credentials);
	      String userId = userInfo.getId();
	      emailAddress = userInfo.getEmail();
	      if (credentials.getRefreshToken() != null) {
	        storeCredentials(userId, credentials);
	        return credentials;
	      } else {
	        credentials = getStoredCredentials(userId);
	        if (credentials != null && credentials.getRefreshToken() != null) {
	          return credentials;
	        }
	      }
	    } catch (CodeExchangeException e) {
	      e.printStackTrace();
	      // Drive apps should try to retrieve the user and credentials for the current
	      // session.
	      // If none is available, redirect the user to the authorization URL.
	      e.setAuthorizationUrl(getAuthorizationUrl(emailAddress, state));
	      throw e;
	    } catch (NoUserIdException e) {
	      e.printStackTrace();
	    }
	    // No refresh token has been retrieved.
	    String authorizationUrl = getAuthorizationUrl(emailAddress, state);
	    throw new NoRefreshTokenException(authorizationUrl);
	  }

	public Boolean triggerLoginToGoogle() {
	    try {
	        Credential c  = getCredentials("", "");
	        return new Boolean(true);
        } catch (CodeExchangeException e) {
	        e.printStackTrace();
	        return new Boolean(false);
        } catch (NoRefreshTokenException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return new Boolean(false);
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return new Boolean(false);
        }
    }

}
