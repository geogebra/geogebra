package org.geogebra.common.jre.io.trasnformer;

import java.util.zip.ZipInputStream;

import javax.annotation.CheckForNull;

public interface XmlExtractor {

    @CheckForNull
    byte[] getBytes(ZipInputStream zipInputStream);
}
