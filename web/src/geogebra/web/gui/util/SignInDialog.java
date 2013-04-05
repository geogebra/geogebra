package geogebra.web.gui.util;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public class SignInDialog extends DialogBox {
	
	private App app;
	private Button cancel;
	private TextBox forumUserName;
	private PasswordTextBox forumPassword;
	private Button submitButton;
	private Button googleLogin;
	private Button facebookLogin;
	private Button twitterLogin;
	private	Button openidLogin;

	/**
	 * creates a SignInDialog for log in to different
	 * accounts
	 */
	public SignInDialog(App app) {
		super();
		this.app = app;
		FlowPanel container = new FlowPanel();
		container.addStyleName("signInDialog");
		
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
		
		forumUserName = new TextBox();
		forumUserName.getElement().setAttribute("placeholder", app.getMenu("username"));
		logins.getFlexCellFormatter().setColSpan(1, 0, 2);
		logins.setWidget(1, 0, forumUserName);
		
		forumPassword = new PasswordTextBox();
		forumPassword.getElement().setAttribute("placeholder", app.getMenu("password"));
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
		
		submitButton = new Button(app.getMenu("submit"));
		logins.getFlexCellFormatter().setColSpan(4, 0, 2);
		logins.setWidget(4, 0, submitButton);
		
		
		
		Label otherWebSites = new Label(app.getMenu("UsingOtherWebSites"));
		logins.getFlexCellFormatter().setColSpan(0, 2, 2);
		logins.setWidget(0, 2, otherWebSites);
		
		googleLogin = new Button("<span class=\"loginbutton\"><img src=\"" + AppResources.INSTANCE.social_google().getSafeUri().asString() + "\"/>" + app.getMenu("LoginToGoogle") + "</span>");
		logins.setWidget(1, 2, googleLogin);
		
		facebookLogin = new Button("<span class=\"loginbutton\"><img src=\"" + AppResources.INSTANCE.social_facebook().getSafeUri().asString() + "\"/>" + app.getMenu("LoginToFaceBook") + "</span>");
		logins.setWidget(1, 3, facebookLogin);
		
		twitterLogin = new Button("<span class=\"loginbutton\"><img src=\"" + AppResources.INSTANCE.social_twitter().getSafeUri().asString() + "\"/>" + app.getMenu("LoginToTwitter") + "</span>");
		logins.setWidget(2, 2, twitterLogin);
		
		openidLogin = new Button("<span class=\"loginbutton\"><img src=\"" + AppResources.INSTANCE.social_openid().getSafeUri().asString() + "\"/>" + app.getMenu("LoginToOpenId") + "</span>");
		logins.setWidget(2, 3, openidLogin);		
		
		ggtLoginPanel.add(logins);
		container.add(ggtLoginPanel);
		
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("buttonPanel");
		final SignInDialog t = this;
		buttonPanel.add(cancel = new Button(app.getMenu("cancel"), new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				t.hide();
			}
		}));
		container.add(buttonPanel);
		
		
		
		add(container);
	}

}
