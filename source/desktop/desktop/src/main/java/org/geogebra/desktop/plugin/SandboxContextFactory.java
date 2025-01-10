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
