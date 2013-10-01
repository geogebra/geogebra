/**
 * 
 */
package geogebra.gui.dialog;

import geogebra.main.AppD;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowEvent;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 * A dialog for login in GeoGebraTube
 * This dialog will show a web view that opens the login dialog of GeoGebraTube
 * 
 * @author stefan
 *
 */
public class SignInDialogD extends JDialog {
	private static final long serialVersionUID = 1L;
	
	/** Reference to the application */
	AppD app;
	private WebView webView;

	/**
	 * @param app The app of type AppD
	 */
	public SignInDialogD(AppD app) {
		super(app.getFrame(), true);
		this.app = app;
		
		createGUI();
	}
	
	private void createGUI() {
		setTitle(app.getPlain("SignInToGGT"));
		setResizable(true);
        getContentPane().setPreferredSize(new Dimension(900, 500));

		//Create the JavaFX Panel for the WebView
        final JFXPanel fxPanel = new JFXPanel();
        add(fxPanel);
        fxPanel.setLocation(new Point(0, 0));

        // Initialize the webView in a JavaFX-Thread
        Platform.runLater(new Runnable() {
            public void run() {
                initWebView(fxPanel);
            }
        });
		
		app.setComponentOrientation(this);
		
		pack();	
		setLocationRelativeTo(app.getFrame());	
	}
	
    /**
     * Creates a web view and opens the login page of GeoGebraTube
     * @param fxPanel The panel that should hold the web view
     */
    void initWebView(final JFXPanel fxPanel) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        fxPanel.setScene(scene);
        
        webView = new WebView();
        
        // Listen for successful page load to query the login result 
        webView.getEngine().getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>() {
                public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                    if (newState == State.SUCCEEDED) {
                    	onPageLoaded();
                    }
                }
            });
        
        // Load the login page
        webView.getEngine().load(app.getLoginOperation().getLoginURL(((AppD) app).getLocale()));
        
        root.setCenter(webView);
    }

    /**
     * Is called when a page is loaded in the pageview.
     * When the page containing the login result is loaded, the login token is read from the web page (using JavaScript)
     * and the API operation is called to authorized the token.
     * The Dialog is closed afterwards.
     */
    void onPageLoaded() {
        final WebEngine webEngine = webView.getEngine();
        
    	// Check if the login result page was loaded
		String title = webEngine.getTitle();
		if(title == null || ! title.startsWith("Login-successful")) {
			return;
		}
		
		Platform.runLater( new Runnable(){
    		public void run() {
    	    	
    	    	// Call the javascript method that returns the token.
    			Object result = webEngine.executeScript("getLoginResult('login_token')");
    			if (result instanceof String) {
    				String token = (String) result;
    				
    				// Call the operation to approve the token
    				app.getLoginOperation().performTokenLogin(token);
    				
    				// Close the dialog
    			    SwingUtilities.invokeLater(new Runnable() {
    				    public void run() {
    				    	SignInDialogD.this.dispatchEvent(new WindowEvent( 
    				    			SignInDialogD.this, WindowEvent.WINDOW_CLOSING)); 
    				    }
    			    });
    			}
    		}
    	});
    }
}
