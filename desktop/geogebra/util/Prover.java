package geogebra.util;


import geogebra.common.kernel.StringTemplate;
import geogebra.common.main.AbstractApplication;

import com.ogprover.main.OGPConfigurationSettings;
import com.ogprover.main.OGPParameters;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Implements desktop dependent parts of the Prover 
 */
public class Prover extends geogebra.common.util.Prover {

	/**
	 * Starts computation of the proof, based on the defined
	 * subsystem.
	 */
	/* This code works in JVM only. */
	private class computeThread implements Runnable {
		public computeThread() {
		}
		public void run() {
			// Display info about this particular thread
			AbstractApplication.debug(Thread.currentThread() + " running");
			decideStatement();
		}
	}
	
	@Override
	public void compute() {
		if (AbstractApplication.proverTimeout == 0) {
			// Do not create a thread if there is no timeout set:
		    decideStatement();
		    // This is especially useful for debugging in Eclipse.
		    return;
		}
		result = ProofResult.UNKNOWN;
		Thread t = new Thread(new computeThread(), "compute");
		long startTime = System.currentTimeMillis();
		t.start();
		int i = 0;
		while (t.isAlive()) {
			AbstractApplication.debug("Waiting for the prover: " + i++);
			try {
				t.join(50);
			} catch (InterruptedException e) {
				return;
			}
			if (((System.currentTimeMillis() - startTime) > timeout * 1000L)
	                  && t.isAlive()) {
	                AbstractApplication.debug("Prover timeout");
	                t.interrupt();
	                // t.join(); // http://docs.oracle.com/javase/tutorial/essential/concurrency/simple.html
	                return;
	            }
		}
	}
	
	@Override
	protected ProofResult openGeoProver() {
		AbstractApplication.debug("OGP is about to run...");
		String c = simplifiedXML(construction);
		AbstractApplication.trace("Construction: " + c);
		// getCASString may also be used 
		String cd = statement.getCommandDescription(StringTemplate.ogpTemplate);
		AbstractApplication.debug("Statement to prove: " + cd);
		OGPConfigurationSettings ogpcs = new OGPConfigurationSettings();
		ogpcs.setMaxNumOfTerms(AbstractApplication.maxTerms);
		OGPParameters ogpp = new OGPParameters();
		// TODO: Call OGP with the needed parameters.
		return ProofResult.UNKNOWN;
	}

}

