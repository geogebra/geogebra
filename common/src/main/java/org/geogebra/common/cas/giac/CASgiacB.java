package org.geogebra.common.cas.giac;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.error.TimeoutException;
import org.geogebra.common.cas.giac.binding.CASGiacBinding;
import org.geogebra.common.cas.giac.binding.Context;
import org.geogebra.common.cas.giac.binding.Gen;
import org.geogebra.common.util.debug.Log;

public abstract class CASgiacB extends CASgiac {

    /**
     * Giac's context.
     */
    private Context context;
    protected String threadResult;

    public CASgiacB(CASparser casParser) {
        super(casParser);
        createContext();
    }

    protected abstract CASGiacBinding createBinding();

    protected void createContext() {
        try {
            CASGiacBinding binding = createBinding();
            context = binding.createContext();
        } catch (Throwable e) {
            Log.error("CAS not available: " + e.getMessage());
        }
    }

    @Override
    final public void clearResult() {
        threadResult = null;
    }

    /**
     * @param exp0                String to send to Giac
     * @param timeoutMilliseconds timeout in milliseconds
     * @return String from Giac
     */
    final String evalRaw(String exp0, long timeoutMilliseconds) {
        CASGiacBinding binding = createBinding();
        // #5439
        // reset Giac before each call
        init(exp0, timeoutMilliseconds);

        String exp = wrapInevalfa(exp0);


        Gen g = binding.createGen("caseval(" + exp + ")", context);
        g = g.eval(1, context);
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
            }
        }

        g = binding.createGen("\"timeout " + (timeoutMilliseconds / 1000) + "\"", context);
        g.eval(1, context);

        // make sure we don't always get the same value!
        int seed = rand.nextInt(Integer.MAX_VALUE);
        g = binding.createGen("srand(" + seed + ")", context);
        g.eval(1, context);
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

    @Override
    protected String evaluate(final String exp, final long timeoutMillis0)
            throws Throwable {
        Runnable evalFunction = new Runnable() {
            @Override
            public void run() {
                threadResult = evalRaw(exp, timeoutMillis0);
            }
        };

        threadResult = null;

        callEvaluateFunction(evalFunction);

        String ret = postProcess(threadResult);

        // Log.debug("giac output: " + ret);
        if (ret.contains("user interruption")) {
            Log.debug("Standard timeout from Giac");
            throw new TimeoutException("Standard timeout from Giac");
        }

        return ret;
    }

    protected abstract void callEvaluateFunction(Runnable evaluateFunction) throws Throwable;

    public boolean externalCAS() {
        return true;
    }
}
