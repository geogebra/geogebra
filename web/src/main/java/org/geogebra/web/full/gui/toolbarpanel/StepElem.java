package org.geogebra.web.full.gui.toolbarpanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;

import java.util.List;

public class StepElem extends VerticalPanel {

    private WebStepGuiBuilder builder;

    private List<SolutionStep> substeps;

    private boolean needsRender;

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

    public void swapStates() {
        if (needsRender) {
            for (SolutionStep step : substeps) {
                add(builder.buildStepGui(step));
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
