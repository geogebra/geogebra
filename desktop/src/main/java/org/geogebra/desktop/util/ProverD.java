package org.geogebra.desktop.util;

import java.util.Iterator;
import java.util.Vector;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.kernel.prover.ProverReciosMethodD;

import com.ogprover.api.GeoGebraOGPInterface;
import com.ogprover.main.OGPConfigurationSettings;
import com.ogprover.main.OpenGeoProver;
import com.ogprover.pp.GeoGebraOGPInputProverProtocol;
import com.ogprover.pp.GeoGebraOGPOutputProverProtocol;

/**
 * Implements desktop dependent parts of the Prover.
 * 
 * @author Zoltan Kovacs
 * 
 */
public class ProverD extends Prover {

	/**
	 * Starts computation of the proof, based on the defined subsystem.
	 */
	/* This code works in JVM only. */
	private class computeThread implements Runnable {
		protected computeThread() {
		}

		@Override
		public void run() {
			// Display info about this particular thread
			Log.debug(Thread.currentThread() + " running");
			decideStatement();
		}
	}

	@Override
	public void compute() {
		if (ProverSettings.get().proverTimeout == 0) {
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
			Log.debug("Waiting for the prover: " + i++);
			try {
				t.join(50);
			} catch (InterruptedException e) {
				return;
			}
			if (((System.currentTimeMillis() - startTime) > getTimeout()
					* 1000L)
					&& t.isAlive()) {
				Log.debug("Prover timeout");
				t.interrupt();
				// t.join(); //
				// http://docs.oracle.com/javase/tutorial/essential/concurrency/simple.html
				return;
			}
		}
	}

	private GeoElement getGeoByLabel(String label) {
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.getLabelSimple().equals(label)) {
				return geo;
			}
		}
		return null;
	}

	@Override
	protected ProofResult openGeoProver(ProverEngine pe) {
		Log.debug("OGP is about to run...");
		String c = simplifiedXML(getConstruction(), statement).replace(
				"command name=\"ProveDetails\"", "command name=\"Prove\""); // dirty
																			// hack,
																			// FIXME
		Log.trace("Construction: " + c);
		// String cd =
		// statement.getCommandDescription(StringTemplate.ogpTemplate);
		// Log.debug("Statement in the XML should be: " + cd);

		OpenGeoProver.settings = new OGPConfigurationSettings();
		ProverSettings proverSettings = ProverSettings.get();
		// Input prover object
		GeoGebraOGPInputProverProtocol inputObject = new GeoGebraOGPInputProverProtocol();
		inputObject.setGeometryTheoremText(c);
		inputObject.setMethod(GeoGebraOGPInputProverProtocol.OGP_METHOD_WU); // default
		if (pe == ProverEngine.OPENGEOPROVER_WU) {
			inputObject.setMethod(GeoGebraOGPInputProverProtocol.OGP_METHOD_WU);
		}
		if (pe == ProverEngine.OPENGEOPROVER_AREA) {
			inputObject
					.setMethod(GeoGebraOGPInputProverProtocol.OGP_METHOD_AREA);
		}
		inputObject.setTimeOut(proverSettings.proverTimeout);
		inputObject.setMaxTerms(proverSettings.getMaxTerms());
		if (isReturnExtraNDGs()) {
			inputObject.setReportFormat(
					GeoGebraOGPInputProverProtocol.OGP_REPORT_FORMAT_ALL);
		} else {
			inputObject.setReportFormat(
					GeoGebraOGPInputProverProtocol.OGP_REPORT_FORMAT_NONE);
		}

		// OGP API
		GeoGebraOGPInterface ogpInterface = new GeoGebraOGPInterface();
		GeoGebraOGPOutputProverProtocol outputObject = (GeoGebraOGPOutputProverProtocol) ogpInterface
				.prove(inputObject); // safe cast

		Log.debug("Prover results");
		Log.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_SUCCESS + ": "
				+ outputObject.getOutputResult(
						GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_SUCCESS));
		Log.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_FAILURE_MSG
				+ ": " + outputObject.getOutputResult(
						GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_FAILURE_MSG));
		Log.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER + ": "
				+ outputObject.getOutputResult(
						GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER));
		Log.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER_MSG
				+ ": " + outputObject.getOutputResult(
						GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER_MSG));
		Log.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_TIME + ": "
				+ outputObject.getOutputResult(
						GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_TIME));
		Log.debug(GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_NUMTERMS + ": "
				+ outputObject.getOutputResult(
						GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_NUMTERMS));

		// Obtaining NDG conditions:
		if (isReturnExtraNDGs()) {
			Vector<String> ndgList = outputObject.getNdgList();
			for (String ndgString : ndgList) {
				int i = ndgString.indexOf("[");
				NDGCondition ndg = new NDGCondition();
				String ndgCommand = ndgString.substring(0, i);
				String params = ndgString.substring(i + 1,
						ndgString.length() - 1);
				String[] paramsArray = params.split(",");
				GeoElement[] geos = new GeoElement[paramsArray.length];
				int j = 0;
				for (String param : paramsArray) {
					// TODO: This is not really fast, improve this somehow:
					geos[j] = getGeoByLabel(param.trim());
					if (geos[j] == null) {
						// We don't want to show such objects which cannot be
						// detected by GeoGebra:
						return ProofResult.TRUE_NDG_UNREADABLE;
					}
					j++;
				}
				if ("IsOnCircle".equals(ndgCommand)) {
					ndgCommand = "IsIsoscelesTriangle";
					// IsOnCircle[A,B,C]: AB==AC
					// IsIsoscelesTriangle[A,B,C]: AB==BC
					GeoElement swap = geos[1];
					geos[1] = geos[0];
					geos[0] = swap;
				}
				ndg.setCondition(ndgCommand);
				ndg.setGeos(geos);
				addNDGcondition(ndg);
			}
		}
		// This would be faster if we could simply get the objects back from OGP
		// as they are.

		if (outputObject
				.getOutputResult(
						GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_SUCCESS)
				.equals("true")) {
			if (outputObject
					.getOutputResult(
							GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER)
					.equals("true")) {
				return ProofResult.TRUE;
			}
			if (outputObject
					.getOutputResult(
							GeoGebraOGPOutputProverProtocol.OGP_OUTPUT_RES_PROVER)
					.equals("false")) {
				return ProofResult.FALSE;
			}
		}
		return ProofResult.UNKNOWN;
	}

	@Override
	protected AbstractProverReciosMethod getNewReciosProver() {
		return new ProverReciosMethodD();
	}

}
