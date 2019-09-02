package org.geogebra.web.full.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.FormatConverterImpl;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.view.algebra.RetexKeyboardListener;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.euclidian.InputBoxWidget;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.util.EventUtil;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.web.MathFieldScroller;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * MathField-capable editor for EV, Web implementation.
 *
 * @author Laszlo
 */
public class SymbolicEditorW implements SymbolicEditor, MathFieldListener,
		BlurHandler, HasKeyboardPopup, InputBoxWidget {

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
	private String text;
	private RetexKeyboardListener retexListener;

	private Canvas canvas;
	private boolean preventBlur;
	private MathFieldScroller scroller;


	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 */
	public SymbolicEditorW(App app) {
		this.kernel = app.getKernel();
		this.app = app;
		directFormulaConversion = app.has(Feature.MOW_DIRECT_FORMULA_CONVERSION);
		fontSize = app.getSettings().getFontSettings().getAppFontSize() + 3;
		createMathField();

		EventUtil.stopPointer(main.getElement());
		ClickStartHandler.init(main,
				new ClickStartHandler(false, true) {

					@Override
					public void onClickStart(int x, int y,
							PointerEventType type) {
						editorClicked();
					}
				});
	}

	/**
	 * Handle click in the editor.
	 */
	protected void editorClicked() {
		preventBlur = true;
		mathField.requestViewFocus(new Runnable() {

			@Override
			public void run() {
				preventBlur = false;
			}
		});
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
		scroller = new MathFieldScroller(main);
		style = main.getElement().getStyle();
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds,
			AbsolutePanel parent) {
		this.geoInputBox = geoInputBox;
		this.bounds = bounds;
		resetChanges();
		if (!main.isAttached()) {
			parent.add(main);
		}
	}

	private void resetChanges() {
		boolean wasEditing = geoInputBox.isEditing();
		this.geoInputBox.setEditing(true);
		main.removeStyleName("hidden");

		updateBounds();
		updateColors();

		if (!wasEditing) {
			updateText();
			updateFont();
			focus();
		}

		initAndShowKeyboard();
		mathField.setText(text, false);
		mathField.setFontSize(fontSize * geoInputBox.getFontSizeMultiplier());
		mathField.setFocus(true);
	}

	private void updateColors() {
		GColor fgColor = geoInputBox.getObjectColor();
		GColor bgColor = geoInputBox.getBackgroundColor();

		String bgCssColor = toCssColor(bgColor != null ? bgColor : GColor.WHITE);
		main.getElement().getStyle().setBackgroundColor(bgCssColor);
		mathField.setForegroundCssColor(toCssColor(fgColor));
		mathField.setBackgroundCssColor(bgCssColor);
		mathField.setOnBlur(this);
	}

	private void updateText() {
		text = geoInputBox.getTextForEditor().trim();
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
		main.addStyleName("hidden");
		onHide();
	}

	private void onHide() {
		if (!geoInputBox.isEditing()) {
			return;
		}

		applyChanges();
		geoInputBox.setEditing(false);
		setKeyboardVisible(false);
	}

	private void setKeyboardVisible(boolean visible) {
		((AppWFull) app).getAppletFrame().showKeyBoard(visible, retexListener,
				false);
	}

	@Override
	public void onEnter() {
		applyChanges();
	}

	private void applyChanges() {
		String editedText = mathField.getText();
		if (editedText.trim().equals(text)) {
			return;
		}
		geoInputBox.updateLinkedGeo(editedText);
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
		top += (diff / 2);
		style.setTop(top, Style.Unit.PX);
		geoInputBox.update();
		mainHeight = main.getOffsetHeight();
	}

	@Override
	public void onCursorMove() {
		scrollToEnd();
	}

	private void scrollToEnd()  {
		scroller.scrollHorizontallyToCursor(PADDING_LEFT);
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
		return main;
	}

	private void initAndShowKeyboard() {
		retexListener = new RetexKeyboardListener(canvas, mathField);
		setKeyboardVisible(true);
	}

	@Override
	public RetexKeyboardListener getKeyboardListener() {
		return retexListener;
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (!preventBlur) {
			hide();
		}
	}
}
