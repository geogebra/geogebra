package org.geogebra.common.jre.cas.giac;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.error.TimeoutException;
import org.geogebra.common.cas.giac.CASgiacB;
import org.geogebra.common.cas.giac.EvalFunction;
import org.geogebra.common.cas.giac.binding.CASGiacBinding;
import org.geogebra.common.jre.cas.giac.binding.CASGiacBindingJre;
import org.geogebra.common.util.debug.Log;

/**
 * Giac for Desktop and Android
 */
public abstract class CASgiacJre extends CASgiacB {

	/**
	 * @param casParser casParser
	 */
	public CASgiacJre(CASparser casParser) {
		super(casParser);
	}

	@Override
	public CASGiacBinding createBinding() {
		return new CASGiacBindingJre();
	}

	/**
	 * synchronized needed in case CAS called from a thread eg Input Bar preview
	 * eg sin(x)&gt;0
	 */
	@Override
	synchronized protected void callEvaluateFunction(EvalFunction evaluateFunction)
			throws Throwable {
		if (useThread()) {
			// send expression to CAS
			Thread thread = new EvaluateThread(evaluateFunction);

			thread.start();
			thread.join(timeoutMillis);
			thread.interrupt();
			evaluateFunction.cancel();

			// if we haven't got a result, CAS took too long to return
			// eg Solve[sin(5/4 pi+x)-cos(x-3/4 pi)=sqrt(6) *
			// cos(x)-sqrt(2)]
			if (threadResult == null) {
				Log.debug("Thread timeout from Giac");
				throw new TimeoutException("Thread timeout from Giac");
			}
		} else {
			evaluateFunction.run();
		}
	}

	protected abstract boolean useThread();

	class EvaluateThread extends Thread {
		private Runnable evaluateFunction;

		public EvaluateThread(Runnable evaluateFunction) {
			this.evaluateFunction = evaluateFunction;
		}

		@Override
		public void run() {
			try {
				evaluateFunction.run();
			} catch (Throwable t) {
				Log.debug("problem from JNI Giac: " + t.toString());
				// force error in GeoGebra
				threadResult = FORCE_ERROR;
			}
		}
	}
}
