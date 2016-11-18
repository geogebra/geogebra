package org.geogebra.web.geogebra3D.web.input3D;

import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.core.client.JsArrayNumber;

public class InputZSpace3DW extends Input3D {

	private ZSpaceGwt zSpace;

	private static double METERS_TO_PIXELS_FACTOR = -3491;

	public void setZSpace(ZSpaceGwt zSpace) {
		this.zSpace = zSpace;
	}

	@Override
	public boolean update() {

		JsArrayNumber matrix = zSpace.getLeftViewMatrix();
		String s = "\n(input) left\n";
		for (int i = 0; i < 16; i++) {
			if (i % 4 == 0) {
				s += "\n";
			}
			s += " " + matrix.get(i);
		}
		Log.debug(s);
		glassesPosition[0].set(
				matrix.get(12) * METERS_TO_PIXELS_FACTOR - 52.37,
				matrix.get(13) * METERS_TO_PIXELS_FACTOR + 296.76,
				matrix.get(14) * METERS_TO_PIXELS_FACTOR + 1410.8
				);

		matrix = zSpace.getRightViewMatrix();
		s = "\n(input) right\n";
		for (int i = 0; i < 16; i++) {
			if (i % 4 == 0) {
				s += "\n";
			}
			s += " " + matrix.get(i);
		}
		Log.debug(s);
		glassesPosition[1].set(matrix.get(12) * METERS_TO_PIXELS_FACTOR - 52.37,
				matrix.get(13) * METERS_TO_PIXELS_FACTOR + 296.76,
				matrix.get(14) * METERS_TO_PIXELS_FACTOR + 1410.8);

		updateHeadTracking();

		return false;
	}

	@Override
	protected void setGlassesPosition() {
		// transformation has been already done
	}

	// public void updateHeadTracking() {
	//
	// // Log.debug("\n"+glassesPosition);
	//
	// // Log.debug(input3D.getGlassesPosition()[2]+"");
	// // if (eyeSepIsNotSet){
	// view3D.setEyes(glassesPosition[0].getX(), glassesPosition[0].getY(),
	// glassesPosition[1].getX(), glassesPosition[1].getY());
	// // eyeSepIsNotSet = false;
	// // }
	//
	// view3D.setProjectionPerspectiveEyeDistance(glassesPosition[0].getZ(),
	// glassesPosition[1].getZ());
	//
	// Log.debug("\nleft: " + glassesPosition[0].getX() + ","
	// + glassesPosition[0].getY() + "," + glassesPosition[0].getZ()
	// + "\nright: " + glassesPosition[1].getX() + ","
	// + glassesPosition[1].getY() + "," + glassesPosition[1].getZ());
	//
	//
	// }


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
