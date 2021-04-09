package org.geogebra.common.jre.util;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.Reflection;
import org.gwtproject.regexp.server.JavaRegExpFactory;
import org.gwtproject.regexp.shared.RegExpFactory;

public abstract class UtilFactoryJre extends UtilFactory  {

	public UtilFactoryJre() {
		setupRegexFactory();
	}

	public static void setupRegexFactory() {
		RegExpFactory.setPrototypeIfNull(new JavaRegExpFactory());
	}

	@Override
	public Reflection newReflection(Class clazz) {
		return new ReflectionJre(clazz);
	}
}
