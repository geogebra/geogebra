package geogebra3D.euclidian3D;


import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.kernel3D.GeoVector3D;

import java.util.ArrayList;

/**
 * Class for drawing vectors
 * @author matthieu
 *
 */
public class DrawVector3D extends DrawJoinPoints {

	
	/**
	 * Common constructor
	 * @param view3D
	 * @param vector
	 */
	public DrawVector3D(EuclidianView3D view3D, GeoVectorND vector)
	{
		
		super(view3D, (GeoElement) vector);
	}
	
	
	
	@Override
	protected void setArrowTypeBefore(PlotterBrush brush){
		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);
	}
	
	@Override
	protected void setArrowTypeAfter(PlotterBrush brush){
		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
	}
	
	@Override
	protected void updateForView(){
		if (getView3D().viewChangedByZoom())
			updateForItSelf();
	}
	
	
	
	
	
	
	

	
	////////////////////////////////
	// Previewable interface 
	
	

	/**
	 * constructor for previewable
	 * @param view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawVector3D(EuclidianView3D view3D, ArrayList selectedPoints){
		
		super(view3D, selectedPoints, new GeoVector3D(view3D.getKernel().getConstruction()));

		
	}	

	


	@Override
	protected void setPreviewableCoords(GeoPointND firstPoint, GeoPointND secondPoint){
		((GeoVector3D) getGeoElement()).setCoords(
				secondPoint.getInhomCoordsInD(3).sub(firstPoint.getInhomCoordsInD(3)).get());
		try {
			((GeoVector3D) getGeoElement()).setStartPoint(firstPoint);
		} catch (CircularDefinitionException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected Coords[] calcPoints(){
		GeoVectorND geo = ((GeoVectorND) getGeoElement());
		
		geo.updateStartPointPosition();

		Coords p1;
		if (geo.getStartPoint()==null){
			p1 = new Coords(4);
			p1.setW(1);
		}else
			p1 = geo.getStartPoint().getInhomCoordsInD(3);
		Coords p2 = p1.add(geo.getCoordsInD(3));
		
		return new Coords[] {p1, p2};
	}

		
		
		

}
