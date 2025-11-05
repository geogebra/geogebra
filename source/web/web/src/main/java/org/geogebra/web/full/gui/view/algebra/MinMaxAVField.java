package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.App;
import org.geogebra.editor.share.util.GWTKeycodes;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.KeyUpEvent;

/**
 * Input field for MinMaxPanel
 */
class MinMaxAVField extends AutoCompleteTextFieldW {

	private final MinMaxPanel widgets;

	/**
	 * @param columns field width
	 * @param app application
	 */
	public MinMaxAVField(MinMaxPanel widgets, int columns, App app) {
		super(columns, app);
		this.widgets = widgets;
		enableGGBKeyboard();
		prepareShowSymbolButton(false);
	}

	@Override
	public void onKeyPress(KeyPressEvent e) {
		if (Browser.isTabletBrowser()) {
			super.onKeyPress(e);
		}
		e.stopPropagation();
	}

	@Override
	public void onKeyDown(KeyDownEvent e) {
		if (Browser.isTabletBrowser()) {
			super.onKeyDown(e);
		}
		e.stopPropagation();
		if (e.getNativeKeyCode() == GWTKeycodes.KEY_ESCAPE) {
			widgets.hide();
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent e) {
		e.stopPropagation();
	}
}
