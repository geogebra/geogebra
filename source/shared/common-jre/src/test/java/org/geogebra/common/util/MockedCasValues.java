package org.geogebra.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify mocked input-output pairs for CAS calculations
 * in test methods. Each pair represents a CAS input and its corresponding mocked output.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockedCasValues {
	/**
	 * An array of strings representing CAS input-output pairs. Each string should contain
	 * exactly one CAS input and mocked output separated by the specified delimiter.
	 * <p>
	 * Example: {@code "Evaluate(5) -> 5"}
	 * @return Array of CAS input-output pairs in the format {@code "input -> output"}
	 */
	String[] value();

	/**
	 * The delimiter used to separate CAS input from mocked output in the value strings.
	 * @return the delimiter string used to split input and output values
	 */
	String delimiter() default "->";
}
