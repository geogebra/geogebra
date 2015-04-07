package org.geogebra.web.html5.awt;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * @author michael
 * 
 *         Added to allow ctx.fill("evenodd") (new winding rule from ggb50)
 *         ignored in IE9, IE10
 *
 */
public class MyContext2d extends Context2d {

	protected MyContext2d() {

	}

	/**
	 * Fills the current path.
	 */
	public final native void fill(String windingRule) /*-{
		this.fill(windingRule);
	}-*/;

}
