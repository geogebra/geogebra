package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoInputBox;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;

public class InternalClipboard {
	private static ArrayList<String> copiedXmlLabels = new ArrayList<>();
	private static StringBuilder copiedXml = new StringBuilder();

	/**
	 * @param app application
	 * @param geos copied objects
	 */
	public static void copyToXMLInternal(App app, List<GeoElement> geos) {
		if (geos.isEmpty()) {
			return;
		}

		boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		// create geoslocal and geostohide
		ArrayList<ConstructionElement> geoslocal = new ArrayList<>();
		for (GeoElement geo:geos) {
			if (!(geo instanceof GeoEmbed && ((GeoEmbed) geo).isGraspableMath())) {
				geoslocal.add(geo);
			}
		}

		CopyPaste.addSubGeos(geoslocal);

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		ArrayList<ConstructionElement> geostohide = CopyPaste.addPredecessorGeos(geoslocal);

		geostohide.addAll(addAlgosDependentFromInside(geoslocal));

		Kernel kernel = app.getKernel();
		EmbedManager embedManager = app.getEmbedManager();
		if (embedManager != null) {
			embedManager.persist();
		}
		beforeSavingToXML(geoslocal, geostohide);

		boolean saveScriptsToXML = kernel.getSaveScriptsToXML();
		kernel.setSaveScriptsToXML(false);

		copiedXml.setLength(0);
		Construction cons = app.getKernel().getConstruction();
		for (int i = 0; i < cons.steps(); ++i) {
			ConstructionElement ce = cons.getConstructionElement(i);
			if (geoslocal.contains(ce)) {
				ce.getXML(false, copiedXml);
			}
		}
		for (Group group : app.getSelectionManager().getSelectedGroups()) {
			group.getXML(copiedXml);
		}

		kernel.setSaveScriptsToXML(saveScriptsToXML);

		afterSavingToXML(geoslocal, geostohide);

		app.setBlockUpdateScripts(scriptsBlocked);
	}

	/**
	 * @param app application
	 * @param copiedXmlLabels object labels
	 * @param copiedXml partial construction XML
	 */
	public static void pasteGeoGebraXMLInternal(App app,
			ArrayList<String> copiedXmlLabels, String copiedXml) {
		app.getKernel().notifyPaste(copiedXml);

		// it turned out to be necessary for e.g. handleLabels
		boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);
		// don't update selection
		app.getActiveEuclidianView().getEuclidianController()
				.clearSelections(true, false);
		// don't update properties view
		app.updateSelection(false);

		EuclidianView ev = app.getActiveEuclidianView();
		app.getGgbApi().evalXML(copiedXml);
		app.getKernel().getConstruction().updateConstruction(false);
		if (ev == app.getEuclidianView1()) {
			app.setActiveView(App.VIEW_EUCLIDIAN);
		} else if (app.isEuclidianView3D(ev)) {
			app.setActiveView(App.VIEW_EUCLIDIAN3D);
		} else {
			app.setActiveView(App.VIEW_EUCLIDIAN2);
		}

		ArrayList<GeoElement> createdElements = CopyPaste.handleLabels(app,
				copiedXmlLabels, false);

		app.setBlockUpdateScripts(scriptsBlocked);
		app.getActiveEuclidianView().invalidateDrawableList();
		app.getKernel().notifyPasteComplete();

		if (app.isWhiteboardActive()) {
			ArrayList<GeoElement> shapes = new ArrayList<>();
			for (GeoElement created : createdElements) {
				if (created.isShape() || created instanceof GeoLocusStroke
						|| created instanceof GeoWidget || created instanceof GeoImage) {
					shapes.add(created);
				}

				if (created instanceof GeoInlineText) {
					DrawInlineText drawInlineText = (DrawInlineText) ev.getDrawableFor(created);
					drawInlineText.updateContent();
					shapes.add(created);
				}
			}

			app.getSelectionManager().setSelectedGeos(shapes);
			ev.getEuclidianController().updateBoundingBoxFromSelection(false);

			int viewCenterX = ev.getWidth() / 2;
			int viewCenterY = ev.getHeight() / 2;

			GRectangle2D boundingBoxRectangle = ev.getBoundingBox().getRectangle();

			double boxCenterX = boundingBoxRectangle.getX() + boundingBoxRectangle.getWidth() / 2;
			double boxCenterY = boundingBoxRectangle.getY() + boundingBoxRectangle.getHeight() / 2;

			Coords coords = new Coords(ev.getInvXscale() * (viewCenterX - boxCenterX),
					ev.getInvYscale() * (boxCenterY - viewCenterY), 0);

			ev.getEuclidianController().addFreePoints(createdElements);
			MoveGeos.moveObjects(createdElements, coords, null, null, ev);
			ev.updateAllDrawables(true);

			ev.getEuclidianController().updateBoundingBoxFromSelection(false);
			ev.getEuclidianController().showDynamicStylebar();
		}

		app.storeUndoInfo();
	}

	/**
	 * copyToXML - Add the algos which belong to our selected geos Also
	 * add the geos which might be side-effects of these algos
	 *
	 * @param conels input and output
	 * @return the possible side-effect geos
	 */
	private static ArrayList<ConstructionElement> addAlgosDependentFromInside(
			ArrayList<ConstructionElement> conels) {

		ArrayList<ConstructionElement> ret = new ArrayList<>();

		for (int i = conels.size() - 1; i >= 0; i--) {
			GeoElement geo = (GeoElement) conels.get(i);

			// also doing this here, which is not about the name of the method,
			// but making sure textfields (which require algos) are shown
			if ((geo.getParentAlgorithm() instanceof AlgoInputBox)
					&& (!ret.contains(geo.getParentAlgorithm()))
					&& (!conels.contains(geo.getParentAlgorithm()))) {
				// other algos will be added to this anyway,
				// so we can handle this issue in this method
				ret.add(geo.getParentAlgorithm());
			}

			ArrayList<AlgoElement> geoal = geo.getAlgorithmList();

			for (AlgoElement ale : geoal) {
				ArrayList<ConstructionElement> ac = new ArrayList<>();
				ac.addAll(Arrays.asList(ale.getInput()));

				if (conels.containsAll(ac) && !conels.contains(ale)) {
					conels.add(ale);
					for (GeoElement geoElement : ale.getOutput()) {
						if (!ret.contains(geoElement)
								&& !conels.contains(geoElement)) {
							ret.add(geoElement);
						}
					}
				}
			}
		}

		conels.addAll(ret);
		return ret;
	}

	/**
	 * copyToXML - Before saving the conels to xml, we have to rename its
	 * labels with labelPrefix and memorize those renamed labels and also hide
	 * the GeoElements in geostohide, and keep in geostohide only those which
	 * were actually hidden...
	 *
	 * @param conels construction elements
	 */
	private static void beforeSavingToXML(ArrayList<ConstructionElement> conels,
			ArrayList<ConstructionElement> geostohide) {

		copiedXmlLabels.clear();

		ConstructionElement geo;
		String label;

		for (ConstructionElement conel : conels) {
			geo = conel;
			if (geo.isGeoElement()) {
				label = ((GeoElement) geo).getLabelSimple();
				if (label != null) {
					copiedXmlLabels.add(CopyPaste.labelPrefix + label);
					((GeoElement) geo).setLabelSimple(CopyPaste.labelPrefix + label);
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
	 * @param conels construction elements
	 */
	private static void afterSavingToXML(ArrayList<ConstructionElement> conels,
			ArrayList<ConstructionElement> geostoshow) {

		ConstructionElement geo;
		String label;
		for (ConstructionElement conel : conels) {
			geo = conel;
			if (geo.isGeoElement()) {
				label = ((GeoElement) geo).getLabelSimple();
				if (label != null && label.length() >= CopyPaste.labelPrefix.length()) {
					if (label.substring(0, CopyPaste.labelPrefix.length())
							.equals(CopyPaste.labelPrefix)) {
						try {
							((GeoElement) geo).setLabelSimple(
									label.substring(CopyPaste.labelPrefix.length()));

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
	 * @return text for external clipboard as labels + newline + XML
	 */
	public static String getTextToSave() {
		StringBuilder textToSave = new StringBuilder();
		for (String label : copiedXmlLabels) {
			textToSave.append(label).append(" ");
		}
		textToSave.append("\n");
		textToSave.append(copiedXml);
		return textToSave.toString();
	}

	/**
	 * @param app application
	 * @param geos objects to duplicate
	 */
	public static void duplicate(App app, List<GeoElement> geos) {
		copyToXMLInternal(app, geos);
		pasteGeoGebraXMLInternal(app, copiedXmlLabels, copiedXml.toString());
	}
}
