package org.geogebra.common.gui.view.algebra;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.web.test.AV;
import org.geogebra.web.test.AppMocker;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class AlgebraItemTest {

    private AV av;

    @Test
    public void shouldShowBothRows() {
        av = new AV();
        testShouldShowBothRowsInGraphing();
        testShouldShowBothRowsInGeometry();
    }

    private void testShouldShowBothRowsInGraphing() {
        av.setApp(AppMocker.mockGraphing(getClass()));

        checkShouldShowBothRowsForLine(false);
        checkShouldShowBothRowsForFitLine(true);
    }

    private void checkShouldShowBothRowsForLine(boolean shouldShowBothRows) {
        GeoElement line = av.enter("Line((0,0), (1,1))");
        assertThat(AlgebraItem.shouldShowBothRows(line), is(shouldShowBothRows));
    }

    private void checkShouldShowBothRowsForFitLine(boolean shouldShowBothRows) {
        GeoElement fitLine = av.enter("FitLine((0,0), (1,1))");
        assertThat(AlgebraItem.shouldShowBothRows(fitLine), is(shouldShowBothRows));
    }

    private void testShouldShowBothRowsInGeometry() {
        App app = AppMocker.mockGeometry(getClass());
        app.getSettings().getAlgebra().setStyle(AlgebraStyle.DefinitionAndValue);
        av.setApp(app);

        checkShouldShowBothRowsForLine(true);
        checkShouldShowBothRowsForFitLine(true);
        checkShouldShowBothRowsForCircle(true);
    }

    private void checkShouldShowBothRowsForCircle(boolean shouldShowBothRows) {
        GeoElement circle = av.enter("Circle((0,0), (1,1))");
        assertThat(AlgebraItem.shouldShowBothRows(circle), is(shouldShowBothRows));
    }
}