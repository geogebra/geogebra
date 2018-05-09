package org.geogebra.keyboard.web;

import com.google.gwt.user.client.ui.*;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;

import java.util.ArrayList;
import java.util.List;

public class KeyboardSwitcher extends FlowPanel {

    private TabbedKeyboard tabbedkeyboard;

    private FlowPanel contents;
    private List<Button> switches;
    private CustomButton closeButton;
    private ToggleButton moreButton;
    boolean isSpecialActive = false;

    public KeyboardSwitcher(TabbedKeyboard tabbedkeyboard) {
        this.tabbedkeyboard = tabbedkeyboard;

        addStyleName("KeyboardSwitcher");
        add(makeCloseButton());
        contents = new FlowPanel();
        contents.addStyleName("switcherContents");
        add(contents);
        switches = new ArrayList<>();
    }

    public void addMoreButton() {
        contents.add(makeMoreButton());
    }

    public void addSwitch(final KeyPanelBase keyboard, String string) {
        Button btn = makeSwitcherButton(keyboard, string);
        switches.add(btn);
        contents.add(btn);
    }

    private Button makeSwitcherButton(final KeyPanelBase keyboard,
                                      String string) {
        final Button ret = new Button(string);
        ClickStartHandler.init(ret, new ClickStartHandler(true, true) {

            @Override
            public void onClickStart(int x, int y, PointerEventType type) {
                tabbedkeyboard.hideTabs();
                unselectAll();
                tabbedkeyboard.currentKeyboard = keyboard;
                keyboard.setVisible(true);
                setSelected(ret, true);
            }
        });
        return ret;
    }

    public void setSelected(Button btn, boolean value) {
        if (value) {
            btn.addStyleName("selected");
        } else {
            btn.removeStyleName("selected");
        }
    }

    public void unselectAll() {
        for (Widget btn : switches) {
            btn.removeStyleName("selected");
        }
    }

    private Widget makeCloseButton() {
        Image img = new Image(KeyboardResources.INSTANCE
                .keyboard_close_black().getSafeUri().asString());
        img.setAltText(tabbedkeyboard.locale.getMenu("Close"));
        Image hoverImg = new Image(KeyboardResources.INSTANCE
                .keyboard_close_purple().getSafeUri().asString());
        hoverImg.setAltText(tabbedkeyboard.locale.getMenu("Close"));
        closeButton = new CustomButton() {
            // it's abstract for some reason
        };
        closeButton.getElement().setAttribute("aria-label",
                tabbedkeyboard.locale.getMenu("Close"));

        closeButton.getUpFace().setImage(img);
        closeButton.getUpHoveringFace().setImage(hoverImg);
        closeButton.addStyleName("closeTabbedKeyboardButton");
        ClickStartHandler.init(closeButton, new ClickStartHandler() {

            @Override
            public void onClickStart(int x, int y, PointerEventType type) {
                tabbedkeyboard.closeButtonClicked();
            }
        });
        return closeButton;
    }

    private Widget makeMoreButton() {
        Image img = new Image(KeyboardResources.INSTANCE.keyboard_more()
                .getSafeUri().asString());
        img.setAltText(tabbedkeyboard.locale.getMenu("Commands"));
        Image hoverImg = new Image(KeyboardResources.INSTANCE
                .keyboard_more_purple().getSafeUri().asString());
        hoverImg.setAltText(tabbedkeyboard.locale.getMenu("Commands"));
        moreButton = new ToggleButton(img, hoverImg);
        moreButton.getElement().setAttribute("aria-label",
                tabbedkeyboard.locale.getMenu("Commands"));

        moreButton.getUpHoveringFace().setImage(hoverImg);
        moreButton.addStyleName("moreKeyboardButton");
        ClickStartHandler.init(moreButton, new ClickStartHandler() {

            @Override
            public void onClickStart(int x, int y, PointerEventType type) {
                tabbedkeyboard.showHelp(
                        getMoreButton().getAbsoluteLeft()
                                + getMoreButton().getOffsetWidth(),
                        getMoreButton().getAbsoluteTop());
            }
        });
        return moreButton;
    }

    public ToggleButton getMoreButton() {
        return moreButton;
    }

    public void reset() {
        if (moreButton != null) {
            moreButton.setValue(false);
        }
    }

    public void select(int idx) {
        isSpecialActive = idx == TabbedKeyboard.TAB_SPECIAL;

        if (idx == TabbedKeyboard.TAB_SPECIAL) {
            setSelected(switches.get(TabbedKeyboard.TAB_ABC), true);
        }

        FlowPanel tabs = tabbedkeyboard.getTabs();
        for (int i = 0; i < tabs.getWidgetCount(); i++) {
            tabs.getWidget(i).setVisible(i == idx);
            setSelected(switches.get(i), i == idx);
        }
    }

    public FlowPanel getContent() {
        return contents;
    }
}