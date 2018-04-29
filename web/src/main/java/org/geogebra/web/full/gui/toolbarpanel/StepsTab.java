package org.geogebra.web.full.gui.toolbarpanel;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;

import com.google.gwt.user.client.ui.ScrollPanel;

public class StepsTab extends ToolbarPanel.ToolbarTab {

    final ToolbarPanel toolbarPanel;

    private ScrollPanel sp;

    private WebStepGuiBuilder stepGuiBuilder;

    public StepsTab(ToolbarPanel toolbarPanel) {
        this.toolbarPanel = toolbarPanel;

        stepGuiBuilder = new WebStepGuiBuilder(toolbarPanel.app);

        sp = new ScrollPanel();
        add(sp);
    }

    public void buildStepGui(SolutionStep steps) {
        VerticalPanel panel = stepGuiBuilder.buildStepGui(steps);

        panel.addStyleName("stepTree");
        panel.setWidth(toolbarPanel.getTabWidth() + "px");

        sp.clear();
        sp.add(panel);
    }

    @Override
    public void onResize() {
        super.onResize();
        sp.getWidget().setWidth(toolbarPanel.getTabWidth() + "px");
    }
}
