package org.geogebra.desktop.geogebra3D.input3D.zspace;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DException;


/**
 * controller with specific methods from leonar3do input system
 * @author mathieu
 *
 */
public class InputZSpace3D extends Input3D {

	
	private Socket socket;
	

	private double[] mousePosition;
	
	private double[] mouseDirection;

	private double[] mouseOrientation;
	
	private boolean isRightPressed, isLeftPressed, isThirdButtonPressed;
	
	
	
	private double[][] socketGlassesPosition;
	
	private double eyeSeparation;
	
	public static void initZSpace() throws Input3DException {
		Socket.initZSpace();
	}

	/**
	 * constructor
	 * 
	 * @throws Input3DException
	 */
	public InputZSpace3D() throws Input3DException {
		
		// 3D mouse position
		mousePosition = new double[3];
		
		// 3D mouse direction
		mouseDirection = new double[3];

		// 3D mouse orientation
		mouseOrientation = new double[4];


		// glasses position
		socketGlassesPosition = new double[2][];
		for (int i = 0 ; i < 2 ; i++){
			socketGlassesPosition[i] = new double[3];
		}
		
		
		
		socket = new Socket();
	}
	
	
	@Override
	public boolean update() {

		// set view port and check if changed
		boolean viewPortChanged = socket.setViewPort(panelWidth, panelHeight,
				panelX, panelY);
		
		// check if new message
		if (socket.getData() || viewPortChanged){
			
			// stylus detected
			stylusDetected = socket.stylusDetected;

			// mouse position
			mousePosition[0] = socket.stylusX;
			mousePosition[1] = socket.stylusY;
			mousePosition[2] = socket.stylusZ;
			
			// mouse direction
			mouseDirection[0] = socket.stylusDX;
			mouseDirection[1] = socket.stylusDY;
			mouseDirection[2] = socket.stylusDZ;
			
			
			// mouse position
			mouseOrientation[0] = socket.stylusOrientationX;
			mouseOrientation[1] = socket.stylusOrientationY;
			mouseOrientation[2] = socket.stylusOrientationZ;
			mouseOrientation[3] = socket.stylusOrientationW;

			
			// third button
			isThirdButtonPressed = socket.button3;

			// right button
			isRightPressed = socket.buttonRight;
			
			// left button
			isLeftPressed = socket.buttonLeft;
			
			 
			
			
			
			// eye separation
			eyeSeparation = socket.getEyeSeparation();//(socket.leftEyeX - socket.rightEyeX) * screenHalfWidth;

			// glasses position
			socketGlassesPosition[0][0] = socket.leftEyeX;// socket.leftEyeX *
															// screenHalfWidth +
															// eyeSeparation/2;
			socketGlassesPosition[0][1] = socket.leftEyeY;// socket.leftEyeY *
															// screenHalfWidth;
			socketGlassesPosition[0][2] = socket.leftEyeZ;// socket.leftEyeZ *
															// screenHalfWidth;
			socketGlassesPosition[1][0] = socket.rightEyeX;// socket.leftEyeX *
															// screenHalfWidth +
															// eyeSeparation/2;
			socketGlassesPosition[1][1] = socket.rightEyeY;// socket.leftEyeY *
															// screenHalfWidth;
			socketGlassesPosition[1][2] = socket.rightEyeZ;// socket.leftEyeZ *
															// screenHalfWidth;

			
			return true;
			
		}
		
			
		
		return false;
		
	}
	
	@Override
	public double[] getInputPosition(){
		return mousePosition;
	}
	
	@Override
	public double[] getInputOrientation(){
		return mouseOrientation;
	}
	
	@Override
	public boolean isRightPressed(){
		return isRightPressed;
	}
	
	@Override
	public boolean isLeftPressed(){
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
	public double[] getGlassesPosition(int i){
		return socketGlassesPosition[i];
	}
	
	@Override
	public double getEyeSeparation(){
		return eyeSeparation;
	}
	
	@Override
	public boolean useMouseRobot(){
		return true;
	}
	
	
	@Override
	public DeviceType getDeviceType(){
		return DeviceType.PEN;
	}
	
	@Override
	public boolean hasMouse(EuclidianView3D view3D, Coords mouse3DPosition){
		return view3D.hasMouse2D();
	}

	@Override
	public boolean hasMouse(EuclidianView3D view3D) {
		return view3D.hasMouse2D();
	}

	@Override
	public boolean currentlyUseMouse2D(){
		return hasStylusNotIntersectingPhysicalScreen || !stylusDetected;
	}
	
	@Override
	public void setHasCompletedGrabbingDelay(boolean flag){
		// not used
	}
	
	@Override
	public boolean hasCompletedGrabbingDelay(){
		return false;
	}

	@Override
	public void setPositionXYOnPanel(double[] absolutePos, Coords panelPos, 
			double screenHalfWidth, double screenHalfHeight, int panelPositionX, int panelPositionY,
			int panelDimW, int panelDimH) {
		// transform has been done before
		panelPos.setX(absolutePos[0]);
		panelPos.setY(absolutePos[1]);
		panelPos.setZ(absolutePos[2]);
		
	}
	
	@Override
	public boolean isStereoBuffered() {
		return true;
	}
	
	@Override
	public boolean useCompletingDelay(){
		return false;
	}
	
	@Override
	public boolean hasMouseDirection() {
		return true;
	}

	@Override
	public double[] getInputDirection() {
		return mouseDirection;
	}

	@Override
	public boolean useQuaternionsForRotate() {
		return false;
	}

	@Override
	public boolean wantsStereo() {
		return socket.wantsStereo;
	}

	@Override
	public double getDefaultRotationOz() {
		return 270;
	}

	@Override
	public double getDefaultRotationXOY() {
		return socket.getDisplayAngle();
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

	private boolean hasStylusNotIntersectingPhysicalScreen = true,
			stylusDetected = false;

	@Override
	public boolean isZSpace() {
		return true;
	}

	@Override
	public void setSpecificSettings(EuclidianSettings3D settings) {
		if (!settings.hadSettingChanged()) {
			settings.beginBatch();
			settings.setRotXYinDegrees(getDefaultRotationOz(),
					getDefaultRotationXOY());
			settings.setProjection(EuclidianView3D.PROJECTION_GLASSES);
			settings.setClippingReduction(0);
			settings.setShowClippingCube(false);
			settings.setUseClippingCube(false);
			settings.endBatch();
		}
	}
	
	@Override
	public boolean useOnlyProjectionGlasses() {
		return true;
	}
}
