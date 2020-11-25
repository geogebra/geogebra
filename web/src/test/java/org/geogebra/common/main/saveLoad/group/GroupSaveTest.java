package org.geogebra.common.main.saveLoad.group;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoJoinPoints;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.geogebra.web.util.file.FileIO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.WithClassesToStub;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;



@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({JLMContext2d.class, RootPanel.class})
public class GroupSaveTest {
    private static AppWFull app;
    private static Construction cons;

    @Before
    public void initTest() {
        AppletParameters articleElement = new AppletParameters("notes");
        app = AppMocker.mockApplet(articleElement);
        cons = app.getKernel().getConstruction();
    }

    @Test
    public void testSaveGroup() {
        GeoPoint A = new GeoPoint(cons, "A", 0, 0, 1);
        GeoPoint B = new GeoPoint(cons, "B", 3, 0, 1);
        GeoPoint C = new GeoPoint(cons, "C", 3, 3, 1);
        AlgoJoinPoints line = new AlgoJoinPoints(cons, "g", B, C);
        ArrayList<GeoElement> geos = new ArrayList<>();
        geos.add(A);
        geos.add(line.getOutput(0));
        cons.createGroup(geos);
        String pathString = "src/test/java/org/geogebra/common/main/saveLoad/group" +
                "/consWithGroupXML.txt";
        String fileContent = FileIO.load(pathString);
        StringBuilder consXMLStrBuilder = new StringBuilder();
        app.getKernel().getConstruction().getConstructionXML(consXMLStrBuilder, false);
        assertEquals(fileContent, consXMLStrBuilder.toString().trim());
    }

    @Test
    public void testLoadGroup() {
        String pathString = "src/test/java/org/geogebra/common/main/saveLoad/group" +
                "/consWithGroupXML.txt";
        String fileContent = FileIO.load(pathString);
        app.getGgbApi().evalXML(fileContent);
        ArrayList<GeoElement> groupedGeos = cons.getGroups().get(0).getGroupedGeos();
        assertThat(groupedGeos.size(), equalTo(2));
        assertThat(groupedGeos.get(0).getLabelSimple(), equalTo("A"));
        assertThat(groupedGeos.get(1).getLabelSimple(), equalTo("g"));
    }

}
