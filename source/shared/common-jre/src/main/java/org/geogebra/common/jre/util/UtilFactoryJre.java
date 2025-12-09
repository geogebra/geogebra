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

	/**
	 * Set regular expression factory prototype.
	 */
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
