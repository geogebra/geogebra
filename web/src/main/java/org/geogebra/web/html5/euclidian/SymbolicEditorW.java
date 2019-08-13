package org.geogebra.web.html5.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.FormatConverterImpl;
import org.geogebra.common.util.StringUtil;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * MathField-capable editor for EV, Web implementation.
 *
 * @author Laszlo
 */
public class SymbolicEditorW implements SymbolicEditor, MathFieldListener, IsWidget {

	public static final int ROUNDING = 8;
	private static final int BORDER_WIDTH = 2;
	private final Kernel kernel;
	private final boolean directFormulaConversion;
	private final App app;
	private FlowPanel main;
	private MathFieldW mathField;
	private int fontSize;
	private static final int PADDING_LEFT = 2;
	private GeoInputBox geoInputBox;
	private GRectangle bounds;
	private Style style;
	private double top;
	private int mainHeight;

	/**
	 * Constructor
	 *
	 * @param app
	 * 			The application.
	 */
	SymbolicEditorW(App app)  {
		this.kernel = app.getKernel();
		this.app = app;
		directFormulaConversion = app.has(Feature.MOW_DIRECT_FORMULA_CONVERSION);
		fontSize = app.getSettings().getFontSettings().getAppFontSize() + 3;
		createMathField();
	}

	private void createMathField() {
		main = new FlowPanel();
		Canvas canvas = Canvas.createIfSupported();
		mathField = new MathFieldW(new FormatConverterImpl(kernel), main,
				canvas, this,
				directFormulaConversion,
				null);
		main.addStyleName("evInputEditor");
		main.add(mathField);
		style = main.getElement().getStyle();
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds) {
		this.geoInputBox = geoInputBox;
		boolean wasEditing = geoInputBox.isEditing();
		this.geoInputBox.setEditing(true);
		main.removeStyleName("hidden");

		updateBounds(bounds);
		updateColors();

		if (!wasEditing) {
			updateText();
			updateFont();
			focus();
		}

	}

	private void updateColors() {
		GColor fgColor = geoInputBox.getObjectColor();
		GColor bgColor = geoInputBox.getBackgroundColor();

		String bgCssColor = toCssColor( bgColor != null ? bgColor : GColor.WHITE);
		main.getElement().getStyle().setBackgroundColor(bgCssColor);
		mathField.setForegroundCssColor(toCssColor(fgColor));
		mathField.setBackgroundCssColor(bgCssColor);
	}

	private void updateText() {
		String text = geoInputBox.getTextForEditor();
		mathField.setText(text, false);
	}

	private void updateFont() {
		mathField.setFontSize(fontSize * geoInputBox.getFontSizeMultiplier());
	}

	private void focus() {
		mathField.setFocus(true);
	}

	private static String toCssColor(GColor color) {
		return "#" + StringUtil.toHexString(color);
	}

	private void updateBounds(GRectangle bounds) {
		this.bounds = bounds;
		double fieldWidth = bounds.getWidth() - PADDING_LEFT;
		style.setLeft(bounds.getX(), Style.Unit.PX);
		top = bounds.getY();
		style.setTop(top, Style.Unit.PX);
		style.setWidth(fieldWidth, Style.Unit.PX);
		setHeight(bounds.getHeight() - 2 * BORDER_WIDTH);
	}

	private void setHeight(double height)  {
		style.setHeight(height, Style.Unit.PX);
		mainHeight = (int) bounds.getHeight();
	}

	@Override
	public void hide() {
		main.addStyleName("hidden");
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
		geoInputBox.updateLinkedGeo(mathField.getText());
	}

	@Override
	public void onKeyTyped() {
		adjustHeightAndPosition();
		scrollToEnd();
	}

	private void adjustHeightAndPosition() {
		int height = mathField.getInputTextArea().getOffsetHeight();
		double diff = mainHeight - main.getOffsetHeight();
		setHeight(height - 2 * BORDER_WIDTH);
		top += (diff/2);
		style.setTop(top, Style.Unit.PX);
		geoInputBox.update();
		mainHeight = main.getOffsetHeight();
	}

	@Override
	public void onCursorMove() {
		scrollToEnd();
	}

	private void scrollToEnd()  {
		MathFieldW.scrollParent(main, PADDING_LEFT);
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

	private void resetChanges() {
		attach(geoInputBox, bounds);
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
		return main;
	}
}
