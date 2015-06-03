package org.geogebra.web.html5.sound;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayUtils;

public class WebAudioWrapper {
	public interface FunctionAudioListener {
		void fillBuffer();
	}
	public static final WebAudioWrapper INSTANCE = new WebAudioWrapper();
	private FunctionAudioListener listener = null;
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
		$wnd.gainNode.gain.value = 10;
		$wnd.processor = $wnd.context.createScriptProcessor(2048, 0, 1);
		$wnd.processor.onaudioprocess = this.@org.geogebra.web.html5.sound.WebAudioWrapper::onAudioProcess(Lcom/google/gwt/core/client/JavaScriptObject;);
		$wnd.processor.connect($wnd.gainNode);
		$wnd.gainNode.connect($wnd.context.destination);
		$wnd.counter = 0;
	}-*/;

	public void fill() {
		if (listener == null) {
			return;
		}
		listener.fillBuffer();
	}
	public void write(byte[] buf, int length) {
		JsArrayInteger arr = JsArrayUtils.readOnlyJsArray(buf);
		addToBuffer(arr);
	}

	private native void addToBuffer(JsArrayInteger buf) /*-{
		console.log("BUF length " + buf.length);
		$wnd.audata = new Float32Array(buf.length);
		for (var i = 0; i < buf.length; i++) {
			$wnd.audata[i] = buf[i] / 32767.0
		}
	}-*/;

	private native void onAudioProcess(JavaScriptObject e) /*-{
		var data = e.outputBuffer.getChannelData(0);
		for (var i = 0; i < data.length; i++) {
			data[i] = $wnd.audata[i];
		}

		$wnd.counter++;
		if ($wnd.counter === 10) {
			$wnd.ins.@org.geogebra.web.html5.sound.WebAudioWrapper::fill()();
			$wnd.counter = 0;
		}
	}-*/;
	public native void stop() /*-{
		$wnd.processor.disconnect($wnd.context.destination);
	}-*/;

	public FunctionAudioListener getListener() {
		return listener;
	}

	public void setListener(FunctionAudioListener listener) {
		this.listener = listener;
	}
}