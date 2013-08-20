package geogebra.common.move.ggtapi.views;

import geogebra.common.move.views.BaseView;
import geogebra.common.move.views.Renderable;

import java.util.Iterator;

/**
 * @author gabor
 * View for LogOut changes
 */
public class LogOutView extends BaseView<Renderable> {
	
	/**
	 * Creates a new LoginOut view
	 */
	public LogOutView() {
		super();
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
