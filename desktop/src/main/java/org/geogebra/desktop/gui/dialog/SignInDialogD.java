/**
 * 
 */
package org.geogebra.desktop.gui.dialog;

import java.awt.Dimension;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;
import org.w3c.dom.events.Event;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebEngine;

/**
 * A dialog for login in GeoGebraTube This dialog will show a web view that
 * opens the login dialog of GeoGebraTube
 * 
 * @author stefan
 *
 */
public class SignInDialogD extends WebViewDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 *            The app of type AppD
	 */
	public SignInDialogD(AppD app) {
		super(app, true);
		createGUI();
	}

	private void createGUI() {
		setTitle(app.getLocalization().getMenu("SignInToGGT"));
		setResizable(true);
		getContentPane().setPreferredSize(new Dimension(500, 270));

		JFXPanel fxPanel1 = createWebView(app.getLoginOperation()
				.getLoginURL(app.getLocale().getLanguage()));
		add(fxPanel1);

		pack();
		setLocationRelativeTo(app.getFrame());
	}

	private boolean firstload = true;

	/**
	 * Is called when a page is loaded in the pageview. When the page containing
	 * the login result is loaded, the login token is read from the web page
	 * (using JavaScript) and the API operation is called to authorized the
	 * token. The Dialog is closed afterwards.
	 */
	@Override
	protected void onPageLoaded() {
		final WebEngine webEngine = getWebEngine();

		// Check if the login result page was loaded
		String title = webEngine.getTitle();
		if (title != null && title.startsWith("Login-successful")) {
			handleLoginResult(webEngine);
			return;
		}

		// Some links need to be opened in an external browser. Add a listener
		// to handle this clicks.
		addHyperlinkListener();

		// Set the size of the dialog to the size of the web page
		if (!firstload) {
			setDialogSizeToPageSize();
		} else {
			firstload = false;
		}
	}

	@Override
	void onHyperlinkClicked(String href, String absuluteURL, String domainName,
			Event ev) {
		Log.debug("Link clicked: " + href);

		// Check if the clicked link should be opened in an external browser
		if ((domainName.contains("geogebra.org")
				&& (href.contains("mode=register")
						|| href.contains("mode=sendpassword")))
				|| domainName.contains("google.com")
				|| domainName.contains("facebook.com")) {

			String url = absuluteURL;
			ev.preventDefault();

			if (domainName.contains("geogebra.org")) {
				url += "&lang=" + app.getLocale().getLanguage();
			}

			Log.debug("Redirecting to URL: " + url);
			app.showURLinBrowser(absuluteURL);
		}
	}

	private void handleLoginResult(final WebEngine webEngine) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				// Call the javascript method that returns the token.
				Object result = webEngine
						.executeScript("getLoginResult('login_token')");
				if (result instanceof String) {
					String token = (String) result;

					// Call the operation to approve the token
					app.getLoginOperation().performTokenLogin(token, false);

					// Close the dialog
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							SignInDialogD.this.dispatchEvent(
									new WindowEvent(SignInDialogD.this,
											WindowEvent.WINDOW_CLOSING));
						}
					});
				}
			}
		});
	}
}
