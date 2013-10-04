package geogebra3D.euclidianInput3D;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.input3D.Input3D;


/**
 * controller with specific methods from leonar3do input system
 * @author mathieu
 *
 */
public class EuclidianControllerInput3D extends EuclidianController3D {

	
	private Input3D input3D;
	

	private Coords mouse3DPosition;
	
	/**
	 * constructor
	 * @param kernel kernel
	 * @param input3d input3d
	 */
	public EuclidianControllerInput3D(Kernel kernel, Input3D input3d) {
		super(kernel);
		
		this.input3D = input3d;
		
		mouse3DPosition = new Coords(4);
		mouse3DPosition.setW(1);
	}
	
	
	@Override
	public void update(){
		if(input3D.update()){
			mouse3DPosition.set(input3D.getMouse3DPosition());
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
}
