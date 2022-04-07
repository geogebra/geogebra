package org.geogebra.common.gui.view.table.dimensions;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

/** Measures text size using ReTeX library */
public class LaTeXTextSizeMeasurer implements TextSizeMeasurer {

	private int fontSize;

	/**
	 * Creates a new LaTeXTextSizeMeasurer
	 * @param fontSize font size
	 */
	public LaTeXTextSizeMeasurer(int fontSize) {
		this.fontSize = fontSize;
	}

	@Override
	public int getWidth(String text) {
		TeXFormula formula = new TeXFormula(text);
		TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontSize);
		icon.setInsets(new Insets(1, 1, 1, 1));
		return icon.getIconWidth();
	}
}
