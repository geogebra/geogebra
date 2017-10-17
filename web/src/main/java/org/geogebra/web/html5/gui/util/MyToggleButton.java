package org.geogebra.web.html5.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.web.gui.accessibility.AccessibilityButton;
import org.geogebra.web.web.gui.accessibility.AccessibilityInterface;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * @author csilla
 *
 */
public class MyToggleButton extends ToggleButton implements AccessibilityInterface {

	private App app;
	private Image image = null;
	private AccessibilityButton acc;

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
		acc = new AccessibilityButton(this);
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
		if (acc != null) {
			acc.correctTabIndex();
		}
	}
	
	@Override
	public void addTabHandler(TabHandler handler) {
		acc.addTabHandler(handler);
	}
	
	@Override
	public void ignoreTab() {
		acc.ignoreTab();
	}
}