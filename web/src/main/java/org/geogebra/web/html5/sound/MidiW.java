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
		$wnd.mwaw = new $wnd.WebMIDIAPIWrapper(true);

		$wnd.mwaw.initMidi();
	}-*/;

	public native JavaScriptObject sendNote(int port, int ch, int note,
			double velocity, double time) /*-{
		$wnd.mwaw.initializePerformanceNow();

		$wnd.mwaw.ports.out[0] = $wnd.mwaw.devices.outputs[0];
		$wnd.mwaw.sendNoteOn(port, ch, note, velocity, time);
	}-*/;


	public void playNote(int ch, int note, int velocity, double time) {
		if (!jsLoaded) {
			return;
		}
		sendNote(0, ch, note, velocity, time);
	}
}