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

import java.util.EventListener;

import javax.swing.event.EventListenerList;

/**
 * You may notice that there is no parse() method in the Parser class! That's
 * because the parse() method may take any type of parameter, as well as any
 * number of parameters, so it isn't something that can declared ahead of time.
 * 
 * @author David Koelle
 *
 */
public class Parser {
	public Parser() {
		progressListenerList = new EventListenerList();
		listenerList = new EventListenerList();

		// The Parser could add itself as a ParserProgressListener.
	}

	// Logging methods
	///////////////////////////////////////////

	/**
	 * Pass this value to setTracing( ) to turn tracing off. Tracing is off by
	 * default.
	 */
	public static final int TRACING_OFF = 0;

	/**
	 * Pass this value to setTracing( ) to turn tracing on. Tracing is off by
	 * default.
	 */
	public static final int TRACING_ON = 1;

	private int tracing = TRACING_ON;

	/**
	 * Turns tracing on or off. If you're having trouble with your music string,
	 * or if you've added new tokens to the parser, turn tracing on to make sure
	 * that your new tokens are parsed correctly.
	 * 
	 * @param tracing
	 *            the state of tracing - on or off
	 */
	public void setTracing(int tracing) {
		this.tracing = tracing;
	}

	/**
	 * Returns the current state of tracing.
	 * 
	 * @return the state of tracing
	 */
	public int getTracing() {
		return this.tracing;
	}

	/**
	 * Displays the passed String.
	 * 
	 * @param s
	 *            the String to display
	 */
	protected void trace(Object... sentenceFragments) {
		if (TRACING_ON == getTracing()) {
			StringBuilder buddy = new StringBuilder();
			for (int i = 0; i < sentenceFragments.length; i++) {
				buddy.append(sentenceFragments[i]);
			}

			System.out.println(buddy.toString());
		}
	}

	//
	// ParserProgressListener methods
	/////////////////////////////////////////////////////////////////////////

	/** List of ParserProgressListeners */
	protected EventListenerList progressListenerList;

	/**
	 * Adds a <code>ParserListener</code>. The listener will receive events when
	 * the parser interprets music string tokens.
	 *
	 * @param listener
	 *            the listener that is to be notified of parser events
	 */
	public void addParserProgressListener(ParserProgressListener l) {
		progressListenerList.add(ParserProgressListener.class, l);
	}

	/**
	 * Removes a <code>ParserListener</code>.
	 *
	 * @param listener
	 *            the listener to remove
	 */
	public void removeParserProgressListener(ParserProgressListener l) {
		progressListenerList.remove(ParserProgressListener.class, l);
	}

	protected void clearParserProgressListeners() {
		EventListener[] l = progressListenerList
				.getListeners(ParserProgressListener.class);
		int numListeners = l.length;
		for (int i = 0; i < numListeners; i++) {
			progressListenerList.remove(ParserProgressListener.class,
					(ParserProgressListener) l[i]);
		}
	}

	/**
	 * Tells all ParserProgressListener interfaces that progress has occurred.
	 */
	protected void fireProgressReported(String description, long partComplete,
			long whole) {
		Object[] listeners = progressListenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserProgressListener.class) {
				((ParserProgressListener) listeners[i + 1])
						.progressReported(description, partComplete, whole);
			}
		}
	}

	//
	// ParserListener methods
	/////////////////////////////////////////////////////////////////////////

	/** List of ParserListeners */
	protected EventListenerList listenerList;

	/**
	 * Adds a <code>ParserListener</code>. The listener will receive events when
	 * the parser interprets music string tokens.
	 *
	 * @param listener
	 *            the listener that is to be notified of parser events
	 */
	public void addParserListener(ParserListener l) {
		listenerList.add(ParserListener.class, l);
	}

	/**
	 * Removes a <code>ParserListener</code>.
	 *
	 * @param listener
	 *            the listener to remove
	 */
	public void removeParserListener(ParserListener l) {
		listenerList.remove(ParserListener.class, l);
	}

	protected void clearParserListeners() {
		EventListener[] l = listenerList.getListeners(ParserListener.class);
		int numListeners = l.length;
		for (int i = 0; i < numListeners; i++) {
			listenerList.remove(ParserListener.class, (ParserListener) l[i]);
		}
	}

	/** Tells all ParserListeners that a voice event has been parsed. */
	protected void fireVoiceEvent(Voice event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).voiceEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a tempo event has been parsed. */
	protected void fireTempoEvent(Tempo event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).tempoEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that an instrument event has been parsed. */
	protected void fireInstrumentEvent(Instrument event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).instrumentEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a layer event has been parsed. */
	protected void fireLayerEvent(Layer event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).layerEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a time event has been parsed. */
	protected void fireTimeEvent(Time event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).timeEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a key signature event has been parsed. */
	protected void fireKeySignatureEvent(KeySignature event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).keySignatureEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a measure event has been parsed. */
	protected void fireMeasureEvent(Measure event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).measureEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a controller event has been parsed. */
	protected void fireControllerEvent(Controller event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).controllerEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a controller event has been parsed. */
	protected void fireChannelPressureEvent(ChannelPressure event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).channelPressureEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a controller event has been parsed. */
	protected void firePolyphonicPressureEvent(PolyphonicPressure event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1])
						.polyphonicPressureEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a controller event has been parsed. */
	protected void firePitchBendEvent(PitchBend event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).pitchBendEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a note event has been parsed. */
	protected void fireNoteEvent(Note event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).noteEvent(event);
			}
		}
	}

	/**
	 * Tells all ParserListeners that a sequential note event has been parsed.
	 */
	protected void fireSequentialNoteEvent(Note event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).sequentialNoteEvent(event);
			}
		}
	}

	/** Tells all ParserListeners that a parallel note event has been parsed. */
	protected void fireParallelNoteEvent(Note event) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ParserListener.class) {
				((ParserListener) listeners[i + 1]).parallelNoteEvent(event);
			}
		}
	}

	//
	// End ParserListener methods
	/////////////////////////////////////////////////////////////////////////
}
