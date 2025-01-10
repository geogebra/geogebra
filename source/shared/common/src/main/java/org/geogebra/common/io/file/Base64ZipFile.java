package org.geogebra.common.io.file;

/* Base64 implementation of ZipFile for web */
public class Base64ZipFile implements ZipFile {

    private String zipData;

    public Base64ZipFile(String base64) {
        this.zipData = base64;
    }

    public String getBase64() {
        return zipData;
    }
}
