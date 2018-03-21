package org.geogebra.common.io.file;

/* Default implementation of ZipFile */
public class ByteArrayZipFile implements ZipFile {

    private byte[] zipData;

    public ByteArrayZipFile(byte[] zipData) {
        this.zipData = zipData;
    }

    public byte[] getByteArray() {
        return zipData;
    }
}
