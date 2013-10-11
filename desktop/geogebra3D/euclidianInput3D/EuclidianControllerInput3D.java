package geogebra3D.euclidianInput3D;

import geogebra.common.euclidian3D.input3D.Input3D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.Matrix.Quaternion;
import geogebra.common.main.App;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;


/**
 * controller with specific methods from leonar3do input system
 * @author mathieu
 *
 */
public class EuclidianControllerInput3D extends EuclidianController3D {

	
	private Input3D input3D;
	

	private Coords mouse3DPosition, startMouse3DPosition;
	
	private Quaternion mouse3DOrientation, startMouse3DOrientation;
	private Quaternion viewQ;
	private CoordMatrix startOrientationMatrix;
	
	
	private boolean wasRightReleased;
	
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
		
		
	}
	
	
	@Override
	public void update(){
		if(input3D.update()){
			mouse3DPosition.set(input3D.getMouse3DPosition());
			mouse3DOrientation.set(input3D.getMouse3DOrientation());
			
			
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
			//App.error("right press");
			
			double z = -((EuclidianView3D) view).getXRot()-90;
			double x = ((EuclidianView3D) view).getZRot()-90;
			App.debug(x+", "+z);
			viewQ = new Quaternion(x*Math.PI/180,z*Math.PI/180);
			//App.debug("\nview quat : "+viewQ);
			startMouse3DOrientation.set(mouse3DOrientation);
			App.error("\n"+startMouse3DOrientation);
			
			startOrientationMatrix = startMouse3DOrientation.getRotMatrix();
			
		}else{ // process mouse drag
			// translation
			Coords v = mouse3DPosition.sub(startMouse3DPosition);
			
			
			// rotation
			//Quaternion rot = viewQ.multiply(startMouse3DOrientation.leftDivide(mouse3DOrientation));
			
			Quaternion rot = startMouse3DOrientation.leftDivide(mouse3DOrientation);
			
			App.debug("\nmouse: "+mouse3DOrientation+"\n-- rot: "+rot);
			
			
			App.debug("\nmouse in start :\n"+startOrientationMatrix.mul(rot.getVector()));
			
			rot.setVector(startOrientationMatrix.mul(rot.getVector()));
			
			
			//App.debug("\nx="+(rot.getAngleX()*180/Math.PI)+"°\ny="+(rot.getAngleY()*180/Math.PI)+"°\nz="+(rot.getAngleZ()*180/Math.PI)+"°");
			double rotX = rot.getAngleX()*180/Math.PI;
			double rotZ = rot.getAngleZ()*180/Math.PI;
			
			App.debug("\nx="+((int) rotX)+"° z="+((int) rotZ)+"°");
			
			((EuclidianViewInput3D) view).setCoordSystemFromMouse3DMove(v.getX(),v.getY(),v.getZ(),-rotZ-90,rotX+90);
		}
		
	}
	
	
	
}
