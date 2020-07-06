package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.html5.util.ArticleElementInterface;
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
public class ContextMenuAVPlusTest {

    @Test
    public void imageToolShownIfAppHasToolbar() {
        ArticleElementInterface articleElement =
                new TestArticleElement("classic")
                    .attr("showToolBar", "true");
        AppWFull app = AppMocker.mockApplet(articleElement);

        RadioTreeItem radioTreeItem = new RadioTreeItem(app.getKernel());

        ContextMenuAVPlus contextMenuAVPlus
                = Mockito.spy(new ContextMenuAVPlus(radioTreeItem));

        contextMenuAVPlus.setLabels();
        Mockito.verify(contextMenuAVPlus, Mockito.times(1)).addImageItem();
    }

    @Test
    public void noImageToolIfShowToolbarIsFalse() {
        ArticleElementInterface articleElement =
                new TestArticleElement("classic")
                        .attr("showToolBar", "false");
        AppWFull app = AppMocker.mockApplet(articleElement);

        RadioTreeItem radioTreeItem = new RadioTreeItem(app.getKernel());

        ContextMenuAVPlus contextMenuAVPlus
                = Mockito.spy(new ContextMenuAVPlus(radioTreeItem));

        contextMenuAVPlus.setLabels();
        Mockito.verify(contextMenuAVPlus, Mockito.never()).addImageItem();
    }

    @Test
    public void noImageToolIfCustomToolbarHasNoImageTool() {
        ArticleElementInterface articleElement =
                new TestArticleElement("classic")
                        .attr("customToolBar", "1 2");
        AppWFull app = AppMocker.mockApplet(articleElement);

        RadioTreeItem radioTreeItem = new RadioTreeItem(app.getKernel());

        ContextMenuAVPlus contextMenuAVPlus
                = Mockito.spy(new ContextMenuAVPlus(radioTreeItem));

        contextMenuAVPlus.setLabels();
        Mockito.verify(contextMenuAVPlus, Mockito.never()).addImageItem();
    }

    @Before
    public void rootPanel() {
        this.getClass().getClassLoader().setDefaultAssertionStatus(false);
    }
}
