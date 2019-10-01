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
     * @param className The classname of the existsing spinner.
     */
    public LoadSpinner(String className) {
        spinner = Dom.querySelector(className);
    }

    /**
     * Show spinner.
     */
    public void show() {
        setSpinnerVisibility(true);
    }

    /**
     * Hide spinner.
     */
    public void hide() {
        setSpinnerVisibility(false);
    }

    private void setSpinnerVisibility(boolean visible) {
        if (spinner == null) {
            return;
        }

        spinner.getStyle().setDisplay(visible ? Style.Display.BLOCK : Style.Display.NONE);

    }
}
