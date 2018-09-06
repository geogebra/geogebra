package org.geogebra.web.solver;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.web.editor.AppWsolver;
import org.geogebra.web.html5.WebSimple;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.SharedResources;

public class Exercise implements EntryPoint {

	private AppWsolver app;
	private RootPanel rootPanel;
	private VerticalPanel dataPanel;

	private int previousComplexity;

	@Override
	public void onModuleLoad() {
		WebSimple.registerSuperdevExceptionHandler();

		TestArticleElement articleElement = new TestArticleElement("true",
				"Solver");
		LoggerW.startLogger(articleElement);
		GeoGebraFrameSimple fr = new GeoGebraFrameSimple(false);
		app = new AppWsolver(articleElement, fr);

		StyleInjector.inject(SharedResources.INSTANCE.solverStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.sharedStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.stepTreeStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.dialogStylesScss());

		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderGWT());
		}

		String id = "appContainer" + DOM.createUniqueId();
		getContainer().setId(id);

		rootPanel = RootPanel.get(id);

		StandardButton exerciseButton = new StandardButton("Generate new exercise!", app);
		exerciseButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				String s = ExerciseGenerator.getExercise(-1).equation;
				newExercise(s);
				previousComplexity = -1;
				onCanvasChanged("", s);
			}
		});

		rootPanel.add(exerciseButton);

		dataPanel = new VerticalPanel();
		rootPanel.add(dataPanel);

		setupListener(this);

		loadGM();
	}

	private void onCanvasChanged(String targetType, String lastEquation) {
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

	private native void setupListener(Exercise e) /*-{
		$wnd.onChangedCallback = function(event) {
            e.@org.geogebra.web.solver.Exercise::onCanvasChanged(Ljava/lang/String;Ljava/lang/String;)(event.target_type, event.last_eq);
        }
    }-*/;

	private native void loadGM() /*-{
    	$wnd.loadGraspableMath();
    }-*/;

    private native void newExercise(String s) /*-{
    	$wnd.canvas.model.reset();
    	$wnd.canvas.model.createElement('derivation', { eq: s, pos: { x: 'center', y: 50 } });
    }-*/;

	private native Element getContainer() /*-{
        return $wnd.getContainer();
    }-*/;
}
