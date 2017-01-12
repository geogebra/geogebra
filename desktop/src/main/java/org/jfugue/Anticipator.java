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

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;

/**
 * This class can be used in conjunction with a call to Player.play() to inform
 * your application about musical events before they happen. This is useful if
 * you're creating an application that requires advance notice of a musical
 * event - for example, an animation program that must wind up or swing an arm
 * back before striking a note.
 * 
 * This feature is covered in detail in "The Complete Guide to JFugue"
 * 
 * @author David Koelle
 * @version 3.0
 */
public class Anticipator {
	protected MidiParser parser;

	public Anticipator() {
		this.parser = new MidiParser();
	}

	/**
	 * Adds a <code>ParserListener</code>.
	 *
	 * @param listener
	 *            the listener to remove
	 */
	public void addParserListener(ParserListener l) {
		this.parser.addParserListener(l);
	}

	/**
	 * Removes a <code>ParserListener</code>.
	 *
	 * @param listener
	 *            the listener to remove
	 */
	public void removeParserListener(ParserListener l) {
		this.parser.removeParserListener(l);
	}

	protected void play(final Sequence sequence) {
		final Thread anticipatingThread = new Thread() {
			@Override
			public void run() {
				TimeFactor.sortAndDeliverMidiMessages(sequence,
						new MidiMessageRecipient() {
							@Override
							public void messageReady(MidiMessage message,
									long timestamp) {
								parser.parse(message, timestamp);
							}
						});
			}
		};

		anticipatingThread.start();
	}
}
