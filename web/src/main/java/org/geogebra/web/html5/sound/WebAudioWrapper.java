package org.geogebra.web.html5.sound;

import com.google.gwt.core.client.JavaScriptObject;

public class WebAudioWrapper {
	public interface FunctionAudioListener {
		double evaluate(double t);
	}
	public static final WebAudioWrapper INSTANCE = new WebAudioWrapper();
	private FunctionAudioListener listener = null;
	private WebAudioWrapper() {
		init();
	}

	public native boolean init() /*-{
		var contextClass = ($wnd.AudioContext || $wnd.webkitAudioContext
				|| $wnd.mozAudioContext || $wnd.oAudioContext || $wnd.msAudioContext);
		if (contextClass) {
			// Web Audio API is available.
			$wnd.context = new contextClass();
			$wnd.deltaTime = 1 / $wnd.context.sampleRate;
			$wnd.ins = this;
			$wnd.processor = $wnd.context.createScriptProcessor(2048, 0, 1);
			$wnd.processor.onaudioprocess = this.@org.geogebra.web.html5.sound.WebAudioWrapper::onAudioProcess(Lcom/google/gwt/core/client/JavaScriptObject;);
		
			return true;
		} else {
			return false;
		}
	}-*/;


	public native void start(double min, int sampleRate) /*-{
		// TODO: use sampleRate somehow as well
		$wnd.time = min;
		$wnd.processor.connect($wnd.context.destination);
	}-*/;

	public double eval(double t) {
		return listener.evaluate(t);
	}


	private native void onAudioProcess(JavaScriptObject e) /*-{
		var data = e.outputBuffer.getChannelData(0);
		for (var i = 0; i < data.length; i++) {

			data[i] = $wnd.ins.@org.geogebra.web.html5.sound.WebAudioWrapper::eval(D)($wnd.time);
			$wnd.time = $wnd.time + $wnd.deltaTime;

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