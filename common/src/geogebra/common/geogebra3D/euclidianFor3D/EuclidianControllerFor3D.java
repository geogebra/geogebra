package geogebra.common.geogebra3D.euclidianFor3D;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianControllerCreator;
import geogebra.common.main.App;

/**
 * class for Euclidian Controller used in ggb3D
 * @author matthieu
 *
 */
public abstract class EuclidianControllerFor3D extends EuclidianController {

	/**
	 * constructor
	 * @param kernel kernel
	 */
	public EuclidianControllerFor3D(App app) {
		super(app);
	}
	
	
	
	@Override
	protected EuclidianControllerCreator newCreator(){
		return new EuclidianControllerCreatorFor3D(this);
	}
	


	
	
}
