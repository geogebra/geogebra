package geogebra3D.euclidianForPlane;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianControllerCreator;
import geogebra.common.main.App;

/**
 * Controler for 2D view created from a plane
 * @author matthieu
 *
 */
public abstract class EuclidianControllerForPlane extends EuclidianController {

	public EuclidianControllerForPlane(App app) {
		super(app);
	}
	
	@Override
	protected EuclidianControllerCreator newCreator(){
		return new EuclidianControllerCreatorForPlane(this);
	}
	
	
	

	


}
