package org.jfugue;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;

/**
 * Represents an attached MIDI device, such as a keyboard. This class uses
 * javax.sound.MidiDevice, but is not derived from javax.sound.MidiDevice.
 * 
 * @author David Koelle
 * @version 3.0
 */
public class TransmitterDevice {
	private Transmitter transmitter;
	private MidiReceiverForTransmitterDevice mrftd;
	private Receiver defaultReceiver;

	public TransmitterDevice(MidiDevice.Info info)
			throws MidiUnavailableException {
		MidiDevice device = null;

		device = MidiSystem.getMidiDevice(info);

		if (!(device.isOpen())) {
			device.open();
		}

		this.transmitter = device.getTransmitter();
		this.defaultReceiver = this.transmitter.getReceiver();
		this.mrftd = new MidiReceiverForTransmitterDevice();
	}

	public void addParserListener(ParserListener listener) {
		this.mrftd.getParser().addParserListener(listener);
	}

	public void removeParserListener(ParserListener listener) {
		this.mrftd.getParser().removeParserListener(listener);
	}

	/**
	 * Reads a pattern from the external device - use this to record the keys
	 * you're pressing on the keyboard!
	 * 
	 * This method will return a JFugue Pattern, which you can then manipulate
	 * to your heart's content.
	 * 
	 * @return The Pattern representing the music played on the device
	 */
	public void startListening() {
		this.transmitter.setReceiver(this.mrftd);
	}

	public void stopListening() {
		this.transmitter.setReceiver(this.defaultReceiver);
	}

	public void listenForMillis(long millis) throws InterruptedException {
		startListening();
		Thread.sleep(millis);
		stopListening();
	}

	public Pattern getPatternFromListening() {
		return this.mrftd.getPattern();
	}

	public Sequence getSequenceFromListening() {
		return this.mrftd.getSequence();
	}

	public void close() {
		this.mrftd.close();
		this.transmitter.close();
	}

	class MidiReceiverForTransmitterDevice implements Receiver {
		private MidiParser parser;
		private Sequencer sequencer;
		private Receiver sequencerReceiver;
		private MusicStringRenderer renderer;

		public MidiReceiverForTransmitterDevice() {
			System.out.println("Built mrftd");

			parser = new MidiParser();
			parser.setTracing(Parser.TRACING_ON);
			renderer = new MusicStringRenderer();
			parser.addParserListener(renderer);

			try {
				sequencer = MidiSystem.getSequencer();
				sequencerReceiver = sequencer.getReceiver();
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}

		public Parser getParser() {
			return this.parser;
		}

		@Override
		public void send(MidiMessage message, long timestamp) {
			System.out.println("Parsing " + message + " ts: " + timestamp);
			parser.parse(message, timestamp);
			sequencerReceiver.send(message, timestamp);
		}

		@Override
		public void close() {
		}

		public Pattern getPattern() {
			return renderer.getPattern();
		}

		public Sequence getSequence() {
			return sequencer.getSequence();
		}

	}
}
