package org.geogebra.web.html5.sound;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.js.JavaScriptInjector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

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

	private void load(String url) {
		RequestCallback cb = new RequestCallback() {

			public void onResponseReceived(Request request, Response response) {
				App.debug("response is " + response.getText());
	//			createMidiFile(response.getText());
			}

			public void onError(Request request, Throwable exception) {
				// TODO Auto-generated method stub

			}
		};

		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, url);

		try {
			rb.setHeader("Content-type", "text/plain");
			rb.sendRequest("", cb);
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private native void createMidiFile(String url)/*-{
		var fetch = new XMLHttpRequest();
		fetch.open('GET', url);
		fetch.overrideMimeType("text/plain; charset=x-user-defined");
		fetch.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var t = this.responseText || "";
				var ff = [];
				var mx = t.length;
				var scc = String.fromCharCode;
				for (var z = 0; z < mx; z++) {
					ff[z] = scc(t.charCodeAt(z) & 255);
				}
				$wnd.midiFile = new $wnd.MidiFile(ff.join(""));
				@org.geogebra.common.main.App::debug(Ljava/lang/String;)($wnd.midiFile);

			}
		}
		fetch.send();

	}-*/;


	public void playFile(String url) {
		if (!jsLoaded) {
			return;
		}

		createMidiFile(url.replace('"', ' '));

	}
}
