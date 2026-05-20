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

package org.geogebra.common.jre.plugin;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.geogebra.common.jre.headless.GgbAPIHeadless;
import org.geogebra.common.util.debug.Log;

/**
 * Utility class for JRE-based scripting sandbox.
 */
public final class ScriptUtil {

	private static final Set<String> ALLOWED = Set.of(
			Timer.class.getName(), // for https://gist.github.com/murkle/f4d0c02aa595f404df143d0bd31b6b88
			TimerTask.class.getName(),
			String.class.getName(),
			Boolean.class.getName(),
			Byte.class.getName(),
			Short.class.getName(),
			Integer.class.getName(),
			Long.class.getName(),
			Float.class.getName(),
			Double.class.getName(),
			Character.class.getName(),
			Number.class.getName(),
			Math.class.getName(),
			Object.class.getName(),
			CharSequence.class.getName(),
			GgbAPIHeadless.class.getName(),
			"adapter1"); // also needed for timer

	private ScriptUtil() {
		// utility class
	}

	/**
	 * @param fullClassName full class name
	 * @return whether this is a common class available in scripts
	 */
	public static boolean isVisibleToScripts(String fullClassName) {
		if (ALLOWED.contains(fullClassName)) {
			if (!fullClassName.contains("geogebra")) {
				Log.debug("Scripting: allowed using class " + fullClassName);
			}
			return true;
		}
		Log.debug("Scripting: blocked using class " + fullClassName);
		return false;
	}
}
