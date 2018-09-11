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
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.DrawEquationW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Exercise {

	private AppWsolver app;
	private AbsolutePanel rootPanel;
	private VerticalPanel dataPanel;

	private int previousComplexity;

	Exercise(AppWsolver app, AbsolutePanel rootPanel) {
		this.app = app;
		this.rootPanel = rootPanel;
	}

	void setupApplication() {
		StandardButton exerciseButton = new StandardButton("Generate new exercise!", app);
		exerciseButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				String s = ExerciseGenerator.getExercise(-1).equation;
				newExercise(s);
				previousComplexity = -1;
				onCanvasChanged(s);
			}
		});

		rootPanel.add(exerciseButton);

		dataPanel = new VerticalPanel();
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

	private void onCanvasChanged(String lastEquation) {
		dataPanel.clear();

		StepNode expression = StepNode.getStepTree(lastEquation, app.getKernel().getParser());
		String currentStep = expression.toLaTeXString(app.getLocalization());

		dataPanel.add(new HTML("<h1>Current equation: " + lastEquation + "</h1>"));
		Canvas c1 = Canvas.createIfSupported();
		DrawEquationW.paintOnCanvas(app, currentStep, c1, 40, GColor.MAGENTA, true);
		dataPanel.add(c1);

		SolutionBuilder sb = new SolutionBuilder();
		((StepTransformable) expression).toSolvable().solve(new StepVariable("x"), sb);
		int complexity = sb.getSteps().getComplexity();
		dataPanel.add(new HTML("<h1>Complexity: " + complexity));

		if (previousComplexity != -1) {
			String text;
			GColor color;
			if (previousComplexity < complexity) {
				if (complexity - previousComplexity < 5) {
					text = "\\text{OK}";
					color = GColor.YELLOW;
				} else {
					text = "\\text{I don't think so}";
					color = GColor.RED;
				}
			} else {
				text = "\\text{GOOD}";
				color = GColor.GREEN;
			}

			Canvas c3 = Canvas.createIfSupported();
			DrawEquationW.paintOnCanvas(app, text, c3, 40, color, true);
			dataPanel.add(c3);
		}

		previousComplexity = complexity;

		String nextStep = getNextStep(sb.getSteps());

		dataPanel.add(new HTML("<h1>Next step: </h1>"));
		Canvas c2 = Canvas.createIfSupported();
		DrawEquationW.paintOnCanvas(app, nextStep, c2, 40, GColor.MAGENTA, true);
		dataPanel.add(c2);
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
