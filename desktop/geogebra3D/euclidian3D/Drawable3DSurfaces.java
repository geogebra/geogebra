package geogebra3D.euclidian3D;

import geogebra.common.kernel.geos.GeoElement;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Renderer.PickingType;

/**
 * Class for drawing surfaces
 * @author matthieu
 *
 */
public abstract class Drawable3DSurfaces extends Drawable3D {
	

	protected boolean elementHasChanged;
	

	/**
	 * common constructor
	 * @param a_view3d
	 * @param a_geo
	 */
	public Drawable3DSurfaces(EuclidianView3D a_view3d, GeoElement a_geo) {
		super(a_view3d, a_geo);
	}

	/**
	 * common constructor for previewable
	 * @param a_view3d
	 */
	public Drawable3DSurfaces(EuclidianView3D a_view3d) {
		super(a_view3d);
	}
	
	
	/**
	 * draws the geometry that hides other drawables (for dashed curves)
	 * @param renderer
	 */
	abstract void drawGeometryHiding(Renderer renderer);


	@Override
	public void drawHiding(Renderer renderer){
		
    	if(isVisible() && hasTransparentAlpha()){
    		drawSurfaceGeometry(renderer);
    	}
    	
    	drawTracesHidingSurface(renderer);
		
	}
	
	
	protected abstract void drawSurfaceGeometry(Renderer renderer);

	


	@Override
	public void drawTransp(Renderer renderer){
		
    	if(isVisible() && hasTransparentAlpha()){
    		setSurfaceHighlightingColor();
    		drawSurfaceGeometry(renderer);
    	}
    	
    	drawTracesTranspSurface(renderer);
		
		
	}
	
	
	
	
	@Override
	protected void drawGeometryForPickingIntersection(Renderer renderer){
		drawOutline(renderer);
	}
	
	// method used only if surface is not transparent
	@Override
	public void drawNotTransparentSurface(Renderer renderer){
		
		if(isVisible() && getAlpha()>=1){
			setSurfaceHighlightingColor();
			drawSurfaceGeometry(renderer);		
		}

		drawTracesNotTranspSurface(renderer);
		
	}
	
	

	@Override
	protected void updateColors(){
		updateAlpha();
		setColorsOutlined();
	}
	
	
	/*
	protected boolean updateForItSelf(){
				
		updateColors();
		
		return true;
	}
	*/
	
	@Override
	protected void updateForView(){
		
	}
	
	/**
	 * says that it has to be updated
	 */
	@Override
	public void setWaitForUpdate(){
		elementHasChanged = true;
		super.setWaitForUpdate();
	}
	


	@Override
	public boolean isTransparent() {
		return getAlpha()<0.99f;
	}
	
	

	

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_SURFACES);
	}
    
    @Override
	public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_SURFACES);
    }
	

	@Override
	protected double getColorShift(){
		return 0.2;
	}
	
	
	@Override
	public PickingType getPickingType(){
		return PickingType.SURFACE;
	}


}
