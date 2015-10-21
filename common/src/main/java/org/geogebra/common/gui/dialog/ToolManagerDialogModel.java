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
import org.geogebra.common.util.Assignment;
import org.geogebra.common.util.Exercise;

public class ToolManagerDialogModel {
	public interface ToolManagerDialogListener {

		void removeMacroFromToolbar(int i);

		void refreshCustomToolsInToolBar();

		void uploadWorksheet(ArrayList<Macro> macros);

	}

	private App app;
	final Localization loc;
	private List<Macro> deletedMacros;
	private ToolManagerDialogListener listener;

	public ToolManagerDialogModel(App app, ToolManagerDialogListener listener) {
		this.app = app;
		this.loc = app.getLocalization();
		this.listener = listener;
		deletedMacros = new ArrayList<Macro>();
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

		// since all Macros are removed and added back, the Assignments have
		// also be added again so:
		// keeping "pointers" on the Assignments in the Exercise to put them
		// in Place afterwards (the Exercise checks if they are still valid
		Exercise ex = app.getKernel().getExercise();
		ArrayList<Assignment> assignments = new ArrayList<Assignment>(
				ex.getParts());

		kernel.removeAllMacros();

		for (Object obj : macros) {
			kernel.addMacro((Macro) obj);
		}

		for (Assignment assignment : assignments) {
			ex.addAssignment(assignment);
		}
	}

	/**
	 * Deletes all selected tools that are not used in the construction.
	 */
	public boolean deleteTools(Object[] sel) {
		if (sel == null || sel.length == 0)
			return false;

		boolean didDeletion = false;
		boolean changeToolBar = false;
		boolean foundUsedMacro = false;
		String macroNames = "";
		Kernel kernel = app.getKernel();
		app.getSelectionManager().setSelectedGeos(new ArrayList<GeoElement>());
		deletedMacros.clear();
		for (int i = 0; i < sel.length; i++) {
			Macro macro = (Macro) sel[i];
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
				while (curr.hasNext())
					app.getSelectionManager().addSelectedGeo(curr.next());
				foundUsedMacro = true;
				macroNames += "\n" + macro.getToolOrCommandName() + ": "
						+ macro.getNeededTypesString();
			}
		}

		if (didDeletion) {
			// we reinit the undo info to make sure an undo does not use
			// any deleted tool
			kernel.initUndoInfo();
		}

		if (foundUsedMacro)
			app.showError(app.getLocalization().getError("Tool.DeleteUsed")
					+ " " + macroNames);

		return changeToolBar;
	}

	public List<Macro> getDeletedMacros() {
		return deletedMacros;
	}

	public void uploadToGeoGebraTube(final Object[] sel) {

		app.setWaitCursor();
		try {
			app.getSelectionManager().clearSelectedGeos(true, false);
			app.updateSelection(false);

			if (sel == null || sel.length == 0)
				return;

			// we need to save all selected tools and all tools
			// that are used by the selected tools
			LinkedHashSet<Macro> tools = new LinkedHashSet<Macro>();
			for (int i = 0; i < sel.length; i++) {
				Macro macro = (Macro) sel[i];
				ArrayList<Macro> macros = macro.getUsedMacros();
				if (macros != null)
					tools.addAll(macros);
				tools.add(macro);
			}

			// create Macro array list from tools set
			ArrayList<Macro> macros = new ArrayList<Macro>(tools.size());
			Iterator<Macro> it = tools.iterator();
			while (it.hasNext()) {
				macros.add(it.next());
			}

			listener.uploadWorksheet(macros);

		} catch (Exception e) {
			App.debug("Uploading failed");
			e.printStackTrace();
		}
		app.setDefaultCursor();
	};

	/**
	 * Saves all selected tools in a new file.
	 */
	public ArrayList<Macro> getAllTools(Object[] sel) {

		// we need to save all selected tools and all tools
		// that are used by the selected tools
		LinkedHashSet<Macro> tools = new LinkedHashSet<Macro>();
		for (int i = 0; i < sel.length; i++) {
			Macro macro = (Macro) sel[i];
			ArrayList<Macro> macros = macro.getUsedMacros();
			if (macros != null)
				tools.addAll(macros);
			tools.add(macro);
		}

		// create Macro array list from tools set
		ArrayList<Macro> macros = new ArrayList<Macro>(tools.size());
		Iterator<Macro> it = tools.iterator();
		while (it.hasNext()) {
			macros.add(it.next());
		}

		return macros;
	}

}
