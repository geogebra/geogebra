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
 * EXPERIMENTAL - Contains a variety of static methods that create Patterns that
 * play various musical ornaments
 *
 * @author David Koelle
 * @version 4.0
 */
public class MusicalEffects {
	/**
	 * Returns a Pattern that plays two notes in rapid succession (for a total
	 * of each note being played numHammers times) over the given duration.
	 *
	 * The resulting Pattern will have note1 and note2 both represented
	 * numHammers times.
	 *
	 * Example: hammerOn(new Note("C5"), new Note("E5"), 0.5, 4); will produce
	 * this Pattern: [60]/0.125 [64]/0.125 [60]/0.125 [64]/0.125 [60]/0.125
	 * [64]/0.125 [60]/0.125 [64]/0.125
	 *
	 * @param note1
	 *            First note to play
	 * @param note2
	 *            Second note to play
	 * @param duration
	 *            Value representing total duration for the resulting pattern.
	 *            1.0=whole note
	 * @param numHammers
	 *            Number of times to repeat each note
	 */
	public static Pattern hammerOn(Note note1, Note note2, double duration,
			int numHammers) {
		StringBuilder buddy = new StringBuilder();
		double durationPerHammer = duration / numHammers;
		buddy.append("[");
		buddy.append(note1.getValue());
		buddy.append("]/");
		buddy.append(durationPerHammer / 2.0);
		buddy.append(" [");
		buddy.append(note2.getValue());
		buddy.append("]/");
		buddy.append(durationPerHammer / 2.0);

		Pattern pattern = new Pattern(buddy.toString());
		pattern.repeat(numHammers);
		return pattern;
	}

	/**
	 * Returns a Pattern that plays a slide between two notes over the given
	 * duration.
	 *
	 * TODO: This is currently a naive implementation, which sounds 'numSteps'
	 * notes, each with a duration of 'duration/numSteps'. This means that if
	 * you're sliding from a F to a G, for example, you could get music that
	 * looks like F F F F F F F F G G G G G G, with each note having a very
	 * short duration. The problem with this is that the sound of each note
	 * stopping and starting again is noticeable. A more intelligent
	 * implementation would sound each note for as long as necessary, then sound
	 * a different note only when the microtonal math requires it. Otherwise,
	 * the pitch wheel messages should cause the note to change while it is
	 * playing. This implementation may require one or more new methods in
	 * MicrotoneNotation.
	 */
	public static Pattern slide(Note note1, Note note2, double duration,
			int numSteps) {
		StringBuilder buddy = new StringBuilder();
		double durationPerStep = duration / numSteps;
		double freq1 = Note.getFrequencyForNote(note1.getValue());
		double freq2 = Note.getFrequencyForNote(note2.getValue());
		double differencePerStep = (freq2 - freq1) / numSteps;

		for (int i = 0; i < numSteps; i++) {
			buddy.append(
					MicrotoneNotation.convertFrequencyToMusicString(freq1));
			buddy.append("/");
			buddy.append(durationPerStep);
			buddy.append(MicrotoneNotation.getResetPitchWheelString());
			buddy.append(" ");
			freq1 += differencePerStep;
		}

		Pattern pattern = new Pattern(buddy.toString());
		return pattern;
	}

	/**
	 * Right now, this is a pass-through to hammerOn()
	 * 
	 * @see hammerOn
	 */
	public static Pattern trill(Note note1, Note note2, double duration,
			int numSteps) {
		return hammerOn(note1, note2, duration, numSteps);
	}
}
