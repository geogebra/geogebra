package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.scicalc.LabelHiderCallback;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.inputfield.InputSuggestions;
import org.geogebra.web.html5.gui.util.CancelEventTimer;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * @author Laszlo
 *
 */
public class LatexTreeItemController extends RadioTreeItemController
		implements MathFieldListener, BlurHandler {

	private InputSuggestions sug;
	private RetexKeyboardListener retexListener;

	/**
	 * @param item
	 *            AV item
	 */
	public LatexTreeItemController(RadioTreeItem item) {
		super(item);
	}

	@Override
	protected void startEdit(boolean ctrl) {
		if (item.isInputTreeItem() && item.onEditStart()) {
			setOnScreenKeyboardTextField();
		} else {
			super.startEdit(ctrl);
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (preventBlur) {
			return;
		}

		item.onEnter(false);
		if (item.isEmpty() && item.isInputTreeItem()) {
			item.addDummyLabel();
			item.setItemWidth(item.getAV().getFullWidth());
		}

		if (item.getAV().isNodeTableEmpty()) {
			// #5245#comment:8, cases B and C excluded
			item.updateGUIfocus(true);
		}
	}

	/**
	 * @param keepFocus
	 *            whether focus should stay
	 * @param createSliders
	 *            whether to create sliders
	 */
	public void onEnter(final boolean keepFocus, boolean createSliders) {
		if (item.isInputTreeItem() && item.isEmpty()) {
			item.styleEditor();
			item.addDummyLabel();
			return;
		}

		item.setShowInputHelpPanel(false);
		if (item.geo == null) {
			if (StringUtil.empty(item.getText())) {
				return;
			}
			item.getAV().setLaTeXLoaded();
			createGeoFromInput(keepFocus, createSliders);
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
		}, keepFocus);
	}

	@Override
	public void onEnter() {
		if (isSuggesting()) {
			sug.needsEnterForSuggestion();
			return;
		}
		onEnter(true, false);
		item.getAV().clearActiveItem();
	}

	@Override
	public void onKeyTyped() {
		if (app.getSelectionManager().getSelectedGeos().size() > 0) {
			// to clear preview points
			app.getSelectionManager().clearSelectedGeos();
		}
		item.onKeyTyped();
	}

	@Override
	public void onCursorMove() {
		item.onCursorMove();
	}

	@Override
	public void onUpKeyPressed() {
		if (isSuggesting()) {
			sug.onKeyUp();
		}
	}

	@Override
	public void onDownKeyPressed() {
		if (isSuggesting()) {
			sug.onKeyDown();
		}
	}

	@Override
	public String serialize(MathSequence selectionText) {
		return GeoGebraSerializer.serialize(selectionText);
	}

	@Override
	public void onInsertString() {
		getMathField().setFormula(
				GeoGebraSerializer.reparse(getMathField().getFormula()));
	}

	/**
	 * @return whether suggestions are open
	 */
	public boolean isSuggesting() {
		return sug != null && sug.isSuggesting();
	}

	/**
	 * @param keepFocus
	 *            whether the focus should stay afterwards
	 * @param withSliders
	 *            whether to create sliders
	 */
	public void createGeoFromInput(final boolean keepFocus,
			boolean withSliders) {
		String newValue = item.getText();
		final String rawInput = app.getKernel().getInputPreviewHelper()
				.getInput(newValue);
		boolean textInput = isInputAsText();
		final String input = textInput ? "\"" + rawInput + "\"" : rawInput;

		setInputAsText(false);
		final boolean valid = input.equals(newValue);

		app.setScrollToShow(true);
		final int oldStep = app.getKernel().getConstructionStep();
		AsyncOperation<GeoElementND[]> callback = new AsyncOperation<GeoElementND[]>() {

			@Override
			public void callback(GeoElementND[] geos) {
				if (geos == null) {
					setFocus(true);
					return;
				}

				if (!app.getConfig().hasAutomaticLabels()) {
					new LabelHiderCallback().callback(geos);
				}
				if (geos.length == 1) {
					// need label if we type just eg
					// lnx
					if (!geos[0].isLabelSet()) {
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

					if (geos[0].isGeoText()) {
						geos[0].setEuclidianVisible(false);
					}

					AlgebraItem.addSelectedGeoWithSpecialPoints(geos[0], app);
				}

				InputHelper.updateProperties(geos, app.getActiveEuclidianView(),
						oldStep);
				app.setScrollToShow(false);

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
				item.removeOutput();
				item.runSuggestionCallbacks(geos[0]);
			}
		};
		// keepFocus==false: this was called from blur, don't use modal slider
		// dialog
		ErrorHandler err = null;
		if (!textInput) {
			err = item.getErrorHandler(valid, keepFocus, withSliders);
			err.resetError();
		}
		EvalInfo info = new EvalInfo(true, true).withSliders(true)
				.withFractions(true).addDegree(app.has(Feature.AUTO_ADD_DEGREE))
				.withUserEquation(true)
				.withSymbolicMode(app.getKernel().getSymbolicMode());
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(input, true, err,
						info, callback);
		if (!keepFocus) {
			item.setFocus(false, false);
		}
	}

	/**
	 * @param text
	 *            text to be inserted
	 */
	public void autocomplete(String text) {
		GuiManagerW.makeKeyboardListener(retexListener, null)
				.insertString(text);
	}

	/**
	 * @return keyboard listener
	 */
	public RetexKeyboardListener getRetexListener() {
		return retexListener;
	}

	/**
	 * @param retexListener
	 *            keyboard listener
	 */
	public void setRetexListener(RetexKeyboardListener retexListener) {
		this.retexListener = retexListener;
	}

	/**
	 * Connect keyboard listener to keyboard
	 */
	public void setOnScreenKeyboardTextField() {
		app.getGuiManager().setOnScreenKeyboardTextField(getRetexListener());
		// prevent that keyboard is closed on clicks (changing
		// cursor position)
		CancelEventTimer.keyboardSetVisible();
	}

	@Override
	public void showKeyboard() {
		app.showKeyboard(retexListener);
	}

	/**
	 * @param show
	 *            whether to show keyboard
	 */
	public void initAndShowKeyboard(boolean show) {
		retexListener = new RetexKeyboardListener(item.canvas, getMathField());
		if (show) {
			app.getAppletFrame().showKeyBoard(true, retexListener, false);
		}
	}

	private MathFieldW getMathField() {
		return item.getMathField();
	}

	/**
	 * @return input suggestion model (lazy load)
	 */
	InputSuggestions getInputSuggestions() {
		if (sug == null) {
			sug = new InputSuggestions(app, item);
		}
		return sug;
	}

	@Override
	public boolean onEscape() {
		if (item.geo != null || StringUtil.empty(item.getText())) {
			onBlur(null);
			app.getAccessibilityManager().focusGeo(item.geo);
			return true;
		}
		return false;
	}

	@Override
	public void onTab(boolean shiftDown) {
		onEnter(false, false);
		if (item.isInputTreeItem()) {
			item.addDummyLabel();
			item.setItemWidth(item.getAV().getFullWidth());
		}
		app.hideKeyboard();
		if (shiftDown) {
			app.getAccessibilityManager().focusPrevious(this);
		} else {
			app.getAccessibilityManager().focusNext(this);
		}
	}
}