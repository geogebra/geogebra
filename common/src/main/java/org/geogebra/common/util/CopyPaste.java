/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoCircleTwoPoints;
import org.geogebra.common.kernel.algos.AlgoConicFivePoints;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoEllipseHyperbolaFociPoint;
import org.geogebra.common.kernel.algos.AlgoJoinPoints;
import org.geogebra.common.kernel.algos.AlgoJoinPointsRay;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.algos.AlgoPolygonRegularND;
import org.geogebra.common.kernel.algos.AlgoTextfield;
import org.geogebra.common.kernel.algos.AlgoVector;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.algos.GetPointsAlgo;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;

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

	// this CopyPaste.INSTANCE shall either be CopyPaste or CopyPaste3D
	// determined by App.initFactories, AppD, App3D, AppW, AppWapplet3D,
	// AppWapplication3D
	public static CopyPaste INSTANCE = null;

	public CopyPaste() {
		// dummy, for now
	}

	protected HashSet<Macro> copiedMacros;
	protected StringBuilder copiedXML;
	protected ArrayList<String> copiedXMLlabels;

	protected StringBuilder copiedXMLforSameWindow;
	protected ArrayList<String> copiedXMLlabelsforSameWindow;
	protected EuclidianViewInterfaceCommon copySource;
	protected Object copyObject, copyObject2;

	/**
	 * Returns whether the clipboard is empty
	 * 
	 * @return whether the clipboard is empty
	 */
	public boolean isEmpty() {
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
	protected void removeFixedSliders(ArrayList<ConstructionElement> geos) {
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
	protected void removeDependentFromAxes(ArrayList<ConstructionElement> geos,
			App app) {

		ConstructionElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = geos.get(i);
			if (geo.getAllIndependentPredecessors().contains(
					app.getKernel().getXAxis())) {
				geos.remove(i);
			} else if (geo.getAllIndependentPredecessors().contains(
					app.getKernel().getYAxis())) {
				geos.remove(i);
			} else if (app.is3D()) {
				if (geo.getAllIndependentPredecessors().contains(
						app.getKernel().getXAxis3D())) {
					geos.remove(i);
				} else if (geo.getAllIndependentPredecessors().contains(
						app.getKernel().getYAxis3D())) {
					geos.remove(i);
				} else if (geo.getAllIndependentPredecessors().contains(
						app.getKernel().getZAxis3D())) {
					geos.remove(i);
				} else if (geo.getAllIndependentPredecessors().contains(
						app.getKernel().getXOYPlane())) {
					geos.remove(i);
				} else if (geo.getAllIndependentPredecessors().contains(
						app.getKernel().getClippingCube())) {
					geos.remove(i);
				} else if (geo.getAllIndependentPredecessors().contains(
						app.getKernel().getSpace())) {
					geos.remove(i);
				}
			}
		}
	}

	protected void removeHavingMacroPredecessors(
			ArrayList<ConstructionElement> geos, boolean copymacro) {

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
						if (copymacro) {
							copiedMacros.add(((AlgoMacro) geo
									.getParentAlgorithm()).getMacro());
						}
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
								if (copymacro) {
									copiedMacros.add(((AlgoMacro) geo2
											.getParentAlgorithm()).getMacro());
								}
								break;
							}
						}
					}
				}
				if (found && !copymacro) {
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
	protected void addSubGeos(ArrayList<ConstructionElement> geos) {
		GeoElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = (GeoElement) geos.get(i);
			if (geo.getParentAlgorithm() == null)
				continue;

			if (!geo.isGeoElement3D()) {
				if ((geo.isGeoLine() && geo.getParentAlgorithm() instanceof AlgoJoinPoints)
						|| (geo.isGeoSegment() && geo.getParentAlgorithm() instanceof AlgoJoinPointsSegment)
						|| (geo.isGeoRay() && geo.getParentAlgorithm() instanceof AlgoJoinPointsRay)
						|| (geo.isGeoVector() && geo.getParentAlgorithm() instanceof AlgoVector)) {

					if (!geos.contains(geo.getParentAlgorithm().getInput()[0])) {
						geos.add(geo.getParentAlgorithm().getInput()[0]);
					}
					if (!geos.contains(geo.getParentAlgorithm().getInput()[1])) {
						geos.add(geo.getParentAlgorithm().getInput()[1]);
					}
				} else if (geo.isGeoPolygon()) {
					if (geo.getParentAlgorithm() instanceof AlgoPolygon) {
						GeoPointND[] points = ((AlgoPolygon) (geo
								.getParentAlgorithm())).getPoints();
						for (int j = 0; j < points.length; j++) {
							if (!geos.contains(points[j])) {
								geos.add((GeoElement) points[j]);
							}
						}
						GeoElement[] ogeos = ((AlgoPolygon) (geo
								.getParentAlgorithm())).getOutput();
						for (int j = 0; j < ogeos.length; j++) {
							if (!geos.contains(ogeos[j])
									&& ogeos[j].isGeoSegment()) {
								geos.add(ogeos[j]);
							}
						}
					} else if (geo.getParentAlgorithm() instanceof AlgoPolygonRegularND) {
						GeoElement[] pgeos = ((geo.getParentAlgorithm()))
								.getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j])
									&& pgeos[j].isGeoPoint()) {
								geos.add(pgeos[j]);
							}
						}
						GeoElement[] ogeos = ((geo.getParentAlgorithm()))
								.getOutput();
						for (int j = 0; j < ogeos.length; j++) {
							if (!geos.contains(ogeos[j])
									&& (ogeos[j].isGeoSegment() || ogeos[j]
											.isGeoPoint())) {
								geos.add(ogeos[j]);
							}
						}
					}
				} else if (geo instanceof GeoPolyLine) {
					if (geo.getParentAlgorithm() instanceof AlgoPolyLine) {
						GeoElement[] pgeos = ((GetPointsAlgo) (geo
								.getParentAlgorithm())).getPoints();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j])) {
								geos.add(pgeos[j]);
							}
						}
					}
				} else if (geo.isGeoConic()) {
					if (geo.getParentAlgorithm() instanceof AlgoCircleTwoPoints) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						if (!geos.contains(pgeos[0]))
							geos.add(pgeos[0]);
						if (!geos.contains(pgeos[1]))
							geos.add(pgeos[1]);
					} else if (geo.getParentAlgorithm() instanceof AlgoCircleThreePoints
							|| geo.getParentAlgorithm() instanceof AlgoEllipseHyperbolaFociPoint) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						if (!geos.contains(pgeos[0]))
							geos.add(pgeos[0]);
						if (!geos.contains(pgeos[1]))
							geos.add(pgeos[1]);
						if (!geos.contains(pgeos[2]))
							geos.add(pgeos[2]);
					} else if (geo.getParentAlgorithm() instanceof AlgoConicFivePoints) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j]))
								geos.add(pgeos[j]);
						}
					} else if (geo.getParentAlgorithm() instanceof AlgoCirclePointRadius) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						if (!geos.contains(pgeos[0]))
							geos.add(pgeos[0]);
					}
				} else if (geo.isGeoList()) {
					// TODO: note that there are a whole lot of other list algos
					// that might need to be supported! 3D cases come here too,
					// because GeoList is 2D object! Or we should make a
					// separate
					// method just for GeoList! It would also be good, for
					// nested
					// lists in lists and GeoElements with subGeos in lists!
					// (new ticket)
					if (geo.getParentAlgorithm().getClassName()
							.equals(Commands.Sequence)) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						if (pgeos.length > 1) {
							if (!geos.contains(pgeos[0]))
								geos.add(pgeos[0]);
						}
					} else if (geo.getParentAlgorithm() instanceof AlgoDependentList) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j]))
								geos.add(pgeos[j]);
						}
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
	protected ArrayList<ConstructionElement> addPredecessorGeos(
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
						&& !(geo2 instanceof GeoAxisND)
						&& (geo2 != geo2.getKernel().getXOYPlane())
						&& (geo2 != geo2.getKernel().getClippingCube())
						&& (geo2 != geo2.getKernel().getSpace())) {
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
	protected ArrayList<ConstructionElement> addAlgosDependentFromInside(
			ArrayList<ConstructionElement> conels, boolean putdown,
			boolean copymacro) {

		ArrayList<ConstructionElement> ret = new ArrayList<ConstructionElement>();

		GeoElement geo;
		ArrayList<AlgoElement> geoal;
		AlgoElement ale;
		ArrayList<ConstructionElement> ac;
		GeoElement[] geos;
		for (int i = conels.size() - 1; i >= 0; i--) {
			geo = (GeoElement) conels.get(i);

			// also doing this here, which is not about the name of the method,
			// but making sure textfields (which require algos) are shown
			if ((geo.getParentAlgorithm() instanceof AlgoTextfield)
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

					ac = new ArrayList<ConstructionElement>();
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
	 * @param selected
	 */
	protected ArrayList<ConstructionElement> removeFreeNonselectedGeoNumerics(
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
	protected void beforeSavingToXML(ArrayList<ConstructionElement> conels,
			ArrayList<ConstructionElement> geostohide, boolean samewindow,
			boolean putdown) {

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
	 * @param geostoshow
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
					if (label.substring(0, labelPrefix.length()).equals(
							labelPrefix)) {
						try {
							((GeoElement) geo).setLabelSimple(label
									.substring(labelPrefix.length()));

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
	public void copyToXML(App app, ArrayList<GeoElement> geos, boolean putdown) {

		boolean copyMacrosPresume = true;

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
		copiedMacros = new HashSet<Macro>();

		// create geoslocal and geostohide
		ArrayList<ConstructionElement> geoslocal = new ArrayList<ConstructionElement>();
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

		ArrayList<ConstructionElement> geostohide = addPredecessorGeos(geoslocal);

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
				if (geoslocal.contains(ce))
					ce.getXML(false, copiedXML);
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
					if (geoslocalsw.contains(ce))
						ce.getXML(false, copiedXMLforSameWindow);
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
	 * In some situations, we may need to clear the clipboard
	 */
	public void clearClipboard() {
		copiedXML = null;
		copiedXMLlabels = new ArrayList<String>();
		copiedXMLforSameWindow = null;
		copiedXMLlabelsforSameWindow = new ArrayList<String>();
		copySource = null;
		copyObject = null;
		copyObject2 = null;
		copiedMacros = null;
	}

	/**
	 * Convenience method to set new labels instead of labels
	 * 
	 * @param app
	 * @param labels
	 */
	protected void handleLabels(App app, ArrayList<String> labels,
			boolean putdown) {

		Kernel kernel = app.getKernel();
		GeoElement geo;
		String oldLabel;
		for (int i = 0; i < labels.size(); i++) {
			String ll = labels.get(i);
			geo = kernel.lookupLabel(ll);
			if (geo != null) {
				if (app.getActiveEuclidianView() == app.getEuclidianView1()) {
					app.addToEuclidianView(geo);
					if (app.hasEuclidianView2(1)) {
						geo.removeView(App.VIEW_EUCLIDIAN2);
						app.getEuclidianView2(1).remove(geo);
					}
					if (app.getEuclidianView3D() != null) {
						geo.removeView(App.VIEW_EUCLIDIAN3D);
						app.getEuclidianView3D().remove(geo);
					}
				} else if (app.getActiveEuclidianView() == app
						.getEuclidianView3D()) {
					app.removeFromEuclidianView(geo);
					if (app.getEuclidianView3D() != null) {
						geo.addView(App.VIEW_EUCLIDIAN3D);
						app.getEuclidianView3D().add(geo);
					}
					if (app.hasEuclidianView2(1)) {
						geo.removeView(App.VIEW_EUCLIDIAN2);
						app.getEuclidianView2(1).remove(geo);
					}
				} else {
					app.removeFromEuclidianView(geo);
					geo.addView(App.VIEW_EUCLIDIAN2);
					app.getEuclidianView2(1).add(geo);
					if (app.getEuclidianView3D() != null) {
						geo.removeView(App.VIEW_EUCLIDIAN3D);
						app.getEuclidianView3D().remove(geo);
					}
				}

				oldLabel = geo.getLabelSimple();
				geo.setLabel(geo.getIndexLabel(geo.getLabelSimple().substring(
						labelPrefix.length())));
				// geo.getLabelSimple() is now not the oldLabel, ideally
				if (putdown) {
					geo.getKernel().renameLabelInScripts(oldLabel,
							geo.getLabelSimple());
				}

				// geo.setLabel(geo.getDefaultLabel(false));
				app.getSelectionManager().addSelectedGeo(geo);

				if (geo.getParentAlgorithm() != null) {
					if (geo.getParentAlgorithm().getClassName()
							.equals(Commands.Sequence)) {
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
	public boolean pasteFast(App app) {
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
	public void pasteFromXML(App app, boolean putdown) {

		if (copiedXML == null)
			return;

		if (copiedXML.length() == 0)
			return;

		if (!app.getActiveEuclidianView().getEuclidianController().mayPaste())
			return;

		app.getKernel().notifyPaste();

		copyObject2 = app.getKernel().getConstruction().getUndoManager()
				.getCurrentUndoInfo();

		if (pasteFast(app) && !putdown) {
			if (copiedXMLforSameWindow == null)
				return;

			if (copiedXMLforSameWindow.length() == 0)
				return;
		}

		// it turned out to be necessary for e.g. handleLabels
		boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		// don't update selection
		app.getActiveEuclidianView().getEuclidianController()
				.clearSelections(true, false);
		// don't update properties view
		app.updateSelection(false);
		app.getActiveEuclidianView().getEuclidianController()
				.setPastePreviewSelected();

		if (pasteFast(app) && !putdown) {
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			if (ev == app.getEuclidianView1()) {
				app.getGgbApi().evalXML(copiedXMLforSameWindow.toString());
				app.getKernel().getConstruction().updateConstruction();
				app.setActiveView(App.VIEW_EUCLIDIAN);
			} else if (ev == app.getEuclidianView3D()) {
				app.getGgbApi().evalXML(copiedXMLforSameWindow.toString());
				app.getKernel().getConstruction().updateConstruction();
				app.setActiveView(App.VIEW_EUCLIDIAN3D);
			} else {
				app.getGgbApi().evalXML(copiedXMLforSameWindow.toString());
				app.getKernel().getConstruction().updateConstruction();
				app.setActiveView(App.VIEW_EUCLIDIAN2);
			}
			handleLabels(app, copiedXMLlabelsforSameWindow, putdown);
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
					app.addMacroXML(copySource
							.getApplication()
							.getXMLio()
							.getFullMacroXML(new ArrayList<Macro>(copiedMacros)));
				} catch (Exception ex) {
					App.debug("Could not load any macros at \"Paste from XML\"");
					ex.printStackTrace();
				}
			}

			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			if (ev == app.getEuclidianView1()) {
				app.getGgbApi().evalXML(copiedXML.toString());
				app.getKernel().getConstruction().updateConstruction();
				app.setActiveView(App.VIEW_EUCLIDIAN);
			} else if (ev == app.getEuclidianView3D()) {
				app.getGgbApi().evalXML(copiedXML.toString());
				app.getKernel().getConstruction().updateConstruction();
				app.setActiveView(App.VIEW_EUCLIDIAN3D);
			} else {
				app.getGgbApi().evalXML(copiedXML.toString());
				app.getKernel().getConstruction().updateConstruction();
				app.setActiveView(App.VIEW_EUCLIDIAN2);
			}
			handleLabels(app, copiedXMLlabels, putdown);
		}

		app.setBlockUpdateScripts(scriptsBlocked);

		if (!putdown) {
			app.getActiveEuclidianView().getEuclidianController()
					.setPastePreviewSelected();
		}

		app.setMode(EuclidianConstants.MODE_MOVE);

		app.getKernel().notifyPasteComplete();
	}

	/**
	 * Currently, we call this only if the pasted object is put down, but it
	 * would be better if this were called every time when kernel.storeUndoInfo
	 * called and there wasn't anything deleted
	 */
	public void pastePutDownCallback(App app) {
		if (pasteFast(app)) {
			copyObject = app.getKernel().getConstruction().getUndoManager()
					.getCurrentUndoInfo();
			copyObject2 = null;
		}
	}
}
