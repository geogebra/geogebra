package org.geogebra.common.main;

/**
 * Listener for File Open event.
 */
public interface OpenFileListener {

	/**
	 * @return whether to unregister this afterwards
	 */
	boolean onOpenFile();

}
