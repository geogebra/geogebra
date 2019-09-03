package org.geogebra.web.full.evaluator;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.components.MathFieldEditor;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;

/**
 * Evaluator Web implementation.
 *
 * @author Laszlo
 */
public class EvaluatorEditor implements IsWidget, MathFieldListener {

	private MathFieldEditor mathFieldEditor;

	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 */
	public EvaluatorEditor(App app) {
		mathFieldEditor = new MathFieldEditor(app, this);
		mathFieldEditor.addStyleName("evaluatorEditor");
	}

	@Override
	public void onEnter() {
		// TODO: implement this
	}

	@Override
	public void onKeyTyped() {
		scrollContentIfNeeded();
	}

	@Override
	public void onCursorMove() {
		scrollContentIfNeeded();
	}

	private void scrollContentIfNeeded() {
		mathFieldEditor.scrollHorizontally();
		mathFieldEditor.scrollVertically();
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
		scrollContentIfNeeded();
	}

	@Override
	public boolean onEscape() {
		return true;
	}

	@Override
	public void onTab(boolean shiftDown) {
		// TODO: implement this.
	}

	@Override
	public Widget asWidget() {
		return mathFieldEditor.asWidget();
	}

	public void requestFocus() {
		mathFieldEditor.requestFocus();
	}
}
