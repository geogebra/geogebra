/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * Creates a state and writes it to filesystem
	 * @param xml construction XML
	 * @throws IOException if saving fails
	 */
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
