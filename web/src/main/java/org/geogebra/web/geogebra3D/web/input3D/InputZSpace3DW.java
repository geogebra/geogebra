package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.web.full.gui.layout.DockPanelW;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 3D input for zspace
 *
 */
public class InputZSpace3DW extends Input3D {

	private ZSpaceGwt zSpace;

	private static final double EYE_SEP_HALF = 0.035;

	private double toPixelRatio = 3600;

	private double[] inputPosition;
	private double[] inputDirection;
	private double[] inputOrientation;

	private double[][] inputGlassesPosition;
	private double eyeSeparation;
	private boolean isRightPressed;
	private boolean isLeftPressed;
	private boolean isThirdButtonPressed;
	private boolean hasStylusNotIntersectingPhysicalScreen = true;

	/**
	 * Create new ZSpace input.
	 */
	public InputZSpace3DW() {
		super();
		inputPosition = new double[3];
		inputDirection = new double[3];
		inputOrientation = new double[4];

		// glasses position
		inputGlassesPosition = new double[2][];
		for (int i = 0; i < 2; i++) {
			inputGlassesPosition[i] = new double[3];
		}
	}

	/**
	 * @param zSpace
	 *            GWT zspace
	 */
	public void setZSpace(ZSpaceGwt zSpace) {
		this.zSpace = zSpace;
	}
	
	static private void updateStylus(double toPixelRatio, double m00,
			double m10, double m20,
			double m01, double m11, double m21, double m02, double m12,
			double m22, double m03, double m13, double m23, double[] position,
			double[] direction, double[] orientation) {

		// update x, y, z
		position[0] = m03 * toPixelRatio;
		position[1] = m13 * toPixelRatio;
		position[2] = m23 * toPixelRatio;
		
		// update direction x, y, z
		direction[0] = m02;
		direction[1] = m12;
		direction[2] = m22;
		
		// update quaternion
		// (from http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion)
		
		double tr = m00 + m11 + m22;

		if (tr > 0) { 
			double s = Math.sqrt(tr + 1.0) * 2; // S=4*qw
			orientation[3] = 0.25 * s;
			orientation[0] = (m21 - m12) / s;
			orientation[1] = (m02 - m20) / s;
			orientation[2] = (m10 - m01) / s;
		} else if ((m00 > m11) & (m00 > m22)) {
			double s = Math.sqrt(1.0 + m00 - m11 - m22) * 2; // S=4*qx 
			orientation[3] = (m21 - m12) / s;
			orientation[0] = 0.25 * s;
			orientation[1] = (m01 + m10) / s;
			orientation[2] = (m02 + m20) / s;
		} else if (m11 > m22) { 
			double s = Math.sqrt(1.0 + m11 - m00 - m22) * 2; // S=4*qy
			orientation[3] = (m02 - m20) / s;
			orientation[0] = (m01 + m10) / s;
			orientation[1] = 0.25 * s;
			orientation[2] = (m12 + m21) / s;
		} else { 
			double s = Math.sqrt(1.0 + m22 - m00 - m11) * 2; // S=4*qz
			orientation[3] = (m10 - m01) / s;
			orientation[0] = (m02 + m20) / s;
			orientation[1] = (m12 + m21) / s;
			orientation[2] = 0.25 * s;
		}
	}

	@Override
	public boolean update() {
		
		if (zSpace == null) {
			return false;
		}

		// set screen dimensions
		RootPanel rootPanel = RootPanel.get();
		setScreenHalfDimensions(rootPanel.getOffsetWidth() / 2.0,
				rootPanel.getOffsetHeight() / 2.0);

		// set panel dimensions
		DockPanelW panel = (DockPanelW) view3D.getApplication().getGuiManager()
				.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN3D);
		setPanel(panel.getWidth(), panel.getHeight(), panel.getAbsoluteLeft(),
				panel.getAbsoluteTop());

		// update eyes
		eyeSeparation = EYE_SEP_HALF * 2 * toPixelRatio;

		JsArrayNumber pose = zSpace.getViewportSpaceHeadPose();

		double x = pose.get(12);
		double y = pose.get(13);
		double z = pose.get(14);
		double dx = EYE_SEP_HALF * pose.get(0);
		double dy = EYE_SEP_HALF * pose.get(1);
		double dz = EYE_SEP_HALF * pose.get(2);
		inputGlassesPosition[0][0] = (x - dx) * toPixelRatio;
		inputGlassesPosition[0][1] = (y - dy) * toPixelRatio;
		inputGlassesPosition[0][2] = (z - dz) * toPixelRatio;
		inputGlassesPosition[1][0] = (x + dx) * toPixelRatio;
		inputGlassesPosition[1][1] = (y + dy) * toPixelRatio;
		inputGlassesPosition[1][2] = (z + dz) * toPixelRatio;

		updateHeadTracking();

		// update stylus buttons
		JsArrayNumber buttons = zSpace.getButtonPressed();

		isLeftPressed = buttons.get(0) > 0.4 ? true : false;
		isRightPressed = buttons.get(1) > 0.4 ? true : false;
		isThirdButtonPressed = buttons.get(2) > 0.4 ? true : false;

		// Log.debug(isLeftPressed + "," + isRightPressed + ","
		// + isThirdButtonPressed);

		// update stylus position

		pose = zSpace.getViewportSpaceStylusPose();

		// String s = "\npose\n";
		// for (int i = 0; i < 16; i++) {
		// if (i % 4 == 0) {
		// s += "\n";
		// }
		// s += " " + pose.get(i);
		// }
		// Log.debug(s);
		
		// along x/z values seems to need to be reversed
		updateStylus(toPixelRatio, -pose.get(0), -pose.get(1), -pose.get(2),
				pose.get(4), pose.get(5), pose.get(6), -pose.get(8),
				-pose.get(9), -pose.get(10), pose.get(12), pose.get(13),
				pose.get(14),
				inputPosition, inputDirection, inputOrientation);

		updateOnScreenPosition();
		
		// Log.debug("hasStylusNotIntersectingPhysicalScreen = "
		// + hasStylusNotIntersectingPhysicalScreen);
		// if (!hasStylusNotIntersectingPhysicalScreen) {
		// MouseRobot.dispatchMouseMoveEvent(getOnScreenX(), getOnScreenY());
		// }

		updateMousePosition();

		updateMouse3DEvent();
		handleButtons();

		return true;
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.PEN;
	}

	@Override
	public double[] getInputPosition() {
		return inputPosition;
	}

	@Override
	public double[] getInputOrientation() {
		return inputOrientation;
	}

	@Override
	public double[] getGlassesPosition(int i) {
		return inputGlassesPosition[i];
	}

	@Override
	public double getEyeSeparation() {
		return eyeSeparation;
	}

	@Override
	public boolean isRightPressed() {
		return isRightPressed;
	}

	@Override
	public boolean isLeftPressed() {
		return isLeftPressed;
	}

	@Override
	public boolean isThirdButtonPressed() {
		return isThirdButtonPressed;
	}

	@Override
	public boolean isButtonPressed() {
		return isRightPressed() || isLeftPressed() || isThirdButtonPressed();
	}

	@Override
	public boolean useMouseRobot() {
		return false;
	}

	@Override
	public boolean hasMouse(EuclidianView3D view3d, Coords mouse3dPosition) {
		return hasMouse(view3d);
	}

	@Override
	public boolean hasMouse(EuclidianView3D view3d) {
		if (hasStylusNotIntersectingPhysicalScreen) {
			return false;
		}
		if (onScreenX < panelX) {
			return false;
		}
		if (onScreenX > panelX + panelWidth) {
			return false;
		}
		if (onScreenY < panelY) {
			return false;
		}
		if (onScreenY > panelY + panelHeight) {
			return false;
		}
		return true;
	}

	@Override
	public boolean currentlyUseMouse2D() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setHasCompletedGrabbingDelay(boolean flag) {
		// not used
	}

	@Override
	public boolean hasCompletedGrabbingDelay() {
		return false;
	}

	@Override
	public void setPositionXYOnPanel(double[] absolutePos, Coords panelPos,
			double screenHalfWidth, double screenHalfHeight, int panelPositionX,
			int panelPositionY, int panelDimW, int panelDimH) {
		// transform has been done before
		panelPos.setX(absolutePos[0]);
		panelPos.setY(absolutePos[1]);
		panelPos.setZ(absolutePos[2]);

	}

	@Override
	public boolean hasMouseDirection() {
		return true;
	}

	@Override
	public double[] getInputDirection() {
		return inputDirection;
	}

	@Override
	public double getDefaultRotationXOY() {
		return 30;
	}

	@Override
	public void setSpecificSettings(EuclidianSettings3D settings) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isStereoBuffered() {
		return true;
	}

	@Override
	public boolean useCompletingDelay() {
		return false;
	}

	@Override
	public boolean useQuaternionsForRotate() {
		return false;
	}

	@Override
	public boolean wantsStereo() {
		return true;
	}

	@Override
	public double getDefaultRotationOz() {
		return 270;
	}

	@Override
	public boolean shouldStoreStereoToXML() {
		return false;
	}

	@Override
	public boolean needsGrayBackground() {
		return true;
	}

	@Override
	public boolean useHeadTracking() {
		return true;
	}

	@Override
	public boolean useHandGrabbing() {
		return false;
	}

	@Override
	public OutOfField getOutOfField() {
		return OutOfField.NEVER;
	}

	@Override
	public void exit() {
		// not used here
	}

	@Override
	public void setPositionOnScreen() {
		hasStylusNotIntersectingPhysicalScreen = false;
	}

	@Override
	public void setPositionOffScreen() {
		hasStylusNotIntersectingPhysicalScreen = true;
	}

	@Override
	public boolean isZSpace() {
		return true;
	}
	
	@Override
	public boolean useOnlyProjectionGlasses() {
		return true;
	}
}
