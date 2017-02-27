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

import java.util.HashMap;
import java.util.Map;

/**
 * Parses music strings, and fires events for <code>ParserListener</code>
 * interfaces when tokens are interpreted. The <code>ParserListener</code> does
 * intelligent things with the resulting events, such as create music, draw
 * sheet music, or transform the data.
 *
 * As of Version 3.0, the Parser supports turning MIDI Sequences into JFugue
 * Patterns with the parse(Sequence) method. In this case, the ParserListeners
 * established by a ParserBuilder use the parsed events to construct the Pattern
 * string.
 *
 * @author David Koelle
 * @version 3.0
 * @version 4.0 - Note parsing split up into many separate methods; verification
 *          added for testing purposes
 */
public final class MusicStringParser extends Parser {
	private Map<String, Object> dictionaryMap;
	private byte keySig = 0;

	/**
	 * Creates a new Parser object, and populates the dictionary with initial
	 * entries.
	 * 
	 * @see JFugueDefinitions
	 */
	public MusicStringParser() {
		dictionaryMap = new HashMap<String, Object>();
		JFugueDefinitions.populateDictionary(dictionaryMap);
	}

	/**
	 * Parses a <code>Pattern</code> and fires events to subscribed
	 * <code>ParserListener</code> interfaces. As the Pattern is parsed, events
	 * are sent to <code>ParserLisener</code> interfaces, which are responsible
	 * for doing something interesting with the music data, such as playing the
	 * music, displaying it as sheet music, or transforming the pattern.
	 *
	 * <p>
	 * The parser breaks a music string into tokens, which are separated by
	 * spaces. It then determines the type of command based on the first
	 * character of the token. If the parser does not recognize the first
	 * character of the token, which is limited to the command letters (K, V, T,
	 * I, L, X, #, $, @, &, +, *, |), the notes (A, B, C, D, E, F, G, R), and
	 * the open-bracket character ( [ ), then the token will be ignored.
	 * </p>
	 *
	 * @param pattern
	 *            the <code>Pattern</code> to parse
	 * @throws Exception
	 *             if there is an error parsing the pattern
	 */
	public void parse(Pattern pattern) throws JFugueException {
		String[] tokens = pattern.getTokens();

		// If the user hasn't specified a tempo as the first token, use the
		// default of 120
		if (tokens.length > 0) {
			if (tokens[0].toUpperCase().charAt(0) != 'T') {
				parseTempoElement("T120");
			}
		}

		int counter = 0;
		for (int t = 0; t < tokens.length; t++) {
			parseToken(tokens[t]);
			counter++;
			fireProgressReported("Parsing music string...", counter,
					tokens.length);
		}
	}

	/**
	 * This method takes a single token, and distributes it to a specific
	 * element parser based on the first character in the string. If the parser
	 * does not recognize the first character of the string, the token will be
	 * ignored.
	 *
	 * @param s
	 *            the single token to parse
	 * @throws JFugueException
	 *             if there is a problem parsing the string
	 */
	private void parseToken(String s) throws JFugueException {
		// If there are any spaces, get out
		if (s.indexOf(" ") != -1) {
			throw new JFugueException(JFugueException.PARSER_SPACES_EXC, s, s);
		}

		s = s.toUpperCase();
		trace("--------Processing Token: ", s);

		switch (s.charAt(0)) {
		case 'V':
			parseVoiceElement(s);
			break;
		case 'T':
			parseTempoElement(s);
			break;
		case 'I':
			parseInstrumentElement(s);
			break;
		case 'L':
			parseLayerElement(s);
			break; // New in 3.0
		case 'K':
			parseKeySignatureElement(s);
			break; // New in 3.0
		case 'X':
			parseControllerElement(s);
			break; // New in 2.0
		case '@':
			parseTimeElement(s);
			break; // New in 3.0
		case '*':
			parsePolyPressureElement(s);
			break; // New in 3.0, also known as Key Pressure
		case '+':
			parseChannelPressureElement(s);
			break; // New in 3.0
		case '&':
			parsePitchBendElement(s);
			break; // New in 3.0
		case '|':
			parseMeasureElement(s);
			break; // New in 3.0
		case '$':
			parseDictionaryElement(s);
			break; // New in 2.0
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'R':
		case '[':
			parseNoteElement(s);
			break;
		default:
			break; // Unknown characters are okay
		}
	}

	/**
	 * Parses a voice element.
	 * 
	 * @param s
	 *            the token that contains a voice element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseVoiceElement(String s) throws JFugueException {
		String voiceNumberString = s.substring(1, s.length());
		byte voiceNumber = getByteFromDictionary(voiceNumberString);
		if (voiceNumber > 15) {
			// throw new
			// JFugueException(JFugueException.VOICE_EXC,voiceNumberString,s);
			return;
		}
		trace("Voice element: voice = ", voiceNumber);
		fireVoiceEvent(new Voice(voiceNumber));
	}

	/**
	 * Parses a tempo element. As of JFugue 4.0, Tempo can be specified in Beats
	 * Per Minute, which is much more intuitive than the original Milliseconds
	 * Per Quarter Note. To maintain compatibility with existing JFugue Music
	 * Strings, those wishing to specify Tempo using BPM need to use the full
	 * word "Tempo" in their music string, instead of just the initial "T". To
	 * summarize: "Tempo120" (or "Tempo[Allegro]") --> Tempo will be is 120
	 * beats per minute "T120" --> Tempo will be 120 milliseconds per beat.
	 * Divide into 60000000 to get BPM.
	 * 
	 * @param s
	 *            the token that contains a tempo element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseTempoElement(String s) throws JFugueException {
		String tempoNumberString = s.substring(1, s.length());
		int tempoNumber = getIntFromDictionary(tempoNumberString);
		trace("Tempo element: tempo = ", tempoNumber);
		fireTempoEvent(new Tempo(tempoNumber));
	}

	/**
	 * Parses an instrument element.
	 * 
	 * @param s
	 *            the token that contains an instrument element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseInstrumentElement(String s) throws JFugueException {
		String instrumentNumberString = s.substring(1, s.length());
		byte instrumentNumber = getByteFromDictionary(instrumentNumberString);
		trace("Instrument element: instrument = ", instrumentNumber);
		fireInstrumentEvent(new Instrument(instrumentNumber));
	}

	/**
	 * Parses a layer element.
	 * 
	 * @param s
	 *            the token that contains a layer element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseLayerElement(String s) throws JFugueException {
		String layerNumberString = s.substring(1, s.length());
		byte layerNumber = getByteFromDictionary(layerNumberString);
		trace("Layer element: layer = ", layerNumber);
		fireLayerEvent(new Layer(layerNumber));
	}

	/**
	 * Parses a time element.
	 * 
	 * @param s
	 *            the token that contains a time element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseTimeElement(String s) throws JFugueException {
		String timeNumberString = s.substring(1, s.length());
		long timeNumber = getLongFromDictionary(timeNumberString);
		trace("Time element: time = ", timeNumber);
		fireTimeEvent(new Time(timeNumber));
	}

	/**
	 * Parses a key signature element.
	 * 
	 * @param s
	 *            the token that contains a key signature
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseKeySignatureElement(String s) throws JFugueException {
		String rootNote = null;
		String majOrMin = null;
		if (s.length() == 5) {
			rootNote = s.substring(1, 5);
			majOrMin = s.substring(2, 5);
		} else {
			rootNote = s.substring(1, 6);
			majOrMin = s.substring(3, 6);
		}
		trace("Key signature element: root=", rootNote, " majOrMin=", majOrMin);
		if (!(majOrMin.equalsIgnoreCase("MAJ")
				|| (majOrMin.equalsIgnoreCase("MIN")))) {
			throw new JFugueException(JFugueException.KEYSIG_EXC, majOrMin, s);
		}
		int scale = (majOrMin.equalsIgnoreCase("MAJ") ? 0 : 1);
		int keySig = 0;

		if (rootNote.equalsIgnoreCase("CBMAJ")
				|| rootNote.equalsIgnoreCase("ABMIN")) {
			keySig = -7;
		} else if (rootNote.equalsIgnoreCase("GBMAJ")
				|| rootNote.equalsIgnoreCase("EBMIN")) {
			keySig = -6;
		} else if (rootNote.equalsIgnoreCase("DBMAJ")
				|| rootNote.equalsIgnoreCase("BBMIN")) {
			keySig = -5;
		} else if (rootNote.equalsIgnoreCase("ABMAJ")
				|| rootNote.equalsIgnoreCase("FMIN")) {
			keySig = -4;
		} else if (rootNote.equalsIgnoreCase("EBMAJ")
				|| rootNote.equalsIgnoreCase("CMIN")) {
			keySig = -3;
		} else if (rootNote.equalsIgnoreCase("BBMAJ")
				|| rootNote.equalsIgnoreCase("GMIN")) {
			keySig = -2;
		} else if (rootNote.equalsIgnoreCase("FMAJ")
				|| rootNote.equalsIgnoreCase("DMIN")) {
			keySig = -1;
		} else if (rootNote.equalsIgnoreCase("CMAJ")
				|| rootNote.equalsIgnoreCase("AMIN")) {
			keySig = 0;
		} else if (rootNote.equalsIgnoreCase("GMAJ")
				|| rootNote.equalsIgnoreCase("EMIN")) {
			keySig = +1;
		} else if (rootNote.equalsIgnoreCase("DMAJ")
				|| rootNote.equalsIgnoreCase("BMIN")) {
			keySig = +2;
		} else if (rootNote.equalsIgnoreCase("AMAJ")
				|| rootNote.equalsIgnoreCase("F#MIN")) {
			keySig = +3;
		} else if (rootNote.equalsIgnoreCase("EMAJ")
				|| rootNote.equalsIgnoreCase("C#MIN")) {
			keySig = +4;
		} else if (rootNote.equalsIgnoreCase("BMAJ")
				|| rootNote.equalsIgnoreCase("G#MIN")) {
			keySig = +5;
		} else if (rootNote.equalsIgnoreCase("F#MAJ")
				|| rootNote.equalsIgnoreCase("D#MIN")) {
			keySig = +6;
		} else if (rootNote.equalsIgnoreCase("C#MAJ")
				|| rootNote.equalsIgnoreCase("A#MIN")) {
			keySig = +7;
		} else {
			throw new JFugueException(JFugueException.KEYSIG_EXC, s);
		}
		trace("Key signature: sig=", keySig, " scale=", scale);
		fireKeySignatureEvent(new KeySignature((byte) keySig, (byte) scale));
		this.keySig = (byte) keySig;
	}

	/**
	 * Parses a measure element.
	 * 
	 * @param s
	 *            the token that contains a measure element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseMeasureElement(String s) throws JFugueException {
		trace("Measure element.");
		fireMeasureEvent(new Measure());
	}

	/**
	 * Parses a controller element.
	 * 
	 * @param s
	 *            the token that contains a controller element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseControllerElement(String s) throws JFugueException {
		int indexOfEquals = s.indexOf("=");
		if (-1 == indexOfEquals) {
			throw new JFugueException(JFugueException.CONTROL_FORMAT_EXC, s, s);
		}

		//
		// Get the Control Index from this token. The Control Index can be one
		// of two things:
		// 1. A byte. In this case, simply use the controller event referred to
		// by that byte.
		// 2. An int. In this case, the coarse adjuster is the high bits (div),
		// and the fine adjuster is the low bits (mod).
		//
		String controlIndexString = s.substring(1, indexOfEquals);
		byte controlIndex = 0;
		int controlIndexInt = -1;
		try {
			controlIndex = getByteFromDictionary(controlIndexString);
		} catch (JFugueException e) {
			controlIndexInt = getIntFromDictionary(controlIndexString);
		}

		String controlValueString = s.substring(indexOfEquals + 1, s.length());

		// An int was found as the Contoller Index number. Therefore, assume
		// that the value passed to this Index is also an int, and should be
		// divided among multiple controllers
		if (-1 != controlIndexInt) {
			int controlValue = getIntFromDictionary(controlValueString);
			byte coarseIndex = (byte) (controlIndexInt / 128);
			byte fineIndex = (byte) (controlIndexInt % 128);

			// Special case for BANK_SELECT, which has a high byte of 0
			if (16383 == controlValue) {
				coarseIndex = 0;
				fineIndex = 32;
			}

			byte coarseValue = (byte) (controlValue / 128);
			byte fineValue = (byte) (controlValue % 128);
			trace("Combined controller element: coarse-index = ", coarseIndex,
					", coarse-value = ", coarseValue, "; fine-index = ",
					fineIndex, ", fine-value = ", fineValue);
			fireControllerEvent(new Controller(coarseIndex, coarseValue));
			fireControllerEvent(new Controller(fineIndex, fineValue));
		} else {
			byte controlValue = getByteFromDictionary(controlValueString);
			trace("Controller element: index = ", controlIndex, ", value = ",
					controlValue);
			fireControllerEvent(new Controller(controlIndex, controlValue));
		}
	}

	/**
	 * Parses a channel pressure element.
	 * 
	 * @param s
	 *            the token that contains a channel pressure element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseChannelPressureElement(String s) throws JFugueException {
		// A ChannelPressure token looks like this:
		// +pressure
		//
		// where "pressure" can each be bytes or dictionary items

		String pressureString = s.substring(1, s.length());
		byte pressureNumber = getByteFromDictionary(pressureString);

		trace("ChannelPressure element: pressure = ", pressureNumber);
		fireChannelPressureEvent(new ChannelPressure(pressureNumber));
	}

	/**
	 * Parses a polyphonic pressure element.
	 * 
	 * @param s
	 *            the token that contains a polyphonic pressure element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parsePolyPressureElement(String s) throws JFugueException {
		// A PolyphonicPressure token looks like this:
		// *key,pressure
		//
		// where "key" and "pressure" can each be bytes or dictionary items

		String keyString = s.substring(1, s.indexOf(','));
		byte keyNumber = getByteFromDictionary(keyString);

		String pressureString = s.substring(s.indexOf(',') + 1, s.length());
		byte pressureNumber = getByteFromDictionary(pressureString);

		trace("PolyphonicPressure element: key = ", keyNumber, ", pressure = ",
				pressureNumber);
		firePolyphonicPressureEvent(
				new PolyphonicPressure(keyNumber, pressureNumber));
	}

	/**
	 * Parses a pitch bend element.
	 * 
	 * @param s
	 *            the token that contains a pitch bend pressure element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parsePitchBendElement(String s) throws JFugueException {
		// A PitchBend token looks like one of the following:
		// &lsb,msb
		// &int
		//
		// where "byte1" and "byte2" or "int" can be bytes/ints or dictionary
		// items

		byte lsb = 0;
		byte msb = 0;

		if (s.indexOf(',') > -1) {
			// We're dealing with two bytes
			String b1String = s.substring(1, s.indexOf(','));
			lsb = getByteFromDictionary(b1String);

			String b2String = s.substring(s.indexOf(',') + 1, s.length());
			msb = getByteFromDictionary(b2String);
		} else {
			// We're dealing with a single integer, which we will break into
			// bytes
			String valueString = s.substring(1, s.length());
			int value = getIntFromDictionary(valueString);
			lsb = (byte) (value % 128);
			msb = (byte) (value / 128);
		}

		trace("PitchBend element: byte1 = ", lsb, ", byte2 = ", msb);
		firePitchBendEvent(new PitchBend(lsb, msb));
	}

	/**
	 * Parses a dictionary element.
	 * 
	 * @param s
	 *            the token that contains a dictionary element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseDictionaryElement(String s) throws JFugueException {
		int indexOfEquals = s.indexOf("=");
		String word = s.substring(1, indexOfEquals);
		String definition = s.substring(indexOfEquals + 1, s.length());
		// Replace tilde's with spaces. I don't think this will work, though,
		// since the
		// MusicString has already been tokenized.
		// definition.replace('~', ' ');
		word = word.toUpperCase();
		trace("Dictionary Definition element: word = ", word, ", value = ",
				definition);
		dictionaryMap.put(word, definition);
	}

	class NoteContext {
		boolean isRest = false;
		boolean isNumericNote = false;
		boolean isChord = false;
		boolean isFirstNote = true;
		boolean isSequentialNote = false;
		boolean isParallelNote = false;
		boolean isNatural = false;
		boolean existAnotherNote = true;
		boolean anotherNoteIsSequential = false;
		boolean anotherNoteIsParallel = false;
		boolean isStartOfTie = false;
		boolean isEndOfTie = false;
		byte[] halfsteps = new byte[5];
		byte numHalfsteps = 0;
		byte noteNumber = 0;
		int octaveNumber = 0;
		double decimalDuration = 0.0;
		long duration = 0L;
		byte attackVelocity = Note.DEFAULT_VELOCITY;
		byte decayVelocity = Note.DEFAULT_VELOCITY;

		public NoteContext() {
			for (int i = 0; i < 5; i++) {
				halfsteps[i] = 0;
			}
		}
	}

	/**
	 * Parses a note element.
	 * 
	 * @param s
	 *            the token that contains a note element
	 * @throws JFugueException
	 *             if there is a problem parsing the element
	 */
	private void parseNoteElement(String s) throws JFugueException {
		NoteContext context = new NoteContext();

		while (context.existAnotherNote) {
			trace("--Parsing note from token " + s);
			decideSequentialOrParallel(context);
			int index = 0;
			int slen = s.length(); // We pass the length of the string because
									// it is an invariant value that is used
									// often
			index = parseNoteRoot(s, slen, index, context);
			index = parseNoteOctave(s, slen, index, context);
			index = parseNoteChord(s, slen, index, context);
			computeNoteValue(context);
			index = parseNoteChordInversion(s, slen, index, context);
			index = parseNoteDuration(s, slen, index, context);
			index = parseNoteVelocity(s, slen, index, context);
			s = parseNoteConnector(s, slen, index, context);
			fireNoteEvents(context);
		}
	}

	private void decideSequentialOrParallel(NoteContext context) {
		// Test whether this note is already known to be sequential (was
		// connected with _) or parallel (was connected with +)
		context.isSequentialNote = false;
		if (context.anotherNoteIsSequential) {
			context.isSequentialNote = true;
			context.anotherNoteIsSequential = false;
			trace("This note is sequential");
		}

		context.isParallelNote = false;
		if (context.anotherNoteIsParallel) {
			context.isParallelNote = true;
			context.anotherNoteIsParallel = false;
			trace("This note is parallel");
		}
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseNoteRoot(String s, int slen, int index,
			NoteContext context) {
		switch (s.charAt(index)) {
		case '[':
			return parseNumericNote(s, slen, index, context);
		case 'R':
			return parseRest(s, slen, index, context);
		default:
			return parseLetterNote(s, slen, index, context);
		}
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseNumericNote(String s, int slen, int index,
			NoteContext context) {
		int indexOfEndBracket = s.indexOf(']', index);
		String stringInBrackets = s.substring(1, indexOfEndBracket);
		context.noteNumber = getByteFromDictionary(stringInBrackets);
		context.isNumericNote = true;

		trace("This note is a numeric note with value ", context.noteNumber);
		return indexOfEndBracket + 1;
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseRest(String s, int slen, int index, NoteContext context) {
		context.isRest = true;

		trace("This note is a Rest");
		return index + 1;
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseLetterNote(String s, int slen, int index,
			NoteContext context) {
		switch (s.charAt(index)) {
		case 'C':
			context.noteNumber = 0;
			break;
		case 'D':
			context.noteNumber = 2;
			break;
		case 'E':
			context.noteNumber = 4;
			break;
		case 'F':
			context.noteNumber = 5;
			break;
		case 'G':
			context.noteNumber = 7;
			break;
		case 'A':
			context.noteNumber = 9;
			break;
		case 'B':
			context.noteNumber = 11;
			break;
		default:
			throw new JFugueException(JFugueException.NOTE_EXC, s);
		}
		index++;

		// Check for #, b, or n (sharp, flat, or natural) modifier
		boolean checkForModifiers = true;
		while (checkForModifiers) {
			if (index < slen) {
				switch (s.charAt(index)) {
				case '#':
					index++;
					context.noteNumber++;
					/* if (context.noteNumber == 12) context.noteNumber = 0; */ break;
				case 'B':
					index++;
					context.noteNumber--;
					/* if (context.noteNumber == -1) context.noteNumber = 11; */ break;
				case 'N':
					index++;
					context.isNatural = true;
					checkForModifiers = false;
					break;
				default:
					checkForModifiers = false;
					break;
				}
			} else {
				checkForModifiers = false;
			}
		}

		trace("Note number within an octave (C=0, B=11): ", context.noteNumber);
		return index;
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private static int parseNoteOctave(String s, int slen, int index,
			NoteContext context) {
		// Don't parse an octave for a rest or a numeric note
		if (context.isRest || context.isNumericNote) {
			return index;
		}

		// Check for octave. Remember that octaves are optional.
		char possibleOctave1 = '.';
		char possibleOctave2 = '.';

		if (index < slen) {
			possibleOctave1 = s.charAt(index);
		}

		if (index + 1 < slen) {
			possibleOctave2 = s.charAt(index + 1);
		}

		byte definiteOctaveLength = 0;
		if ((possibleOctave1 >= '0') && (possibleOctave1 <= '9')) {
			definiteOctaveLength = 1;
			if ((possibleOctave2 >= '0') && (possibleOctave2 <= '9')) {
				definiteOctaveLength = 2;
			}

			String octaveNumberString = s.substring(index,
					index + definiteOctaveLength);
			try {
				context.octaveNumber = Byte.parseByte(octaveNumberString);
			} catch (NumberFormatException e) {
				throw new JFugueException(JFugueException.OCTAVE_EXC,
						octaveNumberString, s);
			}
			if (context.octaveNumber > 10) {
				throw new JFugueException(JFugueException.OCTAVE_EXC,
						octaveNumberString, s);
			}
		}

		return index + definiteOctaveLength;
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseNoteChord(String s, int slen, int index,
			NoteContext context) {
		// Don't parse chord for a rest
		if (context.isRest) {
			return index;
		}

		String possibleChord3 = null;
		String possibleChord4 = null;
		String possibleChord5 = null;
		String possibleChord6 = null;
		String possibleChord7 = null;
		String possibleChord8 = null;
		try {
			possibleChord3 = s.substring(index, index + 3);
			possibleChord4 = s.substring(index, index + 4);
			possibleChord5 = s.substring(index, index + 5);
			possibleChord6 = s.substring(index, index + 6);
			possibleChord7 = s.substring(index, index + 7);
			possibleChord8 = s.substring(index, index + 8);
		} catch (IndexOutOfBoundsException e) {
			// Nothing to do... just needed to catch
		}

		int lengthOfChordString = 0; // This represents the length of the
										// string, not the number of halfsteps

		// Below, 'chordLength' refers to the size of the text for the chord
		// (for example, "min"=3, "dim7"=4),
		// and 'numHalfsteps' refers to the number of elements in the halfsteps
		// array.
		// This must be done in order from smaller to larger strings, so the
		// longer string names
		// take effect. This means 'min' can be overwritten by 'minmaj7', or
		// 'maj' by 'maj7', for example.

		if (possibleChord3 != null) {
			if (possibleChord3.equals("MAJ")) {
				lengthOfChordString = 3;
				context.numHalfsteps = 2;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 7;
			} else if (possibleChord3.equals("MIN")) {
				lengthOfChordString = 3;
				context.numHalfsteps = 2;
				context.halfsteps[0] = 3;
				context.halfsteps[1] = 7;
			} else if (possibleChord3.equals("AUG")) {
				lengthOfChordString = 3;
				context.numHalfsteps = 2;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 8;
			} else if (possibleChord3.equals("DIM")) {
				lengthOfChordString = 3;
				context.numHalfsteps = 2;
				context.halfsteps[0] = 3;
				context.halfsteps[1] = 6;
			}
		}
		if (possibleChord4 != null) {
			if (possibleChord4.equalsIgnoreCase("DOM7")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 7;
				context.halfsteps[2] = 10;
			} else if (possibleChord4.equalsIgnoreCase("MAJ7")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 7;
				context.halfsteps[2] = 11;
			} else if (possibleChord4.equalsIgnoreCase("MIN7")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 3;
				context.halfsteps[1] = 7;
				context.halfsteps[2] = 10;
			} else if (possibleChord4.equalsIgnoreCase("SUS4")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 2;
				context.halfsteps[0] = 5;
				context.halfsteps[1] = 7;
			} else if (possibleChord4.equalsIgnoreCase("SUS2")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 2;
				context.halfsteps[0] = 2;
				context.halfsteps[1] = 7;
			} else if (possibleChord4.equalsIgnoreCase("MAJ6")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 7;
				context.halfsteps[2] = 9;
			} else if (possibleChord4.equalsIgnoreCase("MIN6")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 3;
				context.halfsteps[1] = 7;
				context.halfsteps[2] = 9;
			} else if (possibleChord4.equalsIgnoreCase("DOM9")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 4;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 7;
				context.halfsteps[2] = 10;
				context.halfsteps[3] = 14;
			} else if (possibleChord4.equalsIgnoreCase("MAJ9")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 4;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 7;
				context.halfsteps[2] = 11;
				context.halfsteps[3] = 14;
			} else if (possibleChord4.equalsIgnoreCase("MIN9")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 4;
				context.halfsteps[0] = 3;
				context.halfsteps[1] = 7;
				context.halfsteps[2] = 10;
				context.halfsteps[3] = 14;
			} else if (possibleChord4.equalsIgnoreCase("DIM7")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 3;
				context.halfsteps[1] = 6;
				context.halfsteps[2] = 9;
			} else if (possibleChord4.equalsIgnoreCase("ADD9")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 7;
				context.halfsteps[2] = 14;
			} else if (possibleChord4.equalsIgnoreCase("DAVE")) {
				lengthOfChordString = 4;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 7;
				context.halfsteps[1] = 14;
				context.halfsteps[2] = 21;
			}
		}

		if (possibleChord5 != null) {
			if (possibleChord5.equalsIgnoreCase("MIN11")) {
				lengthOfChordString = 5;
				context.numHalfsteps = 5;
				context.halfsteps[0] = 7;
				context.halfsteps[1] = 10;
				context.halfsteps[2] = 14;
				context.halfsteps[3] = 15;
				context.halfsteps[4] = 17;
			} else if (possibleChord5.equalsIgnoreCase("DOM11")) {
				lengthOfChordString = 5;
				context.numHalfsteps = 4;
				context.halfsteps[0] = 7;
				context.halfsteps[1] = 10;
				context.halfsteps[2] = 14;
				context.halfsteps[3] = 17;
			} else if (possibleChord5.equalsIgnoreCase("DOM13")) {
				lengthOfChordString = 5;
				context.numHalfsteps = 5;
				context.halfsteps[0] = 7;
				context.halfsteps[1] = 10;
				context.halfsteps[2] = 14;
				context.halfsteps[3] = 16;
				context.halfsteps[4] = 21;
			} else if (possibleChord5.equalsIgnoreCase("MIN13")) {
				lengthOfChordString = 5;
				context.numHalfsteps = 5;
				context.halfsteps[0] = 7;
				context.halfsteps[1] = 10;
				context.halfsteps[2] = 14;
				context.halfsteps[3] = 15;
				context.halfsteps[4] = 21;
			} else if (possibleChord5.equalsIgnoreCase("MAJ13")) {
				lengthOfChordString = 5;
				context.numHalfsteps = 5;
				context.halfsteps[0] = 7;
				context.halfsteps[1] = 11;
				context.halfsteps[2] = 14;
				context.halfsteps[3] = 16;
				context.halfsteps[4] = 21;
			}
		}

		if (possibleChord6 != null) {
			if (possibleChord6.equalsIgnoreCase("DOM7<5")) {
				lengthOfChordString = 6;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 6;
				context.halfsteps[2] = 10;
			} else if (possibleChord6.equalsIgnoreCase("DOM7>5")) {
				lengthOfChordString = 6;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 8;
				context.halfsteps[2] = 10;
			} else if (possibleChord6.equalsIgnoreCase("MAJ7<5")) {
				lengthOfChordString = 6;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 6;
				context.halfsteps[2] = 11;
			} else if (possibleChord6.equalsIgnoreCase("MAJ7>5")) {
				lengthOfChordString = 6;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 8;
				context.halfsteps[2] = 11;
			}
		}

		if (possibleChord7 != null) {
			if (possibleChord7.equalsIgnoreCase("minmaj7")) {
				lengthOfChordString = 7;
				context.numHalfsteps = 3;
				context.halfsteps[0] = 3;
				context.halfsteps[1] = 7;
				context.halfsteps[2] = 11;
			}
		}

		if (possibleChord8 != null) {
			if (possibleChord8.equalsIgnoreCase("DOM7<5<9")) {
				lengthOfChordString = 8;
				context.numHalfsteps = 4;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 6;
				context.halfsteps[2] = 10;
				context.halfsteps[3] = 13;
			} else if (possibleChord8.equalsIgnoreCase("DOM7<5>9")) {
				lengthOfChordString = 8;
				context.numHalfsteps = 4;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 6;
				context.halfsteps[2] = 10;
				context.halfsteps[3] = 15;
			} else if (possibleChord8.equalsIgnoreCase("DOM7>5<9")) {
				lengthOfChordString = 8;
				context.numHalfsteps = 4;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 8;
				context.halfsteps[2] = 10;
				context.halfsteps[3] = 13;
			} else if (possibleChord8.equalsIgnoreCase("DOM7>5>9")) {
				lengthOfChordString = 8;
				context.numHalfsteps = 4;
				context.halfsteps[0] = 4;
				context.halfsteps[1] = 8;
				context.halfsteps[2] = 10;
				context.halfsteps[3] = 15;
			}
		}

		if (lengthOfChordString > 0) {
			context.isChord = true;
			trace("Chord: chordLength=", lengthOfChordString,
					", so chord is one of the following: [ 3=", possibleChord3,
					" 4=", possibleChord4, " 5=", possibleChord5, " 6=",
					possibleChord6, " 7=", possibleChord7, " 8=",
					possibleChord8, " ]");
		}

		return index + lengthOfChordString;
	}

	/**
	 * This method does a variety of calculations to get the actual value of the
	 * note.
	 */
	private void computeNoteValue(NoteContext context) {
		// Don't compute note value for a rest
		if (context.isRest) {
			return;
		}

		// If we happen not to have an octave yet, set it to a default value.
		// Default octave: 5 for notes, 3 for chords
		if ((context.octaveNumber == 0) && (!context.isNumericNote)) {
			if (context.isChord) {
				context.octaveNumber = 3;
			} else {
				context.octaveNumber = 5;
			}
		}
		trace("Octave: ", context.octaveNumber);

		// Adjust for Key Signature
		if ((keySig != 0) && (!context.isNatural)) {
			if ((keySig <= -1) && (context.noteNumber == 11)) {
				context.noteNumber = 10;
			}
			if ((keySig <= -2) && (context.noteNumber == 4)) {
				context.noteNumber = 3;
			}
			if ((keySig <= -3) && (context.noteNumber == 9)) {
				context.noteNumber = 8;
			}
			if ((keySig <= -4) && (context.noteNumber == 2)) {
				context.noteNumber = 1;
			}
			if ((keySig <= -5) && (context.noteNumber == 7)) {
				context.noteNumber = 6;
			}
			if ((keySig <= -6) && (context.noteNumber == 0)) {
				context.noteNumber = 11;
				context.octaveNumber--;
			}
			if ((keySig <= -7) && (context.noteNumber == 5)) {
				context.noteNumber = 4;
			}
			if ((keySig >= +1) && (context.noteNumber == 5)) {
				context.noteNumber = 6;
			}
			if ((keySig >= +2) && (context.noteNumber == 0)) {
				context.noteNumber = 1;
			}
			if ((keySig >= +3) && (context.noteNumber == 7)) {
				context.noteNumber = 8;
			}
			if ((keySig >= +4) && (context.noteNumber == 2)) {
				context.noteNumber = 3;
			}
			if ((keySig >= +5) && (context.noteNumber == 9)) {
				context.noteNumber = 10;
			}
			if ((keySig >= +6) && (context.noteNumber == 4)) {
				context.noteNumber = 5;
			}
			if ((keySig >= +7) && (context.noteNumber == 11)) {
				context.noteNumber = 0;
				context.octaveNumber++;
			}
			trace("After adjusting for Key Signature, noteNumber=",
					context.noteNumber, " octave=", context.octaveNumber);
		}

		// Compute the actual note number, based on octave and note
		if (!context.isNumericNote) {
			int intNoteNumber = (context.octaveNumber * 12)
					+ context.noteNumber;
			if (intNoteNumber > 127) {
				throw new JFugueException(JFugueException.NOTE_OCTAVE_EXC,
						Integer.toString(intNoteNumber), "");
			}
			context.noteNumber = (byte) intNoteNumber;
			trace("Computed note number: ", context.noteNumber);
		}
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseNoteChordInversion(String s, int slen, int index,
			NoteContext context) {
		if (!context.isChord) {
			return index;
		}

		int inversionCount = 0;
		int inversionRootNote = -1;
		int inversionOctave = -1;

		boolean checkForInversion = true;
		while (checkForInversion) {
			if (index < slen) {
				switch (s.charAt(index)) {
				case '^':
					index++;
					inversionCount++;
					break;
				case 'C':
					index++;
					inversionRootNote = 0;
					break;
				case 'D':
					index++;
					inversionRootNote = 2;
					break;
				case 'E':
					index++;
					inversionRootNote = 4;
					break;
				case 'F':
					index++;
					inversionRootNote = 5;
					break;
				case 'G':
					index++;
					inversionRootNote = 7;
					break;
				case 'A':
					index++;
					inversionRootNote = 9;
					break;
				// For 'B', need to differentiate between B note and 'b' flat
				case 'B':
					index++;
					if (inversionRootNote == -1) {
						inversionRootNote = 11;
					} else {
						inversionRootNote--;
					}
					break;
				case '#':
					index++;
					inversionRootNote++;
					break;
				// For '0', need to differentiate between initial 0 and 0 as a
				// second digit (i.e., 10)
				case '0':
					index++;
					if (inversionOctave == -1) {
						inversionOctave = 0;
					} else {
						inversionOctave = inversionOctave * 10;
					}
					break;
				case '1':
					index++;
					inversionOctave = 1;
					break;
				case '2':
					index++;
					inversionOctave = 2;
					break;
				case '3':
					index++;
					inversionOctave = 3;
					break;
				case '4':
					index++;
					inversionOctave = 4;
					break;
				case '5':
					index++;
					inversionOctave = 5;
					break;
				case '6':
					index++;
					inversionOctave = 6;
					break;
				case '7':
					index++;
					inversionOctave = 7;
					break;
				case '8':
					index++;
					inversionOctave = 8;
					break;
				case '9':
					index++;
					inversionOctave = 9;
					break;
				// If [, whoo boy, we're checking for a note number
				case '[':
					int indexEndBracket = s.indexOf(']', index);
					inversionRootNote = Integer.parseInt(
							s.substring(index + 1, indexEndBracket - 1));
					index = indexEndBracket + 1;
					break;
				default:
					checkForInversion = false;
					break;
				}
			} else {
				checkForInversion = false;
			}
		}

		// Modify the note values based on the inversion
		if (inversionCount > 0) {
			if (inversionRootNote == -1) {
				// The root is determined by a number of carets. Increase each
				// half-step
				// before the inversion by 12, the number of notes in an octave.
				trace("Inversion is base on count: " + inversionCount);
				trace("Inverting " + context.noteNumber + " to be "
						+ (context.noteNumber + 12));
				context.noteNumber += 12;
				for (int i = inversionCount
						- 1; i < context.numHalfsteps; i++) {
					trace("Inverting " + context.halfsteps[i] + " to be "
							+ (context.halfsteps[i] - 12));
					context.halfsteps[i] -= 12;
				}
			} else {
				// The root is determined by an inversionRoot. This is much
				// trickier, but we can
				// still figure it out.
				if (inversionOctave != -1) {
					inversionRootNote += inversionOctave * 12;
				} else if (inversionRootNote < 12) {
					int currentOctave = context.noteNumber / 12;
					inversionRootNote += currentOctave * 12;
				}
				// Otherwise, inversionRootNote is a numeric note value, like
				// [60]

				trace("Inversion is base on note: " + inversionRootNote);

				if ((inversionRootNote > context.noteNumber
						+ context.halfsteps[context.numHalfsteps - 1])
						|| (inversionRootNote < context.noteNumber)) {
					throw new JFugueException(JFugueException.INVERSION_EXC);
				}

				trace("Inverting " + context.noteNumber + " to be "
						+ (context.noteNumber + 12));
				context.noteNumber += 12;
				for (int i = 0; i < context.numHalfsteps; i++) {
					if (context.noteNumber
							+ context.halfsteps[i] >= inversionRootNote + 12) {
						trace("Inverting " + context.halfsteps[i] + " to be "
								+ (context.halfsteps[i] - 12));
						context.halfsteps[i] -= 12;
					}
				}
			}
		}

		return index;
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseNoteDuration(String s, int slen, int index,
			NoteContext context) {
		context.decimalDuration = 0.0;
		if (index < slen) {
			switch (s.charAt(index)) {
			case '/':
				index = parseNumericDuration(s, slen, index, context);
				break;
			case 'W':
			case 'H':
			case 'Q':
			case 'I':
			case 'S':
			case 'T':
			case 'X':
			case 'O':
			case '-':
				index = parseLetterDuration(s, slen, index, context);
				break;
			default:
				break;
			}
			index = parseTuplet(s, slen, index, context);
		} else {
			context.decimalDuration = 1.0 / 4.0; // Default duration is a
													// quarter note
		}

		// context.duration = (long) (120.0 * 4.0 * context.decimalDuration); //
		// javax.sound.midi.Sequence resolution is 120
		context.duration = (long) (120.0 * context.decimalDuration); // DMK
																		// 9/27/08:
																		// The
																		// *4.0
																		// makes
																		// quarter
																		// notes
																		// 4
																		// times
																		// as
																		// long
																		// as
																		// they
																		// should
																		// be

		// // Below is incorrect, as identified by M. Ahluwalia
		// // Tempo is now in Beats Per Minute. Convert this to Pulses Per
		// Quarter (PPQ), then to
		// // Pulses Per Whole (PPW), then multiply that by durationNumber for
		// WHQITXN notes
		// double ppq = 60000000.0D / (double)this.getTempo();
		// double ppw = ppq * 4.0; // 4 quarter notes in a whole note
		// context.duration = (long)(ppw * context.decimalDuration) / 4000;

		trace("Decimal duration is ", context.decimalDuration);
		trace("Actual duration is ", context.duration);

		return index;
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseLetterDuration(String s, int slen, int index,
			NoteContext context) {
		// Check duration
		boolean durationExists = true;
		boolean isDotted = false;

		while (durationExists == true) {
			int durationNumber = 0;
			// See if the note has a duration
			// Duration is optional; default is Q (4)
			if (index < slen) {
				char durationChar = s.charAt(index);
				switch (durationChar) {
				case '-':
					if ((context.decimalDuration == 0)
							&& (!context.isEndOfTie)) {
						context.isEndOfTie = true;
						trace("Note is end of tie");
					} else {
						context.isStartOfTie = true;
						trace("Note is start of tie");
					}
					break;
				case 'W':
					durationNumber = 1;
					break;
				case 'H':
					durationNumber = 2;
					break;
				case 'Q':
					durationNumber = 4;
					break;
				case 'I':
					durationNumber = 8;
					break;
				case 'S':
					durationNumber = 16;
					break;
				case 'T':
					durationNumber = 32;
					break;
				case 'X':
					durationNumber = 64;
					break;
				case 'O':
					durationNumber = 128;
					break;
				default:
					index--;
					durationExists = false;
					break;
				}
				index++;
				if ((index < slen) && (s.charAt(index) == '.')) {
					isDotted = true;
					index++;
				}

				if (durationNumber > 0) {
					double d = 1.0 / durationNumber;
					if (isDotted) {
						context.decimalDuration += d + (d / 2.0);
					} else {
						context.decimalDuration += d;
					}
				}
			} else {
				durationExists = false;
			}
		}

		return index;
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseNumericDuration(String s, int slen, int index,
			NoteContext context) {
		// The duration has come in as a number, like 0.25 for a quarter note.
		// Advance pointer past the initial slash (/)
		index++;

		// Decimal duration is not required to be enclosed by brackets,
		// but since most of the other numerical input to a MusicString
		// is required to be in brackets, we should support it.
		if ('[' == s.charAt(index)) {
			int indexOfEndingBracket = s.indexOf(']', index);
			context.decimalDuration += getDoubleFromDictionary(
					s.substring(index + 1, indexOfEndingBracket));
			index = indexOfEndingBracket + 1;
		} else {
			int endingIndex = index;
			boolean keepAdvancingPointer = true;
			while (keepAdvancingPointer) {
				try {
					char numericDurationChar = s.charAt(endingIndex);
					if ((numericDurationChar >= '0')
							&& (numericDurationChar <= '9')
							|| (numericDurationChar == '.')) // Decimal dot, not
																// dotted
																// duration
					{
						endingIndex++;
					} else {
						keepAdvancingPointer = false;
					}
				} catch (IndexOutOfBoundsException e) {
					keepAdvancingPointer = false;
				}
			}
			String durationNumberString = s.substring(index, endingIndex);
			context.decimalDuration += Double.parseDouble(durationNumberString);
			index = endingIndex;
		}

		trace("Decimal duration is ", context.decimalDuration);
		return index;
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseTuplet(String s, int slen, int index,
			NoteContext context) {
		if (index < slen) {
			if (s.charAt(index) == '*') {
				trace("Note is a tuplet");
				index++;

				// Figure out tuplet ratio, or figure out when to stop looking
				// for tuplet info
				boolean stopTupletParsing = false;
				int indexOfUnitsToMatch = 0;
				int indexOfNumNotes = 0;
				int counter = -1;
				while (!stopTupletParsing) {
					counter++;
					if (slen > index + counter) {
						if (s.charAt(index + counter) == ':') {
							indexOfNumNotes = index + counter + 1;
						} else if ((s.charAt(index + counter) >= '0')
								&& (s.charAt(index + counter) <= '9')) {
							if (indexOfUnitsToMatch == 0) {
								indexOfUnitsToMatch = index + counter;
							}
						} else if ((s.charAt(index + counter) == '*')) {
							// no op... artifact of parsing
						} else {
							stopTupletParsing = true;
						}
					} else {
						stopTupletParsing = true;
					}
				}

				index += counter;

				double numerator = 2.0;
				double denominator = 3.0;
				if ((indexOfUnitsToMatch > 0) && (indexOfNumNotes > 0)) {
					numerator = Double.parseDouble(s.substring(
							indexOfUnitsToMatch, indexOfNumNotes - 1));
					denominator = Double
							.parseDouble(s.substring(indexOfNumNotes, index));
				}
				trace("Tuplet ratio is " + numerator + ":" + denominator);
				double tupletRatio = numerator / denominator;
				context.decimalDuration = context.decimalDuration * tupletRatio;
				trace("Decimal duration after tuplet is ",
						context.decimalDuration);
			}
		}

		return index;
	}

	/**
	 * Returns the index with which to start parsing the next part of the
	 * string, once this method is done with its part
	 */
	private int parseNoteVelocity(String s, int slen, int index,
			NoteContext context) {
		// Don't compute note velocity for a rest
		if (context.isRest) {
			return index;
		}

		// Process velocity attributes, if they exist
		while (index < slen) {
			int startPoint = index + 1;
			int endPoint = startPoint;

			char velocityChar = s.charAt(index);
			int lengthOfByte = 0;
			if ((velocityChar == '+') || (velocityChar == '_')) {
				break;
			}
			trace("Identified Velocity character ", velocityChar);
			boolean byteDone = false;
			while (!byteDone && (index + lengthOfByte + 1 < slen)) {
				char possibleByteChar = s.charAt(index + lengthOfByte + 1);
				if ((possibleByteChar >= '0') && (possibleByteChar <= '9')) {
					lengthOfByte++;
				} else {
					byteDone = true;
				}
			}
			endPoint = index + lengthOfByte + 1;

			// Or maybe a bracketed string was passed in, instead of a byte
			if ((index + 1 < slen) && (s.charAt(index + 1) == '[')) {
				endPoint = s.indexOf(']', startPoint) + 1;
			}

			byte velocityNumber = getByteFromDictionary(
					s.substring(startPoint, endPoint));

			switch (velocityChar) {
			case 'A':
				context.attackVelocity = velocityNumber;
				break;
			case 'D':
				context.decayVelocity = velocityNumber;
				break;
			default:
				throw new JFugueException(JFugueException.NOTE_VELOCITY_EXC,
						s.substring(startPoint, endPoint), s);
			}
			index = endPoint;
		}
		trace("Attack velocity = ", context.attackVelocity,
				"; Decay velocity = ", context.decayVelocity);
		return index;
	}

	/**
	 * Returns the String of the next sub-token (the parts after + or _), if one
	 * exists; otherwise, returns null
	 */
	private String parseNoteConnector(String s, int slen, int index,
			NoteContext context) {
		context.existAnotherNote = false;
		// See if there's another note to process
		if ((index < slen)
				&& ((s.charAt(index) == '+') || (s.charAt(index) == '_'))) {
			trace("Another note: string = ",
					s.substring(index, s.length() - 1));
			if (s.charAt(index) == '_') {
				context.anotherNoteIsSequential = true;
				trace("Next note will be sequential");
			} else {
				context.anotherNoteIsParallel = true;
				trace("Next note will be parallel");
			}
			index++;
			context.existAnotherNote = true;
			return s.substring(index, slen);
		}
		return null;
	}

	private void fireNoteEvents(NoteContext context) {
		// Set up the note
		Note note = new Note();

		if (context.isRest) {
			note.setRest(true);
			note.setDuration(context.duration);
			note.setDecimalDuration(context.decimalDuration);
			note.setAttackVelocity((byte) 0); // turn off sound for rest notes
			note.setDecayVelocity((byte) 0);
		} else {
			note.setValue(context.noteNumber);
			note.setDuration(context.duration);
			note.setStartOfTie(context.isStartOfTie);
			note.setEndOfTie(context.isEndOfTie);
			note.setDecimalDuration(context.decimalDuration);
			note.setAttackVelocity(context.attackVelocity);
			note.setDecayVelocity(context.decayVelocity);
		}
		note.setHasAccompanyingNotes(
				context.existAnotherNote || context.isChord);

		// Fire note events
		if (context.isFirstNote) {
			note.setType(Note.FIRST);
			trace("Firing first note event");
			fireNoteEvent(note);
		} else if (context.isSequentialNote) {
			note.setType(Note.SEQUENTIAL);
			trace("Firing sequential note event");
			fireSequentialNoteEvent(note);
		} else if (context.isParallelNote) {
			note.setType(Note.PARALLEL);
			trace("Firing parallel note event");
			fireParallelNoteEvent(note);
		}

		if (context.isChord) {
			for (int i = 0; i < context.numHalfsteps; i++) {
				Note chordNote = new Note(
						(byte) (context.noteNumber + context.halfsteps[i]),
						context.duration);
				chordNote.setDecimalDuration(context.decimalDuration); // This
																		// won't
																		// have
																		// any
																		// effect
																		// on
																		// the
																		// note,
																		// but
																		// it's
																		// good
																		// bookkeeping
																		// to
																		// have
																		// it
																		// around.
				chordNote.setType(Note.PARALLEL);
				trace("Chord note number: ",
						(context.noteNumber + context.halfsteps[i]));
				if (i == context.numHalfsteps - 1) {
					chordNote.setHasAccompanyingNotes(context.existAnotherNote);
				} else {
					chordNote.setHasAccompanyingNotes(
							context.existAnotherNote || context.isChord);
				}
				fireParallelNoteEvent(chordNote);
			}
		}
		context.isFirstNote = false;
	}

	/**
	 * Looks up a string's value in the dictionary. The dictionary is used to
	 * keep memorable names of obscure numbers - for example, the string FLUTE
	 * is set to a value of 73, so when users want to play music with a flute,
	 * they can say "I[Flute]" instead of "I[73]".
	 *
	 * <p>
	 * The Dictionary feature also lets users define constants so that if the
	 * value of something were to change, it only needs to be changed in one
	 * place. For example, MY_FAVORITE_INSTRUMENT could be set to 73, then you
	 * can say "I[My_Favorite_Instrument]" when you want to play with that
	 * instrument. If your favorite instrument were ever to change, you only
	 * have to make the change in one place, instead of every place where you
	 * give the Instrument command.
	 * </p>
	 *
	 * @param bracketedString
	 *            the string to look up in the dictionary
	 * @returns the definition of the string
	 * @throws JFugueException
	 *             if there is a problem looking up bracketedString
	 */
	private String dictionaryLookup(String bracketedString)
			throws JFugueException {
		int indexOfOpeningBracket = bracketedString.indexOf("[");
		int indexOfClosingBracket = bracketedString.indexOf("]");

		String word = null;
		if ((indexOfOpeningBracket != -1) && (indexOfClosingBracket != -1)) {
			word = bracketedString.substring(indexOfOpeningBracket + 1,
					indexOfClosingBracket);
		} else {
			// It appears that "bracketedString" wasn't bracketed.
			word = bracketedString;
		}
		word = word.toUpperCase();

		String definition = (String) dictionaryMap.get(word);
		while ((definition != null)
				&& (dictionaryMap.containsKey(definition.toUpperCase()))) {
			definition = (String) dictionaryMap.get(definition.toUpperCase());
		}

		// If there is no definition for this word, see if the word is actually
		// a number.
		if (null == definition) {
			char ch = 0;
			boolean isNumber = true;
			for (int i = 0; i < word.length(); i++) {
				ch = word.charAt(i);
				if ((!Character.isDigit(ch) && (ch != '.'))) {
					isNumber = false;
				}
			}
			if (isNumber) {
				trace("Dictionary lookup returning the number ", word);
				return word;
			}
			throw new JFugueException(JFugueException.WORD_NOT_DEFINED_EXC,
					word, bracketedString);
		}
		trace("Word ", word, " is defined as ", definition);
		return definition;
	}

	/**
	 * Look up a byte from the dictionary
	 * 
	 * @param bracketedString
	 *            the string to look up
	 * @returns the byte value of the definition
	 * @throws JFugueException
	 *             if there is a problem getting a byte from the dictionary
	 *             look-up
	 */
	private byte getByteFromDictionary(String bracketedString)
			throws JFugueException {
		String definition = dictionaryLookup(bracketedString);
		Byte newbyte = null;
		try {
			newbyte = new Byte(definition);
		} catch (NumberFormatException e) {
			throw new JFugueException(JFugueException.EXPECTED_BYTE, definition,
					bracketedString);
		}
		return newbyte.byteValue();
	}

	/**
	 * Look up a long from the dictionary
	 * 
	 * @param bracketedString
	 *            the string to look up
	 * @returns the long value of the definition
	 * @throws JFugueException
	 *             if there is a problem getting a long from the dictionary
	 *             look-up
	 */
	private long getLongFromDictionary(String bracketedString)
			throws JFugueException {
		String definition = dictionaryLookup(bracketedString);
		Long newlong = null;
		try {
			newlong = new Long(definition);
		} catch (NumberFormatException e) {
			throw new JFugueException(JFugueException.EXPECTED_LONG, definition,
					bracketedString);
		}
		return newlong.longValue();
	}

	/**
	 * Look up an int from the dictionary
	 * 
	 * @param bracketedString
	 *            the string to look up
	 * @returns the int value of the definition
	 * @throws JFugueException
	 *             if there is a problem getting a int from the dictionary
	 *             look-up
	 */
	private int getIntFromDictionary(String bracketedString)
			throws JFugueException {
		String definition = dictionaryLookup(bracketedString);
		Integer newint = null;
		try {
			newint = Integer.valueOf(definition);
		} catch (NumberFormatException e) {
			throw new JFugueException(JFugueException.EXPECTED_INT, definition,
					bracketedString);
		}
		return newint.intValue();
	}

	/**
	 * Look up a double from the dictionary
	 * 
	 * @param bracketedString
	 *            the string to look up
	 * @returns the double value of the definition
	 * @throws JFugueException
	 *             if there is a problem getting a double from the dictionary
	 *             look-up
	 */
	private double getDoubleFromDictionary(String bracketedString)
			throws JFugueException {
		String definition = dictionaryLookup(bracketedString);
		Double newdouble = null;
		try {
			newdouble = new Double(definition);
		} catch (NumberFormatException e) {
			throw new JFugueException(JFugueException.EXPECTED_DOUBLE,
					definition, bracketedString);
		}
		return newdouble.doubleValue();
	}

	/**
	 * Checks whether a token is valid. This method is provided for testing
	 * purposes, and is not used during normal operation.
	 * 
	 * @param token
	 *            the token to test for validity
	 * @return <code>true</code> is the token is valid; <code>false</code>
	 *         otherwise.
	 */
	public boolean isValidToken(String token) {
		boolean valid = true;
		try {
			parseToken(token);
		} catch (Exception e) {
			valid = false;
		}

		return valid;
	}

	public void verifyToken(String token, final String verifyString) {
		ParserListener listener = new CollatedParserListener() {
			private StringBuilder results = new StringBuilder();

			@Override
			public void jfugueEvent(JFugueElement e) {
				results.append(e.getVerifyString());

				if (!verifyString.startsWith(results.toString())) {
					throw new JFugueException(
							JFugueException.VERIFICATION_EXCEPTION,
							results.toString(), verifyString);
				}

				results.append("; ");
			}
		};

		addParserListener(listener);
		parseToken(token);
		removeParserListener(listener);
	}

	/**
	 * Parses a string which presumably contains one token, which is a note.
	 *
	 * @param string
	 *            The String that contains one token with a note, like "C5"
	 * @return a Note object representing the note parsed from the string
	 */
	public static Note getNote(String string) {
		return getNote(new Pattern(string));
	}

	/**
	 * Parses a pattern which presumably contains one token, which is a note.
	 *
	 * @param pattern
	 *            The Pattern that contains one token with a note, like "C5"
	 * @return a Note object representing the note parsed from the pattern
	 */
	public static Note getNote(Pattern pattern) {
		final Note rootNote = new Note();

		MusicStringParser parser = new MusicStringParser();
		ParserListener renderer = new ParserListenerAdapter() {
			@Override
			public void noteEvent(Note note) {
				rootNote.setValue(note.getValue());
				rootNote.setDuration(note.getDuration());
				rootNote.setDecimalDuration(note.getDecimalDuration());
				rootNote.setStartOfTie(note.isStartOfTie());
				rootNote.setEndOfTie(note.isEndOfTie());
				rootNote.setAttackVelocity(note.getAttackVelocity());
				rootNote.setDecayVelocity(note.getDecayVelocity());
				rootNote.setRest(note.isRest());
			}
		};

		parser.addParserListener(renderer);
		parser.parse(pattern);

		return rootNote;
	}

	/**
	 * Used for diagnostic purposes. main() makes calls to test the
	 * Pattern-to-MIDI parser. If you make any changes to this parser, run this
	 * method ("java org.jfugue.MusicStringParser"), and make sure everything
	 * works correctly.
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		verifyTokenParsing();
	}

	/**
	 * Used for diagnostic purposes. Contains an assortment of tokens that are
	 * known to parse correctly.
	 */
	private static void verifyTokenParsing() {
		MusicStringParser parser = new MusicStringParser();
		parser.setTracing(MusicStringParser.TRACING_ON);
		try {
			long startTime = System.currentTimeMillis();

			// Don't forget -- individual tokens ONLY! No strings with spaces!
			parser.verifyToken("C", Note.createVerifyString(60, 0.25));
			parser.verifyToken("C3", Note.createVerifyString(36, 0.25));
			parser.verifyToken("Cb3", Note.createVerifyString(35, 0.25));
			parser.verifyToken("B#3", Note.createVerifyString(48, 0.25));
			parser.verifyToken("C#3q", Note.createVerifyString(37, 0.25));
			parser.verifyToken("C3i", Note.createVerifyString(36, 0.125));
			parser.verifyToken("C3qh", Note.createVerifyString(36, 0.75));
			parser.verifyToken("C5minw", Note.createCompoundVerifyString(
					Note.createVerifyString(60, 1.0),
					Note.createVerifyString(63, 1.0, false, true, false),
					Note.createVerifyString(67, 1.0, false, true, false)));
			parser.verifyToken("Cmaj", Note.createCompoundVerifyString(
					Note.createVerifyString(36, 0.25),
					Note.createVerifyString(40, 0.25, false, true, false),
					Note.createVerifyString(43, 0.25, false, true, false)));
			parser.parseToken("Cdom9");
			parser.parseToken("Cmin11");
			parser.parseToken("Cdom7<5");
			parser.parseToken("Cminmaj7");
			parser.parseToken("Cdom7<5<9");
			parser.parseToken("Cwhqistxo");

			parser.parseToken("C10");

			parser.parseToken("V0");
			parser.parseToken("V15");
			parser.parseToken("I0");
			parser.parseToken("I[13]");
			parser.parseToken("I[Acoustic_Grand]");
			parser.parseToken("IFlute");
			parser.parseToken("Cmaj7W");
			parser.parseToken("C#5Q");
			parser.parseToken("eb3Q.");
			parser.parseToken("[Cowbell]O");

			parser.parseToken("P50"); // An unknown token should just pass
										// through

			parser.parseToken("A");
			parser.parseToken("A+B+C");
			parser.parseToken("A_B_C");
			parser.parseToken("RW");
			parser.parseToken("[105]X");

			parser.parseToken("[105]Xa20+[98]X+[78]X");
			parser.parseToken("AW+[18]X+[cabasa]Q+Dmin");

			parser.parseToken("A/0.25");
			parser.parseToken("[70]o");

			// 2.0 Dictionary Definition and Controller Events
			parser.parseToken("$UKELE=72");
			parser.parseToken("IUKELE");
			parser.parseToken("$Volume=43");
			parser.parseToken("X[Volume]=10");
			parser.parseToken("X[PORTAMENTO_TIME]=777");

			// 2.0 Dictionary Definition in odd situations that should work
			parser.parseToken("XVolume=ON");
			parser.parseToken("[Ukele]q");

			// 2.0 Dictionary Definition and non-bytes
			parser.parseToken("$number1=1");
			parser.parseToken("$quarter=0.25");
			parser.parseToken("C4/[quarter]");
			parser.parseToken("C4q");
			parser.parseToken("[Number1]/[Quarter]");

			// 2.0 Note velocity
			parser.parseToken("Cb4qa45");
			parser.parseToken("Gb4qd67");
			parser.parseToken("F#4qa55d77");
			parser.parseToken("B4qa[Volume]d[Number1]");

			// 3.0 Layers
			parser.parseToken("L8");
			parser.parseToken("$number1=1");
			parser.parseToken("L[Number1]");

			// 3.0 Times
			parser.parseToken("@100002");

			// 3.0 Measures
			parser.parseToken("|");

			// 3.0 Tied notes
			parser.parseToken("Cq-");
			parser.parseToken("C5q-");
			parser.parseToken("C5q.-");
			parser.parseToken("C5qh-");
			parser.parseToken("C-q");
			parser.parseToken("C5-q");
			parser.parseToken("C5-q.");
			parser.parseToken("C5-qh");
			parser.parseToken("C-q-");
			parser.parseToken("C--");

			// 3.0 Pitch Bend
			parser.parseToken("&100,50");
			parser.parseToken("&512");
			parser.parseToken("$number110=110");
			parser.parseToken("&[number110],50");
			parser.parseToken("&[number110],[number110]");
			parser.parseToken("$number1010=1010");
			parser.parseToken("&[number1010]");

			// 3.0 Channel Pressure
			parser.parseToken("+100");
			parser.parseToken("$number110=110");
			parser.parseToken("+[number110]");
			parser.parseToken("+[number110]");

			// 3.0 Polyphonic Pressure
			parser.parseToken("*100,20");
			parser.parseToken("$number110=110");
			parser.parseToken("*[number110],50");
			parser.parseToken("*[number110],[number110]");

			// 4.0 Chord Inversions
			parser.parseToken("Cmaj");
			parser.parseToken("C7maj");
			parser.parseToken("C7maj^");
			parser.parseToken("C7maj^^");
			parser.parseToken("C7maj^^^");
			parser.parseToken("C7maj^E");
			parser.parseToken("C7maj^G");
			parser.parseToken("C7maj^E7");
			parser.parseToken("C7maj^G7");
			parser.parseToken("[60]maj^");
			parser.parseToken("[60]maj^^");
			parser.parseToken("[60]maj^^^");
			parser.parseToken("[60]maj^E");
			parser.parseToken("[60]maj^[67]");
			parser.parseToken("Bb6min13^^^^^^");

			// 4.0 Tuplets
			parser.parseToken("Cq*");
			parser.parseToken("Ci*5:4");
			parser.parseToken("Cs.*7:8");
			parser.parseToken("Chx.*10:11");

			// 3.0 Key Signatures
			parser.parseToken("KC#maj");
			parser.parseToken("KAbmin");
			parser.parseToken("Cn");
			parser.parseToken("Cn6");

			// 4.0 New parser
			parser.parseToken("D3");
			parser.parseToken("C##3"); // Should be like D3

			long endTime = System.currentTimeMillis();
			System.out.println("Time taken: " + (endTime - startTime) + "ms");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
