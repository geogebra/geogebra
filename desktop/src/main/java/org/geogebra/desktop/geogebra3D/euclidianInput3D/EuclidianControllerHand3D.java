package org.geogebra.desktop.geogebra3D.euclidianInput3D;

import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.kernel.Kernel;

/**
 * Controller with hand as 3D input
 * 
 * @author mathieu
 *
 */
public class EuclidianControllerHand3D extends EuclidianControllerInput3D {

	/*
	 * private float inputPosition2DX, inputPosition2DY, inputPotition2DFactor,
	 * inputPosition2DOldX, inputPosition2DOldY;
	 */

	/**
	 * constructor
	 * 
	 * @param kernel
	 *            kernel
	 * @param input3d
	 *            3D input
	 */
	public EuclidianControllerHand3D(Kernel kernel, Input3D input3d) {
		super(kernel, input3d);

		// inputPosition2DOldX = Float.NaN;

	}

	/*
	 * @Override public void updateInput3D() { if (input3D.update()) {
	 * 
	 * // //////////////////// // set values
	 * 
	 * // update panel values panelDimension = ((EuclidianView3DD)
	 * view3D).getJPanel().getSize(); panelPosition = ((EuclidianView3DD)
	 * view3D).getJPanel() .getLocationOnScreen();
	 * 
	 * 
	 * 
	 * // input position inputPosition = input3D.getMouse3DPosition();
	 * inputPosition2DX = input3D.getMouse2DX(); inputPosition2DY =
	 * input3D.getMouse2DY(); inputPotition2DFactor =
	 * input3D.getMouse2DFactor();
	 * 
	 * 
	 * // 2D cursor pos if (robot != null) { if
	 * (Float.isNaN(inputPosition2DOldX)){ Point p =
	 * MouseInfo.getPointerInfo().getLocation(); robotX = p.x; robotY = p.y;
	 * inputPosition2DOldX = inputPosition2DX; inputPosition2DOldY =
	 * inputPosition2DY; }else{ int x, y; if (inputPotition2DFactor > 1f){
	 * App.debug(((int) inputPosition2DX)+","+((int) inputPosition2DY)); x =
	 * (int) (inputPosition2DX * screenHalfWidth / 320); y = (int)
	 * (inputPosition2DY * screenHalfHeight / 240); if (x > screenHalfWidth *
	 * 2){ x = (int) (screenHalfWidth * 2); } if (y > screenHalfHeight * 2){ y =
	 * (int) (screenHalfHeight * 2); } App.error("ici"); }else{ int dx = (int)
	 * ((inputPosition2DX - inputPosition2DOldX) * inputPotition2DFactor); int
	 * dy = (int) ((inputPosition2DY - inputPosition2DOldY) *
	 * inputPotition2DFactor); x = robotX + dx; y = robotY + dy; } if (x >= 0 &&
	 * x <= screenHalfWidth * 2) { if (y >= 0 && y <= screenHalfHeight * 2) { //
	 * process mouse if (robotX != x || robotY != y) { //
	 * App.debug(inputPosition[0]+","+inputPosition[1]+","+inputPosition[2]);
	 * //App.debug(x+","+y); robotX = x; robotY = y; robot.mouseMove(robotX,
	 * robotY); inputPosition2DOldX = inputPosition2DX; inputPosition2DOldY =
	 * inputPosition2DY; } } }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 * }
	 */

}
