package org.geogebra.web.html5.gui.laf;

import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

public class LoadSpinner {
	private final Element spinner;

	public LoadSpinner(String className) {
		spinner = Dom.querySelector(className);
	}

	public void show() {
		if (spinner == null) {
			return;
		}

 		spinner.getStyle().setDisplay(Style.Display.BLOCK);
	}

	public void hide() {
		if (spinner == null) {
			return;
		}

		spinner.getStyle().setDisplay(Style.Display.NONE);
	}
}
