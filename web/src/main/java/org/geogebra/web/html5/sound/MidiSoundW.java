package org.geogebra.web.html5.sound;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.js.JavaScriptInjector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.RunAsyncCallback;

public class MidiSoundW {
	public static final MidiSoundW INSTANCE = new MidiSoundW();
	protected static final String PREFIX = "[MIDISOUNDW] ";
	private static final String MS_WAVE_SYNTH = "Microsoft GS Wavetable Synth";
	private static final String TIMIDITY = "TiMidity port 0";
	private static final String IAC = "Bus 1";
	private static final int NO_PORT = -1;
	protected boolean jsLoaded;
	protected List<String> outputs;
	private int outputPort;
	public MidiSoundW() {
		initialize();
		outputs = new ArrayList<String>();
		outputPort = NO_PORT;
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

		var that = this;
		$wnd.mwaw.setMidiOutputSelect = function() {
			var i;
			for (i = 0; i < $wnd.mwaw.devices.outputs.length; i++) {
				that.@org.geogebra.web.html5.sound.MidiSoundW::add(Ljava/lang/String;)($wnd.mwaw.devices.outputs[i]["name"]);
			}

			that.@org.geogebra.web.html5.sound.MidiSoundW::selectPort()();
			$wnd.mwaw.ports.out[0] = $wnd.mwaw.devices.outputs[that.@org.geogebra.web.html5.sound.MidiSoundW::outputPort];

		}

		$wnd.mwaw.initMidi();

	}-*/;

	public void add(String item) {
		App.debug(PREFIX + "Adding output: " + item);

		outputs.add(item);
	}
	
	public native JavaScriptObject sendNote(int port, int ch, int note,
			double velocity, double time) /*-{
		$wnd.mwaw.sendNoteOn(port, ch, note, velocity, time);
	}-*/;

	public native void sendAllSoundOff(int port, int ch, double time) /*-{
		$wnd.mwaw.sendAllNoteOff(port, ch, time);

	}-*/;

	// $wnd.mwaw.sendNoteOn(port, ch, note, velocity, time);


	public void playSequenceNote(int ch, int note, int velocity, double time) {
		if (!isValid()) {
			return;
		}
		App.debug("[MIDIW] ch: " + ch + " note: " + note + " velocity: "
				+ velocity
				+ " time: " + time);
		sendNote(0, ch, note, velocity, time);
	}

	private void selectPort() {
		App.debug(PREFIX + "selectPort()");
		for (int i = 0; i < outputs.size(); i++) {
			String out = outputs.get(i);
			// App.debug(PREFIX + "Available output: " + out + "(" + i + ")");
			if (MS_WAVE_SYNTH.equals(out) || TIMIDITY.equals(out)
					|| IAC.equals(out)) {
				outputPort = i;
				App.debug(PREFIX + "Selected output: " + out + "(" + outputPort
						+ ")");
				break;
			}
		}

	}

	public void stop() {
		if (!isValid()) {
			return;
		}

		for (int i = 0; i < 16; i++) {
			sendAllSoundOff(0, i, 0);
		}
	}

	public int getOutputPort() {
		return outputPort;
	}

	public void setOutputPort(int outputPort) {
		this.outputPort = outputPort;
	}

	public boolean isValid() {
		return jsLoaded && outputPort != NO_PORT;
	}

	private native void setupOutput() /*-{

		$wnd.mwaw.ports.out[0] = $wnd.mwaw.devices.outputs[this.@org.geogebra.web.html5.sound.MidiSoundW::outputPort];
	}-*/;

	public void playMidiFile(String url) {
		App.debug(PREFIX + "playing midi file " + url);
		MidiPlayerW.INSTANCE.playFile(url);
	}

}