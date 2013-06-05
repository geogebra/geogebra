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
	protected BaseView(Renderable view) {
		if (this.viewComponents == null) {
			this.viewComponents = new ArrayList<Renderable>();
		}
		this.viewComponents.add(view);
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
}
