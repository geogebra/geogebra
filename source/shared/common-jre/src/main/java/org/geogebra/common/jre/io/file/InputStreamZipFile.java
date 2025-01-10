package org.geogebra.common.jre.io.file;

import java.io.InputStream;

import org.geogebra.common.io.file.ZipFile;

public class InputStreamZipFile implements ZipFile {

    private InputStream zipData;

    public InputStreamZipFile(InputStream zipData) {
        this.zipData = zipData;
    }

    public InputStream getInputStream() {
        return zipData;
    }
}
