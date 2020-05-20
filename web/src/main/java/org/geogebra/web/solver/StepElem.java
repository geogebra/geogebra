package org.geogebra.web.solver;

import java.util.List;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StepElem extends VerticalPanel {

    private WebStepGuiBuilder builder;

    private List<SolutionStep> substeps;

    private boolean needsRender;

    /**
     * Constructor for StepElem panel: a simple vertical panel, that
     * displays a SolutionStep, and if it has substeps, a show button
     * @param builder WebStepGuiBuilder used to create the step and the
     *                button
     * @param step SolutionStep to display
     */
    public StepElem(WebStepGuiBuilder builder, SolutionStep step) {
        this.builder = builder;

        FlowPanel row = builder.createRow(step, true);

        substeps = step.getSubsteps();

        if (substeps != null) {
            row.add(builder.showButton(this));
            needsRender = true;
        }

        add(row);
        addStyleName("stepGroupPanel");
    }

    /**
     * Switches between closed and open state. If this is the first time
     * opening it, it renders the substeps.
     */
    public void swapStates() {
        if (needsRender) {
            for (SolutionStep step : substeps) {
                add(builder.buildStepGui(step, false));
            }
            needsRender = false;
        } else {
            for (int i = 1; i < getWidgetCount(); i++) {
                boolean visible = !getWidget(i).isVisible();
                getWidget(i).setVisible(visible);
                getWidget(i).getElement().getParentElement()
                        .getParentElement().getStyle()
                        .setDisplay(visible ? Style.Display.BLOCK
                                : Style.Display.NONE);
            }
        }
    }
}
