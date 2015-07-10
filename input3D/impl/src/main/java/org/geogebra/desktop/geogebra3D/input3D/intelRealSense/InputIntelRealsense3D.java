package org.geogebra.desktop.geogebra3D.input3D.intelRealSense;

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
public class InputIntelRealsense3D implements Input3D {

	
	private Socket socket;
	

	private double[] mousePosition;
	
	private double[] mouseOrientation;
	
	private boolean isRightPressed, isLeftPressed;
	
	private double screenHalfWidth;
	
	
	private double[] glassesPosition;
	
	private double eyeSeparation;
	
	/**
	 * constructor
	 */
	public InputIntelRealsense3D() {
		
		// 3D mouse position
		mousePosition = new double[3];
		
		// 3D mouse orientation
		mouseOrientation = new double[4];
		
		// glasses position
		glassesPosition = new double[3];
		
		
		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenHalfWidth = gd.getDisplayMode().getWidth()/2;
		//screenHalfHeight = gd.getDisplayMode().getHeight()/2;		
		//App.debug("screen:"+screenWidth+"x"+screenHeight);
		
		//App.error("height/2="+gd.getDisplayMode().getHeight()/2);
		
		
		socket = new Socket();
	}
	
	
	public boolean update(GPoint panelPosition, GDimension panelDimension) {
	
		boolean updateOccured = false;
		
		// update from last message
		if (socket.gotMessage){
			
			// mouse position
			// double factor = screenHalfWidth;
			double factor = panelDimension.getWidth() / 2;
			mousePosition[0] = socket.birdX * factor;
			mousePosition[1] = socket.birdY * factor;
			mousePosition[2] = socket.birdZ * factor;
			
			// App.debug(socket.birdX + "," + socket.birdY);

			/*
			App.debug("\norientation"
			+"\nx="+leoSocket.birdOrientationX
			+"\ny="+leoSocket.birdOrientationY
			+"\nz="+leoSocket.birdOrientationZ
			+"\nw="+leoSocket.birdOrientationW
			+"\nagnle="+(2*Math.acos(leoSocket.birdOrientationW)*180/Math.PI)+"");
			*/
			
			// mouse position
			mouseOrientation[0] = socket.birdOrientationX;
			mouseOrientation[1] = socket.birdOrientationY;
			mouseOrientation[2] = socket.birdOrientationZ;
			mouseOrientation[3] = socket.birdOrientationW;

			
			// right button
			isRightPressed = socket.rightButton;
			
			// left button
			isLeftPressed = socket.leftButton;
			
			
			
			// eye separation
			eyeSeparation = (socket.leftEyeX - socket.rightEyeX) * screenHalfWidth;

			// glasses position
			glassesPosition[0] = socket.leftEyeX * screenHalfWidth + eyeSeparation/2;
			glassesPosition[1] = socket.leftEyeY * screenHalfWidth;
			glassesPosition[2] = socket.leftEyeZ * screenHalfWidth;

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
		socket.getData();		
		
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
		return glassesPosition;
	}
	
	public double getEyeSeparation(){
		return eyeSeparation;
	}
	
	public boolean useInputDepthForHitting(){
		return false;
	}
	
	public boolean useMouseRobot(){
		return false;
	}


//	@Override
//	public float getMouse2DX(){
//		return socket.hand2Dx;
//	}
//	
//	@Override
//	public float getMouse2DY(){
//		return socket.hand2Dy;
//	}
//	
//	@Override
//	public float getMouse2DFactor(){
//		return socket.hand2Dfactor;
//	}
	
	public DeviceType getDeviceType(){
		return DeviceType.HAND;
	}
	
	public boolean hasMouse(EuclidianView3D view3D){
		return socket.hasTrackedHand();
	}
	
	public boolean currentlyUseMouse2D(){
		return !socket.hasTrackedHand();
	}
	
	public void setLeftButtonPressed(boolean flag){
		socket.setLeftButtonPressed(flag);
	}
	
	public boolean getLeftButton(){
		return socket.leftButton;
	}
	
	public void setPositionXYOnPanel(double[] absolutePos, Coords panelPos, 
			double screenHalfWidth, double screenHalfHeight, int panelPositionX, int panelPositionY,
			int panelDimW, int panelDimH)  {

		panelPos.setX(absolutePos[0]);// + screenHalfWidth - panelPosition.x - panelDimension.width / 2);
		panelPos.setY(absolutePos[1]);// - screenHalfHeight + panelPosition.y + panelDimension.height / 2);

	}
	
	public boolean useScreenZOffset(){
		return false;
	}
	
	public boolean isStereoBuffered() {
		return false;
	}
	
	public boolean useInterlacedPolarization(){
		return false;
	}
	
	public boolean useCompletingDelay(){
		return true;
	}
	
	public boolean hasMouseDirection() {
		return false;
	}

	public double[] getMouse3DDirection() {
		return null;
	}

	public boolean useQuaternionsForRotate() {
		return false;
	}

	public boolean wantsStereo() {
		return false;
	}

	public double getDefaultRotationOz() {
		return EuclidianView3D.ANGLE_ROT_OZ;
	}

	public double getDefaultRotationXOY() {
		return EuclidianView3D.ANGLE_ROT_XOY;
	}

	public boolean shouldStoreStereoToXML() {
		return true;
	}

	public boolean needsGrayBackground() {
		return false;
	}

}
