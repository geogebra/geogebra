package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
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
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.storage.client.Storage;

public class CopyPasteW extends CopyPaste {

	private static final String pastePrefix = "ggbpastedata";

	private static final int defaultTextWidth = 300;

	@Override
	public void copyToXML(App app, List<GeoElement> geos) {
		InternalClipboard.copyToXMLInternal(app, geos);
		String textToSave = InternalClipboard.getTextToSave();
		saveToClipboard(textToSave);
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
		if (!Browser.isiOS()) {
			String encoded = pastePrefix + GlobalFunctions.btoa(toSave);
			writeToExternalClipboard(encoded);
		}
		Storage.getLocalStorageIfSupported().setItem(pastePrefix, toSave);
	}

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
								} else if (data[i].types[j] === 'text/plain') {
									data[i].getType('text/plain').then(function(item) {
										item.text().then(function(text) {
											@org.geogebra.web.html5.util.CopyPasteW::pasteText(*)(app, text);
										});
									});
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
	private static void pasteText(App app, String text) {
		if (text.startsWith(pastePrefix)) {
			pasteGeoGebraXML(app, GlobalFunctions.atob(text.substring(pastePrefix.length())));
		} else {
			pastePlainText(app, text);
		}
	}

	@ExternalAccess
	private static void pasteImage(App app, String encodedImage) {
		((AppW) app).urlDropHappened(encodedImage, null, null, null);
	}

	private static void pastePlainText(final App app, String plainText) {
		if (app.isWhiteboardActive()) {
			final EuclidianView ev = app.getActiveEuclidianView();

			final GeoInlineText txt = new GeoInlineText(app.getKernel().getConstruction(),
					new GPoint2D(ev.toRealWorldCoordX(-defaultTextWidth), 0),
					defaultTextWidth, GeoInlineText.DEFAULT_HEIGHT);
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

			final DrawInlineText drawText = (DrawInlineText) app.getActiveEuclidianView()
					.getDrawableFor(txt);
			drawText.update();
			drawText.updateContent();

			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					int x = (ev.getWidth() - defaultTextWidth) / 2;
					int y = (int) ((ev.getHeight() - txt.getHeight()) / 2);
					txt.setLocation(new GPoint2D(
							ev.toRealWorldCoordX(x), ev.toRealWorldCoordY(y)
					));
					drawText.update();

					ev.getEuclidianController().selectAndShowBoundingBox(txt);
					app.storeUndoInfo();
				}
			});
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
		InternalClipboard.pasteGeoGebraXMLInternal(app, copiedXMLlabels, copiedXML);
	}

	@Override
	public native void clearClipboard() /*-{
		$wnd.localStorage.setItem(
			@org.geogebra.web.html5.util.CopyPasteW::pastePrefix, '');
	}-*/;

	public static native void installCutCopyPaste(App app, Element target) /*-{
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
	 * @param appX application
	 */
	public static void pasteInternal(App appX) {
		String stored = Storage.getLocalStorageIfSupported().getItem(pastePrefix);
		if (!StringUtil.empty(stored)) {
			pasteGeoGebraXML(appX, stored);
		}
	}

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
					$wnd.navigator.clipboard.read().then(function(data) {
						if (data.length === 0 || data[0].types.length === 0) {
							callback.@org.geogebra.common.util.AsyncOperation::callback(*)(false);
							return
						}

						if (data[0].types[0] === 'image/png') {
							callback.@org.geogebra.common.util.AsyncOperation::callback(*)(true);
						} else if (data[0].types[0] === 'text/plain') {
							data[0].getType('text/plain').then(function(item) {
								item.text().then(function(text) {
									callback.@org.geogebra.common.util.AsyncOperation::callback(*)(text !== "");
								});
							});
						}
					}, function() {
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
