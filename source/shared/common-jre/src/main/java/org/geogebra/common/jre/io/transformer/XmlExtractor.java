package org.geogebra.common.jre.io.transformer;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.CheckForNull;

import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.jre.io.StreamUtil;

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
                    return StreamUtil.loadIntoMemory(zipInputStream);
                }
            }
        } catch (IOException ignored) {
            return null;
        }
    }
}
