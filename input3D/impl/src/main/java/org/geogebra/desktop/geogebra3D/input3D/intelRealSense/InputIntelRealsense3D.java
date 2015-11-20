package org.geogebra.desktop.geogebra3D.input3D.intelRealSense;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DException;


/**
 * controller with specific methods from realsense input system
 * 
 * @author mathieu
 *
 */
public class InputIntelRealsense3D implements Input3D {

	
	private Socket socket;
	

	private double[] mousePosition;
	
	private double[] mouseOrientation;
	
	private boolean isRightPressed, isLeftPressed;
	
	private double screenHalfWidth;
	
	
	
	/**
	 * constructor
	 * 
	 * 
	 * @throws Exception
	 */
	public InputIntelRealsense3D(final App app)
			throws Input3DException {
		
		// 3D mouse position
		mousePosition = new double[3];
		
		// 3D mouse orientation
		mouseOrientation = new double[4];
		
		
		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenHalfWidth = gd.getDisplayMode().getWidth()/2;
		//screenHalfHeight = gd.getDisplayMode().getHeight()/2;		
		//App.debug("screen:"+screenWidth+"x"+screenHeight);
		
		//App.error("height/2="+gd.getDisplayMode().getHeight()/2);
		
		
		outOfField = OutOfField.YES;

		socket = new Socket(app);
	}
	
	
	public boolean update(GPoint panelPosition, GDimension panelDimension) {
	
		boolean updateOccured = false;
		
		// update from last message
		if (socket.gotMessage){
			
			// mouse position
			// double factor = screenHalfWidth;
			double factor = panelDimension.getWidth() / 2;
			mousePosition[0] = socket.handX * factor;
			mousePosition[1] = socket.handY * factor;
			mousePosition[2] = socket.handZ * factor;
			
			
			// mouse position
			mouseOrientation[0] = socket.handOrientationX;
			mouseOrientation[1] = socket.handOrientationY;
			mouseOrientation[2] = socket.handOrientationZ;
			mouseOrientation[3] = socket.handOrientationW;

			
			// right button
			isRightPressed = socket.rightButton;
			
			// left button
			isLeftPressed = socket.leftButton;
			
			
			
			
			
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
		return null; // not implemented yet
	}
	
	public double getEyeSeparation(){
		return 0; // not implemented yet
	}
	
	public boolean useInputDepthForHitting(){
		return false;
	}
	
	public boolean useMouseRobot(){
		return false;
	}


	
	public DeviceType getDeviceType(){
		return DeviceType.HAND;
	}
	
	private OutOfField outOfField;

	public boolean hasMouse(EuclidianView3D view3D, Coords mouse3DPosition){
		OutOfField oof = socket.getOutOfField(); // this is camera field of view
		if (oof == OutOfField.NO) {
			// check if mouse is out and if we should keep same out of field
			switch (outOfField) {
			case RIGHT:
				if (mouse3DPosition.getX() < view3D.getRenderer().getRight()) {
					outOfField = OutOfField.NO;
				}
				break;
			case LEFT:
				if (mouse3DPosition.getX() > view3D.getRenderer().getLeft()) {
					outOfField = OutOfField.NO;
				}
				break;
			case TOP:
				if (mouse3DPosition.getY() < view3D.getRenderer().getTop()) {
					outOfField = OutOfField.NO;
				}
				break;
			case BOTTOM:
				if (mouse3DPosition.getY() > view3D.getRenderer().getBottom()) {
					outOfField = OutOfField.NO;
				}
				break;
			case FAR:
				if (mouse3DPosition.getZ() < view3D.getRenderer().getFar()) {
					outOfField = OutOfField.NO;
				}
				break;
			case NEAR:
				if (mouse3DPosition.getZ() > view3D.getRenderer().getNear()) {
					outOfField = OutOfField.NO;
				}
				break;

			}

			// now check if we are indeed in the 3D view field
			if (mouse3DPosition.getX() > view3D.getRenderer().getRight()) {
				outOfField = OutOfField.RIGHT;
			} else if (mouse3DPosition.getX() < view3D.getRenderer().getLeft()) {
				outOfField = OutOfField.LEFT;
			} else if (mouse3DPosition.getY() > view3D.getRenderer().getTop()) {
				outOfField = OutOfField.TOP;
			} else if (mouse3DPosition.getY() < view3D.getRenderer()
					.getBottom()) {
				outOfField = OutOfField.BOTTOM;
			} else if (mouse3DPosition.getZ() > view3D.getRenderer().getFar()) {
				outOfField = OutOfField.FAR;
			} else if (mouse3DPosition.getZ() < view3D.getRenderer().getNear()) {
				outOfField = OutOfField.NEAR;
			}
		} else {
			// set out of field from socket
			outOfField = oof;
		}

		// App.debug(((int) mouse3DPosition.getZ()) + ","
		// + view3D.getRenderer().getNear() + "--"
		// + view3D.getRenderer().getFar() + " , "
		// + view3D.getScreenZOffset());

		// App.debug(oof + "," + outOfField);

		return socket.hasTrackedHand();
	}

	public boolean hasMouse(EuclidianView3D view3D) {
		return socket.hasTrackedHand();
	}

	public boolean currentlyUseMouse2D(){
		return !socket.hasTrackedHand();
	}
	
	private boolean hasCompletedGrabbingDelay = false;

	public void setHasCompletedGrabbingDelay(boolean flag){
		hasCompletedGrabbingDelay = flag;
	}
	
	public boolean hasCompletedGrabbingDelay(){
		return hasCompletedGrabbingDelay;
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

	public boolean useHeadTracking() {
		return false;
	}

	public boolean useHandGrabbing() {
		return true;
	}

	public OutOfField getOutOfField() {
		return outOfField;
	}

	public void exit() {
		// socket.exit();
	}

	public void setPositionOnScreen() {
		// nothing to do
	}

	public void setPositionOffScreen() {
		// nothing to do
	}

}
