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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides the ability to build a rhythm using a simple strings in which
 * individual characters represent MusicString elements.
 *
 * For example, you can develop a drum beat that looks like this:
 * <code>
 * oo'' o'  oo'' o'  oo'' o'  oo'' o'...
 * </code>
 *
 * As of JFugue 4.0, the Rhythm allows the addition of voices outside of the
 * percussion track using the addVoice() method.
 *
 * This feature is covered in detail in "The Complete Guide to JFugue"
 *
 *@see Player
 *@author David Koelle
 *@version 3.0
 *@version 4.0.3 - Now implements Serializable
 */
public class Rhythm implements Serializable
{
    private Map<Character, String> charToNote;
    private String[] layers;
    private String[] voices;
    private String[] voiceDetails;
    private int MAX_LAYERS = 127;
    private int MAX_VOICES = 16;
    private int PERCUSSION_TRACK = 9;

    public Rhythm()
    {
        charToNote = new HashMap<Character, String>();
        layers = new String[MAX_LAYERS];
        voices = new String[MAX_VOICES];
        voiceDetails = new String[MAX_VOICES];
    }

    public void addSubstitution(char stringChar, String musicString)
    {
        charToNote.put(stringChar, musicString);
    }

    public String getSubstitution(char stringChar)
    {
        return charToNote.get(stringChar);
    }

    public void removeSubstitution(char stringChar)
    {
        charToNote.remove(stringChar);
    }

    public void setLayer(int layer, String rhythmString)
    {
        if ((layer < 0) || (layer > MAX_LAYERS)) {
            throw new JFugueException(JFugueException.LAYER_EXC, Integer.toString(layer), rhythmString);
        }
        layers[layer] = rhythmString;
    }

    public String getLayer(int layer)
    {
        return this.layers[layer];
    }

    public void clearLayer(int layer)
    {
        this.layers[layer] = null;
    }

    public void setVoice(int voice, String rhythmString)
    {
        if ((voice < 0) || (voice > MAX_LAYERS) || (voice == PERCUSSION_TRACK)) {
            throw new JFugueException(JFugueException.VOICE_EXC, Integer.toString(voice), rhythmString);
        }
        voices[voice] = rhythmString;
    }

    public String getVoice(int voice)
    {
        return this.voices[voice];
    }

    public void clearVoice(int voice)
    {
        this.voices[voice] = null;
    }

    public void setVoiceDetails(int voice, String musicString)
    {
        if ((voice < 0) || (voice > MAX_LAYERS) || (voice == PERCUSSION_TRACK)) {
            throw new JFugueException(JFugueException.VOICE_EXC, Integer.toString(voice), musicString);
        }
        voiceDetails[voice] = musicString;
    }

    public String getVoiceDetails(int voice)
    {
        return this.voiceDetails[voice];
    }

    public void clearVoiceDetails(int voice)
    {
        this.voiceDetails[voice] = null;
    }


    public String getMusicString()
    {
        StringBuffer buffy = new StringBuffer();

        // Start by adding the percussion track
        buffy.append("V9 ");
        for (int i=0; i < MAX_LAYERS; i++)
        {
            String rhythmString = getLayer(i);
            if (rhythmString != null) {
                buffy.append("L");
                buffy.append(i);
                buffy.append(" ");
                for (int r=0; r < rhythmString.length(); r++)
                {
                    char ch = rhythmString.charAt(r);
                    String substitution = getSubstitution(ch);
                    if (substitution != null) {
                        buffy.append(substitution);
                        buffy.append(" ");
                    }
                }
            }
        }

        // Add the voices
        for (int i=0; i < MAX_VOICES; i++)
        {
            boolean voiceCommandAdded = false;

            String detailsString = getVoiceDetails(i);
            if (detailsString != null) {
                buffy.append("V");
                buffy.append(i);
                voiceCommandAdded = true;
                buffy.append(" ");
                buffy.append(detailsString);
                buffy.append(" ");
            }
            String rhythmString = getVoice(i);
            if (rhythmString != null) {
                if (!voiceCommandAdded) {
                    buffy.append("V");
                    buffy.append(i);
                    buffy.append(" ");
                }
                for (int r=0; r < rhythmString.length(); r++)
                {
                    char ch = rhythmString.charAt(r);
                    String substitution = getSubstitution(ch);
                    if (substitution != null) {
                        buffy.append(substitution);
                        buffy.append(" ");
                    }
                }
            }
        }

        return buffy.toString();
    }

    public Pattern getPattern()
    {
        return new Pattern(getMusicString());
    }

    public Pattern getPatternWithInterval(Pattern pattern)
    {
        IntervalNotation interval = new IntervalNotation(getMusicString());
        return interval.getPatternForRootNote(pattern);
    }

    public Pattern getPatternWithInterval(String string)
    {
        IntervalNotation interval = new IntervalNotation(getMusicString());
        return interval.getPatternForRootNote(string);
    }

    public Pattern getPatternWithInterval(Note rootNote)
    {
        IntervalNotation interval = new IntervalNotation(getMusicString());
        return interval.getPatternForRootNote(rootNote);
    }

}
