package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoInputBox;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.kernel.geos.GeoStadium;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.statistics.AlgoTableToChart;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

public class InternalClipboard {
	private static final ArrayList<String> copiedXmlLabels = new ArrayList<>();
	private static final StringBuilder copiedXml = new StringBuilder();
	private static final Map<String, String> copiedImages = new HashMap<>();
	private static final Map<String, String> copiedEmbeds = new HashMap<>();
	public static final String imagePrefix = "ggbimagedata";
	public static final String embedPrefix = "ggbembeddata";

	/**
	 * Copy XML for given geos into internal clipboard
	 * @param app application
	 * @param geos selected geos
	 */
	public static void copyToXMLInternal(App app, List<GeoElement> geos) {
		if (geos.isEmpty()) {
			return;
		}

		boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		// create geoslocal and geostohide
		ArrayList<ConstructionElement> geoslocal = new ArrayList<>();
		HashSet<Group> selectedGroups = new HashSet<>(app
				.getSelectionManager().getSelectedGroups());
		for (GeoElement geo : geos) {
			if (!(geo instanceof GeoEmbed && ((GeoEmbed) geo).isGraspableMath())) {
				geoslocal.add(geo);
			}
		}

		CopyPaste.addSubGeos(geoslocal, selectedGroups);

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		ArrayList<ConstructionElement> geostohide = CopyPaste.addPredecessorGeos(geoslocal);

		geostohide.addAll(addAlgosDependentFromInside(geoslocal, null));
		// topological order to make sure client listener can process predecessor objects
		// before child objects (e.g. for multiuser)
		Collections.sort(geoslocal);
		Kernel kernel = app.getKernel();
		EmbedManager embedManager = app.getEmbedManager();
		if (embedManager != null) {
			embedManager.persist();
		}
		beforeSavingToXML(geoslocal, geostohide);

		boolean saveScriptsToXML = kernel.getSaveScriptsToXML();
		kernel.setSaveScriptsToXML(false);

		copiedXml.setLength(0);
		copiedImages.clear();
		copiedEmbeds.clear();

		Construction cons = app.getKernel().getConstruction();
		for (int i = 0; i < cons.steps(); ++i) {
			ConstructionElement ce = cons.getConstructionElement(i);
			if (ce instanceof AlgoTableToChart) {
				ce = ((AlgoTableToChart) ce).getOutput(0);
			}
			if (geoslocal.contains(ce)) {
				if (ce instanceof GeoMindMapNode
						&& !geoslocal.contains(((GeoMindMapNode) ce).getParent())) {
					((GeoMindMapNode) ce).getXMLNoParent(copiedXml);
				} else {
					ce.getXML(false, copiedXml);
				}

				if (ce instanceof GeoImage) {
					GeoImage image = (GeoImage) ce;
					String name = image.getImageFileName();
					ImageManager imageManager = (ImageManager) app.getImageManager();
					copiedImages.put(name, imageManager.getExternalImageSrc(name));
				}
				if (ce instanceof GeoEmbed && embedManager != null) {
					int embedID = ((GeoEmbed) ce).getEmbedID();
					String name = String.valueOf(embedID);
					copiedEmbeds.put(name, embedManager.getContent(embedID));
				}
			}
		}

		for (Group group : selectedGroups) {
			group.getXML(copiedXml);
		}

		kernel.setSaveScriptsToXML(saveScriptsToXML);

		afterSavingToXML(geoslocal, geostohide);
		app.setBlockUpdateScripts(scriptsBlocked);
	}

	/**
	 * copyToXML - Add the algos which belong to our selected geos Also
	 * add the geos which might be side-effects of these algos
	 * @param conels input and output
	 * @param copiedMacros output set for collecting macros or null if macro collecting not needed
	 * @return the possible side-effect geos
	 */
	public static ArrayList<ConstructionElement> addAlgosDependentFromInside(
			ArrayList<ConstructionElement> conels, Set<Macro> copiedMacros) {

		ArrayList<ConstructionElement> ret = new ArrayList<>();

		for (int i = conels.size() - 1; i >= 0; i--) {
			if (!(conels.get(i) instanceof GeoElement)) {
				continue;
			}
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
				if (ale instanceof AlgoTableToChart) {
					continue;
				}
				if (ale instanceof AlgoMacro && copiedMacros != null) {
					copiedMacros.add(((AlgoMacro) ale).getMacro());
				}
				List<ConstructionElement> ac = Arrays.asList(ale.getInput());

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
					((GeoElement) geo).addLabelPrefix(CopyPaste.labelPrefix);
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
					if (label.startsWith(CopyPaste.labelPrefix)) {
						try {
							((GeoElement) geo).setLabelSimple(
									label.substring(CopyPaste.labelPrefix.length()));
						} catch (Exception e) {
							Log.debug(e);
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

	private static void print(StringBuilder textToSave, Map<String, String> copiedImages,
			String prefix, EscapeFunction fn) {
		for (Map.Entry<String, String> image : copiedImages.entrySet()) {
			textToSave.append(prefix);
			textToSave.append(" ");
			textToSave.append(fn.escape(image.getKey()));
			textToSave.append(" ");
			textToSave.append(image.getValue());
			textToSave.append("\n");
		}
	}

	/**
	 * @param app application
	 * @param geos selected geos
	 * @param escape escape function
	 * @return text for clipboard
	 */
	public static String getTextToSave(App app, List<GeoElement> geos, EscapeFunction escape) {
		InternalClipboard.copyToXMLInternal(app, geos);

		StringBuilder textToSave = new StringBuilder();
		for (String label : copiedXmlLabels) {
			textToSave.append(label).append(" ");
		}
		textToSave.append("\n");

		print(textToSave, copiedImages, imagePrefix, escape);
		print(textToSave, copiedEmbeds, embedPrefix, escape);

		textToSave.append(copiedXml);
		return textToSave.toString();
	}

	/**
	 * @param app application
	 * @param geos elements to duplicate
	 */
	public static void duplicate(App app, List<GeoElement> geos) {
		InternalClipboard.copyToXMLInternal(app, geos);
		pasteGeoGebraXMLInternal(app, copiedXmlLabels, copiedXml.toString());
	}

	/**
	 * @param app application
	 * @param copiedXmlLabels labels of copied elements
	 * @param copiedXml copied XML
	 */
	public static void pasteGeoGebraXMLInternal(App app,
			List<String> copiedXmlLabels, String copiedXml) {
		app.getKernel().notifyPaste(copiedXml);

		// it turned out to be necessary for e.g. handleLabels
		final boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		EuclidianView ev = app.getActiveEuclidianView();
		EuclidianController euclidianController = ev.getEuclidianController();

		// first we save the selected MindMap node (if there is one)
		final MindMapPaster mindMapPaster = new MindMapPaster();
		mindMapPaster.setTargetFromSelection(app.getSelectionManager());

		// then we clear the selection and stop edit mode for all widgets
		euclidianController.clearSelections(true, false);
		euclidianController.widgetsToBackground();

		// don't update properties view
		app.updateSelection(false);
		app.getGgbApi().evalXML(copiedXml);
		app.getKernel().getConstruction().updateConstruction(false);
		if (ev == app.getEuclidianView1()) {
			app.setActiveView(App.VIEW_EUCLIDIAN);
		} else if (app.isEuclidianView3D(ev)) {
			app.setActiveView(App.VIEW_EUCLIDIAN3D);
		} else {
			app.setActiveView(App.VIEW_EUCLIDIAN2);
		}

		Set<String> duplicateLabels = copiedXmlLabels
				.stream()
				.map(label -> label.substring(CopyPaste.labelPrefix.length()))
				.collect(Collectors.toSet());

		ArrayList<GeoElement> createdElements = CopyPaste.handleLabels(
				app, copiedXmlLabels, duplicateLabels, false);

		app.setBlockUpdateScripts(scriptsBlocked);
		app.getActiveEuclidianView().invalidateDrawableList();
		app.getKernel().notifyPasteComplete(createdElements);

		if (app.isWhiteboardActive()) {
			ArrayList<GeoElement> shapes = new ArrayList<>();
			ArrayList<GeoElement> movable = new ArrayList<>();
			ArrayList<GeoMindMapNode> mindMaps = new ArrayList<>();
			for (GeoElement created : createdElements) {
				if (created.isGeoPolygon() || created.isGeoSegment()
						|| created.isGeoConic() || created instanceof GeoLocusStroke
						|| created instanceof GeoWidget || created instanceof GeoImage
						|| created instanceof GeoInline || created instanceof GeoStadium
						|| created instanceof GeoCurveCartesian) {
					shapes.add(created);
				}
				if (created instanceof GeoMindMapNode) {
					mindMaps.add((GeoMindMapNode) created);
				} else if (!groupedWithMindMap(created)) {
					movable.add(created);
				}
			}

			app.getSelectionManager().setSelectedGeos(shapes);
			euclidianController.updateBoundingBoxFromSelection(false);

			int viewCenterX = ev.getWidth() / 2;
			int viewCenterY = ev.getHeight() / 2;

			GRectangle2D boundingBoxRectangle = ev.getBoundingBox().getRectangle();

			double boxCenterX = boundingBoxRectangle.getX() + boundingBoxRectangle.getWidth() / 2;
			double boxCenterY = boundingBoxRectangle.getY() + boundingBoxRectangle.getHeight() / 2;

			Coords coords = new Coords(ev.getInvXscale() * (viewCenterX - boxCenterX),
					ev.getInvYscale() * (boxCenterY - viewCenterY), 0);

			MoveGeos.moveObjects(movable, coords, null, null, ev);
			mindMapPaster.joinToTarget(mindMaps);
			ev.updateAllDrawables(true);

			euclidianController.updateBoundingBoxFromSelection(false);
			euclidianController.showDynamicStylebar();
		}

		app.getKernel().getConstruction().getUndoManager().storeAddGeo(createdElements);
	}

	private static boolean groupedWithMindMap(GeoElement created) {
		Group parentGroup = created.getParentGroup();
		return parentGroup != null && parentGroup.stream()
				.anyMatch(geo -> geo instanceof GeoMindMapNode);
	}

	/**
	 * Escape function.
	 */
	public interface EscapeFunction {
		/**
		 * @param key raw string
		 * @return escaped string
		 */
		String escape(String key);
	}
}
