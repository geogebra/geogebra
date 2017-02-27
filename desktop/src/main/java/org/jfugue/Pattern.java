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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.swing.event.EventListenerList;

/**
 * This class represents a segment of music. By representing segments of music
 * as patterns, JFugue gives users the opportunity to play around with pieces of
 * music in new and interesting ways. Patterns may be added together,
 * transformed, or otherwise manipulated to expand the possibilities of creative
 * music.
 *
 * @author David Koelle
 * @version 2.0
 * @version 4.0 - Added Pattern Properties
 * @version 4.0.3 - Now implements Serializable
 */
public class Pattern {
	private StringBuilder musicString;
	private Map<String, String> properties;

	/**
	 * Instantiates a new pattern
	 */
	public Pattern() {
		this("");
	}

	/**
	 * Instantiates a new pattern using the given music string
	 * 
	 * @param s
	 *            the music string
	 */
	public Pattern(String musicString) {
		setMusicString(musicString);
		properties = new HashMap<String, String>();
	}

	/** Copy constructor */
	public Pattern(Pattern pattern) {
		this(pattern.getMusicString());
		Iterator<String> iter = pattern.getProperties().keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String value = pattern.getProperty(key);
			setProperty(key, value);
		}
	}

	/**
	 * This constructor creates a new Pattern that contains each of the given
	 * patterns
	 * 
	 * @version 4.0
	 */
	public Pattern(Pattern... patterns) {
		this();
		for (Pattern p : patterns) {
			this.add(p);
		}
	}

	/**
	 * Creates a Pattern given a MIDI file - do not use. Note the Package scope,
	 * limiting this method to be called only by JFugue. If you want to load
	 * MIDI, use Player.loadMidi, which sets the sequence timing correctly.
	 * 
	 * @param file
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 */
	static Pattern loadMidi(File file)
			throws IOException, InvalidMidiDataException {
		MidiParser parser = new MidiParser();
		MusicStringRenderer renderer = new MusicStringRenderer();
		parser.addParserListener(renderer);
		parser.parse(MidiSystem.getSequence(file));
		Pattern pattern = new Pattern(renderer.getPattern().getMusicString());
		return pattern;
	}

	/**
	 * Sets the music string kept by this pattern.
	 * 
	 * @param s
	 *            the music string
	 */
	public void setMusicString(String musicString) {
		this.musicString = new StringBuilder();
		this.musicString.append(musicString);
	}

	/**
	 * Adds to the music string kept by this pattern.
	 * 
	 * @param s
	 *            the music string to add
	 */
	private void appendMusicString(String appendString) {
		this.musicString.append(appendString);
	}

	/**
	 * Returns the music string kept in this pattern
	 * 
	 * @return the music string
	 */
	public String getMusicString() {
		return this.musicString.toString();
	}

	/**
	 * Inserts a MusicString before this music string. NOTE - this does not call
	 * fragmentAdded!
	 * 
	 * @param musicString
	 *            the string to insert
	 */
	public void insert(String musicString) {
		this.musicString.insert(0, " ");
		this.musicString.insert(0, musicString);
	}

	/**
	 * Adds an additional pattern to the end of this pattern.
	 * 
	 * @param pattern
	 *            the pattern to add
	 */
	public void add(Pattern pattern) {
		fireFragmentAdded(pattern);
		appendMusicString(" ");
		appendMusicString(pattern.getMusicString());
	}

	/**
	 * Adds a music string to the end of this pattern.
	 * 
	 * @param musicString
	 *            the music string to add
	 */
	public void add(String musicString) {
		add(new Pattern(musicString));
	}

	/**
	 * Adds an additional pattern to the end of this pattern.
	 * 
	 * @param pattern
	 *            the pattern to add
	 */
	public void add(Pattern pattern, int numTimes) {
		for (int i = 0; i < numTimes; i++) {
			fireFragmentAdded(pattern);
			appendMusicString(" ");
			appendMusicString(pattern.getMusicString());
		}
	}

	/**
	 * Adds a music string to the end of this pattern.
	 * 
	 * @param musicString
	 *            the music string to add
	 */
	public void add(String musicString, int numTimes) {
		add(new Pattern(musicString), numTimes);
	}

	/**
	 * Adds a number of patterns sequentially
	 * 
	 * @param musicString
	 *            the music string to add
	 * @version 4.0
	 */
	public void add(Pattern... patterns) {
		for (Pattern pattern : patterns) {
			add(pattern);
		}
	}

	/**
	 * Adds a number of patterns sequentially
	 * 
	 * @param musicString
	 *            the music string to add
	 * @version 4.0
	 */
	public void add(String... musicStrings) {
		for (String string : musicStrings) {
			add(string);
		}
	}

	/**
	 * Adds an individual element to the pattern. This takes into account the
	 * possibility that the element may be a sequential or parallel note, in
	 * which case no space is placed before it.
	 * 
	 * @param element
	 *            the element to add
	 */
	public void addElement(JFugueElement element) {
		String elementMusicString = element.getMusicString();

		// Don't automatically add a space if this is a continuing note event
		if ((elementMusicString.charAt(0) == '+')
				|| (elementMusicString.charAt(0) == '_')) {
			appendMusicString(elementMusicString);
		} else {
			appendMusicString(" ");
			appendMusicString(elementMusicString);
			fireFragmentAdded(new Pattern(elementMusicString));
		}
	}

	/**
	 * Sets the title for this Pattern. As of JFugue 4.0, the title is set as a
	 * property with the key Pattern.TITLE
	 * 
	 * @param title
	 *            the title for this Pattern
	 */
	public void setTitle(String title) {
		setProperty(TITLE, title);
	}

	/**
	 * Returns the title of this Pattern As of JFugue 4.0, the title is set as a
	 * property with the key Pattern.TITLE
	 * 
	 * @return the title of this Pattern
	 */
	public String getTitle() {
		return getProperty(TITLE);
	}

	/**
	 * Get a property on this pattern, such as "author" or "date".
	 * 
	 * @version 4.0
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}

	/**
	 * Set a property on this pattern, such as "author" or "date".
	 * 
	 * @version 4.0
	 */
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * Get all properties set on this pattern, such as "author" or "date".
	 * 
	 * @version 4.0
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * Repeats the music string in this pattern by the given number of times.
	 * Example: If the pattern is "A B", calling <code>repeat(4)</code> will
	 * make the pattern "A B A B A B A B".
	 * 
	 * @version 3.0
	 */
	public void repeat(int times) {
		repeat(null, getMusicString(), times, null);
	}

	/**
	 * Only repeats the portion of this music string that starts at the string
	 * index provided. This allows some initial header information to only be
	 * specified once in a repeated pattern. Example: If the pattern is "T0 A B"
	 * , calling <code>repeat(4, 3)</code> will make the pattern
	 * "T0 A B A B A B A B".
	 * 
	 * @version 3.0
	 */
	public void repeat(int times, int beginIndex) {
		String string = getMusicString();
		repeat(string.substring(0, beginIndex), string.substring(beginIndex),
				times, null);
	}

	/**
	 * Only repeats the portion of this music string that starts and ends at the
	 * string indices provided. This allows some initial header information and
	 * trailing information to only be specified once in a repeated pattern.
	 * Example: If the pattern is "T0 A B C", calling
	 * <code>repeat(4, 3, 5)</code> will make the pattern "T0 A B A B A B A B C"
	 * .
	 * 
	 * @version 3.0
	 */
	public void repeat(int times, int beginIndex, int endIndex) {
		String string = getMusicString();
		repeat(string.substring(0, beginIndex),
				string.substring(beginIndex, endIndex), times,
				string.substring(endIndex));
	}

	private void repeat(String header, String repeater, int times,
			String trailer) {
		StringBuffer buffy = new StringBuffer();

		// Add the header, if it exists
		if (header != null) {
			buffy.append(header);
		}

		// Repeat and add the repeater
		for (int i = 0; i < times; i++) {
			buffy.append(repeater);
			if (i < times - 1) {
				buffy.append(" ");
			}
		}

		// Add the trailer, if it exists
		if (trailer != null) {
			buffy.append(trailer);
		}

		// Create the new Pattern and return it
		this.setMusicString(buffy.toString());
	}

	/**
	 * Returns a new Pattern that is a subpattern of this pattern.
	 * 
	 * @return subpattern of this pattern
	 * @version 3.0
	 */
	public Pattern subPattern(int beginIndex) {
		return new Pattern(substring(beginIndex));
	}

	/**
	 * Returns a new Pattern that is a subpattern of this pattern.
	 * 
	 * @return subpattern of this pattern
	 * @version 3.0
	 */
	public Pattern subPattern(int beginIndex, int endIndex) {
		return new Pattern(substring(beginIndex, endIndex));
	}

	protected String substring(int beginIndex) {
		return getMusicString().substring(beginIndex);
	}

	protected String substring(int beginIndex, int endIndex) {
		return getMusicString().substring(beginIndex, endIndex);
	}

	public static Pattern loadPattern(File file) throws IOException {
		StringBuffer buffy = new StringBuffer();

		Pattern pattern = new Pattern();

		BufferedReader bread = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		while (bread.ready()) {
			String s = bread.readLine();
			if ((s != null) && (s.length() > 1)) {
				if (s.charAt(0) != '#') {
					buffy.append(" ");
					buffy.append(s);
				} else {
					String key = s.substring(1, s.indexOf(':')).trim();
					String value = s.substring(s.indexOf(':') + 1, s.length())
							.trim();
					if (key.equalsIgnoreCase(TITLE)) {
						pattern.setTitle(value);
					} else {
						pattern.setProperty(key, value);
					}
				}
			}
		}
		bread.close();
		pattern.setMusicString(buffy.toString());

		return pattern;
	}

	/**
	 * Saves the pattern as a text file
	 * 
	 * @param filename
	 *            the filename to save under
	 */
	public void savePattern(File file) throws IOException {
		BufferedWriter out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		if ((getProperties().size() > 0) || (getTitle() != null)) {
			out.write("#\n");
			if (getTitle() != null) {
				out.write("# ");
				out.write("Title: ");
				out.write(getTitle());
				out.write("\n");
			}
			Iterator<String> iter = getProperties().keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				if (!key.equals(TITLE)) {
					String value = getProperty(key);
					out.write("# ");
					out.write(key);
					out.write(": ");
					out.write(value);
					out.write("\n");
				}
			}
			out.write("#\n");
			out.write("\n");
		}
		String musicString = getMusicString();
		while (musicString.length() > 0) {
			if ((musicString.length() > 80)
					&& (musicString.indexOf(' ', 80) > -1)) {
				int indexOf80ColumnSpace = musicString.indexOf(' ', 80);
				out.write(musicString.substring(0, indexOf80ColumnSpace));
				out.newLine();
				musicString = musicString.substring(indexOf80ColumnSpace,
						musicString.length());
			} else {
				out.write(musicString);
				musicString = "";
			}
		}
		out.close();
	}

	/**
	 * Returns a String containing key-value pairs stored in this object's
	 * properties, separated by semicolons and spaces. Values are returned in
	 * the following form: key1: value1; key2: value2; key3: value3
	 *
	 * @return a String containing key-value pairs stored in this object's
	 *         properties, separated by semicolons and spaces
	 */
	public String getPropertiesAsSentence() {
		StringBuilder buddy = new StringBuilder();
		Iterator<String> iter = getProperties().keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String value = getProperty(key);
			buddy.append(key);
			buddy.append(": ");
			buddy.append(value);
			buddy.append("; ");
		}
		String result = buddy.toString();
		return result.substring(0, result.length() - 2); // Take off the last
															// semicolon-space
	}

	/**
	 * Returns a String containing key-value pairs stored in this object's
	 * properties, separated by newline characters.
	 *
	 * Values are returned in the following form: key1: value1\n key2: value2\n
	 * key3: value3\n
	 *
	 * @return a String containing key-value pairs stored in this object's
	 *         properties, separated by newline characters
	 */
	public String getPropertiesAsParagraph() {
		StringBuilder buddy = new StringBuilder();
		Iterator<String> iter = getProperties().keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String value = getProperty(key);
			buddy.append(key);
			buddy.append(": ");
			buddy.append(value);
			buddy.append("\n");
		}
		String result = buddy.toString();
		return result.substring(0, result.length());
	}

	/**
	 * Changes all timestamp values by the offsetTime passed in. NOTE: This
	 * method is only useful for patterns that have been converted from a MIDI
	 * file.
	 * 
	 * @param offsetTime
	 */
	public void offset(long offsetTime) {
		StringBuffer buffy = new StringBuffer();
		String[] tokens = getMusicString().split(" ");
		for (int i = 0; i < tokens.length; i++) {
			if ((tokens[i].length() > 0) && (tokens[i].charAt(0) == '@')) {
				String timeNumberString = tokens[i].substring(1,
						tokens[i].length());
				if (timeNumberString.indexOf("[") == -1) {
					long timeNumber = Long.parseLong(timeNumberString);
					long newTime = timeNumber + offsetTime;
					if (newTime < 0) {
						newTime = 0;
					}
					buffy.append("@" + newTime);
				} else {
					buffy.append(tokens[i]);
				}
			} else {
				buffy.append(tokens[i]);
			}
			buffy.append(" ");
		}
		setMusicString(buffy.toString());
	}

	/**
	 * Returns an array of strings representing each token in the Pattern.
	 * 
	 * @return
	 */
	public String[] getTokens() {
		StringTokenizer strtok = new StringTokenizer(musicString.toString(),
				" \n\t");

		List<String> list = new ArrayList<String>();
		while (strtok.hasMoreTokens()) {
			String token = strtok.nextToken();
			if (token != null) {
				list.add(token);
			}
		}

		String[] retVal = new String[list.size()];
		list.toArray(retVal);
		return retVal;
	}

	/**
	 * Indicates whether the provided musicString is composed of valid elements
	 * that can be parsed by the Parser.
	 * 
	 * @param musicString
	 *            the musicString to test
	 * @return whether the musicString is valid
	 * @version 3.0
	 */
	// public static boolean isValidMusicString(String musicString)
	// {
	// try {
	// Parser parser = new Parser();
	// parser.parse(musicString);
	// } catch (JFugueException e)
	// {
	// return false;
	// }
	// return true;
	// }

	//
	// Listeners
	//

	/** List of ParserListeners */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Adds a <code>PatternListener</code>. The listener will receive events
	 * when new parts are added to the pattern.
	 *
	 * @param listener
	 *            the listener that is to be notified when new parts are added
	 *            to the pattern
	 */
	public void addPatternListener(PatternListener l) {
		listenerList.add(PatternListener.class, l);
	}

	/**
	 * Removes a <code>PatternListener</code>.
	 *
	 * @param listener
	 *            the listener to remove
	 */
	public void removePatternListener(PatternListener l) {
		listenerList.remove(PatternListener.class, l);
	}

	protected void clearPatternListeners() {
		EventListener[] l = listenerList.getListeners(PatternListener.class);
		int numListeners = l.length;
		for (int i = 0; i < numListeners; i++) {
			listenerList.remove(PatternListener.class, (PatternListener) l[i]);
		}
	}

	/** Tells all PatternListener interfaces that a fragment has been added. */
	private void fireFragmentAdded(Pattern fragment) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PatternListener.class) {
				((PatternListener) listeners[i + 1]).fragmentAdded(fragment);
			}
		}
	}

	/**
	 * @version 3.0
	 */
	@Override
	public String toString() {
		return getMusicString();
	}

	public static final String TITLE = "Title";
}
