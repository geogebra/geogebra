package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.FormatConverterImpl;

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
 * @author Laszlo
 */
public class SymbolicEditorW extends SymbolicEditor implements MathFieldListener, IsWidget {

	private final Kernel kernel;
	private final boolean directFormulaConversion;
	private FlowPanel main;
	private MathFieldW mathField;
	private int fontSize;
	private static final int PADDING_TOP = 16;

	SymbolicEditorW(App app)  {
		super();
		this.kernel = app.getKernel();
		directFormulaConversion = app.has(Feature.MOW_DIRECT_FORMULA_CONVERSION);
		fontSize = app.getSettings().getFontSettings().getAppFontSize() + 1;
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds) {
		mathField.setText(geoInputBox.getTextForEditor(), false);
		mathField.setFontSize(fontSize * geoInputBox.getFontSizeMultiplier());
		Style style = main.getElement().getStyle();
		style.setLeft(bounds.getX(), Style.Unit.PX);
		style.setTop(bounds.getY(), Style.Unit.PX);
		style.setWidth(bounds.getWidth(), Style.Unit.PX);
		style.setHeight(bounds.getHeight() - PADDING_TOP, Style.Unit.PX);

	}

	@Override
	protected void createMathField() {
		main = new FlowPanel();
		Canvas canvas = Canvas.createIfSupported();
		mathField = new MathFieldW(new FormatConverterImpl(kernel), main,
				canvas, this,
				directFormulaConversion,
				null);
		main.addStyleName("evInputEditor");
		main.add(mathField);
	}

	@Override
	public void onEnter() {

	}

	@Override
	public void onKeyTyped() {

	}

	@Override
	public void onCursorMove() {

	}

	@Override
	public void onUpKeyPressed() {

	}

	@Override
	public void onDownKeyPressed() {

	}

	@Override
	public String serialize(MathSequence selectionText) {
		return null;
	}

	@Override
	public void onInsertString() {

	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public void onTab(boolean shiftDown) {

	}

	@Override
	public Widget asWidget() {
		return main;
	}
}
