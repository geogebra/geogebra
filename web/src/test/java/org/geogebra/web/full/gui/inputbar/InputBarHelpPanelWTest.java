package org.geogebra.web.full.gui.inputbar;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class InputBarHelpPanelWTest {

    @Test
    public void onlineHelpShownIfMenuBarIsShown() {
        AppletParameters articleElement =
                new AppletParameters("classic")
                    .setAttribute("showMenuBar", "true");
        AppWFull app = AppMocker.mockApplet(articleElement);

        InputBarHelpPanelW inputBarHelpPanelW
                = Mockito.spy(new InputBarHelpPanelW(app));

        inputBarHelpPanelW.updateGUI(300);

        Mockito.verify(inputBarHelpPanelW, Mockito.times(1))
                .showOnlineHelpButton(true);
    }

    @Test
    public void onlineHelpNotShownIfMenuBarIsNotShown() {
        AppletParameters articleElement =
                new AppletParameters("classic")
                        .setAttribute("showMenuBar", "false");
        AppWFull app = AppMocker.mockApplet(articleElement);

        InputBarHelpPanelW inputBarHelpPanelW
                = Mockito.spy(new InputBarHelpPanelW(app));

        inputBarHelpPanelW.updateGUI(300);

        Mockito.verify(inputBarHelpPanelW, Mockito.times(1))
                .showOnlineHelpButton(false);
    }

    @Before
    public void rootPanel() {
        this.getClass().getClassLoader().setDefaultAssertionStatus(false);
    }
}