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

public abstract class EasyAnticipatorListener extends ParserListenerAdapter {
	private Voice activeVoice;
	private Instrument activeInstrument;

	public EasyAnticipatorListener() {
		activeVoice = new Voice((byte) 0);
		activeInstrument = new Instrument((byte) 0);
	}

	private int tempo;

	@Override
	public void tempoEvent(Tempo tempo) {
		this.tempo = tempo.getTempo();
		System.out.println("tempo = " + tempo.getTempo());
	}

	@Override
	public void voiceEvent(Voice voice) {
		this.activeVoice = voice;
	}

	@Override
	public void instrumentEvent(Instrument instrument) {
		this.activeInstrument = instrument;
	}

	@Override
	public void noteEvent(Note note) {
		extendedNoteEvent(activeVoice, activeInstrument, note);
	}

	@Override
	public void parallelNoteEvent(Note note) {
		extendedNoteEvent(activeVoice, activeInstrument, note);
	}

	@Override
	public void sequentialNoteEvent(Note note) {
		extendedNoteEvent(activeVoice, activeInstrument, note);
		// sleep(note.getDuration());
	}

	public abstract void extendedNoteEvent(Voice voice, Instrument instrument,
			Note note);
}
