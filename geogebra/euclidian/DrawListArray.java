package geogebra.euclidian;


import geogebra.kernel.GeoElement;

import java.util.ArrayList;



/**
 * Class for storing drawables includes in a DrawList
 * 
 * @author matthieu
 *
 */
public class DrawListArray extends ArrayList<DrawableND> {

	private static final long serialVersionUID = 1L;
	
	/**  view  */
	protected EuclidianViewInterface view;
	
	/**
	 * common constructor
	 * @param view 
	 */
	public DrawListArray(EuclidianViewInterface view){
		
		this.view = view;
		//drawables = new ArrayList<DrawableND>();
		
	}
	
	
	

    /**
     * Add the listElement's drawable
     * @param listElement
     * @param drawablePos
     * @param oldDrawableSize
     * @param drawList 
     * @return false if the drawable == null
     */
    public boolean addToDrawableList(
    		GeoElement listElement, int drawablePos, int oldDrawableSize, 
    		DrawableND drawList) {
    	DrawableND d = null;
		boolean inOldDrawableRange = drawablePos < oldDrawableSize;
		if (inOldDrawableRange) {
			// try to reuse old drawable
			DrawableND oldDrawable = get(drawablePos);
    		if (oldDrawable.getGeoElement() == listElement) {	
    			d = oldDrawable;
    		} else {
    			d = getDrawable(listElement, drawList);  			
    		}	    		    		    	
		} else {
			d = getDrawable(listElement, drawList); 
		}
		
		if (d != null) {
			
			update(d);
			
			if (inOldDrawableRange) {    				
				set(drawablePos, d);
			} else {
				add(drawablePos, d);
			}
			return true;
		}
		
		return false;
    }

    
    /**
     * update the drawable
     * @param d
     */
    protected void update(DrawableND d){
    	d.update();
    }
	
	
    private DrawableND getDrawable(GeoElement listElement, DrawableND drawList) {
    	
    	DrawableND d = view.getDrawableND(listElement);
		if (d == null) {    	
			// create a new drawable for geo
			d = createDrawableND(listElement);//view.createDrawableND(listElement); 
			if (d!=null)
				d.setCreatedByDrawList(drawList);
		} 
		return d;
    }

    
    /**
     * @param listElement
     * @return the drawable create by the view for this geo
     */
    protected DrawableND createDrawableND(GeoElement listElement){
    	return view.createDrawableND(listElement);  
    }

	

}
