package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.web.gui.layout.DockPanelW;

import com.google.gwt.core.client.JsArrayNumber;

public class InputZSpace3DW extends Input3D {

	private ZSpaceGwt zSpace;

	private static double EYE_SEP_HALF = 0.035;

	private double toPixelRatio = 3600;

	private double[] inputPosition, inputDirection, inputOrientation;

	public InputZSpace3DW() {
		super();
		inputPosition = new double[3];
		inputDirection = new double[3];
		inputOrientation = new double[4];
	}

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
			double S = Math.sqrt(tr+1.0) * 2; // S=4*qw 
			orientation[3] = 0.25 * S;
			orientation[0] = (m21 - m12) / S;
			orientation[1] = (m02 - m20) / S;
			orientation[2] = (m10 - m01) / S;
		} else if ((m00 > m11)&(m00 > m22)) { 
			double S = Math.sqrt(1.0 + m00 - m11 - m22) * 2; // S=4*qx 
			orientation[3] = (m21 - m12) / S;
			orientation[0] = 0.25 * S;
			orientation[1] = (m01 + m10) / S;
			orientation[2] = (m02 + m20) / S;
		} else if (m11 > m22) { 
			double S = Math.sqrt(1.0 + m11 - m00 - m22) * 2; // S=4*qy
			orientation[3] = (m02 - m20) / S;
			orientation[0] = (m01 + m10) / S;
			orientation[1] = 0.25 * S;
			orientation[2] = (m12 + m21) / S;
		} else { 
			double S = Math.sqrt(1.0 + m22 - m00 - m11) * 2; // S=4*qz
			orientation[3] = (m10 - m01) / S;
			orientation[0] = (m02 + m20) / S;
			orientation[1] = (m12 + m21) / S;
			orientation[2] = 0.25 * S;
		}
	}

	@Override
	public boolean update() {

		// set panel dimensions
		DockPanelW panel = (DockPanelW) view3D.getApplication().getGuiManager()
				.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN3D);
		setPanel(panel.getWidth(), panel.getHeight(), panel.getAbsoluteLeft(),
				panel.getAbsoluteTop());

		// update eyes
		JsArrayNumber pose = zSpace.getViewportSpaceHeadPose();

		double x = pose.get(12);
		double y = pose.get(13);
		double z = pose.get(14);
		double dx = EYE_SEP_HALF * pose.get(0);
		double dy = EYE_SEP_HALF * pose.get(1);
		double dz = EYE_SEP_HALF * pose.get(2);
		glassesPosition[0].set((x - dx) * toPixelRatio, (y - dy) * toPixelRatio,
				(z - dz) * toPixelRatio);
		glassesPosition[1].set((x + dx) * toPixelRatio, (y + dy) * toPixelRatio,
				(z + dz) * toPixelRatio);

		updateHeadTracking();

		// update stylus

		pose = zSpace.getViewportSpaceStylusPose();

		String s = "\npose\n";
		for (int i = 0; i < 16; i++) {
			if (i % 4 == 0) {
				s += "\n";
			}
			s += " " + pose.get(i);
		}
		Log.debug(s);
		
		updateStylus(toPixelRatio, pose.get(0), pose.get(1), pose.get(2),
				pose.get(4), pose.get(5), pose.get(6), pose.get(8), pose.get(9),
				pose.get(10), pose.get(12), pose.get(13), pose.get(14),
				inputPosition, inputDirection, inputOrientation);

		updateMousePosition();

		updateMouse3DEvent();
		// handleButtons();

		Log.debug("\nmouse pos:\n" + getMouse3DPosition() + "\ndir:\n"
				+ getMouse3DDirection());

		return false;
	}

	@Override
	protected void setGlassesPosition() {
		// transformation has been already done
	}


	@Override
	public DeviceType getDeviceType() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getEyeSeparation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isRightPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLeftPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isThirdButtonPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isButtonPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useInputDepthForHitting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useMouseRobot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasMouse(EuclidianView3D view3d, Coords mouse3dPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasMouse(EuclidianView3D view3d) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean currentlyUseMouse2D() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setHasCompletedGrabbingDelay(boolean flag) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasCompletedGrabbingDelay() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setSpecificSettings(EuclidianSettings3D settings) {
		// TODO Auto-generated method stub

	}

	public boolean useScreenZOffset() {
		return false;
	}

	public boolean isStereoBuffered() {
		return true;
	}

	public boolean useInterlacedPolarization() {
		return false;
	}

	public boolean useCompletingDelay() {
		return false;
	}


	public boolean useQuaternionsForRotate() {
		return false;
	}

	public boolean wantsStereo() {
		return true;
	}

	public double getDefaultRotationOz() {
		return 270;
	}


	public boolean shouldStoreStereoToXML() {
		return false;
	}

	public boolean needsGrayBackground() {
		return true;
	}

	public boolean useHeadTracking() {
		return true;
	}

	public boolean useHandGrabbing() {
		return false;
	}

	public OutOfField getOutOfField() {
		return OutOfField.NEVER;
	}

	public void exit() {
		// not used here
	}

	public void setPositionOnScreen() {
		hasStylusNotIntersectingPhysicalScreen = false;
	}

	public void setPositionOffScreen() {
		hasStylusNotIntersectingPhysicalScreen = true;
	}

	private boolean hasStylusNotIntersectingPhysicalScreen = true,
			stylusDetected = false;

	public boolean isZSpace() {
		return true;
	}
}
