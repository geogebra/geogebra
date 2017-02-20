package org.geogebra.desktop.geogebra3D.input3D.intelRealSense;

import intel.rssdk.PXCMCaptureManager;
import intel.rssdk.PXCMHandConfiguration;
import intel.rssdk.PXCMHandConfiguration.AlertHandler;
import intel.rssdk.PXCMHandData;
import intel.rssdk.PXCMHandData.AlertData;
import intel.rssdk.PXCMHandData.AlertType;
import intel.rssdk.PXCMHandData.BodySideType;
import intel.rssdk.PXCMHandModule;
import intel.rssdk.PXCMPoint3DF32;
import intel.rssdk.PXCMPoint4DF32;
import intel.rssdk.PXCMSenseManager;
import intel.rssdk.PXCMSession;
import intel.rssdk.pxcmStatus;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.geogebra3D.input3D.Input3D.OutOfField;
import org.geogebra.common.jre.util.DownloadManager;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DException;
import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DExceptionType;


/**
 * socket of realsense
 * 
 * @author mathieu
 *
 */
public class Socket {

	private static final String QUERY_REGISTERY_KEY_FRONT_CAM = "reg query HKLM\\Software\\Intel\\RSSDK\\Components\\ivcam";

	// version embedded in ggb is 1.4.27.41944
	private static final int VERSION_MAJOR = 1;
	private static final int VERSION_MINOR = 4;

	/**
	 * factor screen / real world
	 */
	static double SCREEN_REAL_DIM_FACTOR = 1 / 0.1;
	/**
	 * right/left side offset
	 */
	static double SIDE_OFFSET = 0.75;
	/**
	 * origin point depth
	 */
	static float DEPTH_ZERO = 0.4f;
	/**
	 * samples used for average
	 */
	static int SAMPLES = 7;
	

	/** hand x position */
	public double handX;
	/** hand y position */
	public double handY;
	/** hand z position */
	public double handZ;

	/**
	 * hand orientation vector (x value)
	 */
	public double handOrientationX;
	/**
	 * hand orientation vector (y value)
	 */
	public double handOrientationY;
	/**
	 * hand orientation vector (z value)
	 */
	public double handOrientationZ;
	/**
	 * hand orientation vector (w value)
	 */
	public double handOrientationW;

	/**
	 * right button state
	 */
	public boolean rightButton = false;
	/**
	 * left button state
	 */
	public boolean leftButton = false;
	
	/** says if it has got a message from realsense */
	public boolean gotMessage = false;      

	static private PXCMSenseManager SENSE_MANAGER;
	static private PXCMCaptureManager CAPTURE_MANAGER;
	private pxcmStatus sts;
	private PXCMHandData handData;
	private PXCMHandData.IHand hand;
	
	private DataSampler dataSampler;
	
	private abstract class DataSampler {
		
		protected int samples;
		protected int index;

		protected BodySideType side;
		
		protected int leftSideCount, rightSideCount;

		protected float[] worldX, worldY, worldZ;
		
		protected float[] orientationX, orientationY, orientationZ, orientationW;
		
		public DataSampler(int samples){
			this.samples = samples;
			index = 0;

			
			worldX = new float[samples];
			worldY = new float[samples];
			worldZ = new float[samples];
			
			
			orientationX = new float[samples];
			orientationY = new float[samples];
			orientationZ = new float[samples];
			orientationW = new float[samples];
			
			resetSide();

		}

		protected void resetSide() {
			leftSideCount = 0;
			rightSideCount = 0;
			side = BodySideType.BODY_SIDE_UNKNOWN;
		}

		protected void addSideDetected(BodySideType type) {
			if (type == BodySideType.BODY_SIDE_RIGHT) {
				rightSideCount++;
				if (rightSideCount > 10000) {
					rightSideCount /= 10;
					leftSideCount /= 10;
				}
				updateSide();
			} else if (type == BodySideType.BODY_SIDE_LEFT) {
				leftSideCount++;
				if (leftSideCount > 10000) {
					rightSideCount /= 10;
					leftSideCount /= 10;
				}
				updateSide();
			}
		}

		private void updateSide() {
			if (side == BodySideType.BODY_SIDE_UNKNOWN) {
				// check if we can decide side
				if (rightSideCount > leftSideCount) {
					side = BodySideType.BODY_SIDE_RIGHT;
				} else if (rightSideCount < leftSideCount) {
					side = BodySideType.BODY_SIDE_LEFT;
				}
			} else {
				// check if we should decide side
				if (rightSideCount > 2 * leftSideCount) {
					side = BodySideType.BODY_SIDE_RIGHT;
				} else if (2 * rightSideCount < leftSideCount) {
					side = BodySideType.BODY_SIDE_LEFT;
				}

			}

		}

		public abstract void addData(BodySideType handSide,
				float wx, float wy, float wz, float ox, float oy, float oz, float ow);

		public abstract double getWorldX();

		public abstract double getWorldY();

		public abstract double getWorldZ();

		public abstract double getHandOrientationX();

		public abstract double getHandOrientationY();

		public abstract double getHandOrientationZ();

		public abstract double getHandOrientationW();

		public BodySideType getSide() {
			return side;
		}

	}
	
	private class DataAverage extends DataSampler{
		
		private float worldXSum, worldYSum, worldZSum;
		
		private float handOrientationXSum, handOrientationYSum, handOrientationZSum, handOrientationWSum;
		
		public DataAverage(int samples){
			
			super(samples);
			
			worldXSum = 0f;
			worldYSum = 0f;
			worldZSum = 0f;
			
			
			handOrientationXSum = 0f;
			handOrientationYSum = 0f;
			handOrientationZSum = 0f;
			handOrientationWSum = 0f;
			

		}
		
		@Override
		public void addData(BodySideType handSide,
				float wx, float wy, float wz,
				float ox, float oy, float oz, float ow){
			
			
			if (resetAllValues){

				resetSide();
				addSideDetected(handSide);

				for (int i = 0 ; i < samples ; i++){
					// reset all values
					worldX[i] = wx;
					worldY[i] = wy;
					worldZ[i] = wz;
					orientationX[i] = ox;
					orientationY[i] = oy;
					orientationZ[i] = oz;
					orientationW[i] = ow;
				}
				
				worldXSum = wx * samples;
				worldYSum = wy * samples;
				worldZSum = wz * samples;
				
				handOrientationXSum = ox * samples;
				handOrientationYSum = oy * samples;
				handOrientationZSum = oz * samples;
				handOrientationWSum = ow * samples;
				
				index = 0;
				resetAllValues = false;
				return;
			}
			
			addSideDetected(handSide);

			
			worldXSum -= worldX[index];
			worldYSum -= worldY[index];
			worldZSum -= worldZ[index];
			
			worldX[index] = wx;
			worldY[index] = wy;
			worldZ[index] = wz;
			
			worldXSum += worldX[index];
			worldYSum += worldY[index];
			worldZSum += worldZ[index];
			
			
			handOrientationXSum -= orientationX[index];
			handOrientationYSum -= orientationY[index];
			handOrientationZSum -= orientationZ[index];
			handOrientationWSum -= orientationW[index];

			orientationX[index] = ox;
			orientationY[index] = oy;
			orientationZ[index] = oz;
			orientationW[index] = ow;

			handOrientationXSum += orientationX[index];
			handOrientationYSum += orientationY[index];
			handOrientationZSum += orientationZ[index];
			handOrientationWSum += orientationW[index];

			index++;
			if (index >= samples){
				index = 0;
			}
		}
		

		@Override
		public double getWorldX(){
			return -worldXSum * SCREEN_REAL_DIM_FACTOR / samples;
		}
		

		@Override
		public double getWorldY(){
			return worldYSum * SCREEN_REAL_DIM_FACTOR / samples;
		}
		

		@Override
		public double getWorldZ(){
			return (worldZSum / samples - DEPTH_ZERO) * SCREEN_REAL_DIM_FACTOR;
		}
		

		@Override
		public double getHandOrientationX(){
			return - handOrientationXSum / samples;
		}
		

		@Override
		public double getHandOrientationY(){
			return - handOrientationYSum / samples;
		}
		

		@Override
		public double getHandOrientationZ(){
			return handOrientationZSum / samples;
		}
		

		@Override
		public double getHandOrientationW(){
			return handOrientationWSum / samples;
		}
		
	
		

		
	}
	
	
	
	private int handId = -1;
	
	private Input3D.OutOfField handOut;

	
	/**
	 * says if we need to reset all values
	 */
	boolean resetAllValues = false;
	
	/**
	 * cam set alert, this updates the hand in/out status
	 * 
	 * @param id
	 *            hand id
	 * @param type
	 *            alert type
	 */
	void setAlert(int id, AlertType type) {
		
		// Log.debug("alert hand #" + id + " : " + type);
		
		if (handOut != OutOfField.NO) { // no hand for now
			if (type == AlertType.ALERT_HAND_INSIDE_BORDERS){
				Log.debug("hand #" + id + " inside borders");
				handId = id;
				handOut = OutOfField.NO;
				resetAllValues = true;
			} else if (handId == id) {
				switch (type) {
				case ALERT_HAND_OUT_OF_BOTTOM_BORDER:
					handOut = OutOfField.BOTTOM;
					break;
				case ALERT_HAND_OUT_OF_TOP_BORDER:
					handOut = OutOfField.TOP;
					break;
				case ALERT_HAND_OUT_OF_LEFT_BORDER:
					handOut = OutOfField.LEFT;
					break;
				case ALERT_HAND_OUT_OF_RIGHT_BORDER:
					handOut = OutOfField.RIGHT;
					break;
				case ALERT_HAND_TOO_CLOSE:
					handOut = OutOfField.NEAR;
					break;
				case ALERT_HAND_TOO_FAR:
					handOut = OutOfField.FAR;
					break;
				case ALERT_HAND_CALIBRATED:
					break;
				case ALERT_HAND_DETECTED:
					break;
				case ALERT_HAND_INSIDE_BORDERS:
					break;
				case ALERT_HAND_LOW_CONFIDENCE:
					break;
				case ALERT_HAND_NOT_CALIBRATED:
					break;
				case ALERT_HAND_NOT_DETECTED:
					break;
				case ALERT_HAND_NOT_TRACKED:
					break;
				case ALERT_HAND_OUT_OF_BORDERS:
					break;
				case ALERT_HAND_TRACKED:
					break;
				default:
					break;
				}
			}
		}else if (handId == id){ // new alert from tracked hand
			if (type == AlertType.ALERT_HAND_OUT_OF_BORDERS){
				Log.debug("hand #" + id + " out of borders");
				handOut = OutOfField.YES;
			}
		}
		
		
	}
	

	/**
	 * Create a session to use realsense camera
	 * 
	 * @param app
	 *            application
	 * 
	 * @throws Input3DException
	 *             if no camera installed or session can't be created
	 */
	public static void createSession(final App app)
			throws Input3DException {

		if (SESSION != null) {
			return;
		}

		// reset sense manager
		SENSE_MANAGER = null;

		// query registry to get installed version
		if (queryRegistry()) {
			try {
				// Create session
				SESSION = PXCMSession.CreateInstance();
				// if session == null, install runtimes
				if (SESSION == null) {
					installRuntimes(app, INSTALL_CORE_AND_HAND);
					throw new Input3DException(
							Input3DExceptionType.INSTALL_RUNTIMES,
							"RealSense: needs to install runtimes ("
									+ INSTALL_CORE_AND_HAND + ")");
				}
			} catch (Input3DException e) {
				throw e;
			} catch (Throwable e) {
				throw new Input3DException(
						Input3DExceptionType.INSTALL,
						"RealSense: Failed to start session instance creation, maybe unsupported platform?");
			}
		}

		if (SESSION == null) {
			throw new Input3DException(Input3DExceptionType.INSTALL,
					"RealSense: Failed to create a session instance");
		}

	}
	

	/**
	 * Query windows registry to check version
	 * 
	 * @return true if up-to-date
	 * 
	 * @throws Input3DException
	 *             if no key in registry
	 */
	static public boolean queryRegistry() throws Input3DException {
		int registryQueryResult = 1; // inited to bad value (correct value = 0)
		boolean upToDate = false;
		String version = null;
		try {
			Runtime runtime = Runtime.getRuntime();
			Process p = runtime.exec(QUERY_REGISTERY_KEY_FRONT_CAM);
			p.waitFor();
			registryQueryResult = p.exitValue();
			Log.debug(QUERY_REGISTERY_KEY_FRONT_CAM + " : "
					+ registryQueryResult);
			// get query result -- so we can check version
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String line = "";
				try {
					// get version
					while ((line = reader.readLine()) != null) {
						// Log.debug(line);
						String[] items = line.split(" ");
						int index = 0;
						while (index < items.length
								&& items[index].length() == 0) {
							index++;
						}
						if (index < items.length) {
							// Log.debug(">>>>> " + index + " : " +
							// items[index]);
							if (items[index].equals("Version")) {
								version = items[items.length - 1];
								if (isUpToDate(version)) {
									upToDate = true;
								} else {
									// updateVersion(app);
									upToDate = false;
								}

							}
						}
					}
				} finally {
					reader.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				throw new Input3DException(Input3DExceptionType.INSTALL,
						"RealSense: No key for camera in registry");
			}
		} catch (Throwable e) {
			throw new Input3DException(Input3DExceptionType.INSTALL,
					"RealSense: No key for camera in registry");
		}

		// nothing went wrong but no version found
		if (version == null || version.length() == 0) {
			throw new Input3DException(Input3DExceptionType.INSTALL,
					"RealSense: No key for camera in registry");
		}

		// version is not up to date
		if (!upToDate) {
			throw new Input3DException(Input3DExceptionType.NOT_UP_TO_DATE,
					version);

		}

		return upToDate;

	}
	
	private static boolean isUpToDate(String version) {
		// Log.debug(">>>>> version installed: " + version);
		String[] versionSplit = version.split("\\.");

		if (versionSplit.length == 0) {
			return false;
		}
		int major = Integer.parseInt(versionSplit[0]);
		// Log.debug(">>>>> major: " + major);
		if (major > VERSION_MAJOR) {
			return true;
		}

		if (versionSplit.length <= 1) {
			return false;
		}
		int minor = Integer.parseInt(versionSplit[1]);
		// Log.debug(">>>>> minor: " + minor);
		if (minor >= VERSION_MINOR) {
			return true;
		}

		return false;
	}

	private final static String REALSENSE_ONLINE_ARCHIVE_BASE = "http://dev.geogebra.org/realsense/latest/";
	private final static String REALSENSE_WEBSETUP = "intel_rs_sdk_runtime_websetup_6.0.21.6598.exe";
	private final static String REALSENSE_ONLINE_WEBSETUP = REALSENSE_ONLINE_ARCHIVE_BASE
			+ REALSENSE_WEBSETUP;

	private final static String INSTALL_CORE_AND_HAND = "core,hand";
	private final static String INSTALL_HAND = "hand";
	/** whethe runtimes are installed */
	static boolean installRuntimes = false;

	private static void installRuntimes(final App app, final String modules) {
		
		if (installRuntimes) {
			return;
		}

		Thread t = new Thread(){
			@Override
			public void run() {
				installRuntimes = true;

				Log.debug("\n>>>>>>>>>>>>>> install runtimes: " + modules);
				Localization loc = app.getLocalization();
				showMessage(loc.getMenu("RealSense.DownloadRuntimes"),
						loc.getMenu("RealSenseNotUpToDate2"));

				String filenameWebSetup = null;

				File destWebSetup = null;

				try {
					String updateDir = System.getenv("APPDATA")
							+ GeoGebraConstants.GEOGEBRA_THIRD_PARTY_UPDATE_DIR;
					Log.debug("Creating " + updateDir);
					new File(updateDir).mkdirs();

					// Downloading web setup
					filenameWebSetup = updateDir + File.separator
							+ REALSENSE_WEBSETUP;
					destWebSetup = new File(filenameWebSetup);
					URL url = new URL(REALSENSE_ONLINE_WEBSETUP);
					Log.debug("Downloading " + REALSENSE_ONLINE_WEBSETUP);
					DownloadManager.copyURLToFile(url, destWebSetup);
					Log.debug("=== done");



				} catch (Exception e) {
					Log.error("Unsuccessful update");
					installRuntimes = false;
				}

				boolean installOK = false;

				if (filenameWebSetup != null) {
					installOK = install(filenameWebSetup, modules);
				}

				
				if (installOK) {
					Log.debug("Successful update");
					showMessage(loc.getMenu("RealSense.UpdatedRuntimes"),
							loc.getMenu("RealSenseUpdated2"));
					if (destWebSetup != null) {
						destWebSetup.delete();
					}
				}

				installRuntimes = false;
			}
		};
		
		t.start();

	}

	/**
	 * @param filename
	 *            executable
	 * @param modules
	 *            modules
	 * @return whether execution ended with 0
	 */
	static boolean install(String filename, String modules) {
		Log.debug("installing " + filename + ", modules: " + modules);
		Runtime runtime = Runtime.getRuntime();
		Process p;
		try {
			p = runtime.exec(filename + " --finstall=" + modules
					+ " --fnone=all --silent --noprogress --acceptlicense=yes");
			p.waitFor();
			return p.exitValue() == 0; // all is good
		} catch (IOException e) {
			Log.debug("Unsuccesfull install of " + filename + " : "
					+ e.getMessage());
		} catch (InterruptedException e) {
			Log.debug("Unsuccesfull wait for install of " + filename + " : "
					+ e.getMessage());
		}

		return false;

	}

	/**
	 * @param message1
	 *            first row
	 * @param message2
	 *            second row
	 */
	static void showMessage(String message1, String message2) {
		final JFrame frame = new JFrame();
		Container c = frame.getContentPane();
		JPanel panel = new JPanel();
		c.add(panel);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		JLabel label = new JLabel(message1);
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPanel.setBackground(Color.WHITE);
		labelPanel.add(label);
		panel.add(labelPanel);

		label = new JLabel(message2);
		labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPanel.setBackground(Color.WHITE);
		labelPanel.add(label);
		panel.add(labelPanel);

		JLabel closeLabel = new JLabel("OK");
		closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		closeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.setVisible(false);
			}
		});
		JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		closePanel.setBackground(Color.WHITE);
		closePanel.add(closeLabel);
		panel.add(closePanel);

		frame.setUndecorated(true);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		try {
			frame.setAlwaysOnTop(true);
		} catch (SecurityException e) {
			// failed to set on top
		}
	}

	static private PXCMSession SESSION = null;


	/**
	 * Create a "Socket" for realsense camera
	 * 
	 * @param app
	 *            app
	 * 
	 * @throws Input3DException
	 *             when fails
	 * 
	 * 
	 */
	public Socket(final App app) throws Input3DException {

		if (SESSION == null) {
			try {
				createSession(app);
			} catch (Input3DException e) {
				throw e;
			} catch (Throwable e) {
				Log.error(e.getMessage());
				throw new Input3DException(Input3DExceptionType.UNKNOWN,
						e.getMessage());
			}
		}

		initSession();

		sts = SENSE_MANAGER.EnableHand(null);
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)<0) {
			// we miss hand module: install it
			installRuntimes(app, INSTALL_HAND);
			throw new Input3DException(Input3DExceptionType.INSTALL_RUNTIMES,
					"RealSense: needs to install runtimes (" + INSTALL_HAND
							+ ")");
		}

		dataSampler = new DataAverage(SAMPLES);

		sts = SENSE_MANAGER.Init();
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)>=0) {
			PXCMHandModule handModule = SENSE_MANAGER.QueryHand(); 
			PXCMHandConfiguration handConfig = handModule.CreateActiveConfiguration(); 


			handConfig.EnableAllAlerts();

			AlertHandler alertHandler = new AlertHandler() {
				
				@Override
				public void OnFiredAlert(AlertData data) {
					setAlert(data.handId, data.label);
				}
			};
			handConfig.SubscribeAlert(alertHandler);
			
			handConfig.ApplyChanges();
			handConfig.Update();
			
			handData = handModule.CreateOutput();
			hand = new PXCMHandData.IHand();
			
			handOut = OutOfField.YES;
			connected = true;
		}

		if (!connected) {
			throw new Input3DException(Input3DExceptionType.RUN,
					"RealSense: not connected ("
							+ (sts == null ? "no state" : sts.name() + ")"));
		}
		
		Log.debug("RealSense: connected");
		
	}





	private static void initSession() throws Input3DException {
		if (SESSION == null) {
			throw new Input3DException(Input3DExceptionType.INSTALL,
					"RealSense: no session created");
		}

		if (SENSE_MANAGER != null) {
			throw new Input3DException(Input3DExceptionType.ALREADY_USED,
					"RealSense: already in use");
		}

		SENSE_MANAGER = SESSION.CreateSenseManager();
		if (SENSE_MANAGER == null) {
			throw new Input3DException(Input3DExceptionType.RUN,
					"RealSense: Failed to create a SenseManager instance");
		}

		CAPTURE_MANAGER = SENSE_MANAGER.QueryCaptureManager();
		CAPTURE_MANAGER.FilterByDeviceInfo("RealSense", null, 0);

	}

	private boolean connected = false;




	/**
	 * get data from camera
	 * 
	 * @return true if data have been produced
	 */
	public boolean getData(){

		if (!connected) {
			return false;
		}

		sts = SENSE_MANAGER.AcquireFrame(true);
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)<0){
			gotMessage = false;
			SENSE_MANAGER.ReleaseFrame();
			return false;
		}


		// Query and Display Joint of Hand or Palm
		handData.Update(); 

		sts = handData.QueryHandData(PXCMHandData.AccessOrderType.ACCESS_ORDER_NEAR_TO_FAR, 0, hand);

		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR) >= 0) {
			PXCMPoint3DF32 world = hand.QueryMassCenterWorld();
			PXCMPoint4DF32 palmOrientation = hand.QueryPalmOrientation();
			BodySideType handSide = hand.QueryBodySide();

			
			dataSampler.addData(handSide,
					world.x, world.y, world.z,
					palmOrientation.x, palmOrientation.y, palmOrientation.z, palmOrientation.w);
			
			
			handX = dataSampler.getWorldX();
			handY = dataSampler.getWorldY();
			handZ = dataSampler.getWorldZ();
			

			switch (dataSampler.getSide()) {
			case BODY_SIDE_RIGHT:
				handX -= SIDE_OFFSET;
				break;
			case BODY_SIDE_LEFT:
				handX += SIDE_OFFSET;
				break;
			case BODY_SIDE_UNKNOWN:
			default:
				handX -= SIDE_OFFSET;
				break;
			}
			
			handOrientationX = dataSampler.getHandOrientationX();
			handOrientationY = dataSampler.getHandOrientationY();
			handOrientationZ = dataSampler.getHandOrientationZ();
			handOrientationW = dataSampler.getHandOrientationW();
			

			gotMessage = true;
			
		}else{
			gotMessage = false;
		}

		SENSE_MANAGER.ReleaseFrame();

		return true;
	}



	/**
	 * 
	 * @return true if a hand is tracked
	 */
	public boolean hasTrackedHand() {
		return handOut == OutOfField.NO;
	}

	/**
	 * 
	 * @return out of field type
	 */
	public OutOfField getOutOfField() {
		return handOut;
	}



	/**
	 * set left button status
	 * 
	 * @param flag
	 *            status
	 */
	public void setLeftButtonPressed(boolean flag) {
		leftButton = flag;	
	}

	/**
	 * exit and close manager
	 */
	public static void exit() {
		CAPTURE_MANAGER.CloseStreams();
		SENSE_MANAGER.Close();
		SENSE_MANAGER = null;
		SESSION.close();
		SESSION = null;
	}



}

