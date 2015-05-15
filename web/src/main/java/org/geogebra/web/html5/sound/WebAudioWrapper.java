package org.geogebra.web.html5.sound;

public class WebAudioWrapper {
	public static final WebAudioWrapper INSTANCE = new WebAudioWrapper();

	private WebAudioWrapper() {
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

	public native void play() /*-{
		var source = $wnd.context.createBufferSource();
		var processor = $wnd.context.createScriptProcessor(2048);
		processor.onaudioprocess = function(e) {
			var leftOut = e.outputBuffer.getChannelData(0);
			var rightOut = e.outputBuffer.getChannelData(1);
			for (var i = 0; i < leftOut.length; i++) {
				// Add some noise
				if (true) {
					leftOut[i] += (Math.random() - 0.5) * 2;
					rightOut[i] += (Math.random() - 0.5) * 2;
				}

			}
		};

		source.connect(processor);
		processor.connect($wnd.context.destination);
	}-*/;
}