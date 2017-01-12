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
 * This Adapter class implements all of the methods of ParserListener, but the
 * implementations are blank. If you want something to be a ParserListener, but
 * you don't want to implement all of the ParserListener methods, extend this
 * class.
 *
 * @author David Koelle
 * @version 3.0
 */
public class ParserListenerAdapter implements ParserListener {
	/**
	 * Called when the parser encounters a voice event.
	 * 
	 * @param voice
	 *            the event that has been parsed
	 * @see Voice
	 */
	@Override
	public void voiceEvent(Voice voice) {
	}

	/**
	 * Called when the parser encounters a tempo event.
	 * 
	 * @param tempo
	 *            the event that has been parsed
	 * @see Tempo
	 */
	@Override
	public void tempoEvent(Tempo tempo) {
	}

	/**
	 * Called when the parser encounters an instrument event.
	 * 
	 * @param instrument
	 *            the event that has been parsed
	 * @see Instrument
	 */
	@Override
	public void instrumentEvent(Instrument instrument) {
	}

	/**
	 * Called when the parser encounters a layer event.
	 * 
	 * @param layer
	 *            the event that has been parsed
	 * @see Layer
	 */
	@Override
	public void layerEvent(Layer layer) {
	}

	/**
	 * Called when the parser encounters a measure event.
	 * 
	 * @param measure
	 *            the event that has been parsed
	 * @see Measure
	 */
	@Override
	public void measureEvent(Measure measure) {
	}

	/**
	 * Called when the parser encounters a time event.
	 * 
	 * @param time
	 *            the event that has been parsed
	 * @see Time
	 */
	@Override
	public void timeEvent(Time time) {
	}

	/**
	 * Called when the parser encounters a key signature event.
	 * 
	 * @param time
	 *            the event that has been parsed
	 * @see KeySignature
	 */
	@Override
	public void keySignatureEvent(KeySignature keySig) {
	}

	/**
	 * Called when the parser encounters a controller event.
	 * 
	 * @param controller
	 *            the event that has been parsed
	 */
	@Override
	public void controllerEvent(Controller controller) {
	}

	/**
	 * Called when the parser encounters a channel pressure event.
	 * 
	 * @param channelPressure
	 *            the event that has been parsed
	 * @see ChannelPressure
	 */
	@Override
	public void channelPressureEvent(ChannelPressure channelPressure) {
	}

	/**
	 * Called when the parser encounters a polyphonic pressure event.
	 * 
	 * @param polyphonicPressure
	 *            the event that has been parsed
	 * @see PolyphonicPressure
	 */
	@Override
	public void polyphonicPressureEvent(PolyphonicPressure polyphonicPressure) {
	}

	/**
	 * Called when the parser encounters a pitch bend event.
	 * 
	 * @param pitchBend
	 *            the event that has been parsed
	 * @see PitchBend
	 */
	@Override
	public void pitchBendEvent(PitchBend pitchBend) {
	}

	/**
	 * Called when the parser encounters an initial note event.
	 * 
	 * @param note
	 *            the event that has been parsed
	 * @see Note
	 */
	@Override
	public void noteEvent(Note note) {
	}

	/**
	 * Called when the parser encounters a sequential note event.
	 * 
	 * @param note
	 *            the event that has been parsed
	 * @see Note
	 */
	@Override
	public void sequentialNoteEvent(Note note) {
	}

	/**
	 * Called when the parser encounters a parallel note event.
	 * 
	 * @param note
	 *            the event that has been parsed
	 * @see Note
	 */
	@Override
	public void parallelNoteEvent(Note note) {
	}
}
