package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInline;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.InternalClipboard;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.Clipboard;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Element;

import com.himamis.retex.editor.web.DocumentUtil;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.dom.Blob;
import elemental2.dom.BlobPropertyBag;
import elemental2.dom.CSSProperties;
import elemental2.dom.ClipboardEvent;
import elemental2.dom.DataTransfer;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.EventTarget;
import elemental2.dom.FileReader;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.Response;
import elemental2.dom.URL;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class CopyPasteW extends CopyPaste {

	private static final String pastePrefix = "ggbpastedata";

	private static final int defaultTextWidth = 300;

	/**
	 * @param data copied data
	 * @return blob URL to the data (as plain text)
	 */
	public static String asBlobURL(String data) {
		return URL.createObjectURL(new Blob(
				new JsArray<>(Blob.ConstructorBlobPartsArrayUnionType.of(data))));
	}

	/**
	 * @param app app
	 * @param formula LaTeX or MathML
	 */
	public static void pasteFormula(AppW app, String formula) {
		if (app.isWhiteboardActive()) {
			final EuclidianView ev = app.getActiveEuclidianView();

			final GeoFormula txt = new GeoFormula(app.getKernel().getConstruction(),
					new GPoint2D(ev.toRealWorldCoordX(-defaultTextWidth), 0));
			txt.setLabel(null);
			app.getDrawEquation().checkFirstCall();
			String asciiFormula = new SyntaxAdapterImpl(app.getKernel()).convertMath(formula);
			txt.setContent(asciiFormula);
			center(txt, ev, app);
		}
	}

	private static void center(GeoInline txt, EuclidianView ev, App app) {
		final DrawableND drawText =  app.getActiveEuclidianView()
				.getDrawableFor(txt);
		if (drawText != null) {
			drawText.update();
			((DrawInline) drawText).updateContent();
			Scheduler.get().scheduleDeferred(() -> {
				int x = (int) ((ev.getWidth() - txt.getWidth()) / 2);
				int y = (int) ((ev.getHeight() - txt.getHeight()) / 2);
				txt.setLocation(new GPoint2D(
						ev.toRealWorldCoordX(x), ev.toRealWorldCoordY(y)
				));
				drawText.update();

				ev.getEuclidianController().selectAndShowSelectionUI(txt);
				app.storeUndoInfo();
			});
		}
	}

	@Override
	public void copyToXML(App app, List<GeoElement> geos) {
		String textToSave = InternalClipboard.getTextToSave(app, geos, Global::escape);
		saveToClipboard(textToSave);
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

	/**
	 * @param toWrite string to be copied
	 */
	public static void writeToExternalClipboard(String toWrite) {
		if (copyToExternalSupported()) {
			// Supported in Chrome
			BlobPropertyBag bag =
					BlobPropertyBag.create();
			bag.setType("text/plain");
			Blob blob = new Blob(new JsArray<>(
					Blob.ConstructorBlobPartsArrayUnionType.of(toWrite)), bag);
			Clipboard.ClipboardItem data = new Clipboard.ClipboardItem(JsPropertyMap.of(
				"text/plain", blob));

			Clipboard.write(JsArray.of(data)).then(ignore -> {
				Log.debug("successfully wrote gegeobra data to clipboard");
				return null;
			}, (ignore) -> {
				Log.debug("writing geogebra data to clipboard failed");
				return null;
			});
		} else if (navigatorSupports("clipboard.writeText")) {
			// Supported in Firefox

			Clipboard.writeText(toWrite).then((ignore) -> {
				Log.debug("successfully wrote text to clipboard");
				return null;
			}, (ignore) -> {
				Log.debug("writing text to clipboard failed");
				return null;
			});
		} else {
			// Supported in Safari

			HTMLTextAreaElement copyFrom = getHiddenTextArea();
			copyFrom.value = toWrite;
			copyFrom.select();
			DocumentUtil.copySelection();
			DomGlobal.setTimeout((ignore) -> DomGlobal.document.body.focus(), 0);
		}
	}

	private static HTMLTextAreaElement getHiddenTextArea() {
		HTMLTextAreaElement hiddenTextArea = Js.uncheckedCast(
				DomGlobal.document.getElementById("hiddenCopyPasteTextArea"));
		if (Js.isFalsy(hiddenTextArea)) {
			hiddenTextArea = Js.uncheckedCast(DomGlobal.document.createElement("textarea"));
			hiddenTextArea.id = "hiddenCopyPasteTextArea";
			hiddenTextArea.style.position = "absolute";
			hiddenTextArea.style.width = CSSProperties.WidthUnionType.of("10px");
			hiddenTextArea.style.height = CSSProperties.HeightUnionType.of("10px");
			hiddenTextArea.style.zIndex = CSSProperties.ZIndexUnionType.of(100);
			hiddenTextArea.style.left = "-1000px";
			hiddenTextArea.style.top = "0px";
			DomGlobal.document.body.appendChild(hiddenTextArea);
		}
		return Js.uncheckedCast(hiddenTextArea);
	}

	private static void saveToClipboard(String toSave) {
		String escapedContent = Global.escape(toSave);
		String encoded = pastePrefix + DomGlobal.btoa(escapedContent);
		if (!NavigatorUtil.isiOS() || copyToExternalSupported()) {
			writeToExternalClipboard(encoded);
		}
		BrowserStorage.LOCAL.setItem(pastePrefix, asBlobURL(encoded));
	}

	private static boolean copyToExternalSupported() {
		return navigatorSupports("clipboard.write");
	}

	@Override
	public void pasteFromXML(final App app) {
		paste(app, text -> pasteText(app, text));
	}

	private static void handleStorageFallback(AsyncOperation<String> callback) {
		DomGlobal.fetch(BrowserStorage.LOCAL.getItem(pastePrefix)).then(Response::text)
				.then(text -> {
					callback.callback(text);
					return null;
				});
	}

	@Override
	public void paste(App app, AsyncOperation<String> plainTextFallback) {
		pasteNative(app, text -> {
			if (text.startsWith(pastePrefix)) {
				pasteEncoded(app, text);
			} else {
				plainTextFallback.callback(text);
			}
		});
	}

	/**
	 * @param app application
	 * @param callback consumer for the pasted string
	 */
	public static void pasteNative(App app, AsyncOperation<String> callback) {
		if (navigatorSupports("clipboard.read")) {
			// supported in Chrome
			Clipboard
				.read()
				.then((data) -> {
						for (int i = 0; i < data.length; i++) {
							for (int j = 0; j < data.getAt(i).types.length; j++) {
								String type = data.getAt(i).types.getAt(j);
								if (type.equals("image/png")) {
									FileReader reader = new FileReader();

									reader.addEventListener("load", (ignore) ->
											pasteImage(app, reader.result.asString()), false);

									data.getAt(i).getType("image/png").then((item) -> {
										reader.readAsDataURL(item);
										return null;
									});
								} else if (type.equals("text/plain")
										|| type.equals("text/uri-list")) {
									data.getAt(i).getType(type).then((item) -> {
										readBlob(item, callback);
										return null;
									});
									return null;
								}
							}
						}
						return null;
					},
					(reason) -> {
						Log.debug("reading data from clipboard failed " + reason);
						handleStorageFallback(callback);
						return null;
					});
		} else if (navigatorSupports("clipboard.readText")) {
			// not sure if any browser enters this at the time of writing
			Clipboard.readText().then(
				(text) -> {
					app.getActiveEuclidianView().requestFocus();
					pasteText(app, text);
					return null;
				},
				(reason) -> {
					Log.debug("reading text from clipboard failed: " + reason);
					handleStorageFallback(callback);
					return null;
				});
		} else {
			handleStorageFallback(callback);
		}
	}

	/**
	 * @param app application
	 * @param text clipboard content
	 * @return whether this is valid encoding of GGB paste data
	 */
	public static boolean pasteIfEncoded(App app, String text) {
		if (text.startsWith(pastePrefix)) {
			pasteEncoded(app, text);
			return true;
		}
		return false;
	}

	private static void pasteText(App app, String text) {
		if (text.startsWith(pastePrefix)) {
			pasteEncoded(app, text);
		} else {
			pastePlainText(app, text);
		}
	}

	private static void pasteEncoded(App app, String text) {
		String escapedContent = DomGlobal.atob(text.substring(pastePrefix.length()));
		pasteGeoGebraXML(app, Global.unescape(escapedContent));
	}

	private static void pasteImage(App app, String encodedImage) {
		((AppW) app).urlDropHappened(encodedImage, null, null, null);
	}

	/**
	 * Currently only works
	 * @param app application
	 * @param plainText plain text
	 */
	public static void pastePlainText(final App app, String plainText) {
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
			center(txt, ev, app);
		}
	}

	private static ArrayList<String> separateXMLLabels(String clipboardContent, int endline) {
		return new ArrayList<>(Arrays.asList(clipboardContent.substring(0, endline).split(" ")));
	}

	private static void pasteGeoGebraXML(App app, String clipboardContent) {
		int endline = clipboardContent.indexOf('\n');

		ArrayList<String> copiedXMLlabels = separateXMLLabels(clipboardContent, endline);

		endline++;
		while (clipboardContent.startsWith(InternalClipboard.imagePrefix, endline)
				|| clipboardContent.startsWith(InternalClipboard.embedPrefix, endline)) {
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
				() -> InternalClipboard.pasteGeoGebraXMLInternal(app, copiedXMLlabels, copiedXML));
	}

	private static void handleSpecialLine(String[] tokens, App app) {
		String prefix = tokens[0];
		String name = Global.unescape(tokens[1]);
		String content = tokens[2];
		if (InternalClipboard.imagePrefix.equals(prefix)) {
			ImageManagerW imageManager = ((AppW) app).getImageManager();
			// only add images if they come from a different app
			if (imageManager.getExternalImage(name, false) == null) {
				imageManager.addExternalImage(name, content);
				HTMLImageElement img = imageManager.getExternalImage(name, true);
				img.src = content;
			}
		} else {
			EmbedManager embedManager = app.getEmbedManager();
			if (embedManager != null) {
				embedManager.setContent(Integer.parseInt(name), content);
			}
		}
	}

	@Override
	public void duplicate(App app, List<GeoElement> geos) {
		InternalClipboard.duplicate(app, geos);
	}

	@Override
	public void clearClipboard() {
		BrowserStorage.LOCAL.setItem(pastePrefix, "");
	}

	@Override
	public void copyTextToSystemClipboard(String text) {
		Log.debug("copying to clipboard " + text);
		writeToExternalClipboard(text);
	}

	/**
	 * @param app application
	 * @param element event target
	 */
	public static void installCutCopyPaste(AppW app, Element element) {
		EventTarget target = Js.uncheckedCast(element);
		app.getGlobalHandlers().addEventListener(target, "paste", (event) -> {
			if (incorrectTarget(event.target)) {
				return;
			}
			DataTransfer clipboardData = Js.<ClipboardEvent>uncheckedCast(event).clipboardData;
			if (clipboardData.files.length > 0) {
				FileReader reader = new FileReader();
				reader.addEventListener("load",
						(ignore) -> pasteImage(app, reader.result.asString()));

				reader.readAsDataURL(clipboardData.files.getAt(0));
				return;
			}

			String text = clipboardData.getData("text/plain");
			if (Js.isTruthy(text)) {
				pasteText(app, text);
				event.preventDefault(); // avoid conflict with Murok
				return;
			}

			pasteInternal(app);
		});

		EventListener cutCopy = (event) -> {
			if (incorrectTarget(event.target)) {
				return;
			}

			CopyPaste.handleCutCopy(app, "cut".equals(event.type));
		};

		app.getGlobalHandlers().addEventListener(target, "copy", cutCopy);
		app.getGlobalHandlers().addEventListener(target, "cut", cutCopy);
	}

	/**
	 * Paste from internal keyboard
	 * @param app application
	 */
	public static void pasteInternal(AppW app) {
		handleStorageFallback(content -> {
			if (!StringUtil.empty(content)) {
				pasteGeoGebraXML(app, content);
			}
		});
	}

	/**
	 * @param tgt the target element of the event
	 * @return true if the event targets an input element,
	 * in which case it should be handled by the browser
	 */
	public static boolean incorrectTarget(EventTarget tgt) {
		elemental2.dom.Element target = Js.uncheckedCast(tgt);
		return "INPUT".equalsIgnoreCase(target.tagName)
				|| "TEXTAREA".equalsIgnoreCase(target.tagName)
				|| "BR".equalsIgnoreCase(target.tagName) || target.hasAttribute("contenteditable");
	}

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

	private static void onPermission(AsyncOperation<Boolean> callback) {
		Clipboard.read().then((data) -> {
			if (data.length == 0 || data.getAt(0).types.length == 0) {
				callback.callback(false);
				return null;
			}

			if ("image/png".equals(data.getAt(0).types.getAt(0))) {
				callback.callback(true);
			} else if ("text/plain".equals(data.getAt(0).types.getAt(0))) {
				data.getAt(0).getType("text/plain").then((item) -> {
					callback.callback(item.size > 0);
					return null;
				});
			}
			return null;
		}, (ignore) -> {
			callback.callback(true);
			return null;
		});
	}

	/**
	 * Check if there is any readable content in the system clipboard (if supported),
	 * or the internal clipboard (if not)
	 */
	public static void checkClipboard(AsyncOperation<Boolean> callback) {
		if (navigatorSupports("clipboard.read")) {
			if (navigatorSupports("permissions")) {
				Promise<Permissions.Permission> promise =
						Permissions.query(JsPropertyMap.of("name", "clipboard-read"));

				promise.then((result) -> {
					if ("granted".equals(result.state)) {
						onPermission(callback);
					} else {
						callback.callback(true);
					}
					return null;
				});
			} else {
				// Safari doesn't have navigator.permissions, checking content
				// directly triggers an extra popup on Mac -> just assume we can paste
				callback.callback(true);
			}
		} else {
			callback.callback(!StringUtil.empty(BrowserStorage.LOCAL.getItem(pastePrefix)));
		}
	}

	private static boolean navigatorSupports(String s) {
		return Js.isTruthy(Js.asPropertyMap(DomGlobal.navigator).nestedGet(s));
	}
}
