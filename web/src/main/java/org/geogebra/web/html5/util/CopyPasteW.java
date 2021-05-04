package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.common.util.InternalClipboard;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;

import elemental2.core.Global;
import elemental2.dom.Blob;
import elemental2.dom.DomGlobal;
import elemental2.dom.FileReader;
import elemental2.dom.HTMLImageElement;

public class CopyPasteW extends CopyPaste {

	private static final String pastePrefix = "ggbpastedata";

	private static final int defaultTextWidth = 300;

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

	public static native void writeToExternalClipboard(String toWrite) /*-{
		if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.write) {
			// Supported in Chrome

			var data = new ClipboardItem({
				'text/plain': new Blob([toWrite], {
					type: 'text/plain'
				})
			});

			$wnd.navigator.clipboard.write([data]).then(function() {
				@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)("successfully wrote gegeobra data to clipboard");
			}, function() {
				@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)("writing geogebra data to clipboard failed");
			});
		} else if ($wnd.navigator.clipboard && $wnd.navigator.clipboard.writeText) {
			// Supported in Firefox

			$wnd.navigator.clipboard.writeText(toWrite).then(function() {
				@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)("successfully wrote text to clipboard");
			}, function() {
				@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)("writing text to clipboard failed");
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
						@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)("reading data from clipboard failed " + reason);
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
					@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)("reading text from clipboard failed: " + reason);
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

	private static void handleSpecialLine(String[] tokens, AppW app) {
		String prefix = tokens[0];
		String name = Global.unescape(tokens[1]);
		String content = tokens[2];
		if (InternalClipboard.imagePrefix.equals(prefix)) {
			ImageManagerW imageManager = app.getImageManager();
			imageManager.addExternalImage(name, content);
			HTMLImageElement img = imageManager.getExternalImage(name, app, true);
			img.src = content;
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
