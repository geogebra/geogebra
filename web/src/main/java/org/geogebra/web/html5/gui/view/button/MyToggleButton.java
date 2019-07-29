package org.geogebra.web.html5.gui.view.button;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.html5.gui.accessibility.AccessibilityButton;
import org.geogebra.web.html5.gui.accessibility.AccessibilityInterface;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.GToggleButton;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;

/**
 * @author csilla
 *
 */
public class MyToggleButton extends GToggleButton
		implements AccessibilityInterface {

	private App app;
	private Image image = null;
	private AccessibilityButton acc;
	private boolean mayFocus;

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
		acc = new AccessibilityButton(this);
	}

	/**
	 * @param upFace
	 *            upface img resource
	 * @param downFace
	 *            downface img resource
	 */
	public void setUpfaceDownfaceImg(SVGResource upFace,
			SVGResource downFace) {
		this.getUpFace().setImage(new NoDragImage(upFace, 24));
		this.getDownFace().setImage(new NoDragImage(downFace, 24));
	}

	@Override
	public void setTitle(String title) {
		AriaHelper.setTitle(this, title, app);
	}

	/**
	 * Sets alternate text for button.
	 * 
	 * @param alt
	 *            to set
	 */
	@Override
	public void setAltText(String alt) {
		if (image == null) {
			return;
		}
		AriaHelper.setLabel(this, alt);
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
	public void onBrowserEvent(Event event) {
		if (acc != null && acc.handleBrowserEvent(event)) {
			return;
		}
		this.mayFocus = false;
		super.onBrowserEvent(event);
		this.mayFocus = true;
	}
	
	@Override
	public void setFocus(boolean focus) {
		if (mayFocus) {
			super.setFocus(focus);
		} else {
			((AppW) app).getArticleElement().getElement().focus();
		}
	}

	@Override
	public void addTabHandler(TabHandler handler) {
		acc.addTabHandler(handler);
	}
	
	@Override
	public void setIgnoreTab() {
		acc.setIgnoreTab();
	}
}