package org.geogebra.web.html5.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * @author csilla
 *
 */
public class MyToggleButton extends ToggleButton {

	private App app;
	private Image image = null;
	private boolean ignoreTab = false;

	/**
	 * @param image
	 *            an {@link Image} to use as an up Image
	 * @param app
	 *            application
	 */
	public MyToggleButton(Image image, App app) {
		super(image);
		this.image = image;
		this.app = app;
	}

	/**
	 * @param app
	 *            application
	 */
	public MyToggleButton(App app) {
		super();
		this.app = app;
	}


	@Override
	public void setTitle(String title) {
		if (app.has(Feature.TOOLTIP_DESIGN) && !Browser.isMobile()) {
			getElement().removeAttribute("title");
			if (!"".equals(title)) {
				getElement().setAttribute("data-title", title);
			}
		} else {
			super.setTitle(title);
		}
	}

	/**
	 * Sets alternate text for button.
	 * 
	 * @param alt
	 *            to set
	 */
	public void setAltText(String alt) {
		if (image == null) {
			return;
		}
		image.setAltText(alt);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		if (ignoreTab) {
			setTabIndex(-1);
		}
	}

	/**
	 * 
	 * @return if button should ignore tab key.
	 */
	public boolean isIgnoreTab() {
		return ignoreTab;
	}

	/**
	 * FocusWidget sets tabIndex -1 to 0 automatically for accessibility
	 * reasons. Call this to ignore this default behavior and really ignore tab
	 * key.
	 * 
	 */
	public void ignoreTab() {
		this.ignoreTab = true;
	}
}