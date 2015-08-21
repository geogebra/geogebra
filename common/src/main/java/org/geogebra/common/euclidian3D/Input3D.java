package org.geogebra.common.euclidian3D;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * interface for specific 3D inputs
 * 
 * @author mathieu
 *
 */
public interface Input3D {

	public enum DeviceType {
		HAND, PEN
	};

	/**
	 * 
	 * @return device type
	 */
	public DeviceType getDeviceType();


	/**
	 * Center is center of the screen, unit is pixels
	 * 
	 * @return 3D mouse position
	 */
	public double[] getMouse3DPosition();

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
	public double[] getMouse3DOrientation();

	/**
	 * Center is center of the screen, unit is pixels
	 * 
	 * @return glasses position (two eyes center)
	 */
	public double[] getGlassesPosition(int i);

	/**
	 * 
	 * @return eye separation
	 */
	public double getEyeSeparation();

	/**
	 * 
	 * @return true if right button is pressed
	 */
	public boolean isRightPressed();

	/**
	 * 
	 * @return true if left button is pressed
	 */
	public boolean isLeftPressed();

	/**
	 * 
	 * @return true if third button is pressed
	 */
	public boolean isThirdButtonPressed();

	/**
	 * 
	 * @return true if the input use depth for hitting
	 */
	public boolean useInputDepthForHitting();

	/**
	 * 
	 * @return true if the input use a robot to controll 2D mouse
	 */
	public boolean useMouseRobot();

	/**
	 * 
	 * @param view3D
	 *            3D view
	 * @return true if input3D has mouse on 3D view
	 */
	public boolean hasMouse(EuclidianView3D view3D);

	/**
	 * 
	 * @return true if 3D input is currently (possibly) using 2D mouse
	 */
	public boolean currentlyUseMouse2D();

	/**
	 * set left button is pressed
	 * 
	 * @param flag
	 *            flag
	 */
	public void setHasCompletedGrabbingDelay(boolean flag);

	/**
	 * 
	 * @return if hand input has completed grabbing delay
	 */
	public boolean hasCompletedGrabbingDelay();

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
	public void setPositionXYOnPanel(double[] absolutePos, Coords panelPos,
			double screenHalfWidth, double screenHalfHeight,
			int panelPositionX, int panelPositionY, int panelDimW, int panelDimH);

	/**
	 * 
	 * @return true if we use z offset to make the scene out of the screen
	 */
	public boolean useScreenZOffset();

	/**
	 * 
	 * @return true if it uses stereo buffers
	 */
	public boolean isStereoBuffered();

	/**
	 * 
	 * @return true if uses polarized monitor with interlaced lines
	 */
	public boolean useInterlacedPolarization();

	/**
	 * 
	 * @return true if using completing delay (e.g. for hand tracking -- no button)
	 */
	public boolean useCompletingDelay();

	/**
	 * 
	 * @return true if the input has direction for mouse
	 */
	public boolean hasMouseDirection();

	/**
	 * 
	 * @return mouse direction
	 */
	public double[] getMouse3DDirection();

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
	public boolean update(GPoint panelPosition, GDimension panelDimension);

	/**
	 * 
	 * @return true if input uses quaternions for rotate
	 */
	public boolean useQuaternionsForRotate();

	/**
	 * @return true if stereo glasses are detected
	 * 
	 */
	public boolean wantsStereo();

	/**
	 * 
	 * @return default rotation angle for Oz
	 */
	public double getDefaultRotationOz();

	/**
	 * 
	 * @return default rotation angle for xOy
	 */
	public double getDefaultRotationXOY();

	/**
	 * Inputs tracking head don't need to store eye position
	 * 
	 * @return true if we need to store stereo infos
	 */
	public boolean shouldStoreStereoToXML();

	/**
	 * 
	 * @return true if this input needs gray background to minimize ghost effect
	 */
	public boolean needsGrayBackground();

	/**
	 * 
	 * @return true if this input uses head tracking
	 */
	public boolean useHeadTracking();

	/**
	 * 
	 * @return true if input uses hand grabbing
	 */
	public boolean useHandGrabbing();

}
