package org.geogebra.common.jre.util;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.Reflection;

public abstract class UtilFactoryJre extends UtilFactory  {

	@Override
	public Reflection newReflection(Class clazz) {
		return new ReflectionJre(clazz);
	}
}
