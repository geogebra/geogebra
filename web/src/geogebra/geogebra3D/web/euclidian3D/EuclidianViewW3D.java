package geogebra.geogebra3D.web.euclidian3D;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian3D.EuclidianView3DInterface;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.web.euclidian.EuclidianViewW;

/**
 * 3D view
 * @author mathieu
 *
 */
public class EuclidianViewW3D extends EuclidianViewW implements EuclidianView3DInterface{

	/**
	 * constructor
	 * @param ec euclidian controller
	 * @param settings euclidian settings
	 */
	public EuclidianViewW3D(EuclidianController ec, EuclidianSettings settings) {
	    super(ec, settings);
    }
	
	

	////////////////////////////////////////////////////////////
	// EuclidianView3DInterface
	////////////////////////////////////////////////////////////

	public void setClosestRotAnimation(Coords v) {
	    // TODO Auto-generated method stub
	    
    }

	public double getZmin() {
	    // TODO Auto-generated method stub
	    return -2;
    }

	public double getZmax() {
	    // TODO Auto-generated method stub
	    return 2;
    }

	public GeoPlane3D getxOyPlane() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public void setUseClippingCube(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	public void setShowClippingCube(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	public void setClippingReduction(int value) {
	    // TODO Auto-generated method stub
	    
    }

	public void setProjection(int projection) {
	    // TODO Auto-generated method stub
	    
    }

	public void setShowGrid(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	public void setShowPlane(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	public void setShowPlate(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	public void setScale(double val) {
	    // TODO Auto-generated method stub
	    
    }

	public void setRotXYinDegrees(double a, double b) {
	    // TODO Auto-generated method stub
	    
    }

	public void setZeroFromXML(double x, double y, double z) {
	    // TODO Auto-generated method stub
	    
    }

	public void updateMatrix() {
	    // TODO Auto-generated method stub
	    
    }

	public void setViewChanged() {
	    // TODO Auto-generated method stub
	    
    }

	public void setWaitForUpdate() {
	    // TODO Auto-generated method stub
	    
    }

	public void setYAxisVertical(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	
}
