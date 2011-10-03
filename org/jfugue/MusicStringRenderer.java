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
 * This class is used to build a Pattern (i.e., a MusicString) given a MIDI Sequence. 
 *
 *@author David Koelle
 *@version 3.0
 */
public final class MusicStringRenderer implements ParserListener
{
    private Pattern pattern;
    
    public MusicStringRenderer()
    {
        pattern = new Pattern();
    }

    public Pattern getPattern()
    {
        return this.pattern;
    }
    
    public void voiceEvent(Voice voice)
    {
        pattern.add(voice.getMusicString());
    }
    
    public void instrumentEvent(Instrument instrument)
    {
        pattern.add(instrument.getMusicString());
    }
    
    public void tempoEvent(Tempo tempo)
    {
        pattern.add(tempo.getMusicString());
    }

    public void layerEvent(Layer layer)
    {
        pattern.add(layer.getMusicString());
    }

    public void timeEvent(Time time)
    {
        pattern.add(time.getMusicString());
    }

    public void keySignatureEvent(KeySignature keySig)
    {
        pattern.add(keySig.getMusicString());
    }

    public void measureEvent(Measure measure)
    {
        pattern.add(measure.getMusicString());
    }

    public void controllerEvent(Controller controller)
    {
        pattern.add(controller.getMusicString());
    }

    public void channelPressureEvent(ChannelPressure channelPressure)
    {
        pattern.add(channelPressure.getMusicString());
    }

    public void polyphonicPressureEvent(PolyphonicPressure polyphonicPressure)
    {
        pattern.add(polyphonicPressure.getMusicString());
    }

    public void pitchBendEvent(PitchBend pitchBend)
    {
        pattern.add(pitchBend.getMusicString());
    }

    public void noteEvent(Note note) {
        // Don't use add(note.getMusicString(), because that will incorrectly
        // add a space between sequential or parallel notes.
        
        // Don't add notes that have 0 duration - these indicate a note that 
        // is triggered, but has no duration (TODO: Don't special-case this
        // in the future... maybe add a new noteEvent for notePressed?)
        if (note.getDuration() > 0) {
            pattern.addElement(note);
        }
    }

    public void sequentialNoteEvent(Note note) {
        // We won't get these events from a MIDI parser
    }

    public void parallelNoteEvent(Note note) {
        // We won't get these events from a MIDI parser
    }
}
  
