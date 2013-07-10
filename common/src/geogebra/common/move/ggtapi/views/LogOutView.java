package geogebra.common.move.ggtapi.views;

import geogebra.common.move.views.BaseView;
import geogebra.common.move.views.Renderable;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author gabor
 * View for LogOut changes
 */
public class LogOutView extends BaseView {
	
	/**
	 * Creates a new LoginOut view
	 */
	public LogOutView() {
		super();
	}
	
	/**
	 * @param view adds a new View to viewcomponents
	 */
	public void add(Renderable view) {
		if (this.viewComponents == null) {
			this.viewComponents = new ArrayList<Renderable>();
		}
		this.viewComponents.add(view);
	}
	
	/**
	 * renders the attached views
	 */
	public void render() {
		Iterator<Renderable> view = this.viewComponents.iterator();
		while (view.hasNext()) {
			view.next().render();
		}
	}
	

}
