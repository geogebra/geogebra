	package geogebra3D.euclidian3D;




import geogebra.common.euclidian.Previewable;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPolygon3D;

import java.util.ArrayList;






/**
 * Class for drawing multiple polygons within intersection curve.
 * @author matthieu
 *
 */
public class DrawPolygonsForIntersectionCurve extends Drawable3DSurfaces implements Previewable {


	private ArrayList<DrawPolygon3D> drawPolygons;
	
	/**
	 * Common constructor
	 * @param a_view3D 3D view
	 * @param poly first polygon
	 */
	public DrawPolygonsForIntersectionCurve(EuclidianView3D a_view3D, GeoPolygon3D poly){
		
		super(a_view3D, poly);
		
		drawPolygons = new ArrayList<DrawPolygon3D>();
		
	}
	
	/**
	 * add a polygon to draw
	 * @param d drawable
	 */
	public void add(DrawPolygon3D d){
		drawPolygons.add(d);
	}

	
	//drawing

	@Override
	public void drawGeometry(Renderer renderer) {

		for (DrawPolygon3D d : drawPolygons){
			d.drawGeometry(renderer);
		}

	}
	
	
	@Override
	public void drawOutline(Renderer renderer) {
		
		/*
		if(!isVisible())
			return;	
		*/
		
		setLight(renderer);
			
		setHighlightingColor();
		
		//App.debug("geo:"+getGeoElement()+", lineType="+getGeoElement().getLineTypeHidden());
		renderer.getTextures().setDashFromLineType(getGeoElement().getLineType()); 
		drawGeometry(renderer);
		
		

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
	protected void drawGeometryForPicking(Renderer renderer){
		drawSurfaceGeometry(renderer);
		drawGeometry(renderer);
	}
	

	@Override
	protected void drawSurfaceGeometry(Renderer renderer){

		for (DrawPolygon3D d : drawPolygons){
			d.drawSurfaceGeometry(renderer);
		}

	}


	@Override
	public int getPickOrder(){

		return DRAW_PICK_ORDER_2D; 

	}	




	@Override
	public void addToDrawable3DLists(Drawable3DLists lists){

		addToDrawable3DLists(lists,DRAW_TYPE_SURFACES);
		addToDrawable3DLists(lists,DRAW_TYPE_CURVES);

	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists){

		removeFromDrawable3DLists(lists,DRAW_TYPE_SURFACES);
		removeFromDrawable3DLists(lists,DRAW_TYPE_CURVES);

    }
    
    
	
	
	@Override
	protected boolean updateForItSelf(){
		
		for (DrawPolygon3D d : drawPolygons){
			d.updateForItSelf();
		}

		return true;
		
	}
	
	

	@Override
	protected void updateForView(){

		for (DrawPolygon3D d : drawPolygons){
			d.updateForView();
		}
	}
	
	
	////////////////////////////////
	// Previewable interface 

	









	public void updateMousePos(double xRW, double yRW) {	
		// TODO Auto-generated method stub
		
	}



	public void updatePreview() {
		
		setWaitForUpdate();
		
		
	}
	
	@Override
	public void disposePreview() {
		super.disposePreview();

		for (DrawPolygon3D d : drawPolygons){
			d.disposePreview();
		}
		
	}



}
