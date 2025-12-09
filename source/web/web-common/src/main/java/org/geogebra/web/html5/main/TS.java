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

package org.geogebra.web.html5.main;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Type annotation for generating TypeScript type definition.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TS {
	String CLIENT_LISTENER = "(evt: ClientEvent) => void";
	String OBJECT_LISTENER = "(label:string) => void";
	String VOID_FUNCTION = "() => void";
	String OPTIONAL_BOOL = "?boolean";
	String OPTIONAL_STRING = "?string";

	/**
	 * The returned type may start with ?, in that case arg:?type needs transforming to arg?:type
	 * @return typescript type definition
	 */
	String value();
}
