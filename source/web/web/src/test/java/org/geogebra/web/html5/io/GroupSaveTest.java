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

package org.geogebra.web.html5.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoJoinPoints;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.awt.JLMContext2D;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.geogebra.web.util.file.FileIO;
import org.gwtproject.user.client.ui.RootPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({JLMContext2D.class, RootPanel.class})
public class GroupSaveTest {
    private static AppWFull app;
    private static Construction cons;
    private final String pathString = "src/test/resources/org/geogebra/web/html5/io"
            + "/consWithGroupXML.txt";

    @Before
    public void initTest() {
        this.getClass().getClassLoader().setDefaultAssertionStatus(false);
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

        String fileContent = FileIO.load(pathString);
        XMLStringBuilder consXMLStrBuilder = new XMLStringBuilder();
        app.getKernel().getConstruction().getConstructionXML(consXMLStrBuilder, false);
        assertEquals(fileContent, consXMLStrBuilder.toString().trim());
    }

    @Test
    public void testLoadGroup() {
        String fileContent = FileIO.load(pathString);
        app.getGgbApi().evalXML(fileContent);
        ArrayList<GeoElement> groupedGeos = cons.getGroups().get(0).getGroupedGeos();
        assertThat(groupedGeos.size(), equalTo(2));
        assertThat(groupedGeos.get(0).getLabelSimple(), equalTo("A"));
        assertThat(groupedGeos.get(1).getLabelSimple(), equalTo("g"));
    }

}
