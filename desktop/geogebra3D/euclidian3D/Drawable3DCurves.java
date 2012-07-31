package geogebra3D.euclidian3D;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra3D.euclidian3D.opengl.Renderer;


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

	public void drawOutline(Renderer renderer) {
		
		if(!isVisible())
			return;	
		
		
		setLight(renderer);
			
		setHighlightingColor();
		
		//Application.debug("geo:"+getGeoElement().getLabel()+", lineType="+getGeoElement().getLineTypeHidden());
		renderer.getTextures().setDashFromLineType(getGeoElement().getLineType()); 
		drawGeometry(renderer);
		
		

	}

	
	
	
	public void drawHidden(Renderer renderer){
		
		
		if(!isVisible())
			return;
		
		
		if (getGeoElement().getLineTypeHidden()==EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE)
			return;
		
		setLight(renderer);

		setHighlightingColor();
		
		if (getGeoElement().getLineTypeHidden()==EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN)
			renderer.getTextures().setDashFromLineType(getGeoElement().getLineType()); 
		else
			renderer.getTextures().setDashFromLineTypeHidden(getGeoElement().getLineType()); 
		
		drawGeometryHidden(renderer);		
		


	} 
	
	
	
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	

	
	public void drawGeometryPicked(Renderer renderer){
		drawGeometry(renderer);
	}
	
	public void drawGeometryHidden(Renderer renderer){
		
		drawGeometry(renderer);
	} 
	


	
	// methods not used for solid drawables
	public void drawHiding(Renderer renderer) {}
	public void drawTransp(Renderer renderer) {}
	public void drawNotTransparentSurface(Renderer renderer) {}

	public boolean isTransparent() {
		return false;
	}
	
	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_CURVES);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_CURVES);
    }
	

	protected double getColorShift(){
		return 0.75;
	}
}
