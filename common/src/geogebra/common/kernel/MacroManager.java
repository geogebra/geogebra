/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel;

import geogebra.common.main.AbstractApplication;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages macros (user defined tools).
 * 
 * @author Markus Hohenwarter
 */
public class MacroManager {
	
	private HashMap<String, Macro> macroMap; // maps macro name to macro object
	private ArrayList<Macro> macroList; // lists all macros	
	private AbstractApplication app;
	
	public MacroManager(AbstractApplication app) {
		macroMap = new HashMap<String, Macro>();
		macroList = new ArrayList<Macro>();
		this.app = app;
	}
		
	public void addMacro(Macro macro) {						
		macroMap.put(app.toLowerCase(macro.getCommandName()), macro);
		macroList.add(macro);
	}
	
	public Macro getMacro(String name) {
		return (Macro) macroMap.get(app.toLowerCase(name));
	}
	
	public void removeMacro(Macro macro) {
		macroMap.remove(app.toLowerCase(macro.getCommandName()));	
		macroList.remove(macro);		
	}	
	
	public void removeAllMacros() {
		macroMap.clear();
		macroList.clear();
	}
	
	

	/**
	 * Sets the command name of a macro.
	 */
	public void setMacroCommandName(Macro macro, String cmdName) {
		macroMap.remove(app.toLowerCase(macro.getCommandName()));
		macro.setCommandName(cmdName);
		macroMap.put(app.toLowerCase(macro.getCommandName()), macro);			
	}
	
	public Macro getMacro(int i) {
		return (Macro) macroList.get(i);		
	}
	
	public int getMacroID(Macro macro) {		
		for (int i=0; i < macroList.size(); i++) {
			if (macro == macroList.get(i))
				return i;			
		}
		return -1;				
	}
	
	public void setAllMacrosUnused() {
		for (int i=0; i < macroList.size(); i++) {
			((Macro) macroList.get(i)).setUnused();				
		}
	}
	
	/**
	 * Returns the current number of macros handled by this MacroManager. 
	 */
	public int getMacroNumber() {
		return macroList.size();
	}
	
	/**
	 * Returns an array of all macros handled by this MacroManager. 
	 */
	public ArrayList<Macro> getAllMacros() {
		return macroList;
	}
	
	/**
	 * Updates all macros that need to be 
	 */
	public final void notifyEuclidianViewCE() {		
		// save selected macros
		for (int i=0; i < macroList.size(); i++) {			
			Macro macro = (Macro) macroList.get(i);			
			macro.getMacroConstruction().notifyEuclidianViewCE();			
		}		
	}
	
	/**
	 * Returns an XML represenation of the specified macros in this kernel.	 
	 */
	public static String getMacroXML(ArrayList<Macro> macros) {				
		if (macros == null) return "";

		StringBuilder sb = new StringBuilder();	
		// save selected macros
		for (int i=0; i < macros.size(); i++) {				
			((Macro) macros.get(i)).getXML(sb);
		}						
		return sb.toString();
	}

}
