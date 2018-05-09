package org.geogebra.web.solver.keyboard;

import org.geogebra.common.main.App;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.KeyboardSwitcher;
import org.geogebra.keyboard.web.TabbedKeyboard;

public class SolverKeyboard extends TabbedKeyboard {

    public SolverKeyboard(App app, HasKeyboard appKeyboard) {
        super(app, appKeyboard);
        switcher = new KeyboardSwitcher(this) {
            @Override
            public void addMoreButton() {
                //don't!
            }
        };
    }

    @Override
    protected void closeButtonClicked() {
        tabs.setVisible(!tabs.isVisible());
    }
}
