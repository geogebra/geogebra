package org.jfugue;


public abstract class PatternTool extends ParserListenerAdapter
{
    /**
     * Resets the variables in a subclass of PatternTool.  This method
     * is called every time this class's execute() method is called.
     */
    public abstract void reset();

    /**
     * Returns the result of the pattern tool after it has been executed
     * on a pattern.
     * @return the result of executing this pattern tool on a pattern
     */
    public abstract Object getResult();

    /**
     * Indicates what this PatternTool does.
     * @return A String giving a quick description of this tool
     */
    public abstract String getDescription();

    /**
     * Runs the pattern tool and returns the result
     * @param pattern the pattern on which to use this tool
     * @return the result of performing this pattern tool on the given pattern
     */
    public Object execute(Pattern pattern)
    {
        this.reset();

        MusicStringParser parser = new MusicStringParser();
        parser.addParserListener(this);
        try {
            parser.parse(pattern);
        } catch (JFugueException e)
        {
            e.printStackTrace();
        }

        return this.getResult();
    }
}


