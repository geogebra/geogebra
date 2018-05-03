package org.geogebra.web.full.gui.toolbarpanel;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;

import java.util.List;

public class WebStepGuiBuilder {

    private AppW app;
    private Localization loc;
    private GeoNumeric gn;

    private ImageResource openButton = GuiResources.INSTANCE.algebra_tree_open();
    private ImageResource closeButton = GuiResources.INSTANCE.algebra_tree_closed();

    /**
     * Constructor for WebStepGuiBuilder, a factory class
     * used to create rows of solution and buttons
     * @param app required for Localization, GeoNumeric and
     *            StandardButton..
     */
    public WebStepGuiBuilder(AppW app) {
        this.app = app;

        loc = app.getLocalization();
        gn = new GeoNumeric(app.getKernel().getConstruction());
    }

    /**
     * Creates the appropriate panel for the step
     * @param step SolutionSte to be displayed
     * @return StepElem, StepAlternative or a simple vertical
     * panel, depending on the type of the substep
     */
    public VerticalPanel buildStepGui(SolutionStep step) {
        if (step instanceof SolutionLine) {
            SolutionLine line = (SolutionLine) step;

            if (line.getType() == SolutionStepType.SUBSTEP_WRAPPER) {
                return new StepAlternative(this, step);
            } else if (line.getType() == SolutionStepType.WRAPPER
                    || line.getType() == SolutionStepType.GROUP_WRAPPER) {
                VerticalPanel panel = new VerticalPanel();

                for (SolutionStep substep : step.getSubsteps()) {
                    panel.add(buildStepGui(substep));
                }

                panel.setStyleName("stepPanel");
                return panel;
            }
        }

        return new StepElem(this, step);
    }

    /**
     * Render a SolutionStep
     * @param step SolutionStep to be rendered
     * @param detailed detailed means the colored version of
     *                 the substep
     * @return a FlowPanel containing the elements of the step
     * (text and LaTeX snippets)
     */
    public FlowPanel createRow(SolutionStep step, boolean detailed) {
        FlowPanel row = new FlowPanel();

        List<String> equations;
        if (detailed) {
            equations = step.getDetailed(loc);
        } else {
            equations = step.getDefault(loc);
        }

        for (String s : equations) {
            Widget toAdd;
            if (s.startsWith("$")) {
                toAdd = DrawEquationW.paintOnCanvas(gn, s.substring(1), null,
                        app.getFontSizeWeb());
            } else {
                toAdd = new InlineLabel(s);
            }

            toAdd.addStyleName("stepTreeElem");
            row.add(toAdd);
        }

        row.setVisible(true);

        row.setStyleName("stepTreeLine");
        return row;
    }

    /**
     * Create a show or hide details button
     * @param show wether show or hide button
     * @param alternative the StepAlternative panel to switch
     *                    on and off
     * @return a StandardButton, that switches the state of
     * alternative
     */
    public StandardButton detailsButton(boolean show, final StepAlternative alternative) {
        StandardButton detailsButton;
        if (show) {
            detailsButton = new StandardButton(closeButton, app);
        } else {
            detailsButton = new StandardButton(openButton, app);
        }

        detailsButton.setStyleName("stepTreeButton");

        FastClickHandler clickHandler = new FastClickHandler() {
            @Override
            public void onClick(Widget w) {
                alternative.swapStates();
            }
        };

        detailsButton.addFastClickHandler(clickHandler);

        return detailsButton;
    }

    /**
     * Create a show or hide substeps button
     * @param elem the StepElem panel to switch on
     *             and off
     * @return a StandardButton, that switches the state of
     * elem
     */
    public StandardButton showButton(final StepElem elem) {
        final StandardButton showButton = new StandardButton(closeButton, app);

        showButton.setStyleName("stepTreeButton");

        FastClickHandler clickHandler = new FastClickHandler() {
            boolean open;

            @Override
            public void onClick(Widget source) {
                if (open) {
                    showButton.setResource(closeButton);
                } else {
                    showButton.setResource(openButton);
                }
                open = !open;
                elem.swapStates();
            }
        };

        showButton.addFastClickHandler(clickHandler);

        return showButton;
    }
}
