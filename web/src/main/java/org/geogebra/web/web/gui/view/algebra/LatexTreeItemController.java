package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.inputfield.InputSuggestions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.web.MathFieldW;

public class LatexTreeItemController extends RadioTreeItemController
		implements MathFieldListener, BlurHandler {

	private InputSuggestions sug;
	private RetexKeyboardListener retexListener;

	public LatexTreeItemController(RadioTreeItem item) {
		super(item);
	}

	@Override
	protected void startEdit(boolean substituteNumbers) {
		LatexTreeItem li = getLatexTreeItem();
		if (li.isInputTreeItem() && li.onEditStart(false)) {
			setOnScreenKeyboardTextField();
		} else {
			super.startEdit(substituteNumbers);
		}
	}

	public void onBlur(BlurEvent event) {
		if (item.isEmpty() && item.isInputTreeItem()) {
			item.addDummyLabel();
		}

		if (((AlgebraViewW) item.getAV()).isNodeTableEmpty()) {
			// #5245#comment:8, cases B and C excluded
			item.updateGUIfocus(event == null ? this : event.getSource(), true);
		}
	}

	public void onEnter(final boolean keepFocus) {
		if (app.has(Feature.AV_SINGLE_TAP_EDIT) && item.isInputTreeItem()
				&& item.isEmpty()) {
			item.styleEditor();
			item.addDummyLabel();
			return;
		}

		if (item.geo == null) {
			if (StringUtil.empty(item.getText())) {
				return;
			}
			createGeoFromInput(keepFocus);

			return;
		}
		if (!isEditing()) {
			return;
		}

		item.stopEditing(item.getText(), new AsyncOperation<GeoElementND>() {

			@Override
			public void callback(GeoElementND obj) {
				if (obj != null && !keepFocus) {
					if (app.has(Feature.AUTOSCROLLING_SPREADSHEET)) {
						app.setScrollToShow(true);
					}
					obj.update();
				}
			}
		});
	}
	public void onEnter() {
		if (isSuggesting()) {
			sug.needsEnterForSuggestion();
			return;
		}
		onEnter(!item.hasGeo());
	}

	public void onKeyTyped() {
		getLatexTreeItem().onKeyTyped();
	}

	public void onCursorMove() {
		getLatexTreeItem().onCursorMove();
	}

	public void onUpKeyPressed() {
		if (isSuggesting()) {
			sug.onKeyUp();
		}

	}


	public void onDownKeyPressed() {
		if (isSuggesting()) {
			sug.onKeyDown();
		}
	}

	public String alt(int unicodeKeyChar, boolean shift) {
		return getRetexListener().alt(unicodeKeyChar, shift);
	}

	public String serialize(MathSequence selectionText) {
		return GeoGebraSerializer.serialize(selectionText);
	}

	public void onInsertString() {
		getMathField().setFormula(
				GeoGebraSerializer.reparse(getMathField().getFormula()));

	}

	public boolean isSuggesting() {
		return sug != null && sug.isSuggesting();
	}

	private void createGeoFromInput(final boolean keepFocus) {
		String newValue = item.getText();
		final String input = app.getKernel().getInputPreviewHelper()
				.getInput(newValue);
		final boolean valid = input.equals(newValue);

		app.setScrollToShow(true);

		AsyncOperation<GeoElementND[]> callback = new AsyncOperation<GeoElementND[]>() {

			@Override
			public void callback(GeoElementND[] geos) {

				if (geos == null) {
					// inputField.getTextBox().setFocus(true);
					setFocus(true);
					return;
				}

				// need label if we type just eg
				// lnx
				if (geos.length == 1 && !geos[0].isLabelSet()) {
					geos[0].setLabel(geos[0].getDefaultLabel());
				}

				InputHelper.updateProperties(geos,
						app.getActiveEuclidianView());
				app.setScrollToShow(false);
				/**
				 * if (!valid) { addToHistory(input, null);
				 * addToHistory(newValueF, latexx); } else { addToHistory(input,
				 * latexx); }
				 */

				Scheduler.get()
						.scheduleDeferred(new Scheduler.ScheduledCommand() {
							@Override
							public void execute() {
								item.scrollIntoView();
								if (keepFocus) {
									setFocus(true);
								} else {
									item.setFocus(false, true);
								}

							}
						});

				item.setText("");

				item.updateLineHeight();
			}

		};
		ErrorHandler err = item.getErrorHandler(valid);
		err.resetError();
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(input, true, err,
						true, callback);

	}

	public void autocomplete(String text) {
		GuiManagerW.makeKeyboardListener(retexListener).insertString(text);

	}

	public RetexKeyboardListener getRetexListener() {
		return retexListener;
	}

	public void setRetexListener(RetexKeyboardListener retexListener) {
		this.retexListener = retexListener;
	}

	public void setOnScreenKeyboardTextField() {
		app.getGuiManager().setOnScreenKeyboardTextField(getRetexListener());
		// prevent that keyboard is closed on clicks (changing
		// cursor position)
		CancelEventTimer.keyboardSetVisible();
	}
	public void showKeyboard() {
		app.showKeyboard(retexListener);

	}
	
	public void initAndShowKeyboard(boolean show) {
		retexListener = new RetexKeyboardListener(item.canvas, getMathField());
		if (show) {
			app.getAppletFrame().showKeyBoard(true, retexListener, false);
		}

	}

	private LatexTreeItem getLatexTreeItem() {
		return (LatexTreeItem) item;
	}

	private MathFieldW getMathField() {
		return getLatexTreeItem().getMathField();
	}

	InputSuggestions getInputSuggestions() {
		if (sug == null) {
			sug = new InputSuggestions(app, item);
		}
		return sug;
	}
}