package geogebra3D.euclidianForPlane;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianViewCompanion;
import geogebra.common.euclidianForPlane.EuclidianViewForPlaneInterface;
import geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import geogebra.common.kernel.kernelND.ViewCreator;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.euclidian.EuclidianStyleBarD;
import geogebra.euclidian.EuclidianViewD;

/**
 * 2D view for plane.
 * 
 * @author matthieu
 *
 */
public class EuclidianViewForPlaneD extends EuclidianViewD implements EuclidianViewForPlaneInterface {


	
	/**
	 * 
	 * @param ec controller
	 * @param plane plane creating this view
	 * @param settings euclidian settings
	 */
	public EuclidianViewForPlaneD(EuclidianController ec, ViewCreator plane, EuclidianSettings settings) {
		super(ec, new boolean[]{ false, false }, false, EVNO_GENERAL, settings); //TODO euclidian settings
		
		((EuclidianViewForPlaneCompanion) companion).initView(plane);
	}
	
	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion(){
		return new EuclidianViewForPlaneCompanion(this);
	}
	


	
	@Override
	public EuclidianViewForPlaneCompanion getCompanion(){
		return (EuclidianViewForPlaneCompanion) super.getCompanion();
	}
	


	
	
	@Override
	protected EuclidianStyleBarD newEuclidianStyleBar(){
		return new EuclidianStyleBarForPlane(this);
	}
	
	
	
}
