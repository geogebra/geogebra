package geogebra3D.euclidian3D;

import geogebra.common.euclidian.DrawableND;
import geogebra.common.kernel.geos.GeoElement;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Renderer.PickingType;

/**
 * Class for drawing geos that are lists of geos (GeoList, GeoQuadric3DLimited, etc.)
 * 
 * @author matthieu
 *
 */
public abstract class Drawable3DList extends Drawable3D {
	
	
	protected DrawList3DArray drawables;

	/**
	 * common constructor
	 * @param view3D
	 * @param geo
	 */
	public Drawable3DList(EuclidianView3D view3D, GeoElement geo) {
		super(view3D, geo);
		
		drawables = new DrawList3DArray(view3D);
		
	}

    /**
     * @return all 3D drawables contained in this list
     */
    public DrawList3DArray getDrawables3D(){
    	return drawables;
    }

	@Override
	public void drawOutline(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drawNotTransparentSurface(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGeometryPicked(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawHidden(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawHiding(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawTransp(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Drawable3D drawForPicking(Renderer renderer, boolean intersection, PickingType type) {
		
		return getDrawablePicked();
		
	}

	@Override
	public void drawLabel(Renderer renderer){
		
	}
	
	
	@Override
	public boolean drawLabelForPicking(Renderer renderer){
		return false;
	}
	
	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_LIST;
	}



	@Override
	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_DEFAULT);
	}
    
    @Override
	public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_DEFAULT);
    }
    
    
	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	protected void updateForView() {
		
	}
	
	
	@Override
	protected void updateLabel(){
		//no label for 3D lists
	}

	

	@Override
	protected double getColorShift(){
		return COLOR_SHIFT_NONE; //not needed here
	}
	
	@Override
	public void setWaitForUpdateVisualStyle(){
		
		super.setWaitForUpdateVisualStyle();
		for (DrawableND d : drawables){
			d.setWaitForUpdateVisualStyle();
		}
		
		//also update for e.g. line width
		setWaitForUpdate();
	}
	
	@Override
	public void setWaitForUpdate(){
		
		super.setWaitForUpdate();
		for (DrawableND d : drawables){
			d.setWaitForUpdate();
		}
	}
	
	@Override
	public PickingType getPickingType(){
		return getDrawablePicked().getPickingType();
	}
}
