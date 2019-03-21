package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.scicalc.LabelHiderCallback;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

import com.google.gwt.core.client.Scheduler;

/**
 * Class to evaluate AV Input row.
 *
 * @author laszlo
 *
 */
public class EvaluateInput {
	RadioTreeItem item;
	App app;
	LatexTreeItemController ctrl;

	/**
	 * Constructor.
	 *
	 * @param item to evaluate.
	 * @param ctrl the controller.
	 * @param app {@Link App}
	 */
	public EvaluateInput(RadioTreeItem item, LatexTreeItemController ctrl) {
		this.item = item;
		this.app = item.getApplication();
		this.ctrl = ctrl;
	}

	/**
	 * @param keepFocus
	 *            whether the focus should stay afterwards
	 * @param withSliders
	 *            whether to create sliders
	 */
	public void createGeoFromInput(final boolean keepFocus,
			boolean withSlider) {
		evaluate(keepFocus, withSlider, createCallback(keepFocus));
	}

	/**
	 * @param afterCb 
	 * 				additional callback that runs after creation.
	 */
	public void createGeoFromInput(final AsyncOperation<GeoElementND[]> afterCb) {
		evaluate(true, false, createCallback(afterCb));
	}

	/**
	 * Just evaulate input.
	 * @return the evaulated geo.
	 */
	public GeoElementND evaluateToGeo() {
		return app.getKernel().getAlgebraProcessor().evaluateToGeoElement(item.getText(), false);
	}

	private void evaluate(final boolean keepFocus,
			boolean withSliders, AsyncOperation<GeoElementND[]> cbEval) {
		String newValue = item.getText();
		final String rawInput = app.getKernel().getInputPreviewHelper()
				.getInput(newValue);
		boolean textInput = ctrl.isInputAsText();
		final String input = textInput ? "\"" + rawInput + "\"" : rawInput;

		ctrl.setInputAsText(false);
		final boolean valid = input.equals(newValue);

		app.setScrollToShow(true);

		ErrorHandler err = null;
		if (!textInput) {
			err = item.getErrorHandler(valid, keepFocus, withSliders);
			err.resetError();
		}
		EvalInfo info = new EvalInfo(true, true).withSliders(true)
				.withFractions(true).addDegree(app.has(Feature.AUTO_ADD_DEGREE))
				.withUserEquation(true)
				.withSymbolicMode(app.getKernel().getSymbolicMode());
		// undo point stored in callback
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(input, false, err,
						info, cbEval);
		if (!keepFocus) {
			item.setFocus(false, false);
		}
}

	private AsyncOperation<GeoElementND[]> createCallback(final boolean keepFocus) {
		final int oldStep = app.getKernel().getConstructionStep();
		return new AsyncOperation<GeoElementND[]>() {

			@Override
			public void callback(GeoElementND[] geos) {
				if (geos == null) {
					ctrl.setFocus(true);
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

					if (AlgebraItem.isTextItem(geos[0]) && !(geos[0] instanceof AlgoFractionText)) {
						geos[0].setEuclidianVisible(false);
					}

					AlgebraItem.addSelectedGeoWithSpecialPoints(geos[0], app);
				}

				InputHelper.updateProperties(geos, app.getActiveEuclidianView(),
						oldStep);
				app.storeUndoInfo();
				app.setScrollToShow(false);

				Scheduler.get()
						.scheduleDeferred(new Scheduler.ScheduledCommand() {
							@Override
							public void execute() {
								item.scrollIntoView();
								if (keepFocus) {
									ctrl.setFocus(true);
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
	}

	private AsyncOperation<GeoElementND[]> createCallback(final AsyncOperation<GeoElementND[]> 
		createAfterCb) {
		final AsyncOperation<GeoElementND[]>  ceateCb = createCallback(false);
		return new AsyncOperation<GeoElementND[]>() {
			
			@Override
			public void callback(GeoElementND[] obj) {
				ceateCb.callback(obj);
				createAfterCb.callback(obj);
			}
		};
	}	
}
