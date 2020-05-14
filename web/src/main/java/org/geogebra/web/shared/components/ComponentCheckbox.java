package org.geogebra.web.shared.components;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * material design checkbox component
 */
public class ComponentCheckbox extends FlowPanel {
    private boolean selected;
    private FlowPanel checkbox;

    /**
     *
     * @param setSelected true if the checkmark is shown
     */
    public ComponentCheckbox(boolean setSelected, Label templateTxt) {
        this.selected = setSelected;
        this.addStyleName("templatePanel");
        checkbox = new FlowPanel();
        checkbox.addStyleName("checkbox");
        if (selected) {
            checkbox.addStyleName("selected");
        }
        SimplePanel background = new SimplePanel();
        background.addStyleName("background");
        NoDragImage checkMark = new NoDragImage(MaterialDesignResources.INSTANCE.check_white(),
                14, 14);
        checkMark.addStyleName("checkmark");
        checkbox.add(background);
        checkbox.add(checkMark);
        templateTxt.setStyleName("templateTxt");
        this.add(checkbox);
        this.add(templateTxt);
        ClickStartHandler.init(this, new ClickStartHandler(true, true) {

            @Override
            public void onClickStart(int x, int y, PointerEventType type) {
                setSelected(!isSelected());
            }
        });
    }

    /**
     * @return true if checkbox is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected
     *            true if switch is on
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        updateCheckboxStyle();
    }

    /**
     * update style of checkbox depending on its status (selected or not selected)
     */
    public void updateCheckboxStyle() {
        Dom.toggleClass(checkbox, "selected", isSelected());
    }
}
