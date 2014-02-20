package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.euclidian.Previewable;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.Hitting;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

/**
 * Class for drawing 1D coord sys (lines, segments, ...)
 * @author matthieu
 *
 */
public abstract class DrawJoinPoints extends Drawable3DCurves implements Previewable {

	private double[] drawMinMax = new double[2];
	


	
	/**
	 * common constructor
	 * @param a_view3D
	 * @param cs1D
	 */
	public DrawJoinPoints(EuclidianView3D a_view3D, GeoElement geo){
		
		super(a_view3D, geo);
	}	
	
	
	
	/**
	 * common constructor for previewable
	 * @param a_view3d
	 */
	public DrawJoinPoints(EuclidianView3D a_view3d) {
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
	
	
	@Override
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}
	
	

	@Override
	public void drawInObjFormat(Renderer renderer) {
		if(isVisible()){
			renderer.getGeometryManager().drawInObjFormat(getGeoElement(),getGeometryIndex());
		}
	}
	
	
	
	private Coords startPoint, endPoint;
	
	/**
	 * 
	 * @return start and end points coords
	 */
	abstract protected Coords[] calcPoints();
	
	
	@Override
	protected boolean updateForItSelf(){
		
		Coords[] points = calcPoints();
		updateForItSelf(points[0],points[1]);		
		return true;
	}

	/**
	 * update the drawable as a segment from p1 to p2
	 * @param p1
	 * @param p2
	 */
	final protected void updateForItSelf(Coords p1, Coords p2){

		//TODO prevent too large values
		startPoint = p1;
		endPoint = p2;
		
		double[] minmax = getDrawMinMax(); 
		
		if (Math.abs(minmax[0])>1E10)
			return;
		
		if (Math.abs(minmax[1])>1E10)
			return;
		
		if (minmax[0]>minmax[1])
			return;
		
		
		Renderer renderer = getView3D().getRenderer();
		


		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		setArrowTypeBefore(brush);
		brush.start(8);
		brush.setThickness(getLineThickness(),(float) getView3D().getScale());
		brush.setAffineTexture( (float) ((0.5-minmax[0])/(minmax[1]-minmax[0])),  0.25f);		

		brush.segment(p1, p2);
		setArrowTypeAfter(brush);
		setGeometryIndex(brush.end());
		
		
		
	}
	
	/**
	 * used for vectors
	 * @param brush brush
	 */
	protected void setArrowTypeBefore(PlotterBrush brush){
		// nothing to do there
	}
	
	/**
	 * used for vectors
	 * @param brush brush
	 */
	protected void setArrowTypeAfter(PlotterBrush brush){
		// nothing to do there
	}
	
	
	/**
	 * @return the line thickness
	 */
	protected int getLineThickness(){
		return getGeoElement().getLineThickness();
	}
	
	
	
	
	
	
	
	
	

	
	@Override
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
	 * @param geo
	 */
	@SuppressWarnings("unchecked")
	public DrawJoinPoints(EuclidianView3D a_view3D, ArrayList selectedPoints, GeoElement geo){
		
		super(a_view3D);
		
		
		geo.setIsPickable(false);
		setGeoElement(geo);
		
		this.selectedPoints = selectedPoints;
		
		updatePreview();
		
	}	

	




	public void updateMousePos(double xRW, double yRW) {	
		
	}


	public void updatePreview() {

		if (selectedPoints == null){ //when intersection curve
			setWaitForUpdate();
			return;
		} 

		if (selectedPoints.size()==2){
			GeoPointND firstPoint = (GeoPointND) selectedPoints.get(0);
			GeoPointND secondPoint = (GeoPointND) selectedPoints.get(1);
			setPreviewableCoords(firstPoint, secondPoint);
			getGeoElement().setEuclidianVisible(true);
			//setWaitForUpdate();
		}else if (selectedPoints.size()==1){
			GeoPointND firstPoint = (GeoPointND) selectedPoints.get(0);
			GeoPointND secondPoint = getView3D().getCursor3D();
			setPreviewableCoords(firstPoint, secondPoint);
			getGeoElement().setEuclidianVisible(true);
			//setWaitForUpdate();
		}else{
			getGeoElement().setEuclidianVisible(false);
			//setWaitForUpdate();
		}
		
		//Application.debug("selectedPoints : "+selectedPoints+" -- isEuclidianVisible : "+getGeoElement().isEuclidianVisible());
	
		setWaitForUpdate();	
	}
	
	
	/**
	 * set previewable coords
	 * @param firstPoint first point
	 * @param secondPoint second point
	 */
	abstract protected void setPreviewableCoords(GeoPointND firstPoint, GeoPointND secondPoint);

	
	
	
	
	
	
	
	
	
	@Override
	protected boolean hit(Hitting hitting){
		
		Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(hitting.origin, hitting.direction, startPoint, endPoint.sub(startPoint));
		
		// check if hitting and line are parallel
		double parameterOnHitting = project[2].getX();
		if (Double.isNaN(parameterOnHitting)){
			return false;
		}
		
		// check if point on line is visible
		double parameterOnCS = project[2].getY();
		if (parameterOnCS < 0 || parameterOnCS > 1){
			return false;
		}
		
		double d = project[0].distance(project[1]);
		if (d * getView3D().getScale() <= getGeoElement().getLineThickness() + 2){
			double z = -parameterOnHitting;
			double dz = getGeoElement().getLineThickness()/getView3D().getScale();
			setZPick(z+dz, z-dz);
			return true;
		}
		
		return false;
	}
	


}
