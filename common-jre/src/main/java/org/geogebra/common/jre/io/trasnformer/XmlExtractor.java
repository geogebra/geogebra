package org.geogebra.common.jre.io.trasnformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.CheckForNull;

import org.geogebra.common.io.MyXMLio;

/**
 * Extracts the geogebra.xml from the .ggb file
 */
public class XmlExtractor {

    /**
     * @param zipInputStream the .ggb file in ZipInputStream form
     * @return geogebra.xml in byte array form
     */
    @CheckForNull
    public byte[] getBytes(ZipInputStream zipInputStream) {
        try {
            while (true) {
                ZipEntry entry =  zipInputStream.getNextEntry();
                if (entry == null || MyXMLio.XML_FILE.equals(entry.getName())) {
                    return loadIntoMemory(zipInputStream);
                }
            }
        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * Writes all contents of the given InputStream to a byte array.
     * @param is input stream
     * @return Byte array with the content of the input stream.
     * @throws IOException when reading or writing fails
     */
    public byte[] loadIntoMemory(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        copyStream(is, bos);
        return bos.toByteArray();
    }

    private void copyStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] buf = new byte[4096];
        int len;
        while ((len = in.read(buf)) > -1) {
            out.write(buf, 0, len);
        }
    }
}
