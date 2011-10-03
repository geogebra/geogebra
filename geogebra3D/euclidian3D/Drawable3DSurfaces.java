package geogebra3D.euclidian3D;

import geogebra.kernel.GeoElement;
import geogebra3D.euclidian3D.opengl.Renderer;

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


	public void drawHiding(Renderer renderer){
		if(!isVisible())
			return;

		if (!hasTransparentAlpha())
			return;
		
		//renderer.setMatrix(getMatrix());
		drawGeometryHiding(renderer);
		
	}
	
	
	

	


	public void drawTransp(Renderer renderer){
		if(!isVisible()){
			return;
		}
		
		
		if (!hasTransparentAlpha())
			return;
		
		setLight(renderer);

		setHighlightingColor();
			
		
		//renderer.setMatrix(getMatrix());
		drawGeometry(renderer);
		
		
	}
	
	
	public void drawOutline(Renderer renderer) {}
	
	
	// method used only if surface is not transparent
	public void drawNotTransparentSurface(Renderer renderer){
		
		if(!isVisible()){
			return;
		}
		
		
		if (getAlpha()<1)
			return;
		
		setLight(renderer);
		setHighlightingColor();
		drawGeometry(renderer);
		
	}
	
	public void drawHidden(Renderer renderer){} 	
	
	
	protected void updateColors(){
		//update alpha value
		updateAlpha();
		super.updateColors();
	}
	
	/*
	protected boolean updateForItSelf(){
				
		updateColors();
		
		return true;
	}
	*/
	
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
	


	public boolean isTransparent() {
		return true;
	}
	
	

	

	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_SURFACES);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_SURFACES);
    }
	

	protected double getColorShift(){
		return 0.2;
	}
	
	



}
