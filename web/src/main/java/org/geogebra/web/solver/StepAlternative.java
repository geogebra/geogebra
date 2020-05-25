package org.geogebra.web.solver;

import java.util.List;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StepAlternative extends VerticalPanel {

    private WebStepGuiBuilder builder;

    private List<SolutionStep> substeps;

    private boolean needsRender = true;

    /**
     * Constructor for a StepAlternative panel: a vertical panel, that
     * enables switching between the default and detailed view
     * @param builder WebStepGuiBuilder to render the LaTeX and create
     *                the buttons
     * @param step SubstepWrappter type SolutionLine containing the
     *             substeps
     */
    public StepAlternative(WebStepGuiBuilder builder, SolutionStep step) {
        this.builder = builder;
        substeps = step.getSubsteps();

        StandardButton showDetails = builder.detailsButton(true, this);

        FlowPanel row = builder.createRow(substeps.get(1), false);
        row.add(showDetails);
        add(row);

        add(builder.createRow(substeps.get(substeps.size() - 1), false));
        addStyleName("stepAlternativePanel");
    }

    /**
     * Switches between default and detailed view, renders the detailed,
     * if this is the first time it'c called
     */
    public void swapStates() {
        for (int i = 0; i < getWidgetCount(); i++) {
            boolean visible = !getWidget(i).isVisible();
            getWidget(i).setVisible(visible);
            getWidget(i).getElement().getParentElement()
                    .getParentElement().getStyle()
                    .setDisplay(visible ? Style.Display.BLOCK
                            : Style.Display.NONE);
        }

        if (needsRender) {
            StandardButton hideDetails = builder.detailsButton(false, this);

            FlowPanel row = builder.createRow(substeps.get(0), true);
            row.add(hideDetails);
            add(row);

            for (int i = 1; i < substeps.size(); i++) {
                add(builder.createRow(substeps.get(i), true));
            }

            needsRender = false;
        }
    }
}
