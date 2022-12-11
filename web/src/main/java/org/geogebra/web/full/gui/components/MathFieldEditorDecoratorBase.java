package org.geogebra.web.full.gui.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.util.StringUtil;
import org.gwtproject.dom.client.Style;

import com.himamis.retex.editor.web.MathFieldW;

public abstract class MathFieldEditorDecoratorBase {
	protected static final int PADDING_LEFT = 2;
	private final MathFieldW mathField;
	private final Style style;

	/**
	 *
	 * @param editor the mathfield editor to be decorated.
	 */
	public MathFieldEditorDecoratorBase(MathFieldEditor editor) {
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
		style.setLeft(value, Style.Unit.PX);
	}

	/**
	 * Sets top position of the editor.
	 *
	 * @param value to set.
	 */
	public void setTop(double value) {
		style.setTop(value, Style.Unit.PX);
	}

	/**
	 * Sets width of the editor.
	 *
	 * @param value to set.
	 */
	protected void setWidth(double value) {
		style.setWidth(value, Style.Unit.PX);
	}

	/**
	 * Sets height position of the editor.
	 *
	 * @param value to set.
	 */
	protected void setHeight(double value) {
		style.setHeight(value, Style.Unit.PX);
	}

}
