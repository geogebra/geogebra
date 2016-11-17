package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings3D;

public class InputZSpace3DW extends Input3D {

	@Override
	public DeviceType getDeviceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getMouse3DPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getMouse3DOrientation() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub

	}

	@Override
	public boolean useScreenZOffset() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStereoBuffered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useInterlacedPolarization() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useCompletingDelay() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasMouseDirection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double[] getMouse3DDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useQuaternionsForRotate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean wantsStereo() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getDefaultRotationOz() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDefaultRotationXOY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean shouldStoreStereoToXML() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean needsGrayBackground() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useHeadTracking() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useHandGrabbing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OutOfField getOutOfField() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPositionOnScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPositionOffScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isZSpace() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSpecificSettings(EuclidianSettings3D settings) {
		// TODO Auto-generated method stub

	}

}
