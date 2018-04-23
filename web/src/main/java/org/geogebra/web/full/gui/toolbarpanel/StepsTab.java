package org.geogebra.web.full.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.view.algebra.OpenButton;
import org.geogebra.web.full.gui.view.algebra.TreeImages;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class StepsTab extends ToolbarPanel.ToolbarTab {

    final AppW app;
    final ToolbarPanel toolbarPanel;
    final GeoNumeric gn;

    private ScrollPanel sp;
    private Tree tree;

    private StepGuiBuilder stepGuiBuilder = new StepGuiBuilder() {
        private TreeItem item;
        private TreeItem child = null;
        private boolean detailed = false;

        private StandardButton showDetails;
        private StandardButton hideDetails;

        private boolean addShowDetails;
        private boolean addHideDetails;

        private ArrayList<Widget> summary = new ArrayList<>();

        @Override
        public void addRow(List<String> equations) {
            FlowPanel row = new FlowPanel();

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

            if (addShowDetails) {
                row.add(showDetails);
                addShowDetails = false;
            } else if (addHideDetails) {
                row.add(hideDetails);
                addHideDetails = false;
            }

            if (detailed) {
                row.setVisible(false);
            }

            summary.add(row);
            addWidget(row);
        }

        private void addWidget(Widget c) {
            child = new TreeItem(c);
            if (item != null) {
                if (item.getChildCount() == 0) {
                    FlowPanel row = (FlowPanel) item.getWidget();

                    OpenButton openButton = new OpenButton(
                            GuiResources.INSTANCE.algebra_tree_open().getSafeUri(),
                            GuiResources.INSTANCE.algebra_tree_closed().getSafeUri(),
                            item, "stepTreeButton");
                    openButton.setChecked(false);
                    row.add(openButton);

                    item.addStyleName("stepTreeGroup");
                }

                item.addItem(child);
            } else {
                tree.addItem(child);
            }
            if (detailed) {
                child.getElement().getStyle().setDisplay(Style.Display.NONE);
            }
        }

        @Override
        public void startGroup() {
            if (child == null || tree.getItemCount() == 1) {
                return;
            }
            item = child;
        }

        @Override
        public void endGroup() {
            if (item != null) {
                item = item.getParentItem();
            }
        }

        @Override
        public void linebreak() {
            addWidget(new FlowPanel());
        }

        @Override
        public void startDefault() {
            showDetails = new StandardButton(
                    GuiResources.INSTANCE.algebra_tree_closed(), app);
            hideDetails = new StandardButton(
                    GuiResources.INSTANCE.algebra_tree_open(), app);

            showDetails.getElement().getStyle().setPadding(1, Style.Unit.PX);
            showDetails.getElement().getStyle().setFontSize(10, Style.Unit.PX);
            showDetails.setStyleName("stepTreeButton");

            hideDetails.getElement().getStyle().setPadding(1, Style.Unit.PX);
            hideDetails.getElement().getStyle().setFontSize(10, Style.Unit.PX);
            hideDetails.setStyleName("stepTreeButton");

            addShowDetails = true;
            summary = new ArrayList<>();
            detailed = false;
        }

        @Override
        public void switchToDetailed() {
            addHideDetails = true;

            detailed = true;
        }

        @Override
        public void endDetailed() {
            detailed = false;

            final ArrayList<Widget> swap = new ArrayList<>(summary);
            summary = new ArrayList<>();

            FastClickHandler clickHandler = new FastClickHandler() {

                @Override
                public void onClick(Widget w) {
                    for (Widget line : swap) {
                        boolean visible = !line.isVisible();
                        line.setVisible(visible);
                        line.getElement().getParentElement()
                                .getParentElement().getStyle()
                                .setDisplay(visible ? Style.Display.BLOCK
                                        : Style.Display.NONE);
                    }
                }
            };

            showDetails.addFastClickHandler(clickHandler);
            hideDetails.addFastClickHandler(clickHandler);
        }
    };

    public StepsTab(ToolbarPanel toolbarPanel) {
        this.toolbarPanel = toolbarPanel;
        app = toolbarPanel.app;
        gn = new GeoNumeric(app.getKernel().getConstruction());

        sp = new ScrollPanel();
        add(sp);
    }

    public StepGuiBuilder getStepGuiBuilder() {
        tree = new Tree(new TreeImages());
        tree.addStyleName("stepTree");
        tree.setWidth(toolbarPanel.getTabWidth() + "px");

        sp.clear();
        sp.add(tree);

        return stepGuiBuilder;
    }

    @Override
    public void onResize() {
        super.onResize();
        sp.getWidget().setWidth(toolbarPanel.getTabWidth() + "px");
    }
}
