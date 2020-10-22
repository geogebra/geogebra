/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.gui.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

public class ToolManagerDialogModel {
	@Weak
	private final App app;
	final Localization loc;
	private final List<Macro> deletedMacros;
	private final ToolManagerDialogListener listener;

	public interface ToolManagerDialogListener {

		void removeMacroFromToolbar(int i);

		void refreshCustomToolsInToolBar();

		void uploadWorksheet(ArrayList<Macro> macros);

	}

	/**
	 * @param app
	 *            application
	 * @param listener
	 *            listener
	 */
	public ToolManagerDialogModel(App app, ToolManagerDialogListener listener) {
		this.app = app;
		this.loc = app.getLocalization();
		this.listener = listener;
		deletedMacros = new ArrayList<>();
	}

	/**
	 * Will delete all Macros and add all macros in order
	 * 
	 * @param macros
	 *            the Macros to be added
	 */
	public void addMacros(Object[] macros) {
		// update order of macros:
		// remove all macros from kernel and add them again in new order
		Kernel kernel = app.getKernel();

		kernel.removeAllMacros();

		for (Object obj : macros) {
			kernel.addMacro((Macro) obj);
		}
	}

	/**
	 * Deletes all selected tools that are not used in the construction.
	 * 
	 * @param sel
	 *            tools selected for deletion
	 * @return whether some tools were deleted
	 */
	public boolean deleteTools(List<Macro> sel) {
		if (sel == null || sel.size() == 0) {
			return false;
		}

		boolean didDeletion = false;
		boolean changeToolBar = false;
		boolean foundUsedMacro = false;
		StringBuilder macroNames = new StringBuilder();
		Kernel kernel = app.getKernel();
		app.getSelectionManager().setSelectedGeos(new ArrayList<GeoElement>());
		deletedMacros.clear();
		for (int i = 0; i < sel.size(); i++) {
			Macro macro = sel.get(i);
			if (!macro.isUsed()) {
				// delete macro
				changeToolBar = changeToolBar || macro.isShowInToolBar();
				listener.removeMacroFromToolbar(kernel.getMacroID(macro)
						+ EuclidianConstants.MACRO_MODE_ID_OFFSET);

				kernel.removeMacro(macro);
				listener.refreshCustomToolsInToolBar();
				deletedMacros.add(macro);
				didDeletion = true;
			} else {
				// don't delete, remember name
				ArrayList<GeoElement> geos = macro.getDependentGeos();
				Iterator<GeoElement> curr = geos.iterator();
				while (curr.hasNext()) {
					app.getSelectionManager().addSelectedGeo(curr.next());
				}
				foundUsedMacro = true;
				macroNames.append("\n");
				macroNames.append(macro.getToolOrCommandName());
				macroNames.append(": ");
				macroNames.append(macro.getNeededTypesString());
			}
		}

		if (didDeletion) {
			// we reinit the undo info to make sure an undo does not use
			// any deleted tool
			kernel.initUndoInfo();
		}

		if (foundUsedMacro) {
			app.showError(Errors.ToolDeleteUsed, macroNames.toString());
		}

		return changeToolBar;
	}

	/**
	 * @return deleted tools
	 */
	public List<Macro> getDeletedMacros() {
		return deletedMacros;
	}

	/**
	 * @param sel
	 *            selected macros
	 */
	public void uploadToGeoGebraTube(final Object[] sel) {
		app.setWaitCursor();
		try {
			app.getSelectionManager().clearSelectedGeos(true, false);
			app.updateSelection(false);

			if (sel == null || sel.length == 0) {
				return;
			}

			// we need to save all selected tools and all tools
			// that are used by the selected tools
			LinkedHashSet<Macro> tools = new LinkedHashSet<>();
			for (int i = 0; i < sel.length; i++) {
				Macro macro = (Macro) sel[i];
				ArrayList<Macro> macros = macro.getUsedMacros();
				if (macros != null) {
					tools.addAll(macros);
				}
				tools.add(macro);
			}

			// create Macro array list from tools set
			ArrayList<Macro> macros = new ArrayList<>(tools.size());
			Iterator<Macro> it = tools.iterator();
			while (it.hasNext()) {
				macros.add(it.next());
			}

			listener.uploadWorksheet(macros);

		} catch (Exception e) {
			Log.debug("Uploading failed");
			e.printStackTrace();
		}
		app.setDefaultCursor();
	}

	/**
	 * Saves all selected tools in a new file.
	 * 
	 * @param sel
	 *            selected tools
	 * @return selected tools and dependencies
	 */
	public ArrayList<Macro> getAllTools(Macro[] sel) {

		// we need to save all selected tools and all tools
		// that are used by the selected tools
		LinkedHashSet<Macro> tools = new LinkedHashSet<>();
		for (int i = 0; i < sel.length; i++) {
			Macro macro = sel[i];
			ArrayList<Macro> macros = macro.getUsedMacros();
			if (macros != null) {
				tools.addAll(macros);
			}
			tools.add(macro);
		}

		// create Macro array list from tools set
		ArrayList<Macro> macros = new ArrayList<>(tools.size());
		macros.addAll(tools);

		return macros;
	}

}
