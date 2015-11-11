package org.geogebra.desktop.geogebra3D.input3D.leonar3do;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;


/**
 * controller with specific methods from leonar3do input system
 * @author mathieu
 *
 */
public class InputLeo3D implements Input3D {

	
	private LeoSocket leoSocket;
	

	private double[] mousePosition;
	
	private double[] mouseOrientation;
	
	private boolean isRightPressed, isLeftPressed;
	
	private double screenHalfWidth;
	
	
	private double[][] glassesPosition;
	
	private double eyeSeparation;
	
	/**
	 * constructor
	 */
	public InputLeo3D() {
		
		// 3D mouse position
		mousePosition = new double[3];
		
		// 3D mouse orientation
		mouseOrientation = new double[4];
		
		// glasses position
		glassesPosition = new double[2][];
		for (int i = 0 ; i < 2 ; i++){
			glassesPosition[i] = new double[3];
		}
		
		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenHalfWidth = gd.getDisplayMode().getWidth()/2;
		//screenHalfHeight = gd.getDisplayMode().getHeight()/2;		
		//App.debug("screen:"+screenWidth+"x"+screenHeight);
		
		//App.error("height/2="+gd.getDisplayMode().getHeight()/2);
		
		
		leoSocket = new LeoSocket();
	}
	
	
	public boolean update(GPoint panelPosition, GDimension panelDimension) {
	
		boolean updateOccured = false;
		
		// update from last message
		if (leoSocket.gotMessage){
			
			// mouse position
			mousePosition[0] = leoSocket.birdX * screenHalfWidth;
			mousePosition[1] = leoSocket.birdY * screenHalfWidth;
			mousePosition[2] = leoSocket.birdZ * screenHalfWidth;
			
			/*
			App.debug("\norientation"
			+"\nx="+leoSocket.birdOrientationX
			+"\ny="+leoSocket.birdOrientationY
			+"\nz="+leoSocket.birdOrientationZ
			+"\nw="+leoSocket.birdOrientationW
			+"\nagnle="+(2*Math.acos(leoSocket.birdOrientationW)*180/Math.PI)+"");
			*/
			
			// mouse position
			mouseOrientation[0] = leoSocket.birdOrientationX;
			mouseOrientation[1] = leoSocket.birdOrientationY;
			mouseOrientation[2] = leoSocket.birdOrientationZ;
			mouseOrientation[3] = leoSocket.birdOrientationW;

			
			// right button
			isRightPressed = (leoSocket.bigButton > 0.5);
			
			// left button
			isLeftPressed = (leoSocket.smallButton > 0.5);
			
		
			
			
			
			// eye separation
			eyeSeparation = (leoSocket.leftEyeX - leoSocket.rightEyeX) * screenHalfWidth;

			// glasses position
			glassesPosition[0][0] = leoSocket.leftEyeX * screenHalfWidth;
			glassesPosition[0][1] = leoSocket.leftEyeY * screenHalfWidth;
			glassesPosition[0][2] = leoSocket.leftEyeZ * screenHalfWidth;
			glassesPosition[1][0] = leoSocket.rightEyeX * screenHalfWidth;
			glassesPosition[1][1] = leoSocket.rightEyeY * screenHalfWidth;
			glassesPosition[1][2] = leoSocket.rightEyeZ * screenHalfWidth;

			/*
			App.debug("\nleft eye"
					+"\nx="+leftEyePosition[0]
					+"\ny="+leftEyePosition[1]
					+"\nz="+leftEyePosition[2]
				    +
					"\nright eye"
					+"\nx="+rightEyePosition[0]
					+"\ny="+rightEyePosition[1]
					+"\nz="+rightEyePosition[2]);
					
			App.debug("\nleft-right="+(rightEyePosition[0]-leftEyePosition[0])+"\nheight="+GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight());
			*/
			
			/*
			App.debug("\nbuttons"
					+"\nbig = "+leoSocket.bigButton
					+"\nright = "+isRightPressed
					);
					*/
			
			updateOccured = true;
		}
		
		// request next message
		leoSocket.getLeoData();		
		
		return updateOccured;
		
	}
	
	public double[] getMouse3DPosition(){
		return mousePosition;
	}
	
	public double[] getMouse3DOrientation(){
		return mouseOrientation;
	}
	
	public boolean isRightPressed(){
		return isRightPressed;
	}
	
	public boolean isLeftPressed(){
		return isLeftPressed;
	}

	public boolean isThirdButtonPressed() {
		return false;
	}

	public double[] getGlassesPosition(int i){
		return glassesPosition[i];
	}
	
	public double getEyeSeparation(){
		return eyeSeparation;
	}
	
	public boolean useInputDepthForHitting(){
		return true;
	}
	
	public boolean useMouseRobot(){
		return true;
	}
	
//	@Override
//	public float getMouse2DX(){
//		return 0f;
//	}
//	
//	@Override
//	public float getMouse2DY(){
//		return 0f;
//	}
//	
//	@Override
//	public float getMouse2DFactor(){
//		return 1f;
//	}
	
	public DeviceType getDeviceType(){
		return DeviceType.PEN;
	}
	
	public boolean hasMouse(EuclidianView3D view3D, Coords mouse3DPosition){
		return view3D.hasMouse2D();
	}

	public boolean hasMouse(EuclidianView3D view3D) {
		return view3D.hasMouse2D();
	}

	public boolean currentlyUseMouse2D(){
		return false;
	}
	
	public void setHasCompletedGrabbingDelay(boolean flag) {
		// not used for leo
	}
	
	public boolean hasCompletedGrabbingDelay(){
		return false;
	}

	public void setPositionXYOnPanel(double[] absolutePos, Coords panelPos, 
			double screenHalfWidth, double screenHalfHeight, int panelPositionX, int panelPositionY,
			int panelDimW, int panelDimH) {
		panelPos.setX(absolutePos[0] + screenHalfWidth - panelPositionX
				- panelDimW / 2);
		panelPos.setY(absolutePos[1] - screenHalfHeight + panelPositionY
				+ panelDimH / 2);				

	}
	
	public boolean useScreenZOffset(){
		return true;
	}
	
	public boolean isStereoBuffered() {
		return false;
	}
	
	public boolean useInterlacedPolarization(){
		return true;
	}
	
	public boolean useCompletingDelay(){
		return false;
	}
	
	public boolean hasMouseDirection() {
		return false;
	}

	public double[] getMouse3DDirection() {
		return null;
	}

	public boolean useQuaternionsForRotate() {
		return true;
	}

	public boolean wantsStereo() {
		return true;
	}

	public double getDefaultRotationOz() {
		return EuclidianView3D.ANGLE_ROT_OZ;
	}

	public double getDefaultRotationXOY() {
		return EuclidianView3D.ANGLE_ROT_XOY;
	}

	public boolean shouldStoreStereoToXML() {
		return false;
	}

	public boolean needsGrayBackground() {
		return false;
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
		// nothing to do
	}

	public void setPositionOffScreen() {
		// nothing to do
	}

}
