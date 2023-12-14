package org.geogebra.common.ownership;

/**
 * Convey to readers that the annotated reference is a non-owning reference
 * (i.e., does not imply ownership).
 *
 * This is relevant information for code reviewers and architectural audits.
 */
public @interface NonOwning {
}
