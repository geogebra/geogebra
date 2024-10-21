package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.gui.view.algebra.GeoSelectionCallback;
import org.geogebra.common.gui.view.algebra.scicalc.LabelHiderCallback;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.gwtproject.core.client.Scheduler;

/**
 * Class to evaluate AV Input row.
 *
 * @author laszlo
 *
 */
public class EvaluateInput {
	private final GeoSelectionCallback selectionCallback;
	RadioTreeItem item;
	App app;
	RadioTreeItemController ctrl;
	private boolean usingValidInput;

	/**
	 * Constructor.
	 *
	 * @param item to evaluate.
	 * @param ctrl the controller.
	 */
	public EvaluateInput(RadioTreeItem item, RadioTreeItemController ctrl,
			GeoSelectionCallback callback) {
		this.item = item;
		this.app = item.getApplication();
		this.ctrl = ctrl;
		this.usingValidInput = true;
		this.selectionCallback = callback;
	}

	/**
	 * Set wether it should use the last valid input,
	 * or the user input
	 * @param usingValidInput use valid input
	 */
	public void setUsingValidInput(boolean usingValidInput) {
		this.usingValidInput = usingValidInput;
	}

	/**
	 * @param keepFocus
	 *          whether the focus should stay afterwards
	 * @param forceSliders
	 *          whether to override default creating sliders
	 */
	public void createGeoFromInput(final boolean keepFocus, boolean forceSliders) {
		evaluate(keepFocus, forceSliders, evaluationCallback(keepFocus));
	}

	/**
	 * @param afterCb
	 *            additional callback that runs after creation.
	 */
	public void createGeoFromInput(final AsyncOperation<GeoElementND[]> afterCb) {
		evaluate(true, false, createEvaluationCallback(afterCb));
	}

	/**
	 * Just evaulate input.
	 * @return the evaulated geo.
	 */
	public GeoElementND evaluateToGeo() {
		return app.getKernel().getAlgebraProcessor().evaluateToGeoElement(item.getText(), false);
	}

	private String getUserInput() {
		return item.getText();
	}

	private String getValidInput(String userInput) {
		return app.getKernel().getInputPreviewHelper()
				.getInput(userInput);
	}

	private String getInput(String userInput, String validInput) {
		String input = usingValidInput ? validInput : userInput;
		boolean textInput = ctrl.isInputAsText();
		return textInput ? "\"" + input + "\"" : input;
	}

	private void evaluate(final boolean keepFocus, boolean forceSliders,
			AsyncOperation<GeoElementND[]> cbEval) {
		String userInput = getUserInput();
		String validInput = getValidInput(userInput);
		String input = getInput(userInput, validInput);
		boolean withSliders = forceSliders || app.getConfig().hasAutomaticSliders();

		ctrl.setInputAsText(false);
		app.setScrollToShow(true);

		ErrorHandler err = null;
		if (!ctrl.isInputAsText()) {
			boolean valid = input.equals(userInput);
			err = item.getErrorHandler(valid, keepFocus, withSliders);
		}
		EvalInfo info = EvalInfoFactory.getEvalInfoForAV(app, withSliders);

		// undo point stored in callback
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(input, false, err,
						info, cbEval);
		if (!keepFocus) {
			item.setFocus(false);
		}
	}

	private AsyncOperation<GeoElementND[]> evaluationCallback(final boolean keepFocus) {
		final int oldStep = app.getKernel().getConstructionStep();
		return geos -> {
			if (geos == null) {
				ctrl.setFocus(true);
				return;
			}

			// TODO none of all this logic should be implemented in here
			if (!app.getConfig().hasAutomaticLabels()) {
				new LabelHiderCallback().callback(geos);
			}
			if (geos.length == 1) {
				// need label if we type just eg
				// lnx
				if (!geos[0].isLabelSet()) {
					geos[0].setLabel(geos[0].getDefaultLabel());
				}

				if (AlgebraItem.isTextItem(geos[0]) && !(geos[0] instanceof AlgoFractionText)) {
					geos[0].setEuclidianVisible(false);
				}
			}
			InputHelper.updateProperties(geos, app.getActiveEuclidianView(),
					oldStep);
			selectionCallback.callback(geos);
			app.storeUndoInfo();
			app.setScrollToShow(false);

			Scheduler.get()
					.scheduleDeferred(() -> {
						item.scrollIntoView();
						if (keepFocus) {
							ctrl.setFocus(true);
						} else {
							item.setFocus(false);
						}
					});

			item.setText("");
			item.removeOutput();
		};
	}

	private AsyncOperation<GeoElementND[]> createEvaluationCallback(
			final AsyncOperation<GeoElementND[]> afterEvalCb) {
		final AsyncOperation<GeoElementND[]> evalCb = evaluationCallback(false);
		return obj -> {
			evalCb.callback(obj);
			afterEvalCb.callback(obj);
		};
	}
}
