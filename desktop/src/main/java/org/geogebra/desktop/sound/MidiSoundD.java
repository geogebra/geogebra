package org.geogebra.desktop.sound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.swing.JFileChooser;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;
import org.jfugue.Pattern;
import org.jfugue.Player;

/**
 * Class for managing and playing Midi sound.
 * 
 * @author G. Sturr 2010-9-18
 * 
 */
public class MidiSoundD implements MetaEventListener {

	private final AppD app;
	private Synthesizer synthesizer;
	private Instrument instruments[];
	private MidiChannel channels[];

	private Sequencer sequencer;
	private Sequence seq;
	private long tickPos;

	private Player player;
	// Midi meta event
	public static final int END_OF_TRACK_MESSAGE = 47;

	/***********************************************
	 * Constructor
	 */
	public MidiSoundD(App app) {
		this.app = (AppD) app;
	}

	// ==================================================
	// Initialization
	// ==================================================

	public boolean initialize() {

		boolean success = true;

		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.addMetaEventListener(this);

			if (synthesizer == null) {
				synthesizer = MidiSystem.getSynthesizer();

				Soundbank sb = synthesizer.getDefaultSoundbank();
				if (sb != null) {
					instruments = synthesizer.getDefaultSoundbank()
							.getInstruments();
					synthesizer.loadInstrument(instruments[0]);
				}

				setChannels(synthesizer.getChannels());
			}
		}

		catch (MidiUnavailableException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return success;
	}

	/**
	 * Generates a list of available instruments in String form
	 */
	public String getInstrumentNames() {

		int size = Math.min(128, instruments.length);

		StringBuilder list = new StringBuilder("{");
		for (int i = 0; i < size; i++) {
			list.append("\"" + i + ": " + instruments[i].getName() + "\"");
			if (i != (size - 1)) {
				list.append(",");
			}
		}
		list.append("}");

		return list.toString();
	}

	/**
	 * Plays a midi sequence with default tempo
	 */
	public void playSequence(Sequence sequence, long tickPosition) {
		int tempo = 120;
		playSequence(sequence, tempo, tickPosition);
	}

	/**
	 * Plays a MIDI sequence
	 * 
	 * @param sequence
	 * @param tempo
	 * @param tickPosition
	 */
	public void playSequence(Sequence sequence, int tempo, long tickPosition) {

		if (sequence == null) {
			return;
		}

		try {
			initialize();
			sequencer.open();
			synthesizer.open();

			// Specify the sequence, tempo, and tickPosition
			sequencer.setSequence(sequence);
			sequencer.setTempoInBPM(tempo);
			sequencer.setTickPosition(tickPosition);

			// Start playing
			sequencer.start();

		} catch (MidiUnavailableException e) {
			// ignore
		} catch (InvalidMidiDataException e) {
			// ignore
		}
	}

	public void pause(boolean doPause) {

		if (sequencer == null) {
			return;
		}

		if (doPause) {
			tickPos = sequencer.getTickPosition();
			closeMidiSound();
		} else {
			playSequence(seq, tickPos);
		}

	}

	public void stop() {
		closeMidiSound();
		seq = null;
	}

	public void closeMidiSound() {

		if (synthesizer != null) {
			synthesizer.close();
		}
		instruments = null;
		setChannels(null);
		if ((sequencer != null) && sequencer.isOpen()) {
			// sequencer.stop();
			sequencer.close();
		}

	}

	/**
	 * Midi meta event listener that closes the sequencer at end of track.
	 */
	@Override
	public void meta(MetaMessage event) {
		// System.out.println("midi sound event " + event.getType());
		if (event.getType() == END_OF_TRACK_MESSAGE) {
			closeMidiSound();
		}
	}

	/**
	 * Uses the Sequencer to play a single note in channel[0]
	 * 
	 */
	public void playSequenceNote(final int note, final double duration,
			final int instrument) {

		tickPos = 0;
		String str = "[" + note + "]/" + Double.toString(duration);
		this.playSequenceFromJFugueString(str, instrument);

	}

	/**
	 * Uses the sequencer to play a Midi sequence from a .mid file or a .txt
	 * file containing a JFugue string.
	 */
	/*
	 * Uses the sequencer to play a Midi sequence from a .mid file or a .txt
	 * file containing a JFugue string.
	 */
	public void playMidiFile(String filePath0) {
		String filePath = filePath0;
		try {

			if ("".equals(filePath) && app.isUsingFullGui()) {
				// launch a file chooser (just for testing)
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(app.getMainComponent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					filePath = fc.getSelectedFile().getAbsolutePath();
				}
			}

			File f = null;
			URL url = null;
			String ext = null;

			// allow: PlaySound["http://www.btc-bci.com/~rlewis/spider.mid"]
			// PlaySound["allthing.mid"]
			// PlaySound["c:\whiduck.mid"]
			// PlaySound["http://cs.uccs.edu/~cs525/midi/vivi.mid"]
			// PlaySound["@1296677"]
			// PlaySound["http://tube-beta.geogebra.org/material/download/format/file/id/1296677"]
			// PlaySound["http://tube-beta.geogebra.org/files/material-1296677.mid"]

			if (filePath.startsWith("@")) {

				String id = filePath.substring(1);

				String urlStr;

				// if (app.has(Feature.TUBE_BETA)) {
				urlStr = GeoGebraConstants.GEOGEBRA_WEBSITE_BETA;
				// } else {
				// urlStr = GeoGebraConstants.GEOGEBRATUBE_WEBSITE;
				// }

				// something like
				// http://tube-beta.geogebra.org/files/material-1296677.mid
				urlStr = urlStr + "files/material-" + id + ".mid";

				url = new URL(urlStr);
				ext = "mid";
			} else if (filePath.startsWith("http://")) {
				url = new URL(filePath);
			} else {

				f = new File(filePath);
				if (!f.exists()) {
					f = new File(
							app.getCurrentPath() + File.separator + filePath);
				}
			}

			if (ext == null) {
				ext = filePath.substring(filePath.lastIndexOf(".") + 1);
			}

			if ("txt".equals(ext)) {
				playJFugueFromFile(f, url);

			} else if ("gm".equals(ext)) {
				loadSoundBank(f, url);
			} else {
				// Load new sequence from .mid file
				tickPos = 0;

				seq = f == null ? MidiSystem.getSequence(url)
						: MidiSystem.getSequence(f);
				playSequence(seq, tickPos);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

	}

	private void loadSoundBank(File soundbankFile, URL soundbankURL) {

		try {

			synthesizer.close();
			Soundbank sb = soundbankFile == null
					? MidiSystem.getSoundbank(soundbankURL)
					: MidiSystem.getSoundbank(soundbankFile);
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();

			Log.debug("soundbank added: " + sb);

			if (sb != null) {
				Log.debug("soundbank supported: "
						+ synthesizer.isSoundbankSupported(sb));
				boolean bInstrumentsLoaded = synthesizer.loadAllInstruments(sb);
				Log.debug("Instruments loaded: " + bInstrumentsLoaded);
			}

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playSequenceFromJFugueString(String noteString0,
			int instrument) {

		initialize();
		try {
			sequencer.open();
			synthesizer.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}

		String noteString = "I[" + instrument + "] " + noteString0;
		player = new Player(sequencer);
		Pattern pattern = new Pattern(noteString);
		PlayerThread thread = new PlayerThread(player, pattern);
		thread.start();
	}

	public void playJFugueFromFile(File file, URL url) throws IOException {

		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;

		try {
			reader = file == null
					? new BufferedReader(new InputStreamReader(url.openStream(),
							Charsets.getUtf8()))
					: new BufferedReader(new InputStreamReader(
							new FileInputStream(file), Charsets.getUtf8()));
			String text = null;
			while ((text = reader.readLine()) != null) {
				contents.append(text);
			}
			// System.out.println(contents.toString());
			this.playSequenceFromJFugueString(contents.toString(), 0);
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public MidiChannel[] getChannels() {
		return channels;
	}

	private void setChannels(MidiChannel channels[]) {
		this.channels = channels;
	}

	/**********************************************************
	 * Class PlayerThread Thread extension that runs a JFugue MIDI player
	 */
	private static class PlayerThread extends Thread {

		private final Pattern pattern;
		private final Player player;

		public PlayerThread(Player player, Pattern pattern) {
			this.player = player;
			this.pattern = pattern;
		}

		@Override
		public void run() {
			player.play(pattern);
			player.close();
		}
	}

}
