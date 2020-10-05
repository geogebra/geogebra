package org.geogebra.common.jre.io.trasnformer;

import java.util.zip.ZipInputStream;

import javax.annotation.CheckForNull;

/**
 * Extracts the geogebra.xml from the .ggb file
 */
public interface XmlExtractor {

    /**
     * @param zipInputStream the .ggb file in ZipInputStream form
     * @return geogebra.xml in byte array form
     */
    @CheckForNull
    byte[] getBytes(ZipInputStream zipInputStream);
}
