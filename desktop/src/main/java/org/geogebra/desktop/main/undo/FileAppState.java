package org.geogebra.desktop.main.undo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.geogebra.common.jre.io.MyXMLioJre;
import org.geogebra.common.main.undo.AppState;
import org.geogebra.common.util.debug.Log;

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
	public void delete() {
		if (!file.delete()) {
			Log.warn("Failed to delete " + file.getAbsolutePath());
		}
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
