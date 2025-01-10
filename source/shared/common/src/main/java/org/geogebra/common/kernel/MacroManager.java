/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.util.StringUtil;

/**
 * Manages macros (user defined tools).
 * 
 * @author Markus Hohenwarter
 */
public class MacroManager {

	private HashMap<String, Macro> macroMap; // maps macro name to macro object
	private ArrayList<Macro> macroList; // lists all macros

	/**
	 * Creates new macro manager
	 * 
	 */
	public MacroManager() {
		macroMap = new HashMap<>();
		macroList = new ArrayList<>();
	}

	/**
	 * @param macro
	 *            macro to be added
	 */
	public void addMacro(Macro macro) {
		macroMap.put(StringUtil.toLowerCaseUS(macro.getCommandName()), macro);
		macroList.add(macro);
	}

	/**
	 * Returns macro with given name
	 * 
	 * @param name
	 *            macro's command name
	 * @return macro
	 */
	public Macro getMacro(String name) {
		return macroMap.get(StringUtil.toLowerCaseUS(name));
	}

	/**
	 * Removes given macro
	 * 
	 * @param macro
	 *            macro for removal
	 */
	public void removeMacro(Macro macro) {
		macroMap.remove(StringUtil.toLowerCaseUS(macro.getCommandName()));
		macroList.remove(macro);
	}

	/**
	 * Removes all macros
	 */
	public void removeAllMacros() {
		macroMap.clear();
		macroList.clear();
	}

	/**
	 * Sets the command name of a macro.
	 * 
	 * @param macro
	 *            macro
	 * @param cmdName
	 *            command name
	 */
	public void setMacroCommandName(Macro macro, String cmdName) {
		macroMap.remove(StringUtil.toLowerCaseUS(macro.getCommandName()));
		macro.setCommandName(cmdName);
		macroMap.put(StringUtil.toLowerCaseUS(macro.getCommandName()), macro);
	}

	/**
	 * @param i
	 *            index
	 * @return i-th macro from the list
	 */
	public Macro getMacro(int i) {
		return macroList.get(i);
	}

	/**
	 * @param macro
	 *            macro
	 * @return order of the macro in macro list
	 */
	public int getMacroID(Macro macro) {
		for (int i = 0; i < macroList.size(); i++) {
			if (macro == macroList.get(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * All macros are marked as unused
	 */
	public void setAllMacrosUnused() {
		for (int i = 0; i < macroList.size(); i++) {
			macroList.get(i).setUnused();
		}
	}

	/**
	 * Returns the current number of macros handled by this MacroManager.
	 * 
	 * @return current number of macros
	 */
	public int getMacroNumber() {
		return macroList.size();
	}

	/**
	 * Returns an array of all macros handled by this MacroManager.
	 * 
	 * @return an array of all macros handled by this MacroManager.
	 */
	public ArrayList<Macro> getAllMacros() {
		return macroList;
	}

	/**
	 * Updates all macros that need to be
	 * 
	 * @param prop
	 *            what property changed
	 */
	public final void notifyEuclidianViewCE(EVProperty prop) {
		// save selected macros
		for (int i = 0; i < macroList.size(); i++) {
			Macro macro = macroList.get(i);
			macro.getMacroConstruction().notifyEuclidianViewCE(prop);
		}
	}

	/**
	 * Returns an XML representation of the specified macros in this kernel.
	 * 
	 * @param macros
	 *            list of macros
	 * @return XML representation as one string
	 */
	public static String getMacroXML(List<Macro> macros) {
		if (macros == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		// save selected macros
		for (int i = 0; i < macros.size(); i++) {
			Macro macro = macros.get(i);
			if (macro != null) {
				macro.getXML(sb);
			}
		}
		return sb.toString();
	}

}
