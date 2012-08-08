package geogebra.iwb;

import geogebra.CommandLineArguments;
import geogebra.GeoGebra;
import geogebra.SplashWindow;
import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.common.main.GlobalKeyDispatcher;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.main.AppD;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.SwingConstants;

import com.smarttech.board.sbsdk.SBSDK;
import com.smarttech.board.sbsdk.SBSDKBase;

public class GeoGebraIWB {

	public static Frame splashFrame = null;

	public static void main(String[] cmdArgs) {
		CommandLineArguments args = new CommandLineArguments(cmdArgs);

		boolean showSplash = true;
		if (!args.getBooleanValue("showSplash", true)) {
			showSplash = false;
		}

		if (args.containsArg("help") || args.containsArg("proverhelp")
				|| args.containsArg("v") || args.containsArg("regressionFile")) {
			showSplash = false;
		}

		if (showSplash) {
			// Show splash screen
			URL imageURL = GeoGebra.class.getResource("/geogebra/"
					+ GeoGebraConstants.SPLASH_STRING);
			if (imageURL != null) {
				splashFrame = SplashWindow.splash(Toolkit.getDefaultToolkit()
						.createImage(imageURL));
			} else {
				System.err.println("Splash image not found");
			}
		}

		// Start GeoGebra
		try {
			startGeoGebra(args);
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.flush();
			System.exit(10);
		}

		// Hide splash screen
		if (splashFrame != null)
			splashFrame.setVisible(false);
	}

	private static void startGeoGebra(CommandLineArguments args) {
		// create and open first GeoGebra window
		GeoGebraFrame ggf = new GeoGebraFrame();
		geogebra.gui.app.GeoGebraFrame.init(args, ggf);
		AppD app = ggf.getApplication();
//		app.setMode(EuclidianConstants.MODE_DELETE);
		setUpSMARTBoardConnection(app);
	}
	
	protected static void setUpSMARTBoardConnection(final AppD app){
		boolean dllFound = false;
		try {
			System.loadLibrary("RegistrationUtils");
			dllFound = true;
		} catch (UnsatisfiedLinkError e) {
			dllFound = false;
		}
		if (!dllFound) {
			try {
				System.loadLibrary("RegistrationUtilsx64");
				dllFound = true;
			} catch (UnsatisfiedLinkError e) {
				dllFound = false;
			}
			if (!dllFound) {
				app.showError("RegistrationUtilsNotFound"); // TODO add to
														// properties
			}
		}
		if (dllFound) {
			try {
				final SBSDK board = new SBSDK();
				App.debug(board.getSoftwareVersion());
				/*
				 * The following will write an exception/stacktrace to
				 * stderr if a board has never been connected, but driver
				 * are installed.
				 */
				boolean connected = board.isABoardConnected();

				if (connected) {

					GlobalKeyDispatcher.changeFontsAndGeoElements(app, 20,	false); // bigger font and points
					app.getEuclidianView1().setCapturingThreshold(10); // easier to select objects
					app.setToolbarPosition(SwingConstants.SOUTH, true);
					App.debug("board is connected");

					final SMARTEventListener listener = new SMARTEventListener(
							app, board);
					board.attach(app.getFrame(), true);
					board.sendGestures(
							SBSDKBase.SBSDK_GESTURE_EVENT_FLAG.SB_GEF_SEND_ALL_THROUGH_SDK,
							-1);
					board.sendMouseEvents(
							SBSDKBase.SBSDK_MOUSE_EVENT_FLAG.SB_MEF_NEVER, -1);
					board.sendXMLToolChanges(true);
					board.startToolCacheSending();
					Container evjp = app.getEuclidianView1().getJPanel();
					board.registerComponent(evjp, listener);

				} else {
					App.debug("board is not connected");
				}
			} catch (RuntimeException e) {
				// will happen if no SMARTboard-driver is installed
				App.debug("No SMARTboard-driver installed.");
				App.debug(e.getMessage());
				e.printStackTrace();
				app.showError("CouldNotConnectToSMARTBoard"); // TODO add to
															// properties
			}
		}
	}

}
