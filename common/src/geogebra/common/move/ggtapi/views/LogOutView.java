package geogebra.common.move.ggtapi.views;

import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.views.BaseView;
import geogebra.common.move.views.SuccessErrorRenderable;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author gabor
 * View for LogOut changes
 */
public class LogOutView extends BaseView {
	
	/**
	 * Creates a new Login view
	 */
	public LogOutView() {
		super();
	}

	/**
	 * @param response from GGT
	 * sets the successfull response 
	 */
	public void logOutSuccess(JSONObject response) {
		Iterator<SuccessErrorRenderable> views = this.viewComponents.iterator();
		while (views.hasNext()) {
			views.next().success(response);
		}		
	}

	/**
	 * @param response from GGT
	 * Error happened, must be shown if view
	 */
	public void logOutError(JSONObject response) {
		Iterator<SuccessErrorRenderable> views = this.viewComponents.iterator();
		while (views.hasNext()) {
			views.next().fail(response);
		}		
	}
	
	/**
	 * @param view SuccessErrorRenderable view
	 * 
	 * Adds new view to the view's list
	 */
	public void add(SuccessErrorRenderable view) {
		if (viewComponents == null) {
			viewComponents = new ArrayList<SuccessErrorRenderable>();
		}
		viewComponents.add(view);
	}

}
