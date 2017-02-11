package org.geogebra.common.kernel;

import org.geogebra.common.kernel.advanced.CmdEnvelope;
import org.geogebra.common.kernel.advanced.CmdLocusEquation;
import org.geogebra.common.kernel.commands.CommandProcessor;

/**
 * needed to separate out LocusEquation stuff into the cas jar (so that minimal
 * applets work without it etc)
 * 
 * @author michael
 * 
 */
public class LocusEquation {

	/**
	 * @param kernel
	 *            kernel
	 * @return processor for Envelope command
	 */
	public static CommandProcessor newCmdEnvelope(Kernel kernel) {
		return new CmdEnvelope(kernel);
	}

	/**
	 * @param kernel
	 *            kernel
	 * @return processor for LocusEquation command
	 */
	public static CommandProcessor newCmdLocusEquation(Kernel kernel) {
		return new CmdLocusEquation(kernel);
	}

}