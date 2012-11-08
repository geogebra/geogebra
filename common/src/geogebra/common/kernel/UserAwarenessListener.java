/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel;

/**
 * Interfaces to keep informed about certain user actions.
 *
 */
public interface UserAwarenessListener {
	
	// Custom Tool Methods
	public void addMacro(Macro newMacro);
	
	public void removeMacro(Macro macro);
	
	public void removeAllMacros();
	
	public void setMacroCommandName(Macro macro, String cmdName);

	// File Loading Methods
	public void fileLoading();
	
	public void fileLoadComplete();
}
