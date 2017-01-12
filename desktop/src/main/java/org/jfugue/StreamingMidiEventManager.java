/*
 * JFugue - API for Music Programming
 * Copyright (C) 2003-2007  David Koelle
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

/**
 * Assists the StreamingMidiRenderer in converting Patterns to MIDI.
 *
 * @see StreamingPlayer
 * @author David Koelle
 * @version 3.2
 */
final public class StreamingMidiEventManager {
	private final int CHANNELS = 16;
	private final int LAYERS = 16;
	private byte currentTrack = 0;
	private byte[] currentLayer = new byte[CHANNELS];
	private long time[][] = new long[CHANNELS][LAYERS];
	private MidiChannel channels[] = new MidiChannel[CHANNELS];
	private Map<Long, List<NoteOffTimerEvent>> timerMap;
	private long currentTime;
	private boolean isActive;

	public StreamingMidiEventManager() {
		timerMap = new HashMap<Long, List<NoteOffTimerEvent>>();
		isActive = true;
		currentTime = System.currentTimeMillis();

		Thread timerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isActive) {
					long checkTime = System.currentTimeMillis();
					if (checkTime != currentTime) {
						long tempBackTime = currentTime;
						currentTime = System.currentTimeMillis(); // Do this
																	// again to
																	// get the
																	// most
																	// up-to-date
																	// time

						// Get any TimerEvents that may have happened in the
						// intervening time, and execute them
						for (long time = tempBackTime; time < currentTime; time++) {
							List<NoteOffTimerEvent> timerEvents = timerMap
									.get(time);
							if (null != timerEvents) {
								for (NoteOffTimerEvent event : timerEvents) {
									channels[event.track].noteOff(
											event.noteValue,
											event.decayVelocity);
								}
							}
							timerMap.put(time, null);
						}
					}

					try {
						Thread.sleep(20); // Don't hog the CPU
					} catch (InterruptedException e) {
						throw new JFugueException(JFugueException.ERROR_SLEEP);
					}
				}
			}
		});
		timerThread.start();

		try {
			Synthesizer synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			channels = synthesizer.getChannels();
		} catch (MidiUnavailableException e) {
			throw new JFugueException(JFugueException.ERROR_PLAYING_MUSIC);
		}

		for (int i = 0; i < CHANNELS; i++) {
			for (int u = 0; u < LAYERS; u++) {
				time[i][u] = 0;
			}
			currentLayer[i] = 0;
		}
		currentTrack = 0;
	}

	public void close() {
		isActive = false;
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
		// NOP
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
		addEvent(command, data1, 0);
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
		switch (command) {
		case ShortMessage.PROGRAM_CHANGE:
			channels[currentTrack].programChange(data1);
			break;
		case ShortMessage.CONTROL_CHANGE:
			channels[currentTrack].controlChange(data1, data2);
			break;
		case ShortMessage.CHANNEL_PRESSURE:
			channels[currentTrack].setChannelPressure(data1);
			break;
		case ShortMessage.POLY_PRESSURE:
			channels[currentTrack].setPolyPressure(data1, data2);
			break;
		case ShortMessage.PITCH_BEND:
			channels[currentTrack].setPitchBend(data1);
			break;
		default:
			break;
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
	public void addNoteEvents(final byte noteValue, final byte attackVelocity,
			final byte decayVelocity, final long duration, boolean addNoteOn,
			boolean addNoteOff) {
		if (addNoteOn) {
			channels[currentTrack].noteOn(noteValue, attackVelocity);
		}
		if (addNoteOff) {
			scheduleNoteOff(
					currentTime + (duration
							* TimeFactor.QUARTER_DURATIONS_IN_WHOLE),
					currentTrack, noteValue, decayVelocity);
		}
	}

	private void scheduleNoteOff(long when, byte track, byte noteValue,
			byte theWaxTadpole) {
		List<NoteOffTimerEvent> timerEvents = timerMap.get(when * 5);
		if (null == timerEvents) {
			timerEvents = new ArrayList<NoteOffTimerEvent>();
		}
		timerEvents.add(new NoteOffTimerEvent(track, noteValue, theWaxTadpole));
		timerMap.put(when, timerEvents);
	}

	class NoteOffTimerEvent {
		public byte track;
		public byte noteValue;
		public byte decayVelocity;

		public NoteOffTimerEvent(byte track, byte noteValue,
				byte decayVelocity) {
			this.track = track;
			this.noteValue = noteValue;
			this.decayVelocity = decayVelocity;
		}
	}
}
