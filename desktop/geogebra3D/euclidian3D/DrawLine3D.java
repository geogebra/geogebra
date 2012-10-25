package geogebra3D.euclidian3D;

import geogebra.common.euclidian.Previewable;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.main.App;
import geogebra3D.kernel3D.GeoLine3D;

import java.util.ArrayList;

/**
 * Class for drawing lines
 * @author matthieu
 *
 */
public class DrawLine3D extends DrawCoordSys1D implements Previewable {

	
	
	/**
	 * common constructor
	 * @param a_view3D
	 * @param line
	 */
	public DrawLine3D(EuclidianView3D a_view3D, GeoLineND line){
		
		super(a_view3D, (GeoElement) line);
	}	
	
	
	
	
	protected boolean updateForItSelf(){
		

		updateForItSelf(true);
		return true;

	}
	
	/**
	 * update the drawable when the element changes
	 * @param updateDrawMinMax update min and max values
	 */
	protected void updateForItSelf(boolean updateDrawMinMax){
		

		if (updateDrawMinMax)
			updateDrawMinMax();
		
		super.updateForItSelf();

	}

	/**
	 *  update min and max values
	 * @param extendedDepth says if the depth is to be extended
	 */
	protected void updateDrawMinMax(boolean extendedDepth){
		
		GeoLineND line = (GeoLineND) getGeoElement();
		
		Coords o = getView3D().getToScreenMatrix().mul(line.getPointInD(3, 0).getInhomCoordsInSameDimension());
		Coords v = getView3D().getToScreenMatrix().mul(line.getPointInD(3, 1).getInhomCoordsInSameDimension()).sub(o);
						
		double[] minmax = 
			getView3D().getRenderer().getIntervalInFrustum(
				new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
				o, v, extendedDepth);
		
		//App.debug("\no=\n"+o+"\nv=\n"+v+"\nminmax="+minmax[0]+", "+minmax[1]);
		
		setDrawMinMax(minmax[0], minmax[1]);
	}
	
	
	/**
	 *  update min and max values
	 */
	public void updateDrawMinMax(){
		updateDrawMinMax(true);
	}
	

	protected void updateForView(){
		if (getView3D().viewChanged())
			updateForItSelf();
	}
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	

	/**
	 * constructor for previewable
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoLine3D(a_view3D.getKernel().getConstruction()));
		
		
	}	


	

}
