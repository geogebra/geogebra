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
 
package org.geogebra.common.gui.view.algebra.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class FunctionAndEquationFilterTest extends BaseUnitTest {

    private final FunctionAndEquationFilter filter = new FunctionAndEquationFilter();

    @Test
    public void testIsAllowed() {
        GeoElement f = addAvInput("f(x) = x");
        assertThat(filter.isAllowed(f), is(true));
    }

    @Test
    public void testRemoveUndefinedIsAllowedCommandWithinFunction() {
        GeoElement poly = addAvInput("FitPoly(RemoveUndefined({(1,2),(3,4),(2,1)}),2)");
        assertThat(filter.isAllowed(poly), is(true));
    }
}