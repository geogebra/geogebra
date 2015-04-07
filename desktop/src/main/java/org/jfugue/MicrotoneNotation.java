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

import java.util.HashMap;
import java.util.Map;

/**
 * Facilitates playing microtonal music - Indian, Turkish, Indonesian, etc. styles.
 * Also useful for performing slides and other effects that rely on "the notes in between the notes".
 *
 * This feature is covered in detail in "The Complete Guide to JFugue"
 *
 * @author David Koelle
 * @version 3.0
 * @version 4.0 - renamed from MicrotoneHelper; mictoronal notes now encased in angle brackets instead of square brackets, for consistency
 */
public class MicrotoneNotation
{
    private Map<String, Double> keyToFreqDict;
    private Map<String, String> keyToMusicStringDict;

    public MicrotoneNotation()
    {
        keyToFreqDict = new HashMap<String, Double>();
        keyToMusicStringDict = new HashMap<String, String>();
    }

    public void put(String key, double freq)
    {
        keyToFreqDict.put(key, freq);
        keyToMusicStringDict.put(key, convertFrequencyToMusicString(freq));
    }

    public double get(String key)
    {
        return keyToFreqDict.get(key);
    }

    public String getMusicString(String key)
    {
        return keyToMusicStringDict.get(key);
    }

    /**
     * Converts the given frequency to a music string that involves
     * the Pitch Wheel and notes to create the frequency
     * @param freq the frequency
     * @return a MusicString that represents the frequency
     */
    public static String convertFrequencyToMusicString(double freq)
    {
        double totalCents = 1200 * Math.log(freq / 16.3515978312876) / Math.log(2);
        double octave = Math.round(totalCents / 1200.0);
        double semitoneCents = totalCents - (octave * 1200.0);
        double semitone = Math.round(semitoneCents / 100.0);
        double cents = 8192 + Math.round(semitoneCents - (semitone * 100));

        double note = ((octave+1)*12)+semitone; // This gives a MIDI value, 0 - 128
        if (note > 127) note = 127;

        StringBuilder buffy = new StringBuilder();
        buffy.append("&");
        buffy.append((int)cents);
        buffy.append(" [");
        buffy.append((int)note);
        buffy.append("]");
        return buffy.toString();
    }

    public static String getResetPitchWheelString()
    {
        return (" &8192"); // Reset the pitch wheel.  8192 = original pitch wheel position
    }

    public Pattern getPattern(String notation)
    {
        StringBuilder buddy = new StringBuilder();
        String[] tokens = notation.split(" ");

        // Go through the Pattern, and replace known microtone keys with microtone music strings
        for (int i=0; i < tokens.length; i++)
        {
            if ((tokens[i].length() > 0) && (tokens[i].charAt(0) == '<')) {
                int end = tokens[i].indexOf('>');
                String possibleKey = tokens[i].substring(1, end);
                if (keyToMusicStringDict.containsKey(possibleKey))
                {
                    buddy.append(keyToMusicStringDict.get(possibleKey));
                    buddy.append(tokens[i].substring(end+1, tokens[i].length())); // Add the rest of the token
                    buddy.append(getResetPitchWheelString());
                } else {
                    buddy.append(tokens[i]);
                }
            } else {
                buddy.append(tokens[i]);
            }
            buddy.append(" ");
        }

        return new Pattern(buddy.toString());
    }
}
