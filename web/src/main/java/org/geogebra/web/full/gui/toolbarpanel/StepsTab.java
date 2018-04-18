package org.geogebra.web.full.gui.toolbarpanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;

import java.util.ArrayList;

public class StepsTab extends ToolbarPanel.ToolbarTab {

    final AppW app;
    final GeoNumeric gn;

    private ScrollPanel sp;
    private Tree tree;

    private StepGuiBuilder stepGuiBuilder = new StepGuiBuilder() {
        private TreeItem item;
        private TreeItem child = null;
        private boolean detailed = false;
        private Button showDetails;
        private ArrayList<Widget> summary = new ArrayList<>();

        @Override
        public void addLatexRow(String equations) {
            Canvas c = DrawEquationW.paintOnCanvas(gn, equations, null,
                    app.getFontSizeWeb());
            if (detailed) {
                c.setVisible(false);
            }
            summary.add(c);
            addWidget(c);
        }

        private void addWidget(Widget c) {
            child = new TreeItem(c);
            if (item != null) {
                item.addItem(child);
            } else {
                tree.addItem(child);
            }
            if (detailed) {
                child.getElement().getStyle().setDisplay(Style.Display.NONE);
            }
        }

        @Override
        public void addPlainRow(String equations) {
            addWidget(new Label(equations));
        }

        @Override
        public void show() {
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
            // TODO Auto-generated method stub
        }

        @Override
        public void startDefault() {
            summary = new ArrayList<>();
            detailed = false;
            showDetails = new Button("?");
            addWidget(showDetails);
            showDetails.getElement().getStyle().setPadding(1, Style.Unit.PX);
            showDetails.getElement().getStyle().setFontSize(5, Style.Unit.PX);
            showDetails.getElement().getParentElement().getParentElement()
                    .getStyle().setProperty("float", "left");
        }

        @Override
        public void switchToDetailed() {
            detailed = true;
        }

        @Override
        public void endDetailed() {
            detailed = false;
            final ArrayList<Widget> swap = new ArrayList<>(summary);
            summary = new ArrayList<>();
            showDetails.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    event.getSource();
                    for (Widget line : swap) {
                        boolean visible = !line.isVisible();
                        line.setVisible(visible);
                        line.getElement().getParentElement()
                                .getParentElement().getStyle()
                                .setDisplay(visible ? Style.Display.BLOCK
                                        : Style.Display.NONE);
                    }
                }
            });
        }
    };

    public StepsTab(ToolbarPanel toolbarPanel) {
        app = toolbarPanel.app;
        gn = new GeoNumeric(app.getKernel().getConstruction());

        sp = new ScrollPanel();
        sp.setAlwaysShowScrollBars(false);
        add(sp);
    }

    public StepGuiBuilder getStepGuiBuilder() {
        tree = new Tree();
        tree.addStyleName("stepTree");
        sp.clear();
        sp.add(tree);

        return stepGuiBuilder;
    }

}
