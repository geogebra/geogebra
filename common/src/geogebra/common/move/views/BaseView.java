package geogebra.common.move.views;

import java.util.ArrayList;

/**
 * @author gabor
 * 	Base of all views
 *
 */
public abstract class BaseView<T> {
	
	
	/**
	 * 
	 */
	protected ArrayList<T> viewComponents = null;
	
	/**
	 * @param view to render
	 * 
	 * called from child objects.
	 */
	protected BaseView() {
		
	}
	
	/**
	 * @param view Renderable view
	 * 
	 * Removes a view from the views list
	 */
	public void remove(T view) {
		if (viewComponents != null) {
			if (viewComponents.contains(view)) {
				viewComponents.remove(view);
			}
		}
	}
}
