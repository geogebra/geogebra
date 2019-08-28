package org.geogebra.web.full.evaluator;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.FormatConverterImpl;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * Evaluator Web implementation.
 *
 * @author Laszlo
 */
public class EvaluatorEditor implements IsWidget, MathFieldListener {

	private final Kernel kernel;
	private final boolean directFormulaConversion;
	private FlowPanel main;
	private static final int PADDING_LEFT = 2;

	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 */
	public EvaluatorEditor(App app) {
		this.kernel = app.getKernel();
		directFormulaConversion = app.has(Feature.MOW_DIRECT_FORMULA_CONVERSION);
		createMathField();
	}


	private void createMathField() {
		main = new FlowPanel();
		Canvas canvas = Canvas.createIfSupported();
		MathFieldW mathField = new MathFieldW(new FormatConverterImpl(kernel), main,
				canvas, this,
				directFormulaConversion,
				null);
		main.addStyleName("evaluatorEditor");
		main.add(mathField);
	}

	@Override
	public void onEnter() {

	}

	@Override
	public void onKeyTyped() {
		adjustHeightAndPosition();
		scrollToEnd();
	}

	private void adjustHeightAndPosition() {
	}

	@Override
	public void onCursorMove() {
		scrollToEnd();
	}

	private void scrollToEnd() {
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
		return true;
	}

	@Override
	public void onTab(boolean shiftDown) {
	}

	@Override
	public Widget asWidget() {
		return main;
	}

}
