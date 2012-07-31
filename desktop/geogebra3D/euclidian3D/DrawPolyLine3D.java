package geogebra3D.euclidian3D;

import geogebra.common.euclidian.Previewable;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;

import java.util.ArrayList;

/**
 * Class for drawing 1D coord sys (lines, segments, ...)
 * @author matthieu
 *
 */
public class DrawPolyLine3D extends Drawable3DCurves implements Previewable {

	private double[] drawMinMax = new double[2];
	


	
	/**
	 * common constructor
	 * @param a_view3D
	 * @param cs1D
	 */
	public DrawPolyLine3D(EuclidianView3D a_view3D, GeoElement p){
		
		super(a_view3D, p);
	}	
	
	
	
	/**
	 * common constructor for previewable
	 * @param a_view3d
	 */
	public DrawPolyLine3D(EuclidianView3D a_view3d) {
		super(a_view3d);
		
	}

	
	
	/**
	 * sets the values of drawable extremities
	 * @param drawMin
	 * @param drawMax
	 */
	public void setDrawMinMax(double drawMin, double drawMax){
		this.drawMinMax[0] = drawMin;
		this.drawMinMax[1] = drawMax;
	}
	
	
	/**
	 * @return the min-max extremity
	 */
	public double[] getDrawMinMax(){
		return drawMinMax;
	}
	
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}
	
	
	
	
	
	protected boolean updateForItSelf(){
		
		//updateColors();
		
		GeoPolyLine p = (GeoPolyLine) getGeoElement();
		int num = p.getNumPoints();
		
		double[] minmax = getDrawMinMax(); 
		
		if (Math.abs(minmax[0])>1E10)
			return true;
		
		if (Math.abs(minmax[1])>1E10)
			return true;
		
		if (minmax[0]>minmax[1])
			return true;
		
		
		Renderer renderer = getView3D().getRenderer();
		


		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		
		brush.start(8);
		brush.setThickness(getLineThickness(),(float) getView3D().getScale());
		//brush.setColor(getGeoElement().getObjectColor());
		brush.setAffineTexture(
				(float) ((0.5-minmax[0])/(minmax[1]-minmax[0])),  0.25f);
		

		for (int i = 0; i<p.getNumPoints()-1; i++)
			brush.segment(p.getPointND(i).getInhomCoordsInD(3), p.getPointND(i+1).getInhomCoordsInD(3));
		
		setGeometryIndex(brush.end());
		
	
		return true;
	}

	/**
	 * update the drawable as a segment from p1 to p2
	 * @param p1
	 * @param p2
	 */
	protected void updateForItSelf(Coords p1, Coords p2){

		//TODO prevent too large values
		

		
		
	}
	
	/**
	 * @return the line thickness
	 */
	protected int getLineThickness(){
		return getGeoElement().getLineThickness();
	}
	
	
	
	
	
	
	
	
	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}	

	
	
	
	
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	
	@SuppressWarnings("unchecked")
	private ArrayList selectedPoints;

	/**
	 * constructor for previewable
	 * @param a_view3D
	 * @param selectedPoints
	 * @param cs1D
	 */
	@SuppressWarnings("unchecked")
	public DrawPolyLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints, GeoPolyLine p){
		
		super(a_view3D);
		
		
		p.setIsPickable(false);
		setGeoElement(p);
		
		this.selectedPoints = selectedPoints;
		
		updatePreview();
		
	}	

	




	public void updateMousePos(double xRW, double yRW) {	
		
	}

public void updatePreview() {
		
		
		if (selectedPoints.size()>0){
			GeoPointND[] points = new GeoPointND[selectedPoints.size()];
			
			for (int i = 0; i<selectedPoints.size(); i++){
				points[i] = (GeoPointND) selectedPoints.get(i);
			}

			((GeoPolyLine) getGeoElement()).setPoints(points);
			
			getGeoElement().setEuclidianVisible(true);
			setWaitForUpdate();
		}else{
			getGeoElement().setEuclidianVisible(false);
		}
		
			
	}
	

protected void updateForView(){
	if (getView3D().viewChangedByZoom())
		updateForItSelf();
}




}
