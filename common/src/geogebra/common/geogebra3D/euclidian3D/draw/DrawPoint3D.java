package geogebra.common.geogebra3D.euclidian3D.draw;



import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.draw.DrawPoint;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.Hitting;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.Functional2Var;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;




//TODO does not extend Drawable3DCurves


/**
 * Class for drawing 3D points.
 * 
 * @author matthieu
 * 
 *
 */
public class DrawPoint3D extends Drawable3DCurves 
implements Previewable, Functional2Var{
	
	
	/** factor for drawing points */
	public static final float DRAW_POINT_FACTOR = 1.5f;
	
		
	
	/**
	 * common constructor
	 * @param view3D
	 * @param point
	 */
	public DrawPoint3D(EuclidianView3D view3D, GeoPointND point) {     
		
		super(view3D, (GeoElement) point);
		
	}
	
	
	
	
	

	@Override
	public void drawGeometry(Renderer renderer) {


		renderer.getGeometryManager().draw(getGeometryIndex(), center);


	}

	@Override
	public void drawInObjFormat(Renderer renderer){
		if(isVisible()){
			renderer.getGeometryManager().drawInObjFormat(getGeoElement(),getGeometryIndex());
		}
	}
	
	@Override
	public void drawGeometryHidden(Renderer renderer){

		drawGeometry(renderer);
	}	
	
	@Override
	protected void setLineTextureHidden(Renderer renderer){
		// nothing to do here
	}
	
	
	private Coords center = new Coords(4);

	@Override
	protected boolean updateForItSelf(){
		
	
		GeoPointND point = (GeoPointND) getGeoElement(); 
		center.setValues(point.getInhomCoordsInD3(), 3);
		center.setW(point.getPointSize()); // put point size in fourth unused coord
		setGeometryIndex(
				getView3D().getRenderer().getGeometryManager().
				drawPoint(point.getPointSize(),center));
		
		
		return true;
	}
	
	@Override
	protected void doRemoveGeometryIndex(int index){
		// use Manager templates -- no remove for points
	}
	
	@Override
	protected void updateForView(){
		
		if (getView3D().viewChangedByZoom()){
			updateForItSelf();
		}
		
	}
	
	
	
	
	@Override
	public int getPickOrder(){
		return DRAW_PICK_ORDER_0D;
	}	
	
	
	
	////////////////////////////////
	// Previewable interface 
	

	/**
	 * @param a_view3D
	 */
	public DrawPoint3D(EuclidianView3D a_view3D){
		
		super(a_view3D);
		
		setGeoElement(a_view3D.getCursor3D());
		
	}	

	@Override
	public void disposePreview() {
		// TODO Auto-generated method stub
		
	}



	public void updateMousePos(double xRW, double yRW) {	
			
		
	}


	public void updatePreview() {

		
	}
	
	

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_POINTS);
	}
    
    @Override
	public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_POINTS);
    }



	///////////////////////////////////
	// FUNCTION2VAR INTERFACE
	///////////////////////////////////
	
	








	public Coords evaluatePoint(double u, double v) {
		
		GeoPointND point = (GeoPointND) getGeoElement(); 
		
		double r = point.getPointSize()/getView3D().getScale()*1.5;
		Coords n = new Coords(new double[] {
				Math.cos(u)*Math.cos(v)*r,
				Math.sin(u)*Math.cos(v)*r,
				Math.sin(v)*r});
		
		return (Coords) n.add(point.getInhomCoordsInD3());
	}


	

	public Coords evaluateNormal(double u, double v) {
		return new Coords(new double[] {
				Math.cos(u)*Math.cos(v),
				Math.sin(u)*Math.cos(v),
				Math.sin(v)});
	}




	public double getMinParameter(int index) {
		switch(index){
		case 0: //u
		default:
			return 0;
		case 1: //v
			return -Math.PI/2;
		}
	}


	public double getMaxParameter(int index) {
		switch(index){
		case 0: //u
		default:
			return 2*Math.PI; 
		case 1: //v
			return Math.PI/2;
		}
		
	}





	

	@Override
	protected float getLabelOffsetX(){
		//consistent with DrawPoint
		return super.getLabelOffsetX()+4;
	}

	@Override
	protected float getLabelOffsetY(){
		//consistent with DrawPoint
		return super.getLabelOffsetY()-2*((GeoPointND) getGeoElement()).getPointSize();
	}
	
	
	

	
	@Override
	protected double getColorShift(){
		return COLOR_SHIFT_POINTS;
	}
	
	
	
	@Override
	public boolean hit(Hitting hitting){
		
		GeoPointND point = (GeoPointND) getGeoElement();
		Coords p = point.getInhomCoordsInD3();		
		return DrawPoint3D.hit(hitting, p, this, point.getPointSize());
		
	}
	
	/**
	 * 
	 * @param hitting hitting
	 * @param p point coords
	 * @param drawable drawable calling
	 * @param pointSize point size
	 * @return true if the hitting hits the point
	 */
	static public boolean hit(Hitting hitting, Coords p, Drawable3D drawable, int pointSize){
		Coords[] project = p.projectLine(hitting.origin, hitting.direction);
		
		if (!hitting.isInsideClipping(project[0])){
			return false;
		}
		
		double d = p.distance(project[0]);
		double scale = drawable.getView3D().getScale();
		if (d * scale <= DrawPoint.getSelectionThreshold(hitting.getThreshold())){
			double z = -project[1].getX();
			double dz = pointSize/scale;
			drawable.setZPick(z+dz, z-dz);
			return true;
		}
		
		return false;
	}
	
	
	@Override
	protected TraceIndex newTraceIndex(){
		return new TraceIndex(getGeometryIndex(), getSurfaceIndex(), center);
	}
	
	@Override
	protected void drawGeom(Renderer renderer, TraceIndex index){
		renderer.getGeometryManager().draw(index.geom, index.center);
	}
	
}
