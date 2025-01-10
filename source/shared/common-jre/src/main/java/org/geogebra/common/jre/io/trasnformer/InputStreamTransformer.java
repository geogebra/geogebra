package org.geogebra.common.jre.io.trasnformer;

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

    @CheckForNull
    public Reader getReader(InputStream inputStream) {
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
