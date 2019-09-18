package org.geogebra.web.full.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.html5.euclidian.InputBoxWidget;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;

/**
 * MathField-capable editor for EV, Web implementation.
 *
 * @author Laszlo
 */
public class SymbolicEditorW implements SymbolicEditor, MathFieldListener,
						InputBoxWidget, BlurHandler {
	public static final int PADDING_LEFT = 2;
	public static final int BORDER_WIDTH = 1;
	private final App app;
	private GeoInputBox geoInputBox;
	private GRectangle bounds;
	private Style style;
	private double top;
	private int mainHeight;
	private String text;
	private int fontSize;
	private MathFieldEditor mathFieldEditor;

	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 */
	public SymbolicEditorW(App app) {
		this.app = app;
		this.fontSize = app.getSettings().
				getFontSettings().getAppFontSize() + 3;
		mathFieldEditor = new MathFieldEditor(app, this);
		mathFieldEditor.addStyleName("evInputEditor");
		mathFieldEditor.setFontSize(fontSize);
		mathFieldEditor.addBlurHandler(this);
		style = mathFieldEditor.getStyle();
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds,
			AbsolutePanel parent) {
		this.geoInputBox = geoInputBox;
		this.bounds = bounds;
		resetChanges();
		if (!mathFieldEditor.asWidget().isAttached()) {
			parent.add(mathFieldEditor.asWidget());
		}
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return null;
	}

	private void resetChanges() {
		boolean wasEditing = geoInputBox.isEditing();
		this.geoInputBox.setEditing(true);
		mathFieldEditor.removeStyleName("hidden");

		updateBounds();
		updateColors();

		if (!wasEditing) {
			updateText();
			updateFont();
			focus();
		}

		mathFieldEditor.setText(text);
		mathFieldEditor.setFontSize(fontSize * geoInputBox.getFontSizeMultiplier());
		mathFieldEditor.focus();
	}

	private void updateColors() {
		GColor bgColor = geoInputBox.getBackgroundColor();
		mathFieldEditor.setBackgroundColor(bgColor != null ? bgColor : GColor.WHITE);
		mathFieldEditor.setForegroundColor(geoInputBox.getObjectColor());
	}

	private void updateText() {
		text = geoInputBox.getTextForEditor().trim();
		mathFieldEditor.setText(text);
	}

	private void updateFont() {
		mathFieldEditor.setFontSize(fontSize * geoInputBox.getFontSizeMultiplier());
	}

	private void focus() {
		mathFieldEditor.focus();
	}

	private void updateBounds() {
		double fieldWidth = bounds.getWidth() - PADDING_LEFT;
		style.setLeft(bounds.getX(), Style.Unit.PX);
		top = bounds.getY();
		style.setTop(top, Style.Unit.PX);
		style.setWidth(fieldWidth, Style.Unit.PX);
		setHeight(bounds.getHeight());
	}

	private void setHeight(double height)  {
		style.setHeight(height, Style.Unit.PX);
		mainHeight = (int) bounds.getHeight();
	}

	@Override
	public boolean isClicked(GPoint point) {
		return geoInputBox.isEditing() && bounds.contains(point.getX(), point.getY());
	}

	@Override
	public void hide() {
		mathFieldEditor.addStyleName("hidden");
		onHide();
	}

	private void onHide() {
		if (!geoInputBox.isEditing()) {
			return;
		}

		applyChanges();
		geoInputBox.setEditing(false);
	}

	@Override
	public void onEnter() {
		applyChanges();
	}

	private void applyChanges() {
		String editedText = mathFieldEditor.getText();
		if (editedText.trim().equals(text)) {
			return;
		}
		geoInputBox.updateLinkedGeo(editedText);
	}

	@Override
	public void onKeyTyped() {
		adjustHeightAndPosition();
		mathFieldEditor.scrollHorizontally();
	}

	@Override
	public void onCursorMove() {

	}

	private void adjustHeightAndPosition() {
		int height = mathFieldEditor.getMathField().
				getInputTextArea().getOffsetHeight();
		double diff = mainHeight - asWidget().getOffsetHeight();
		setHeight(height - 2 * BORDER_WIDTH);
		top += (diff / 2);
		style.setTop(top, Style.Unit.PX);
		geoInputBox.update();
		mainHeight = asWidget().getOffsetHeight();
	}

	@Override
	public void onUpKeyPressed() {
	 	// nothing to do.
	}

	@Override
	public void onDownKeyPressed() {
		// nothing to do.
	}

	@Override
	public String serialize(MathSequence selectionText) {
		return null;
	}

	@Override
	public void onInsertString() {
		// nothing to do.
	}

	@Override
	public boolean onEscape() {
		resetChanges();
		return true;
	}

	@Override
	public void onTab(boolean shiftDown) {
		applyChanges();
		hide();
		app.getGlobalKeyDispatcher().handleTab(false, shiftDown);
		ArrayList<GeoElement> selGeos = app.getSelectionManager().getSelectedGeos();
		GeoElement next = selGeos.isEmpty() ? null : selGeos.get(0);
		if (next instanceof GeoInputBox) {
			app.getActiveEuclidianView().focusTextField((GeoInputBox) next);
		} else {
			app.getActiveEuclidianView().requestFocus();
		}
	}

	@Override
	public Widget asWidget() {
		return mathFieldEditor.asWidget();
	}

	@Override
	public void onBlur(BlurEvent event) {
		hide();
	}
}