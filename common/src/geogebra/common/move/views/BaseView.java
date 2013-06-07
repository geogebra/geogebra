package geogebra.common.move.views;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author gabor
 * 	Base of all views
 *
 */
public abstract class BaseView {
	
	
	/**
	 * 
	 */
	protected ArrayList<Renderable> viewComponents = null;
	
	/**
	 * @param view to render
	 * 
	 * called from child objects.
	 */
	protected BaseView() {
		
	}
	
	/**
	 * renders the given View
	 */
	public void render() {
		Iterator<Renderable> views = this.viewComponents.iterator();
		while (views.hasNext()) {
			views.next().render();
		}
		
	}
	
	/**
	 * @param view Renderable view
	 * 
	 * Adds new view to the view's list
	 */
	public void add(Renderable view) {
		if (viewComponents == null) {
			viewComponents = new ArrayList<Renderable>();
		}
		viewComponents.add(view);
	}
	
	/**
	 * @param view Renderable view
	 * 
	 * Removes a view from the views list
	 */
	public void remove(Renderable view) {
		if (viewComponents != null) {
			if (viewComponents.contains(view)) {
				viewComponents.remove(view);
			}
		}
	}
}
