package geogebra3D.euclidian3D;


import geogebra.common.euclidian.Previewable;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoVector3D;

import java.util.ArrayList;

/**
 * Class for drawing vectors
 * @author matthieu
 *
 */
public class DrawVector3D extends Drawable3DCurves
implements Previewable {

	
	/**
	 * Common constructor
	 * @param view3D
	 * @param vector
	 */
	public DrawVector3D(EuclidianView3D view3D, GeoVectorND vector)
	{
		
		super(view3D, (GeoElement) vector);
	}
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}


	
	protected boolean updateForItSelf(){

		//updateColors();
		
		GeoVectorND geo = ((GeoVectorND) getGeoElement());
		
		geo.updateStartPointPosition();

		Renderer renderer = getView3D().getRenderer();

		
		Coords p1;
		if (geo.getStartPoint()==null){
			p1 = new Coords(4);
			p1.setW(1);
		}else
			p1 = geo.getStartPoint().getInhomCoordsInD(3);
		Coords p2 = (Coords) p1.add(geo.getCoordsInD(3));
		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);
		brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());
		
		brush.start(8);
		brush.setAffineTexture(0.5f, 0.125f);
		brush.segment(p1,p2);
		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
		setGeometryIndex(brush.end());
		
		return true;
	}
	
	protected void updateForView(){
		if (getView3D().viewChangedByZoom())
			updateForItSelf();
	}
	
	
	
	
	
	public int getPickOrder() {		
		return DRAW_PICK_ORDER_1D;
	}

	
	
	

	
	////////////////////////////////
	// Previewable interface 
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList selectedPoints;

	/**
	 * constructor for previewable
	 * @param view3D
	 * @param selectedPoints
	 * @param cs1D
	 */
	@SuppressWarnings("unchecked")
	public DrawVector3D(EuclidianView3D view3D, ArrayList selectedPoints){
		
		super(view3D);
		
		GeoVector3D v = new GeoVector3D(view3D.getKernel().getConstruction());
		setGeoElement(v);
		v.setIsPickable(false);
		setGeoElement(v);
		
		this.selectedPoints = selectedPoints;
		
		updatePreview();
		
	}	

	




	public void updateMousePos(double xRW, double yRW) {	
		
	}


	public void updatePreview() {
		
		GeoPointND firstPoint = null;
		GeoPointND secondPoint = null;
		if (selectedPoints.size()>=1){
			firstPoint = (GeoPointND) selectedPoints.get(0);
			if (selectedPoints.size()==2)
				secondPoint = (GeoPointND) selectedPoints.get(1);
			else
				secondPoint = getView3D().getCursor3D();
		}
			
		
		if (selectedPoints.size()>=1){
			((GeoVector3D) getGeoElement()).setCoords(
					secondPoint.getInhomCoordsInD(3).sub(firstPoint.getInhomCoordsInD(3)).get());
			try {
				((GeoVector3D) getGeoElement()).setStartPoint(firstPoint);
			} catch (CircularDefinitionException e) {
				e.printStackTrace();
			}
			getGeoElement().setEuclidianVisible(true);
			setWaitForUpdate();
		}else{
			getGeoElement().setEuclidianVisible(false);
		}
		
			
	}
		
		
		

}
