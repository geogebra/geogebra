/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package org.geogebra.desktop.util;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.undo.AppState;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.InternalClipboard;
import org.geogebra.common.util.debug.Log;

/**
 * This class saves the given geos (which are usually the selected ones) into an
 * XML string, and makes it possible to insert a copy of them into the
 * construction As a nature of the clipboard, this class contains only static
 * data and methods
 *
 * @author Arpad Fekete
 */
public class CopyPasteD extends CopyPaste {

	protected HashSet<Macro> copiedMacros;
	protected StringBuilder copiedXML;
	protected ArrayList<String> copiedXMLlabels;

	protected StringBuilder copiedXMLforSameWindow;
	protected ArrayList<String> copiedXMLlabelsforSameWindow;
	protected EuclidianViewInterfaceCommon copySource;
	protected AppState copyObject;
	protected AppState copyObject2;

	private Set<String> duplicateLabels;

	/**
	 * Returns whether the clipboard is empty
	 *
	 * @return whether the clipboard is empty
	 */
	public boolean isEmpty() {
		return copiedXML == null || copiedXML.length() == 0;
	}

	/**
	 * copyToXML - Step 1 Remove fixed sliders
	 *
	 * @param geos
	 *            input and output
	 */
	protected void removeFixedSliders(ArrayList<ConstructionElement> geos) {
		GeoElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = (GeoElement) geos.get(i);
			if (geo.isGeoNumeric() && geo.isLockedPosition()) {
				geos.remove(geo);
			}
		}
	}

	protected void removeHavingMacroPredecessors(
			ArrayList<ConstructionElement> geos) {
		boolean found;
		for (int i = geos.size() - 1; i >= 0; i--) {
			if (geos.get(i).isGeoElement()) {
				GeoElement geo = (GeoElement) geos.get(i);
				found = checkMacros(geo);
				if (!found) {
					Iterator<GeoElement> it = geo.getAllPredecessors().iterator();
					while (it.hasNext() && !found) {
						GeoElement geo2 = it.next();
						found = checkMacros(geo2);
					}
				}
			}
		}
	}

	private boolean checkMacros(GeoElement geo) {
		AlgoElement parent = geo.getParentAlgorithm();
		if (parent instanceof AlgoMacro) {
			copiedMacros.add(((AlgoMacro) parent).getMacro());
			return true;
		}
		return false;
	}

	/**
	 * copyToXML - Step 4 Add the algos which belong to our selected geos Also
	 * add the geos which might be side-effects of these algos
	 * @param conels input and output
	 * @return the possible side-effect geos
	 */
	protected ArrayList<ConstructionElement> addAlgosDependentFromInside(
			ArrayList<ConstructionElement> conels) {
		return InternalClipboard.addAlgosDependentFromInside(conels, copiedMacros);
	}

	/**
	 * copyToXML - Step 4.5 If copied to the same window, don't copy free
	 * non-selected GeoNumerics
	 *
	 * @param conels
	 *            construction elements
	 * @param selected
	 *            selected elements
	 */
	protected ArrayList<ConstructionElement> removeFreeNonselectedGeoNumerics(
			List<ConstructionElement> conels,
			List<GeoElement> selected) {

		ArrayList<ConstructionElement> ret = new ArrayList<>(conels);
		GeoElement geo;
		for (int i = ret.size() - 1; i >= 0; i--) {
			if (ret.get(i).isGeoElement()) {
				geo = (GeoElement) ret.get(i);
				if (geo.isGeoNumeric() && geo.isIndependent()
						&& !selected.contains(geo)) {
					ret.remove(i);
				}
			}
		}
		return ret;
	}

	/**
	 * copyToXML - Step 5 Before saving the conels to xml, we have to rename its
	 * labels with labelPrefix and memorize those renamed labels and also hide
	 * the GeoElements in geostohide, and keep in geostohide only those which
	 * were actually hidden...
	 *
	 * @param conels
	 *            construction elements
	 * @param geostohide
	 *            geos to be hidden
	 */
	protected void beforeSavingToXML(ArrayList<ConstructionElement> conels,
			ArrayList<ConstructionElement> geostohide, boolean samewindow,
			boolean putdown) {

		if (samewindow) {
			copiedXMLlabelsforSameWindow = new ArrayList<>();
		} else {
			copiedXMLlabels = new ArrayList<>();
		}

		ConstructionElement geo;
		String label;
		for (int i = 0; i < conels.size(); i++) {
			geo = conels.get(i);
			if (geo.isGeoElement()) {
				label = ((GeoElement) geo).getLabelSimple();
				if (label != null) {
					((GeoElement) geo).addLabelPrefix(labelPrefix);

					if (samewindow) {
						copiedXMLlabelsforSameWindow
								.add(((GeoElement) geo).getLabelSimple());
					} else {
						copiedXMLlabels
								.add(((GeoElement) geo).getLabelSimple());
					}

					if (putdown) {
						geo.getKernel().renameLabelInScripts(label,
								labelPrefix + label);
					}

					// TODO: check possible realLabel issues
					// reallabel = ((GeoElement)geo).getRealLabel();
					// if (!reallabel.equals( ((GeoElement)geo).getLabelSimple()
					// )) {
					// ((GeoElement)geo).setRealLabel(labelPrefix + reallabel);
					// }
				}
			}
		}

		for (int j = geostohide.size() - 1; j >= 0; j--) {
			geo = geostohide.get(j);
			if (geo.isGeoElement() && ((GeoElement) geo).isEuclidianVisible()) {
				((GeoElement) geo).setEuclidianVisible(false);
			} else {
				geostohide.remove(geo);
			}
		}
	}

	/**
	 * copyToXML - Step 6 After saving the conels to xml, we have to rename its
	 * labels and also show the GeoElements in geostoshow
	 *
	 * @param conels
	 *            construction elements
	 * @param geostoshow
	 *            geos to be shown
	 */
	protected void afterSavingToXML(ArrayList<ConstructionElement> conels,
			ArrayList<ConstructionElement> geostoshow, boolean putdown) {

		ConstructionElement geo;
		String label;
		for (int i = 0; i < conels.size(); i++) {
			geo = conels.get(i);
			if (geo.isGeoElement()) {
				label = ((GeoElement) geo).getLabelSimple();
				if (label != null && label.startsWith(labelPrefix)) {
					try {
						((GeoElement) geo).setLabelSimple(
								label.substring(labelPrefix.length()));

						if (putdown) {
							geo.getKernel().renameLabelInScripts(label,
									label.substring(labelPrefix.length()));
						}
					} catch (Exception e) {
						Log.debug(e);
					}
				}
			}
		}

		for (int j = geostoshow.size() - 1; j >= 0; j--) {
			geo = geostoshow.get(j);
			if (geo.isGeoElement()) {
				((GeoElement) geo).setEuclidianVisible(true);
			}
		}
	}

	/**
	 * This method saves geos and all predecessors of them in XML
	 *
	 * @param app
	 *            the App object (the main application instance)
	 * @param geos
	 *            the set of GeoElement's that should be copied
	 * @param putdown
	 *            boolean which means the InsertFile case
	 */
	public void copyToXML(App app, List<GeoElement> geos,
			boolean putdown) {
		if (geos.isEmpty()) {
			return;
		}

		final boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		copiedXML = new StringBuilder();
		copiedXMLlabels = new ArrayList<>();
		copiedXMLforSameWindow = new StringBuilder();
		copiedXMLlabelsforSameWindow = new ArrayList<>();
		copySource = app.getActiveEuclidianView();
		copyObject = app.getUndoManager().getCurrentUndoInfo();
		copiedMacros = new HashSet<>();

		// create geoslocal and geostohide
		ArrayList<ConstructionElement> geoslocal = new ArrayList<>(geos);

		if (!putdown) {
			removeFixedSliders(geoslocal);
		}

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		if (!putdown) {
			removeHavingMacroPredecessors(geoslocal);

			if (geoslocal.isEmpty()) {
				app.setBlockUpdateScripts(scriptsBlocked);
				return;
			}
		}

		addSubGeos(geoslocal, new HashSet<>(app.getSelectionManager().getSelectedGroups()));

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		ArrayList<ConstructionElement> geostohide = addPredecessorGeos(
				geoslocal);

		// what about a GeoElement which is the result of an algo with no input?
		// this is especially important if the GeoElement cannot be shown,
		// e.g. in case the GeoElement is a textfield. In other cases, the bug
		// is
		// not visible, but it would still be nice to include the parent algo
		// too.
		// it is okay to handle it after this, as algos are resistant to hiding

		geostohide.addAll(addAlgosDependentFromInside(geoslocal
		));

		ArrayList<ConstructionElement> geoslocalsw = removeFreeNonselectedGeoNumerics(
				geoslocal, geos);
		ArrayList<ConstructionElement> geostohidesw = removeFreeNonselectedGeoNumerics(
				geostohide, geos);

		Kernel kernel = app.getKernel();

		// // FIRST XML SAVE
		beforeSavingToXML(geoslocal, geostohide, false, putdown);
		// change kernel settings temporarily
		// StringType oldPrintForm = kernel.getStringTemplate().getStringType();
		boolean saveScriptsToXML = kernel.getSaveScriptsToXML();
		if (!putdown) {
			kernel.setSaveScriptsToXML(false);
		}
		try {
			// step 5
			copiedXML = new StringBuilder();
			ConstructionElement ce;

			// loop through Construction to keep the good order of
			// ConstructionElements
			XMLStringBuilder xmlBuilder = new XMLStringBuilder(copiedXML);
			Construction cons = app.getKernel().getConstruction();
			for (int i = 0; i < cons.steps(); ++i) {
				ce = cons.getConstructionElement(i);
				if (geoslocal.contains(ce)) {
					ce.getXML(false, xmlBuilder);
				}
			}
		} catch (Exception e) {
			Log.debug(e);
			copiedXML = new StringBuilder();
		}
		// restore kernel settings
		// kernel.setCASPrintForm(oldPrintForm);
		if (!putdown) {
			kernel.setSaveScriptsToXML(saveScriptsToXML);
		}
		afterSavingToXML(geoslocal, geostohide, putdown);
		// FIRST XML SAVE END

		// SECOND XML SAVE
		if (!putdown) {
			beforeSavingToXML(geoslocalsw, geostohidesw, true, putdown);
			kernel.setSaveScriptsToXML(false);
			try {
				// step 5
				copiedXMLforSameWindow = new StringBuilder();
				ConstructionElement ce;

				// loop through Construction to keep the good order of
				// ConstructionElements
				Construction cons = app.getKernel().getConstruction();
				XMLStringBuilder xmlBuilder = new XMLStringBuilder(copiedXMLforSameWindow);
				for (int i = 0; i < cons.steps(); ++i) {
					ce = cons.getConstructionElement(i);
					if (geoslocalsw.contains(ce)) {
						ce.getXML(false, xmlBuilder);
					}
				}
			} catch (Exception e) {
				Log.debug(e);
				copiedXMLforSameWindow = new StringBuilder();
			}
			// restore kernel settings
			// kernel.setCASPrintForm(oldPrintForm);
			kernel.setSaveScriptsToXML(saveScriptsToXML);
			afterSavingToXML(geoslocalsw, geostohidesw, putdown);
		}
		// SECOND XML SAVE END

		app.setMode(EuclidianConstants.MODE_MOVE);
		app.getActiveEuclidianView().setSelectionRectangle(null);

		app.setBlockUpdateScripts(scriptsBlocked);
	}

	/**
	 * Checks whether the copyXMLforSameWindow may be used
	 *
	 * @param app
	 *            application
	 * @return boolean
	 */
	public boolean pasteFast(App app) {
		if (app.getActiveEuclidianView() != copySource) {
			return false;
		}
		return copyObject == copyObject2;
	}

	/**
	 * This method pastes the content of the clipboard from XML into the
	 * construction
	 *
	 * @param app
	 *            application
	 */
	public void pasteFromXML(App app, boolean putdown) {
		if (copiedXML == null || copiedXML.length() == 0) {
			return;
		}

		copyObject2 = app.getUndoManager().getCurrentUndoInfo();

		if (pasteFast(app) && !putdown) {
			if (copiedXMLforSameWindow == null
					|| copiedXMLforSameWindow.length() == 0) {
				return;
			}
		}

		if (pasteFast(app)) {
			app.getKernel().notifyPaste(copiedXMLforSameWindow.toString());
		} else {
			app.getKernel().notifyPaste(copiedXML.toString());
		}

		// it turned out to be necessary for e.g. handleLabels
		boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		// don't update selection
		app.getActiveEuclidianView().getEuclidianController()
				.clearSelections(true, false);
		// don't update properties view
		app.updateSelection(false);

		ArrayList<GeoElement> createdGeos;
		if (pasteFast(app) && !putdown) {
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			app.getGgbApi().evalXML(copiedXMLforSameWindow.toString());
			app.getKernel().getConstruction().updateConstruction(false);
			if (ev == app.getEuclidianView1()) {
				app.setActiveView(App.VIEW_EUCLIDIAN);
			} else if (app.isEuclidianView3D(ev)) {
				app.setActiveView(App.VIEW_EUCLIDIAN3D);
			} else {
				app.setActiveView(App.VIEW_EUCLIDIAN2);
			}
			createdGeos = handleLabels(app, copiedXMLlabelsforSameWindow,
					duplicateLabels, putdown);
		} else {
			// here the possible macros should be copied as well,
			// in case we should copy any macros
			if (!copiedMacros.isEmpty()) {
				// now we have to copy the macros from ad to app
				// in order to make some advanced constructions work
				// as it was hard to copy macro classes, let's use
				// strings, but how to load them into the application?
				try {
					// app.getXMLio().processXMLString(copySource.getApplication().getMacroXML(),
					// false, true);

					// alternative solution
					app.addMacroXML(copySource.getApplication().getXMLio()
							.getFullMacroXML(
									new ArrayList<>(copiedMacros)));
				} catch (Exception ex) {
					Log.debug(
							"Could not load any macros at \"Paste from XML\"");
					Log.debug(ex);
				}
			}

			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			app.getGgbApi().evalXML(copiedXML.toString());
			app.getKernel().getConstruction().updateConstruction(false);
			if (ev == app.getEuclidianView1()) {
				app.setActiveView(App.VIEW_EUCLIDIAN);
			} else if (app.isEuclidianView3D(ev)) {
				app.setActiveView(App.VIEW_EUCLIDIAN3D);
			} else {
				app.setActiveView(App.VIEW_EUCLIDIAN2);
			}
			createdGeos = handleLabels(app, copiedXMLlabels,
					duplicateLabels, putdown);
		}

		app.setBlockUpdateScripts(scriptsBlocked);

		app.setMode(EuclidianConstants.MODE_MOVE);

		app.getKernel().notifyPasteComplete(createdGeos);
	}

	@Override
	public void copyToXML(App app, List<GeoElement> selection) {
		copyToXML(app, selection, false);
	}

	@Override
	public void pasteFromXML(App app) {
		pasteFromXML(app, false);
	}

	@Override
	public void duplicate(App app, List<GeoElement> selection) {
		copyToXML(app, selection);
		pasteFromXML(app);
	}

	@Override
	public void clearClipboard() {
		if (copiedXML != null) {
			copiedXML.setLength(0);
		}
		if (copiedXMLforSameWindow != null) {
			copiedXMLforSameWindow.setLength(0);
		}
	}

	@Override
	public void copyTextToSystemClipboard(String text) {
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(text), null);
	}

	/**
	 * Copy and paste all geos from source app to target app.
	 * @param fromApp source app
	 * @param toApp target app
	 * @param duplicateLabels Set of duplicated labels that may need renaming
	 * @param overwrite Whether duplicated elements should be overwritten
	 */
	public void insertFrom(App fromApp, App toApp,
			@Nonnull Set<String> duplicateLabels, boolean overwrite) {
		Construction fromConstruction = fromApp.getKernel().getConstruction();
		fromConstruction.getGeoSetConstructionOrder().stream()
				.map(GeoElement::getLabelSimple)
				.forEach(toApp.getKernel().getConstruction()::addProtectedLabel);
		copyToXML(fromApp,
						new ArrayList<>(fromConstruction.getGeoSetWithCasCellsConstructionOrder()),
						true);

		this.duplicateLabels = duplicateLabels;
		if (overwrite) {
			for (String label: duplicateLabels) {
				GeoElement toRemove = toApp.getKernel().lookupLabel(label);
				toRemove.remove();
			}
		}
		pasteFromXML(toApp, true);
	}
}
