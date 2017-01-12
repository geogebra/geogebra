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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

/**
 * Prepares a pattern to be turned into music by the Renderer. This class also
 * handles saving the sequence derived from a pattern as a MIDI file.
 *
 * @see MidiRenderer
 * @see Pattern
 * @author David Koelle
 * @version 2.0
 */
public class Player {
	private Sequencer sequencer;
	private MusicStringParser parser;
	private MidiRenderer renderer;
	private float sequenceTiming = Sequence.PPQ;
	private int resolution = 120;
	private boolean paused = false;
	private boolean started = false;
	private boolean finished = false;

	/**
	 * Instantiates a new Player object, which is used for playing music.
	 */
	public Player() {
		this(true);
	}

	/**
	 * Instantiates a new Player object, which is used for playing music. The
	 * <code>connected</code> parameter is passed directly to
	 * MidiSystem.getSequencer. Pass false when you do not want to copy a live
	 * synthesizer - for example, if your Player is on a server, and you don't
	 * want to create new synthesizers every time the constructor is called.
	 */
	public Player(boolean connected) {
		try {
			// Get default sequencer.
			setSequencer(MidiSystem.getSequencer(connected)); // use non
																// connected
																// sequencer so
																// no copy of
																// live
																// synthesizer
																// will be
																// created.
		} catch (MidiUnavailableException e) {
			throw new JFugueException(
					JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED_WITH_EXCEPTION
							+ e.getMessage());
		}
		initParser();
	}

	/**
	 * Creates a new Player instance using a Sequencer that you have provided.
	 * 
	 * @param sequencer
	 *            The Sequencer to send the MIDI events
	 */
	public Player(Sequencer sequencer) {
		setSequencer(sequencer);
		initParser();
	}

	/**
	 * Creates a new Player instance using a Sequencer obtained from the
	 * Synthesizer that you have provided.
	 * 
	 * @param synth
	 *            The Synthesizer you want to use for this Player.
	 */
	public Player(Synthesizer synth) throws MidiUnavailableException {
		this(Player.getSequencerConnectedToSynthesizer(synth));
	}

	private void initParser() {
		this.parser = new MusicStringParser();
		this.renderer = new MidiRenderer(sequenceTiming, resolution);
		this.parser.addParserListener(this.renderer);
	}

	private void initSequencer() {
		// Close the sequencer and synthesizer
		getSequencer().addMetaEventListener(new MetaEventListener() {
			@Override
			public void meta(MetaMessage event) {
				if (event.getType() == 47) {
					close();
				}
			}
		});
	}

	private void openSequencer() {
		if (getSequencer() == null) {
			throw new JFugueException(
					JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED);
		}

		// Open the sequencer, if it is not already open
		if (!getSequencer().isOpen()) {
			try {
				getSequencer().open();
			} catch (MidiUnavailableException e) {
				throw new JFugueException(
						JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED_WITH_EXCEPTION
								+ e.getMessage());
			}
		}
	}

	/**
	 * Plays a pattern by setting up a Renderer and feeding the pattern to it.
	 * 
	 * @param pattern
	 *            the pattern to play
	 * @see MidiRenderer
	 */
	public void play(Pattern pattern) {
		Sequence sequence = getSequence(pattern);
		play(sequence);
	}

	/**
	 * Plays a pattern by setting up a Renderer and feeding the pattern to it.
	 * 
	 * @param pattern
	 *            the pattern to play
	 * @see MidiRenderer
	 */
	public void play(Rhythm rhythm) {
		Pattern pattern = rhythm.getPattern();
		Sequence sequence = getSequence(pattern);
		play(sequence);
	}

	/**
	 * Plays a MIDI Sequence
	 * 
	 * @param sequence
	 *            the Sequence to play
	 * @throws JFugueException
	 *             if there is a problem playing the music
	 * @see MidiRenderer
	 */
	private void play(Sequence sequence) {
		// Open the sequencer
		openSequencer();

		// Set the sequence
		try {
			getSequencer().setSequence(sequence);
		} catch (Exception e) {
			throw new JFugueException(
					JFugueException.ERROR_PLAYING_MUSIC + e.getMessage());
		}

		setStarted(true);

		// Start the sequence
		getSequencer().start();

		// Wait for the sequence to finish
		while (isPlaying() || isPaused()) {
			try {
				Thread.sleep(20); // don't hog all of the CPU
			} catch (InterruptedException e) {
				throw new JFugueException(JFugueException.ERROR_SLEEP);
			}
		}

		// Close the sequencer
		getSequencer().close();

		setStarted(false);
		setFinished(true);
	}

	/**
	 * Plays a string of music. Be sure to call player.close() after play() has
	 * returned.
	 * 
	 * @param musicString
	 *            the MusicString (JFugue-formatted string) to play
	 * @version 3.0
	 */
	public void play(String musicString) {
		if (musicString.indexOf(".mid") > 0) {
			// If the user tried to call this method with "filename.mid" or
			// "filename.midi", throw the following exception
			throw new JFugueException(
					JFugueException.PLAYS_STRING_NOT_FILE_EXC);
		}

		Pattern pattern = new Pattern(musicString);
		play(pattern);
	}

	/**
	 * Plays a MIDI file, without doing any conversions to MusicStrings. Be sure
	 * to call player.close() after play() has returned.
	 * 
	 * @param file
	 *            the MIDI file to play
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 * @version 3.0
	 */
	public void playMidiDirectly(File file)
			throws IOException, InvalidMidiDataException {
		Sequence sequence = MidiSystem.getSequence(file);
		play(sequence);
	}

	/**
	 * Plays a URL that contains a MIDI sequence. Be sure to call player.close()
	 * after play() has returned.
	 * 
	 * @param url
	 *            the URL to play
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 * @version 3.0
	 */
	public void playMidiDirectly(URL url)
			throws IOException, InvalidMidiDataException {
		Sequence sequence = MidiSystem.getSequence(url);
		play(sequence);
	}

	public void play(Anticipator anticipator, Pattern pattern, long offset) {
		Sequence sequence = getSequence(pattern);
		Sequence sequence2 = getSequence(pattern);
		play(anticipator, sequence, sequence2, offset);
	}

	public void play(Anticipator anticipator, Sequence sequence,
			Sequence sequence2, long offset) {
		anticipator.play(sequence);

		if (offset > 0) {
			try {
				Thread.sleep(offset);
			} catch (InterruptedException e) {
				throw new JFugueException(JFugueException.ERROR_SLEEP);
			}
		}

		play(sequence2);
	}

	/**
	 * Closes MIDI resources - be sure to call this after play() has returned.
	 */
	public void close() {
		getSequencer().close();
		try {
			if (MidiSystem.getSynthesizer() != null) {
				MidiSystem.getSynthesizer().close();
			}
		} catch (MidiUnavailableException e) {
			throw new JFugueException(
					JFugueException.GENERAL_ERROR + e.getMessage());
		}
	}

	private void setStarted(boolean started) {
		this.started = started;
	}

	private void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isStarted() {
		return this.started;
	}

	public boolean isFinished() {
		return this.finished;
	}

	public boolean isPlaying() {
		return getSequencer().isRunning();
	}

	public boolean isPaused() {
		return paused;
	}

	public void pause() {
		paused = true;
		if (isPlaying()) {
			getSequencer().stop();
		}
	}

	public void resume() {
		paused = false;
		getSequencer().start();
	}

	public void stop() {
		paused = false;
		getSequencer().stop();
		getSequencer().setMicrosecondPosition(0);
	}

	public void jumpTo(long microseconds) {
		getSequencer().setMicrosecondPosition(microseconds);
	}

	public long getSequenceLength(Sequence sequence) {
		return sequence.getMicrosecondLength();
	}

	public long getSequencePosition() {
		return getSequencer().getMicrosecondPosition();
	}

	/**
	 * Saves the MIDI data from a pattern into a file.
	 * 
	 * @param pattern
	 *            the pattern to save
	 * @param file
	 *            the File to save the pattern to. Should include file
	 *            extension, such as .mid
	 */
	public void saveMidi(Pattern pattern, File file) throws IOException {
		Sequence sequence = getSequence(pattern);

		int[] writers = MidiSystem.getMidiFileTypes(sequence);
		if (writers.length == 0) {
			return;
		}

		MidiSystem.write(sequence, writers[0], file);
	}

	/**
	 * Saves the MIDI data from a MusicString into a file.
	 * 
	 * @param musicString
	 *            the MusicString to save
	 * @param file
	 *            the File to save the MusicString to. Should include file
	 *            extension, such as .mid
	 */
	public void saveMidi(String musicString, File file) throws IOException {
		Pattern pattern = new Pattern(musicString);
		saveMidi(pattern, file);
	}

	/**
	 * Parses a MIDI file and returns a Pattern. This is an excellent example of
	 * JFugue's Parser-Renderer architecture:
	 *
	 * <pre>
	 * MidiParser parser = new MidiParser();
	 * MusicStringRenderer renderer = new MusicStringRenderer();
	 * parser.addParserListener(renderer);
	 * parser.parse(sequence);
	 * </pre>
	 *
	 * @param filename
	 *            The name of the MIDI file
	 * @return a Pattern containing the MusicString representing the MIDI music
	 * @throws IOException
	 *             If there is a problem opening the MIDI file
	 * @throws InvalidMidiDataException
	 *             If there is a problem obtaining MIDI resources
	 */
	public Pattern loadMidi(File file)
			throws IOException, InvalidMidiDataException {
		MidiFileFormat format = MidiSystem.getMidiFileFormat(file);
		this.sequenceTiming = format.getDivisionType();
		this.resolution = format.getResolution();
		return Pattern.loadMidi(file);
	}

	public static void allNotesOff() {
		try {
			allNotesOff(MidiSystem.getSynthesizer());
		} catch (MidiUnavailableException e) {
			throw new JFugueException(JFugueException.GENERAL_ERROR);
		}
	}

	/**
	 * Stops all notes from playing on all MIDI channels.
	 */
	public static void allNotesOff(Synthesizer synth) {
		try {
			if (!synth.isOpen()) {
				synth.open();
			}
			MidiChannel[] channels = synth.getChannels();
			for (int i = 0; i < channels.length; i++) {
				channels[i].allNotesOff();
			}
		} catch (MidiUnavailableException e) {
			throw new JFugueException(JFugueException.GENERAL_ERROR);
		}
	}

	/**
	 * Returns the sequencer containing the MIDI data from a pattern that has
	 * been parsed.
	 * 
	 * @return the Sequencer from the pattern that was recently parsed
	 */
	public Sequencer getSequencer() {
		return this.sequencer;
	}

	private void setSequencer(Sequencer sequencer) {
		this.sequencer = sequencer;
		initSequencer();
	}

	/**
	 * Returns the sequence containing the MIDI data from the given pattern.
	 * 
	 * @return the Sequence from the given pattern
	 */
	public Sequence getSequence(Pattern pattern) {
		this.renderer.reset();
		this.parser.parse(pattern);
		Sequence sequence = this.renderer.getSequence();
		return sequence;
	}

	/**
	 * Returns an instance of a Sequencer that uses the provided Synthesizer as
	 * its receiver. This is useful when you have made changes to a specific
	 * Synthesizer--for example, you've loaded in new patches--that you want the
	 * Sequencer to use. You can then pass the Sequencer to the Player
	 * constructor.
	 *
	 * @param synth
	 *            The Synthesizer to use as the receiver for the returned
	 *            Sequencer
	 * @return a Sequencer with the provided Synthesizer as its receiver
	 * @throws MidiUnavailableException
	 * @version 4.0
	 */
	public static Sequencer getSequencerConnectedToSynthesizer(
			Synthesizer synth) throws MidiUnavailableException {
		Sequencer sequencer = MidiSystem.getSequencer(false); // Get Sequencer
																// which is not
																// connected to
																// new
																// Synthesizer.
		sequencer.open();
		if (!synth.isOpen()) {
			synth.open();
		}
		sequencer.getTransmitter().setReceiver(synth.getReceiver()); // Connect
																		// the
																		// Synthesizer
																		// to
																		// our
																		// synthesizer
																		// instance.
		return sequencer;
	}
}