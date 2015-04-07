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

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

/**
 * Parses MIDI data, whether from a file, a connected device, or some other stream.
 * 
 * @version 4.0.3 - A Note event with 0 duration is now sent when a note is first encountered
 */
public final class MidiParser extends Parser
{
    long[][] tempNoteRegistry = new long[16][255];
    byte[][] tempNoteAttackRegistry = new byte[16][255];
    int tempo;
    private static final int DEFAULT_TEMPO = 120;

    public MidiParser()
    {
        this.tempo = DEFAULT_TEMPO;

        // Create a two dimensional array of bytes [ track, note ] - when a NoteOn event is found,
        // populate the proper spot in the array with the note's start time.  When a NoteOff event 
        // is found, new Time and Note objects are constructed and added to the composition
        for (int m=0; m < 16; m++) {
            for (int n=0; n < 255; n++) {
                tempNoteRegistry[m][n] = 0L;
                tempNoteAttackRegistry[m][n] = (byte)0;
            }
        }
    }

    /**
     * Parses a <code>Sequence</code> and fires events to subscribed <code>ParserListener</code>
     * interfaces.  As the Sequence is parsed, events are sent
     * to <code>ParserListener</code> interfaces, which are responsible for doing
     * something interesting with the music data, such as adding notes to a pattern.
     *
     * @param sequence the <code>Sequence</code> to parse
     * @throws Exception if there is an error parsing the pattern
     */
    public void parse(Sequence sequence) 
    {
        this.tempo = DEFAULT_TEMPO;

        // Get the MIDI tracks from the sequence.  Expect a maximum of 16 tracks.
        Track[] tracks = sequence.getTracks();

        // Compute the size of this adventure for the ParserProgressListener
        long totalCount = 0;
        long counter = 0;
        for (byte i=0; i < tracks.length; i++)
        {
            totalCount += tracks[i].size();
        }

        
        // And now to parse the MIDI!
        for (int t = 0; t < tracks.length; t++)
        {
            int trackSize = tracks[t].size();
            if (trackSize > 0)
            {
                fireVoiceEvent(new Voice((byte)t)); 

                for (int ev = 0; ev < trackSize; ev++)
                {
                    counter++;
                    fireProgressReported("Parsing MIDI...", counter, totalCount);
                    
                    MidiEvent event = tracks[t].get(ev);
                    MidiMessage message = event.getMessage();

                    trace("Message received: ",message);
                    parse(message, event.getTick());
                }
            }
        }
    }

    /** 
     * Delegator method that calls specific parsers depending on the
     * type of MidiMessage passed in.
     * @param message the message to parse
     * @param timestamp the time at which the message was encountered in this track
     */
    public void parse(MidiMessage message, long timestamp)
    {
        if (message instanceof ShortMessage)
        {
            parseShortMessage((ShortMessage)message, timestamp);
        }
        else if (message instanceof SysexMessage)
        {
            parseSysexMessage((SysexMessage)message, timestamp);
        }
        else if (message instanceof MetaMessage)
        {
            parseMetaMessage((MetaMessage)message, timestamp);
        }
    }
    
    /**
     * Parses instances of ShortMessage. 
     * @param message The message to parse
     * @param timestamp the time at which the message was encountered in this track
     */
    private void parseShortMessage(ShortMessage message, long timestamp)
    {
        int track = message.getChannel();
        
        switch (message.getCommand())
        {
            case ShortMessage.PROGRAM_CHANGE :                  // 0xC0, 192
                trace("Program change to ",message.getData1());
                Instrument instrument = new Instrument((byte)message.getData1());
                fireTimeEvent(new Time(timestamp));
                fireVoiceEvent(new Voice((byte)track));
                fireInstrumentEvent(instrument);
                break;
                
            case ShortMessage.CONTROL_CHANGE :                  // 0xB0, 176
                trace("Controller change to ",message.getData1(),", value = ",message.getData2());                                    
                Controller controller = new Controller((byte)message.getData1(), (byte)message.getData2());
                fireTimeEvent(new Time(timestamp));
                fireVoiceEvent(new Voice((byte)track));
                fireControllerEvent(controller);
                break;
            case ShortMessage.NOTE_ON :                         // 0x90, 144
                if (message.getData2() == 0) {
                    // A velocity of zero in a note-on event is a note-off event
                    noteOffEvent(timestamp, track, message.getData1(), message.getData2());
                } else {
                    noteOnEvent(timestamp, track, message.getData1(), message.getData2());
                }
                break;
            case ShortMessage.NOTE_OFF :                        // 0x80, 128
                noteOffEvent(timestamp, track, message.getData1(), message.getData2());
                break;
            case ShortMessage.CHANNEL_PRESSURE :                // 0xD0, 208
                trace("Channel pressure, pressure = ",message.getData1());                                    
                ChannelPressure pressure = new ChannelPressure((byte)message.getData1());
                fireTimeEvent(new Time(timestamp));
                fireVoiceEvent(new Voice((byte)track));
                fireChannelPressureEvent(pressure);
                break;
            case ShortMessage.POLY_PRESSURE :                   // 0xA0, 128
                trace("Poly pressure on key ",message.getData1(),", pressure = ",message.getData2());                                    
                PolyphonicPressure poly = new PolyphonicPressure((byte)message.getData1(), (byte)message.getData2());
                fireTimeEvent(new Time(timestamp));
                fireVoiceEvent(new Voice((byte)track));
                firePolyphonicPressureEvent(poly);
                break;
            case ShortMessage.PITCH_BEND :                      // 0xE0, 224
                trace("Pitch Bend, data1= ",message.getData1(),", data2= ",message.getData2());                                    
                PitchBend bend = new PitchBend((byte)message.getData1(), (byte)message.getData2());
                fireTimeEvent(new Time(timestamp));
                fireVoiceEvent(new Voice((byte)track));
                firePitchBendEvent(bend);
                break;
            default : 
                trace("Unparsed message: ",message.getCommand());
                break;
        }
    }

    private void noteOnEvent(long timestamp, int track, int data1, int data2)
    {
        trace("Note on ",data1," - attack is ",data2);
        tempNoteRegistry[track][data1] = timestamp;
        tempNoteAttackRegistry[track][data1] = (byte)data2;

        // Added 9/27/2008 - fire a Note with duration 0 to signify a that a Note was pressed 
        Note note = new Note((byte)data1, 0);
        note.setDecimalDuration(0);
        note.setAttackVelocity((byte)data2);
        fireNoteEvent(note);
    }
    
    private void noteOffEvent(long timestamp, int track, int data1, int data2)
    {
        long time = tempNoteRegistry[track][data1];
        trace("Note off ",data1," - decay is ",data2,". Duration is ",(timestamp - time));

        fireTimeEvent(new Time(time));
        fireVoiceEvent(new Voice((byte)track));
        Note note = new Note((byte)data1, (long)(timestamp - time));
        note.setDecimalDuration((double)((timestamp - time) / (this.tempo * 4.0D)));
        note.setAttackVelocity(tempNoteAttackRegistry[track][data1]);
        note.setDecayVelocity((byte)data2);
        fireNoteEvent(note);
        tempNoteRegistry[track][data1] = 0L;
    }
        
    /**
     * Parses instances of SysexMessage. 
     * @param message The message to parse
     * @param timestamp the time at which the message was encountered in this track
     */
    private void parseSysexMessage(SysexMessage message, long timestamp)
    {
        // Nothing to do - JFugue doesn't use sysex messages
        trace("SysexMessage received but not parsed by JFugue (doesn't use them)");
    }

    /**
     * Parses instances of MetaMessage. 
     * @param message The message to parse
     * @param timestamp the time at which the message was encountered in this track
     */
    private void parseMetaMessage(MetaMessage message, long timestamp)
    {
        switch (message.getType())
        {
          case 0x51 : parseTempo(message, timestamp); break;
          case 0x59 : break; // Even though we care about Key Signatures, we don't want to read one in from a MIDI file,
                             // because the notes that we'll receive will already be adjusted for the key signature.
                             // MIDI's Key Signature is more about notating sheet music that influencing the played notes.
        default : break;
        }
        // Nothing to do - JFugue doesn't use sysex messages
        trace("MetaMessage received but not parsed by JFugue (doesn't use them)");
    }
    
    private void parseTempo(MetaMessage message, long timestamp)
    {
        int beatsPerMinute = TimeFactor.parseMicrosecondsPerBeat(message, timestamp);
        trace("Tempo Event, bpm = ",beatsPerMinute);
        fireTimeEvent(new Time(timestamp));
        fireTempoEvent(new Tempo(beatsPerMinute));
        this.tempo = beatsPerMinute;
    }
}
