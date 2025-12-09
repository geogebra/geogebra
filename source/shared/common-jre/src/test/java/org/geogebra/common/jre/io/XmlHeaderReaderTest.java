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
 
package org.geogebra.common.jre.io;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SUITE_APPCODE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class XmlHeaderReaderTest {

    private  XmlHeaderReader xmlHeaderReader = new XmlHeaderReader();

    @Test
    public void testGetSubAppCode() {
        String header = "<geogebra format=\"5.0\" version=\"5.0.604.0\" app=\"suite\""
                + " subApp=\"cas\" platform=\"a\" id=\"5d4d7fa5-d91c-4019-8218-c02d5789c3aa\""
                + "  xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/apps/xsd/ggb.xsd\""
                + " xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >";
        XmlHeaderReader.HeaderAttributes attributes = xmlHeaderReader.getHeaderAttributes(header);
        assertThat(attributes.getAppCode(), equalTo(SUITE_APPCODE));
        assertThat(attributes.getSubAppCode(), equalTo(CAS_APPCODE));
    }
}