package org.geogebra.common.euclidian3D;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings3D;

/**
 * class for specific 3D inputs
 * 
 * @author mathieu
 *
 */
abstract public class Input3D {

	public enum DeviceType {
		HAND, PEN
	};

	public enum OutOfField {
		LEFT, RIGHT, FAR, NEAR, BOTTOM, TOP, NO, NEVER, YES
	}

	static public String PREFS_REALSENSE = "realsense";
	static public String PREFS_ZSPACE = "zspace";
	static public String PREFS_NONE = "none";

	/**
	 * 
	 * @return device type
	 */
	abstract public DeviceType getDeviceType();


	/**
	 * Center is center of the screen, unit is pixels
	 * 
	 * @return 3D mouse position
	 */
	abstract public double[] getMouse3DPosition();

	// /**
	// *
	// * @return 2D mouse x position
	// */
	// public float getMouse2DX();
	//
	// /**
	// *
	// * @return 2D mouse y position
	// */
	// public float getMouse2DY();
	//
	// /**
	// *
	// * @return 2D mouse factor
	// */
	// public float getMouse2DFactor();

	/**
	 *
	 * @return 3D mouse orientation (as quaternion)
	 */
	abstract public double[] getMouse3DOrientation();

	/**
	 * Center is center of the screen, unit is pixels
	 * 
	 * @return glasses position (two eyes center)
	 */
	abstract public double[] getGlassesPosition(int i);

	/**
	 * 
	 * @return eye separation
	 */
	abstract public double getEyeSeparation();

	/**
	 * 
	 * @return true if right button is pressed
	 */
	abstract public boolean isRightPressed();

	/**
	 * 
	 * @return true if left button is pressed
	 */
	abstract public boolean isLeftPressed();

	/**
	 * 
	 * @return true if third button is pressed
	 */
	abstract public boolean isThirdButtonPressed();

	/**
	 * 
	 * @return true if one button is pressed
	 */
	abstract public boolean isButtonPressed();

	/**
	 * 
	 * @return true if the input use depth for hitting
	 */
	abstract public boolean useInputDepthForHitting();

	/**
	 * 
	 * @return true if the input use a robot to controll 2D mouse
	 */
	abstract public boolean useMouseRobot();

	/**
	 * 
	 * @param view3D
	 *            3D view
	 * @param mouse3DPosition
	 *            current 3D mouse position
	 * @return true if input3D has mouse on 3D view
	 */
	abstract public boolean hasMouse(EuclidianView3D view3D,
			Coords mouse3DPosition);

	/**
	 * 
	 * @param view3D
	 *            3D view
	 * @return true if input3D has mouse on 3D view
	 */
	abstract public boolean hasMouse(EuclidianView3D view3D);

	/**
	 * 
	 * @return true if 3D input is currently (possibly) using 2D mouse
	 */
	abstract public boolean currentlyUseMouse2D();

	/**
	 * set left button is pressed
	 * 
	 * @param flag
	 *            flag
	 */
	abstract public void setHasCompletedGrabbingDelay(boolean flag);

	/**
	 * 
	 * @return if hand input has completed grabbing delay
	 */
	abstract public boolean hasCompletedGrabbingDelay();

	/**
	 * calc position of 3D mouse on 3D view
	 * 
	 * @param absolutePos
	 *            position from input
	 * @param panelPos
	 *            position for panel
	 * @param screenHalfWidth
	 *            screen half width
	 * @param screenHalfHeight
	 *            screen half height
	 * @param panelPositionX
	 *            panel x position on screen
	 * @param panelPositionY
	 *            panel y position on screen
	 * @param panelDimW
	 *            panel width
	 * @param panelDimH
	 *            panel height
	 */
	abstract public void setPositionXYOnPanel(double[] absolutePos,
			Coords panelPos,
			double screenHalfWidth, double screenHalfHeight,
			int panelPositionX, int panelPositionY, int panelDimW, int panelDimH);

	/**
	 * 
	 * @return true if we use z offset to make the scene out of the screen
	 */
	abstract public boolean useScreenZOffset();

	/**
	 * 
	 * @return true if it uses stereo buffers
	 */
	abstract public boolean isStereoBuffered();

	/**
	 * 
	 * @return true if uses polarized monitor with interlaced lines
	 */
	abstract public boolean useInterlacedPolarization();

	/**
	 * 
	 * @return true if using completing delay (e.g. for hand tracking -- no button)
	 */
	abstract public boolean useCompletingDelay();

	/**
	 * 
	 * @return true if the input has direction for mouse
	 */
	abstract public boolean hasMouseDirection();

	/**
	 * 
	 * @return mouse direction
	 */
	abstract public double[] getMouse3DDirection();

	/**
	 * update values
	 * 
	 * @param panelPosition
	 *            TODO
	 * @param panelDimension
	 *            TODO
	 * 
	 * @return true if the update worked
	 */
	abstract public boolean update(GPoint panelPosition,
			GDimension panelDimension);

	/**
	 * 
	 * @return true if input uses quaternions for rotate
	 */
	abstract public boolean useQuaternionsForRotate();

	/**
	 * @return true if stereo glasses are detected
	 * 
	 */
	abstract public boolean wantsStereo();

	/**
	 * 
	 * @return default rotation angle for Oz
	 */
	abstract public double getDefaultRotationOz();

	/**
	 * 
	 * @return default rotation angle for xOy
	 */
	abstract public double getDefaultRotationXOY();

	/**
	 * Inputs tracking head don't need to store eye position
	 * 
	 * @return true if we need to store stereo infos
	 */
	abstract public boolean shouldStoreStereoToXML();

	/**
	 * 
	 * @return true if this input needs gray background to minimize ghost effect
	 */
	abstract public boolean needsGrayBackground();

	/**
	 * 
	 * @return true if this input uses head tracking
	 */
	abstract public boolean useHeadTracking();

	/**
	 * 
	 * @return true if input uses hand grabbing
	 */
	abstract public boolean useHandGrabbing();

	/**
	 * 
	 * @return out of field type
	 */
	abstract public OutOfField getOutOfField();

	/**
	 * exit
	 */
	abstract public void exit();

	/**
	 * says that 3D input position leads to mouse cursor on physical screen
	 */
	abstract public void setPositionOnScreen();

	/**
	 * says that 3D input position leads to mouse cursor off physical screen
	 */
	abstract public void setPositionOffScreen();

	/**
	 * 
	 * @return true if zSpace
	 */
	abstract public boolean isZSpace();

	/**
	 * 
	 * @param settings TODO
	 */
	abstract public void setSpecificSettings(EuclidianSettings3D settings);

	private Coords[] glassesPosition;

	protected double screenHalfWidth, screenHalfHeight;
	protected int panelWidth, panelHeight, panelX, panelY;

	private EuclidianView3D view3D;

	public void init(EuclidianView3D view3D) {
		this.view3D = view3D;

		// glasses position
		glassesPosition = new Coords[2];
		for (int i = 0; i < 2; i++) {
			glassesPosition[i] = new Coords(3);
		}
	}

	public void setScreenHalfDimensions(double halfWidth, double halfHeight) {
		screenHalfWidth = halfWidth;
		screenHalfHeight = halfHeight;
	}

	public double getScreenHalfWidth() {
		return screenHalfWidth;
	}

	public double getScreenHalfHeight() {
		return screenHalfHeight;
	}

	public void setPanel(int width, int height, int x, int y) {
		panelWidth = width;
		panelHeight = height;
		panelX = x;
		panelY = y;
	}

	public int getPanelWidth() {
		return panelWidth;
	}

	public int getPanelHeight() {
		return panelHeight;
	}

	public int getPanelX() {
		return panelX;
	}

	public int getPanelY() {
		return panelY;
	}

	public void updateHeadTracking() {
		// eyes : set position only if we use glasses
		if (useHeadTracking()
				&& view3D.getProjection() == EuclidianView3D.PROJECTION_GLASSES)

		{
			for (int i = 0; i < 2; i++) {
				double[] pos = getGlassesPosition(i);
				setPositionXYOnPanel(pos, glassesPosition[i]);
				glassesPosition[i].setZ(pos[2]);
			}

			// Log.debug("\n"+glassesPosition);

			// Log.debug(input3D.getGlassesPosition()[2]+"");
			// if (eyeSepIsNotSet){
			view3D.setEyes(glassesPosition[0].getX(), glassesPosition[0].getY(),
					glassesPosition[1].getX(), glassesPosition[1].getY());
			// eyeSepIsNotSet = false;
			// }

			view3D.setProjectionPerspectiveEyeDistance(
					glassesPosition[0].getZ(), glassesPosition[1].getZ());

		}
	}

	public void setPositionXYOnPanel(double[] absolutePos, Coords panelPos) {

		setPositionXYOnPanel(absolutePos, panelPos, screenHalfWidth,
				screenHalfHeight, panelX, panelY, panelWidth, panelHeight);
	}

}
