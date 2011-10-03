
package org.apache.log4j;

import java.util.logging.Level;


/**
 * Logger adapter for log4j to java logger. 
 * It provides log4j Logger methods used in JAS.
 * @author Heinz Kredel.
 */
public class Logger {

    // logger object
    final java.util.logging.Logger logger;

    // name of class to log
    final String className;


    /**
     * Create Logger object.
     * @param logger Logger object.
     * @param name name of class to use logging.
     */
    protected Logger(java.util.logging.Logger logger, String name) {
        this.logger = logger; 
        this.className = name;
        this.logger.setLevel(Level.WARNING);
    }


    /**
     * Get Logger object.
     * @param clazz class object used in JAS and log4j.
     * @return Logger object.
     */
    public static Logger getLogger(Class clazz) {
        return getLogger( clazz.getName() ); 
    }


    /**
     * Get Logger object.
     * @param name class name (not used in JAS and log4j).
     * @return Logger object.
     */
    public static Logger getLogger(String name) {
        return new Logger( java.util.logging.Logger.getLogger(name), name );
    }


    /**
     * Test if debug logging is enabled.
     * @return true if debug level is enabled.
     */
    public boolean isDebugEnabled() {
        return logger.isLoggable( java.util.logging.Level.FINE );
    }


    /**
     * Test if info logging is enabled.
     * @return true if info level is enabled.
     */
    public boolean isInfoEnabled() {
        return logger.isLoggable( java.util.logging.Level.INFO );
    }


    /**
     * Log debug message.
     * @param msg message to log.
     */
    public void debug( Object msg ) {
        debug( "" + msg );
    }


    /**
     * Log debug message.
     * @param msg message to log.
     */
    public void debug( String msg ) {
        //logger.fine( msg );
        logger.logp( java.util.logging.Level.FINE, className, null, msg );
    }


    /**
     * Log info message.
     * @param msg message to log.
     */
    public void info( Object msg ) {
        info( "" + msg );
    }


    /**
     * Log info message.
     * @param msg message to log.
     */
    public void info( String msg ) {
        //logger.info( msg );
        logger.logp( java.util.logging.Level.INFO, className, null, msg );
    }


    /**
     * Log info message.
     * @param msg message to log.
     * @param e Exception object.
     */
    public void info( String msg , Exception e) {
        //logger.info( msg );
        logger.logp( java.util.logging.Level.INFO, className, null, msg );
    }


    /**
     * Log warn message.
     * @param msg message to log.
     */
    public void warn( Object msg ) {
        warn( "" + msg );
    }


    /**
     * Log warn message.
     * @param msg message to log.
     */
    public void warn( String msg ) {
        //logger.warning( msg );
        logger.logp( java.util.logging.Level.WARNING, className, null, msg );
    }


    /**
     * Log error message.
     * @param msg message to log.
     */
    public void error( Object msg ) {
        error( "" + msg );
    }


    /**
     * Log error message.
     * @param msg message to log.
     */
    public void error( String msg ) {
        //logger.severe( msg );
        logger.logp( java.util.logging.Level.SEVERE, className, null, msg );
    }

}