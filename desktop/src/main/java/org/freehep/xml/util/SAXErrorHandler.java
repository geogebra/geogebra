/*
 * SAXErrorHandler.java
 *
 * Created on February 14, 2001, 4:38 PM
 */

package org.freehep.xml.util;
import org.geogebra.desktop.main.AppD;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A simple SAXErrorHandler. Reports errors to System.err and keeps track
 * of the most severe error. Can be configured to throw exceptions when errors
 * more severe than a certain LEVEL are encountered
 * @author  tonyj
 * @version $Id: SAXErrorHandler.java,v 1.5 2008-10-23 19:04:05 hohenwarter Exp $
 */
public class SAXErrorHandler implements ErrorHandler
{
    public final static int LEVEL_SUCCESS = 0;
    public final static int LEVEL_WARNING = 1;
    public final static int LEVEL_ERROR = 2;
    public final static int LEVEL_FATAL = 3;
    
    /**
     * Create a SAXErrorHandler which will throw exceptions for all errors (but not warnings)
     */
    public SAXErrorHandler()
    {
        this(LEVEL_ERROR);
    }
    /**
     * Create a SAXErrorHandler
     * @param minLevelForException The minimum error level for which exceptions will be thrown 
     */
    public SAXErrorHandler(int minLevelForException)
    {
        this.minLevel = minLevelForException;
    }
    public void warning(SAXParseException exception) throws org.xml.sax.SAXException
    {
        handle(exception,LEVEL_WARNING,"Warning");
    }
    public void error(SAXParseException exception) throws org.xml.sax.SAXException
    {
        handle(exception,LEVEL_ERROR,"Error");
    }
    public void fatalError(SAXParseException exception) throws SAXException
    {
        handle(exception,LEVEL_FATAL,"Fatal");
    }
    private void handle(SAXParseException exception, int level, String levelName) throws SAXException
    {
        StringBuffer message = new StringBuffer(levelName);
        String fileName = exception.getPublicId();
        if (fileName == null) fileName = exception.getSystemId();
        if (fileName != null) message.append(" at "+fileName);
        message.append(" line "+exception.getLineNumber());
        if (level > maxLevel) maxLevel = level;
        if (level >= minLevel) throw new BadXMLException(message.toString(),exception);
        else 
        {
            message.append(": "+exception);
            AppD.debug(message+""); 
        }
        
    }
    /**
     * Get the level of the most severe error.
     * @return one of LEVEL_SUCCESS, LEVEL_WARNING, LEVEL_ERROR, LEVEL_FATAL
     */
    public int getErrorLevel()
    {
        return maxLevel;
    }
    private int maxLevel = 0;
    private int minLevel;
}

