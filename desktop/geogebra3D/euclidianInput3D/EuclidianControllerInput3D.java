package geogebra3D.euclidianInput3D;

import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian3D.Input3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.Matrix.Quaternion;
import geogebra3D.awt.GPointWithZ;
import geogebra3D.euclidian3D.EuclidianController3DD;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidian3D.EuclidianView3DD;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;


/**
 * controller with specific methods from leonar3do input system
 * @author mathieu
 *
 */
public class EuclidianControllerInput3D extends EuclidianController3DD {

	
	private Input3D input3D;
	

	private Coords mouse3DPosition, startMouse3DPosition, glassesPosition;
	
	private Quaternion mouse3DOrientation, startMouse3DOrientation;
	private Coords rotV;
	private CoordMatrix startOrientationMatrix; 
	private CoordMatrix4x4 toSceneRotMatrix;
	
	private Coords vx;
	
	
	private boolean wasRightReleased;
	private boolean wasLeftReleased;
	
	private double screenHalfWidth, screenHalfHeight;
	private Dimension panelDimension;
	private Point panelPosition;
	
	private boolean eyeSepIsNotSet = true;
	
	/**
	 * constructor
	 * @param kernel kernel
	 * @param input3d input3d
	 */
	public EuclidianControllerInput3D(Kernel kernel, Input3D input3d) {
		super(kernel);
		
		this.input3D = input3d;
		
		// glasses position
		glassesPosition = new Coords(3);

		
		// 3D mouse position
		mouse3DPosition = new Coords(3);
		//mouse3DPosition.setW(1);		
		startMouse3DPosition = new Coords(3);
		
		
		// 3D mouse orientation
		mouse3DOrientation = new Quaternion();
		startMouse3DOrientation = new Quaternion();
		rotV = new Coords(4);
		toSceneRotMatrix = new CoordMatrix4x4();
		
		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenHalfWidth = gd.getDisplayMode().getWidth()/2;
		screenHalfHeight = gd.getDisplayMode().getHeight()/2;		
		
	}
	
	private void setPositionXYOnPanel(double[] absolutePos, Coords panelPos){
		panelPos.setX(absolutePos[0] + screenHalfWidth - panelPosition.x - panelDimension.width/2);
		panelPos.setY(absolutePos[1] - screenHalfHeight + panelPosition.y + panelDimension.height/2);
		
	}
	
	
	@Override
	public void updateInput3D(){
		if(input3D.update()){
			
			//////////////////////
			// set values
			
			// update panel values
			panelDimension = ((EuclidianView3DD) view3D).getJPanel().getSize();
			panelPosition = ((EuclidianView3DD) view3D).getJPanel().getLocationOnScreen();
	

			
			// eyes : set position only if we use glasses
			if (view3D.getProjection() == EuclidianView3D.PROJECTION_GLASSES){
				//App.debug(input3D.getGlassesPosition()[2]+"");
				if (eyeSepIsNotSet){
					view3D.setEyeSep(input3D.getEyeSeparation());
					eyeSepIsNotSet = false;
				}
				
				double[] pos = input3D.getGlassesPosition();
				setPositionXYOnPanel(pos, glassesPosition);
				glassesPosition.setZ(pos[2]);
				view3D.setProjectionPerspectiveEyeDistance(glassesPosition.getZ());
				
			}

			
			
			
			// mouse pos
			double[] pos = input3D.getMouse3DPosition();
			setPositionXYOnPanel(input3D.getMouse3DPosition(), mouse3DPosition);
			mouse3DPosition.setZ(pos[2] - view3D.getScreenZOffset());
			
			// check if the 3D mouse is on screen
			if((Math.abs(mouse3DPosition.getX()) < panelDimension.width/2) 
					&& (Math.abs(mouse3DPosition.getY()) < panelDimension.height/2)
					&& (mouse3DPosition.getZ() < view3D.getRenderer().getEyeToScreenDistance())){

				((EuclidianViewInput3D) view3D).setHasMouse(true);
				
				updateMouse3DEvent();

				// mouse orientation
				mouse3DOrientation.set(input3D.getMouse3DOrientation());


				if (input3D.isRightPressed()){ // process right press
					processRightPress();
					wasRightReleased = false;
					wasLeftReleased = true;
				}else if (input3D.isLeftPressed()){ // process left press
					if (wasLeftReleased){
						startMouse3DPosition.set(mouse3DPosition);
						wrapMousePressed(mouseEvent);
					}else{
						wrapMouseDragged(mouseEvent);
					}
					wasRightReleased = true;
					wasLeftReleased = false;				
				}else{ 
					// process button release
					if (!wasRightReleased || !wasLeftReleased){
						wrapMouseReleased(mouseEvent);
					}
					
					// process move
					wrapMouseMoved(mouseEvent);
					wasRightReleased = true;
					wasLeftReleased = true;					
				}
			
			}else{ // bird outside the view
				((EuclidianViewInput3D) view3D).setHasMouse(false);
			}
		}
		
		
	}

	
	
	/**
	 * 
	 * @return 3D mouse position
	 */
	public Coords getMouse3DPosition(){
		
		return mouse3DPosition;
	}
	
	
	private void processRightPress(){
		
		if (wasRightReleased){ // process first press : remember mouse start
			startMouse3DPosition.set(mouse3DPosition);
			
			view.rememberOrigins();
			((EuclidianViewInput3D) view).setStartPos(startMouse3DPosition);
			
			startMouse3DOrientation.set(mouse3DOrientation);
			
			startOrientationMatrix = startMouse3DOrientation.getRotMatrix();
			
			toSceneRotMatrix.set(view3D.getUndoRotationMatrix());
			
			// to-the-right screen vector in scene coords
			vx = toSceneRotMatrix.mul(Coords.VX);
			
		}else{ // process mouse drag
			
			
			// rotation			
			Quaternion rot = startMouse3DOrientation.leftDivide(mouse3DOrientation);
			
			
			// get the relative quaternion and rotation matrix in scene coords
			rotV.set(startOrientationMatrix.mul(rot.getVector()));
			rot.setVector(toSceneRotMatrix.mul(rotV));
			
			CoordMatrix rotMatrix = rot.getRotMatrix();
			

			//App.debug("\n"+rot);
			
			// rotate view vZ
			Coords vZrot = rotMatrix.getVz();
			//App.debug("\n"+vZrot);
			Coords vZ1 = (vZrot.sub(vx.mul(vZrot.dotproduct(vx)))).normalize(); // project the rotation to keep vector plane orthogonal to the screen
			Coords vZp = Coords.VZ.crossProduct(vZ1); // to get angle (vZ,vZ1)
				
			// rotate screen vx
			Coords vxRot = rotMatrix.mul(vx);
			Coords vx1 = (vxRot.sub(vZ1.mul(vxRot.dotproduct(vZ1)))).normalize(); // project in plane orthogonal to vZ1
			Coords vxp = vx.crossProduct(vx1); // to get angle (vx,vx1)

						
			// rotation around x (screen)
			double rotX = Math.asin(vxp.norm())*180/Math.PI;
			//App.debug("rotX="+rotX+", vx1.dotproduct(vx) = "+vx1.dotproduct(vx)+", vxp.dotproduct(vZ1) = "+vxp.dotproduct(vZ1));
			if (vx1.dotproduct(vx) < 0){ // check if rotX should be > 90°
				rotX = 180 - rotX;
			}
			if (vxp.dotproduct(vZ1) > 0){ // check if rotX should be negative
				rotX = -rotX;
			}
			
			// rotation around z (scene)
			double rotZ = Math.asin(vZp.norm())*180/Math.PI;
			//App.debug("rotZ="+rotZ+", vZp.dotproduct(vx) = "+vZp.dotproduct(vx)+", Coords.VZ.dotproduct(vZ1) = "+vZ1.getZ());
			if (vZ1.getZ() < 0){ // check if rotZ should be > 90°
				rotZ = 180 - rotZ;
			}
			if (vZp.dotproduct(vx)  < 0){ // check if rotZ should be negative
				rotZ = -rotZ;
			}
			
			//App.debug("rotZ="+rotZ);
			
			
			

			// set the view
			((EuclidianViewInput3D) view).setCoordSystemFromMouse3DMove(startMouse3DPosition,mouse3DPosition,rotX,rotZ);
			

			
			/*
			// USE FOR CHECK 3D MOUSE ORIENTATION
			// use file leonar3do-rotation2.ggb			
			GeoVector3D geovx = (GeoVector3D) getKernel().lookupLabel("vx");
			geovx.setCoords(toSceneRotMatrix.mul(Coords.VX).normalize());
			geovx.updateCascade();
			GeoVector3D vy = (GeoVector3D) getKernel().lookupLabel("vy");
			vy.setCoords(toSceneRotMatrix.mul(Coords.VY).normalize());
			vy.updateCascade();
			GeoVector3D vz = (GeoVector3D) getKernel().lookupLabel("vz");
			vz.setCoords(toSceneRotMatrix.mul(Coords.VZ).normalize());
			vz.updateCascade();

			
			GeoAngle a = (GeoAngle) getKernel().lookupLabel("angle");
			GeoVector3D v = (GeoVector3D) getKernel().lookupLabel("v");
			a.setValue(2*Math.acos(rot.getScalar()));
			v.setCoords(rot.getVector());
			a.updateCascade();
			v.updateCascade();
			
			GeoText text = (GeoText) getKernel().lookupLabel("text");
			text.setTextString("az = "+rotZ+"°\n"+"ax = "+rotX+"°\n"+
					"vxp.dotproduct(vZ1)="+vxp.dotproduct(vZ1)+"\nvx1.dotproduct(vx)="+vx1.dotproduct(vx)
					+"\nvZp.dotproduct(vx) = "+vZp.dotproduct(vx)
					);
			text.update();
			getKernel().notifyRepaint();
			 */
			 
			
			
		}
		
	}

	/*
	 * process 3D mouse move
	 *
	private void processMouse3DMoved() {	

		GPoint mouse3DLoc = new GPoint(panelDimension.width/2 + (int) mouse3DPosition.getX(), panelDimension.height/2 - (int) mouse3DPosition.getY());
		view3D.setHits3D(mouse3DLoc);	



		//for next mouse move process
		mouseEvent = new Mouse3DEvent(mouse3DLoc);
		mouseMoved = true;



	}
	*/
	
	private void updateMouse3DEvent(){
		
		GPointWithZ mouse3DLoc = new GPointWithZ(
				panelDimension.width/2 + (int) mouse3DPosition.getX(), 
				panelDimension.height/2 - (int) mouse3DPosition.getY(),
				(int) mouse3DPosition.getZ());
		
		mouseEvent = new Mouse3DEvent(mouse3DLoc, ((EuclidianView3DD) view3D).getJPanel());
		
	}
	

	@Override
	protected void setMouseLocation(AbstractEvent event) {
		mouseLoc = event.getPoint();
	}

	
	private Coords movedGeoPointStartCoords = new Coords(0,0,0,1);
	
	@Override
	protected void updateMovedGeoPointStartValues(Coords coords){
		movedGeoPointStartCoords.set(coords);
	}
	
	
	@Override
	protected void movePoint(boolean repaint, AbstractEvent event){
		
		
		Coords v = new Coords(4);
		v.set(mouse3DPosition.sub(startMouse3DPosition));
		view3D.toSceneCoords3D(v);
		
		
		
		movedGeoPoint.setCoords(movedGeoPointStartCoords.add(v), true);
		movedGeoPoint.updateCascade();


		if (movedGeoPoint.isGeoElement3D() && !movedGeoPoint.hasPath() && !movedGeoPoint.hasRegion()){
			//update point decorations
			view3D.updatePointDecorations((GeoPoint3D) movedGeoPoint);
		}

	}


	/**
	 * 
	 * @return true if 3D mouse has a button pressed
	 */
	public boolean isMouse3DPressed() {
		return input3D.isLeftPressed() || input3D.isRightPressed();
	}
	
	@Override
	protected GeoPoint3D createNewFreePoint(boolean complex){
		GeoPoint3D point3D = view3D.getCursor3D();	
		point3D.setPath(null);
		point3D.setRegion(null);
		
		Coords coords = view3D.getPickPoint(mouseLoc).copyVector();
		view3D.toSceneCoords3D(coords);
		point3D.setCoords(coords);
		
		return point3D;
	}
}
