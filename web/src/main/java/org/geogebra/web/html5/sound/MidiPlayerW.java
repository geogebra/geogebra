package org.geogebra.web.html5.sound;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.js.JavaScriptInjector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

public class MidiPlayerW {
	public static final MidiPlayerW INSTANCE = new MidiPlayerW();
	protected static final String PREFIX = "[MIDIPLAYER]";
	protected boolean jsLoaded = false;

	private MidiPlayerW() {
		initialize();
	}
	public void initialize() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				App.debug(PREFIX + "jasmid.js loading success");
				JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE
						.jasmidJs());
				MidiPlayerW.this.jsLoaded = true;

			}

			public void onFailure(Throwable reason) {
				App.debug(PREFIX + "jasmid.js loading failure");
			}
		});
	}

	public native void loadFile(String url) /*-{
		var fetch = new XMLHttpRequest();
		fetch.open('GET', url);
		fetch.overrideMimeType('text/plain; charset=x-user-defined');
		fetch.onreadystatechange = function() {
			if (this.readyState === 1) {
				if (this.status === 200) {
					var t = this.responseText || '';
					var ff = [];
					var mx = t.length;
					var scc = String.fromCharCode;
					for (var z = 0; z < mx; z++) {
						ff[z] = scc(t.charCodeAt(z) & 255);
					}
					///
					var data = ff.join('');
					$wnd.midiFile = new $wnd.MidiFile(data);
					console.dir($wnd.midiFile);
				} else {

					console.log('Unable to load MIDI file');
				}
			}
		}
	}-*/;

	public void playFile(String url) {
		if (!jsLoaded) {
			return;
		}

		loadFile(url.replace('"', ' '));

	}
}
