package org.geogebra.web.html5.sound;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.js.JavaScriptInjector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Command;

public class MidiSoundW {
	public interface MidiSoundListenerW {
		void onInfo(String msg);
		void onError(int errorCode);
	}
	public static final MidiSoundW INSTANCE = new MidiSoundW();
	protected static final String PREFIX = "[MIDISOUNDW] ";
	private static final String MS_WAVE_SYNTH = "Microsoft GS Wavetable Synth";
	private static final String TIMIDITY = "TiMidity port 0";
	private static final String IAC = "IAC Driver Bus 1";
	private static final int NO_PORT = -1;
	public static final int MIDI_ERROR_PORT = 1;
	protected boolean jsLoaded;
	protected List<String> outputs;
	private MidiSoundListenerW listener = null;

	// storing commands while MIDI is not fully initialized.
	protected List<Command> cmdQueue;

	private int outputPort;
	public MidiSoundW() {
		initialize();
		outputs = new ArrayList<String>();
		outputPort = NO_PORT;
		cmdQueue = new ArrayList<Command>();
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
			that.@org.geogebra.web.html5.sound.MidiSoundW::processQueue()();

		}

		$wnd.mwaw.initMidi();

	}-*/;

	public void add(String item) {
		App.debug(PREFIX + "Adding output: " + item);

		outputs.add(item);
	}
	
	public native JavaScriptObject sendNote(int port, int ch, int note,
			double velocity, double time) /*-{
		$wnd.mwaw.initializePerformanceNow();
		$wnd.mwaw.sendNoteOn(port, ch, note, velocity, 0);
		$wnd.mwaw.sendNoteOff(port, ch, note, velocity, 1600 * time);
	}-*/;

	public native void sendAllSoundOff(int port, int ch, double time) /*-{
		$wnd.mwaw.sendAllNoteOff(port, ch, time);

	}-*/;

	// $wnd.mwaw.sendNoteOn(port, ch, note, velocity, time);

	private void processCommand(Command cmd) {
		if (isValid()) {
			cmd.execute();
		} else {
			cmdQueue.add(cmd);
		}
	}

	private void processQueue() {
		for (Command cmd : cmdQueue) {
			cmd.execute();
		}
		cmdQueue.clear();
	}
	public void playSequenceNote(final int ch, final int note,
			final int velocity, final double time) {

		processCommand(new Command() {

			public void execute() {
				App.debug("[MIDIW] ch: " + ch + " note: " + note
						+ " velocity: " + velocity + " time: " + time);
				sendNote(0, ch, note, velocity, time);
			}
		});

	}

	private void selectPort() {
		App.debug(PREFIX + "selectPort()");
		for (int i = 0; i < outputs.size(); i++) {
			String out = outputs.get(i);
			App.debug(PREFIX + "Available output: " + out + "(" + i + ")");
			if (MS_WAVE_SYNTH.equals(out) || TIMIDITY.equals(out)
					|| IAC.equals(out)) {
				outputPort = i;
				listener.onInfo("Selected output: " + out);
				break;
			}
		}
		if (outputPort == NO_PORT) {
			if (outputs.size() == 0) {
				listener.onError(MIDI_ERROR_PORT);
			} else {
				outputPort = 0;
				listener.onInfo("Selecting default output: "
						+ outputs.get(outputPort));

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

	public void playMidiFile(final String url) {
		processCommand(new Command() {

			public void execute() {
				App.debug(PREFIX + "playing midi file " + url);
				MidiPlayerW.INSTANCE.playFile(url);
			}
		});
	}

	public MidiSoundListenerW getListener() {
		return listener;
	}

	public void setListener(MidiSoundListenerW listener) {
		this.listener = listener;
	}

}