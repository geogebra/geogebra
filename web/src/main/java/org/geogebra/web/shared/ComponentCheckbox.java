package org.geogebra.web.shared;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;

/**
 * material design checkbox component
 */
public class ComponentCheckbox extends FlowPanel {

    private boolean selected;

    /**
     *
     * @param setSelected true if the checkmark is shown
     */
    public ComponentCheckbox(boolean setSelected) {
        setSelected(setSelected);
        this.addStyleName("checkbox");
        SimplePanel background = new SimplePanel();
        background.addStyleName("background");
        NoDragImage checkMark = new NoDragImage(MaterialDesignResources.INSTANCE.check_white(),
                14, 14);
        checkMark.addStyleName("checkmark");
        this.add(background);
        this.add(checkMark);
        updateCheckboxStyle();
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
        if (isSelected()) {
            this.addStyleName("selected");
        } else {
            this.removeStyleName("selected");
        }
    }
}
