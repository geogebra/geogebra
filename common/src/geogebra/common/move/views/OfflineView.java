package geogebra.common.move.views;

import java.util.ArrayList;
import java.util.Iterator;



/**
 * @author gabor
 * 
 * renders the view concerning application is offline
 *
 */
public class OfflineView extends BaseView {
	
	/**
	 * Contstructs an offline view pool
	 */
	public OfflineView() {
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
	 * @param view adds a new view
	 */
	public void add(Renderable view) {
		if (this.viewComponents == null) {
			this.viewComponents = new ArrayList<Renderable>();
		}
		this.viewComponents.add(view);
	}
	
	

}
