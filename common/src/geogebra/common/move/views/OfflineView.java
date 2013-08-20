package geogebra.common.move.views;

import java.util.ArrayList;
import java.util.Iterator;



/**
 * @author gabor
 * 
 * renders the view concerning application is offline
 *
 */
public class OfflineView extends BaseView<BooleanRenderable> {
	
	/**
	 * Contstructs an offline view pool
	 */
	public OfflineView() {
		super();
	}
	
	/**
	 * renders the given View
	 */
	public void render(boolean b) {
		Iterator<BooleanRenderable> views = this.viewComponents.iterator();
		while (views.hasNext()) {
			views.next().render(b);
		}		
	}
	
	/**
	 * @param view adds a new view
	 */
	public void add(BooleanRenderable view) {
		if (this.viewComponents == null) {
			this.viewComponents = new ArrayList<BooleanRenderable>();
		}
		this.viewComponents.add(view);
	}
	
	

}
