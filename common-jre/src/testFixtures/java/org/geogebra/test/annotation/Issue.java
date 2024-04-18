package org.geogebra.test.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test case to a bug filed in the issue tracker.
 * Based on <a href="https://github.com/jenkinsci/lib-test-annotations">Jenkins test annotations</a>
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Issue {

	/**
	 * @return Issue ID, such as APPS-123 or GGB-456.
	 */
	String[] value();
}
