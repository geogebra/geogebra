package org.geogebra.web.html5.main;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
