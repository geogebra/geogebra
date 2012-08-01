/**
 * 
 */
package geogebra.mobile.gui;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Matthias Meisinger
 * 
 */
public interface Resources extends ClientBundle
{
	public static final Resources INSTANCE = GWT.create(Resources.class);

	@Source("icons/intersect_two_objects.svg")
	SVGResource intersect_two_objects();

	@Source("icons/material-279.ggb")
	DataResource material279();

	@Source("icons/Tux.svg")
	SVGResource tux();

	@Source("icons/Youtube-Logo.png")
	ImageResource youtubeLogo();

	@Source("icons/svg/movement/move.svg")
	SVGResource move();

	@Source("icons/svg/movement/record_to_spreadsheet.svg")
	SVGResource record_to_spreadsheet();

	@Source("icons/svg/movement/rotate_around_point.svg")
	SVGResource rotate_around_point();

}
