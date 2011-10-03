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
 * This implementation of a ParserListener listens for all events, and funnels
 * them all to the abstract jfugueEvent() method.  Therefore, you can extend
 * this class if you want to create a ParserListener that will handle all
 * JFugue elements in the same way.  For example, this is used by the
 * MusicStringParser to verify the results of a parse.  In this case, all
 * JFugue events are handled in the same way: they're all asked for their
 * verification string.
 *
 * @author David Koelle
 * @version 4.0
 */
public abstract class CollatedParserListener implements ParserListener
{

    public void channelPressureEvent(ChannelPressure channelPressure)
    {
        jfugueEvent(channelPressure);
    }

    public void controllerEvent(Controller controller)
    {
        jfugueEvent(controller);
    }

    public void instrumentEvent(Instrument instrument)
    {
        jfugueEvent(instrument);
    }

    public void keySignatureEvent(KeySignature keySig)
    {
        jfugueEvent(keySig);
    }

    public void layerEvent(Layer layer)
    {
        jfugueEvent(layer);
    }

    public void measureEvent(Measure measure)
    {
        jfugueEvent(measure);
    }

    public void noteEvent(Note note)
    {
        jfugueEvent(note);
    }

    public void parallelNoteEvent(Note note)
    {
        jfugueEvent(note);
    }

    public void pitchBendEvent(PitchBend pitchBend)
    {
        jfugueEvent(pitchBend);
    }

    public void polyphonicPressureEvent(PolyphonicPressure polyphonicPressure)
    {
        jfugueEvent(polyphonicPressure);
    }

    public void sequentialNoteEvent(Note note)
    {
        jfugueEvent(note);
    }

    public void tempoEvent(Tempo tempo)
    {
        jfugueEvent(tempo);
    }

    public void timeEvent(Time time)
    {
        jfugueEvent(time);
    }

    public void voiceEvent(Voice voice)
    {
        jfugueEvent(voice);
    }

    public abstract void jfugueEvent(JFugueElement element);

}
