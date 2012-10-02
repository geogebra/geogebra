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
 * Calculates the length of the given pattern, in milliseconds
 * 
 *@author David Koelle
 *@version 2.0
 *@version 4.0.2 - Changed from returning PPQ to milliseconds
 * 
 */
public class DurationPatternTool extends ParserListenerAdapter
{
    private byte activeVoice = 0;
    private long voiceDuration[];

    public DurationPatternTool()
    {
        reset();
    }
    
    public void voiceEvent(Voice voice)
    {
        this.activeVoice = voice.getVoice();
    }

    // Only look at the first Note events, not parallel or sequential ones.
    public void noteEvent(Note note)
    {
        long duration = note.getDuration();
        this.voiceDuration[this.activeVoice] += duration;
    }

    public void reset()
    {
        voiceDuration = new long[16];
        for (int i=0; i < 16; i++) {
            voiceDuration[i] = 0L;
        }
    }

    public long getDuration()
    {
        long returnDuration = 0L;
        for (int i=0; i < 16; i++) {
            if (voiceDuration[i] > returnDuration) {
                returnDuration = voiceDuration[i];
            }
        }

        return returnDuration;
    }
}

