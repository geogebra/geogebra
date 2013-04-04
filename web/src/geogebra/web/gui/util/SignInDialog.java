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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

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
		VerticalPanel container = new VerticalPanel();
		
		HorizontalPanel ggtLoginPanel = new HorizontalPanel();
		ggtLoginPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		Label ggtLogin = new Label(app.getMenu("LogIntoGeoGebraTube"));
		ggtLoginPanel.add(ggtLogin);
		container.add(ggtLoginPanel);
		
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
		
		Anchor forgotPassword = new Anchor(app.getMenu("forgottenPassword"));
		forgotPassword.setHref(GeoGebraConstants.GGT_FORGOT_PWD_URL);
		forgotPassword.setTarget("_blank");
		logins.getFlexCellFormatter().setColSpan(3, 0, 2);
		logins.setWidget(3, 0, forgotPassword);
		
		submitButton = new Button(app.getMenu("submit"));
		logins.setWidget(4, 0, submitButton);
		
		Anchor register = new Anchor(app.getMenu("register"));
		register.setHref(GeoGebraConstants.GGT_REGISTER_URL);
		register.setTarget("_blank");
		logins.setWidget(4, 1, register);
		
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
		
		container.add(logins);
		
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
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
