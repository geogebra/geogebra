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

package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.G3D;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;

import java.util.Set;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;

final class NiedersachsenExamRestrictions extends ExamRestrictions {

	NiedersachsenExamRestrictions() {
		super(ExamType.NIEDERSACHSEN,
				Set.of(G3D, SCIENTIFIC),
				GRAPHING,
				null,
				null,
				null,
				Set.of(new CommandNameFilter(true, Commands.CSolve, Commands.CSolutions)),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null);
	}
}
