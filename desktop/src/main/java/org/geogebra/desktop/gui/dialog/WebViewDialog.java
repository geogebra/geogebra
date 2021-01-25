package org.geogebra.desktop.gui.dialog;

import java.awt.Dimension;
import java.awt.Point;
import java.net.URI;
import java.net.URISyntaxException;

import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Provides basic functionality for creating a JavaFX WebView within a dialog.
 * The method <code>createWebView(String startURL)</code> has to be called to
 * create and initialize the webView
 * 
 * @author stefan
 *
 */
public abstract class WebViewDialog extends Dialog {
	private static final long serialVersionUID = 1L;
	private static final String EVENT_TYPE_CLICK = "click";

	protected JFXPanel fxPanel;

	/** Reference to the application */
	protected AppD app;

	/**
	 * The webview in this dialog. is created by the method
	 * <code>createWebView</code>.
	 */
	protected WebView webView;

	/**
	 * Sets the main window of the GeoGebra App as owner of the dialog
	 * 
	 * @param app
	 *            The App
	 * @param modal
	 *            Specifies whether dialog blocks user input to other top-level
	 *            windows when shown. If <code>true</code>, the modality type
	 *            property is set to <code>DEFAULT_MODALITY_TYPE</code>,
	 *            otherwise the dialog is modeless.
	 */
	public WebViewDialog(AppD app, boolean modal) {
		super(app.getFrame(), modal);
		this.app = app;
	}

	/**
	 * Creates and initializes the webview. This method has to be called from
	 * implementing dialogs to create the WebView.
	 * 
	 * @param startURL
	 *            The initial URL that will be loaded in the webview
	 * @return The JFXPanel that is the parent of the WebView.
	 */
	protected JFXPanel createWebView(final String startURL) {

		// Create the JavaFX Panel for the WebView
		fxPanel = new JFXPanel();
		fxPanel.setLocation(new Point(0, 0));

		// Initialize the webView in a JavaFX-Thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initWebView(fxPanel, startURL);
			}
		});

		app.setComponentOrientation(this);
		return fxPanel;
	}

	/**
	 * Creates a web view and opens the login page of GeoGebraTube
	 * 
	 * @param fxPanel
	 *            The panel that should hold the web view
	 * @param startURL
	 *            The Start URL that is loaded in the WebView
	 */
	void initWebView(final JFXPanel fxPanel, String startURL) {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		fxPanel.setScene(scene);

		webView = new WebView();

		// Listen for successful page load to query the login result
		webView.getEngine().getLoadWorker().stateProperty()
				.addListener(new ChangeListener<State>() {
					@Override
					public void changed(ObservableValue<? extends State> ov,
							State oldState, State newState) {
						if (newState == State.SUCCEEDED) {
							Document doc = getWebEngine().getDocument();
							if (doc != null) {
								Log.debug("Load page finished: "
										+ doc.getBaseURI());
							}
							onPageLoaded();
						}
					}
				});

		// Load the login page
		webView.getEngine().load(startURL);

		root.setCenter(webView);
	}

	/**
	 * Returns the javaFX webEngine instance that is associated with this web
	 * view.
	 * 
	 * @return The instance of WebEngine or null, if the web view was not
	 *         created yet.
	 */
	public WebEngine getWebEngine() {
		if (webView == null) {
			return null;
		}
		return webView.getEngine();
	}

	/**
	 * This method is called after a page was loaded in the WebView and can be
	 * overwritten to handle this event.
	 */
	protected void onPageLoaded() {
		// No default action
	}

	/**
	 * Can be overwritten to handle hyperlink clicks. This handler is only
	 * called, when <code>addHyperlinkListener()</code> was called.
	 * 
	 * @param href
	 *            The URL of the clicked hyperlink
	 * @param absoluteURL
	 *            If href is a relative URL this is the absolute URL. Otherwise
	 *            this is the same as href.
	 * @param domainName
	 *            The domainname of the clicked link.
	 * @param ev
	 *            The web event that was triggered. Can be used to gather more
	 *            detailed information about the clicked element or to prevent
	 *            the default action by calling <code>ev.preventDefault()</code>
	 *            .
	 */
	void onHyperlinkClicked(String href, String absoluteURL, String domainName,
			Event ev) {
		// No Default action
	}

	/**
	 * Resizes the dialog to fit the size of the document in the webview
	 */
	protected void setDialogSizeToPageSize() {
		String widthScript = "Math.max( document.documentElement.clientWidth, document.documentElement.scrollWidth, document.documentElement.offsetWidth );";
		final int width = ((Integer) getWebEngine().executeScript(widthScript))
				.intValue();
		String heightScript = "Math.max( document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight );";
		final int height = ((Integer) getWebEngine()
				.executeScript(heightScript)).intValue();

		setPreferredSize(new Dimension(
				width + (getWidth() - getContentPane().getWidth()),
				height + (getHeight() - getContentPane().getHeight())));
		pack();
		setLocationRelativeTo(app.getFrame());
	}

	/**
	 * Adds a listener for clicks on hyperlinks, if not added yet. This method
	 * has to be called from <code>onPageLoaded</code>.
	 * 
	 * Calls the method <code>onLinkClicked(String href)</code> when a link was
	 * clicked
	 */
	protected void addHyperlinkListener() {

		// Some links need to be opened in an external browser. Add a listener
		// to handle this clicks.
		EventListener listener = new EventListener() {
			@Override
			public void handleEvent(Event ev) {
				String domEventType = ev.getType();
				Log.debug("EventType: " + domEventType);
				if (domEventType.equals(EVENT_TYPE_CLICK)) {
					String href = ((Element) ev.getTarget())
							.getAttribute("href");
					if (href != null) {
						String absoluteURL = href;
						String domainName;
						// Make relative urls absolute
						if (!href.startsWith("http://")
								&& !href.startsWith("https://")) {
							domainName = (String) getWebEngine()
									.executeScript("window.location.origin");
							absoluteURL = domainName + href;
						} else {
							domainName = getDomainNameFromURL(absoluteURL);
						}
						WebViewDialog.this.onHyperlinkClicked(href, absoluteURL,
								domainName, ev);
					}
				}

			}
		};

		Document doc = getWebEngine().getDocument();
		if (doc != null) {
			NodeList nodeList = doc.getElementsByTagName("a");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node instanceof EventTarget) {
					((EventTarget) node).addEventListener(EVENT_TYPE_CLICK,
							listener, false);
				}
			}
			nodeList = doc.getElementsByTagName("input");
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getAttributes()
						.getNamedItem("href") != null) {
					((EventTarget) nodeList.item(i)).addEventListener(
							EVENT_TYPE_CLICK, listener, false);
				}
			}
		}
	}

	/**
	 * Extracts the domain name form an URL
	 * 
	 * @param url
	 *            The URL
	 * @return The domainname
	 */
	public static String getDomainNameFromURL(String url) {
		String domain;
		try {
			URI uri = new URI(url);
			domain = uri.getHost();
		} catch (URISyntaxException e) {
			domain = null;
		}

		return domain;
	}
}