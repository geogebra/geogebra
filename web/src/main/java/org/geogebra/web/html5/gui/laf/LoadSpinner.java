package org.geogebra.web.html5.gui.laf;

import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

/**
 * Class to wrap load spinner
 *
 * @author laszlo
 */
public class LoadSpinner {
	private final Element spinner;

	/**
	 * Constructor to wrap existsing spinner on the page.
	 *
	 * @param className
	 * 					The classname of the existsing spinner.
	 */
	public LoadSpinner(String className) {
		spinner = Dom.querySelector(className);
	}

	/**
	 * Show spinner.
	 */
	public void show() {
		setDisplay(Style.Display.BLOCK);
	}

	/**
	 * Hide spinner.
	 */
	public void hide() {
		setDisplay(Style.Display.NONE);
	}

	private void setDisplay(Style.Display display) {
		if (spinner == null) {
			return;
		}

		spinner.getStyle().setDisplay(display);

	}
}
