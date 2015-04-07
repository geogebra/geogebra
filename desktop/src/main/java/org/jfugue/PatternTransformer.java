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
 * This class is used to transform a pattern.  Extend this class to create your own
 * PatternTransformer, which
 * listens to parser events and can modify the events that are fired off by the parser.
 * Some sample
 * PatternTransformer subclasses are packaged with JFugue; refer to those to see examples
 * of transformers in action.
 *
 * This feature is covered in detail in The Complete Guide to JFugue.
 *
 * @see org.jfugue.extras.DiatonicIntervalPatternTransformer
 * @see org.jfugue.extras.DurationPatternTransformer
 * @see org.jfugue.extras.IntervalPatternTransformer
 * @see org.jfugue.extras.ReversePatternTransformer
 * @author David Koelle
 * @version 2.0
 */
public class PatternTransformer implements ParserListener
{
    /**
     * Contains the pattern to return at the end of the transformation.
     * As of version 4.0, this variable is private.  Use the protected methods
     * getReturnPattern() and setReturnPattern() to access the return pattern.
     */
    private Pattern returnPattern;

    /**
     * Returns the pattern that the transformer is modifying
     * @version 4.0
     */
    protected Pattern getReturnPattern()
    {
        return returnPattern;
    }

    /**
     * Sets the pattern that the transformer is modifying
     * @version 4.0
     */
    protected void setReturnPattern(Pattern pattern)
    {
        this.returnPattern = pattern;
    }

    /** Transforms the pattern, according to the event method that you have
     *  presumably extended.
     */
    public Pattern transform(Pattern p)
    {
        setReturnPattern(new Pattern());
        MusicStringParser parser = new MusicStringParser();
        parser.addParserListener(this);
        try {
            parser.parse(p);
        } catch (JFugueException e)
        {
            e.printStackTrace();
        }
        return getReturnPattern();
    }

    /** Extend this method to make your transformer modify the voice. */
    public void voiceEvent(Voice voice)
    {
        returnPattern.addElement(voice);
    }

    /** Extend this method to make your transformer modify the tempo. */
    public void tempoEvent(Tempo tempo)
    {
        returnPattern.addElement(tempo);
    }

    /** Extend this method to make your transformer modify the instrument. */
    public void instrumentEvent(Instrument instrument)
    {
        returnPattern.addElement(instrument);
    }

    /** Extend this method to make your transformer modify the layer. */
    public void layerEvent(Layer layer)
    {
        returnPattern.addElement(layer);
    }

    /** Extend this method to make your transformer modify the time. */
    public void timeEvent(Time time)
    {
        returnPattern.addElement(time);
    }

    /** Extend this method to make your transformer modify the time. */
    public void keySignatureEvent(KeySignature keySig)
    {
        returnPattern.addElement(keySig);
    }

    /** Extend this method to make your transformer modify the measure. */
    public void measureEvent(Measure measure)
    {
        returnPattern.addElement(measure);
    }

    /** Extend this method to make your transformer modify the controller messages. */
    public void controllerEvent(Controller controller)
    {
        returnPattern.addElement(controller);
    }

    /** Extend this method to make your transformer modify the channel pressure messages. */
    public void channelPressureEvent(ChannelPressure channelPressure)
    {
        returnPattern.addElement(channelPressure);
    }

    /** Extend this method to make your transformer modify the polyphonic pressure messages. */
    public void polyphonicPressureEvent(PolyphonicPressure polyphonicPressure)
    {
        returnPattern.addElement(polyphonicPressure);
    }

    /** Extend this method to make your transformer modify the pitch bend messages. */
    public void pitchBendEvent(PitchBend pitchBend)
    {
        returnPattern.addElement(pitchBend);
    }

    /** Extend this method to make your transformer modify the note.
     *  Don't forget to also extend sequentialNoteEvent and parallelNoteEvent.
     */
    public void noteEvent(Note note)
    {
        returnPattern.addElement(note);
    }

    /** Extend this method to make your transformer modify the note.
     *  Don't forget to also extend noteEvent and parallelNoteEvent.
     */
    public void sequentialNoteEvent(Note note)
    {
        returnPattern.addElement(note);
    }

    /** Extend this method to make your transformer modify the note.
     *  Don't forget to also extend noteEvent and sequentialNoteEvent.
     */
    public void parallelNoteEvent(Note note)
    {
        returnPattern.addElement(note);
    }
}

