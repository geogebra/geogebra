package org.geogebra.common.ownership;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Convey to readers that the annotated reference is a non-owning reference
 * (i.e., does not imply ownership).
 *
 * This is relevant information for code reviewers and architectural audits.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NonOwning {
}
