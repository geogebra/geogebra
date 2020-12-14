package org.geogebra.common.jre.io;

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
        assertThat(attributes.getAppCode(), equalTo("suite"));
        assertThat(attributes.getSubAppCode(), equalTo("cas"));
    }
}