package org.geogebra.web.html5.sound;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.js.JavaScriptInjector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.RunAsyncCallback;

public class MidiSoundW {
	public static final MidiSoundW INSTANCE = new MidiSoundW();
	protected static final String PREFIX = "[MIDIW] ";
	protected boolean jsLoaded;
	private int outputPort;
	public MidiSoundW() {
		initialize();
		outputPort = 4;
	}

	public void initialize() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				App.debug(PREFIX + "WebMIDIAPIWrapper.js loading success");
				JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE
						.webMidiAPIWrapperJs());
				MidiSoundW.this.jsLoaded = true;
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
		$wnd.mwaw.sendNoteOn(port, ch, note, velocity, time);
	}-*/;

	public native void sendAllSoundOff(int port, int ch, double time) /*-{
		$wnd.mwaw.sendAllNoteOff(port, ch, time);

	}-*/;

	// $wnd.mwaw.sendNoteOn(port, ch, note, velocity, time);


	public void playSequenceNote(int ch, int note, int velocity, double time) {
		if (!jsLoaded) {
			return;
		}
		App.debug("[MIDIW] ch: " + ch + " note: " + note + " velocity: "
				+ velocity
				+ " time: " + time);
		setupOutput();
		sendNote(0, ch, note, velocity, time);
	}

	public void stop() {
		if (!jsLoaded) {
			return;
		}
		//
		setupOutput();
		for (int i = 0; i < 16; i++) {
			sendAllSoundOff(0, i, 0);
		}
	}

	public int getOutputPort() {
		return outputPort;
	}

	public void setOutputPort(int outputPort) {
		this.outputPort = outputPort;
		setupOutput();
	}

	private native void setupOutput() /*-{
		$wnd.mwaw.ports.out[0] = $wnd.mwaw.devices.outputs[this.@org.geogebra.web.html5.sound.MidiSoundW::outputPort];
	}-*/;

}