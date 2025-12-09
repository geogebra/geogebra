/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.CmdAreCollinear;
import org.geogebra.common.kernel.advanced.CmdAreConcurrent;
import org.geogebra.common.kernel.advanced.CmdAreConcyclic;
import org.geogebra.common.kernel.advanced.CmdAreCongruent;
import org.geogebra.common.kernel.advanced.CmdAreEqual;
import org.geogebra.common.kernel.advanced.CmdAreParallel;
import org.geogebra.common.kernel.advanced.CmdArePerpendicular;
import org.geogebra.common.kernel.advanced.CmdEnvelope;
import org.geogebra.common.kernel.advanced.CmdIsTangent;
import org.geogebra.common.kernel.advanced.CmdLocusEquation;
import org.geogebra.common.kernel.advanced.CmdProve;
import org.geogebra.common.kernel.advanced.CmdProveDetails;

/**
 * Factory for prover commands.
 * @see CommandProcessorFactory
 */
public class ProverCommandProcessorFactory implements CommandProcessorFactory {

    @Override
    public CommandProcessor getProcessor(Commands command, Kernel kernel) {
        switch (command) {
        case Prove:
            return new CmdProve(kernel);
        case ProveDetails:
            return new CmdProveDetails(kernel);
        case AreCollinear:
            return new CmdAreCollinear(kernel);
        case IsTangent:
            return new CmdIsTangent(kernel);
        case AreParallel:
            return new CmdAreParallel(kernel);
        case AreConcyclic:
            return new CmdAreConcyclic(kernel);
        case ArePerpendicular:
            return new CmdArePerpendicular(kernel);
        case AreEqual:
            return new CmdAreEqual(kernel);
        case AreCongruent:
            return new CmdAreCongruent(kernel);
        case AreConcurrent:
            return new CmdAreConcurrent(kernel);
        case LocusEquation:
            return new CmdLocusEquation(kernel);
        case Envelope:
            return new CmdEnvelope(kernel);
        default:
            return null;
        }
    }
}
