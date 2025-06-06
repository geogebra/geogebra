package org.geogebra.web.html5.sound;

import elemental2.core.Float32Array;
import elemental2.media.AudioContext;
import elemental2.media.AudioProcessingEvent;
import elemental2.media.ScriptProcessorNode;

public final class WebAudioWrapper {

	private static WebAudioWrapper INSTANCE;

	private FunctionAudioListener listener = null;

	private double time;
	private double deltaTime;
	private double stopTime;

	private AudioContext context;
	private ScriptProcessorNode processor;

	interface FunctionAudioListener {
		double getValueAt(double t);
	}

	private WebAudioWrapper() {
		init();
	}

	/**
	 * @return the audio wrapper instance (singleton)
	 */
	public static WebAudioWrapper getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new WebAudioWrapper();
		}

		return INSTANCE;
	}

	private void init() {
		context = new AudioContext();
		deltaTime = 1 / context.sampleRate;

		processor = context.createScriptProcessor(2048, 0, 1);

		processor.onaudioprocess = ScriptProcessorNode.OnaudioprocessUnionType
				.of((ScriptProcessorNode.OnaudioprocessFn) this::onAudioProcess);
	}

	void start(double min, double max) {
		time = min;
		stopTime = max;
		processor.connect(context.destination);
	}

	private boolean onAudioProcess(AudioProcessingEvent event) {
		Float32Array data = event.outputBuffer.getChannelData(0);

		for (int i = 0; i < data.length; i++) {
			data.setAt(i, listener.getValueAt(time));
			time += deltaTime;
		}
		if (time >= stopTime) {
			stop();
		}

		return true;
	}

	void stop() {
		processor.disconnect();
	}

	public FunctionAudioListener getListener() {
		return listener;
	}

	public void setListener(FunctionAudioListener listener) {
		this.listener = listener;
	}
}
