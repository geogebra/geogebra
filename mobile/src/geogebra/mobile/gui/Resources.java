package geogebra.mobile.gui;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

interface Resources extends ClientBundle
{
	public static final Resources INSTANCE = GWT.create(Resources.class);

	@Source("../mobile/resources/Youtube-Logo.png")
	ImageResource logo();

	@Source("../mobile/resources/Tux.svg")
	SVGResource tux();
	
	@Source("../common/resources/icons/svg/1_movement/move.svg")
	SVGResource move();
}