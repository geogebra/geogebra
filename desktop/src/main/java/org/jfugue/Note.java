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

/**
 * Contains all information necessary for a musical note, including pitch,
 * duration, attack velocity, and decay velocity.
 *
 * <p>
 * Most of these settings have defaults. The default octave is 5. The default
 * duration is a quarter note. The default attack and decay velocities are 64.
 * </p>
 *
 * @author David Koelle
 * @version 2.0.1
 */
public final class Note implements JFugueElement {
	private byte value = 0;
	private long duration = 0;
	private double decimalDuration = 0.0;
	private boolean isStartOfTie = false;
	private boolean isEndOfTie = false;
	private byte attackVelocity = DEFAULT_VELOCITY;
	private byte decayVelocity = DEFAULT_VELOCITY;
	private boolean rest = false;
	private byte type = 0;
	private boolean accompanyingNotes = false;

	/**
	 * Instantiates a new Note object.
	 */
	public Note() {
		this.value = 0;
		this.duration = 0;
		this.type = 0;
	}

	/**
	 * Instantiates a new Note object with the given note value. This
	 * constructor should only be called in cases where the duration of the note
	 * is not important (for example, when specifying a root note for a
	 * IntervalNotation)
	 * 
	 * @param value
	 *            the numeric value of the note. C5 is 60.
	 */
	public Note(byte value) {
		this(value, 1);
	}

	/**
	 * Instantiates a new Note object with the given note value and duration.
	 * 
	 * @param value
	 *            the numeric value of the note. C5 is 60.
	 * @param duration
	 *            the duration of the note, as milliseconds.
	 */
	public Note(byte value, long duration) {
		this.value = value;
		this.duration = duration;
	}

	/**
	 * Instantiates a new Note object with the given note value and duration.
	 * 
	 * @param value
	 *            the numeric value of the note. C5 is 60.
	 * @param duration
	 *            the duration of the note, as a decimal fraction of a whole
	 *            note.
	 */
	public Note(byte value, double decimalDuration) {
		this.value = value;
		this.decimalDuration = decimalDuration;
	}

	/**
	 * Instantiates a new Note object with the given note value, duration, and
	 * attack and decay velocities.
	 * 
	 * @param value
	 *            the numeric value of the note. C5 is 60.
	 * @param duration
	 *            the duration of the note.
	 */
	public Note(byte value, long duration, byte attackVelocity,
			byte decayVelocity) {
		this.value = value;
		this.duration = duration;
		this.attackVelocity = attackVelocity;
		this.decayVelocity = decayVelocity;
	}

	/**
	 * Instantiates a new Note object with the given note value, duration, and
	 * attack and decay velocities.
	 * 
	 * @param value
	 *            the numeric value of the note. C5 is 60.
	 * @param duration
	 *            the duration of the note.
	 */
	public Note(byte value, double decimalDuration, byte attackVelocity,
			byte decayVelocity) {
		this.value = value;
		this.decimalDuration = decimalDuration;
		this.attackVelocity = attackVelocity;
		this.decayVelocity = decayVelocity;
	}

	/**
	 * Indicates whether this Note object actually represents a rest.
	 * 
	 * @param rest
	 *            indicates whether this note is rest
	 */
	public void setRest(boolean rest) {
		this.rest = rest;
	}

	/**
	 * Returns whether this Note object actually represents a rest.
	 * 
	 * @return whether this note is a rest
	 */
	public boolean isRest() {
		return this.rest;
	}

	/**
	 * Sets the numeric value of this note. C5 is 60.
	 * 
	 * @param value
	 *            the value of the note
	 */
	public void setValue(byte value) {
		this.value = value;
	}

	/**
	 * Returns the numeric value of this note. C5 is 60.
	 * 
	 * @return the value of this note
	 */
	public byte getValue() {
		return this.value;
	}

	/**
	 * Sets the duration of this note.
	 * 
	 * @param duration
	 *            the duration of this note
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * Returns the duration of this note.
	 * 
	 * @return the duration of this note
	 */
	public long getDuration() {
		return this.duration;
	}

	/**
	 * Sets the decimal fraction value for the duration.
	 * 
	 * @param number
	 *            the decimal fraction for the duration
	 */
	public void setDecimalDuration(double duration) {
		this.decimalDuration = duration;
	}

	/**
	 * Returns the decimal fraction value for the duration.
	 * 
	 * @return the decimal fraction value for the duration
	 */
	public double getDecimalDuration() {
		return this.decimalDuration;
	}

	/**
	 * Indicates whether this note has a tie to some future note.
	 * 
	 * @param tied
	 *            true if the note is tied, false if not
	 */
	public void setStartOfTie(boolean startOfTie) {
		this.isStartOfTie = startOfTie;
	}

	/**
	 * Returns whether this note has a tie to some future note.
	 * 
	 * @return true is the note is tied, false if not
	 */
	public boolean isStartOfTie() {
		return this.isStartOfTie;
	}

	/**
	 * Indicates whether this note is tied to some past note.
	 * 
	 * @param tied
	 *            true if the note is tied, false if not
	 */
	public void setEndOfTie(boolean endOfTie) {
		this.isEndOfTie = endOfTie;
	}

	/**
	 * Returns whether this note is tied to some past note.
	 * 
	 * @return true is the note is tied, false if not
	 */
	public boolean isEndOfTie() {
		return this.isEndOfTie;
	}

	/**
	 * Sets the attack velocity for this note.
	 * 
	 * @param velocity
	 *            the attack velocity
	 */
	public void setAttackVelocity(byte velocity) {
		this.attackVelocity = velocity;
	}

	/**
	 * Returns the attack velocity for this note.
	 * 
	 * @return the attack velocity
	 */
	public byte getAttackVelocity() {
		return this.attackVelocity;
	}

	/**
	 * Sets the decay velocity for this note.
	 * 
	 * @param velocity
	 *            the decay velocity
	 */
	public void setDecayVelocity(byte velocity) {
		this.decayVelocity = velocity;
	}

	/**
	 * Returns the decay velocity for this note.
	 * 
	 * @return the decay velocity
	 */
	public byte getDecayVelocity() {
		return this.decayVelocity;
	}

	/**
	 * Sets whether this Note will have other Notes (sequential or parallel)
	 * associated with it.
	 * 
	 * @param accompanying
	 */
	public void setHasAccompanyingNotes(boolean accompanying) {
		this.accompanyingNotes = accompanying;
	}

	/**
	 * Returns whether this Note will have other Notes (sequential or parallel)
	 * associated with it.
	 */
	public boolean hasAccompanyingNotes() {
		return this.accompanyingNotes;
	}

	/**
	 * Sets the note type - either First, Sequential, or Parallel.
	 * 
	 * @param type
	 *            the note type
	 */
	public void setType(byte type) {
		this.type = type;
	}

	/**
	 * Returns the note type - either First, Sequential, or Parallel.
	 * 
	 * @return the note type
	 */
	public byte getType() {
		return this.type;
	}

	/** Indicates that this note is the first note in the token. */
	public static final byte FIRST = 0;
	/**
	 * Indicates that this note immediately follows a previous note in the same
	 * token.
	 */
	public static final byte SEQUENTIAL = 1;
	/**
	 * Indicates that this note is played at the same time as a previous note in
	 * the same token.
	 */
	public static final byte PARALLEL = 2;
	/** Default value for attack and decay velocity. */
	public static final byte DEFAULT_VELOCITY = 64;

	/**
	 * Returns the Music String representing this element and all of its
	 * settings. For a Note object, the Music String is a note, expressed as
	 * either a letter, <code>note</code>, or a bracketed number,
	 * <code>[<i>note-value</i>],
	 * and a duration, expressed as either a letter, <code>duration</code>, or a
	 * slash followed by a numeric duration,
	 * <code>/<i>decimal-duration</i></code><br />
	 * If either the attack or decay velocity is set to a value besides the
	 * default, <code>a<i>velocity</i></code> and/or
	 * <code>d<i>velocity</i></code> will be added to the string. If this note
	 * is to be played in sequence or in parallel to another note, a
	 * <code>+</code> or <code>_</code> character will be added as appropriate.
	 * 
	 * @return the Music String for this element
	 */
	@Override
	public String getMusicString() {
		StringBuffer buffy = new StringBuffer();

		// If this is a Sequential note or a Parallel note, include that
		// information.
		if (SEQUENTIAL == this.type) {
			buffy.append("_");
		} else if (PARALLEL == this.type) {
			buffy.append("+");
		}

		// Add the note value and duration value
		// buffy.append("[");
		// buffy.append(this.value);
		// buffy.append("]/");
		// buffy.append(this.decimalDuration);
		buffy.append(Note.getStringForNote(this.value, this.decimalDuration));

		if (this.attackVelocity != DEFAULT_VELOCITY) {
			buffy.append("a");
			buffy.append(this.attackVelocity);
		}
		if (this.decayVelocity != DEFAULT_VELOCITY) {
			buffy.append("d");
			buffy.append(this.decayVelocity);
		}

		return buffy.toString();
	}

	/**
	 * Returns verification string in this format: Note: value={#},
	 * duration={#}, startTie={T|F}, endTie={T|F}, attack={#}, decay={#},
	 * isFirst={T|F}, isParallel={T|F}, isSequential={T|F}
	 * 
	 * @version 4.0
	 */
	@Override
	public String getVerifyString() {
		StringBuffer buffy = new StringBuffer();
		buffy.append("Note: value=");
		buffy.append(getValue());
		buffy.append(", duration=");
		buffy.append(getDecimalDuration());
		buffy.append(", startTie=");
		buffy.append(isStartOfTie() ? "T" : "F");
		buffy.append(", endTie=");
		buffy.append(isEndOfTie() ? "T" : "F");
		buffy.append(", attack=");
		buffy.append(getAttackVelocity());
		buffy.append(", decay=");
		buffy.append(getDecayVelocity());
		buffy.append(", isFirst=");
		buffy.append(getType() == FIRST ? "T" : "F");
		buffy.append(", isParallel=");
		buffy.append(getType() == PARALLEL ? "T" : "F");
		buffy.append(", isSequential=");
		buffy.append(getType() == SEQUENTIAL ? "T" : "F");

		return buffy.toString();
	}

	public static String createVerifyString(int value, double duration) {
		return createVerifyString(value, duration, false, false, (byte) 64,
				(byte) 64, true, false, false);
	}

	public static String createVerifyString(int value, double duration,
			boolean startTie, boolean endTie) {
		return createVerifyString(value, duration, startTie, endTie, (byte) 64,
				(byte) 64, true, false, false);
	}

	public static String createVerifyString(int value, double duration,
			int attack, int decay) {
		return createVerifyString(value, duration, false, false, attack, decay,
				true, false, false);
	}

	public static String createVerifyString(int value, double duration,
			boolean isFirst, boolean isParallel, boolean isSequential) {
		return createVerifyString(value, duration, false, false, (byte) 64,
				(byte) 64, isFirst, isParallel, isSequential);
	}

	public static String createVerifyString(int value, double duration,
			boolean startTie, boolean endTie, int attack, int decay,
			boolean isFirst, boolean isParallel, boolean isSequential) {
		StringBuffer buffy = new StringBuffer();
		buffy.append("Note: value=");
		buffy.append(value);
		buffy.append(", duration=");
		buffy.append(duration);
		buffy.append(", startTie=");
		buffy.append(startTie ? "T" : "F");
		buffy.append(", endTie=");
		buffy.append(endTie ? "T" : "F");
		buffy.append(", attack=");
		buffy.append(attack);
		buffy.append(", decay=");
		buffy.append(decay);
		buffy.append(", isFirst=");
		buffy.append(isFirst ? "T" : "F");
		buffy.append(", isParallel=");
		buffy.append(isParallel ? "T" : "F");
		buffy.append(", isSequential=");
		buffy.append(isSequential ? "T" : "F");

		return buffy.toString();
	}

	/**
	 * Easily create compound note verification strings, like: Note: value=60,
	 * duration=1.0, startTie=F, endTie=F, attack=64, decay=64, isFirst=T,
	 * isParallel=F, isSequential=F; Note: value=63, duration=0.0, startTie=F,
	 * endTie=F, attack=64, decay=64, isFirst=F, isParallel=T, isSequential=F
	 */
	public static String createCompoundVerifyString(String... strings) {
		StringBuffer buffy = new StringBuffer();

		for (String string : strings) {
			buffy.append(string);
			buffy.append("; ");
		}

		return buffy.toString().substring(0, buffy.toString().length() - 2);
	}

	/**
	 * Returns a MusicString representation of the given MIDI note value and
	 * duration -- which indicates a note and an octave.
	 * 
	 * @param noteValue
	 *            this MIDI note value, like 60
	 * @param decimalDuration
	 *            the duration of this note, like 0.5
	 * @return a MusicString value, like C5h
	 */
	public static String getStringForNote(byte noteValue,
			double decimalDuration) {
		StringBuffer buffy = new StringBuffer();
		buffy.append(getStringForNote(noteValue));
		buffy.append(getStringForDuration(decimalDuration));
		return buffy.toString();
	}

	/**
	 * Returns the frequency, in Hertz, for the given note value. For example,
	 * the frequency for A5 (MIDI note 69) is 440.0
	 * 
	 * @param noteValue
	 * @return
	 */
	public static double getFrequencyForNote(int noteValue) {
		double freq = 0.0;
		double freq0 = 8.1757989156;
		for (double d = 0; d <= noteValue; d++) {
			freq = freq0 * Math.pow(2.0, d / 12.0);
		}

		// Truncate to 4 significant digits
		double retVal = Math.rint(freq * 10000.0) / 10000.0;

		return retVal;
	}

	/**
	 * Returns a MusicString representation of the given MIDI note value --
	 * which indicates a note and an octave.
	 * 
	 * @param noteValue
	 *            this MIDI note value, like 60
	 * @return a MusicString value, like C5
	 */
	public static String getStringForNote(byte noteValue) {
		int note = noteValue % 12;
		int octave = noteValue / 12;
		StringBuffer buffy = new StringBuffer();
		buffy.append(NOTES[note]);
		buffy.append(octave);
		return buffy.toString();
	}

	/**
	 * Returns a MusicString representation of a decimal duration. This code
	 * currently only converts single duration values representing whole, half,
	 * quarter, eighth, etc. durations; and dotted durations associated with
	 * those durations (such as "h.", equal to 0.75). This method does not
	 * convert combined durations (for example, "hi" for 0.625) or anything
	 * greater than a duration of 1.0 (for example, "wwww" for 4.0). For these
	 * values, the original decimal duration is returned in a string, prepended
	 * with a "/" to make the returned value a valid MusicString duration
	 * indicator.
	 *
	 * @param decimalDuration
	 *            The decimal value of the duration to convert
	 * @return a MusicString fragment representing the duration
	 */
	public static String getStringForDuration(double decimalDuration) {
		if (decimalDuration == 1.0) {
			return "w";
		} else if (decimalDuration == 0.75) {
			return "h.";
		} else if (decimalDuration == 0.5) {
			return "h";
		} else if (decimalDuration == 0.375) {
			return "q.";
		} else if (decimalDuration == 0.25) {
			return "q";
		} else if (decimalDuration == 0.1875) {
			return "i.";
		} else if (decimalDuration == 0.125) {
			return "i";
		} else if (decimalDuration == 0.09375) {
			return "s.";
		} else if (decimalDuration == 0.0625) {
			return "s";
		} else if (decimalDuration == 0.046875) {
			return "t.";
		} else if (decimalDuration == 0.03125) {
			return "t";
		} else if (decimalDuration == 0.0234375) {
			return "x.";
		} else if (decimalDuration == 0.015625) {
			return "x";
		} else if (decimalDuration == 0.01171875) {
			return "o.";
		} else if (decimalDuration == 0.0078125) {
			return "o";
		} else {
			return "/" + decimalDuration;
		}
	}

	/**
	 * Returns the decimal duration that is equal to the given MusicString
	 * representation. This code currently only converts single duration values
	 * representing whole, half, quarter, eighth, etc. durations; and dotted
	 * durations associated with those durations (such as "h.", equal to 0.75).
	 * This method does not convert combined durations (for example, "hi" for
	 * 0.625) or anything greater than a duration of 1.0 (for example, "wwww"
	 * for 4.0). For these values, the original decimal duration is returned in
	 * a string, prepended with a "/" to make the returned value a valid
	 * MusicString duration indicator.
	 *
	 * @param stringDuration
	 *            The MusicString duration character (or dotted character)
	 * @return a decimal value representing the duration, expressed as a
	 *         fraction of a whole note
	 */
	public static double getDecimalForDuration(String stringDuration) {
		String stringDuration2 = stringDuration.toLowerCase();
		if (stringDuration2.equals("w")) {
			return 1.0;
		} else if (stringDuration2.equals("h.")) {
			return 0.75;
		} else if (stringDuration2.equals("h")) {
			return 0.5;
		} else if (stringDuration2.equals("q.")) {
			return 0.375;
		} else if (stringDuration2.equals("q")) {
			return 0.25;
		} else if (stringDuration2.equals("i.")) {
			return 0.1875;
		} else if (stringDuration2.equals("i")) {
			return 0.125;
		} else if (stringDuration2.equals("s.")) {
			return 0.09375;
		} else if (stringDuration2.equals("s")) {
			return 0.0625;
		} else if (stringDuration2.equals("t.")) {
			return 0.046875;
		} else if (stringDuration2.equals("t")) {
			return 0.03125;
		} else if (stringDuration2.equals("x.")) {
			return 0.0234375;
		} else if (stringDuration2.equals("x")) {
			return 0.015625;
		} else if (stringDuration2.equals("o.")) {
			return 0.01171875;
		} else if (stringDuration2.equals("o")) {
			return 0.0078125;
		} else {
			return 0.0;
		}
	}

	public static final String[] NOTES = new String[] { "C", "C#", "D", "Eb",
			"E", "F", "F#", "G", "G#", "A", "Bb", "B" };

	public static final byte ACOUSTIC_BASS_DRUM = 35;
	public static final byte BASS_DRUM = 36;
	public static final byte SIDE_STICK = 37;
	public static final byte ACOUSTIC_SNARE = 38;
	public static final byte HAND_CLAP = 39;

	public static final byte ELECTRIC_SNARE = 40;
	public static final byte LOW_FLOOR_TOM = 41;
	public static final byte CLOSED_HI_HAT = 42;
	public static final byte HIGH_FLOOR_TOM = 43;
	public static final byte PEDAL_HI_HAT = 44;
	public static final byte LOW_TOM = 45;
	public static final byte OPEN_HI_HAT = 46;
	public static final byte LOW_MID_TOM = 47;
	public static final byte HI_MID_TOM = 48;
	public static final byte CRASH_CYMBAL_1 = 49;

	public static final byte HIGH_TOM = 50;
	public static final byte RIDE_CYMBAL_1 = 51;
	public static final byte CHINESE_CYMBAL = 52;
	public static final byte RIDE_BELL = 53;
	public static final byte TAMBOURINE = 54;
	public static final byte SPLASH_CYMBAL = 55;
	public static final byte COWBELL = 56;
	public static final byte CRASH_CYMBAL_2 = 57;
	public static final byte VIBRASLAP = 58;
	public static final byte RIDE_CYMBAL_2 = 59;

	public static final byte HI_BONGO = 60;
	public static final byte LOW_BONGO = 61;
	public static final byte MUTE_HI_CONGA = 62;
	public static final byte OPEN_HI_CONGA = 63;
	public static final byte LOW_CONGA = 64;
	public static final byte HIGH_TIMBALE = 65;
	public static final byte LOW_TIMBALE = 66;
	public static final byte HIGH_AGOGO = 67;
	public static final byte LOW_AGOGO = 68;
	public static final byte CABASA = 69;

	public static final byte MARACAS = 70;
	public static final byte SHORT_WHISTLE = 71;
	public static final byte LONG_WHISTLE = 72;
	public static final byte SHORT_GUIRO = 73;
	public static final byte LONG_GUIRO = 74;
	public static final byte CLAVES = 75;
	public static final byte HI_WOOD_BLOCK = 76;
	public static final byte LOW_WOOD_BLOCK = 77;
	public static final byte MUTE_CUICA = 78;
	public static final byte OPEN_CUICA = 79;

	public static final byte MUTE_TRIANGLE = 80;
	public static final byte OPEN_TRIANGLE = 81;

}

/*
 * 
 * Here's a handy chart from http://www.borg.com/~jglatt/tutr/notefreq.htm
 * 
 * MIDI MIDI MIDI Note Frequency Note Frequency Note Frequency C 0 8.1757989156
 * 12 16.3515978313 24 32.7031956626 Db 1 8.6619572180 13 17.3239144361 25
 * 34.6478288721 D 2 9.1770239974 14 18.3540479948 26 36.7080959897 Eb 3
 * 9.7227182413 15 19.4454364826 27 38.8908729653 E 4 10.3008611535 16
 * 20.6017223071 28 41.2034446141 F 5 10.9133822323 17 21.8267644646 29
 * 43.6535289291 Gb 6 11.5623257097 18 23.1246514195 30 46.2493028390 G 7
 * 12.2498573744 19 24.4997147489 31 48.9994294977 Ab 8 12.9782717994 20
 * 25.9565435987 32 51.9130871975 A 9 13.7500000000 21 27.5000000000 33
 * 55.0000000000 Bb 10 14.5676175474 22 29.1352350949 34 58.2704701898 B 11
 * 15.4338531643 23 30.8677063285 35 61.7354126570
 * 
 * C 36 65.4063913251 48 130.8127826503 60 261.6255653006 Db 37 69.2956577442 49
 * 138.5913154884 61 277.1826309769 D 38 73.4161919794 50 146.8323839587 62
 * 293.6647679174 Eb 39 77.7817459305 51 155.5634918610 63 311.1269837221 E 40
 * 82.4068892282 52 164.8137784564 64 329.6275569129 F 41 87.3070578583 53
 * 174.6141157165 65 349.2282314330 Gb 42 92.4986056779 54 184.9972113558 66
 * 369.9944227116 G 43 97.9988589954 55 195.9977179909 67 391.9954359817 Ab 44
 * 103.8261743950 56 207.6523487900 68 415.3046975799 A 45 110.0000000000 57
 * 220.0000000000 69 440.0000000000 Bb 46 116.5409403795 58 233.0818807590 70
 * 466.1637615181 B 47 123.4708253140 59 246.9416506281 71 493.8833012561
 * 
 * C 72 523.2511306012 84 1046.5022612024 96 2093.0045224048 Db 73
 * 554.3652619537 85 1108.7305239075 97 2217.4610478150 D 74 587.3295358348 86
 * 1174.6590716696 98 2349.3181433393 Eb 75 622.2539674442 87 1244.5079348883 99
 * 2489.0158697766 E 76 659.2551138257 88 1318.5102276515 100 2637.0204553030 F
 * 77 698.4564628660 89 1396.9129257320 101 2793.8258514640 Gb 78 739.9888454233
 * 90 1479.9776908465 102 2959.9553816931 G 79 783.9908719635 91 1567.9817439270
 * 103 3135.9634878540 Ab 80 830.6093951599 92 1661.2187903198 104
 * 3322.4375806396 A 81 880.0000000000 93 1760.0000000000 105 3520.0000000000 Bb
 * 82 932.3275230362 94 1864.6550460724 106 3729.3100921447 B 83 987.7666025122
 * 95 1975.5332050245 107 3951.0664100490
 * 
 * C 108 4186.0090448096 120 8372.0180896192 Db 109 4434.9220956300 121
 * 8869.8441912599 D 110 4698.6362866785 122 9397.2725733570 Eb 111
 * 4978.0317395533 123 9956.0634791066 E 112 5274.0409106059 124
 * 10548.0818212118 F 113 5587.6517029281 125 11175.3034058561 Gb 114
 * 5919.9107633862 126 11839.8215267723 G 115 6271.9269757080 127
 * 12543.8539514160 Ab 116 6644.8751612791 A 117 7040.0000000000 Bb 118
 * 7458.6201842894 B 119 7902.1328200980
 * 
 * Note: Middle C is note #60. Frequency is in Hertz.
 * 
 * 
 */