package org.geogebra.desktop.plugin;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * https://codeutopia.net/blog/2009/01/02/sandboxing-rhino-in-java/
 */
public class SandboxContextFactory extends ContextFactory {
	@Override
	protected Context makeContext() {
		Context cx = super.makeContext();
		cx.setWrapFactory(new SandboxWrapFactory());
		return cx;
	}
}
