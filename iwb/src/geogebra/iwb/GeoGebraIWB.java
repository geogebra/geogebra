package geogebra.iwb;

import geogebra.CommandLineArguments;
import geogebra.GeoGebra;

import geogebra.common.main.App;
import geogebra.common.main.GlobalKeyDispatcher;
import geogebra.common.util.GeoGebraLogger;
import geogebra.euclidian.EuclidianViewD;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.main.AppD;

import java.awt.Container;

import javax.swing.SwingConstants;

import com.smarttech.board.sbsdk.SBSDK;
import com.smarttech.board.sbsdk.SBSDKBase;

public class GeoGebraIWB extends GeoGebra {

	protected GeoGebraIWB() {
	}

	public static void main(String[] cmdArgs) {
		(new GeoGebraIWB()).doMain(cmdArgs);
	}

	protected void startGeoGebra(CommandLineArguments args) {
		// create and open first GeoGebra window
		GeoGebraFrame ggf = new GeoGebraFrame();
		geogebra.gui.app.GeoGebraFrame.init(args, ggf);
		AppD app = ggf.getApplication();
		setUpSMARTBoardConnection(app);
	}

	protected void setUpSMARTBoardConnection(final AppD app) {
		boolean dllFound = false;
//		App.debug(System.getProperty("java.library.path"));
//		App.debug(System.getProperty("os.arch"));
		/** is the bitness of the jvm 64 */
		boolean jvm64=System.getProperty("os.arch").endsWith("64");
		
		//we try to load both files just to be sure, only the errormessage depends
		//on the bitness.
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
				app.showError(app.getPlain("SMARTBoardDLLErrorA",
						(jvm64?"RegistrationUtilsx64.dll":"RegistrationUtils.dll")));
			}
		}
		if (dllFound) {
			try {
				final SBSDK board = new SBSDK();
				App.info(board.getSoftwareVersion().toString());
				/*
				 * The following will write an exception/stacktrace to stderr if
				 * a board has never been connected, but driver are installed.
				 */
				boolean connected = board.isABoardConnected();

				if (connected) {

					GlobalKeyDispatcher.changeFontsAndGeoElements(app, 20,
							false); // bigger font and points
					EuclidianViewD ev = app.getEuclidianView1();
					ev.setCapturingThreshold(10); // easier to select objects
					app.setToolbarPosition(SwingConstants.SOUTH, true);
					App.info("board is connected");

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
					App.info("board is not connected");
				}
			} catch (RuntimeException e) {
				// will happen if no SMARTboard-driver is installed
				App.info("No SMARTboard-driver installed.");
				App.debug(e.getMessage());
				e.printStackTrace();
				app.showError(app.getPlain("SMARTBoardConnectionError"));
			}
		}
	}

}
