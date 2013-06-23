package geogebra.common.move.views;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author gabor
 * 
 * renders the functionality concerning application is online
 *
 */
public class OnlineView extends BaseView {
	
	/**
	 * creates a new online View pool
	 */
	public OnlineView() {
		super();
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
}
