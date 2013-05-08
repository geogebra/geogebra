package geogebra3D.euclidian3D;

import geogebra.common.kernel.geos.GeoElement;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Renderer.PickingType;


/**
 * 
 * @author ggb3D
 *	
 *	for "solid" drawables, like lines, segments, etc.
 *	these are drawable that are not to become transparent
 *
 */


public abstract class Drawable3DCurves extends Drawable3D {


	
	protected boolean elementHasChanged;


	/**
	 * common constructor
	 * @param a_view3d
	 * @param a_geo
	 */
	public Drawable3DCurves(EuclidianView3D a_view3d, GeoElement a_geo) {
		super(a_view3d, a_geo);
	}
	
	/**
	 * says that it has to be updated
	 */
	@Override
	public void setWaitForUpdate(){
		elementHasChanged = true;
		super.setWaitForUpdate();
	}

	
	/**
	 * constructor for previewables
	 * @param a_view3d
	 */
	public Drawable3DCurves(EuclidianView3D a_view3d) {
		super(a_view3d);
	}

	@Override
	public void drawOutline(Renderer renderer) {
		
		if(!isVisible())
			return;	
		
		
		setLight(renderer);
			
		setHighlightingColor();
		
		//Application.debug("geo:"+getGeoElement().getLabel()+", lineType="+getGeoElement().getLineTypeHidden());
		renderer.getTextures().setDashFromLineType(getGeoElement().getLineType()); 
		drawGeometry(renderer);
		
		

	}

	
	
	

	
	
	
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	

	
	@Override
	public void drawGeometryPicked(Renderer renderer){
		drawGeometry(renderer);
	}
	
	@Override
	public void drawGeometryHidden(Renderer renderer){
		
		drawGeometry(renderer);
	} 
	


	
	// methods not used for solid drawables
	@Override
	public void drawHiding(Renderer renderer) {}
	@Override
	public void drawTransp(Renderer renderer) {}
	@Override
	public void drawNotTransparentSurface(Renderer renderer) {}

	@Override
	public boolean isTransparent() {
		return false;
	}
	
	@Override
	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_CURVES);
	}
    
    @Override
	public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_CURVES);
    }
	

	@Override
	protected double getColorShift(){
		return 0.75;
	}

	@Override
	public void setWaitForUpdateVisualStyle(){
		super.setWaitForUpdateVisualStyle();
		
		//also update for e.g. line width
		setWaitForUpdate();
	}
	
	@Override
	public PickingType getPickingType(){
		return PickingType.POINT_OR_CURVE;
	}
}
