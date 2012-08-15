/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package geogebra.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.extensions.appengine.auth.oauth2.AppEngineCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

/**
 * Object that manages credentials associated with this Drive application and
 * its users. Performs all OAuth 2.0 authorization, authorization code
 * upgrades, and token storage/retrieval.
 *
 * @author vicfryzel@google.com (Vic Fryzel)
 */
public class CredentialMediator {
  /**
   * The HTTP request used to make a request to this Drive application.
   * Required so that we can manage a session for the active user, and keep
   * track of their email address which is used to identify their credentials.
   * We also need this in order to access a bunch of request parameters like
   * {@code state} and {@code code}.
   */
  private HttpServletRequest request;

  /**
   * Scopes for which to request authorization.
   */
  private Collection<String> scopes;

  /**
   * Loaded data from war/WEB-INF/client_secrets.json.
   */
  private GoogleClientSecrets secrets;

  /**
   * CredentialStore at which Credential objects are stored.
   */
  private CredentialStore credentialStore;

  /**
   * JsonFactory to use in parsing JSON.
   */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /**
   * HttpTransport to use for external requests.
   */
  private static final HttpTransport TRANSPORT = new NetHttpTransport();

  /**
   * Key of session variable to store user IDs.
   */
  private static final String USER_ID_KEY = "userId";

  /**
   * Key of session variable to store user email addresses.
   */
  private static final String EMAIL_KEY = "emailAddress";

  /**
   * Creates a new CredentialsManager for the given HTTP request.
   *
   * @param request Request in which session credentials are stored.
   * @param clientSecretsStream Stream of client_secrets.json.
   * @throws InvalidClientSecretsException
   */
  public CredentialMediator(HttpServletRequest request,
      InputStream clientSecretsStream, Collection<String> scopes)
      throws InvalidClientSecretsException {
    this.request = request;
    this.scopes = scopes;
    this.credentialStore = new AppEngineCredentialStore();
    try {
      secrets = GoogleClientSecrets.load(
          JSON_FACTORY, clientSecretsStream);
    } catch (IOException e) {
      throw new InvalidClientSecretsException(
          "client_secrets.json is missing or invalid.");
    }
  }

  /**
   * @return Client information parsed from client_secrets.json.
   */
  protected GoogleClientSecrets getClientSecrets() {
    return secrets;
  }

  /**
   * Builds an empty GoogleCredential, configured with appropriate
   * HttpTransport, JsonFactory, and client information.
   */
  private Credential buildEmptyCredential() {
    return new GoogleCredential.Builder()
        .setClientSecrets(this.secrets)
        .setTransport(TRANSPORT)
        .setJsonFactory(JSON_FACTORY)
        .build();
  }

  /**
   * Retrieves stored credentials for the provided email address.
   *
   * @param userId User's Google ID.
   * @return Stored GoogleCredential if found, {@code null} otherwise.
   */
  private Credential getStoredCredential(String userId) {
    Credential credential = buildEmptyCredential();
    if(credentialStore.load(userId, credential)) {
      return credential;
    }
    return null;
  }

  /**
   * Deletes stored credentials for the provided email address.
   *
   * @param userId User's Google ID.
   */
  private void deleteStoredCredential(String userId) {
    if (userId != null) {
      Credential credential = getStoredCredential(userId);
      credentialStore.delete(userId, credential);
    }
  }

  /**
   * Exchange an authorization code for a credential.
   *
   * @param authorizationCode Authorization code to exchange for OAuth 2.0
   *        credentials.
   * @return Credential representing the upgraded authorizationCode.
   * @throws CodeExchangeException An error occurred.
   */
  private Credential exchangeCode(String authorizationCode)
      throws CodeExchangeException {
    // Talk to Google and upgrade the given authorization code to an access
    // token and hopefully a refresh token.
    try {
      GoogleTokenResponse response =
          new GoogleAuthorizationCodeTokenRequest(
              TRANSPORT,
              JSON_FACTORY,
              secrets.getWeb().getClientId(),
              secrets.getWeb().getClientSecret(),
              authorizationCode,
              secrets.getWeb().getRedirectUris().get(0)).execute();
      return buildEmptyCredential().setFromTokenResponse(response);
    } catch (IOException e) {
      e.printStackTrace();
      throw new CodeExchangeException();
    }
  }

  /**
   * Send a request to the UserInfo API to retrieve user e-mail address
   * associated with the given credential.
   *
   * @param credential Credential to authorize the request.
   * @return User's e-mail address.
   * @throws NoUserIdException An error occurred, and the retrieved email
   *                                 address was null.
   */
  private Userinfo getUserInfo(Credential credential)
      throws NoUserIdException {
    Userinfo userInfo = null;

    // Create a user info service, and make a request to get the user's info.
    Oauth2 userInfoService =
        Oauth2.builder(TRANSPORT, JSON_FACTORY)
            .setHttpRequestInitializer(credential).build();
    try {
      userInfo = userInfoService.userinfo().get().execute();
      if (userInfo == null) {
        throw new NoUserIdException();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return userInfo;
  }

  /**
   * Retrieve the authorization URL to authorize the user with the given
   * email address.
   *
   * @param emailAddress User's e-mail address.
   * @return Authorization URL to redirect the user to.
   */
  private String getAuthorizationUrl(String emailAddress) {
    // Generate an authorization URL based on our client settings,
    // the user's email address, and the state parameter, if present.
    GoogleAuthorizationCodeRequestUrl urlBuilder =
        new GoogleAuthorizationCodeRequestUrl(
            secrets.getWeb().getClientId(),
            secrets.getWeb().getRedirectUris().get(0),
            scopes)
            .setAccessType("offline")
            .setApprovalPrompt("force");
    // Propagate through the current state parameter, so that when the
    // user gets redirected back to our app, they see the file(s) they
    // were originally supposed to see before we realized we weren't
    // authorized.
    if (request.getParameter("state") != null) {
      urlBuilder.set("state", request.getParameter("state"));
    }
    if (emailAddress != null) {
      urlBuilder.set("user_id", emailAddress);
    }
    return urlBuilder.build();
  }

  /**
   * Deletes the credential of the active session.
   */
  public void deleteActiveCredential() {
    String userId = (String) request.getSession().getAttribute(USER_ID_KEY);
    this.deleteStoredCredential(userId);
  }

  /**
   * Retrieve credentials using the provided authorization code.
   *
   * This function exchanges the authorization code for an access token and
   * queries the UserInfo API to retrieve the user's e-mail address. If a
   * refresh token has been retrieved along with an access token, it is stored
   * in the application database using the user's e-mail address as key. If no
   * refresh token has been retrieved, the function checks in the application
   * database for one and returns it if found or throws a
   * NoRefreshTokenException with the authorization URL to redirect the user
   * to.
   *
   * @return Credential containing an access and refresh token.
   * @throws NoRefreshTokenException No refresh token could be retrieved from
   *         the available sources.
   */
  public Credential getActiveCredential() throws NoRefreshTokenException {
    String userId = (String) request.getSession().getAttribute(USER_ID_KEY);
    Credential credential = null;
    try {
      // Only bother looking for a Credential if the user has an existing
      // session with their email address stored.
      if (userId != null) {
        credential = getStoredCredential(userId);
      }

      // No Credential was stored for the current user or no refresh token is
      // available.
      // If an authorizationCode is present, upgrade it into an
      // access token and hopefully a refresh token.
      if ((credential == null || credential.getRefreshToken() == null)
          && request.getParameter("code") != null) {
        credential = exchangeCode(request.getParameter("code"));
        if (credential != null) {
          Userinfo userInfo = getUserInfo(credential);
          userId = userInfo.getId();
          request.getSession().setAttribute(USER_ID_KEY, userId);
          request.getSession().setAttribute(EMAIL_KEY, userInfo.getEmail());
          // Sometimes we won't get a refresh token after upgrading a code.
          // This won't work for our app, because the user can land directly
          // at our app without first visiting Google Drive. Therefore,
          // only bother to store the Credential if it has a refresh token.
          // If it doesn't, we'll get one below.
          if (credential.getRefreshToken() != null) {
            credentialStore.store(userId, credential);
          }
        }
      }

      if (credential == null || credential.getRefreshToken() == null) {
        // No refresh token has been retrieved.
        // Start a "fresh" OAuth 2.0 flow so that we can get a refresh token.
        String email = (String) request.getSession().getAttribute(EMAIL_KEY);
        String authorizationUrl = getAuthorizationUrl(email);
        throw new NoRefreshTokenException(authorizationUrl);
      }
    } catch (CodeExchangeException e) {
      // The code the user arrived here with was bad.  This pretty much never
      // happens. In a production application, we'd either redirect the user
      // somewhere like a home page, or show them a vague error mentioning
      // that they probably didn't arrive to our app from Google Drive.
      e.printStackTrace();
    } catch (NoUserIdException e) {
      // This is bad because it means the user either denied us access
      // to their email address, or we couldn't fetch it for some reason.
      // This is unrecoverable. In a production application, we'd show the
      // user a message saying that we need access to their email address
      // to work.
      e.printStackTrace();
    }
    return credential;
  }

  /**
   * Exception thrown when no refresh token has been found.
   */
  public static class NoRefreshTokenException extends Exception {

    /**
     * Authorization URL to which to redirect the user.
     */
    private String authorizationUrl;

    /**
     * Construct a NoRefreshTokenException.
     *
     * @param authorizationUrl The authorization URL to redirect the user to.
     */
    public NoRefreshTokenException(String authorizationUrl) {
      this.authorizationUrl = authorizationUrl;
    }

    /**
     * @return Authorization URL to which to redirect the user.
     */
    public String getAuthorizationUrl() {
      return authorizationUrl;
    }
  }

  /**
   * Exception thrown when client_secrets.json is missing or invalid.
   */
  public static class InvalidClientSecretsException extends Exception {
    /**
     * Construct an InvalidClientSecretsException with a message.
     *
     * @param message Message to escalate.
     */
    public InvalidClientSecretsException(String message) {
      super(message);
    }
  }

  /**
   * Exception thrown when no email address could be retrieved.
   */
  private static class NoUserIdException extends Exception {}

  /**
   * Exception thrown when a code exchange has failed.
   */
  private static class CodeExchangeException extends Exception {}

  public String getUserEmail() {
	String email = (String) request.getSession().getAttribute(EMAIL_KEY);
	if (email != null) {
		return email;
	}
	return "";
  }
}
