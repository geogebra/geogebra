package org.geogebra.web.solver;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.web.editor.AppWsolver;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.DrawEquationW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Exercise {

	private AppWsolver app;
	private AbsolutePanel rootPanel;
	private VerticalPanel dataPanel;

	private ProgressBar progressBar;
	private Canvas hint;

	private int initialComplexity;
	private int previousComplexity;

	/**
	 * @param app
	 *            application
	 * @param rootPanel
	 *            parent panel
	 */
	Exercise(AppWsolver app, AbsolutePanel rootPanel) {
		this.app = app;
		this.rootPanel = rootPanel;
	}

	void setupApplication() {
		dataPanel = new VerticalPanel();
		dataPanel.setWidth("400px");

		StandardButton exerciseButton = new StandardButton("Generate new exercise!");
		exerciseButton.setStyleName("solverButton");

		exerciseButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				generateNewExercise();
			}
		});

		dataPanel.add(exerciseButton);

		progressBar = new ProgressBar();
		dataPanel.add(progressBar);

		StandardButton hintButton = new StandardButton("Show/Hide next step");
		hintButton.setStyleName("solverButton");

		hintButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				hint.setVisible(!hint.isVisible());
			}
		});

		dataPanel.add(hintButton);

		hint = Canvas.createIfSupported();
		hint.setVisible(false);
		dataPanel.add(hint);

		rootPanel.add(dataPanel);

		FlowPanel gmDiv = new FlowPanel();
		gmDiv.getElement().setId("gm-div");
		gmDiv.setHeight("400px");
		rootPanel.add(gmDiv);

		ScriptInjector.fromUrl("https://graspablemath.com/shared/libs/gmath/gm-inject.js")
				.setWindow(ScriptInjector.TOP_WINDOW)
				.setCallback(new Callback<Void, Exception>() {
					@Override
					public void onFailure(Exception reason) {
						// network error
					}

					@Override
					public void onSuccess(Void result) {
						loadGM(Exercise.this);
					}
				}).inject();
	}

	private void generateNewExercise() {
		String s = ExerciseGenerator.getExercise(-1).equation;
		newExercise(s);
		initialComplexity = -1;
		onCanvasChanged(s);
	}

	private void onCanvasChanged(String lastEquation) {
		StepNode expression = StepNode.getStepTree(lastEquation, app.getKernel().getParser());

		SolutionBuilder sb = new SolutionBuilder();
		((StepTransformable) expression).toSolvable().solve(new StepVariable("x"), sb);
		int complexity = sb.getSteps().getComplexity();

		if (initialComplexity == -1) {
			initialComplexity = complexity;
		}

		progressBar.setMax(initialComplexity);
		progressBar.setValue(initialComplexity - complexity);

		if (previousComplexity < complexity) {
			if (complexity - previousComplexity < 5) {
				progressBar.setProgress("medium");
			} else {
				progressBar.setProgress("bad");
			}
		} else {
			progressBar.setProgress("good");
		}

		previousComplexity = complexity;

		String nextStep = getNextStep(sb.getSteps());

		DrawEquationW.paintOnCanvas(app, nextStep, hint, 40, GColor.BLACK, true);
	}

	private String getNextStep(SolutionStep ss) {
		if (ss.getType() == SolutionStepType.EQUATION) {
			return ss.getDefault(app.getLocalization()).get(0).latex;
		}

		if (ss.getType() == SolutionStepType.GROUP_WRAPPER) {
			if (ss.getSubsteps().get(0).getSubsteps() != null) {
				if (ss.getSubsteps().get(0).shouldSkipSubsteps()) {
					return getNextStep(ss.getSubsteps().get(1));
				} else if (ss.getSubsteps().get(0).shouldSkip()) {
					return null;
				}
			}
		}

		if (ss.getType() == SolutionStepType.SUBSTEP_WRAPPER) {
			return getNextStep(ss.getSubsteps().get(2));
		}

		if (ss.getSubsteps() != null) {
			for (SolutionStep step : ss.getSubsteps()) {
				String temp = getNextStep(step);
				if (temp != null) {
					return temp;
				}
			}
		}

		return null;
	}

	private native void loadGM(Exercise e) /*-{
		$wnd.loadGM(initCanvas, { version: 'latest' });

		function initCanvas() {
            $wnd.canvas = new $wnd.gmath.Canvas('#gm-div');
            $wnd.canvas.model.on('el_changed', onChangedCallback);
            e.@org.geogebra.web.solver.Exercise::generateNewExercise()();
		}

        function onChangedCallback(event) {
            e.@org.geogebra.web.solver.Exercise::onCanvasChanged(Ljava/lang/String;)(event.last_eq.replace('{', '(').replace('}', ')'));
        }
    }-*/;

    private native void newExercise(String s) /*-{
		$wnd.canvas.model.reset();
		$wnd.canvas.model.createElement('derivation', {
			eq : s,
			pos : {
				x : 'center',
				y : 50
			}
		});
	}-*/;
}
