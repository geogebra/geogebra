package org.geogebra.web.full.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.FormatConverterImpl;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.view.algebra.RetexKeyboardListener;
import org.geogebra.web.full.main.AppWFull;

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
	private static final int PADDING_TOP = 16;
	private static final int PADDING_LEFT = 2;
	private GeoInputBox geoIntputBox;
	private GRectangle bounds;
	private Style style;
	private double top;
	private int mainHeight;
	private RetexKeyboardListener retexListener;
	private Canvas canvas;

	/**
	 * Constructor
	 *
	 * @param app
	 * 			The application.
	 */
	public SymbolicEditorW(App app)  {
		this.kernel = app.getKernel();
		this.app = app;
		directFormulaConversion = app.has(Feature.MOW_DIRECT_FORMULA_CONVERSION);
		fontSize = app.getSettings().getFontSettings().getAppFontSize() + 2;
		createMathField();
	}

	private void createMathField() {
		main = new FlowPanel();
		canvas = Canvas.createIfSupported();
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
		this.geoIntputBox = geoInputBox;
		this.geoIntputBox.setEditing(true);
		this.bounds = bounds;
		String text = geoInputBox.getTextForEditor();
		main.removeStyleName("hidden");
		updateBounds(bounds);
		updateColors();
		initAndShowKeyboard(true);
		mathField.setText(text, false);
		mathField.setFontSize(fontSize * geoInputBox.getFontSizeMultiplier());
		mathField.setFocus(true);
	}

	private void updateColors() {
		GColor fgColor = geoIntputBox.getObjectColor();
		GColor bgColor = geoIntputBox.getBackgroundColor();

		String bgCssColor = toCssColor( bgColor != null ? bgColor : GColor.WHITE);
		main.getElement().getStyle().setBackgroundColor(bgCssColor);
		mathField.setForegroundCssColor(toCssColor(fgColor));
		mathField.setBackgroundCssColor(bgCssColor);
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
	public boolean isAttached(GPoint point) {
		return geoIntputBox.isEditing() && bounds.contains(point.getX(), point.getY());
	}

	@Override
	public void hide() {
		main.addStyleName("hidden");
		geoIntputBox.setEditing(false);
	}

	@Override
	public void onEnter() {
		applyChanges();
	}

	private void applyChanges() {
		geoIntputBox.updateLinkedGeo(mathField.getText());
	}

	@Override
	public void onKeyTyped() {
		adjustHeightAndPosition();
		scrollToEnd();
	}

	private void adjustHeightAndPosition() {
		int height = mathField.getInputTextArea().getOffsetHeight();
		double diff = mainHeight - main.getOffsetHeight();
		setHeight(height - PADDING_TOP - 2 * BORDER_WIDTH);
		top += (diff/2);
		style.setTop(top, Style.Unit.PX);
		geoIntputBox.update();
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
		attach(geoIntputBox, bounds);
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

	/**
	 * @param show
	 *            whether to show keyboard
	 */
	public void initAndShowKeyboard(boolean show) {
		retexListener = new RetexKeyboardListener(canvas, mathField);
		if (show) {
			((AppWFull)app).getAppletFrame().showKeyBoard(true, retexListener, false);
		}
	}


}
