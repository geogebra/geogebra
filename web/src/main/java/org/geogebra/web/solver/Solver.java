package org.geogebra.web.solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.*;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.util.debug.Log;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.editor.AppWsolver;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.WebSimple;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.HasLanguage;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.SharedResources;
import org.geogebra.web.solver.keyboard.SolverKeyboard;
import org.geogebra.web.solver.keyboard.SolverKeyboardButton;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.web.JlmEditorLib;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.CreateLibrary;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.font.opentype.Opentype;

public class Solver implements EntryPoint, MathFieldListener {

	private AppWsolver app;
	private JlmEditorLib library;
	private Opentype opentype;
	private MathFieldW mathField;
	private SolverKeyboard keyboard;

	private HorizontalPanel editorPanel;

	private RootPanel rootPanel;
	private VerticalPanel solverPanel;
	private VerticalPanel stepsPanel;

	@Override
	public void onModuleLoad() {
		WebSimple.registerSuperdevExceptionHandler();

		TestArticleElement articleElement = new TestArticleElement("true",
				"Solver");
		LoggerW.startLogger(articleElement);

		app = new AppWsolver(articleElement, new GeoGebraFrameSimple(false));

		StyleInjector.inject(SharedResources.INSTANCE.solverStyleScss());
		StyleInjector.inject(SharedResources.INSTANCE.stepTreeStyleScss());
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());
		JavaScriptInjector.inject(KeyboardResources.INSTANCE.wavesScript());
		StyleInjector.inject(KeyboardResources.INSTANCE.wavesStyle());

		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderGWT());
		}
		library = new JlmEditorLib();
		opentype = Opentype.INSTANCE;
		CreateLibrary.exportLibrary(library, opentype);
		startEditor();
	}

	/**
	 * @param parent
	 *            editor parent
	 */
	public void edit(Element parent) {
		Canvas canvas = Canvas.createIfSupported();
		String id = "JlmEditorKeyboard" + DOM.createUniqueId();
		parent.setId(id);

		rootPanel = RootPanel.get(id);

		solverPanel = new VerticalPanel();
		solverPanel.addStyleName("solverPanel");

		rootPanel.add(solverPanel);

		Element el = DOM.createDiv();
		el.appendChild(canvas.getCanvasElement());

		editorPanel = new HorizontalPanel();
		editorPanel.setStyleName("editorPanel");

		mathField = new MathFieldW(null, rootPanel, canvas, this, false, null);
		mathField.setPixelRatio(Browser.getPixelRatio());

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				mathField.setPixelRatio(Browser.getPixelRatio());
				mathField.repaint();
			}
		});

		keyboard = new SolverKeyboard(app, app);

		FlowPanel editorFocusPanel = new FlowPanel();
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

		StandardButton solveButton = new StandardButton("Compute", app);
		solveButton.setStyleName("solveButton");
		solveButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				onEnter();
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
				keyboard.buildGUI();

				String parameter = Window.Location.getParameter("i");
				if (parameter != null && !"".equals(parameter)) {
					compute(parameter);
				} else {
					keyboard.show();
				}
			}
		});
	}

	private native void startEditor() /*-{
		this.@org.geogebra.web.solver.Solver::edit(Lcom/google/gwt/dom/client/Element;)
		    ($wnd.getEditorElement());
	}-*/;

	private void compute(String text) {
		Browser.changeUrl("?i=" + URL.encodePathSegment(text));
		mathField.setText(text, false);

		StepTransformable input = StepNode.getStepTree(text, app.getKernel().getParser());

		if (stepsPanel != null) {
			solverPanel.remove(stepsPanel);
		}
		stepsPanel = new VerticalPanel();
		stepsPanel.addStyleName("stepTree");
		solverPanel.add(stepsPanel);

		if (input == null) {
			stepsPanel.add(new HTML("<h3>Sorry, but I am unable to do anything with "
					+ "your input</h3>"));

			return;
		}

		mathField.setFocus(false);

		WebStepGuiBuilder guiBuilder = new WebStepGuiBuilder(app);

		SolutionBuilder sb = new SolutionBuilder();

		List<StepInformation> alternativeForms = new ArrayList<>();

		StepTransformable regrouped = input.regroupOutput(sb);
		if (!regrouped.equals(input)) {
			alternativeForms.add(new StepInformation(app, guiBuilder, regrouped,
					sb.getSteps()));
		}
		sb.reset();

		StepTransformable expanded = input.expandOutput(sb);
		if (!expanded.equals(input) && !expanded.equals(regrouped)) {
			alternativeForms.add(new StepInformation(app, guiBuilder, expanded,
					sb.getSteps()));
		}
		sb.reset();

		StepTransformable factored = input.factorOutput(sb);
		if (!factored.equals(input) && !factored.equals(regrouped)
				&& !factored.equals(expanded)) {
			alternativeForms.add(new StepInformation(app, guiBuilder, factored,
					sb.getSteps()));
		}
		sb.reset();

		if (alternativeForms.size() > 0) {
			stepsPanel.add(new HTML("<h2>Alternative forms</h2>"));

			for (StepInformation alternativeForm : alternativeForms) {
				stepsPanel.add(alternativeForm);
			}
		}

		Set<StepVariable> variableSet = new HashSet<>();
		input.getListOfVariables(variableSet);

		List<StepInformation> solutions = new ArrayList<>();

		StepSolvable solvable = input.toSolvable();

		for (StepVariable variable : variableSet) {
			try {
				double startTime = app.getMillisecondTime();
				List<StepSolution> solutionList = solvable.solve(variable, sb);
				double solveTime = app.getMillisecondTime();
				solutions.add(
						new StepInformation(app, guiBuilder, solutionList, sb.getSteps()));
				double endTime = app.getMillisecondTime();

				Log.debug("Total execution time: " + (endTime - startTime) + " ms");
				Log.debug("Solve time: " + (solveTime - startTime) + " ms");
				Log.debug("Render time: " + (endTime - solveTime) + " ms");
			} catch (SolveFailedException e) {
				Log.debug("Solve failed for " + solvable + " for variable " + variable);
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

		if (input instanceof StepExpression) {
			stepsPanel.add(new HTML("<h2>Derivatives</h2>"));

			for (StepVariable variable : variableSet) {
				StepExpression derivative =
						StepNode.differentiate((StepExpression) input, variable);
				StepExpression result = (StepExpression) derivative.differentiate(sb);
				stepsPanel.add(new StepInformation(app, guiBuilder,
						new StepEquation(derivative, result), sb.getSteps()));

				sb.reset();
			}
		}

		if (stepsPanel.getWidgetCount() == 0) {
			stepsPanel.add(new HTML("<h3>Sorry, but I am unable to do anything with "
					+ "your input</h3>"));
		}
	}

	@Override
	public void onEnter() {
		keyboard.hide();

		String text = new GeoGebraSerializer()
				.serialize(mathField.getFormula());

		compute(text);
	}

	@Override
	public void onKeyTyped() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCursorMove() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDownKeyPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpKeyPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public String serialize(MathSequence selectionText) {
		return GeoGebraSerializer.serialize(selectionText);
	}

	@Override
	public void onInsertString() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onEscape() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onTab(boolean shiftDown) {
		// TODO Auto-generated method stub
	}
}
