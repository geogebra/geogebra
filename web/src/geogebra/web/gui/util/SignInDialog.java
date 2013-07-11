package geogebra.web.gui.util;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.AuthenticationModel;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;
import geogebra.common.move.views.SuccessErrorRenderable;
import geogebra.html5.util.JSON;
import geogebra.html5.util.JavaScriptObjectWrapper;
import geogebra.html5.util.ggtapi.GeoGebraTubeAPI;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author gabor
 * 
 * Dialog for signing in users
 *
 */
public class SignInDialog extends DialogBox implements SuccessErrorRenderable {
	
	private App app;
	private Button cancel;
	/**
	 * userName for forum textfield
	 */
	TextBox forumUserName;
	private PasswordTextBox forumPassword;
	private Button submitButton;
	private Button googleLogin;
	private Button facebookLogin;
	private Button twitterLogin;
	private	Button openidLogin;
	private Label errormsg;
	
	private KeyUpHandler enableSubmit;

	/**
	 * creates a SignInDialog for log in to different
	 * accounts
	 */
	public SignInDialog(final App app) {
		super(false, true);
		this.app = app;
		
		addStyleName("signInDialog");
		FlowPanel container = new FlowPanel();
		container.addStyleName("signInDialogContainer");
		
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("headerPanel");
		final SignInDialog t = this;
		buttonPanel.add(cancel = new Button(app.getMenu("X"), new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				t.hide();
			}
		}));
		container.add(buttonPanel);
		
		FlowPanel ggtLoginPanel = new FlowPanel();
		ggtLoginPanel.addStyleName("ggtLoginPanel");
		
		FlowPanel ggtLoginHeader = new FlowPanel();
		ggtLoginHeader.addStyleName("headerLabel");
		Label ggtLogin = new Label(app.getMenu("LogIntoGeoGebraTube"));
		ggtLoginHeader.add(ggtLogin);
		ggtLoginPanel.add(ggtLoginHeader);
		
		FlexTable logins = new FlexTable();
		logins.addStyleName("loginspanel");
		
		Label forumAccount = new Label(app.getMenu("UsingYourForumAccount"));
		logins.getFlexCellFormatter().setColSpan(0, 0, 2);
		logins.setWidget(0, 0, forumAccount);
		
		enableSubmit = new KeyUpHandler() {
			
			public void onKeyUp(KeyUpEvent event) {
				errormsg.setText("");
				submitButton.setEnabled(!isloginFieldsAreEmpty());
			}
		};
		
		forumUserName = new TextBox();
		forumUserName.getElement().setAttribute("placeholder", app.getMenu("username"));
		forumUserName.setTabIndex(1);
		
		forumUserName.addKeyUpHandler(enableSubmit);
		
		logins.getFlexCellFormatter().setColSpan(1, 0, 2);
		logins.setWidget(1, 0, forumUserName);
		
		forumPassword = new PasswordTextBox();
		forumPassword.getElement().setAttribute("placeholder", app.getMenu("password"));
		forumPassword.setTabIndex(2);
		
		forumPassword.addKeyUpHandler(enableSubmit);
		forumPassword.addKeyDownHandler(new KeyDownHandler() {
			
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					submitButton.click();
				}
			}
		});
		
		logins.getFlexCellFormatter().setColSpan(2, 0, 2);
		logins.setWidget(2, 0, forumPassword);
		
		FlowPanel anchorsContainer = new FlowPanel();
		anchorsContainer.addStyleName("anchorsContainer");
		
		Anchor forgotPassword = new Anchor(app.getMenu("forgottenPassword"));
		forgotPassword.setHref(GeoGebraConstants.GGT_FORGOT_PWD_URL);
		forgotPassword.setTarget("_blank");
		anchorsContainer.add(forgotPassword);
		
		Anchor register = new Anchor(app.getMenu("register"));
		register.setHref(GeoGebraConstants.GGT_REGISTER_URL);
		register.setTarget("_blank");
		anchorsContainer.add(register);
		
		logins.getFlexCellFormatter().setColSpan(3, 0, 2);
		logins.setWidget(3, 0, anchorsContainer);
		
		errormsg = new Label();
		errormsg.addStyleName("loginerror");
		
		logins.setWidget(4, 0, errormsg);
		
		submitButton = new Button(app.getMenu("SignIn"));
		submitButton.getElement().setTabIndex(3);
		logins.setWidget(4, 1, submitButton);
		
		submitButton.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				final String userName = forumUserName.getText();
				String passwd = forumPassword.getText();
				(GeoGebraTubeAPI.getInstance(geogebra.common.move.ggtapi.models.GeoGebraTubeAPI.test_url)).logIn(userName, passwd, new RequestCallback() {
					
					public void onResponseReceived(Request request, Response response) {
						JavaScriptObjectWrapper json = (JavaScriptObjectWrapper) JSON.parse(response.getText());
						
						JSONObject resp = new JSONObject();

						if (json.getKeyAsString("error") != null) {
							resp.put("error", new JSONString(json.getKeyAsString("error")));
						} else {					
							String token_value = json.getKeyAsObject("responses")
									.getKeyAsObject("response")
									.getKeyAsObject("token")
									.getKeyAsString("-value");
							resp.put(AuthenticationModel.GGB_TOKEN_KEY_NAME, new JSONString(token_value));
							
							JSONObject userInfo = new JSONObject();
							userInfo.put(AuthenticationModel.GGB_LOGIN_DATA_USERNAME_KEY_NAME, new JSONString(userName));
							
							resp.put(AuthenticationModel.GGB_LOGIN_DATA_KEY_NAME, userInfo);
						}
						
						
						((AppW) app).getLoginOperation().getEvent().trigger(resp);
					}
					
					public void onError(Request request, Throwable exception) {
						GWT.log(exception.getLocalizedMessage());
					}
				});
			}
		});
		
		
		
		Label otherWebSites = new Label(app.getMenu("UsingOtherWebSites"));
		logins.getFlexCellFormatter().setColSpan(0, 2, 2);
		logins.setWidget(0, 2, otherWebSites);
		
		googleLogin = new Button("<span class=\"loginbutton\"><img src=\"" + AppResources.INSTANCE.social_google().getSafeUri().asString() + "\"/>" + app.getMenu("LoginToGoogle") + "</span>");
		googleLogin.setTabIndex(4);
		logins.setWidget(1, 2, googleLogin);
		
		facebookLogin = new Button("<span class=\"loginbutton\"><img src=\"" + AppResources.INSTANCE.social_facebook().getSafeUri().asString() + "\"/>" + app.getMenu("LoginToFaceBook") + "</span>");
		facebookLogin.setTabIndex(5);
		logins.setWidget(1, 3, facebookLogin);
		
		twitterLogin = new Button("<span class=\"loginbutton\"><img src=\"" + AppResources.INSTANCE.social_twitter().getSafeUri().asString() + "\"/>" + app.getMenu("LoginToTwitter") + "</span>");
		twitterLogin.setTabIndex(6);
		logins.setWidget(2, 2, twitterLogin);
		
		openidLogin = new Button("<span class=\"loginbutton\"><img src=\"" + AppResources.INSTANCE.social_openid().getSafeUri().asString() + "\"/>" + app.getMenu("LoginToOpenId") + "</span>");
		openidLogin.setTabIndex(7);
		logins.setWidget(2, 3, openidLogin);		
		
		ggtLoginPanel.add(logins);
		container.add(ggtLoginPanel);
		
		((AppW) app).getLoginOperation().getView().add(this);		
		
		add(container);
	}
	
	@Override
    public void show() {
		super.show();
		clearLoginFields();
		submitButton.setEnabled(!isloginFieldsAreEmpty());
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
	        public void execute () {
	            forumUserName.setFocus(true);
	        }
	    });
	}

	private boolean isloginFieldsAreEmpty() {
		return (forumUserName.getText().length() == 0 || forumPassword.getText().length() == 0);
    }

	private void clearLoginFields() {
		forumUserName.setText("");
	    forumPassword.setText("");
	    errormsg.setText("");
    }

	public void success(JSONObject response) {
	    this.hide();
    }

	public void fail(JSONObject resonse) {
	   errormsg.setText(app.getMenu("LoginFailed"));
    }

}
