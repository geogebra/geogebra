package org.geogebra.web.full.gui.menubar.action;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class ImageExporterTest {

    private static final String EXTENSION = "pdf";

    private AppWFull app;
    private ImageExporter imageExporter;

    @Before
    public void setUp() {
        app = spy(AppMocker.mockGraphing(getClass()));
        imageExporter = new ImageExporter(app, EXTENSION);
    }

    @Test
    public void export() {
        MaterialsManagerI materialsManager = spy(app.getFileManager());
        when(app.getFileManager()).thenReturn(materialsManager);

        String exportUrl = "";
        imageExporter.export(exportUrl);
        verify(materialsManager, times(1))
                .showExportAsPictureDialog(exportUrl, app.getExportTitle(), EXTENSION, "ExportAsPicture", app);
    }
}