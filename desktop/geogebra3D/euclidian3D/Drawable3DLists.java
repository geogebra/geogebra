package geogebra3D.euclidian3D;

import geogebra.common.kernel.StringTemplate;
import geogebra3D.euclidian3D.opengl.Renderer;

import java.util.Iterator;
import java.util.LinkedList;




/**
 * Class to list the 3D drawables for EuclidianView3D
 * 
 * @author ggb3D
 * 
 *
 */
public class Drawable3DLists {
	
	
	protected class Drawable3DList extends LinkedList<Drawable3D>{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			for (Drawable3D d: this){
				sb.append(d);
				sb.append(" -- ");
				sb.append(d.getGeoElement().getLabel(StringTemplate.defaultTemplate));
				sb.append("\n");
			}
			return sb.toString();
				
		}
	}
	
	/** lists of Drawable3D */
	private Drawable3DList[] lists;
	
	
	private EuclidianView3D view3D;
	
	
	/**
	 * default constructor
	 * @param view3D 
	 */
	public Drawable3DLists(EuclidianView3D view3D){
		
		this.view3D = view3D;
		lists = new Drawable3DList[Drawable3D.DRAW_TYPE_MAX];
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			lists[i] = new Drawable3DList();
	}
	
	
	
	/** add the drawable to the correct list
	 * @param drawable drawable to add
	 */
	public void add(Drawable3D drawable){
		
		drawable.addToDrawable3DLists(this);
		
		if (drawable.getGeoElement()!=null && drawable.getGeoElement().isPickable())
			view3D.addOneGeoToPick();
		
	}
	
	/**
	 * add a list of drawables
	 * @param list
	 */
	public void add(LinkedList<Drawable3D> list){
		
		for (Drawable3D d : list)
			add(d);
		
		
		
		
	}
	
	/** remove the drawable from the correct list
	 * @param drawable drawable to remove
	 */
	private void remove(Drawable3D drawable){
	
		//TODO fix it
		if (drawable!=null){
			//Application.debug(drawable.getGeoElement());
			drawable.removeFromDrawable3DLists(this);
			//Application.debug(size());
			if (drawable.getGeoElement()!=null && drawable.getGeoElement().isPickable())
				view3D.removeOneGeoToPick();
		}
		
	}
	
	
	/** remove all drawables contained in the list
	 * @param list
	 */
	public void remove(LinkedList<Drawable3D> list){
		for (Drawable3D d : list)
			remove(d);
			
	}
	
	
	Drawable3DList getList(int type){
		return lists[type];
	}
	
	/**
	 *  return the size of the cummulated lists
	 * @return the size of the cummulated lists
	 */
	public int size(){
		int size = 0;
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			size += lists[i].size();
		return size;
	}
	
	/**
	 * 
	 * @return true if contains clipped surfaces
	 */
	public boolean containsClippedSurfaces(){
		return !lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES].isEmpty();
	}
	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++){
			sb.append("list #");
			sb.append(i);
			sb.append(":\n");
			sb.append(lists[i].toString());
		}
		
		return sb.toString();
	}
	
	
	/**
	 * clear all the lists
	 */
	public void clear(){
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			lists[i].clear();
	}
	
	
	//private boolean isUpdatingAll = false;
	
	/** update all 3D objects */
	public void updateAll(){
		
		
		/*
		if (isUpdatingAll){
			Application.printStacktrace("is already updating");
			return;
		}
		
		isUpdatingAll = true;
		*/
		
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();){
				Drawable3D d3d = d.next();
				//Application.debug("updating :"+d3d.getGeoElement());
				d3d.update();	
			}	
		
		//isUpdatingAll = false;
		
	}
	
	
	/** says all have to be reset */
	public void resetAllDrawables(){
		
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().setWaitForReset();		
		
	}
	
	/** says all visual styles to be updated */
	public void resetAllVisualStyles(){
		
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().setWaitForUpdateVisualStyle();	
		
	}
	
	/**
	 * draw hidden parts not dashed
	 * @param renderer
	 */
	public void drawHiddenNotTextured(Renderer renderer){
		// points TODO hidden aspect ?
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_POINTS].iterator(); d.hasNext();) 
			d.next().drawHidden(renderer);
	}
	
	
	/**
	 * draw surfaces that are not transparent
	 * @param renderer
	 */
	public void drawNotTransparentSurfaces(Renderer renderer){
		

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_SURFACES].iterator(); d.hasNext();) 
			d.next().drawNotTransparentSurface(renderer);		

		
	}
	
	/**
	 * draw closed surfaces that are not transparent
	 * @param renderer
	 */
	public void drawNotTransparentSurfacesClosed(Renderer renderer){
		
	
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES].iterator(); d.hasNext();) 
			d.next().drawNotTransparentSurface(renderer);	

		
	}

	/**
	 * draw clipped surfaces that are not transparent
	 * @param renderer
	 */
	public void drawNotTransparentSurfacesClipped(Renderer renderer){
		
	
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES].iterator(); d.hasNext();) 
			d.next().drawNotTransparentSurface(renderer);	

		
	}
	
	/** draw the hidden (dashed) parts of curves and points
	 * @param renderer opengl context
	 */
	public void drawHiddenTextured(Renderer renderer){


		
		// curves
		// TODO if there's no surfaces, no hidden part has to be drawn
		//if(!lists[Drawable3D.DRAW_TYPE_SURFACES].isEmpty())
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CURVES].iterator(); d.hasNext();) 
			d.next().drawHidden(renderer);

		

		view3D.drawHidden(renderer);

	}


	/** draw surfaces as transparent parts
	 * @param renderer opengl context
	 */
	public void drawTransp(Renderer renderer){

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_SURFACES].iterator(); d.hasNext();) 
			d.next().drawTransp(renderer);	
		
		view3D.drawTransp(renderer);


	}
	
	/**
	 * draw transparent closed surfaces
	 * @param renderer
	 */
	public void drawTranspClosed(Renderer renderer){

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES].iterator(); d.hasNext();) 
			d.next().drawTransp(renderer);	
		
	}
	
	/**
	 * draw transparent clipped surfaces
	 * @param renderer
	 */
	public void drawTranspClipped(Renderer renderer){

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES].iterator(); d.hasNext();) 
			d.next().drawTransp(renderer);	
		
	}

	/** draw the not hidden (solid) parts of curves and points
	 * @param renderer opengl context
	 */
	public void draw(Renderer renderer){	

		// points TODO hidden aspect ?
		/*
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_POINTS].iterator(); d.hasNext();) 
			d.next().draw(renderer);
			*/
		
		// curves
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CURVES].iterator(); d.hasNext();) 
			d.next().drawOutline(renderer);
		
		view3D.draw(renderer);
		
	}
	
	/** draw the labels of objects
	 * @param renderer opengl context
	 */
	public void drawLabel(Renderer renderer){

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().drawLabel(renderer);	

		view3D.drawLabel(renderer);
	}


	/** draw the hiding (surfaces) parts
	 * @param renderer opengl context
	 */
	public void drawSurfacesForHiding(Renderer renderer){

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_SURFACES].iterator(); d.hasNext();) 
			d.next().drawHiding(renderer);	
		
		view3D.drawHiding(renderer);

	}
	
	/** draw the hiding (closed surfaces) parts
	 * @param renderer opengl context
	 */
	public void drawClosedSurfacesForHiding(Renderer renderer){

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLOSED_SURFACES].iterator(); d.hasNext();) 
			d.next().drawHiding(renderer);	

	}
	
	/** draw the hiding (clipped surfaces) parts
	 * @param renderer opengl context
	 */
	public void drawClippedSurfacesForHiding(Renderer renderer){

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES].iterator(); d.hasNext();) 
			d.next().drawHiding(renderer);	

	}

	/** draw objects to pick them
	 * @param renderer opengl context
	 */
	public void drawForPicking(Renderer renderer){

		renderer.setCulling(true);
		for(int i=0; i<Drawable3D.DRAW_TYPE_SURFACES; i++)
			for (Iterator<Drawable3D> iter = lists[i].iterator(); iter.hasNext();) {
	        	Drawable3D d = iter.next();
	        	renderer.pick(d);
			}
		
		renderer.setCulling(false);
		for(int i=Drawable3D.DRAW_TYPE_SURFACES; i<=Drawable3D.DRAW_TYPE_CLOSED_SURFACES; i++)
			for (Iterator<Drawable3D> iter = lists[i].iterator(); iter.hasNext();) {
	        	Drawable3D d = iter.next();
	        	renderer.pick(d);
			}		

		if (containsClippedSurfaces()){
			renderer.enableClipPlanesIfNeeded();
			for (Iterator<Drawable3D> iter = lists[Drawable3D.DRAW_TYPE_CLIPPED_SURFACES].iterator(); iter.hasNext();) {
				Drawable3D d = iter.next();
				renderer.pick(d);
			}
			renderer.disableClipPlanesIfNeeded();
		}
		
		view3D.drawForPicking(renderer);
		
		renderer.setCulling(true);
		

	}
	
	/** draw objects labels to pick them
	 * @param renderer opengl context
	 */
	public void drawLabelForPicking(Renderer renderer){

		
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> iter = lists[i].iterator(); iter.hasNext();) {
	        	Drawable3D d = iter.next();
	        	
	        	renderer.pickLabel(d);
	        	
	        	/*
	        	loop++;
	        	renderer.glLoadName(loop);
	        	d.drawLabel(renderer,false,true);
	        	drawHits[loop] = d;
	        	*/
			}
		
		//return loop;

	}
	

}
