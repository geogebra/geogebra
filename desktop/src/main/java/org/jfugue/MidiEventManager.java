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
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * Places musical data into the MIDI sequence.
 *
 * <p>
 * This was named EventManager in previous versions of JFugue.
 * </p>
 *
 * @author David Koelle
 * @version 2.0
 * @version 3.0 - renamed to MidiEventManager
 */
public final class MidiEventManager {
	private final static int CHANNELS = 16;
	private final static int LAYERS = 16;
	private byte currentTrack = 0;
	private byte[] currentLayer = new byte[CHANNELS];
	private long time[][] = new long[CHANNELS][LAYERS];
	private Sequence sequence;
	private Track track[] = new Track[CHANNELS];

	public MidiEventManager(float sequenceTiming, int resolution) {
		try {
			this.sequence = new Sequence(sequenceTiming, resolution);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < CHANNELS; i++) {
			for (int u = 0; u < LAYERS; u++) {
				time[i][u] = 0;
			}
			currentLayer[i] = 0;
			track[i] = sequence.createTrack();
		}
		currentTrack = 0;
	}

	/**
	 * Sets the current track, or channel, to which new events will be added.
	 * 
	 * @param track
	 *            the track to select
	 */
	public void setCurrentTrack(byte track) {
		currentTrack = track;
	}

	/**
	 * Sets the current layer within the track to which new events will be
	 * added.
	 * 
	 * @param track
	 *            the track to select
	 */
	public void setCurrentLayer(byte layer) {
		currentLayer[currentTrack] = layer;
	}

	/**
	 * Advances the timer for the current track by the specified duration, which
	 * is specified in Pulses Per Quarter (PPQ)
	 * 
	 * @param duration
	 *            the duration to increase the track timer
	 */
	public void advanceTrackTimer(long duration) {
		time[currentTrack][currentLayer[currentTrack]] += duration;
	}

	/**
	 * Sets the timer for the current track by the given time, which is
	 * specified in Pulses Per Quarter (PPQ)
	 * 
	 * @param newTime
	 *            the time at which to set the track timer
	 */
	public void setTrackTimer(long newTime) {
		time[currentTrack][currentLayer[currentTrack]] = newTime;
	}

	/**
	 * Returns the timer for the current track.
	 * 
	 * @return the timer value for the current track, specified in Pulses Per
	 *         Quarter (PPQ)
	 */
	public long getTrackTimer() {
		return time[currentTrack][currentLayer[currentTrack]];
	}

	/**
	 * Adds a MetaMessage to the current track.
	 *
	 * @param definition
	 *            the MIDI command represented by this message
	 * @param data1
	 *            the first data byte
	 * @param data2
	 *            the second data byte
	 */
	public void addMetaMessage(int type, byte[] bytes) {
		try {
			MetaMessage message = new MetaMessage();
			message.setMessage(type, bytes, bytes.length);
			MidiEvent event = new MidiEvent(message, getTrackTimer());
			track[currentTrack].add(event);
		} catch (InvalidMidiDataException e) {
			// We've kept a good eye on the data. This exception won't happen.
			e.printStackTrace();
		}
	}

	/**
	 * Adds a MIDI event to the current track.
	 *
	 * @param command
	 *            the MIDI command represented by this message
	 * @param data1
	 *            the first data byte
	 */
	public void addEvent(int command, int data1) {
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(command, currentTrack, data1);
			MidiEvent event = new MidiEvent(message, getTrackTimer());
			track[currentTrack].add(event);
		} catch (InvalidMidiDataException e) {
			// We've kept a good eye on the data. This exception won't happen.
			e.printStackTrace();
		}
	}

	/**
	 * Adds a MIDI event to the current track.
	 *
	 * @param command
	 *            the MIDI command represented by this message
	 * @param data1
	 *            the first data byte
	 * @param data2
	 *            the second data byte
	 */
	public void addEvent(int command, int data1, int data2) {
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(command, currentTrack, data1, data2);
			MidiEvent event = new MidiEvent(message, getTrackTimer());
			track[currentTrack].add(event);
		} catch (InvalidMidiDataException e) {
			// We've kept a good eye on the data. This exception won't happen.
			e.printStackTrace();
		}
	}

	/**
	 * Adds a ShortMessage.NOTE_ON event to the current track, using attack and
	 * decay velocity values. Also adds a ShortMessage.NOTE_OFF command for the
	 * note, using the duration parameter to space the NOTE_OFF command
	 * properly.
	 * 
	 * Both the NOTE_ON and NOTE_OFF events can be suppressed. This is useful
	 * when notes are tied to other notes.
	 *
	 * @param data1
	 *            the first data byte, which contains the note value
	 * @param data2
	 *            the second data byte for the NOTE_ON event, which contains the
	 *            attack velocity
	 * @param data3
	 *            the second data byte for the NOTE_OFF event, which contains
	 *            the decay velocity
	 * @param duration
	 *            the duration of the note
	 * @param addNoteOn
	 *            whether a ShortMessage.NOTE_ON event should be created for for
	 *            this event. For the end of a tied note, this should be false;
	 *            otherwise it should be true.
	 * @param addNoteOff
	 *            whether a ShortMessage.NOTE_OFF event should be created for
	 *            for this event. For the start of a tied note, this should be
	 *            false; otherwise it should be true.
	 */
	public void addNoteEvent(int data1, int data2, int data3, long duration,
			boolean addNoteOn, boolean addNoteOff) {
		try {
			if (addNoteOn) {
				ShortMessage message = new ShortMessage();
				message.setMessage(ShortMessage.NOTE_ON, currentTrack, data1,
						data2);
				MidiEvent event = new MidiEvent(message, getTrackTimer());
				track[currentTrack].add(event);
			}

			advanceTrackTimer(duration);

			if (addNoteOff) {
				ShortMessage message2 = new ShortMessage();
				message2.setMessage(ShortMessage.NOTE_OFF, currentTrack, data1,
						data3);
				MidiEvent event2 = new MidiEvent(message2, getTrackTimer());
				track[currentTrack].add(event2);
			}
		} catch (InvalidMidiDataException e) {
			// We've kept a good eye on the data. This exception won't happen.
			e.printStackTrace();
		}
	}

	/**
	 * Returns the current sequence, which is a collection of tracks. If your
	 * goal is to add events to the sequence, you don't want to use this method
	 * to get the sequence; instead, use the addEvent methods to add your
	 * events.
	 * 
	 * @return the current sequence
	 */
	public Sequence getSequence() {
		return sequence;
	}
}