package org.geogebra.web.solver;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import java.util.List;

public class StepInformation extends VerticalPanel {

    private WebStepGuiBuilder builder;

    private SolutionStep steps;
    private VerticalPanel renderedSteps;
    private StandardButton stepsButton;

    private boolean rendered;

    public StepInformation(AppW app, WebStepGuiBuilder builder,
                           StepNode result, SolutionStep steps) {
        setupInformation(app, builder,
                new SolutionLine(SolutionStepType.EQUATION, result), steps);
    }

    public StepInformation(AppW app, WebStepGuiBuilder builder,
                           List<StepSolution> result, SolutionStep steps) {
        SolutionLine display;
        if (result.size() == 0) {
            display = new SolutionLine(SolutionStepType.NO_REAL_SOLUTION);
        } else if (result.size() == 1) {
            display = new SolutionLine(SolutionStepType.SOLUTION, result.get(0));
        } else {
            display = new SolutionLine(SolutionStepType.SOLUTIONS, result.toArray(new StepNode[0]));
        }

        setupInformation(app, builder, display, steps);
    }

    private void setupInformation(AppW app, WebStepGuiBuilder builder,
                           SolutionLine display, SolutionStep steps) {
        this.steps = steps;
        this.builder = builder;

        if (steps != null) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.setStyleName("stepInformation");

            hp.add(builder.createRow(display, false));

            stepsButton = new StandardButton("Show Steps", app);
            stepsButton.setStyleName("solveButton");
            stepsButton.addFastClickHandler(new FastClickHandler() {
                @Override
                public void onClick(Widget source) {
                    showSteps();
                }
            });
            hp.add(stepsButton);

            add(hp);
        } else {
            add(builder.createRow(display, false));
        }
    }

    public void showSteps() {
        if (!rendered) {
            rendered = true;
            renderedSteps = builder.buildStepGui(steps);
            add(renderedSteps);
            stepsButton.setLabel("Hide Steps");
        } else if (renderedSteps.isVisible()) {
            stepsButton.setLabel("Show Steps");
            renderedSteps.setVisible(false);
        } else {
            stepsButton.setLabel("Hide Steps");
            renderedSteps.setVisible(true);
        }
    }
}
