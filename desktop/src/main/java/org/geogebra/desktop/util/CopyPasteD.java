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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoInputBox;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.CopyPaste;
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
	protected Object copyObject;
	protected Object copyObject2;

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

	/**
	 * copyToXML - Step 1.5 (temporary) currently we remove all geos which
	 * depend on the axes TODO: make geos dependent on GeoAxis objects copiable
	 * again (this is not easy as there is a bug when copy & paste something
	 * which depends on xAxis or yAxis, then copy & paste something else, points
	 * may be repositioned)
	 *
	 * @param geos
	 *            elements
	 * @param app
	 *            application
	 */
	protected void removeDependentFromAxes(ArrayList<ConstructionElement> geos,
			App app) {
		TreeSet<GeoElement> ancestors = new TreeSet<>();
		ConstructionElement geo;
		Construction cons = app.getKernel().getConstruction();
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = geos.get(i);
			ancestors.clear();
			geo.addPredecessorsToSet(ancestors, true);
			if (contained(ancestors, app.getKernel().getXAxis())
					|| contained(ancestors, app.getKernel().getYAxis())
					|| contained(ancestors, app.getKernel().getZAxis3D())
					|| contained(ancestors, cons.getXOYPlane())
					|| contained(ancestors, cons.getClippingCube())
					|| contained(ancestors, cons.getSpace())) {
				geos.remove(i);
			}
		}
	}

	private static boolean contained(TreeSet<GeoElement> ancestors,
			GeoElementND el) {
		return el != null && ancestors.contains(el);
	}

	protected void removeHavingMacroPredecessors(
			ArrayList<ConstructionElement> geos, boolean copymacro) {

		GeoElement geo, geo2;
		Iterator<GeoElement> it;
		boolean found = false;
		for (int i = geos.size() - 1; i >= 0; i--) {
			if (geos.get(i).isGeoElement()) {
				geo = (GeoElement) geos.get(i);
				found = checkMacros(geo, copymacro);
				if (!found) {
					it = geo.getAllPredecessors().iterator();
					while (it.hasNext() && !found) {
						geo2 = it.next();
						found = checkMacros(geo2, copymacro);
					}
				}
				if (found && !copymacro) {
					geos.remove(i);
				}
			}
		}
	}

	private boolean checkMacros(GeoElement geo, boolean copymacro) {
		if (Algos.isUsedFor(Algos.AlgoMacro, geo)) {
			if (copymacro) {
				copiedMacros
						.add(((AlgoMacro) geo.getParentAlgorithm()).getMacro());
			}
			return true;
		}
		return false;
	}

	/**
	 * copyToXML - Step 4 Add the algos which belong to our selected geos Also
	 * add the geos which might be side-effects of these algos
	 *
	 * @param conels
	 *            input and output
	 * @return the possible side-effect geos
	 */
	protected ArrayList<ConstructionElement> addAlgosDependentFromInside(
			ArrayList<ConstructionElement> conels, boolean putdown,
			boolean copymacro) {

		ArrayList<ConstructionElement> ret = new ArrayList<>();

		GeoElement geo;
		ArrayList<AlgoElement> geoal;
		AlgoElement ale;
		ArrayList<ConstructionElement> ac;
		GeoElement[] geos;
		for (int i = conels.size() - 1; i >= 0; i--) {
			geo = (GeoElement) conels.get(i);

			// also doing this here, which is not about the name of the method,
			// but making sure textfields (which require algos) are shown
			if ((geo.getParentAlgorithm() instanceof AlgoInputBox)
					&& (!ret.contains(geo.getParentAlgorithm()))
					&& (!conels.contains(geo.getParentAlgorithm()))) {
				// other algos will be added to this anyway,
				// so we can handle this issue in this method
				ret.add(geo.getParentAlgorithm());
			}
			// probably not needed? although corner number is NumberValue,
			// it is converted to a GeoElement, so this might be Okay
			/*
			 * if ((geo.getParentAlgorithm() instanceof AlgoDrawingPadCorner) &&
			 * (!ret.contains(geo.getParentAlgorithm())) &&
			 * (!geos.contains(geo.getParentAlgorithm()))) { // other algos will
			 * be added to this anyway, // so we can handle this issue in this
			 * method ret.add(geo.getParentAlgorithm()); }
			 */

			geoal = geo.getAlgorithmList();

			for (int j = 0; j < geoal.size(); j++) {
				ale = geoal.get(j);

				if (!(ale instanceof AlgoMacro) || putdown || copymacro) {

					ac = new ArrayList<>();
					ac.addAll(Arrays.asList(ale.getInput()));
					if (conels.containsAll(ac) && !conels.contains(ale)) {

						if ((ale instanceof AlgoMacro) && copymacro) {
							copiedMacros.add(((AlgoMacro) ale).getMacro());
						}

						conels.add(ale);
						geos = ale.getOutput();
						if (geos != null) {
							for (int k = 0; k < geos.length; k++) {
								if (!ret.contains(geos[k])
										&& !conels.contains(geos[k])) {
									ret.add(geos[k]);
								}
							}
						}
					}
				}
			}
		}
		conels.addAll(ret);
		return ret;
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

		ArrayList<ConstructionElement> ret = new ArrayList<>();
		ret.addAll(conels);
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
					((GeoElement) geo).setLabelSimple(labelPrefix + label);

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
				if (label != null && label.length() >= labelPrefix.length()) {
					if (label.substring(0, labelPrefix.length())
							.equals(labelPrefix)) {
						try {
							((GeoElement) geo).setLabelSimple(
									label.substring(labelPrefix.length()));

							if (putdown) {
								geo.getKernel().renameLabelInScripts(label,
										label.substring(labelPrefix.length()));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
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

		boolean copyMacrosPresume = true;

		if (geos.isEmpty()) {
			return;
		}

		boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		copiedXML = new StringBuilder();
		copiedXMLlabels = new ArrayList<>();
		copiedXMLforSameWindow = new StringBuilder();
		copiedXMLlabelsforSameWindow = new ArrayList<>();
		copySource = app.getActiveEuclidianView();
		copyObject = app.getUndoManager().getCurrentUndoInfo();
		copiedMacros = new HashSet<>();

		// create geoslocal and geostohide
		ArrayList<ConstructionElement> geoslocal = new ArrayList<>();
		geoslocal.addAll(geos);

		if (!putdown) {
			removeFixedSliders(geoslocal);
		}

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		removeDependentFromAxes(geoslocal, app);

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		if (!putdown) {
			removeHavingMacroPredecessors(geoslocal, copyMacrosPresume);

			if (geoslocal.isEmpty()) {
				app.setBlockUpdateScripts(scriptsBlocked);
				return;
			}
		}

		addSubGeos(geoslocal);

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

		geostohide.addAll(addAlgosDependentFromInside(geoslocal, putdown,
				copyMacrosPresume));

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
			Construction cons = app.getKernel().getConstruction();
			for (int i = 0; i < cons.steps(); ++i) {
				ce = cons.getConstructionElement(i);
				if (geoslocal.contains(ce)) {
					ce.getXML(false, copiedXML);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				for (int i = 0; i < cons.steps(); ++i) {
					ce = cons.getConstructionElement(i);
					if (geoslocalsw.contains(ce)) {
						ce.getXML(false, copiedXMLforSameWindow);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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
		if (copyObject != copyObject2) {
			return false;
		}
		return true;
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
			createdGeos = handleLabels(app, copiedXMLlabelsforSameWindow, putdown);
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
					ex.printStackTrace();
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
			createdGeos = handleLabels(app, copiedXMLlabels, putdown);
		}

		app.setBlockUpdateScripts(scriptsBlocked);

		app.setMode(EuclidianConstants.MODE_MOVE);

		app.getKernel().notifyPasteComplete(createdGeos);
	}

	/**
	 * Currently, we call this only if the pasted object is put down, but it
	 * would be better if this were called every time when kernel.storeUndoInfo
	 * called and there wasn't anything deleted
	 *
	 * @param app
	 *            application
	 */
	public void pastePutDownCallback(App app) {
		if (pasteFast(app)) {
			copyObject = app.getUndoManager().getCurrentUndoInfo();
			copyObject2 = null;
		}
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
}
