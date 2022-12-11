package org.geogebra.web.solver;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.editor.AppWsolver;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.profiler.FpsProfilerW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.solver.keyboard.SolverKeyboard;
import org.geogebra.web.solver.keyboard.SolverKeyboardButton;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.HTML;
import org.gwtproject.user.client.ui.HorizontalPanel;
import org.gwtproject.user.client.ui.VerticalPanel;

import com.himamis.retex.editor.web.MathFieldW;

import elemental2.dom.DomGlobal;

public class Solver {

	private AppWsolver app;
	private MathFieldW mathField;
	private SolverKeyboard keyboard;

	private HorizontalPanel editorPanel;

	private AbsolutePanel rootPanel;
	private VerticalPanel solverPanel;
	private VerticalPanel stepsPanel;

	private WebStepGuiBuilder guiBuilder;

	Solver(AppWsolver app, AbsolutePanel rootPanel) {
		this.app = app;
		this.rootPanel = rootPanel;
	}

	void setupApplication() {
		guiBuilder = new WebStepGuiBuilder(app);

		Canvas canvas = Canvas.createIfSupported();

		solverPanel = new VerticalPanel();
		solverPanel.addStyleName("solverPanel");

		rootPanel.add(solverPanel);

		Element el = DOM.createDiv();
		el.appendChild(canvas.getCanvasElement());

		editorPanel = new HorizontalPanel();
		editorPanel.setStyleName("editorPanel");

		FlowPanel editorFocusPanel = new FlowPanel();

		mathField = new MathFieldW(new SyntaxAdapterImpl(app.getKernel()), editorFocusPanel, canvas,
				new SolverMathFieldListener(this));
		mathField.setExpressionReader(ScreenReader.getExpressionReader(app));
		app.setMathField(mathField);

		keyboard = new SolverKeyboard(app);

		editorFocusPanel.setStyleName("editorFocusPanel");
		editorFocusPanel.add(mathField.asWidget());
		ClickStartHandler.init(editorFocusPanel, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				mathField.setFocus(true);
			}
		});

		mathField.setFocus(true);

		editorPanel.add(editorFocusPanel);

		StandardButton solveButton = new StandardButton("Compute");
		solveButton.setStyleName("solverButton");
		solveButton.addFastClickHandler(source -> hideKeyboardAndCompute());
		editorPanel.add(solveButton);

		solverPanel.add(editorPanel);
		solverPanel.add(keyboard);
		solverPanel.add(new SolverKeyboardButton(keyboard));

		app.getLocalization().loadScript("en", (lang, asyncCall) -> {
			app.getLocalization().setLanguage(lang);

			keyboard.setProcessing(new MathFieldProcessing(mathField));
			keyboard.clearAndUpdate();

			String parameter = NavigatorUtil.getUrlParameter("i");
			if (parameter != null && !"".equals(parameter)) {
				compute(parameter);
			} else {
				keyboard.show();
			}
		});

		DomGlobal.window.addEventListener("resize", event -> resize());

		Scheduler.get().scheduleDeferred(this::resize);
	}

	private void resize() {
		app.getAppletFrame().updateHeaderSize();
		mathField.setPixelRatio(Browser.getPixelRatio());
		mathField.repaint();
		keyboard.onResize();
		if (stepsPanel != null) {
			for (int i = 0; i < stepsPanel.getWidgetCount(); i++) {
				if (stepsPanel.getWidget(i) instanceof StepInformation) {
					((StepInformation) stepsPanel.getWidget(i)).resize();
				}
			}
		}
	}

	void hideKeyboardAndCompute() {
		keyboard.hide();
		compute(mathField.getText());
	}

	private void compute(String text) {
		Browser.changeUrl(AppWsolver.getRelativeURLforEqn(text));
		mathField.parse(text);
		mathField.setFocus(false);

		if (stepsPanel != null) {
			solverPanel.remove(stepsPanel);
		}
		stepsPanel = new VerticalPanel();
		stepsPanel.addStyleName("stepTree");
		solverPanel.add(stepsPanel);

		StepTransformable input = StepNode.getStepTree(text, app.getKernel().getParser());

		if (input == null) {
			stepsPanel.add(new HTML("<h3>Sorry, but I am unable to do anything with "
					+ "your input</h3>"));
			return;
		}

		printAlternativeForms(input);

		List<StepVariable> variableList = input.getListOfVariables();

		printSolutions(input.toSolvable(), variableList);

		printDerivatives(input, variableList);

		if (stepsPanel.getWidgetCount() == 0) {
			stepsPanel.add(new HTML("<h3>Sorry, but I am unable to do anything with "
					+ "your input</h3>"));
		}
	}

	private void printAlternativeForms(StepTransformable input) {
		List<StepInformation> alternativeForms = new ArrayList<>();
		SolutionBuilder sb = new SolutionBuilder();

		StepTransformable regrouped = input.regroupOutput(sb);
		if (!regrouped.equals(input)) {
			alternativeForms.add(new StepInformation(guiBuilder, regrouped,
					sb.getSteps()));
		}
		sb.reset();

		StepTransformable expanded = input.expandOutput(sb);
		if (!expanded.equals(input) && !expanded.equals(regrouped)) {
			alternativeForms.add(new StepInformation(guiBuilder, expanded,
					sb.getSteps()));
		}
		sb.reset();

		StepTransformable factored = input.factorOutput(sb);
		if (!factored.equals(input) && !factored.equals(regrouped)
				&& !factored.equals(expanded)) {
			alternativeForms.add(new StepInformation(guiBuilder, factored,
					sb.getSteps()));
		}
		sb.reset();

		if (alternativeForms.size() > 0) {
			stepsPanel.add(new HTML("<h2>Alternative forms</h2>"));

			for (StepInformation alternativeForm : alternativeForms) {
				stepsPanel.add(alternativeForm);
			}
		}
	}

	private void printSolutions(StepSolvable solvable, List<StepVariable> variableList) {
		List<StepInformation> solutions = new ArrayList<>();
		SolutionBuilder sb = new SolutionBuilder();

		for (StepVariable variable : variableList) {
			try {
				double startTime = FpsProfilerW.getMillisecondTimeNative();
				List<StepSolution> solutionList = solvable.solve(variable, sb);
				double solveTime = FpsProfilerW.getMillisecondTimeNative();
				solutions.add(
						new StepInformation(guiBuilder, solutionList, sb.getSteps()));
				double endTime = FpsProfilerW.getMillisecondTimeNative();

				Log.debug("Total execution time: " + (endTime - startTime) + " ms");
				Log.debug("Solve time: " + (solveTime - startTime) + " ms");
				Log.debug("Render time: " + (endTime - solveTime) + " ms");
			} catch (SolveFailedException e) {
				Log.debug("Solve failed for " + solvable + " for variable " + variable);
			} catch (RuntimeException e) {
				Log.error("Something terrible happened when solving "
						+ solvable + " in " + variable);
				Log.debug(e);
			} finally {
				sb.reset();
			}
		}

		if (solutions.size() > 0) {
			stepsPanel.add(new HTML("<h2>Solutions</h2>"));

			for (StepInformation solution : solutions) {
				stepsPanel.add(solution);
			}
		}
	}

	private void printDerivatives(StepTransformable input, List<StepVariable> variableList) {
		if (input instanceof StepExpression && variableList.size() > 0) {
			stepsPanel.add(new HTML("<h2>Derivatives</h2>"));

			SolutionBuilder sb = new SolutionBuilder();
			for (StepVariable variable : variableList) {
				StepExpression derivative =
						StepNode.differentiate((StepExpression) input, variable);
				StepExpression result = (StepExpression) derivative.differentiateOutput(sb);
				stepsPanel.add(new StepInformation(guiBuilder,
						new StepEquation(derivative, result), sb.getSteps()));

				sb.reset();
			}
		}
	}
}
