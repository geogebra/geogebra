package org.geogebra.common.main.undo;

/**
 * Interface for application state
 */
public interface AppState {

	/** @return the application state in XML */
    String getXml();

    /** deletes this application state (i.e. deletes file) */
    void delete();

	/**
	 * Check if contents of this app state equals to another
	 * 
	 * @param state
	 *            other state
	 * @return are they equal?
	 */
    boolean equalsTo(AppState state);
}
