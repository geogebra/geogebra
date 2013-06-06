	package geogebra3D.euclidian3D;




import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Renderer.PickingType;
import geogebra3D.kernel3D.GeoPolyhedron;






/**
 * Class for drawing 3D polygons.
 * @author matthieu
 *
 */
public class DrawPolyhedron3D extends Drawable3DSurfaces {


	
	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param poly
	 */
	public DrawPolyhedron3D(EuclidianView3D a_view3D, GeoPolyhedron poly){
		
		super(a_view3D, poly);
		
		
		
		

		
	}
	

	
	//drawing

	@Override
	public void drawGeometry(Renderer renderer) {

		renderer.setLayer(getGeoElement().getLayer()); //+0f for z-fighting with planes
		renderer.getGeometryManager().draw(getGeometryIndex());	
		renderer.setLayer(0);

	}
	
	
	@Override
	public void drawOutline(Renderer renderer) {
		
		if(isVisible()){

			setHighlightingColor();

			renderer.getTextures().setDashFromLineType(getGeoElement().getLineType());
			drawGeometry(renderer);
		}

		drawTracesOutline(renderer);

	}
	

	
	@Override
	public void drawGeometryPicked(Renderer renderer){
		drawSurfaceGeometry(renderer);
	}
	@Override
	public void drawGeometryHiding(Renderer renderer) {
		drawSurfaceGeometry(renderer);
	}
	
	
	@Override
	public void drawGeometryHidden(Renderer renderer){
		drawGeometry(renderer);
	}
	
    @Override
    protected void drawGeometryForPicking(Renderer renderer, PickingType type){
    	if (type==PickingType.POINT_OR_CURVE){
    		drawGeometry(renderer);
    	}else{
    		if(getAlpha()>0){ //surface is pickable only if not totally transparent
    			drawSurfaceGeometry(renderer);
    		}
    	}
	}
	

	@Override
	protected void drawSurfaceGeometry(Renderer renderer){

		renderer.setLayer(getGeoElement().getLayer()); //+0f to avoid z-fighting with planes
		renderer.getGeometryManager().draw(getSurfaceIndex());
		renderer.setLayer(0);

	}
	
	
	@Override
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D; 
	}	
	
	


	@Override
	public void addToDrawable3DLists(Drawable3DLists lists){

		addToDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED);
		addToDrawable3DLists(lists,DRAW_TYPE_CURVES);
		
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists){

		removeFromDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES_NOT_CURVED);
		removeFromDrawable3DLists(lists,DRAW_TYPE_CURVES);

	}
    
    
	
	
	@Override
	protected boolean updateForItSelf(){
		
		Renderer renderer = getView3D().getRenderer();
		
		// outline
		updateOutline(renderer);
		
		
		// surface
		int index = renderer.startPolygons();
		for (GeoPolygon p : ((GeoPolyhedron) getGeoElement()).getPolygonsLinked()){
			drawPolygon(renderer, p);
		}
		for (GeoPolygon p : ((GeoPolyhedron) getGeoElement()).getPolygons()){
			drawPolygon(renderer, p);
		}
		renderer.endPolygons();
		
		setSurfaceIndex(index);

		
		
		return true;
		
	}
	
	
	private void updateOutline(Renderer renderer){

		
		GeoPolyhedron poly = (GeoPolyhedron) getGeoElement();
		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();	
		brush.start(8);
		brush.setThickness(poly.getLineThickness(),(float) getView3D().getScale());
		
		for (GeoSegmentND seg: poly.getSegmentsLinked()){
			drawSegment(brush, seg);
		}
		for (GeoSegmentND seg: poly.getSegments()){
			drawSegment(brush, seg);
		}
		
		setGeometryIndex(brush.end());

	}
	
	
	private static void drawSegment(PlotterBrush brush, GeoSegmentND seg){
		brush.setAffineTexture(0.5f,  0.25f);
		brush.segment(seg.getStartInhomCoords(), seg.getEndInhomCoords());
		
	}
	
	private static void drawPolygon(Renderer renderer, GeoPolygon polygon){
		
		int pointLength = polygon.getPointsLength();
		
		if (pointLength<3 || Kernel.isZero(polygon.getArea())){ //no polygon
			return;
		}
		
		Coords n = polygon.getMainDirection();
		Coords[] vertices = new Coords[pointLength];
		for(int i=0;i<pointLength;i++){
			vertices[i] = polygon.getPoint3D(i);
		}
		renderer.drawPolygon(n, vertices);
		
	}
	

	@Override
	protected void updateForView(){

		if (getView3D().viewChangedByZoom()){

			Renderer renderer = getView3D().getRenderer();

			// outline
			updateOutline(renderer);

			recordTrace();

		}
	}
	
	






}
