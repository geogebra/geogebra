package geogebra3D.euclidian3D;




import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra3D.kernel3D.GeoSegment3D;

import java.util.ArrayList;



/**
 * Class for drawing segments
 * @author matthieu
 *
 */
public class DrawSegment3D extends DrawCoordSys1D {

	
	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param segment
	 */
	public DrawSegment3D(EuclidianView3D a_view3D, GeoSegmentND segment){
		
		super(a_view3D,(GeoElement) segment);
		
		setDrawMinMax(0, 1);
	}

	
	@Override
	public boolean doHighlighting(){
		
		//if the segments depends on a polygon (or polyhedron), look at the poly' highlighting
		GeoElement meta = ((GeoSegmentND) getGeoElement()).getMetas()[0];		
		if (meta!=null && meta.doHighlighting())
			return true;
		
		return super.doHighlighting();
	}
	
	@Override
	protected void updateForView(){
		if (getView3D().viewChangedByZoom())
			updateForItSelf();
	}

	
	
	////////////////////////////////
	// Previewable interface 
	
	
	/**
	 * Constructor for previewable
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawSegment3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoSegment3D(a_view3D.getKernel().getConstruction()));
		
		setDrawMinMax(0, 1);
		
	}	

}
