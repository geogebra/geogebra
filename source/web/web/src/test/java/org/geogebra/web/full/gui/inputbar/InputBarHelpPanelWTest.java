/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.inputbar;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.gwtproject.dom.client.TextAreaElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

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

}