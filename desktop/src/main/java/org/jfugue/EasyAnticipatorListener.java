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


public abstract class EasyAnticipatorListener extends ParserListenerAdapter
{
    private Voice activeVoice;
    private Instrument activeInstrument;
    
    public EasyAnticipatorListener()
    {
        activeVoice = new Voice((byte)0);
        activeInstrument = new Instrument((byte)0);
    }
    
    private int tempo;
    
    public void tempoEvent(Tempo tempo)
    {
        this.tempo = tempo.getTempo();
        System.out.println("tempo = "+tempo.getTempo());
    }

    public void voiceEvent(Voice voice)
    {
        this.activeVoice = voice;
    }

    public void instrumentEvent(Instrument instrument)
    {
        this.activeInstrument = instrument;
    }

    public void noteEvent(Note note)
    {
        extendedNoteEvent(activeVoice, activeInstrument, note);
    }
    
    public void parallelNoteEvent(Note note)
    {
        extendedNoteEvent(activeVoice, activeInstrument, note);
    }
    
    public void sequentialNoteEvent(Note note)
    {
        extendedNoteEvent(activeVoice, activeInstrument, note);
//        sleep(note.getDuration());
    }

    /** Duration is in PPQ, need to translate that into msec */
    // TODO: Is duration ALWAYS in PPQ, or does it depend on sequenceTiming?
    private void sleep(long durationInPPQ)
    {
        try {
            long msec = ((durationInPPQ / 4 ) ); 
            Thread.sleep(msec);
        } catch (InterruptedException e)
        {    
            throw new JFugueException(JFugueException.ERROR_SLEEP);            
        }
    }
    
    public abstract void extendedNoteEvent(Voice voice, Instrument instrument, Note note);
}
