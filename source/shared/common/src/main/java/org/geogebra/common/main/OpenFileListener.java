package org.geogebra.common.main;

public interface OpenFileListener {

	/**
	 * @return whether to unregister this afterwards
	 */
	boolean onOpenFile();

}
