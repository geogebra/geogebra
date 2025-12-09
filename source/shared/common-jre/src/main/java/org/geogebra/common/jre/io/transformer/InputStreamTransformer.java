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

package org.geogebra.common.jre.io.transformer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.zip.ZipInputStream;

import javax.annotation.CheckForNull;

import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.jre.io.MyXMLioJre;
import org.geogebra.common.jre.io.file.InputStreamZipFile;

/**
 * Transforms the .ggb file's InputStream into a Reader
 */
public class InputStreamTransformer {

    private XmlExtractor xmlExtractor;

    public InputStreamTransformer() {
        this.xmlExtractor = new XmlExtractor();
    }

	/**
	 * @param inputStream input stream
	 * @return reader reader
	 */
	public @CheckForNull Reader getReader(InputStream inputStream) {
        return getReader(new InputStreamZipFile(inputStream));
    }

    private Reader getReader(InputStreamZipFile inputStreamZipFile) {
        return getReader(new ZipInputStream(inputStreamZipFile.getInputStream()));
    }

    private Reader getReader(ZipInputStream zip) {
        byte[] bytes = xmlExtractor.getBytes(zip);
        if (bytes != null) {
            return getReader(bytes);
        } else {
            return null;
        }
    }

    private Reader getReader(byte[] bytes) {
        ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
        MyXMLioJre.XMLStreamInputStream ir = new MyXMLioJre.XMLStreamInputStream(bs);
        return getReader(ir);
    }

    private Reader getReader(MyXMLio.XMLStream stream) {
        MyXMLioJre.XMLStreamJre streamJre = (MyXMLioJre.XMLStreamJre) stream;
        try {
            return streamJre.getReader();
        } catch (Exception e) {
            return null;
        }
    }
}
