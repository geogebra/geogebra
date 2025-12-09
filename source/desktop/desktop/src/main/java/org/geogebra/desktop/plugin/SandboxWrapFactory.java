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

package org.geogebra.desktop.plugin;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

/**
 * https://codeutopia.net/blog/2009/01/02/sandboxing-rhino-in-java/
 *
 */
public class SandboxWrapFactory extends WrapFactory {
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope,
			Object javaObject, Class staticType) {
		return new SandboxNativeJavaObject(scope, javaObject, staticType);
	}
}
