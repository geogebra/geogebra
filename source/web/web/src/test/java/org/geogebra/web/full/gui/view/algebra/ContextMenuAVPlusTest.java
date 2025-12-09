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

package org.geogebra.web.full.gui.view.algebra;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class ContextMenuAVPlusTest {

    @Test
    public void imageToolShownIfAppHasToolbar() {
        AppletParameters articleElement =
                new AppletParameters("classic")
                    .setAttribute("showToolBar", "true");
        AppWFull app = AppMocker.mockApplet(articleElement);

        RadioTreeItem radioTreeItem = new LaTeXTreeItem(app.getKernel(), app.getAlgebraView());

        ContextMenuAVPlus contextMenuAVPlus = new ContextMenuAVPlus(radioTreeItem);
        contextMenuAVPlus.setLabels();
        assertThat(contextMenuAVPlus.hasImageItem(), equalTo(true));
    }

    @Test
    public void noImageToolIfShowToolbarIsFalse() {
        AppletParameters articleElement =
                new AppletParameters("classic")
                        .setAttribute("showToolBar", "false");
        AppWFull app = AppMocker.mockApplet(articleElement);

        RadioTreeItem radioTreeItem = new LaTeXTreeItem(app.getKernel(), app.getAlgebraView());

        ContextMenuAVPlus contextMenuAVPlus = new ContextMenuAVPlus(radioTreeItem);
        contextMenuAVPlus.setLabels();
        assertThat(contextMenuAVPlus.hasImageItem(), equalTo(false));
    }

    @Test
    public void noImageToolIfCustomToolbarHasNoImageTool() {
        AppletParameters articleElement =
                new AppletParameters("classic")
                        .setAttribute("customToolBar", "1 2");
        AppWFull app = AppMocker.mockApplet(articleElement);

        RadioTreeItem radioTreeItem = new LaTeXTreeItem(app.getKernel(), app.getAlgebraView());

        ContextMenuAVPlus contextMenuAVPlus = new ContextMenuAVPlus(radioTreeItem);
        contextMenuAVPlus.setLabels();
        assertThat(contextMenuAVPlus.hasImageItem(), equalTo(false));
    }
}
