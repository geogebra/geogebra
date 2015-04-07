/*
 * JFugue - API for Music Programming
 * Copyright (C) 2003-2007  David Koelle
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

import javax.sound.midi.ShortMessage;

/**
 * Assists the StreamingPlayer in converting Patterns to MIDI.
 *
 *@see StreamingPlayer
 *@author David Koelle
 *@version 3.2
 */
public class StreamingMidiRenderer implements ParserListener
{
    private StreamingMidiEventManager eventManager;
    private MusicStringParser parser;
    long initialNoteTime = 0;

    /**
     * Instantiates a Renderer
     */
    public StreamingMidiRenderer()
    {
        this.parser = new MusicStringParser();
        this.parser.addParserListener(this);
        reset();
    }

    /**
     * Creates a new MidiEventManager.  If this isn't called,
     * events from multiple calls to render() will be added
     * to the same eventManager, which means that the second
     * time render() is called, it will contain music left over
     * from the first time it was called.  (This wasn't a problem
     * with Java 1.4)
     * @since 3.0
     */
    public void reset()
    {
        this.eventManager = new StreamingMidiEventManager();
    }

    // ParserListener methods
    ////////////////////////////

    public void voiceEvent(Voice voice)
    {
        this.eventManager.setCurrentTrack(voice.getVoice());
    }

    public void tempoEvent(Tempo tempo)
    {
//        this.parser.setTempo(tempo.getTempo());
    }

    public void instrumentEvent(Instrument instrument)
    {
        this.eventManager.addEvent(ShortMessage.PROGRAM_CHANGE, instrument.getInstrument(), 0);
    }

    public void layerEvent(Layer layer)
    {
        this.eventManager.setCurrentLayer(layer.getLayer());
    }
    
    public void timeEvent(Time time)
    {
        this.eventManager.setTrackTimer(time.getTime());
    }
    
    public void measureEvent(Measure measure)
    {
        // No MIDI is generated when a measure indicator is identified.
    }
    
    public void keySignatureEvent(KeySignature keySig)
    {
        this.eventManager.addMetaMessage(0x59, new byte[] { keySig.getKeySig(), keySig.getScale() });
    }

    public void controllerEvent(Controller controller)
    {
        this.eventManager.addEvent(ShortMessage.CONTROL_CHANGE, controller.getIndex(), controller.getValue());
    }

    public void channelPressureEvent(ChannelPressure channelPressure)
    {
        this.eventManager.addEvent(ShortMessage.CHANNEL_PRESSURE, channelPressure.getPressure());
    }

    public void polyphonicPressureEvent(PolyphonicPressure polyphonicPressure)
    {
        this.eventManager.addEvent(ShortMessage.POLY_PRESSURE, polyphonicPressure.getKey(), polyphonicPressure.getPressure());
    }

    public void pitchBendEvent(PitchBend pitchBend)
    {
        this.eventManager.addEvent(ShortMessage.PITCH_BEND, pitchBend.getBend()[0], pitchBend.getBend()[1]);
    }

    public void noteEvent(Note note)
    {
        // Remember the current track time, so we can flip back to it
        // if there are other notes to play in parallel
        this.initialNoteTime = this.eventManager.getTrackTimer();
        long duration = note.getDuration();
        boolean noteOn = !note.isEndOfTie();
        boolean noteOff = !note.isStartOfTie();

        // Add messages to the track
        if (note.isRest()) {
            this.eventManager.advanceTrackTimer(note.getDuration());
        } else {
            initialNoteTime = eventManager.getTrackTimer();
            byte attackVelocity = note.getAttackVelocity();
            byte decayVelocity = note.getDecayVelocity();
            this.eventManager.addNoteEvents(note.getValue(), attackVelocity, decayVelocity, duration, noteOn, noteOff);
        }
    }

    public void sequentialNoteEvent(Note note)
    {
        throw new UnsupportedOperationException("Sequential notes (declared using an underscore character) are not supported by JFugue's StreamingMidiRenderer");
    }

    public void parallelNoteEvent(Note note)
    {
        long duration = note.getDuration();
        this.eventManager.setTrackTimer(this.initialNoteTime);
        if (note.isRest()) {
            this.eventManager.advanceTrackTimer(note.getDuration());
        } else {
            byte attackVelocity = note.getAttackVelocity();
            byte decayVelocity = note.getDecayVelocity();
            this.eventManager.addNoteEvents(note.getValue(), attackVelocity, decayVelocity, duration, !note.isEndOfTie(), !note.isStartOfTie());
        }
    }
    
    public void close()
    {
        this.eventManager.close();
    }
}
