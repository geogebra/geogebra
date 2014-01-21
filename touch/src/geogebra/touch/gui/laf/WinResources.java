package geogebra.touch.gui.laf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

interface WinResources extends DefaultResources {

	static WinResources INSTANCE = GWT.create(WinResources.class);

	@Override
	@Source("icons/png/win/icon_warning.png")
	ImageResource icon_warning();

	@Override
	@Source("icons/png/win/elem_radioButtonActive.png")
	ImageResource radioButtonActive();

	@Override
	@Source("icons/png/win/elem_radioButtonInactive.png")
	ImageResource radioButtonInactive();

}
