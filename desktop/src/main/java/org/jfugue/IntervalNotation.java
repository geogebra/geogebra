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
 * A IntervalNotation is a MusicString that only contains interval information and durations, not actual notes.
 * A riff is converted into an actual MusicString by applying a root note.
 *
 * @author David Koelle
 * @version 4.0
 */
public class IntervalNotation
{
    private String musicStringWithIntervals;

    public IntervalNotation(String musicStringWithIntervals)
    {
        setMusicStringWithIntervals(musicStringWithIntervals);
    }

    public void setMusicStringWithIntervals(String musicStringWithIntervals)
    {
        this.musicStringWithIntervals = musicStringWithIntervals;
    }

    public String getMusicStringWithIntervals()
    {
        return this.musicStringWithIntervals;
    }


    public Pattern getPatternForRootNote(String musicString)
    {
        return getPatternForRootNote(new Pattern(musicString));
    }

    public Pattern getPatternForRootNote(Pattern pattern)
    {
        Note rootNote = MusicStringParser.getNote(pattern);
        return getPatternForRootNote(rootNote);
    }

    public Pattern getPatternForRootNote(Note rootNote)
    {
        StringBuilder buddy = new StringBuilder();
        String[] tokens = getMusicStringWithIntervals().split(" ");
        byte rootNoteValue = rootNote.getValue();

        // Go through the Pattern, and replace intervals specified within < and > with the root note plus the interval value, minus 1
        for (int i=0; i < tokens.length; i++)
        {
            int lastAngleBracketPosition = -1;
            boolean leftAngleBracketExists = (tokens[i].indexOf('<') != -1);

            if (leftAngleBracketExists) {
                while (leftAngleBracketExists)
                {
                    int start = tokens[i].indexOf('<', lastAngleBracketPosition);
                    int end = tokens[i].indexOf('>', start);
                    String intervalString = tokens[i].substring(start+1, end);
                    byte intervalValue = 0;
                    try {
                        intervalValue = Byte.valueOf(intervalString);
                    } catch (NumberFormatException e)
                    {
                        throw new JFugueException(JFugueException.EXPECTED_BYTE, intervalString, tokens[i]);
                    }

                    buddy.append("[");
                    buddy.append(rootNoteValue + intervalValue - 1);
                    buddy.append("]");

                    lastAngleBracketPosition = end;
                    int nextLeftAngleBracketPosition = tokens[i].indexOf('<', lastAngleBracketPosition-1);
                    if (nextLeftAngleBracketPosition == -1) {
                        buddy.append(tokens[i].substring(end+1, tokens[i].length())); // Add the rest of the token
                        leftAngleBracketExists = false;
                    } else {
                        buddy.append(tokens[i].substring(end+1, nextLeftAngleBracketPosition)); // Add the rest of the token up to the next angle
                        leftAngleBracketExists = true;
                    }
                }
            } else {
                buddy.append(tokens[i]);
            }
            buddy.append(" ");
        }

        return new Pattern(buddy.toString());

    }
}
