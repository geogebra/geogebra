package org.geogebra.web.html5.sound;

import org.geogebra.web.html5.Browser;

import com.google.gwt.core.client.JavaScriptObject;

public class WebAudioWrapper {
	public interface FunctionAudioListener {
		double getValueAt(double t);
	}
	public static final WebAudioWrapper INSTANCE = new WebAudioWrapper();
	private FunctionAudioListener listener = null;
	private boolean supported;
	private static double time;
	private static double deltaTime;
	private static double stopTime;
	private static JavaScriptObject context;
	private static JavaScriptObject processor;

	private WebAudioWrapper() {
		supported = !Browser.isIE() && !Browser.isFirefox();
		init();
	}

	public native boolean init() /*-{
		if (!this.@org.geogebra.web.html5.sound.WebAudioWrapper::isSupported()()) {
			return false;
		}
		var contextClass = ($wnd.AudioContext || $wnd.webkitAudioContext
				|| $wnd.mozAudioContext || $wnd.oAudioContext || $wnd.msAudioContext);
		if (contextClass) {
			// Web Audio API is available.
			@org.geogebra.web.html5.sound.WebAudioWrapper::context = new contextClass();
			@org.geogebra.web.html5.sound.WebAudioWrapper::deltaTime = 
				1 / @org.geogebra.web.html5.sound.WebAudioWrapper::context.sampleRate;
		
			@org.geogebra.web.html5.sound.WebAudioWrapper::processor
			 	= @org.geogebra.web.html5.sound.WebAudioWrapper::context.createScriptProcessor(2048, 0, 1);
			
			@org.geogebra.web.html5.sound.WebAudioWrapper::processor.onaudioprocess 
				= @org.geogebra.web.html5.sound.WebAudioWrapper::onAudioProcess(Lcom/google/gwt/core/client/JavaScriptObject;);
		
			return true;
		} else {
			return false;
		}
	}-*/;


	public native void start(double min, double max, int sampleRate) /*-{
		if (!this.@org.geogebra.web.html5.sound.WebAudioWrapper::isSupported()()) {
			return;
		}
		// TODO: use sampleRate somehow as well
		@org.geogebra.web.html5.sound.WebAudioWrapper::time = min;
		@org.geogebra.web.html5.sound.WebAudioWrapper::stopTime = max;
		@org.geogebra.web.html5.sound.WebAudioWrapper::processor
				.connect(@org.geogebra.web.html5.sound.WebAudioWrapper::context.destination);
	}-*/;

	/**
	 * Gets the value of a sound function at given time
	 * 
	 * @param t
	 *            the time for function value
	 * @return the sound value.
	 */
	public double getValueAt(double t) {
		return listener.getValueAt(t);
	}


	private static native void onAudioProcess(JavaScriptObject e) /*-{
		var data = e.outputBuffer.getChannelData(0);

		for (var i = 0; i < data.length; i++) {

			data[i] = @org.geogebra.web.html5.sound.WebAudioWrapper::INSTANCE
					.@org.geogebra.web.html5.sound.WebAudioWrapper::getValueAt(
							D)
					(@org.geogebra.web.html5.sound.WebAudioWrapper::time);
			@org.geogebra.web.html5.sound.WebAudioWrapper::time = @org.geogebra.web.html5.sound.WebAudioWrapper::time
					+ @org.geogebra.web.html5.sound.WebAudioWrapper::deltaTime;
		}
		if (@org.geogebra.web.html5.sound.WebAudioWrapper::time >= @org.geogebra.web.html5.sound.WebAudioWrapper::stopTime) {
			@org.geogebra.web.html5.sound.WebAudioWrapper::INSTANCE.@org.geogebra.web.html5.sound.WebAudioWrapper::stop()();
		}

	}-*/;

	public native void stop() /*-{
		if (!this.@org.geogebra.web.html5.sound.WebAudioWrapper::isSupported()()) {
			return;
		}
		@org.geogebra.web.html5.sound.WebAudioWrapper::processor.disconnect();
	}-*/;

	public FunctionAudioListener getListener() {
		return listener;
	}

	public void setListener(FunctionAudioListener listener) {
		this.listener = listener;
	}

	public boolean isSupported() {
		return supported;
	}

}
