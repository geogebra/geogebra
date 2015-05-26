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


	public native void start() /*-{
		$wnd.ins = this;
		$wnd.gainNode = $wnd.context.createGain();
		$wnd.gainNode.connect($wnd.context.destination);
		$wnd.gainNode.gain.value = 0.1;
	}-*/;


	// private native void audioProcess(JavaScriptObject e) /*-{
	// var leftOut = e.outputBuffer.getChannelData(0);
	// var rightOut = e.outputBuffer.getChannelData(1);
	// // for (var i = 0; i < leftOut.length; i++) {
	// // leftOut[i] += (Math.random() - 0.5) * 0.05;
	// // rightOut[i] += (Math.random() - 0.5) * 0.05;
	// //
	// // }
	//
	// var d = 0.5;
	// if ($wnd.buf) {
	// for (var i = 0; i < $wnd.buf.length; i++) {
	// leftOut[i] = $wnd.buf[i] * d;
	// rightOut[i] = $wnd.buf[i] * d;
	//
	// }
	// }
	// $wnd.ins.@org.geogebra.web.html5.sound.WebAudioWrapper::fill()();
	//
	// }-*/;

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
		JsArrayInteger arr = JsArrayUtils.readOnlyJsArray(buf);
		createBufferSource(arr);
	}

	private native void toBuffer(JsArrayInteger buf) /*-{
		if ($wnd.buf && $wnd.buf.length + buf.length <= 2048) {
			$wnd.buf += buf;
		} else {
			$wnd.buf = buf;
		}
	}-*/;

	private native void createBufferSource(JsArrayInteger buf) /*-{
		var source = $wnd.context.createBufferSource();
		source.buffer = buf;
		source.connect($wnd.gainNode);
		source.onended = this
				.@org.geogebra.web.html5.sound.WebAudioWrapper::bufferEnded(Lcom/google/gwt/core/client/JavaScriptObject;);
		
		source.start();
	}-*/;
	public void stop() {
		// TODO Auto-generated method stub

	}

	public FunctionAudioBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(FunctionAudioBuffer buffer) {
		this.buffer = buffer;
	}
}