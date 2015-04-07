package org.geogebra.web.phone.gui.view;

import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.images.PerspectiveResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Some common methods.
 */
public abstract class AbstractStyleBar implements StyleBar {

	private IsWidget styleBar;
	private ImageResource styleBarIcon;

	public IsWidget getStyleBar() {
		if (styleBar == null) {
			styleBar = createStyleBar();
		}
		return styleBar;
	}

	public ImageResource getStyleBarIcon() {
		if (styleBarIcon == null) {
			styleBarIcon = createStyleBarIcon();
		}
		return styleBarIcon;
	}
	
	/**
	 * Class providing the style bar icons.
	 */
	protected PerspectiveResources resources = ((ImageFactory)GWT.create(ImageFactory.class)).getPerspectiveResources();

	protected abstract IsWidget createStyleBar();

	protected abstract ImageResource createStyleBarIcon();
}
