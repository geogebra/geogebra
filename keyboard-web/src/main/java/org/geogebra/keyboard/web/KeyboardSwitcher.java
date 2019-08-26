package org.geogebra.keyboard.web;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.GCustomButton;
import org.geogebra.web.html5.gui.util.GToggleButton;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class KeyboardSwitcher extends FlowPanel {

    private TabbedKeyboard tabbedkeyboard;

    private FlowPanel contents;
    private List<SwitcherButton> switches;
	private GToggleButton moreButton;

    private class SwitcherButton extends Button {

        private KeyPanelBase keyboard;

        public SwitcherButton(String label, KeyPanelBase keyboard) {
            super(label);
            this.keyboard = keyboard;

            ClickStartHandler.init(this, new ClickStartHandler(true, true) {

                @Override
                public void onClickStart(int x, int y, PointerEventType type) {
                    select();
                }
            });
        }

        public void select() {
            tabbedkeyboard.hideTabs();
            unselectAll();
            keyboard.setVisible(true);
            setSelected(this, true);
        }
    }

	/**
	 * @param tabbedkeyboard
	 *            keyboard
	 */
    public KeyboardSwitcher(TabbedKeyboard tabbedkeyboard) {
        this.tabbedkeyboard = tabbedkeyboard;
        addStyleName("KeyboardSwitcher");
        setup();
    }

	protected void setup() {
        addCloseButton();
        contents = new FlowPanel();
        contents.addStyleName("switcherContents");
        add(contents);
        switches = new ArrayList<>();
    }

	protected void addSwitch(final KeyPanelBase keyboard, String string) {
        SwitcherButton btn = new SwitcherButton(string, keyboard);
        switches.add(btn);
        contents.add(btn);
    }

	protected void setSelected(Button btn, boolean value) {
        if (value) {
            btn.addStyleName("selected");
        } else {
            btn.removeStyleName("selected");
        }
    }

	protected void unselectAll() {
        for (Widget btn : switches) {
            btn.removeStyleName("selected");
        }
    }

    protected void addCloseButton() {
        Image img = new Image(KeyboardResources.INSTANCE
                .keyboard_close_black().getSafeUri().asString());
        img.setAltText(tabbedkeyboard.locale.getMenu("Close"));
        Image hoverImg = new Image(KeyboardResources.INSTANCE
                .keyboard_close_purple().getSafeUri().asString());
        hoverImg.setAltText(tabbedkeyboard.locale.getMenu("Close"));
		GCustomButton closeButton = new GCustomButton() {
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
        add(closeButton);
    }

	protected final void addMoreButton() {
        Image img = new Image(KeyboardResources.INSTANCE.keyboard_more()
                .getSafeUri().asString());
        img.setAltText(tabbedkeyboard.locale.getMenu("Commands"));
        Image hoverImg = new Image(KeyboardResources.INSTANCE
                .keyboard_more_purple().getSafeUri().asString());
        hoverImg.setAltText(tabbedkeyboard.locale.getMenu("Commands"));
		moreButton = new GToggleButton(img, hoverImg);
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
        contents.add(moreButton);
    }

	public GToggleButton getMoreButton() {
        return moreButton;
    }

	protected void reset() {
        if (moreButton != null) {
            moreButton.setValue(false);
        }
    }

	/**
	 * @param keyboardType
	 *            keyboard type
	 */
	protected void select(KeyboardType keyboardType) {
		if (keyboardType == KeyboardType.SPECIAL) {
			tabbedkeyboard.hideTabs();
			tabbedkeyboard.getTabs().getWidget(keyboardType.getIndex())
					.setVisible(true);
		} else {
			switches.get(keyboardType.getIndex()).select();
		}
	}

    public FlowPanel getContent() {
        return contents;
    }

	/**
	 * Shows the More button
	 */
	public void showMoreButton() {
		moreButton.setVisible(true);
	}

	/**
	 * Hides the More button
	 */
    public void hideMoreButton() {
		moreButton.setVisible(false);
	}
}