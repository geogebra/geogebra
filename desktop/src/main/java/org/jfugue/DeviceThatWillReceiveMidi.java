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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;

/**
 * Represents an attached MIDI device, such as a keyboard - use this class to
 * send MIDI from your JFugue program to a keyboard or sythesizer. This class
 * uses javax.sound.MidiDevice, but is not derived from javax.sound.MidiDevice.
 * 
 * @author David Koelle
 * @version 3.0
 */
public class DeviceThatWillReceiveMidi {
	private MidiDevice device;
	private Receiver receiver;

	/**
	 * Creates a new DeviceThatWillReceiveMidi using JFugue's Intelligent Device
	 * Resolver to pick the most likely device to open.
	 * 
	 * @throws MidiUnavailableException
	 */
	public DeviceThatWillReceiveMidi() throws MidiUnavailableException {
		this.device = IntelligentDeviceResolver.selectReceiverDevice();
		init();
	}

	public DeviceThatWillReceiveMidi(MidiDevice.Info info)
			throws MidiUnavailableException {
		this.device = MidiSystem.getMidiDevice(info);
		init();
	}

	private void init() throws MidiUnavailableException {
		if (!(device.isOpen())) {
			device.open();
		}

		this.receiver = device.getReceiver();
	}

	/**
	 * Send the given sequence to the MIDI device - use this to send MIDI files
	 * to your keyboard!
	 * 
	 * @param sequence
	 *            The sequence to send to the MIDI device
	 */
	public void sendSequence(Sequence sequence) {
		TimeFactor.sortAndDeliverMidiMessages(sequence,
				new MidiMessageRecipient() {
					@Override
					public void messageReady(MidiMessage message,
							long timestamp) {
						receiver.send(message, -1);
					}
				});

		// Send messages to turn all controllers and all notes off for all
		// tracks (channels)
		ShortMessage allControllersOff = new ShortMessage();
		ShortMessage allNotesOff = new ShortMessage();
		for (byte track = 0; track < 16; track++) {
			try {
				allControllersOff.setMessage(ShortMessage.CONTROL_CHANGE, track,
						(byte) 121, (byte) 0);
				receiver.send(allControllersOff, -1);
				allNotesOff.setMessage(ShortMessage.CONTROL_CHANGE, track,
						(byte) 123, (byte) 0);
				receiver.send(allNotesOff, -1);
			} catch (InvalidMidiDataException e) {
				throw new JFugueException(
						JFugueException.ERROR_PLAYING_MUSIC + e);
			}
		}
	}

	public void close() {
		receiver.close();
		device.close();
	}

	public Receiver getReceiver() {
		return this.receiver;
	}
}
