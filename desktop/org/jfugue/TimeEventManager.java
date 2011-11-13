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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

/**
 * Takes the events in a MIDI sequence and places them into a time-based
 * map.  This is done so the events can be played back in order of when 
 * the events occur, regardless of the tracks they happen to be in.  This is
 * useful when sending events to an external device, or any occasion 
 * when iterating through the tracks is not useful because the tracks would be
 * played sequentially rather than in parallel.
 *  
 * @author David Koelle
 * @version 3.0
 */
public final class TimeEventManager 
{
    public static final long sortSequenceByTimestamp(Sequence sequence, Map<Long, List<MidiEvent>> timeMap)
    {
        // Keep track of how long the sequence is
        long longestTime = 0;
        
        // Iterate through the tracks, and store the events into our time map
        Track[] tracks = sequence.getTracks();
        for (int i=0; i < tracks.length; i++)
        {
            for (int e=0; e < tracks[i].size(); e++)
            {
                // Get MIDI message and time data from event
                MidiEvent event = tracks[i].get(e);
                long timestamp = event.getTick();

                // Put the MIDI message into the time map
                List<MidiEvent> list = null;
                if ((list = (ArrayList<MidiEvent>)timeMap.get(timestamp)) == null)
                {
                    // Add a new list to the map if one doesn't already exist 
                    // for the timestamp in question
                    list = new ArrayList<MidiEvent>();
                    timeMap.put(timestamp, list);
                } 
                list.add(event);
                
                // Update the longest time known, if required
                if (timestamp > longestTime)
                {
                    longestTime = timestamp;
                }
            }
        }
        
        return longestTime;
    }
    
    /**
     * Returns the events from this sequence in temporal order.  This is
     * done in a two step process:
     * 1. mapSequence() populates timeMap.  Each timestamp key in timeMap is mapped to
     *    a List of events that take place at that time
     * 2. A list of all events from all timestamps is created and returned
     * @return The events from the sequence, in temporal order
     */
    public static final List<MidiEvent> getAllEventsSortedByTimestamp(Sequence sequence)
    {
        Map<Long, List<MidiEvent>> timeMap = new HashMap<Long, List<MidiEvent>>();
        long longestTime = sortSequenceByTimestamp(sequence, timeMap);
        
        List<MidiEvent> totalList = new ArrayList<MidiEvent>();
        
        for (long l=0; l < longestTime; l++)
        {
            Long key = new Long(l);
            if (timeMap.containsKey(key))
            {
                List<MidiEvent> list = (List<MidiEvent>)timeMap.get(key);
                totalList.addAll(list);
            }
        }
        
        return totalList;
    }
}
