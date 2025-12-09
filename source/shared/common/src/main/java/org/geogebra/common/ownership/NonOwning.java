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
