package org.geogebra.common.main;

public interface OpenFileListener {

	/**
	 * @return whether to unregister this afterwards
	 */
	public boolean onOpenFile();

}
