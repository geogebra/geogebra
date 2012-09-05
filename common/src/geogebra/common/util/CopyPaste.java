/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.util;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.algos.GetPointsAlgo;
import geogebra.common.kernel.geos.GeoAxis;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class saves the given geos (which are usually the selected ones) into an
 * XML string, and makes it possible to insert a copy of them into the
 * construction As a nature of the clipboard, this class contains only static
 * data and methods
 * 
 * @author Arpad Fekete
 */
public class CopyPaste {

	// labelPrefix has to contain something else than big letters,
	// otherwise the parsed label could be regarded as a spreadsheet label
	// see GeoElement.isSpreadsheetLabel
	// check if name is valid for geo
	public static final String labelPrefix = "CLIPBOARDmagicSTRING";

	protected static StringBuilder copiedXML;
	protected static ArrayList<String> copiedXMLlabels;

	protected static StringBuilder copiedXMLforSameWindow;
	protected static ArrayList<String> copiedXMLlabelsforSameWindow;
	protected static EuclidianViewInterfaceCommon copySource;
	protected static Object copyObject, copyObject2;

	/**
	 * Returns whether the clipboard is empty
	 * 
	 * @return whether the clipboard is empty
	 */
	public static boolean isEmpty() {
		if (copiedXML == null)
			return true;

		return (copiedXML.length() == 0);
	}

	/**
	 * copyToXML - Step 1 Remove fixed sliders
	 * 
	 * @param geos
	 *            input and output
	 */
	protected static void removeFixedSliders(ArrayList<ConstructionElement> geos) {
		GeoElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = (GeoElement) geos.get(i);
			if (geo.isGeoNumeric())
				if (((GeoNumeric) geo).isSliderFixed()) {
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
	 */
	protected static void removeDependentFromAxes(
			ArrayList<ConstructionElement> geos, App app) {

		ConstructionElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = geos.get(i);
			if (geo.getAllIndependentPredecessors().contains(
					app.getKernel().getXAxis())) {
				geos.remove(i);
			} else if (geo.getAllIndependentPredecessors().contains(
					app.getKernel().getYAxis())) {
				geos.remove(i);
			}
		}
	}

	protected static void removeHavingMacroPredecessors(
			ArrayList<ConstructionElement> geos) {

		GeoElement geo, geo2;
		Iterator<GeoElement> it;
		boolean found = false;
		for (int i = geos.size() - 1; i >= 0; i--) {
			if (geos.get(i).isGeoElement()) {
				geo = (GeoElement) geos.get(i);
				found = false;
				if (geo.getParentAlgorithm() != null) {
					if (geo.getParentAlgorithm().getClassName()
							.equals(Algos.AlgoMacro)) {
						found = true;
					}
				}
				if (!found) {
					it = geo.getAllPredecessors().iterator();
					while (it.hasNext()) {
						geo2 = it.next();
						if (geo2.getParentAlgorithm() != null) {
							if (geo2.getParentAlgorithm().getClassName()
									.equals(Algos.AlgoMacro)) {
								found = true;
								break;
							}
						}
					}
				}
				if (found) {
					geos.remove(i);
				}
			}
		}
	}

	/**
	 * copyToXML - Step 2 Add subgeos of geos like points of a segment or line
	 * or polygon These are copied anyway but this way they won't be hidden
	 * 
	 * @param geos
	 *            input and output
	 */
	protected static void addSubGeos(ArrayList<ConstructionElement> geos) {
		GeoElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = (GeoElement) geos.get(i);
			if(geo.getParentAlgorithm()==null)
				continue;
			if ((geo.isGeoLine() && geo.getParentAlgorithm().getClassName()
					.equals(Algos.AlgoJoinPoints))
					|| (geo.isGeoSegment() && geo.getParentAlgorithm()
							.getClassName().equals(Algos.AlgoJoinPointsSegment))
					|| (geo.isGeoRay() && geo.getParentAlgorithm()
							.getClassName().equals(Algos.AlgoJoinPointsRay))
					|| (geo.isGeoVector() && geo.getParentAlgorithm()
							.getClassName().equals(Algos.AlgoVector))) {

				if (!geos.contains(geo.getParentAlgorithm().getInput()[0])) {
					geos.add(geo.getParentAlgorithm().getInput()[0]);
				}
				if (!geos.contains(geo.getParentAlgorithm().getInput()[1])) {
					geos.add(geo.getParentAlgorithm().getInput()[1]);
				}
			} else if (geo.isGeoPolygon()) {
				if (geo.getParentAlgorithm().getClassName()
						.equals(Algos.AlgoPolygon)) {
					GeoElement[] points = ((AlgoPolygon) (geo
							.getParentAlgorithm())).getPoints();
					for (int j = 0; j < points.length; j++) {
						if (!geos.contains(points[j])) {
							geos.add(points[j]);
						}
					}
					GeoElement[] ogeos = ((AlgoPolygon) (geo
							.getParentAlgorithm())).getOutput();
					for (int j = 0; j < ogeos.length; j++) {
						if (!geos.contains(ogeos[j]) && ogeos[j].isGeoSegment()) {
							geos.add(ogeos[j]);
						}
					}
				} else if (geo.getParentAlgorithm().getClassName()
						.equals(Algos.AlgoPolygonRegular)) {
					GeoElement[] pgeos = ((geo
							.getParentAlgorithm())).getInput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j]) && pgeos[j].isGeoPoint()) {
							geos.add(pgeos[j]);
						}
					}
					GeoElement[] ogeos = ((geo
							.getParentAlgorithm())).getOutput();
					for (int j = 0; j < ogeos.length; j++) {
						if (!geos.contains(ogeos[j])
								&& (ogeos[j].isGeoSegment() || ogeos[j]
										.isGeoPoint())) {
							geos.add(ogeos[j]);
						}
					}
				}
			} else if (geo instanceof GeoPolyLine) {
				if (geo.getParentAlgorithm().getClassName()
						.equals(Algos.AlgoPolyLine)) {
					GeoElement[] pgeos = ((GetPointsAlgo) (geo
							.getParentAlgorithm())).getPoints();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j])) {
							geos.add(pgeos[j]);
						}
					}
				}
			} else if (geo.isGeoConic()) {
				if (geo.getParentAlgorithm().getClassName()
						.equals(Algos.AlgoCircleTwoPoints)) {
					GeoElement[] pgeos = geo.getParentAlgorithm().getInput();
					if (!geos.contains(pgeos[0]))
						geos.add(pgeos[0]);
					if (!geos.contains(pgeos[1]))
						geos.add(pgeos[1]);
				} else if (geo.getParentAlgorithm().getClassName()
						.equals(Algos.AlgoCircleThreePoints)
						|| geo.getParentAlgorithm().getClassName()
								.equals(Algos.AlgoEllipseFociPoint)
						|| geo.getParentAlgorithm().getClassName()
								.equals(Algos.AlgoHyperbolaFociPoint)) {
					GeoElement[] pgeos = geo.getParentAlgorithm().getInput();
					if (!geos.contains(pgeos[0]))
						geos.add(pgeos[0]);
					if (!geos.contains(pgeos[1]))
						geos.add(pgeos[1]);
					if (!geos.contains(pgeos[2]))
						geos.add(pgeos[2]);
				} else if (geo.getParentAlgorithm().getClassName()
						.equals(Algos.AlgoConicFivePoints)) {
					GeoElement[] pgeos = geo.getParentAlgorithm().getInput();
					for (int j = 0; j < pgeos.length; j++) {
						if (!geos.contains(pgeos[j]))
							geos.add(pgeos[j]);
					}
				} else if (geo.getParentAlgorithm().getClassName()
						.equals(Algos.AlgoCirclePointRadius)) {
					GeoElement[] pgeos = geo.getParentAlgorithm().getInput();
					if (!geos.contains(pgeos[0]))
						geos.add(pgeos[0]);
				}
			} else if (geo.isGeoList()) {
				if (geo.getParentAlgorithm().getClassName()
						.equals(Algos.AlgoSequence)) {
					GeoElement[] pgeos = geo.getParentAlgorithm().getInput();
					if (pgeos.length > 1) {
						if (!geos.contains(pgeos[0]))
							geos.add(pgeos[0]);
					}
				}
			}
		}
	}

	/**
	 * copyToXML - Step 3 Add geos which might be intermediates between our
	 * existent geos And also add all predecessors of our geos except GeoAxis
	 * objects (GeoAxis objects should be dealt with later - we suppose they are
	 * always on)
	 * 
	 * @param geos
	 *            input and output
	 * @return just the predecessor and intermediate geos for future handling
	 */
	protected static ArrayList<ConstructionElement> addPredecessorGeos(
			ArrayList<ConstructionElement> geos) {

		ArrayList<ConstructionElement> ret = new ArrayList<ConstructionElement>();

		GeoElement geo, geo2;
		TreeSet<GeoElement> ts;
		Iterator<GeoElement> it;
		for (int i = 0; i < geos.size(); i++) {
			geo = (GeoElement) geos.get(i);
			ts = geo.getAllPredecessors();
			it = ts.iterator();
			while (it.hasNext()) {
				geo2 = it.next();
				if (!ret.contains(geo2) && !geos.contains(geo2)
						&& !(geo2 instanceof GeoAxis)) {
					ret.add(geo2);
				}
			}
		}
		geos.addAll(ret);
		return ret;
	}

	/**
	 * copyToXML - Step 4 Add the algos which belong to our selected geos Also
	 * add the geos which might be side-effects of these algos
	 * 
	 * @param conels
	 *            input and output
	 * @return the possible side-effect geos
	 */
	protected static ArrayList<ConstructionElement> addAlgosDependentFromInside(
			ArrayList<ConstructionElement> conels) {

		ArrayList<ConstructionElement> ret = new ArrayList<ConstructionElement>();

		GeoElement geo;
		ArrayList<AlgoElement> geoal;
		AlgoElement ale;
		ArrayList<ConstructionElement> ac;
		GeoElement[] geos;
		for (int i = conels.size() - 1; i >= 0; i--) {
			geo = (GeoElement) conels.get(i);
			geoal = geo.getAlgorithmList();

			for (int j = 0; j < geoal.size(); j++) {
				ale = geoal.get(j);

				if (!ale.getClassName().equals(Algos.AlgoMacro)) {

					ac = new ArrayList<ConstructionElement>();
					ac.addAll(Arrays.asList(ale.getInput()));
					if (conels.containsAll(ac)
							&& !conels.contains(ale)) {
						conels.add(ale);
						geos = ale.getOutput();
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
		conels.addAll(ret);
		return ret;
	}

	/**
	 * copyToXML - Step 4.5 If copied to the same window, don't copy free
	 * non-selected GeoNumerics
	 * 
	 * @param conels
	 * @param selected
	 */
	protected static ArrayList<ConstructionElement> removeFreeNonselectedGeoNumerics(
			ArrayList<ConstructionElement> conels,
			ArrayList<GeoElement> selected) {

		ArrayList<ConstructionElement> ret = new ArrayList<ConstructionElement>();
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
	 * @param geostohide
	 */
	protected static void beforeSavingToXML(
			ArrayList<ConstructionElement> conels,
			ArrayList<ConstructionElement> geostohide, boolean samewindow) {

		if (samewindow)
			copiedXMLlabelsforSameWindow = new ArrayList<String>();
		else
			copiedXMLlabels = new ArrayList<String>();

		ConstructionElement geo;
		String label;
		for (int i = 0; i < conels.size(); i++) {
			geo = conels.get(i);
			if (geo.isGeoElement()) {
				label = ((GeoElement) geo).getLabelSimple();
				if (label != null) {
					((GeoElement) geo).setLabelSimple(labelPrefix + label);

					if (samewindow)
						copiedXMLlabelsforSameWindow.add(((GeoElement) geo)
								.getLabelSimple());
					else
						copiedXMLlabels
								.add(((GeoElement) geo).getLabelSimple());

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
	 * @param geostoshow
	 */
	protected static void afterSavingToXML(
			ArrayList<ConstructionElement> conels,
			ArrayList<ConstructionElement> geostoshow) {

		ConstructionElement geo;
		String label;
		for (int i = 0; i < conels.size(); i++) {
			geo = conels.get(i);
			if (geo.isGeoElement()) {
				label = ((GeoElement) geo).getLabelSimple();
				if (label != null && label.length() >= labelPrefix.length()) {
					if (label.substring(0, labelPrefix.length()).equals(
							labelPrefix)) {
						try {
							((GeoElement) geo).setLabelSimple(label
									.substring(labelPrefix.length()));
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
	 * @param geos
	 */
	public static void copyToXML(App app,
			ArrayList<GeoElement> geos) {

		if (geos.isEmpty())
			return;

		boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		copiedXML = new StringBuilder();
		copiedXMLlabels = new ArrayList<String>();
		copiedXMLforSameWindow = new StringBuilder();
		copiedXMLlabelsforSameWindow = new ArrayList<String>();
		copySource = app.getActiveEuclidianView();
		copyObject = app.getKernel().getConstruction().getUndoManager()
				.getCurrentUndoInfo();

		// create geoslocal and geostohide
		ArrayList<ConstructionElement> geoslocal = new ArrayList<ConstructionElement>();
		geoslocal.addAll(geos);
		removeFixedSliders(geoslocal);

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		removeDependentFromAxes(geoslocal, app);

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		removeHavingMacroPredecessors(geoslocal);

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		addSubGeos(geoslocal);

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		ArrayList<ConstructionElement> geostohide = addPredecessorGeos(geoslocal);
		geostohide.addAll(addAlgosDependentFromInside(geoslocal));

		ArrayList<ConstructionElement> geoslocalsw = removeFreeNonselectedGeoNumerics(
				geoslocal, geos);
		ArrayList<ConstructionElement> geostohidesw = removeFreeNonselectedGeoNumerics(
				geostohide, geos);

		Kernel kernel = app.getKernel();

		// // FIRST XML SAVE
		beforeSavingToXML(geoslocal, geostohide, false);
		// change kernel settings temporarily
		//StringType oldPrintForm = kernel.getStringTemplate().getStringType();
		boolean saveScriptsToXML = kernel.getSaveScriptsToXML();
		kernel.setSaveScriptsToXML(false);
		try {
			// step 5
			copiedXML = new StringBuilder();
			ConstructionElement ce;

			// loop through Construction to keep the good order of
			// ConstructionElements
			Construction cons = app.getKernel().getConstruction();
			for (int i = 0; i < cons.steps(); ++i) {
				ce = cons.getConstructionElement(i);
				if (geoslocal.contains(ce))
					ce.getXML(copiedXML);
			}
		} catch (Exception e) {
			e.printStackTrace();
			copiedXML = new StringBuilder();
		}
		// restore kernel settings
		//kernel.setCASPrintForm(oldPrintForm);
		kernel.setSaveScriptsToXML(saveScriptsToXML);
		afterSavingToXML(geoslocal, geostohide);
		// FIRST XML SAVE END

		// SECOND XML SAVE
		beforeSavingToXML(geoslocalsw, geostohidesw, true);
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
				if (geoslocalsw.contains(ce))
					ce.getXML(copiedXMLforSameWindow);
			}
		} catch (Exception e) {
			e.printStackTrace();
			copiedXMLforSameWindow = new StringBuilder();
		}
		// restore kernel settings
		//kernel.setCASPrintForm(oldPrintForm);
		kernel.setSaveScriptsToXML(saveScriptsToXML);
		afterSavingToXML(geoslocalsw, geostohidesw);
		// SECOND XML SAVE END

		app.setMode(EuclidianConstants.MODE_MOVE);
		app.getActiveEuclidianView().setSelectionRectangle(null);

		app.setBlockUpdateScripts(scriptsBlocked);
	}

	/**
	 * In some situations, we may need to clear the clipboard
	 */
	public static void clearClipboard() {
		copiedXML = null;
		copiedXMLlabels = new ArrayList<String>();
		copiedXMLforSameWindow = null;
		copiedXMLlabelsforSameWindow = new ArrayList<String>();
		copySource = null;
		copyObject = null;
		copyObject2 = null;
	}

	/**
	 * Convenience method to set new labels instead of labels
	 * 
	 * @param app
	 * @param labels
	 */
	protected static void handleLabels(App app,
			ArrayList<String> labels) {

		Kernel kernel = app.getKernel();
		GeoElement geo;
		for (int i = 0; i < labels.size(); i++) {
			String ll = labels.get(i);
			geo = kernel.lookupLabel(ll);
			if (geo != null) {
				if (app.getActiveEuclidianView() == app.getEuclidianView1()) {
					app.addToEuclidianView(geo);
					if (app.hasEuclidianView2()) {
						geo.removeView(App.VIEW_EUCLIDIAN2);
						app.getEuclidianView2().remove(geo);
					}
				} else {
					app.removeFromEuclidianView(geo);
					geo.addView(App.VIEW_EUCLIDIAN2);
					app.getEuclidianView2().add(geo);
				}

				geo.setLabel(geo.getIndexLabel(geo.getLabelSimple().substring(
						labelPrefix.length())));
				// geo.setLabel(geo.getDefaultLabel(false));
				app.addSelectedGeo(geo);

				if (geo.getParentAlgorithm() != null) {
					if (geo.getParentAlgorithm().getClassName()
							.equals(Algos.AlgoSequence)) {
						// variable of AlgoSequence is not returned in
						// lookupLabel!
						// the old name of the variable may remain, as it is not
						// part of the construction anyway
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						if (pgeos.length > 1
								&& pgeos[1].getLabelSimple().length() > labelPrefix
										.length())
							if (pgeos[1].getLabelSimple()
									.substring(0, labelPrefix.length())
									.equals(labelPrefix))
								pgeos[1].setLabelSimple(pgeos[1]
										.getLabelSimple().substring(
												labelPrefix.length()));
					}
				}
			}
		}
	}

	/**
	 * Checks whether the copyXMLforSameWindow may be used
	 * 
	 * @param app
	 * @return boolean
	 */
	public static boolean pasteFast(App app) {
		if (app.getActiveEuclidianView() != copySource)
			return false;
		if (copyObject != copyObject2)
			return false;
		return true;
	}

	/**
	 * This method pastes the content of the clipboard from XML into the
	 * construction
	 * 
	 * @param app
	 */
	public static void pasteFromXML(App app) {

		if (copiedXML == null)
			return;

		if (copiedXML.length() == 0)
			return;

		if (!app.getActiveEuclidianView().getEuclidianController().mayPaste())
			return;

		copyObject2 = app.getKernel().getConstruction().getUndoManager()
				.getCurrentUndoInfo();

		if (pasteFast(app)) {
			if (copiedXMLforSameWindow == null)
				return;

			if (copiedXMLforSameWindow.length() == 0)
				return;
		}

		//don't update selection
		app.getActiveEuclidianView().getEuclidianController().clearSelections(true,false);
		//don't update properties view
		app.updateSelection(false);
		app.getActiveEuclidianView().getEuclidianController()
				.setPastePreviewSelected();

		if (pasteFast(app)) {
			EuclidianViewInterfaceCommon ev = app
					.getActiveEuclidianView();
			if (ev == app.getEuclidianView1()) {
				app.getGgbApi().evalXML(copiedXMLforSameWindow.toString());
				app.getKernel().getConstruction().updateConstruction();
				app.setActiveView(App.VIEW_EUCLIDIAN);
			} else {
				app.getGgbApi().evalXML(copiedXMLforSameWindow.toString());
				app.getKernel().getConstruction().updateConstruction();
				app.setActiveView(App.VIEW_EUCLIDIAN2);
			}
			handleLabels(app, copiedXMLlabelsforSameWindow);
		} else {
			EuclidianViewInterfaceCommon ev = app
					.getActiveEuclidianView();
			if (ev == app.getEuclidianView1()) {
				app.getGgbApi().evalXML(copiedXML.toString());
				app.getKernel().getConstruction().updateConstruction();
				app.setActiveView(App.VIEW_EUCLIDIAN);
			} else {
				app.getGgbApi().evalXML(copiedXML.toString());
				app.getKernel().getConstruction().updateConstruction();
				app.setActiveView(App.VIEW_EUCLIDIAN2);
			}
			handleLabels(app, copiedXMLlabels);
		}

		app.getActiveEuclidianView().getEuclidianController()
				.setPastePreviewSelected();
		app.setMode(EuclidianConstants.MODE_MOVE);
	}

	/**
	 * Currently, we call this only if the pasted object is put down, but it
	 * would be better if this were called every time when kernel.storeUndoInfo
	 * called and there wasn't anything deleted
	 */
	public static void pastePutDownCallback(App app) {
		if (pasteFast(app)) {
			copyObject = app.getKernel().getConstruction().getUndoManager()
					.getCurrentUndoInfo();
			copyObject2 = null;
		}
	}
}
