package org.geogebra.web.solver.keyboard;

import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.KeyboardSwitcher;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.util.CSSAnimation;

public class SolverKeyboard extends TabbedKeyboard {

	/**
	 * @param appKeyboard
	 *            keyboard context
	 */
	public SolverKeyboard(HasKeyboard appKeyboard) {
		super(appKeyboard, false);
        switcher = new KeyboardSwitcher(this) {

            @Override
            public void addCloseButton() {
                //don't
            }
        };
    }

    @Override
    public void closeButtonClicked() {
        setVisible(false);
    }

	/**
	 * Hide the keyboard.
	 */
    public void hide() {
        addStyleName("animatingOut");
        CSSAnimation.runOnAnimation(new Runnable() {
            @Override
            public void run() {
                setVisible(false);
            }
        }, getElement(), "animatingOut");
    }

    @Override
    public void show() {
        setVisible(true);
        addStyleName("animating");
    }
}
