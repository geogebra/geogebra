/*
 * JFugue - API for Music Programming
 * Copyright (C) 2003-2008  David Koelle
 *
 * http://www.jfugue.org 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *  
 */

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
public class DeviceThatWillTransmitMidi {
	private MidiDevice device;
	private Transmitter transmitter;
	private MidiReceiverForTransmitterDevice mrftd;
	// private Receiver defaultReceiver;

	/**
	 * Creates a new DeviceThatWillTransmitMidi using JFugue's Intelligent
	 * Device Resolver to pick the most likely device to open.
	 * 
	 * @throws MidiUnavailableException
	 */
	public DeviceThatWillTransmitMidi() throws MidiUnavailableException {
		this.device = IntelligentDeviceResolver.selectTransmitterDevice();
		init();
	}

	public DeviceThatWillTransmitMidi(MidiDevice.Info info)
			throws MidiUnavailableException {
		this.device = MidiSystem.getMidiDevice(info);
		init();
	}

	private void init() throws MidiUnavailableException {
		try {
			if (!(device.isOpen())) {
				device.open();
			}

			this.transmitter = device.getTransmitter();
			this.mrftd = new MidiReceiverForTransmitterDevice();
		} catch (MidiUnavailableException e) {
			device.close();
			throw e;
		}
	}

	public Transmitter getTransmitter() {
		return this.transmitter;
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
		// this.transmitter.setReceiver(this.defaultReceiver);
		device.close();
		// this.transmitter.close();
		// this.mrftd.close();
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
		transmitter.close();
		device.close();
		// this.mrftd.close();
		// this.transmitter.close();
	}

	class MidiReceiverForTransmitterDevice implements Receiver {
		private MidiParser parser;
		private Sequencer sequencer;
		private Receiver sequencerReceiver;
		private MusicStringRenderer renderer;

		public MidiReceiverForTransmitterDevice() {
			System.out.println("Built mrftd");

			parser = new MidiParser();
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
			parser.parse(message, timestamp / (1000 * 4));
			sequencerReceiver.send(message, timestamp);
		}

		@Override
		public void close() {
			sequencerReceiver.close();
		}

		public Pattern getPattern() {
			return renderer.getPattern();
		}

		public Sequence getSequence() {
			return sequencer.getSequence();
		}

	}
}