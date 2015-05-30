package org.geogebra.web.html5.sound;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayUtils;

public class WebAudioWrapper {
	public interface FunctionAudioBuffer {
		void fillBuffer();
	}
	public static final WebAudioWrapper INSTANCE = new WebAudioWrapper();
	private FunctionAudioBuffer buffer = null;
	private int sampleRate = 44500;
	private WebAudioWrapper() {
		init();
	}

	public native boolean init() /*-{
		var contextClass = ($wnd.AudioContext || $wnd.webkitAudioContext
				|| $wnd.mozAudioContext || $wnd.oAudioContext || $wnd.msAudioContext);
		if (contextClass) {
			// Web Audio API is available.
			$wnd.context = new contextClass();

			return true;
		} else {
			return false;
		}
	}-*/;


	public native void start(int sampleRate) /*-{
		$wnd.ins = this;
		this.@org.geogebra.web.html5.sound.WebAudioWrapper::sampleRate = sampleRate;
		$wnd.gainNode = $wnd.context.createGain();
		$wnd.gainNode.connect($wnd.context.destination);
		//$wnd.gainNode.gain.value = 50;
	}-*/;


	private native void bufferEnded(JavaScriptObject e) /*-{
		$wnd.ins.@org.geogebra.web.html5.sound.WebAudioWrapper::fill()();
	}-*/;

	public void fill() {
		if (buffer == null) {
			return;
		}
		buffer.fillBuffer();
	}
	public void write(byte[] buf, int length) {
		// stop();
		JsArrayInteger arr = JsArrayUtils.readOnlyJsArray(buf);
		createBufferSource(arr, length);
	}

	private native void createBufferSource(JsArrayInteger buf, int length) /*-{
		var audioBuffer = $wnd.context.createBuffer(1, length,
				this.@org.geogebra.web.html5.sound.WebAudioWrapper::sampleRate);

		audioBuffer.getChannelData(0).set(buf);

		var source = $wnd.context.createBufferSource();
		source.buffer = audioBuffer;
		source.connect($wnd.gainNode);
		//		source.onended = this
		//				.@org.geogebra.web.html5.sound.WebAudioWrapper::bufferEnded(Lcom/google/gwt/core/client/JavaScriptObject;);
		//
		source.start(0);
		$wnd.actualSource = source;

	}-*/;

	public native void stop() /*-{
		if ($wnd.actualSource) {
			$wnd.actualSource.stop();
			$wnd.actualSource = null;
		}
	}-*/;

	public FunctionAudioBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(FunctionAudioBuffer buffer) {
		this.buffer = buffer;
	}
}