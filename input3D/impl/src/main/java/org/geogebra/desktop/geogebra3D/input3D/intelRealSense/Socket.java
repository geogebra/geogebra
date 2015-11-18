package org.geogebra.desktop.geogebra3D.input3D.intelRealSense;

import intel.rssdk.PXCMCaptureManager;
import intel.rssdk.PXCMHandConfiguration;
import intel.rssdk.PXCMHandConfiguration.AlertHandler;
import intel.rssdk.PXCMHandData;
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
import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.euclidian3D.Input3D.OutOfField;
import org.geogebra.common.main.App;
import org.geogebra.common.util.DownloadManager;
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

	private static final String VERSION = "1.4.27.41944";

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
		
		// App.debug("alert hand #" + id + " : " + type);
		
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
	 * @throws Input3DException
	 *             if no camera installed
	 */
	public static void createSession() throws Input3DException {

		if (SESSION != null) {
			return;
		}

		// reset sense manager
		SENSE_MANAGER = null;

		// query registry to get installed version
		queryRegistry();

		try {
			// Create session
			SESSION = PXCMSession.CreateInstance();
		} catch (Throwable e) {
			throw new Input3DException(
					Input3DExceptionType.INSTALL,
					"RealSense: Failed to start session instance creation, maybe unsupported platform?");
		}
		if (SESSION == null) {
			throw new Input3DException(Input3DExceptionType.INSTALL,
					"RealSense: Failed to create a session instance");
		}

	}
	

	/**
	 * Query windows registry to check version
	 * 
	 * @throws Input3DException
	 *             if no key in registry
	 */
	static public void queryRegistry() throws Input3DException {
		int registeryQueryResult = 1; // inited to bad value (correct value = 0)
		try {
			Runtime runtime = Runtime.getRuntime();
			Process p = runtime.exec(QUERY_REGISTERY_KEY_FRONT_CAM);
			p.waitFor();
			registeryQueryResult = p.exitValue();
			App.debug(QUERY_REGISTERY_KEY_FRONT_CAM + " : "
					+ registeryQueryResult);
			// get query result -- so we can check version
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String line = "";
				try {
					// get version
					while ((line = reader.readLine()) != null) {
						// App.debug(line);
						String[] items = line.split(" ");
						int index = 0;
						while (index < items.length
								&& items[index].length() == 0) {
							index++;
						}
						if (index < items.length) {
							// App.debug(">>>>> " + index + " : " +
							// items[index]);
							if (items[index].equals("Version")) {
								String version = items[items.length - 1];
								App.debug(">>>>> " + version + " , "
										+ version.equals(VERSION));
								if (!version.equals(VERSION)) {
									updateVersion();
								}

							}
						}
					}
				} finally {
					reader.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} catch (Throwable e) {
			throw new Input3DException(Input3DExceptionType.INSTALL,
					"RealSense: No key for camera in registery");
		}

	}
	
	private final static String REALSENSE_ONLINE_ARCHIVE_BASE = "http://dev.geogebra.org/realsense/latest/";
	private final static String REALSENSE_DCM_EXE = "intel_rs_dcm_f200_1.4.27.41944.exe";
	private final static String REALSENSE_ONLINE_ARCHIVE_DCM = REALSENSE_ONLINE_ARCHIVE_BASE
			+ REALSENSE_DCM_EXE;
	private final static String REALSENSE_CAMERA_EXE = "intel_rs_sdk_runtime_6.0.21.6598.exe";
	private final static String REALSENSE_ONLINE_ARCHIVE_CAMERA = REALSENSE_ONLINE_ARCHIVE_BASE
			+ REALSENSE_CAMERA_EXE;

	private static void updateVersion() {
		
		Thread t = new Thread(){
			@Override
			public void run() {
				App.debug("\n>>>>>>>>>>>>>> update version");

				showMessage(
						"RealSense not up to date, we'll download and install new version.",
						"This may take several minutes, you will be notified when achieved.");

				String filenameDCM = null;
				String filenameCAM = null;

				try {
					String updateDir = System.getenv("APPDATA")
							+ GeoGebraConstants.GEOGEBRA_THIRD_PARTY_UPDATE_DIR;
					App.debug("Creating " + updateDir);
					new File(updateDir).mkdirs();

					// Downloading dcm installer
					filenameDCM = updateDir + File.separator + REALSENSE_DCM_EXE;
					File dest = new File(filenameDCM);
					URL url = new URL(REALSENSE_ONLINE_ARCHIVE_DCM);
					App.debug("Downloading " + REALSENSE_ONLINE_ARCHIVE_DCM);
					DownloadManager.copyURLToFile(url, dest);
					App.debug("=== done");

					// Downloading camera installer
					filenameCAM = updateDir + File.separator + REALSENSE_CAMERA_EXE;
					dest = new File(filenameCAM);
					url = new URL(REALSENSE_ONLINE_ARCHIVE_CAMERA);
					App.debug("Downloading " + REALSENSE_ONLINE_ARCHIVE_CAMERA);
					DownloadManager.copyURLToFile(url, dest);
					App.debug("=== done");

				} catch (Exception e) {
					App.error("Unsuccessful update");
				}

				boolean installOK = false;

				if (filenameDCM != null) {
					installOK = install(filenameDCM);
				}

				if (installOK && filenameCAM != null) {
					installOK = install(filenameCAM);
				}
				
				if (installOK) {
					App.debug("Successful update");
					showMessage("RealSense is now up to date.",
							"Please restart GeoGebra to use Intel RealSense camera.");
				}
			}
		};
		
		t.start();

	}

	static boolean install(String filename) {
		App.debug("installing " + filename);
		Runtime runtime = Runtime.getRuntime();
		Process p;
		try {
			p = runtime.exec(filename
					+ " --silent --no-progress --acceptlicense=yes");
			p.waitFor();
			return p.exitValue() == 0; // all is good
		} catch (IOException e) {
			App.debug("Unsuccesfull install of " + filename + " : "
					+ e.getMessage());
		} catch (InterruptedException e) {
			App.debug("Unsuccesfull wait for install of " + filename + " : "
					+ e.getMessage());
		}

		return false;

	}

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
	 * @throws Input3DException
	 * 
	 * 
	 * @throws Exception
	 *             when fails
	 */
	public Socket() throws Input3DException {

		if (SESSION == null) {
			createSession();
		}

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

		sts = SENSE_MANAGER.EnableHand(null);
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)<0) {
			throw new Input3DException(Input3DExceptionType.RUN,
					"RealSense: Failed to enable HandAnalysis");
		}

		dataSampler = new DataAverage(SAMPLES);

		sts = SENSE_MANAGER.Init();
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)>=0) {
			PXCMHandModule handModule = SENSE_MANAGER.QueryHand(); 
			PXCMHandConfiguration handConfig = handModule.CreateActiveConfiguration(); 


			handConfig.EnableAllAlerts();

			AlertHandler alertHandler = new AlertHandler() {
				
				@Override
				public void OnFiredAlert(intel.rssdk.PXCMHandData.AlertData data) {
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





	private boolean connected = false;




	/**
	 * get data from camera
	 * 
	 * @return true if data have been produced
	 */
	public boolean getData(){

		if (!connected)
			return false;

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
	public void exit() {
		CAPTURE_MANAGER.CloseStreams();
		SENSE_MANAGER.Close();
		SENSE_MANAGER = null;
		SESSION.close();
		SESSION = null;
	}



}

