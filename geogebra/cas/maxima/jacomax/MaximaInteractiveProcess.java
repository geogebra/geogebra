/* $Id: MaximaInteractiveProcess.java 21 2010-04-19 16:17:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax;

/**
 * Handle on an "interactive" Maxima process, as created using
 * {@link MaximaProcessLauncher#launchInteractiveProcess()}.
 * <p>
 * Use this if you want to send a series of commands (or calls) to Maxima
 * for it to evaluate and (possibly) return results.
 * <p>
 * Call {@link #terminate()} to terminate and clean up
 * the Maxima process once you have finished using it. Any further calls
 * made will result in a {@link MaximaProcessTerminatedException} being thrown.
 * <p>
 * Calls are executed with a timeout (specified in seconds), which can be passed
 * explicitly of defaulted in various ways. If the timeout is greater than zero and
 * the call has not completed in the allotted time, the underlying Maxima process is
 * terminated and a {@link MaximaTimeoutException} is thrown. Any further calls made
 * will generate a {@link MaximaProcessTerminatedException}. If the timeout is zero
 * or less, then calls are allowed to run indefinitely. (This should be used
 * with caution!)
 * <p>
 * An instance of this class should only be used by one thread at a time. 
 *
 * @author  David McKain
 * @version $Revision: 21 $
 */
public interface MaximaInteractiveProcess {
    
    public static final int PROCESS_ALREADY_TERMINATED = -1;
    public static final int PROCESS_FORCIBLY_DESTROYED = -2;
    
    /**
     * Returns the default call timeout for this process.
     */
    int getDefaultCallTimeout();
    
    /**
     * Sets the default call timeout for this process.
     * @param defaultCallTimeout
     */
    void setDefaultCallTimeout(int defaultCallTimeout);
    
    /**
     * Executes the given Maxima code, waiting for Maxima to finish evaluating
     * it and returning the raw output.
     * <p>
     * If the call times out, then {@link #terminate()} will be called and a 
     * {@link MaximaTimeoutException} is thrown.
     * 
     * @param maximaInput Maxima code to call. This should include any required
     *   terminator characters (e.g. <tt>;</tt> or <tt>$</tt>) as expected by Maxima.
     *   We also support calls containing <tt>:lisp</tt>, which are expected to end
     *   with a <tt>)</tt> character.
     *   
     * @return raw Maxima output
     * 
     * @throws IllegalArgumentException if maximaInput is null or does not appear to
     *   end with a terminator that can be safely handled.
     * @throws MaximaTimeoutException if the default timeout is positive and Maxima
     *   did not return a result within this time.
     * @throws MaximaProcessTerminatedException if the Maxima process has already
     *   been terminated, either by calling {@link #terminate()}, because a
     *   {@link MaximaTimeoutException} previously occurred or because a previous
     *   call failed to execute.
     */
    String executeCall(String maximaInput)
        throws MaximaTimeoutException;
    
    /**
     * Version of {@link #executeCall(String)} that uses the given timeout instead
     * of the current default.
     * 
     * @param maximaInput
     * @param callTimeout
     * 
     * @throws IllegalArgumentException
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException
     */
    String executeCall(String maximaInput, int callTimeout)
        throws MaximaTimeoutException;
    
    /**
     * Version of {@link #executeCall(String)} that throws away the output from Maxima.
     * (This is marginally more efficient than calling {@link #executeCall(String)} and
     * then throwing away the output yourself.)
     * 
     * @param maximaInput
     * 
     * @throws IllegalArgumentException
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException
     */
    void executeCallDiscardOutput(String maximaInput)
        throws MaximaTimeoutException;
    
    /**
     * Version of {@link #executeCallDiscardOutput(String)} that takes a custom timeout.
     * 
     * @param maximaInput
     * @param callTimeout
     * 
     * @throws IllegalArgumentException
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException
     */
    void executeCallDiscardOutput(String maximaInput, int callTimeout)
        throws MaximaTimeoutException;
    
    /**
     * Performs a "soft reset" of the process by calling
     * <tt>[kill(all),reset()];</tt>, which has the effect of clearing up
     * <em>most</em> things that you might have done. Consult the Maxima documentation
     * for more details.
     * 
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException
     */
    void softReset()
        throws MaximaTimeoutException;
    
    /**
     * Returns whether or not this process has been terminated due to a call to
     * {@link #terminate()}, or because of a timeout, or due to a previous call
     * failing.
     * 
     * @return true if the process has been terminated, false otherwise.
     */
    boolean isTerminated();
    
    /**
     * Terminates the underlying Maxima process, forcibly if required. No
     * more calls can be made to this process after this point.
     * <p>
     * Calling this on a process that has already terminated will do nothing.
     * 
     * @return underlying exit value from the Maxima process, {@link #PROCESS_ALREADY_TERMINATED}
     *    if the process was already terminated, or {@link #PROCESS_FORCIBLY_DESTROYED} 
     *    if the process had to be forcibly destroyed.
     */
    int terminate();

}
