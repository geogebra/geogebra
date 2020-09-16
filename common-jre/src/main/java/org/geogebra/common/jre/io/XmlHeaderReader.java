package org.geogebra.common.jre.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.annotation.CheckForNull;

import org.geogebra.common.jre.io.trasnformer.InputStreamTransformer;
import org.geogebra.common.jre.io.trasnformer.XmlExtractor;

public class XmlHeaderReader {

    private InputStreamTransformer transformer;

    public XmlHeaderReader(XmlExtractor xmlExtractor) {
        this.transformer = new InputStreamTransformer(xmlExtractor);
    }

    @CheckForNull
    public String getSubAppCode(InputStream inputStream) {
        Reader reader = transformer.getReader(inputStream);
        String xmlString = reader != null ? getString(reader) : null;
        return xmlString != null ? getSubAppCode(xmlString) : null;
    }

    @CheckForNull
    private String getString(Reader reader) {
        int headerBufferLength = 512;
        StringBuilder headerBuilder = new StringBuilder(headerBufferLength);
        try {
            for (int i = 0; i < headerBufferLength; i++) {
                int character = reader.read();
                if (character == -1) {
                    break;
                }
                headerBuilder.append((char) character);
            }
            return headerBuilder.toString();
        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * @param xml xml
     * @return subApp value from the xml header
     */
    @CheckForNull
    protected String getSubAppCode(String xml) {
        String header = getHeader(xml);
        if (header == null) {
            return null;
        }
        int subAppIndex = header.indexOf("subApp=\"");
        if (subAppIndex >= 0) {
            int appCodeStartIndex = subAppIndex + 8;
            int appCodeEndIndex = header.indexOf('"', appCodeStartIndex);
            return header.substring(appCodeStartIndex, appCodeEndIndex);
        }
        return null;
    }

    @CheckForNull
    private String getHeader(String xml) {
        int headerStartIndex = xml.indexOf("geogebra") - 1;
        if (headerStartIndex >= 0) {
            int headerEndIndex = xml.indexOf('>', headerStartIndex) + 1;
            return xml.substring(headerStartIndex, headerEndIndex);
        }
        return null;
    }
}
