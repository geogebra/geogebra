package geogebra.util;


import geogebra.common.kernel.StringTemplate;
import geogebra.common.main.AbstractApplication;

import com.ogprover.api.GeoGebraOGPInterface;
import com.ogprover.main.OGPConfigurationSettings;
import com.ogprover.main.OGPParameters;
import com.ogprover.main.OpenGeoProver;
import com.ogprover.pp.GeoGebraOGPInputProverProtocol;
import com.ogprover.pp.GeoGebraOGPOutputProverProtocol;
import com.ogprover.utilities.logger.ILogger;

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

        OpenGeoProver.settings = new OGPConfigurationSettings();
        ILogger logger = OpenGeoProver.settings.getLogger();
		
		// Input prover object
		GeoGebraOGPInputProverProtocol inputObject = new GeoGebraOGPInputProverProtocol();
		inputObject.setGeometryTheoremText(c);
		inputObject.setMethod(GeoGebraOGPInputProverProtocol.OGP_METHOD_WU);
		inputObject.setTimeOut(AbstractApplication.proverTimeout);
		inputObject.setMaxTerms(AbstractApplication.maxTerms);
		
        // OGP API
        GeoGebraOGPInterface ogpInterface = new GeoGebraOGPInterface();
        GeoGebraOGPOutputProverProtocol outputObject = (GeoGebraOGPOutputProverProtocol)ogpInterface.prove(inputObject); // safe cast
		
        AbstractApplication.debug("Prover results");
        AbstractApplication.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_SUCCESS + ": " + outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_SUCCESS));
        AbstractApplication.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_FAILURE_MSG + ": " + outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_FAILURE_MSG));
        AbstractApplication.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER + ": " + outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER));
        AbstractApplication.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER_MSG + ": " + outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER_MSG));
        AbstractApplication.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_TIME + ": " + outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_TIME));
        AbstractApplication.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_NUMTERMS + ": " + outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_NUMTERMS));
        
        if (outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_SUCCESS).equals("true")) {
        	if (outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER).equals("true"))
        		return ProofResult.TRUE;
        	if (outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER).equals("false"))
        		return ProofResult.FALSE;
        }
		return ProofResult.UNKNOWN;
	}

}

