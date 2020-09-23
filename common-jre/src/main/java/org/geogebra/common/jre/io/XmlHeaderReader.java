package org.geogebra.common.jre.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.annotation.CheckForNull;

import org.geogebra.common.jre.io.trasnformer.InputStreamTransformer;
import org.geogebra.common.jre.io.trasnformer.XmlExtractor;

/**
 * Extracts attribute values from the header tag of the geogebra.xml
 */
public class XmlHeaderReader {

    /**
     * Contains the extracted attributes.
     */
    public static class HeaderAttributes {

        private String appCode;
        private String subAppCode;

        @CheckForNull
        public String getAppCode() {
            return appCode;
        }

        @CheckForNull
        public String getSubAppCode() {
            return subAppCode;
        }
    }

    private InputStreamTransformer transformer;

    public XmlHeaderReader(XmlExtractor xmlExtractor) {
        this.transformer = new InputStreamTransformer(xmlExtractor);
    }

    /**
     * @param inputStream InputStream of the .ggb file
     * @return new HeaderAttributes instance containing the app code of the app and sub-app
     */
    @CheckForNull
    public HeaderAttributes getHeaderAttributes(InputStream inputStream) {
        Reader reader = transformer.getReader(inputStream);
        String xmlString = reader != null ? getString(reader) : null;
        String headerString = xmlString != null ? getHeader(xmlString) : null;
        return headerString != null ? getHeaderAttributes(headerString) : null;
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

    @CheckForNull
    private String getHeader(String xml) {
        int headerStartIndex = xml.indexOf("geogebra") - 1;
        if (headerStartIndex >= 0) {
            int headerEndIndex = xml.indexOf('>', headerStartIndex) + 1;
            return xml.substring(headerStartIndex, headerEndIndex);
        }
        return null;
    }

    HeaderAttributes getHeaderAttributes(String header) {
        HeaderAttributes attributes = new HeaderAttributes();
        attributes.appCode = getAttributeValue("app", header);
        attributes.subAppCode = getAttributeValue("subApp", header);
        return attributes;
    }

    @CheckForNull
    private String getAttributeValue(String attributeName, String header) {
        String searchedExpression = attributeName + "=\"";
        int attributeNameIndex = header.indexOf(searchedExpression);
        if (attributeNameIndex >= 0) {
            int attributeValueStartIndex = attributeNameIndex + searchedExpression.length();
            int attributeValueEndIndex = header.indexOf('"', attributeValueStartIndex);
            return header.substring(attributeValueStartIndex, attributeValueEndIndex);
        }
        return null;
    }
}
