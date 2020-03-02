package org.geogebra.web.full.gui.menubar.action;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatSTL;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class DownloadActionsTest {

    private AppWFull app;

    @Before
    public void setUp() {
        app = spy(AppMocker.mockGraphing(getClass()));
    }

    @Test
    public void downloadColladaDaeTest() {
        DownloadColladaDaeAction downloadColladaDaeAction = new DownloadColladaDaeAction();
        downloadColladaDaeAction.execute(null, app);
        verify(app, times(1)).exportCollada(false);
    }

    @Test
    public void downloadColladaHtmlTest() {
        DownloadColladaHtmlAction downloadColladaHtmlAction = new DownloadColladaHtmlAction();
        downloadColladaHtmlAction.execute(null, app);
        verify(app, times(1)).exportCollada(true);
    }

    @Test
    public void downloadGgbTest() {
        MaterialsManagerI materialsManager = spy(app.getFileManager());
        when(app.getFileManager()).thenReturn(materialsManager);

        DownloadDefaultFormatAction downloadGgbAction = new DownloadDefaultFormatAction();
        downloadGgbAction.execute(null, app);
        verify(materialsManager, times(1)).export(app);
    }

    @Test
    public void downloadStlAction() {
        DownloadStlAction downloadStlAction = new DownloadStlAction();
        downloadStlAction.execute(null, app);
        verify(app, times(1)).setExport3D(any(FormatSTL.class));
    }
}