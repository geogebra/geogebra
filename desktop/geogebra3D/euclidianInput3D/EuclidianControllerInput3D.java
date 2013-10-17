package geogebra3D.euclidianInput3D;

import geogebra.common.euclidian3D.input3D.Input3D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.Matrix.Quaternion;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;


/**
 * controller with specific methods from leonar3do input system
 * @author mathieu
 *
 */
public class EuclidianControllerInput3D extends EuclidianController3D {

	
	private Input3D input3D;
	

	private Coords mouse3DPosition, startMouse3DPosition;
	
	private Quaternion mouse3DOrientation, startMouse3DOrientation;
	private Coords rotV;
	private CoordMatrix startOrientationMatrix;
	
	
	private boolean wasRightReleased;
	
	private double screenHalfWidth, screenHalfHeight;
	
	
	private boolean eyeSepIsNotSet = true;
	
	/**
	 * constructor
	 * @param kernel kernel
	 * @param input3d input3d
	 */
	public EuclidianControllerInput3D(Kernel kernel, Input3D input3d) {
		super(kernel);
		
		this.input3D = input3d;
		
		// 3D mouse position
		mouse3DPosition = new Coords(3);
		//mouse3DPosition.setW(1);		
		startMouse3DPosition = new Coords(3);
		
		
		// 3D mouse orientation
		mouse3DOrientation = new Quaternion();
		startMouse3DOrientation = new Quaternion();
		rotV = new Coords(4);
		
		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenHalfWidth = gd.getDisplayMode().getWidth()/2;
		screenHalfHeight = gd.getDisplayMode().getHeight()/2;		
		
	}
	
	
	@Override
	public void update(){
		if(input3D.update()){
			
			//////////////////////
			// set values
			
			// mouse pos
			Dimension d = view3D.getJPanel().getSize();
			Point p = view3D.getJPanel().getLocationOnScreen();
	
			double[] pos = input3D.getMouse3DPosition();
			
			//App.debug(""+input3D.getGlassesPosition()[0]+","+input3D.getGlassesPosition()[1]+","+input3D.getGlassesPosition()[2]);

			mouse3DPosition.setX(pos[0] + screenHalfWidth - p.x - d.width/2);
			mouse3DPosition.setY(pos[1] - screenHalfHeight + p.y + d.height/2);
			mouse3DPosition.setZ(pos[2] - ((EuclidianView3D) view).getScreenZOffset());
			
			
			// mouse orientation
			mouse3DOrientation.set(input3D.getMouse3DOrientation());
			
			
			// eyes
			//App.debug(input3D.getGlassesPosition()[2]+"");
			if (eyeSepIsNotSet){
				view3D.setEyeSep(input3D.getEyeSeparation());
				eyeSepIsNotSet = false;
			}
			view3D.setProjectionPerspectiveEyeDistance(input3D.getGlassesPosition()[2]);
			
			
			//////////////////////////////
			// process right press
			if (input3D.isRightPressed()){
				processRightPress();
				wasRightReleased = false;
			}else{
				wasRightReleased = true;
			}
			
		}
		super.update();
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
			
			startMouse3DOrientation.set(mouse3DOrientation);
			
			startOrientationMatrix = startMouse3DOrientation.getRotMatrix();
			
		}else{ // process mouse drag
			
			// translation
			Coords translation = mouse3DPosition.sub(startMouse3DPosition);
			
	
			
			// rotation			
			Quaternion rot = startMouse3DOrientation.leftDivide(mouse3DOrientation);
			
			
			// get the relative quaternion and rotation matrix in scene coords
			rotV.set(startOrientationMatrix.mul(rot.getVector()));
			((EuclidianView3D) view).toSceneCoords3D(rotV);
			rot.setVector(rotV.mul(view.getXscale()));
			
			CoordMatrix rotMatrix = rot.getRotMatrix();
			
			// to-the-right screen vector in scene coords
			Coords vx = ((EuclidianView3D) view).getToSceneMatrix().mul(Coords.VX).normalize();
			
			// rotate view vZ
			Coords vZrot = rotMatrix.getVz();
			Coords vZ1 = (vZrot.sub(vx.mul(vZrot.dotproduct(vx)))).normalize(); // project the rotation to keep vector plane orthogonal to the screen
			Coords vZp = Coords.VZ.crossProduct(vZ1); // to get angle (vZ,vZ1)
				
			// rotate screen vx
			Coords vxRot = rotMatrix.mul(vx);
			Coords vx1 = (vxRot.sub(vZ1.mul(vxRot.dotproduct(vZ1)))).normalize(); // project in plane orthogonal to vZ1
			Coords vxp = vx.crossProduct(vx1); // to get angle (vx,vx1)

						
			// rotation around x (screen)
			double rotX = Math.asin(vxp.norm())*180/Math.PI;
			if (vx1.dotproduct(vx) < 0){ // check if rotX should be > 90°
				rotX = 180 - rotX;
			}
			if (vxp.dotproduct(vZ1) > 0){ // check if rotX should be negative
				rotX = -rotX;
			}
			
			// rotation around z (scene)
			double rotZ = Math.asin(vZp.norm())*180/Math.PI;
			if (vZp.dotproduct(vx) < 0){ // check if rotZ should be negative
				rotZ = -rotZ;
			}

			
			

			// set the view
			((EuclidianViewInput3D) view).setCoordSystemFromMouse3DMove(translation.getX(),translation.getY(),translation.getZ(),rotX,rotZ);
			

			/*
			
			// USE FOR CHECK 3D MOUSE ORIENTATION
			// use file leonar3do-rotation2.ggb
			
			GeoVector3D geovx = (GeoVector3D) getKernel().lookupLabel("vx");
			geovx.setCoords(((EuclidianViewInput3D) view).getToSceneMatrix().mul(Coords.VX).normalize());
			geovx.updateCascade();
			GeoVector3D vy = (GeoVector3D) getKernel().lookupLabel("vy");
			vy.setCoords(((EuclidianViewInput3D) view).getToSceneMatrix().mul(Coords.VY).normalize());
			vy.updateCascade();
			GeoVector3D vz = (GeoVector3D) getKernel().lookupLabel("vz");
			vz.setCoords(((EuclidianViewInput3D) view).getToSceneMatrix().mul(Coords.VZ).normalize());
			vz.updateCascade();

			
			GeoAngle a = (GeoAngle) getKernel().lookupLabel("angle");
			GeoVector3D v = (GeoVector3D) getKernel().lookupLabel("v");
			a.setValue(2*Math.acos(rot.getScalar()));
			v.setCoords(rot.getVector());
			a.updateCascade();
			v.updateCascade();
			
			GeoText text = (GeoText) getKernel().lookupLabel("text");
			text.setTextString("az = "+rotZ+"°\n"+"ax = "+rotX+"°\n"+vxp.dotproduct(vZ1)+"\n"+vx1.dotproduct(vx));
			text.update();
			getKernel().notifyRepaint();

			*/
			
		}
		
	}
	
	
	
}
