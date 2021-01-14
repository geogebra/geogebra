package org.geogebra.common.jre.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.jre.io.trasnformer.InputStreamTransformer;

/**
 * Extracts attribute values from the header tag of the geogebra.xml
 */
public class XmlHeaderReader {

    private InputStreamTransformer transformer;
    private final HeaderAttributes defaultHeaderAttributes;

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

        private HeaderAttributes addAppCode(String appCode) {
            this.appCode = appCode;
            return this;
        }
    }

    /**
     * Constructor
     */
    public XmlHeaderReader() {
        this.transformer = new InputStreamTransformer();
        defaultHeaderAttributes =
                new HeaderAttributes().addAppCode(GeoGebraConstants.GRAPHING_APPCODE);
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

    private String getHeader(String xml) {
        Pattern regex = Pattern.compile("<geogebra (.*?)>");
        Matcher matcher = regex.matcher(xml);
        return matcher.find() ? matcher.group(1) : null;
    }

    HeaderAttributes getHeaderAttributes(String header) {
        HeaderAttributes attributes = new HeaderAttributes();
        attributes.appCode = getAttributeValue("app", header);
        attributes.subAppCode = getAttributeValue("subApp", header);
        if (attributes.appCode != null || attributes.subAppCode != null) {
            return attributes;
        } else {
            return defaultHeaderAttributes;
        }
    }

    private String getAttributeValue(String attributeName, String header) {
        Pattern regex = Pattern.compile(attributeName + "=\"(.*?)\"");
        Matcher matcher = regex.matcher(header);
        return matcher.find() ? matcher.group(1) : null;
    }
}
