package org.geogebra.web.full.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.gui.components.MathFieldEditorDecoratorBase;

public class SymbolicEditorDecorator extends MathFieldEditorDecoratorBase {
	private static final double BORDER_WIDTH = 1;
	private final App app;
	private int mainHeight;
	private int top;
	private double fontSize;

	SymbolicEditorDecorator(App app, MathFieldEditor editor) {
		super(editor);
		this.app = app;
	}

	@Override
	public void decorate() {
		this.fontSize = app.getSettings().
				getFontSettings().getAppFontSize() + 3;
		editor.addStyleName("evInputEditor");
		editor.setFontSize(fontSize);
	}

	@Override
	public void updateSize() {
		int height = mathField.
				getInputTextArea().getOffsetHeight();
		double diff = mainHeight - getHeight();
		setHeight(height - 2 * BORDER_WIDTH);
		top += (diff / 2);
		setTop(top);
		mainHeight = getHeight();
	}

	@Override
	public void show() {
		editor.removeStyleName("hidden");
	}

	@Override
	public void hide() {
		editor.addStyleName("hidden");
	}

	@Override
	public double getFontSize() {
		return fontSize;
	}

	void update(GRectangle bounds, GeoInputBox geoInputBox) {
		updateBounds(bounds);
		setForegroundColor(geoInputBox.getObjectColor());
		setBackgroundColor(geoInputBox.getBackgroundColor());
		editor.setFontSize(fontSize * geoInputBox.getFontSizeMultiplier());

	}

	private void updateBounds(GRectangle bounds) {
		double fieldWidth = bounds.getWidth() - PADDING_LEFT;
		setLeft(bounds.getX());
		setTop(bounds.getY());
		setWidth(fieldWidth);
		setHeight(bounds.getHeight());

		top = (int) bounds.getY();
		mainHeight = (int) bounds.getHeight();
	}

}
