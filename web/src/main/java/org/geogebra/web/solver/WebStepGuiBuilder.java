package org.geogebra.web.solver;

import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.TextElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.shared.SharedResources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WebStepGuiBuilder {

    private AppW app;
    private Localization loc;
    private GeoNumeric gn;

    private ImageResource openButton = SharedResources.INSTANCE.algebra_tree_open();
    private ImageResource closeButton = SharedResources.INSTANCE.algebra_tree_closed();

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
    public VerticalPanel buildStepGui(SolutionStep step, boolean first) {
		VerticalPanel panel;
		switch (step.getType()) {
		case SUBSTEP_WRAPPER:
			return new StepAlternative(this, step);

		case SOLVE_FOR:
		case SIMPLIFY:
		case EXPAND:
		case FACTOR:
		case DIFFERENTIATE:
			panel = new VerticalPanel();
			if (!first) {
				return new StepElem(this, step);
			}

			panel.add(createRow(step, false));

			if (step.getSubsteps() != null) {
				for (SolutionStep substep : step.getSubsteps()) {
					panel.add(buildStepGui(substep, false));
				}
			}

			panel.setStyleName("stepGroupPanel");
			return panel;

		case WRAPPER:
		case GROUP_WRAPPER:
			panel = new VerticalPanel();
			for (SolutionStep substep : step.getSubsteps()) {
				panel.add(buildStepGui(substep, first));
			}

			panel.setStyleName("stepPanel");
			return panel;

		default:
			return new StepElem(this, step);
		}
    }

    /**
     * Render a SolutionStep
     * @param step SolutionStep to be rendered
     * @param detailed detailed means the colored version of
     *                 the substep
     * @return a FlowPanel containing the elements of the step
     * (text and LaTeX snippets)
     */
	FlowPanel createRow(SolutionStep step, boolean detailed) {
        FlowPanel row = new FlowPanel();

        List<TextElement> equations;
        if (detailed) {
            equations = step.getDetailed(loc);
        } else {
            equations = step.getDefault(loc);
        }

        for (TextElement s : equations) {
            Widget toAdd;
            if (s.latex != null) {
                toAdd = DrawEquationW.paintOnCanvas(gn, s.latex, null,
                        app.getFontSizeWeb());
                toAdd.getElement().setInnerText(s.plain);
				ClickStartHandler.init(toAdd, new ClickStartHandler() {
					@Override
					public void onClickStart(int x, int y, PointerEventType type) {
                        // to be decided
					}
				});
            } else {
                toAdd = new InlineLabel(s.plain);
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
	 * 
	 * @param show
	 *            whether show or hide button
	 * @param alternative
	 *            the StepAlternative panel to switch on and off
	 * @return a StandardButton, that switches the state of alternative
	 */
	StandardButton detailsButton(boolean show, final StepAlternative alternative) {
        StandardButton detailsButton;
        if (show) {
            detailsButton = new StandardButton(closeButton);
        } else {
            detailsButton = new StandardButton(openButton);
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
	StandardButton showButton(final StepElem elem) {
        final StandardButton showButton = new StandardButton(closeButton);

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
