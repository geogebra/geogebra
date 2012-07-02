package geogebra.util;


import java.util.Iterator;
import java.util.Vector;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.Prover.NDGCondition;

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
	
	private GeoElement getGeoByLabel(String label) {
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.getLabelSimple().equals(label))
				return geo;
		}
		return null;
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
		inputObject.setMethod(GeoGebraOGPInputProverProtocol.OGP_METHOD_WU); // default
		if (AbstractApplication.proverMethod.equalsIgnoreCase("wu"))
			inputObject.setMethod(GeoGebraOGPInputProverProtocol.OGP_METHOD_WU);
		if (AbstractApplication.proverMethod.equalsIgnoreCase("area"))
			inputObject.setMethod(GeoGebraOGPInputProverProtocol.OGP_METHOD_AREA);
		inputObject.setTimeOut(AbstractApplication.proverTimeout);
		inputObject.setMaxTerms(AbstractApplication.maxTerms);
		if (isReturnExtraNDGs())
			inputObject.setReportFormat(GeoGebraOGPInputProverProtocol.OGP_REPORT_FORMAT_ALL);
		else
			inputObject.setReportFormat(GeoGebraOGPInputProverProtocol.OGP_REPORT_FORMAT_NONE);		
		
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
        
        // Obtaining NDG conditions:
        Vector<String> ndgList = outputObject.getNdgList();
        for (String ndgString : ndgList) {
        	int i = ndgString.indexOf("[");
        	NDGCondition ndg = new NDGCondition();
    		ndg.setCondition(ndgString.substring(0, i));
    		String params = ndgString.substring(i+1, ndgString.length()-1);
    		String[] paramsArray = params.split(",");
    		GeoElement[] geos = new GeoElement[paramsArray.length];
    		int j = 0;
    		for (String param : paramsArray) {
    			geos[j] = getGeoByLabel(param.trim());
    			j++;
    		}
    		ndg.setGeos(geos);
    		addNDGcondition(ndg);
        }
        // This would be faster if we could simply get the objects back from OGP as they are.
        
        if (outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_SUCCESS).equals("true")) {
        	if (outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER).equals("true"))
        		return ProofResult.TRUE;
        	if (outputObject.getOutputResult(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER).equals("false"))
        		return ProofResult.FALSE;
        }
		return ProofResult.UNKNOWN;
	}

}

