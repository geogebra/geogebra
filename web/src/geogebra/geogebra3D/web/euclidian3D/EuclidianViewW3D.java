package geogebra.geogebra3D.web.euclidian3D;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian3D.EuclidianView3DInterface;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.euclidian3D.openGL.RendererWebGL;
import geogebra.web.euclidian.EuclidianViewW;

import com.google.gwt.user.client.ui.Widget;

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
	
	
	
	/**
	 * @return panel component
	 */
	public Widget getComponent() {
	    return EVPanel.getAbsolutePanel();
    }
	
	

	////////////////////////////////////////////////////////////
	// EuclidianView3DInterface
	////////////////////////////////////////////////////////////

	@Override
    public void setClosestRotAnimation(Coords v) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public double getZmin() {
	    // TODO Auto-generated method stub
	    return -2;
    }

	@Override
    public double getZmax() {
	    // TODO Auto-generated method stub
	    return 2;
    }

	@Override
    public GeoPlane3D getxOyPlane() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void setUseClippingCube(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setShowClippingCube(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setClippingReduction(int value) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setProjection(int projection) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setShowGrid(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setShowPlane(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setShowPlate(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setScale(double val) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setRotXYinDegrees(double a, double b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setZeroFromXML(double x, double y, double z) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void updateMatrix() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setViewChanged() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setWaitForUpdate() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setYAxisVertical(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	
	
	
	
	
	
	////////////////////////////////////////////////////////////
	// MyEuclidianViewPanel
	////////////////////////////////////////////////////////////

	
	@Override
    protected MyEuclidianViewPanel newMyEuclidianViewPanel(){
		return new MyEuclidianViewPanel3D(this);
	}
	
	/**
	 * panel for 3D
	 * @author mathieu
	 *
	 */
	private class MyEuclidianViewPanel3D extends MyEuclidianViewPanel{

		/**
		 * constructor
		 * @param ev euclidian view
		 */
		public MyEuclidianViewPanel3D(EuclidianView ev) {
	        super(ev);
        }
		
		@Override
        protected void createCanvas(){
			RendererWebGL test = new RendererWebGL();
			canvas = test.getGLCanvas();
		}
		
	}

	
}
