package org.geogebra.web.full.gui.components;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
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
public class ComponentCheckbox extends FlowPanel implements SetLabels {
    private Localization loc;
    private boolean selected;
    private FlowPanel checkbox;
    private Label checkboxLbl;
    private String checkboxTxt;

    /**
     * @param loc - localization
     * @param setSelected - true if checkbox should be selected
     * @param templateTxt - text of checkbox
     * @param callback - on click action
     */
    public ComponentCheckbox(Localization loc, boolean setSelected, String templateTxt,
            Runnable callback) {
        this.loc = loc;
        this.selected = setSelected;
        this.checkboxTxt = templateTxt;

        this.addStyleName("checkboxPanel");
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

        checkboxLbl = new Label();
        checkboxLbl.setStyleName("checkboxLbl");
        add(checkbox);
        add(checkboxLbl);
        ClickStartHandler.init(this, new ClickStartHandler(true, true) {

            @Override
            public void onClickStart(int x, int y, PointerEventType type) {
                setSelected(!isSelected());
                if (callback != null) {
                    callback.run();
                }
            }
        });

        setLabels();
    }

    /**
     * @param loc - localization
     * @param setSelected - true if checkbox should be selected
     * @param templateTxt - text of checkbox
     */
    public ComponentCheckbox(Localization loc, boolean setSelected, String templateTxt) {
        this(loc, setSelected, templateTxt, null);
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

    @Override
    public void setLabels() {
        checkboxLbl.setText(loc.getMenu(checkboxTxt));
    }
}
