package org.geogebra.desktop.geogebra3D.euclidianInput3D;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianControllerCompanion;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.Matrix.Quaternion;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.desktop.geogebra3D.awt.GPointWithZ;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianController3DD;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianView3DD;

/**
 * controller with specific methods from leonar3do input system
 * 
 * @author mathieu
 * 
 */
public class EuclidianControllerInput3D extends EuclidianController3DD {

	protected Input3D input3D;

	protected Coords mouse3DPosition, mouse3DScenePosition, mouse3DDirection;

	protected Coords startMouse3DPosition;

	private Quaternion mouse3DOrientation, startMouse3DOrientation;
	private Coords rotV;
	private CoordMatrix startOrientationMatrix;
	private CoordMatrix4x4 toSceneRotMatrix;

	private Coords vx, vz;

	private boolean wasRightReleased;
	private boolean wasLeftReleased;
	private boolean wasThirdButtonReleased;

	private boolean eyeSepIsNotSet = true;

	protected Robot robot;
	protected int robotX, robotY, onScreenX, onScreenY;
	protected double[] inputPosition, inputPositionOnScreen, inputDirection;

	/**
	 * constructor
	 * 
	 * @param kernel
	 *            kernel
	 * @param input3d
	 *            input3d
	 */
	public EuclidianControllerInput3D(Kernel kernel, Input3D input3d) {
		super(kernel);

		this.input3D = input3d;
		
		// 3D mouse position
		mouse3DPosition = new Coords(3);
		// mouse3DPosition.setW(1);
		startMouse3DPosition = new Coords(3);

		mouse3DScenePosition = new Coords(4);
		mouse3DScenePosition.setW(1);

		// 3D mouse direction
		if (input3D.hasMouseDirection()) {
			mouse3DDirection = new Coords(4);
		} else {
			mouse3DDirection = null;
		}

		// 3D mouse orientation
		mouse3DOrientation = new Quaternion();
		startMouse3DOrientation = new Quaternion();
		rotV = new Coords(4);
		toSceneRotMatrix = new CoordMatrix4x4();

		// 3D mouse position on screen (screen coords)
		inputPositionOnScreen = new double[2];

		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		input3D.setScreenHalfDimensions(gd.getDisplayMode().getWidth() / 2.0,
				gd.getDisplayMode().getHeight() / 2.0);

		// robot
		robot = null;
		if (input3d.useMouseRobot()) {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// buttons
		wasRightReleased = true;
		wasLeftReleased = true;
		wasThirdButtonReleased = true;

	}

	@Override
	protected EuclidianControllerCompanion newCompanion() {
		return new EuclidianControllerInput3DCompanion(this);
	}

	private void updateOnScreenPosition() {
		if (input3D.hasMouseDirection()) { // project position on
			// screen
			double dz = input3D.getMouse3DDirection()[2];
			if (dz < 0) {
				double t = -inputPosition[2] / dz;
				inputPositionOnScreen[0] = inputPosition[0] + t
						* input3D.getMouse3DDirection()[0];
				inputPositionOnScreen[1] = inputPosition[1] + t
						* input3D.getMouse3DDirection()[1];
			}
		} else {
			inputPositionOnScreen[0] = inputPosition[0];
			inputPositionOnScreen[1] = inputPosition[1];
		}

		// init to center panel
		onScreenX = input3D.getPanelX() + input3D.getPanelWidth() / 2;
		onScreenY = input3D.getPanelY() + input3D.getPanelHeight() / 2;

		// check if pointer is on screen
		int x1 = onScreenX + (int) (inputPositionOnScreen[0]);
		int y1 = onScreenY - (int) (inputPositionOnScreen[1]);
		if (x1 >= 0 && x1 <= input3D.getScreenHalfWidth() * 2 && y1 >= 0
				&& y1 <= input3D.getScreenHalfHeight() * 2) {
			onScreenX = x1;
			onScreenY = y1;
			input3D.setPositionOnScreen();
			// Log.debug("onS: " + x1 + "," + y1);
		} else {
			input3D.setPositionOffScreen();
			// Log.debug("NOT onS: " + x1 + "," + y1);
		}

	}

	private boolean isNotMovingObjectOrView() {
		return moveMode == MOVE_NONE && isNotMovingView;
	}

	@Override
	public void updateInput3D() {
		
		// update panel values
		Dimension panelDimension = ((EuclidianView3DD) view3D).getJPanel()
				.getSize();
		Point panelPosition = ((EuclidianView3DD) view3D).getJPanel()
				.getLocationOnScreen();

		input3D.setPanel(panelDimension.width, panelDimension.height,
				panelPosition.x, panelPosition.y);

		if (input3D.update()) {

			// ////////////////////
			// set values


			// eyes : set position only if we use glasses
			input3D.updateHeadTracking();

			// input position
			inputPosition = input3D.getMouse3DPosition();

			// update on screen position to check if we allow to use regular
			// mouse
			if (robot != null
					&& (isNotMovingObjectOrView() || !input3D
							.hasMouseDirection())) {
				updateOnScreenPosition();
			}

			// regular 2D mouse is used? then return
			if (input3D.currentlyUseMouse2D()) {
				return;
			}


			// 2D cursor pos
			if (robot != null) {
				if (isNotMovingObjectOrView() || !input3D.hasMouseDirection()) {
					// process mouse
					if (robotX != onScreenX || robotY != onScreenY) {
						// Log.debug(inputPosition[0]+","+inputPosition[1]+","+inputPosition[2]);
						// Log.debug(x+","+y);
						robotX = onScreenX;
						robotY = onScreenY;
						robot.mouseMove(robotX, robotY);
					}

				}
			}

			// mouse pos
			input3D.setPositionXYOnPanel(inputPosition, mouse3DPosition);
			mouse3DPosition.setZ(inputPosition[2] - view3D.getScreenZOffset());

			// check if the 3D mouse is on 3D view
			if (input3D.hasMouse(view3D, mouse3DPosition)) {
				if (!input3D.useInputDepthForHitting()
						|| mouse3DPosition.getZ() < view3D.getRenderer()
								.getEyeToScreenDistance()) {

					updateMouse3DEvent();

					// mouse direction
					if (input3D.hasMouseDirection()) {
						mouse3DDirection.setMul(view3D.getUndoRotationMatrix(),
								input3D.getMouse3DDirection());
						mouse3DScenePosition.set(mouse3DPosition);
						view3D.toSceneCoords3D(mouse3DScenePosition);

						if (moveMode != MOVE_NONE) {
							((EuclidianViewInput3D) view3D)
									.updateStylusBeamForMovedGeo();
						}
					}

					// mouse orientation
					mouse3DOrientation.set(input3D.getMouse3DOrientation());

					// Log.debug("\nstart: "+startMouse3DOrientation+"\ncurrent: "+mouse3DOrientation);

					if (input3D.isThirdButtonPressed()) { // process 3rd
						// button
						processThirdButtonPress();
						wasThirdButtonReleased = false;
						wasRightReleased = true;
						wasLeftReleased = true;
					} else if (input3D.isRightPressed()) { // process right
						// press
						processRightPress();
						wasRightReleased = false;
						wasLeftReleased = true;
						wasThirdButtonReleased = true;
					} else if (input3D.isLeftPressed()) { // process left
						// press
						if (wasLeftReleased) {
							startMouse3DPosition.set(mouse3DPosition);
							storeOrientation();
							wrapMousePressed(mouseEvent);
						} else {
							// no capture in desktop
							wrapMouseDragged(mouseEvent, false);
						}
						wasRightReleased = true;
						wasLeftReleased = false;
						wasThirdButtonReleased = true;
					} else if (input3D.hasCompletedGrabbingDelay()) { // use
						// hand
						// dragging
						if (wasLeftReleased) {
							startMouse3DPosition.set(mouse3DPosition);
							storeOrientation();
						} else {
							// no capture in desktop
							wrapMouseDragged(mouseEvent, false);
						}
						wasRightReleased = true;
						wasLeftReleased = false;
						wasThirdButtonReleased = true;
					} else {
						// process button release
						if (!wasRightReleased || !wasLeftReleased
								|| !wasThirdButtonReleased) {
							wrapMouseReleased(mouseEvent);
						}

						// process move
						wrapMouseMoved(mouseEvent);
						wasRightReleased = true;
						wasLeftReleased = true;
						wasThirdButtonReleased = true;
					}
				}

			} else if (robot != null) { // bird outside the view

				// process right press / release
				if (input3D.isRightPressed()) {
					if (wasRightReleased) {
						robot.mousePress(InputEvent.BUTTON3_MASK);
						wasRightReleased = false;
					}
				} else {
					if (!wasRightReleased) {
						robot.mouseRelease(InputEvent.BUTTON3_MASK);
						wasRightReleased = true;
					}
				}

				// process left press / release
				if (input3D.isLeftPressed()) {
					if (wasLeftReleased) {
						robot.mousePress(InputEvent.BUTTON1_MASK);
						wasLeftReleased = false;
					}
				} else {
					if (!wasLeftReleased) {
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
						wasLeftReleased = true;
					}
				}

			}
		}


	}

	/**
	 * 
	 * @return 3D mouse position
	 */
	public Coords getMouse3DPosition() {

		return mouse3DPosition;
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

	private void storeOrientation() {
		startMouse3DOrientation.set(mouse3DOrientation);
		startOrientationMatrix = startMouse3DOrientation.getRotMatrix();

		toSceneRotMatrix.set(view3D.getUndoRotationMatrix());

	}

	private Quaternion currentRot;

	/**
	 * calc current rotation
	 */
	protected void calcCurrentRot() {
		currentRot = startMouse3DOrientation.leftDivide(mouse3DOrientation);

		// get the relative quaternion and rotation matrix in scene coords
		rotV.set(startOrientationMatrix.mul(currentRot.getVector()));
		currentRot.setVector(toSceneRotMatrix.mul(rotV));
	}

	/**
	 * 
	 * @return current/start rotation as a matrix
	 */
	protected CoordMatrix getCurrentRotMatrix() {
		return currentRot.getRotMatrix();
	}

	/**
	 * 
	 * @return current rotation quaternion
	 */
	protected Quaternion getCurrentRotQuaternion() {
		return currentRot;
	}

	private void processThirdButtonPress() {
		if (wasThirdButtonReleased) {
			setMouse3DPositionShifted(startMouse3DPosition);
			view.rememberOrigins();
			isNotMovingView = false;
		} else {
			getShiftForMouse3D(tmpCoords);
			tmpCoords.setAdd3(tmpCoords, mouse3DPosition);
			tmpCoords.setSub(tmpCoords, startMouse3DPosition);

			tmpCoords2.setMul(view3D.getToSceneMatrix(), tmpCoords.val);

			((EuclidianViewInput3D) view3D)
					.setCoordSystemFromMouse3DMove(tmpCoords2);

		}
	}

	private void setMouse3DPositionShifted(Coords ret) {
		getShiftForMouse3D(tmpCoords2);
		tmpCoords2.setAdd(mouse3DPosition, tmpCoords2);
		ret.set(tmpCoords2);
	}


	/**
	 * get shift in beam direction about 150px
	 * 
	 * @param ret
	 *            result
	 */
	private void getShiftForMouse3D(Coords ret) {
		ret.set(input3D.getMouse3DDirection());
		ret.mulInside(150);
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
			if (input3D.useQuaternionsForRotate()) {
				startRightPressQuaternions();
			} else {
				startRightPress();
			}
		} else { // process mouse drag
			if (input3D.useQuaternionsForRotate()) {
				processRightDragQuaternions();
			} else {
				processRightDrag();
			}
		}

	}

	private void startRightPressQuaternions() {
		startMouse3DPosition.set(mouse3DPosition);

		view.rememberOrigins();
		((EuclidianViewInput3D) view).setStartPos(startMouse3DPosition);

		storeOrientation();

		// to-the-right screen vector in scene coords
		vx = toSceneRotMatrix.mul(Coords.VX);
	}

	private void processRightDragQuaternions() {

		// rotation
		calcCurrentRot();
		CoordMatrix rotMatrix = getCurrentRotMatrix();

		// Log.debug("\n"+rot);

		// rotate view vZ
		Coords vZrot = rotMatrix.getVz();
		// Log.debug("\n"+vZrot);
		Coords vZ1 = (vZrot.sub(vx.mul(vZrot.dotproduct(vx)))).normalize(); // project
																			// the
																			// rotation
																			// to
																			// keep
																			// vector
																			// plane
																			// orthogonal
																			// to
																			// the
																			// screen
		Coords vZp = Coords.VZ.crossProduct(vZ1); // to get angle (vZ,vZ1)

		// rotate screen vx
		Coords vxRot = rotMatrix.mul(vx);
		Coords vx1 = (vxRot.sub(vZ1.mul(vxRot.dotproduct(vZ1)))).normalize(); // project
																				// in
																				// plane
																				// orthogonal
																				// to
																				// vZ1
		Coords vxp = vx.crossProduct(vx1); // to get angle (vx,vx1)

		// rotation around x (screen)
		double rotX = Math.asin(vxp.norm()) * 180 / Math.PI;
		// Log.debug("rotX="+rotX+", vx1.dotproduct(vx) = "+vx1.dotproduct(vx)+", vxp.dotproduct(vZ1) = "+vxp.dotproduct(vZ1));
		if (vx1.dotproduct(vx) < 0) { // check if rotX should be > 90degrees
			rotX = 180 - rotX;
		}
		if (vxp.dotproduct(vZ1) > 0) { // check if rotX should be negative
			rotX = -rotX;
		}

		// rotation around z (scene)
		double rotZ = Math.asin(vZp.norm()) * 180 / Math.PI;
		// Log.debug("rotZ="+rotZ+", vZp.dotproduct(vx) = "+vZp.dotproduct(vx)+", Coords.VZ.dotproduct(vZ1) = "+vZ1.getZ());
		if (vZ1.getZ() < 0) { // check if rotZ should be > 90degrees
			rotZ = 180 - rotZ;
		}
		if (vZp.dotproduct(vx) < 0) { // check if rotZ should be negative
			rotZ = -rotZ;
		}

		// Log.debug("rotZ="+rotZ);

		// set the view
		((EuclidianViewInput3D) view).setCoordSystemFromMouse3DMove(
				startMouse3DPosition, mouse3DPosition, rotX, rotZ);

		/*
		 * // USE FOR CHECK 3D MOUSE ORIENTATION // use file
		 * leonar3do-rotation2.ggb GeoVector3D geovx = (GeoVector3D)
		 * getKernel().lookupLabel("vx");
		 * geovx.setCoords(toSceneRotMatrix.mul(Coords.VX).normalize());
		 * geovx.updateCascade(); GeoVector3D vy = (GeoVector3D)
		 * getKernel().lookupLabel("vy");
		 * vy.setCoords(toSceneRotMatrix.mul(Coords.VY).normalize());
		 * vy.updateCascade(); GeoVector3D vz = (GeoVector3D)
		 * getKernel().lookupLabel("vz");
		 * vz.setCoords(toSceneRotMatrix.mul(Coords.VZ).normalize());
		 * vz.updateCascade();
		 * 
		 * 
		 * GeoAngle a = (GeoAngle) getKernel().lookupLabel("angle"); GeoVector3D
		 * v = (GeoVector3D) getKernel().lookupLabel("v");
		 * a.setValue(2*Math.acos(rot.getScalar()));
		 * v.setCoords(rot.getVector()); a.updateCascade(); v.updateCascade();
		 * 
		 * GeoText text = (GeoText) getKernel().lookupLabel("text");
		 * text.setTextString
		 * ("az = "+rotZ+"degrees\n"+"ax = "+rotX+"degrees\n"+
		 * "vxp.dotproduct(vZ1)="
		 * +vxp.dotproduct(vZ1)+"\nvx1.dotproduct(vx)="+vx1.dotproduct(vx)
		 * +"\nvZp.dotproduct(vx) = "+vZp.dotproduct(vx) ); text.update();
		 * getKernel().notifyRepaint();
		 */
	}


	private double angleOld;

	private void startRightPress() {

		// automatic rotation
		if (view3D.isRotAnimated()) {
			view3D.stopAnimation();
			viewRotationOccured = true;
		}

		timeOld = app.getMillisecondTime();
		animatedRotSpeed = 0;
		angleOld = 0;

		// start values
		startMouse3DPosition.set(mouse3DPosition);

		view.rememberOrigins();
		vz = view3D.getRotationMatrix().getVz();
		
		isNotMovingView = false;
	}

	private boolean isNotMovingView = true;

	private Coords tmpCoords = new Coords(3), tmpCoords2 = new Coords(3),
			tmpCoords3 = new Coords(3);

	private Coords rightDragElevation = new Coords(3);

	/**
	 * 
	 * @return elevation for cursor when right-drag
	 */
	public Coords getRightDragElevation() {
		return rightDragElevation;
	}

	private void processRightDrag() {

		tmpCoords.setValues(mouse3DPosition, 3);
		rightDragElevation.setMul(vz, tmpCoords.dotproduct(vz));
		tmpCoords2.setSub(tmpCoords, rightDragElevation);

		tmpCoords.setMul(vz, startMouse3DPosition.dotproduct(vz));
		tmpCoords.setSub(startMouse3DPosition, tmpCoords);

		tmpCoords3.setCrossProduct(tmpCoords, tmpCoords2);

		double c = tmpCoords.dotproduct(tmpCoords2);
		double s = tmpCoords3.calcNorm();
		double angle = Math.atan2(s, c) * 180 / Math.PI;
		if (tmpCoords3.dotproduct(vz) > 0) {
			angle *= -1;
		}

		view3D.shiftRotAboutZ(angle);

		double time = app.getMillisecondTime();
		animatedRotSpeed = (angleOld - angle) / (time - timeOld);
		timeOld = time;
		angleOld = angle;

	}

	private void processRightRelease() {
		((EuclidianView3D) view).setRotContinueAnimation(
				app.getMillisecondTime() - timeOld, animatedRotSpeed);
	}


	private GPointWithZ mouse3DLoc = new GPointWithZ();

	private void updateMouse3DEvent() {

		mouse3DLoc = new GPointWithZ(
				input3D.getPanelWidth() / 2
						+ (int) mouse3DPosition.getX(),
				input3D.getPanelHeight() / 2
				- (int) mouse3DPosition.getY(), (int) mouse3DPosition.getZ());

		mouseEvent = new Mouse3DEvent(mouse3DLoc,
				((EuclidianView3DD) view3D).getJPanel());

	}

	@Override
	protected void setMouseLocation(AbstractEvent event) {
		if (input3D.currentlyUseMouse2D()) {
			super.setMouseLocation(event);
		} else {
			mouseLoc = event.getPoint();
		}
	}

	protected Coords movedGeoPointStartCoords = new Coords(0, 0, 0, 1);

	@Override
	protected void updateMovedGeoPointStartValues(Coords coords) {
		if (input3D.currentlyUseMouse2D()) {
			super.updateMovedGeoPointStartValues(coords);
		} else {
			movedGeoPointStartCoords.set(coords);
			if (input3D.hasMouseDirection()) {
				startZNearest = ((EuclidianViewInput3D) view3D).getZNearest();
			}
		}
	}



	@Override
	public void mousePressed(MouseEvent e) {
		if (input3D.currentlyUseMouse2D()) {
			super.mousePressed(e);
		} else if (input3D.useHandGrabbing() && mode != MOVE_NONE) {
			releaseGrabbing();
			super.mousePressed(e);
		}
	}



	@Override
	public void mouseReleased(MouseEvent e) {
		if (input3D.currentlyUseMouse2D()) {
			super.mouseReleased(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (input3D.currentlyUseMouse2D()) {
			super.mouseMoved(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (input3D.currentlyUseMouse2D()) {
			super.mouseDragged(e);
		}
	}

	@Override
	public boolean hasInput3D() {
		return true;
	}

	@Override
	public boolean useInputDepthForHitting() {
		return input3D.useInputDepthForHitting();
	}

	@Override
	final protected boolean handleMovedElementFreePlane() {
		if (movedGeoElement.isGeoPlane()) {
			moveMode = MOVE_PLANE;
			setMovedGeoPlane(movedGeoElement);

			return true;
		}

		return false;
	}

	protected GeoPlane3D movedGeoPlane;
	protected CoordSys movedGeoPlaneStartCoordSys;
	private Coords movedGeoStartPosition;

	protected ArrayList<GeoPointND> stickyPoints;

	/**
	 * set plane to move
	 * 
	 * @param geo
	 *            moved geo
	 */
	public void setMovedGeoPlane(GeoElement geo) {

		movedGeoPlane = (GeoPlane3D) geo;

		if (movedGeoPlaneStartCoordSys == null) {
			movedGeoPlaneStartCoordSys = new CoordSys(2);
		}
		movedGeoPlaneStartCoordSys.set(movedGeoPlane.getCoordSys());

		if (movedGeoStartPosition == null) {
			movedGeoStartPosition = new Coords(4);
		}
		movedGeoStartPosition.set(mouse3DPosition);

		updateMovedGeoPointStartValues(view3D.getCursor3D()
				.getInhomCoordsInD(3));

		view3D.setDragCursor();

		// set sticky points
		if (stickyPoints == null) {
			stickyPoints = new ArrayList<GeoPointND>();
		} else {
			stickyPoints.clear();
		}

		for (GeoElement geo1 : geo.getConstruction()
				.getGeoSetConstructionOrder()) {
			if (geo1.isGeoPoint() && geo1.isVisibleInView3D()
					&& !geo1.isChildOf(geo)) {
				stickyPoints.add((GeoPointND) geo1);
			}
		}

	}

	protected double startZNearest;

	@Override
	public float getPointCapturingPercentage() {
		return 2f * super.getPointCapturingPercentage();
	}

	@Override
	public boolean cursor3DVisibleForCurrentMode(int cursorType) {
		if (mode == EuclidianConstants.MODE_MOVE
				&& !input3D.hasMouseDirection()
				&& !input3D.currentlyUseMouse2D()) {
			return false;
		}

		return super.cursor3DVisibleForCurrentMode(cursorType);

	}

	@Override
	public GPoint getMouseLoc() {
		if (input3D.currentlyUseMouse2D()) {
			return super.getMouseLoc();
		}

		return mouse3DLoc;
	}

	@Override
	protected void setMouseOrigin(GeoPoint3D point) {

		if (input3D.hasMouseDirection() && !input3D.currentlyUseMouse2D()) {
			point.setWillingCoords(getMouse3DScenePosition());
		} else {
			super.setMouseOrigin(point);
		}
	}

	@Override
	protected int getModeForShallMoveView(AbstractEvent event) {
		if (input3D.currentlyUseMouse2D()) {
			return super.getModeForShallMoveView(event);
		}

		return EuclidianConstants.MODE_MOVE;
	}

	@Override
	public void wrapMouseReleased(AbstractEvent e) {
		isNotMovingView = true;
		if (!wasRightReleased && !input3D.useQuaternionsForRotate()) {
			processRightRelease();
			return;
		}

		super.wrapMouseReleased(e);

	}

	/**
	 * release hand grabbing
	 */
	protected void releaseGrabbing() {
		((EuclidianViewInput3D) view3D).getStationaryCoords()
				.consumeLongDelay();
		((EuclidianControllerInput3DCompanion) getCompanion())
				.releaseGrabbing();
	}

	@Override
	public void exitInput3D() {
		input3D.exit();
	}

	@Override
	public boolean isZSpace() {
		return input3D.isZSpace();
	}

}
