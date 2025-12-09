/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.web.MathFieldW;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;

public class MathFieldEditorDecoratorBase {
	protected static final int PADDING_LEFT = 2;
	private final MathFieldW mathField;
	private final Style style;

	/**
	 *
	 * @param editor the mathfield editor to be decorated.
	 */
	protected MathFieldEditorDecoratorBase(MathFieldEditor editor) {
		this.mathField = editor.getMathField();
		this.style = editor.getStyle();
	}

	/**
	 * Sets background color for the editor
	 *
	 * @param backgroundColor {@link GColor}
	 */
	protected void setBackgroundColor(GColor backgroundColor) {
		GColor color = backgroundColor != null
				? backgroundColor
				: GColor.WHITE;
		String cssColor = StringUtil.toHtmlColor(color);
		style.setBackgroundColor(cssColor);
	}

	/**
	 * Sets foreground color for the editor
	 *
	 * @param foregroundColor {@link GColor}
	 */
	protected void setForegroundColor(GColor foregroundColor) {
		mathField
				.setForegroundColor(StringUtil.toHtmlColor(foregroundColor));
	}

	/**
	 * Sets the font size of the editor.
	 *
	 * @param fontSize to set.
	 */
	public void setFontSize(double fontSize) {
		mathField.setFontSize(fontSize);
	}

	/**
	 * Sets left position of the editor.
	 *
	 * @param value to set.
	 */
	protected void setLeft(double value) {
		style.setLeft(value, Unit.PX);
	}

	/**
	 * Sets top position of the editor.
	 *
	 * @param value to set.
	 */
	public void setTop(double value) {
		style.setTop(value, Unit.PX);
	}

	/**
	 * Sets width of the editor.
	 *
	 * @param value to set.
	 */
	protected void setWidth(double value) {
		style.setWidth(value, Unit.PX);
	}

	/**
	 * Sets height position of the editor.
	 *
	 * @param value to set.
	 */
	protected void setHeight(double value) {
		style.setHeight(value, Unit.PX);
	}

}
