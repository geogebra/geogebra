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

        RadioTreeItem radioTreeItem = new RadioTreeItem(app.getKernel());

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

        RadioTreeItem radioTreeItem = new RadioTreeItem(app.getKernel());

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

        RadioTreeItem radioTreeItem = new RadioTreeItem(app.getKernel());

        ContextMenuAVPlus contextMenuAVPlus = new ContextMenuAVPlus(radioTreeItem);
        contextMenuAVPlus.setLabels();
        assertThat(contextMenuAVPlus.hasImageItem(), equalTo(false));
    }
}
