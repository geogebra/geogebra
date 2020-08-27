package org.geogebra.desktop.geogebra3D.input3D.intelRealSense;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DException;
import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DExceptionType;


/**
 * controller with specific methods from realsense input system
 * 
 * @author mathieu
 *
 */
public class InputIntelRealsense3D extends Input3D {

	
	private Socket socket;
	

	private double[] mousePosition;
	
	private double[] mouseOrientation;
	
	private boolean isRightPressed, isLeftPressed;
	

	/**
	 * Check if realsense camera can be inited (check registry)
	 * 
	 * @throws Input3DException
	 *             if no camera installed
	 */
	public static void init() throws Input3DException {

		try {
			Socket.queryRegistry();
		} catch (Input3DException e) {
			throw e;
		} catch (Throwable e) {
			Log.error(e.getMessage());
			throw new Input3DException(Input3DExceptionType.UNKNOWN,
					e.getMessage());
		}
	}
	
	
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
		screenHalfWidth = gd.getDisplayMode().getWidth() / 2d;
		
		outOfField = OutOfField.YES;

		socket = new Socket(app);
	}
	
	
	@Override
	public boolean update() {
	
		boolean updateOccured = false;
		
		// update from last message
		if (socket.gotMessage){
			
			// mouse position
			// double factor = screenHalfWidth;
			double factor = panelWidth / 2d;
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
		return false;
	}

	@Override
	public boolean isButtonPressed(){
		return isRightPressed() || isLeftPressed();
	}

	@Override
	public double[] getGlassesPosition(int i){
		return null; // not implemented yet
	}
	
	@Override
	public double getEyeSeparation(){
		return 0; // not implemented yet
	}
	
	@Override
	public boolean useMouseRobot(){
		return false;
	}

	@Override
	public DeviceType getDeviceType(){
		return DeviceType.HAND;
	}
	
	private OutOfField outOfField;

	@Override
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
			case NEVER:
				break;
			case NO:
				break;
			case YES:
				break;
			default:
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

	@Override
	public boolean hasMouse(EuclidianView3D view3D) {
		return socket.hasTrackedHand();
	}

	@Override
	public boolean currentlyUseMouse2D(){
		return !socket.hasTrackedHand();
	}
	
	private boolean hasCompletedGrabbingDelay = false;

	@Override
	public void setHasCompletedGrabbingDelay(boolean flag){
		hasCompletedGrabbingDelay = flag;
	}
	
	@Override
	public boolean hasCompletedGrabbingDelay(){
		return hasCompletedGrabbingDelay;
	}
	
	@Override
	public void setPositionXYOnPanel(double[] absolutePos, Coords panelPos, 
			double screenHalfWidth, double screenHalfHeight, int panelPositionX, int panelPositionY,
			int panelDimW, int panelDimH)  {

		panelPos.setX(absolutePos[0]);// + screenHalfWidth - panelPosition.x - panelDimension.width / 2);
		panelPos.setY(absolutePos[1]);// - screenHalfHeight + panelPosition.y + panelDimension.height / 2);

	}
	
	@Override
	public boolean isStereoBuffered() {
		return false;
	}
	
	@Override
	public boolean useCompletingDelay(){
		return true;
	}
	
	@Override
	public boolean hasMouseDirection() {
		return false;
	}

	@Override
	public double[] getInputDirection() {
		return null;
	}

	@Override
	public boolean useQuaternionsForRotate() {
		return false;
	}

	@Override
	public boolean wantsStereo() {
		return false;
	}

	@Override
	public double getDefaultRotationOz() {
		return EuclidianView3D.ANGLE_ROT_OZ;
	}

	@Override
	public double getDefaultRotationXOY() {
		return EuclidianView3D.ANGLE_ROT_XOY;
	}

	@Override
	public boolean shouldStoreStereoToXML() {
		return true;
	}

	@Override
	public boolean needsGrayBackground() {
		return false;
	}

	@Override
	public boolean useHeadTracking() {
		return false;
	}

	@Override
	public boolean useHandGrabbing() {
		return true;
	}

	@Override
	public OutOfField getOutOfField() {
		return outOfField;
	}

	@Override
	public void exit() {
		// socket.exit();
	}

	@Override
	public void setPositionOnScreen() {
		// nothing to do
	}

	@Override
	public void setPositionOffScreen() {
		// nothing to do
	}

	@Override
	public boolean isZSpace() {
		return false;
	}

	@Override
	public void setSpecificSettings(EuclidianSettings3D settings) {
		// nothing to do
	}

}
