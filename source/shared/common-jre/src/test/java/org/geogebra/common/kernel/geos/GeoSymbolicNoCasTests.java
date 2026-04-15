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

package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

/**
 * Invariant checks around symbolic definition ownership that do not need CAS-specific setup.
 */
public class GeoSymbolicNoCasTests extends BaseUnitTest {

	@Test
	public void shouldRejectResetDefinition() {
		GeoSymbolic symbolic = newSymbolic("x + 1");

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				symbolic::resetDefinition);

		assertThat(exception.getMessage(), containsString("resetDefinition"));
		assertThat(exception.getMessage(), containsString("missing definition"));
	}

	@Test
	public void shouldRejectSetDefinitionNull() {
		GeoSymbolic symbolic = newSymbolic("x + 1");

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> symbolic.setDefinition(null));

		assertThat(exception.getMessage(), containsString("setDefinition"));
		assertThat(exception.getMessage(), containsString("missing definition"));
	}

	@Test
	public void shouldRejectReuseDefinitionThatWouldDropSymbolicDefinition() {
		GeoSymbolic symbolic = newSymbolic("x + 1");
		GeoNumeric numeric = add("a = 1");
		GeoNumeric dependent = add("b = 2 * a");

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> symbolic.set(dependent));

		assertThat(exception.getMessage(), containsString("reuseDefinition"));
		assertThat(exception.getMessage(), containsString("missing definition"));

		symbolic.set(numeric);
		assertThat(symbolic.getDefinition(StringTemplate.defaultTemplate), containsString("1"));
	}

	private GeoSymbolic newSymbolic(String definition) {
		return new GeoSymbolic(getConstruction(), parseExpression(definition).wrap());
	}
}
