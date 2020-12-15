package org.geogebra.web.solver;

import java.util.List;

import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

class StepInformation extends DockPanel {

    private WebStepGuiBuilder builder;

    private SolutionStep steps;
    private VerticalPanel container;
    private FlowPanel renderedResult;
    private VerticalPanel renderedSteps;
    private StandardButton stepsButton;

    private boolean rendered;

    /**
     * Constructs a StepInformation given a single StepNode
     * @param builder WebStepGuiBuilder for rendering step tree
     * @param result StepNode result
     * @param steps SolutionSteps tree to be rendered
     */
	StepInformation(WebStepGuiBuilder builder,
            StepNode result, SolutionStep steps) {
        setupInformation(builder,
                new SolutionLine(SolutionStepType.EQUATION, result), steps);
    }

    /**
     * Constructs a StepInformation given a list of StepSolutions
     * @param builder WebStepGuiBuilder for rendering step tree
     * @param result list of StepSolutions, to be rendered as the result
     * @param steps SolutionSteps tree to be rendered
     */
	StepInformation(WebStepGuiBuilder builder,
            List<StepSolution> result, SolutionStep steps) {
        SolutionLine line;
        if (result.size() == 0) {
            line = new SolutionLine(SolutionStepType.NO_REAL_SOLUTION);
        } else {
            line = new SolutionLine(SolutionStepType.LIST, result.toArray(new StepNode[0]));
        }

		setupInformation(builder, line, steps);
    }

    private void setupInformation(WebStepGuiBuilder builder,
			SolutionLine display, SolutionStep steps) {
        this.steps = steps;
        this.builder = builder;

        if (steps != null) {
            setStyleName("stepInformation");

            container = new VerticalPanel();

            renderedResult = builder.createRow(display, false);
            container.add(renderedResult);

            add(container, DockPanel.WEST);

            stepsButton = new StandardButton("Show Steps");
            stepsButton.setStyleName("solverButton");
            stepsButton.addFastClickHandler(new FastClickHandler() {
                @Override
                public void onClick(Widget source) {
                    showSteps();
                }
            });
            add(stepsButton, DockPanel.EAST);
        } else {
            add(builder.createRow(display, false), DockPanel.WEST);
        }
    }

    private void showSteps() {
        if (!rendered) {
            rendered = true;
            renderedSteps = builder.buildStepGui(steps, true);
            container.add(renderedSteps);
            stepsButton.setLabel("Hide Steps");
            renderedResult.setVisible(false);
        } else if (renderedSteps.isVisible()) {
            stepsButton.setLabel("Show Steps");
            renderedSteps.setVisible(false);
            renderedResult.setVisible(true);
        } else {
            stepsButton.setLabel("Hide Steps");
            renderedSteps.setVisible(true);
            renderedResult.setVisible(false);
        }
    }

    public void resize() {
	    if (steps != null) {
            if (Window.getClientWidth() < 500) {
                add(container, DockPanel.NORTH);
                add(stepsButton, DockPanel.SOUTH);
            } else {
                add(container, DockPanel.WEST);
                add(stepsButton, DockPanel.EAST);
            }
        }
    }
}
