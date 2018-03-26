package org.geogebra.web.full.util;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.cas.view.CASLaTeXEditor;
import org.geogebra.web.full.cas.view.CASTableControllerW;
import org.geogebra.web.full.cas.view.CASTableW;
import org.geogebra.web.full.gui.view.algebra.CheckboxTreeItem;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.gui.view.algebra.SliderTreeItemRetex;
import org.geogebra.web.full.gui.view.algebra.TextTreeItem;
import org.geogebra.web.html5.main.AppW;

import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * Factory class for ReTeX based editor
 */
public class ReTeXHelper implements LaTeXHelper {

	@Override
	public CASTableCellEditor getCASEditor(CASTableW table, AppW app,
			CASTableControllerW ml) {
		return new CASLaTeXEditor(table, app, ml);
	}

	@Override
	public RadioTreeItem getAVItem(GeoElement ob) {
		return new RadioTreeItem(ob);
	}

	@Override
	public RadioTreeItem getAVInput(Kernel kernel) {
		return new RadioTreeItem(kernel).initInput();
	}

	@Override
	public RadioTreeItem getSliderItem(GeoElement ob) {
		return new SliderTreeItemRetex(ob);
	}

	@Override
	public RadioTreeItem getCheckboxItem(GeoElement ob) {
		return new CheckboxTreeItem(ob);
	}

	@Override
	public RadioTreeItem getTextItem(GeoElement ob) {
		return new TextTreeItem(ob);
	}

	private static void parseText(String text0, MathFieldW mf) {
		Parser parser = new Parser(mf.getMetaModel());
		MathFormula formula;
		try {
			formula = parser.parse(text0);
			mf.setFormula(formula);
		} catch (ParseException e) {
			Log.warn("Problem parsing: " + text0);
			e.printStackTrace();
		}
	}

	/**
	 * @param mf
	 *            editor
	 * @param text0
	 *            text
	 * @param asPlainText
	 *            whether to use it as plain text
	 */
	public static void setText(MathFieldW mf, String text0, boolean asPlainText) {
		if (asPlainText && mf != null) {
			parseText("", mf);
			mf.setPlainTextMode(true);
			mf.insertString(text0);
		} else if (mf != null) {
			parseText(text0, mf);
		}
	}
}
