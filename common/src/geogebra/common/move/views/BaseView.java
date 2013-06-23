package geogebra.common.move.views;

import java.util.ArrayList;

/**
 * @author gabor
 * 	Base of all views
 *
 */
public abstract class BaseView {
	
	
	/**
	 * 
	 */
	protected ArrayList viewComponents = null;
	
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
	public void remove(Renderable view) {
		if (viewComponents != null) {
			if (viewComponents.contains(view)) {
				viewComponents.remove(view);
			}
		}
	}
}
