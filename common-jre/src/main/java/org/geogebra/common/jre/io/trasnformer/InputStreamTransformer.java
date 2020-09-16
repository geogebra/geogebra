package org.geogebra.common.jre.io.trasnformer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.zip.ZipInputStream;

import javax.annotation.CheckForNull;

import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.jre.io.MyXMLioJre;
import org.geogebra.common.jre.io.file.InputStreamZipFile;

public class InputStreamTransformer {

    private XmlExtractor xmlExtractor;

    public InputStreamTransformer(XmlExtractor xmlExtractor) {
        this.xmlExtractor = xmlExtractor;
    }

    @CheckForNull
    public Reader getReader(InputStream inputStream) {
        return getReader(new InputStreamZipFile(inputStream));
    }

    @CheckForNull
    private Reader getReader(InputStreamZipFile inputStreamZipFile) {
        return getReader(new ZipInputStream(inputStreamZipFile.getInputStream()));
    }

    @CheckForNull
    private Reader getReader(ZipInputStream zip) {
        byte[] bytes = xmlExtractor.getBytes(zip);
        if (bytes != null) {
            return getReader(bytes);
        } else {
            return null;
        }
    }

    @CheckForNull
    private Reader getReader(byte[] bytes) {
        ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
        MyXMLioJre.XMLStreamInputStream ir = new MyXMLioJre.XMLStreamInputStream(bs);
        return getReader(ir);
    }

    @CheckForNull
    private Reader getReader(MyXMLio.XMLStream stream) {
        MyXMLioJre.XMLStreamJre streamJre = (MyXMLioJre.XMLStreamJre) stream;
        try {
            return streamJre.getReader();
        } catch (Exception e) {
            return null;
        }
    }
}
