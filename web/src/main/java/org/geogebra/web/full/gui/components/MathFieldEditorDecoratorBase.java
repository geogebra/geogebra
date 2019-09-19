package org.geogebra.web.full.gui.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.util.StringUtil;

import com.google.gwt.dom.client.Style;
import com.himamis.retex.editor.web.MathFieldW;

public class MathFieldEditorDecoratorBase implements MathFieldEditorDecorator {
	public static final int PADDING_LEFT = 2;
	protected final MathFieldEditor editor;
	protected final MathFieldW mathField;
	private final Style style;

	public MathFieldEditorDecoratorBase(MathFieldEditor editor) {
		this.editor = editor;
		this.mathField = editor.getMathField();
		this.style = editor.getStyle();
	}

	@Override
	public void setBackgroundColor(GColor backgroundColor) {
		String cssColor = toCssColor(backgroundColor);
		style.setBackgroundColor(cssColor);
		mathField.setBackgroundCssColor(cssColor);
	}

	private static String toCssColor(GColor color) {
		return "#" + StringUtil.toHexString(color);
	}

	@Override
	public void setForegroundColor(GColor foregroundColor) {
		mathField.setForegroundCssColor(toCssColor(foregroundColor));
	}

	@Override
	public void updateSize() {
		// default is no update.
	}

	@Override
	public void setFontSize(double fontSize) {
		mathField.setFontSize(fontSize);
	}

	@Override
	public void updateBounds(GRectangle bounds) {
		double fieldWidth = bounds.getWidth() - PADDING_LEFT;
		setLeft(bounds.getX());
		setTop(bounds.getY());
		setWidth(fieldWidth);
		setHeight(bounds.getHeight());
	}

	protected void setLeft(double value) {
		style.setLeft(value, Style.Unit.PX);
	}
	
	protected void setTop(double value) {
		style.setTop(value, Style.Unit.PX);
	}
	
	protected void setWidth(double value) {
		style.setWidth(value, Style.Unit.PX);
	}
	protected void setHeight(double value) {
		style.setHeight(value, Style.Unit.PX);
	}

	protected int getHeight() {
		return editor.asWidget().getOffsetHeight();
	}
}
