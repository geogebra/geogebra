package org.geogebra.web.solver;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.web.JlmEditorLib;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.CreateLibrary;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.font.opentype.Opentype;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.editor.AppWsolver;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.html5.WebSimple;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.*;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.SharedResources;
import org.geogebra.web.solver.keyboard.SolverKeyboard;

import java.util.List;

public class Solver implements EntryPoint, MathFieldListener {

    private AppWsolver app;
    private JlmEditorLib library;
    private Opentype opentype;
    private MathFieldW mathField;

    private HorizontalPanel editorPanel;

    private RootPanel rootPanel;
    private VerticalPanel solverPanel;
    private VerticalPanel stepsPanel;

    @Override
    public void onModuleLoad() {
        WebSimple.registerSuperdevExceptionHandler();

        TestArticleElement articleElement =
                new TestArticleElement("true", "Solver");
        LoggerW.startLogger(articleElement);

        app = new AppWsolver(
                articleElement,
                new GeoGebraFrameSimple(false)
        );

        StyleInjector.inject(SharedResources.INSTANCE.solverStyleScss());
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
        mathField = new MathFieldW(null, rootPanel,
                canvas,
                this, false, null);

        editorPanel = new HorizontalPanel();
        editorPanel.setStyleName("editorPanel");

        editorPanel.add(mathField.asWidget());

        StandardButton solveButton = new StandardButton("Solve", app);
        solveButton.setStyleName("solveButton");
        solveButton.addFastClickHandler(new FastClickHandler() {
            @Override
            public void onClick(Widget source) {
                onEnter();
            }
        });
        editorPanel.add(solveButton);

        solverPanel.add(editorPanel);

        final SolverKeyboard kb = new SolverKeyboard(app, app);

        app.getLocalization().loadScript("en",
                new HasLanguage() {
                    @Override
                    public void doSetLanguage(String lang, boolean asyncCall) {
                        app.getLocalization().setLanguage(lang);

                        kb.setProcessing(new MathFieldProcessing(mathField));

                        solverPanel.add(kb);

                        kb.buildGUI();
                        kb.show();
                    }
                });
    }

    private native void startEditor() /*-{
		this.@org.geogebra.web.solver.Solver::edit(Lcom/google/gwt/dom/client/Element;)
		    ($wnd.getEditorElement());
	}-*/;

    @Override
    public void onEnter() {
        String text = new GeoGebraSerializer()
                .serialize(mathField.getFormula());

        StepNode sn = StepNode.getStepTree(text, app.getKernel().getParser());


        if (stepsPanel != null) {
            solverPanel.remove(stepsPanel);
        }
        stepsPanel = new VerticalPanel();
        solverPanel.add(stepsPanel);

        WebStepGuiBuilder guiBuilder = new WebStepGuiBuilder(app);

        SolutionBuilder sb = new SolutionBuilder();
        if (sn instanceof StepExpression) {
            StepExpression expr = (StepExpression) sn;

            StepExpression regrouped = (expr).regroupOutput(sb);
            if (!regrouped.equals(expr)) {
                stepsPanel.add(new StepInformation(app, guiBuilder,
                        regrouped, sb.getSteps()));
            }
            sb.reset();

            StepExpression expanded = (expr).expandOutput(sb);
            if (!expanded.equals(expr) && !expanded.equals(regrouped)) {
                stepsPanel.add(new StepInformation(app, guiBuilder,
                        expanded, sb.getSteps()));
            }
            sb.reset();

            StepExpression factored = (expr).factorOutput(sb);
            if (!factored.equals(expr) && !factored.equals(regrouped)
                    && !factored.equals(expanded)) {
                stepsPanel.add(new StepInformation(app, guiBuilder,
                        factored, sb.getSteps()));
            }
        } else if (sn instanceof StepEquation) {
            List<StepSolution> solutions =
                    ((StepEquation) sn).solve(new StepVariable("x"), sb);

            stepsPanel.add(new StepInformation(app, guiBuilder,
                    solutions, sb.getSteps()));
        }

        stepsPanel.addStyleName("stepTree");
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
