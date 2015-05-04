package org.geogebra.web.html5.sound;

import org.geogebra.common.main.App;

import com.google.gwt.core.client.JavaScriptObject;

public class MidiW {
	public static final MidiW INSTANCE = new MidiW();

	public MidiW() {
		init();
	}

	public static native void init() /*-{
	//navigator.requestMIDIAccess() callbacks
	$wnd.onRequestMidiSuccess = $entry(@org.geogebra.web.html5.sound.MidiW::onRequestMidiSuccess(Lcom/google/gwt/core/client/JavaScriptObject;));
	$wnd.onRequestMidiError = $entry(@org.geogebra.web.html5.sound.MidiW::onRequestMidiError(Lcom/google/gwt/core/client/JavaScriptObject;));
	//init Web MIDI
	$wnd.navigator.requestMIDIAccess().then($wnd.onRequestMidiSuccess, $wnd.onRequestMidiError);
}-*/;

	/**
	 * Success callback for <code>navigator.requestMIDIAccess()</code>.
	 */
	public static void onRequestMidiSuccess(JavaScriptObject midiAccess) {
		App.debug("[MIDI] initialized: ");
		getMidiOutput(midiAccess);

	}

	public static native void getMidiOutput(JavaScriptObject midiAccess) /*-{
		var out = midiAccess.outputs.get(0);
		$wnd.console.log(out);
	}-*/;

	/**
	 * Error callback for <code>navigator.requestMIDIAccess()</code>.
	 */
	public static void onRequestMidiError(JavaScriptObject error) {
		App.debug("[MIDI] initializer faiure.");

	}

	public void playNote() {
		App.debug("[MIDI] foo");

	}
}