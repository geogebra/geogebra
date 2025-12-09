/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.input3D;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPointWithZ;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian3D.Mouse3DEvent;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.matrix.Quaternion;
import org.geogebra.common.main.settings.EuclidianSettings3D;

/**
 * class for specific 3D inputs
 * 
 * @author mathieu
 *
 */
abstract public class Input3D {

	/**
	 * Input device type.
	 */
	public enum DeviceType {
		HAND, PEN
	}

	/**
	 * Direction in which we can be out of visible field.
	 */
	public enum OutOfField {
		LEFT, RIGHT, FAR, NEAR, BOTTOM, TOP, NO, NEVER, YES
	}

	private final Coords mouse3DScenePosition;
	private final Coords mouse3DDirection;
	protected Coords[] glassesPosition;

	protected double screenHalfWidth;
	protected double screenHalfHeight;
	protected int panelWidth;
	protected int panelHeight;
	protected int panelX;
	protected int panelY;

	protected EuclidianView3D view3D;

	private final double[] inputPositionOnScreen;

	protected int onScreenX;
	protected int onScreenY;
	private final Coords mouse3DPosition;

	private boolean wasRightReleased;
	private boolean wasLeftReleased;
	private boolean wasThirdButtonReleased;
	private final Coords tmpCoords = new Coords(3);
	private final Coords tmpCoords2 = new Coords(3);
	private final Coords tmpCoords3 = new Coords(3);

	private final Coords startMouse3DPosition;

	private Coords vx;
	private Coords vz;

	private final Coords rightDragElevation = new Coords(3);

	private final Quaternion mouse3DOrientation;
	private final Quaternion startMouse3DOrientation;
	private final Coords rotV;
	private CoordMatrix startOrientationMatrix;
	private final CoordMatrix4x4 toSceneRotMatrix;

	private Quaternion currentRot;

	private GPointWithZ mouse3DLoc = new GPointWithZ();

	private Mouse3DEvent mouse3DEvent;

	/**
	 * simple constructor
	 */
	public Input3D() {
		// 3D mouse position on screen (screen coords)
		inputPositionOnScreen = new double[2];

		// 3D mouse position
		mouse3DPosition = new Coords(3);
		startMouse3DPosition = new Coords(3);

		mouse3DScenePosition = new Coords(4);
		mouse3DScenePosition.setW(1);

		// 3D mouse direction
		if (hasMouseDirection()) {
			mouse3DDirection = new Coords(4);
		} else {
			mouse3DDirection = null;
		}

		// 3D mouse orientation
		mouse3DOrientation = new Quaternion();
		startMouse3DOrientation = new Quaternion();
		rotV = new Coords(4);
		toSceneRotMatrix = new CoordMatrix4x4();

		// buttons
		wasRightReleased = true;
		wasLeftReleased = true;
		wasThirdButtonReleased = true;
	}

	/**
	 * 
	 * @return device type
	 */
	abstract public DeviceType getDeviceType();

	/**
	 * Center is center of the screen, unit is pixels
	 * 
	 * @return input position
	 */
	abstract public double[] getInputPosition();

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
	 * @return input orientation (as quaternion)
	 */
	abstract public double[] getInputOrientation();

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
	 * @return true if the input use a robot to control 2D mouse
	 */
	abstract public boolean useMouseRobot();

	/**
	 * 
	 * @param ev3D
	 *            3D view
	 * @param mouse3D
	 *            current 3D mouse position
	 * @return true if input3D has mouse on 3D view
	 */
	abstract public boolean hasMouse(EuclidianView3D ev3D,
			Coords mouse3D);

	/**
	 * 
	 * @param ev3D
	 *            3D view
	 * @return true if input3D has mouse on 3D view
	 */
	abstract public boolean hasMouse(EuclidianView3D ev3D);

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
			Coords panelPos, double screenHalfWidth, double screenHalfHeight,
			int panelPositionX, int panelPositionY, int panelDimW,
			int panelDimH);

	/**
	 * 
	 * @return true if it uses stereo buffers
	 */
	abstract public boolean isStereoBuffered();

	/**
	 * 
	 * @return true if using completing delay (e.g. for hand tracking -- no
	 *         button)
	 */
	abstract public boolean useCompletingDelay();

	/**
	 * 
	 * @return true if the input has direction for mouse
	 */
	abstract public boolean hasMouseDirection();

	/**
	 * 
	 * @return input direction
	 */
	abstract public double[] getInputDirection();

	/**
	 * update values
	 * 
	 * @return true if the update worked
	 */
	abstract public boolean update();

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
	 * @param settings
	 *            TODO
	 */
	abstract public void setSpecificSettings(EuclidianSettings3D settings);

	/**
	 * @param eView3D
	 *            3D view
	 */
	public void init(EuclidianView3D eView3D) {
		this.view3D = eView3D;

		// glasses position
		glassesPosition = new Coords[2];
		for (int i = 0; i < 2; i++) {
			glassesPosition[i] = new Coords(3);
		}
	}

	/**
	 * @param halfWidth
	 *            half-width
	 * @param halfHeight
	 *            half-height
	 */
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

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param x
	 *            panel left
	 * @param y
	 *            panel top
	 */
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

	/**
	 * Update glasses position.
	 */
	protected void setGlassesPosition() {
		for (int i = 0; i < 2; i++) {
			double[] pos = getGlassesPosition(i);
			setPositionXYOnPanel(pos, glassesPosition[i]);
			glassesPosition[i].setZ(pos[2]);
		}
	}

	/**
	 * Update head tracking.
	 */
	public void updateHeadTracking() {
		// eyes : set position only if we use glasses
		if (useHeadTracking() && view3D
				.getProjection() == EuclidianView3D.PROJECTION_GLASSES) {

			// set glasses position from tracker data
			setGlassesPosition();

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

	/**
	 * @param absolutePos
	 *            position from input
	 * @param panelPos
	 *            position for panel
	 */
	public void setPositionXYOnPanel(double[] absolutePos, Coords panelPos) {
		setPositionXYOnPanel(absolutePos, panelPos, screenHalfWidth,
				screenHalfHeight, panelX, panelY, panelWidth, panelHeight);
	}

	/**
	 * Update input position on screen.
	 */
	public void updateOnScreenPosition() {
		if (hasMouseDirection()) { // project position on
			// screen
			double dz = getInputDirection()[2];
			if (dz < 0) {
				double t = -getInputPosition()[2] / dz;
				inputPositionOnScreen[0] = getInputPosition()[0]
						+ t * getInputDirection()[0];
				inputPositionOnScreen[1] = getInputPosition()[1]
						+ t * getInputDirection()[1];
			}
		} else {
			inputPositionOnScreen[0] = getInputPosition()[0];
			inputPositionOnScreen[1] = getInputPosition()[1];
		}

		// init to center panel
		onScreenX = getPanelX() + getPanelWidth() / 2;
		onScreenY = getPanelY() + getPanelHeight() / 2;

		// check if pointer is on screen
		int x1 = onScreenX + (int) inputPositionOnScreen[0];
		int y1 = onScreenY - (int) inputPositionOnScreen[1];
		if (x1 >= 0 && x1 <= getScreenHalfWidth() * 2 && y1 >= 0
				&& y1 <= getScreenHalfHeight() * 2) {
			onScreenX = x1;
			onScreenY = y1;
			setPositionOnScreen();
			// Log.debug("onS: " + x1 + "," + y1);
		} else {
			setPositionOffScreen();
			// Log.debug("NOT onS: " + x1 + "," + y1);
		}
	}

	public int getOnScreenX() {
		return onScreenX;
	}

	public int getOnScreenY() {
		return onScreenY;
	}

	public Coords getMouse3DPosition() {
		return mouse3DPosition;
	}

	/**
	 * Update mouse position.
	 */
	public void updateMousePosition() {
		setPositionXYOnPanel(getInputPosition(), mouse3DPosition);
		mouse3DPosition.setZ(getInputPosition()[2]);
	}

	/**
	 * @return whether mouse input is available
	 */
	public boolean hasMouse() {
		return hasMouse(view3D, mouse3DPosition);
	}

	private void processThirdButtonPress() {
		if (wasThirdButtonReleased) {
			setMouse3DPositionShifted(startMouse3DPosition);
			view3D.rememberOrigins();
		} else {
			getShiftForMouse3D(tmpCoords);
			tmpCoords.setAdd3(tmpCoords, mouse3DPosition);
			tmpCoords.setSub(tmpCoords, startMouse3DPosition);

			tmpCoords2.setMul(view3D.getToSceneMatrix(), tmpCoords.val);

			view3D.setCoordSystemFromMouse3DMove(tmpCoords2);
		}
	}

	/**
	 * get shift in beam direction about 150px
	 * 
	 * @param ret
	 *            result
	 */
	private void getShiftForMouse3D(Coords ret) {
		ret.set(getInputDirection());
		ret.mulInside(150);
	}

	private void setMouse3DPositionShifted(Coords ret) {
		getShiftForMouse3D(tmpCoords2);
		tmpCoords2.setAdd(mouse3DPosition, tmpCoords2);
		ret.set(tmpCoords2);
	}

	/**
	 * get mouse 3D position for translate view
	 * 
	 * @param ret
	 *            coords set
	 */
	public void getMouse3DPositionShifted(Coords ret) {
		getShiftForMouse3D(tmpCoords);
		tmpCoords.setAdd3(tmpCoords, mouse3DPosition);
		ret.setMul(view3D.getToSceneMatrix(), tmpCoords.val);
		ret.setW(0.0);
		ret.addInside(view3D.getToSceneMatrix().getOrigin());

	}

	private void processRightPress() {

		if (wasRightReleased) { // process first press : remember mouse start
			if (useQuaternionsForRotate()) {
				startRightPressQuaternions();
			} else {
				startRightPress();
			}
		} else { // process mouse drag
			if (useQuaternionsForRotate()) {
				processRightDragQuaternions();
			} else {
				processRightDrag();
			}
		}

	}

	private void startRightPressQuaternions() {
		startMouse3DPosition.set(mouse3DPosition);

		view3D.rememberOrigins();
		view3D.setStartPos(startMouse3DPosition);

		storeOrientation();

		// to-the-right screen vector in scene coords
		vx = toSceneRotMatrix.mul(Coords.VX);
	}

	private EuclidianController3D getEuclidianController() {
		return (EuclidianController3D) view3D.getEuclidianController();
	}

	private void startRightPress() {

		// automatic rotation
		if (view3D.isRotAnimated()) {
			view3D.stopAnimation();
			getEuclidianController().setViewRotationOccurred(true);
		}

		getEuclidianController().getRotationSpeedHandler().setStart(0,
				PointerEventType.MOUSE);

		// start values
		startMouse3DPosition.set(mouse3DPosition);

		view3D.rememberOrigins();
		vz = view3D.getRotationMatrix().getVz();
	}

	private void processRightDrag() {

		tmpCoords.setValues(mouse3DPosition, 3);
		rightDragElevation.setMul(vz, tmpCoords.dotproduct(vz));
		tmpCoords2.setSub(tmpCoords, rightDragElevation);

		tmpCoords.setMul(vz, startMouse3DPosition.dotproduct(vz));
		tmpCoords.setSub(startMouse3DPosition, tmpCoords);

		tmpCoords3.setCrossProduct3(tmpCoords, tmpCoords2);

		double c = tmpCoords.dotproduct(tmpCoords2);
		double s = tmpCoords3.calcNorm();
		double angle = Math.atan2(s, c) * 180 / Math.PI;
		if (tmpCoords3.dotproduct(vz) > 0) {
			angle *= -1;
		}

		view3D.shiftRotAboutZ(angle);
		getEuclidianController().getRotationSpeedHandler()
				.rotationOccurred(angle);
	}

	private void processRightDragQuaternions() {

		// rotation
		calcCurrentRot();
		CoordMatrix rotMatrix = getCurrentRotMatrix();

		// Log.debug("\n"+rot);

		// rotate view vZ
		Coords vZrot = rotMatrix.getVz();
		// project the rotation to keep the vector plane orthogonal to the screen
		Coords vZ1 = vZrot.sub(vx.mul(vZrot.dotproduct(vx))).normalize();
		Coords vZp = Coords.VZ.crossProduct(vZ1); // to get angle (vZ,vZ1)

		// rotate screen vx
		Coords vxRot = rotMatrix.mul(vx);
		// project in plane orthogonal to vZ1
		Coords vx1 = vxRot.sub(vZ1.mul(vxRot.dotproduct(vZ1))).normalize();
		Coords vxp = vx.crossProduct(vx1); // to get angle (vx,vx1)

		// rotation around x (screen)
		double rotX = Math.asin(vxp.norm()) * 180 / Math.PI;
		if (vx1.dotproduct(vx) < 0) { // check if rotX should be > 90degrees
			rotX = 180 - rotX;
		}
		if (vxp.dotproduct(vZ1) > 0) { // check if rotX should be negative
			rotX = -rotX;
		}

		// rotation around z (scene)
		double rotZ = Math.asin(vZp.norm()) * 180 / Math.PI;
		if (vZ1.getZ() < 0) { // check if rotZ should be > 90degrees
			rotZ = 180 - rotZ;
		}
		if (vZp.dotproduct(vx) < 0) { // check if rotZ should be negative
			rotZ = -rotZ;
		}

		// set the view
		view3D.setCoordSystemFromMouse3DMove(startMouse3DPosition,
				mouse3DPosition, rotX, rotZ);
	}

	private void storeOrientation() {
		startMouse3DOrientation.set(mouse3DOrientation);
		startOrientationMatrix = startMouse3DOrientation.getRotMatrix();

		toSceneRotMatrix.set(view3D.getUndoRotationMatrix());
	}

	/**
	 * calc current rotation
	 */
	public void calcCurrentRot() {
		currentRot = startMouse3DOrientation.leftDivide(mouse3DOrientation);

		// get the relative quaternion and rotation matrix in scene coords
		rotV.set(startOrientationMatrix.mul(currentRot.getVector()));
		currentRot.setVector(toSceneRotMatrix.mul(rotV));
	}

	/**
	 * 
	 * @return current/start rotation as a matrix
	 */
	public CoordMatrix getCurrentRotMatrix() {
		return currentRot.getRotMatrix();
	}

	/**
	 * 
	 * @return current rotation quaternion
	 */
	protected Quaternion getCurrentRotQuaternion() {
		return currentRot;
	}

	/**
	 * Update button released flags, check for completed grab.
	 */
	public void handleButtons() {
		if (isThirdButtonPressed()) { // process 3rd
			// button
			processThirdButtonPress();
			wasThirdButtonReleased = false;
			wasRightReleased = true;
			wasLeftReleased = true;
		} else if (isRightPressed()) { // process right
			// press
			processRightPress();
			wasRightReleased = false;
			wasLeftReleased = true;
			wasThirdButtonReleased = true;
		} else if (isLeftPressed()) { // process left
			// press
			if (wasLeftReleased) {
				startMouse3DPosition.set(mouse3DPosition);
				storeOrientation();
				view3D.getEuclidianController().wrapMousePressed(mouse3DEvent);
			} else {
				// no capture in desktop
				view3D.getEuclidianController().wrapMouseDragged(mouse3DEvent,
						false);
			}
			wasRightReleased = true;
			wasLeftReleased = false;
			wasThirdButtonReleased = true;
		} else if (hasCompletedGrabbingDelay()) { // use
			// hand
			// dragging
			if (wasLeftReleased) {
				startMouse3DPosition.set(mouse3DPosition);
				storeOrientation();
			} else {
				// no capture in desktop
				view3D.getEuclidianController().wrapMouseDragged(mouse3DEvent,
						false);
			}
			wasRightReleased = true;
			wasLeftReleased = false;
			wasThirdButtonReleased = true;
		} else {
			// process button release
			if (!wasRightReleased || !wasLeftReleased
					|| !wasThirdButtonReleased) {
				view3D.getEuclidianController().wrapMouseReleased(mouse3DEvent);
			}

			// process move
			view3D.getEuclidianController().wrapMouseMoved(mouse3DEvent);
			wasRightReleased = true;
			wasLeftReleased = true;
			wasThirdButtonReleased = true;
		}
	}

	/**
	 * 
	 * @return elevation for cursor when right-drag
	 */
	public Coords getRightDragElevation() {
		return rightDragElevation;
	}

	/**
	 * Update 3D mouse location / direction.
	 */
	final public void updateMouse3DEvent() {
		mouse3DLoc = new GPointWithZ(
				getPanelWidth() / 2 + (int) mouse3DPosition.getX(),
				getPanelHeight() / 2 - (int) mouse3DPosition.getY(),
				(int) mouse3DPosition.getZ());

		mouse3DEvent = view3D.createMouse3DEvent(mouse3DLoc);

		// mouse direction
		if (hasMouseDirection()) {
			mouse3DDirection.setMul(view3D.getUndoRotationMatrix(),
					getInputDirection());
			mouse3DScenePosition.set(getMouse3DPosition());
			view3D.toSceneCoords3D(mouse3DScenePosition);

			view3D.getCompanion().updateStylusBeamForMovedGeo();

		}

		// mouse orientation
		mouse3DOrientation.set(getInputOrientation());

		// Log.debug("\nstart: "+startMouse3DOrientation+"\ncurrent:
		// "+mouse3DOrientation);
	}

	public GPoint getMouseLoc() {
		return mouse3DLoc;
	}

	/**
	 * TODO remove
	 * @return right removed?
	 */
	public boolean wasRightReleased() {
		return wasRightReleased;
	}

	public void setWasRightReleased(boolean flag) {
		wasRightReleased = flag;
	}

	/**
	 * TODO remove
	 * @return left released?
	 */
	public boolean wasLeftReleased() {
		return wasLeftReleased;
	}

	public void setWasLeftReleased(boolean flag) {
		wasLeftReleased = flag;
	}

	/**
	 * 
	 * @return 3D mouse position (scene coords)
	 */
	public Coords getMouse3DScenePosition() {
		return mouse3DScenePosition;
	}

	/**
	 * 
	 * @return 3D mouse direction
	 */
	public Coords getMouse3DDirection() {
		return mouse3DDirection;
	}

	public Coords getStartMouse3DPosition() {
		return startMouse3DPosition;
	}

	/**
	 * 
	 * @return true for some 3D stereo devices
	 */
	public boolean useOnlyProjectionGlasses() {
		return false;
	}
}
