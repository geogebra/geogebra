package org.geogebra.web.solver.keyboard;

import org.geogebra.common.main.App;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.KeyboardSwitcher;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.util.CSSAnimation;

public class SolverKeyboard extends TabbedKeyboard {

	/**
	 * @param app
	 *            application
	 * @param appKeyboard
	 *            keyboard context
	 */
    public SolverKeyboard(App app, HasKeyboard appKeyboard) {
		super(app, appKeyboard, false);
        switcher = new KeyboardSwitcher(this) {
            @Override
            public void addMoreButton() {
                //don't!
            }

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
