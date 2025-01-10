package org.geogebra.common.jre.util;

import java.io.UnsupportedEncodingException;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.Reflection;
import org.geogebra.common.util.URLEncoder;
import org.geogebra.regexp.server.JavaRegExpFactory;
import org.geogebra.regexp.shared.RegExpFactory;

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

	@Override
	public URLEncoder newURLEncoder() {
		return urlComponent -> {
			try {
				return java.net.URLEncoder.encode(urlComponent, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// cannot happen, UTF-8 supported everywhere
			}
			return urlComponent;
		};
	}
}
