package org.geogebra.common.cas.giac;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.error.TimeoutException;
import org.geogebra.common.cas.giac.binding.CASGiacBinding;
import org.geogebra.common.cas.giac.binding.Context;
import org.geogebra.common.cas.giac.binding.Gen;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

public abstract class CASgiacB extends CASgiac {

    /**
     * store result from Thread here
     */
    private String threadResult;

    private Context context;
    /**
     * @param casParser casParser
     */
    public CASgiacB(CASparser casParser) {
        super(casParser);
    }

    protected abstract CASGiacBinding createBinding();

    @Override
    final public void clearResult() {
        this.threadResult = null;
    }

    /**
     * @param exp0                String to send to Giac
     * @param timeoutMilliseconds timeout in milliseconds
     * @return String from Giac
     */
    final String evalRaw(String exp0, long timeoutMilliseconds) {
        // #5439
        // reset Giac before each call
        init(exp0, timeoutMilliseconds);

        String exp = wrapInevalfa(exp0);

        CASGiacBinding binding = createBinding();
        Gen g = binding.createGen("caseval(" + exp + ")", context);
        Log.debug("giac evalRaw input: " + StringUtil.toJavaString(exp));

        g = g.eval(1, context);
        //String ret = g.print(C);
        String ret = g.print(context);
        Log.debug("giac evalRaw output: " + ret);

        if (ret != null && ret.startsWith("\"") && ret.endsWith("\"")) {
            ret = ret.substring(1, ret.length() - 1);
        }

        return ret;
    }

    private void init(String exp, long timeoutMilliseconds) {
        CASGiacBinding binding = createBinding();

        Gen g = binding.createGen(initString, context);
        g.eval(1, context);

        CustomFunctions[] init = CustomFunctions.values();

        for (int i = 0; i < init.length; i++) {
            CustomFunctions function = init[i];

            // send only necessary init commands
            if (function.functionName == null
                    || exp.indexOf(function.functionName) > -1) {
                g = binding.createGen(function.definitionString, context);
                g.eval(1, context);
                //giac.eval(g, context);
                // Log.debug("sending " + function);
            } else {
                // Log.error("not sending " + function + " "
                // + function.functionName);
            }

            // Log.error(function.functionName + " " +
            // function.definitionString);
        }

        g = binding.createGen("\"timeout " + (timeoutMilliseconds / 1000) + "\"", context);
        g.eval(1, context);
        //giac.eval(g, context);

        // make sure we don't always get the same value!
        int seed = rand.nextInt(Integer.MAX_VALUE);
        g = binding.createGen("srand(" + seed + ")", context);
        g.eval(1, context);
        //giac.eval(g, context);
    }

    /**
     * create context
     */
    final protected void createContext() {
        try {
            CASGiacBinding binding = createBinding();
            context = binding.createContext();
        } catch (UnsatisfiedLinkError e) {
            Log.error("CAS not available: " + e.getMessage());
        }
    }

    @Override
    public String evaluateCAS(String input) {

        // don't need to replace Unicode when sending to JNI
        String exp = casParser.replaceIndices(input, false);

        try {
            return evaluate(exp, timeoutMillis);
        } catch (TimeoutException te) {
            throw te;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return whether to use thread (JNI only)
     */
    abstract protected boolean useThread();

    @Override
    protected String evaluate(String exp, long timeoutMillis0)
            throws Throwable {

        String ret;
        // Log.debug("giac input: " + exp);

        threadResult = null;
        Thread thread;

        if (useThread()) {
            // send expression to CAS
            thread = new GiacJNIThread(exp);

            thread.start();
            thread.join(timeoutMillis0);
            thread.interrupt();
            // thread.interrupt() doesn't seem to stop it, so add this for
            // good measure:
            stopThread(thread);
            // in fact, stop will do nothing (never implemented)
            // Log.debug("giac: after interrupt/stop");

            // if we haven't got a result, CAS took too long to return
            // eg Solve[sin(5/4 pi+x)-cos(x-3/4 pi)=sqrt(6) *
            // cos(x)-sqrt(2)]
            if (threadResult == null) {
                Log.debug("Thread timeout from Giac");
                throw new TimeoutException("Thread timeout from Giac");
            }
        } else {
            threadResult = evalRaw(exp, timeoutMillis0);
        }

        ret = postProcess(threadResult);

        // Log.debug("giac output: " + ret);
        if (ret.contains("user interruption")) {
            Log.debug("Standard timeout from Giac");
            throw new TimeoutException("Standard timeout from Giac");
        }

        return ret;
    }

    /**
     * stop thread. TODO remove it?
     *
     * @param thread thread
     */
    abstract protected void stopThread(Thread thread);

    /**
     * @author michael
     */
    class GiacJNIThread extends Thread {
        private String exp;

        /**
         * @param exp Expression to send to Giac
         */
        public GiacJNIThread(String exp) {
            this.exp = exp;
        }

        @Override
        public void run() {
            // Log.debug("thread starting: " + exp);

            try {
                threadResult = evalRaw(exp, timeoutMillis);

                // Log.debug("message from thread: " + threadResult);
            } catch (Throwable t) {
                Log.debug("problem from JNI Giac: " + t.toString());
                // force error in GeoGebra
                threadResult = "(";
            }
        }
    }

    public boolean externalCAS() {
        return true;
    }


}
