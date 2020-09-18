package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianController;
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
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;

import elemental2.core.Global;
import elemental2.dom.Blob;
import elemental2.dom.DomGlobal;
import elemental2.dom.FileReader;

public class CopyPasteW extends CopyPaste {

	private static final String pastePrefix = "ggbpastedata";
	private static final String imagePrefix = "ggbimagedata";
	private static final String embedPrefix = "ggbembeddata";

	private static final int defaultTextWidth = 300;

	private static final ArrayList<String> copiedXmlLabels = new ArrayList<>();
	private static final StringBuilder copiedXml = new StringBuilder();
	private static final Map<String, String> copiedImages = new HashMap<>();
	private static final Map<String, String> copiedEmbeds = new HashMap<>();

	/**
	 * copyToXML - Add the algos which belong to our selected geos Also
	 * add the geos which might be side-effects of these algos
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
					copiedXmlLabels.add(labelPrefix + label);
					((GeoElement) geo).setLabelSimple(labelPrefix + label);
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
				if (label != null && label.length() >= labelPrefix.length()) {
					if (label.startsWith(labelPrefix)) {
						try {
							((GeoElement) geo).setLabelSimple(
									label.substring(labelPrefix.length()));
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

	@Override
	public void copyToXML(App app, List<GeoElement> geos) {
		copyToXMLInternal(app, geos);

		StringBuilder textToSave = new StringBuilder();
		for (String label : copiedXmlLabels) {
			textToSave.append(label).append(" ");
		}
		textToSave.append("\n");

		print(textToSave, copiedImages, imagePrefix);
		print(textToSave, copiedEmbeds, embedPrefix);

		textToSave.append(copiedXml);

		saveToClipboard(textToSave.toString());
	}

	private void print(StringBuilder textToSave, Map<String, String> copiedImages, String prefix) {
		for (Map.Entry<String, String> image : copiedImages.entrySet()) {
			textToSave.append(prefix);
			textToSave.append(" ");
			textToSave.append(Global.escape(image.getKey()));
			textToSave.append(" ");
			textToSave.append(image.getValue());
			textToSave.append("\n");
		}
	}

	private static void copyToXMLInternal(App app, List<GeoElement> geos) {
		if (geos.isEmpty()) {
			return;
		}

		boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		// create geoslocal and geostohide
		ArrayList<ConstructionElement> geoslocal = new ArrayList<>();
		for (GeoElement geo : geos) {
			if (!(geo instanceof GeoEmbed && ((GeoEmbed) geo).isGraspableMath())) {
				geoslocal.add(geo);
			}
		}

		addSubGeos(geoslocal);

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		ArrayList<ConstructionElement> geostohide = addPredecessorGeos(geoslocal);

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
		copiedImages.clear();
		copiedEmbeds.clear();

		Construction cons = app.getKernel().getConstruction();
		for (int i = 0; i < cons.steps(); ++i) {
			ConstructionElement ce = cons.getConstructionElement(i);
			if (geoslocal.contains(ce)) {
				ce.getXML(false, copiedXml);

				if (ce instanceof GeoImage) {
					GeoImage image = (GeoImage) ce;
					String name = image.getImageFileName();
					ImageManagerW imageManager = ((ImageManagerW) app.getImageManager());
					copiedImages.put(name, imageManager.getExternalImageSrc(name));
				}
				if (ce instanceof GeoEmbed && embedManager != null) {
					int embedID = ((GeoEmbed) ce).getEmbedID();
					String name = String.valueOf(embedID);
					copiedEmbeds.put(name, embedManager.getContent(embedID));
				}
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
	 * @param toWrite text to be copied
	 * @return whether text is non-empty
	 */
	public static boolean writeToExternalClipboardIfNonempty(String toWrite) {
		if (StringUtil.empty(toWrite)) {
			return false;
		}
		writeToExternalClipboard(toWrite);
		BrowserStorage.LOCAL.setItem(pastePrefix, toWrite);
		return true;
	}

	public static native void writeToExternalClipboard(String toWrite) /*-{
		if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.write) {
			// Supported in Chrome

			var data = new ClipboardItem({
				'text/plain': new Blob([toWrite], {
					type: 'text/plain'
				})
			});

			$wnd.navigator.clipboard.write([data]).then(function() {
				@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("successfully wrote gegeobra data to clipboard");
			}, function() {
				@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("writing geogebra data to clipboard failed");
			});
		} else if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.writeText) {
			// Supported in Firefox

			$wnd.navigator.clipboard.writeText(toWrite).then(function() {
				@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("successfully wrote text to clipboard");
			}, function() {
				@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("writing text to clipboard failed");
			});
		} else {
			// Supported in Safari

			var copyFrom = @org.geogebra.web.html5.main.AppW::getHiddenTextArea()();
			copyFrom.value = toWrite;
			copyFrom.select();
			$doc.execCommand('copy');
			$wnd.setTimeout(function() {
				$doc.body.focus();
			}, 0);
		}
	}-*/;

	private static void saveToClipboard(String toSave) {
		String escapedContent = Global.escape(toSave);
		String encoded = pastePrefix + DomGlobal.btoa(escapedContent);
		if (!Browser.isiOS() || copyToExternalSupported()) {
			writeToExternalClipboard(encoded);
		}
		BrowserStorage.LOCAL.setItem(pastePrefix, encoded);
	}

	private static native boolean copyToExternalSupported() /*-{
		return !!($wnd.navigator.clipboard && $wnd.navigator.clipboard.write);
	}-*/;

	@Override
	public void pasteFromXML(final App app) {
		paste(app, text -> pasteText((AppW) app, text));
	}

	@ExternalAccess
	private static void handleStorageFallback(AsyncOperation<String> callback) {
		callback.callback(BrowserStorage.LOCAL.getItem(pastePrefix));
	}

	@Override
	public void paste(App app, AsyncOperation<String> plainTextFallback) {
		pasteNative(app, text -> {
			if (text.startsWith(pastePrefix)) {
				pasteEncoded((AppW) app, text);
			} else {
				plainTextFallback.callback(text);
			}
		});
	}

	public static native void pasteNative(App app, AsyncOperation<String> callback) /*-{
		function storageFallback() {
			@org.geogebra.web.html5.util.CopyPasteW::handleStorageFallback(*)(callback);
		}

		if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.read) {
			// supported in Chrome

			$wnd.navigator.clipboard
				.read()
				.then(function(data) {
						for (var i = 0; i < data.length; i++) {
							for (var j = 0; j < data[i].types.length; j++) {
								if (data[i].types[j] === 'image/png') {
									var reader = new FileReader();

									reader.addEventListener("load", function() {
										@org.geogebra.web.html5.util.CopyPasteW::pasteImage(*)(app, this.result);
									}, false);

									data[i].getType('image/png').then(function(item) {
										reader.readAsDataURL(item);
									});
								} else if (data[i].types[j] === 'text/plain'
										|| data[i].types[j] === 'text/uri-list') {
									data[i].getType(data[i].types[j]).then(function(item) {
										@org.geogebra.web.html5.util.CopyPasteW::readBlob(*)(item, callback);
									});
									return;
								}
							}
						}
					},
					function(reason) {
						@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("reading data from clipboard failed " + reason);
						storageFallback();
					});
		} else if ($wnd.navigator.clipboard
			&& $wnd.navigator.clipboard.readText) {
			// not sure if any browser enters this at the time of writing

			$wnd.navigator.clipboard.readText().then(
				function(text) {
					@org.geogebra.web.html5.util.CopyPasteW::pasteText(*)(app, text);
				},
				function(reason) {
					@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("reading text from clipboard failed: " + reason);
					storageFallback();
				})
		} else {
			storageFallback();
		}
	}-*/;

	@ExternalAccess
	private static void pasteText(AppW app, String text) {
		if (text.startsWith(pastePrefix)) {
			pasteEncoded(app, text);
		} else {
			pastePlainText(app, text);
		}
	}

	private static void pasteEncoded(AppW app, String text) {
		String escapedContent = DomGlobal.atob(text.substring(pastePrefix.length()));
		pasteGeoGebraXML(app, Global.unescape(escapedContent));
	}

	@ExternalAccess
	private static void pasteImage(App app, String encodedImage) {
		((AppW) app).urlDropHappened(encodedImage, null, null, null);
	}

	private static void pastePlainText(final App app, String plainText) {
		if (app.isWhiteboardActive()) {
			final EuclidianView ev = app.getActiveEuclidianView();

			final GeoInlineText txt = new GeoInlineText(app.getKernel().getConstruction(),
					new GPoint2D(ev.toRealWorldCoordX(-defaultTextWidth), 0));
			txt.setSize(defaultTextWidth, GeoInlineText.DEFAULT_HEIGHT);
			txt.setLabel(null);

			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();
			try {
				object.put("text", plainText);
			} catch (JSONException e) {
				Log.error(e.getMessage());
				return;
			}
			array.put(object);

			txt.setContent(array.toString());

			final DrawableND drawText =  app.getActiveEuclidianView()
					.getDrawableFor(txt);
			if (drawText != null) {
				drawText.update();
				((DrawInlineText) drawText).updateContent();
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						int x = (ev.getWidth() - defaultTextWidth) / 2;
						int y = (int) ((ev.getHeight() - txt.getHeight()) / 2);
						txt.setLocation(new GPoint2D(
								ev.toRealWorldCoordX(x), ev.toRealWorldCoordY(y)
						));
						drawText.update();

						ev.getEuclidianController().selectAndShowSelectionUI(txt);
						app.storeUndoInfo();
					}
				});
			}
		}
	}

	private static ArrayList<String> separateXMLLabels(String clipboardContent, int endline) {
		return new ArrayList<>(Arrays.asList(clipboardContent.substring(0, endline).split(" ")));
	}

	@ExternalAccess
	private static void pasteGeoGebraXML(AppW app, String clipboardContent) {
		int endline = clipboardContent.indexOf('\n');

		ArrayList<String> copiedXMLlabels = separateXMLLabels(clipboardContent, endline);

		endline++;
		while (clipboardContent.startsWith(imagePrefix, endline)
				|| clipboardContent.startsWith(embedPrefix, endline)) {
			int nextEndline = clipboardContent.indexOf('\n', endline);
			String line = clipboardContent
					.substring(endline, nextEndline);

			String[] tokens = line.split(" ", 3);
			if (tokens.length == 3) {
				handleSpecialLine(tokens, app);
			}
			endline = nextEndline + 1;
		}

		String copiedXML = clipboardContent.substring(endline);

		Scheduler.get().scheduleDeferred(
				() -> pasteGeoGebraXMLInternal(app, copiedXMLlabels, copiedXML));
	}

	private static void handleSpecialLine(String[] tokens, AppW app) {
		String prefix = tokens[0];
		String name = Global.unescape(tokens[1]);
		String content = tokens[2];
		if (imagePrefix.equals(prefix)) {
			ImageManagerW imageManager = app.getImageManager();
			imageManager.addExternalImage(name, content);
			ImageElement img = imageManager.getExternalImage(name, app, true);
			img.setSrc(content);
		} else {
			EmbedManager embedManager = app.getEmbedManager();
			if (embedManager != null) {
				embedManager.setContent(Integer.parseInt(name), content);
			}
		}
	}

	private static void pasteGeoGebraXMLInternal(App app,
			ArrayList<String> copiedXmlLabels, String copiedXml) {
		app.getKernel().notifyPaste(copiedXml);

		// it turned out to be necessary for e.g. handleLabels
		final boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		EuclidianView ev = app.getActiveEuclidianView();
		// don't update selection
		EuclidianController euclidianController = ev.getEuclidianController();
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

		ArrayList<GeoElement> createdElements = handleLabels(app, copiedXmlLabels, false);

		app.setBlockUpdateScripts(scriptsBlocked);
		app.getActiveEuclidianView().invalidateDrawableList();
		app.getKernel().notifyPasteComplete();

		if (app.isWhiteboardActive()) {
			ArrayList<GeoElement> shapes = new ArrayList<>();
			for (GeoElement created : createdElements) {
				if (created.isShape() || created instanceof GeoLocusStroke
						|| created instanceof GeoWidget || created instanceof GeoImage
						|| created instanceof GeoInline) {
					shapes.add(created);
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

			euclidianController.addFreePoints(createdElements);
			MoveGeos.moveObjects(createdElements, coords, null, null, ev);
			ev.updateAllDrawables(true);

			euclidianController.updateBoundingBoxFromSelection(false);
			euclidianController.showDynamicStylebar();
		}

		app.storeUndoInfo();
	}

	@Override
	public void duplicate(App app, List<GeoElement> geos) {
		copyToXMLInternal(app, geos);
		pasteGeoGebraXMLInternal(app, copiedXmlLabels, copiedXml.toString());
	}

	@Override
	public native void clearClipboard() /*-{
		$wnd.localStorage.setItem(
			@org.geogebra.web.html5.util.CopyPasteW::pastePrefix, '');
	}-*/;

	@Override
	public void copyTextToSystemClipboard(String text) {
		Log.debug("copying to clipboard " + text);
		writeToExternalClipboard(text);
	}

	public static native void installCutCopyPaste(AppW app, Element target) /*-{
		function incorrectTarget(target) {
			return target.tagName.toUpperCase() === 'INPUT'
				|| target.tagName.toUpperCase() === 'TEXTAREA'
				|| target.tagName.toUpperCase() === 'BR'
				|| target.parentElement.classList.contains('mowTextEditor');
		}

		target.addEventListener('paste', function(a) {
			if (incorrectTarget(a.target)) {
				return;
			}

			if (a.clipboardData.files.length > 0) {
				var reader = new FileReader();

				reader.addEventListener("load", function() {
					@org.geogebra.web.html5.util.CopyPasteW::pasteImage(*)(app, this.result);
				}, false);

				reader.readAsDataURL(a.clipboardData.files[0]);
				return;
			}

			var text = a.clipboardData.getData("text/plain");
			if (text) {
				@org.geogebra.web.html5.util.CopyPasteW::pasteText(*)(app, text);
				return;
			}

			@org.geogebra.web.html5.util.CopyPasteW::pasteInternal(*)(app);
		});

		function cutCopy(event) {
			if (incorrectTarget(event.target)) {
				return;
			}

			@org.geogebra.common.util.CopyPaste::handleCutCopy(*)(app, event.type === 'cut');
		}

		target.addEventListener('copy', cutCopy);
		target.addEventListener('cut', cutCopy)
	}-*/;

	/**
	 * Paste from internal keyboard
	 * @param app application
	 */
	public static void pasteInternal(AppW app) {
		String stored = BrowserStorage.LOCAL.getItem(pastePrefix);
		if (!StringUtil.empty(stored)) {
			pasteGeoGebraXML(app, stored);
		}
	}

	@ExternalAccess
	private static void readBlob(Blob blob, AsyncOperation<String> callback) {
		// in Chrome one could use blob.text().then(callback)
		// but the FileReader code is also compatible with Safari 13.1
		FileReader reader = new FileReader();
		reader.addEventListener("loadend", evt -> {
			if (reader.result != null) {
				callback.callback(reader.result.asString());
			}
		});
		reader.readAsText(blob);
	}

	/**
	 * Check if there is any readable content in the system clipboard (if supported),
	 * or the internal clipboard (if not)
	 */
	public static native void checkClipboard(AsyncOperation<Boolean> callback) /*-{
		if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.read) {
			var onPermission = function() {
				$wnd.navigator.clipboard.read().then(function(data) {
					if (data.length === 0 || data[0].types.length === 0) {
						callback.@org.geogebra.common.util.AsyncOperation::callback(*)(false);
						return
					}

					if (data[0].types[0] === 'image/png') {
						callback.@org.geogebra.common.util.AsyncOperation::callback(*)(true);
					} else if (data[0].types[0] === 'text/plain') {
						data[0].getType('text/plain').then(function(item) {
							callback.@org.geogebra.common.util.AsyncOperation::callback(*)(item.size > 0);
						});
					}
				}, function() {
					callback.@org.geogebra.common.util.AsyncOperation::callback(*)(true);
				})
			}
			if ($wnd.navigator.permissions) {
				$wnd.navigator.permissions.query({
					name: 'clipboard-read'
				}).then(function(result) {
					if (result.state === "granted") {
						onPermission();
					} else {
						callback.@org.geogebra.common.util.AsyncOperation::callback(*)(true);
					}
				});
			} else {
				// Safari doesn't have navigator.permissions, checking content
				// directly triggers an extra popup on Mac -> just assume we can paste
				callback.@org.geogebra.common.util.AsyncOperation::callback(*)(true);
			}
		} else {
			var pastePrefix = @org.geogebra.web.html5.util.CopyPasteW::pastePrefix;
			var stored = $wnd.localStorage.getItem(pastePrefix);
			callback.@org.geogebra.common.util.AsyncOperation::callback(*)(!!stored);
		}
	}-*/;
}
