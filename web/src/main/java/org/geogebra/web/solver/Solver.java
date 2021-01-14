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
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.editor.AppWsolver;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.profiler.FpsProfilerW;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.HasLanguage;
import org.geogebra.web.solver.keyboard.SolverKeyboard;
import org.geogebra.web.solver.keyboard.SolverKeyboardButton;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.web.JlmEditorLib;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.web.CreateLibrary;
import com.himamis.retex.renderer.web.font.opentype.Opentype;

public class Solver {

	private AppWsolver app;
	private JlmEditorLib library;
	private Opentype opentype;
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
		library = new JlmEditorLib();
		opentype = Opentype.INSTANCE;
		CreateLibrary.exportLibrary(library, opentype);

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

		mathField = new MathFieldW(null, editorFocusPanel, canvas,
				new SolverMathFieldListener(this), false);
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
		solveButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				hideKeyboardAndCompute();
			}
		});
		editorPanel.add(solveButton);

		solverPanel.add(editorPanel);
		solverPanel.add(keyboard);
		solverPanel.add(new SolverKeyboardButton(keyboard));

		app.getLocalization().loadScript("en", new HasLanguage() {
			@Override
			public void doSetLanguage(String lang, boolean asyncCall) {
				app.getLocalization().setLanguage(lang);

				keyboard.setProcessing(new MathFieldProcessing(mathField));
				keyboard.buildGUI(null);

				String parameter = Window.Location.getParameter("i");
				if (parameter != null && !"".equals(parameter)) {
					compute(parameter);
				} else {
					keyboard.show();
				}
			}
		});

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				resize();
			}
		});
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
		mathField.setText(text, false);
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
