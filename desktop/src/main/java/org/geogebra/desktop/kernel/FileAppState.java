package org.geogebra.desktop.kernel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.geogebra.common.jre.io.MyXMLioJre;
import org.geogebra.common.kernel.AppState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * App State that is saved as a temporary file.
 */
public class FileAppState implements AppState {

    private static final String TEMP_FILE_PREFIX = "GeoGebraUndoInfo";

    private File file;

    public FileAppState(StringBuilder xml) throws IOException  {
        // create temp file
        file = File.createTempFile(TEMP_FILE_PREFIX, ".ggb");
        // Remove when program ends
        file.deleteOnExit();

        // create file
        FileOutputStream fos = new FileOutputStream(file);
        MyXMLioJre.writeZipped(fos, xml);
        fos.close();
    }

    @Override
    public String getXml() {
        return null;
    }

    @Override
    @SuppressFBWarnings({ "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE",
            "don't need to check return value" })
    public void delete() {
        file.delete();
    }

    /**
     * Unwrap the file
     *
     * @return file
     */
    public File getFile() {
        return file;
    }

    @Override
    public boolean equalsTo(AppState state) {
        return false;
    }
}
