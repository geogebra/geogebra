/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.jfugue;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

/**
 * Represents an attached MIDI device, such as a keyboard. This class uses
 * javax.sound.MidiDevice, but is not derived from javax.sound.MidiDevice.
 * 
 * @author David Koelle
 * @version 3.0
 */
public class ReceiverDevice {
	// private Receiver receiver;

	public ReceiverDevice(MidiDevice.Info info)
			throws MidiUnavailableException {
		MidiDevice device = null;

		device = MidiSystem.getMidiDevice(info);
		if (!(device.isOpen())) {
			device.open();
		}

		// this.receiver = device.getReceiver();
	}

	/**
	 * Send the given sequence to the MIDI device - use this to send MIDI files
	 * to your keyboard!
	 * 
	 * @param sequence
	 *            The sequence to send to the MIDI device
	 */
	// public void sendSequence(Sequence sequence)
	// {
	// TimeEventManager tem = new TimeEventManager();
	//
	// // ==============================================
	// // made null because getEvents does not exist
	// // TODO must fix this if we ever need it
	// MidiEvent[] events = tem.getEvents(sequence);
	// // ==============================================
	//
	// long elapsedTime = 0;
	// for (int i = 0; i < events.length; i++) {
	// MidiEvent event = events[i];
	// MidiMessage message = event.getMessage();
	//
	// long timestamp = event.getTick();
	// long deltaTime = timestamp - elapsedTime;
	// elapsedTime = timestamp;
	//
	// if (deltaTime < 500) {
	// System.out.print("sleeping for "+deltaTime+"...");
	// try {
	// Thread.sleep((int)(deltaTime * 1.25));
	// } catch (Exception ex)
	// {
	// ex.printStackTrace();
	// }
	// System.out.println("awake");
	// }
	//
	// receiver.send(message, -1);
	// }
	// }
}
