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

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public final class IntelligentDeviceResolver
{
    public static MidiDevice selectReceiverDevice() throws MidiUnavailableException
    {
        return selectDevice("midi", "usb", "out");
    }
    
    public static MidiDevice selectTransmitterDevice() throws MidiUnavailableException
    {
        return selectDevice("midi", "usb", "in");
    }
    
    public static MidiDevice selectDevice(String... keywords) throws MidiUnavailableException
    {
        int bestMatch = 0;
        int thisMatch = 0;
        MidiDevice device = null;
        
        MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
        for (int i=0; i < info.length; i++)
        {
            String infoString = info[i].toString().toLowerCase();
            thisMatch = 0;
            for (int k=0; k < keywords.length; k++)
            {
                if (infoString.contains(keywords[k]))
                {
                    thisMatch++;
                }
            }
            if (thisMatch > bestMatch)
            {
                device = MidiSystem.getMidiDevice(info[i]);
                bestMatch = thisMatch;
            }
        }
        
        if (device == null)
        {
            throw new JFugueException(JFugueException.INTELLIGENT_RESOLVER_FAILED);
        }

        return device;
    }
}
