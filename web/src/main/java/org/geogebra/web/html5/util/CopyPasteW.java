package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoInputBox;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoMedia;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;

public class CopyPasteW extends CopyPaste {

	@ExternalAccess
	private static final String pastePrefix = "ggbpastedata";

	private static final int defaultTextWidth = 300;

	private static ArrayList<String> copiedXmlLabels = new ArrayList<>();
	private static StringBuilder copiedXml = new StringBuilder();

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
				if (label != null && label.length() >= labelPrefix.length()) {
					if (label.substring(0, labelPrefix.length())
							.equals(labelPrefix)) {
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
		textToSave.append(copiedXml);

		saveToClipboard(textToSave.toString());
	}

	private static void copyToXMLInternal(App app, List<GeoElement> geos) {
		if (geos.isEmpty()) {
			return;
		}

		boolean scriptsBlocked = app.isBlockUpdateScripts();
		app.setBlockUpdateScripts(true);

		// create geoslocal and geostohide
		ArrayList<ConstructionElement> geoslocal = new ArrayList<>();
		geoslocal.addAll(geos);

		addSubGeos(geoslocal);

		if (geoslocal.isEmpty()) {
			app.setBlockUpdateScripts(scriptsBlocked);
			return;
		}

		ArrayList<ConstructionElement> geostohide = addPredecessorGeos(geoslocal);

		geostohide.addAll(addAlgosDependentFromInside(geoslocal));

		Kernel kernel = app.getKernel();

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

		kernel.setSaveScriptsToXML(saveScriptsToXML);

		afterSavingToXML(geoslocal, geostohide);

		app.setBlockUpdateScripts(scriptsBlocked);
	}

	private static native void saveToClipboard(String toSave) /*-{
		var encoded = @org.geogebra.web.html5.util.CopyPasteW::pastePrefix
				+ btoa(toSave);

		if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.write) {
			// Supported in Chrome

			var data = new ClipboardItem({
				'text/plain' : new Blob([ encoded ], {
					type : 'text/plain'
				})
			});

			$wnd.navigator.clipboard.write([ data ]).then(function() {
				console.log("successfully wrote gegeobra data to clipboard");
			}, function() {
				console.log("writing geogebra data to clipboard failed");
			});
		} else {
			if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.writeText) {
				// Supported in Firefox

				$wnd.navigator.clipboard.writeText(encoded).then(function() {
					console.log("successfully wrote text to clipboard");
				}, function() {
					console.log("writing text to clipboard failed");
				});
			}
		}

		$wnd.localStorage.setItem(
				@org.geogebra.web.html5.util.CopyPasteW::pastePrefix, toSave);
	}-*/;

	@Override
	public native void pasteFromXML(App app)  /*-{
		function storageFallback() {
			var stored = $wnd.localStorage
					.getItem(@org.geogebra.web.html5.util.CopyPasteW::pastePrefix);
			if (stored) {
				@org.geogebra.web.html5.util.CopyPasteW::pasteGeoGebraXML(*)(app, stored);
			}
		}

		if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.read) {
			// supported in Chrome

			$wnd.navigator.clipboard
					.read()
					.then(
							function(data) {
								for (var i = 0; i < data.length; i++) {
									for (var j = 0; j < data[i].types.length; j++) {
										if (data[i].types[j] === 'image/png') {
											var reader = new FileReader();

											reader
													.addEventListener(
															"load",
															function() {
																@org.geogebra.web.html5.util.CopyPasteW::pasteImage(*)(app, this.result);
															}, false);

											data[i]
													.getType('image/png')
													.then(
															function(item) {
																reader
																		.readAsDataURL(item);
															});
										} else if (data[i].types[j] === 'text/plain') {
											data[i]
													.getType('text/plain')
													.then(
															function(item) {
																item
																		.text()
																		.then(
																				function(
																						text) {
																					@org.geogebra.web.html5.util.CopyPasteW::pasteText(*)(app, text);
																				});
															});
										}
									}
								}
							},
							function(reason) {
								console
										.log("reading data from clipboard failed "
												+ reason);
								storageFallback();
							});
		} else if ($wnd.navigator.clipboard
				&& $wnd.navigator.clipboard.readText) {
			// not sure if any browser enters this at the time of writing

			$wnd.navigator.clipboard
					.readText()
					.then(
							function(text) {
								@org.geogebra.web.html5.util.CopyPasteW::pasteText(*)(app, text);
							},
							function(reason) {
								console
										.log("reading text from clipboard failed: "
												+ reason);
								storageFallback();
							})
		} else {
			storageFallback();
		}
	}-*/;

	@ExternalAccess
	private static native void pasteText(App app, String text) /*-{
		var pastePrefix = @org.geogebra.web.html5.util.CopyPasteW::pastePrefix;

		if (text.startsWith(pastePrefix)) {
			@org.geogebra.web.html5.util.CopyPasteW::pasteGeoGebraXML(*)(app, atob(text.substring(pastePrefix.length)));
		} else {
			@org.geogebra.web.html5.util.CopyPasteW::pastePlainText(*)(app, text);
		}
	}-*/;

	@ExternalAccess
	private static void pasteImage(App app, String encodedImage) {
		((AppW) app).urlDropHappened(encodedImage, null, null, null);
	}

	@ExternalAccess
	private static void pastePlainText(App app, String plainText) {
		EuclidianView ev = app.getActiveEuclidianView();

		GeoText txt = app.getKernel().getAlgebraProcessor().text(plainText);
		txt.setLabel(null);

		DrawText drawText = (DrawText) app.getActiveEuclidianView().getDrawableFor(txt);
		GRectangle bounds = AwtFactory.getPrototype().newRectangle(
				0, 0, defaultTextWidth, 0);
		drawText.adjustBoundingBoxToText(bounds);

		txt.setNeedsUpdatedBoundingBox(true);
		txt.update();

		try {
			txt.setStartPoint(new GeoPoint(app.getKernel().getConstruction(),
					ev.toRealWorldCoordX((ev.getWidth() - defaultTextWidth) / 2.0),
					ev.toRealWorldCoordY((ev.getHeight() - drawText.getBounds().getHeight()) / 2),
					1));
		} catch (CircularDefinitionException e) {
			// should never happen
		}

		txt.setNeedsUpdatedBoundingBox(true);
		txt.update();

		if (app.isWhiteboardActive()) {
			ev.getEuclidianController().selectAndShowBoundingBox(txt);
		}
	}

	private static ArrayList<String> separateXMLLabels(String clipboardContent) {
		return new ArrayList<>(Arrays.asList(clipboardContent.split("\n")[0].split(" ")));
	}

	private static String separateCopiedXML(String clipboardContent) {
		return clipboardContent.substring(clipboardContent.indexOf('\n'));
	}

	@ExternalAccess
	private static void pasteGeoGebraXML(App app, String clipboardContent) {
		ArrayList<String> copiedXMLlabels = separateXMLLabels(clipboardContent);
		String copiedXML = separateCopiedXML(clipboardContent);

		pasteGeoGebraXMLInternal(app, copiedXMLlabels, copiedXML);
	}

	private static void pasteGeoGebraXMLInternal(App app,
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

		ArrayList<GeoElement> createdElements = handleLabels(app, copiedXmlLabels, false);

		app.setBlockUpdateScripts(scriptsBlocked);

		app.getKernel().notifyPasteComplete();

		if (app.isWhiteboardActive()) {
			ArrayList<GeoElement> shapes = new ArrayList<>();
			for (GeoElement created : createdElements) {
				if (created.isShape() || created instanceof GeoLocusStroke
						|| created instanceof GeoMedia || created instanceof GeoText
						|| created instanceof GeoImage) {
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
	}

	@Override
	public void duplicate(App app, List<GeoElement> geos) {
		copyToXMLInternal(app, geos);
		pasteGeoGebraXMLInternal(app, copiedXmlLabels, copiedXml.toString());
	}

	public static native void installPaste(App app, Element target) /*-{
		target
				.addEventListener(
						'paste',
						function(a) {
							if (a.target.tagName.toUpperCase() === 'INPUT'
									|| a.target.tagName.toUpperCase() === 'TEXTAREA'
									|| a.target.tagName.toUpperCase() === 'BR'
									|| a.target.parentElement.classList
											.contains("mowTextEditor")) {
								return;
							}

							var pastePrefix = @org.geogebra.web.html5.util.CopyPasteW::pastePrefix;

							var text = a.clipboardData.getData("text/plain");
							if (text) {
								@org.geogebra.web.html5.util.CopyPasteW::pasteText(*)(app, text);
								return;
							}

							if (a.clipboardData.files.length > 0) {
								var reader = new FileReader();

								reader
										.addEventListener(
												"load",
												function() {
													@org.geogebra.web.html5.util.CopyPasteW::pasteImage(*)(app, this.result);
												}, false);

								reader.readAsDataURL(a.clipboardData.files[0]);
								return;
							}

							var stored = $wnd.localStorage.getItem(pastePrefix);
							if (stored) {
								@org.geogebra.web.html5.util.CopyPasteW::pasteGeoGebraXML(*)(app, stored);
							}
						});
	}-*/;

	/**
	 * Check if there is any readable content in the system clipboard (if supported),
	 * or the internal clipboard (if not)
	 */
	public static native void checkClipboard(AsyncOperation<Boolean> callback) /*-{
		if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.readText) {
			$wnd.navigator.permissions.query({
				name: 'clipboard-read'
			}).then(function(result) {
				if (result.state === "granted") {
					$wnd.navigator.clipboard.read().then(function (data) {
						if (data.length === 0 || data[0].types.length === 0) {
							callback.@org.geogebra.common.util.AsyncOperation::callback(*)(false);
							return
						}

						if (data[0].types[0] === 'image/png') {
							callback.@org.geogebra.common.util.AsyncOperation::callback(*)(true);
						} else if (data[0].types[0] === 'text/plain') {
							data[0].getType('text/plain').then(function (item) {
								item.text().then(function (text) {
									callback.@org.geogebra.common.util.AsyncOperation::callback(*)(text !== "");
								});
							});
						 }
					}, function () {
						callback.@org.geogebra.common.util.AsyncOperation::callback(*)(true);
					})
				} else {
					callback.@org.geogebra.common.util.AsyncOperation::callback(*)(true);
				}
			});
		} else {
			var pastePrefix = @org.geogebra.web.html5.util.CopyPasteW::pastePrefix;
			var stored = $wnd.localStorage.getItem(pastePrefix);
			callback.@org.geogebra.common.util.AsyncOperation::callback(*)(!!stored);
		}
	}-*/;
}
