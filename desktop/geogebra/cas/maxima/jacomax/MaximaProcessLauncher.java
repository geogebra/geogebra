/* $Id: MaximaProcessLauncher.java 22 2010-04-19 16:29:49Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax;

import geogebra.cas.maxima.jacomax.internal.ConstraintUtilities;
import geogebra.cas.maxima.jacomax.internal.DummyLogger;
import geogebra.cas.maxima.jacomax.internal.MaximaBatchProcessImpl;
import geogebra.cas.maxima.jacomax.internal.MaximaInteractiveProcessImpl;
import geogebra.cas.maxima.jacomax.internal.MaximaProcessController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * This is the main entry point into Jacomax.
 * <p>
 * Create one of these Objects using a {@link MaximaConfiguration} to indicate how to run
 * and interact with Maxima. You can then:
 * <ul>
 *   <li>
 *     Use {@link #launchInteractiveProcess()} to create a Maxima process that you can use
 *     in a simple "interactive" mode by sending a number of calls to Maxima and getting
 *     results back.
 *   </li>
 *   <li>
 *     Use {@link #runBatchProcess(InputStream, OutputStream)} to run a Maxima process in
 *     a simple batch mode
 *   </li>
 * </ul>
 * An instance of this class is thread-safe, provided that the {@link MaximaConfiguration}
 * it was created with is not modified.
 * 
 * @author  David McKain
 * @version $Revision: 22 $
 */
public final class MaximaProcessLauncher {

    private static final DummyLogger logger = new DummyLogger();//LoggerFactory.getLogger(MaximaProcessLauncher.class);
    
    /** Default value for {@link MaximaConfiguration#getDefaultCallTimeout()} */
    public static final int DEFAULT_CALL_TIMEOUT = 60;
    
    /** Default value for {@link MaximaConfiguration#getDefaultBatchTimeout()} */
    public static final int DEFAULT_BATCH_TIMEOUT = 180;
    
    /** Default value for {@link MaximaConfiguration#getMaximaCharset()} */
    public static final String DEFAULT_MAXIMA_CHARSET = "US-ASCII";
   
    /** Underlying {@link MaximaConfiguration} used by this launcher */
    private final MaximaConfiguration maximaConfiguration;

    /**
     * Creates a new Maxima process launcher, using the given {@link MaximaConfiguration}
     * to specify how to run and connect to Maxima.
     */
    public MaximaProcessLauncher(MaximaConfiguration maximaConfiguration) {
        ConstraintUtilities.ensureNotNull(maximaConfiguration, "MaximaConfiguration");
        this.maximaConfiguration = maximaConfiguration;
    }

    /**
     * Launches a new {@link MaximaInteractiveProcess} that you can send individual calls
     * to.
     */
    public MaximaInteractiveProcess launchInteractiveProcess() {
        return launchInteractiveProcess(null);
    }
    
    public MaximaInteractiveProcess launchInteractiveProcess(OutputStream maximaStderrHandler) {
        MaximaInteractiveProcessImpl process = new MaximaInteractiveProcessImpl(newMaximaProcessController(maximaStderrHandler),
                computeDefaultTimeout(maximaConfiguration.getDefaultCallTimeout(), DEFAULT_CALL_TIMEOUT),
                computeMaximaCharset());
        process.advanceToFirstInputPrompt();
        logger.info("Maxima interactive process started and ready for communication");
        return process;
    }
    
    /**
     * Runs a Maxima process in a kind of "batch" mode, feeding it data from the given
     * batchInputStream and sending the resulting output to batchOutputStream.
     * <p>
     * The default timeout specified in {@link MaximaConfiguration} is used here.
     * 
     * @param batchInputStream
     * @param batchOutputStream
     * 
     * @throws MaximaTimeoutException if the process exceeded its timeout and had to be killed.
     */
    public void runBatchProcess(InputStream batchInputStream, OutputStream batchOutputStream)
            throws MaximaTimeoutException {
        runBatchProcess(batchInputStream, batchOutputStream, null,
                computeDefaultTimeout(maximaConfiguration.getDefaultBatchTimeout(), DEFAULT_BATCH_TIMEOUT));
    }
    
    public void runBatchProcess(InputStream batchInputStream, OutputStream batchOutputStream, OutputStream batchErrorStream)
            throws MaximaTimeoutException {
        runBatchProcess(batchInputStream, batchOutputStream, batchErrorStream,
                computeDefaultTimeout(maximaConfiguration.getDefaultBatchTimeout(), DEFAULT_BATCH_TIMEOUT));
    }

    /**
     * Runs a Maxima process in a kind of "batch" mode, feeding it data from the given
     * batchInputStream and sending the resulting output to batchOutputStream.
     * <p>
     * The specified timeout (in seconds) is used here. If this value is positive and
     * the batch process has not completed by this time, then the proces is killed and a
     * {@link MaximaTimeoutException} is thrown.
     * 
     * FIXME: What's our policy on closing the streams after completion?
     * 
     * @param batchInputStream
     * @param batchOutputStream
     * @param timeout timeout to use. Zero or less indicates that no timeout should be
     *   applied
     *   
     * @return underlying exit value from the Maxima process
     * 
     * @throws MaximaTimeoutException if the process exceeded its timeout and had to be killed.
     */
    public int runBatchProcess(InputStream batchInputStream, OutputStream batchOutputStream, int timeout)
            throws MaximaTimeoutException {
        return runBatchProcess(batchInputStream, batchOutputStream, null, timeout);
    }
    
    public int runBatchProcess(InputStream batchInputStream, OutputStream batchOutputStream, OutputStream batchErrorStream, int timeout)
            throws MaximaTimeoutException {
        ConstraintUtilities.ensureNotNull(batchInputStream, "batchInputStream");
        ConstraintUtilities.ensureNotNull(batchOutputStream, "batchOutputStream");
        MaximaBatchProcessImpl batchProcess = new MaximaBatchProcessImpl(newMaximaProcessController(batchErrorStream), batchInputStream, batchOutputStream);
        return batchProcess.run(timeout);
    }
    
    //------------------------------------------------------------------------
    
    private Charset computeMaximaCharset() {
        String charset = maximaConfiguration.getMaximaCharset();
        if (charset==null) {
            charset = DEFAULT_MAXIMA_CHARSET;
        }
        try {
            return Charset.forName(charset);
        }
        catch (IllegalCharsetNameException e) {
            throw new JacomaxConfigurationException("Unknown character set " + charset, e);
        }
    }
    
    private int computeDefaultTimeout(int configured, int defaultValue) {
        if (configured > 0) {
            return configured;
        }
        else if (configured==0) {
            return defaultValue;
        }
        else {
            return 0;
        }
    }
    
    private MaximaProcessController newMaximaProcessController(OutputStream maximaStderrHandler) {
        return new MaximaProcessController(this, launchMaximaProcess(), maximaStderrHandler);
    }
    
    private Process launchMaximaProcess() {
        /* Extract relevant configuration required to get Maxima running */
        String maximaExecutablePath = maximaConfiguration.getMaximaExecutablePath();
        String[] maximaCommandArguments = maximaConfiguration.getMaximaCommandArguments();
        String[] maximaRuntimeEnvironment = maximaConfiguration.getMaximaRuntimeEnvironment();
        if (maximaExecutablePath==null) {
            throw new JacomaxConfigurationException("maximaExecutablePath must not be null");
        }
        
        /* Build up the resulting command that we will execute */
        List<String> maximaCommandArray = new ArrayList<String>();
        Pattern windowsMagicPattern = Pattern.compile("^(.+?\\\\Maxima-([\\d.]+))\\\\bin\\\\maxima.bat$");
        Matcher windowsMagicMatcher = windowsMagicPattern.matcher(maximaExecutablePath);
        if (windowsMagicMatcher.matches()) {
            /* (We are actually going to directly call the underlying GCL binary that's bundled with
             * the Windows Maxima EXE, which is a bit of a cheat. The reason we do this is so
             * that the Maxima process can be killed if there's a timeout. Otherwise, we'd just
             * be killing the maxima.bat script, which doesn't actually kill the child process on
             * Windows, leaving an orphaned process causing havoc.
             * 
             * If you don't want to use GCL here, you'll need to specify the exact Lisp runtime
             * you want and the appropriate command line arguments and environment variables.
             * This information can be gleaned from the maxima.bat script itself.)
             */
            logger.info("Replacing configured call to Windows Maxima batch file with call to "
                    + "the underlying GCL binary that comes with vanilla Windows Maxima installs. "
                    + "If you don't want this, please adjust your configuration!");
            String basePath = windowsMagicMatcher.group(1);
            String versionString = windowsMagicMatcher.group(2);
            
            maximaCommandArray.add(basePath + "\\lib\\maxima\\" + versionString + "\\binary-gcl\\maxima.exe");
            maximaCommandArray.add("-eval");
            maximaCommandArray.add("(cl-user::run)");
            maximaCommandArray.add("-f");
            if (maximaCommandArguments.length>0) {
                maximaCommandArray.add("--");
            }
            if (maximaRuntimeEnvironment==null || maximaRuntimeEnvironment.length==0) {
                /* (This makes sure Maxima can find modules and suchlike) */
                maximaRuntimeEnvironment = new String[] { "MAXIMA_PREFIX=" + basePath };
            }
            else {
                logger.warn("I have replaced the maximaExecutablePath in order to invoke the underlying GCL binary."
                        + " I would normally update your maximaRuntimeEnvironment to set MAXIMA_PREFIX"
                        + " but you have already set this, so I'm going with your decision. You may find you"
                        + " need to add a MAXIMA_PREFIX setting to the environment (if you haven't done so already)"
                        + " so that Maxima can find any modules you want to load");
            }
        }
        else {
            maximaCommandArray.add(maximaExecutablePath);
        }
        if (maximaCommandArguments!=null) {
            for (String arg : maximaCommandArguments) {
                maximaCommandArray.add(arg);
            }
        }

        /* Now start the process up */
        Process result;
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Starting Maxima cmdarray {} with environment {}",
                        maximaCommandArray, Arrays.toString(maximaRuntimeEnvironment));
            }
            result = Runtime.getRuntime().exec(maximaCommandArray.toArray(new String[maximaCommandArray.size()]), maximaRuntimeEnvironment);
            logger.debug("Maxima process started");
        }
        catch (IOException e) {
            throw new JacomaxRuntimeException("Could not launch Maxima process", e);
        }
        return result;
    }
}
