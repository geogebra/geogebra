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
import org.mozilla.javascript.ContextFactory;

/**
 * https://codeutopia.net/blog/2009/01/02/sandboxing-rhino-in-java/
 */
public class SandboxContextFactory extends ContextFactory {
	private static ContextFactory instance;

	@Override
	protected Context makeContext() {
		Context cx = super.makeContext();
		cx.setWrapFactory(new SandboxWrapFactory());
		return cx;
	}

	/**
	 * @return singleton instance
	 */
	public static ContextFactory getInstance() {
		if (instance == null) {
			instance = new SandboxContextFactory();
		}
		return instance;
	}
}
