package org.geogebra.web.html5.sound;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.js.JavaScriptInjector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.RunAsyncCallback;

public class MidiW {
	public static final MidiW INSTANCE = new MidiW();
	protected static final String PREFIX = "[MIDIW] ";
	protected boolean jsLoaded;
	JavaScriptObject mwaw;
	public MidiW() {
		initialize();
	}

	public void initialize() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				App.debug(PREFIX + "WebMIDIAPIWrapper.js loading success");
				JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE
						.webMidiAPIWrapperJs());
				MidiW.this.jsLoaded = true;
				init();

			}

			public void onFailure(Throwable reason) {
				App.debug(PREFIX + "WebMIDIAPIWrapper.js loading failure");
			}
		});
	}

	public native JavaScriptObject init() /*-{
		var mwaw = new $wnd.WebMIDIAPIWrapper(true);
		mwaw.initMidi();
		this.@org.geogebra.web.html5.sound.MidiW::mwaw = mwaw;
	}-*/;

	public native JavaScriptObject play() /*-{
		var m = this.@org.geogebra.web.html5.sound.MidiW::mwaw;
		console.dir(PREFIX + m);
	}-*/;


	public void playNote() {
		if (!jsLoaded) {
			return;
		}

		play();
	}
}