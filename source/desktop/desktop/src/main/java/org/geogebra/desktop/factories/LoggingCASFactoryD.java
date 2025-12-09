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

package org.geogebra.desktop.factories;

import java.util.HashMap;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.cas.giac.CASgiacD;

public class LoggingCASFactoryD extends CASFactory {
	private static HashMap<String, String> rawResponses = new HashMap<>();

	@Override
	public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
		return new CASgiacD(parser) {
			private String lastInput;

			@Override
			protected void debug(String prefix, String giacString) {
				if (prefix.contains("input")) {
					lastInput = giacString;
				} else {
					rawResponses.put(lastInput,
							StringUtil.toJavaString(giacString));
					lastInput = null;
				}
				Log.debug(prefix + giacString);
			}
		};
	}

}
