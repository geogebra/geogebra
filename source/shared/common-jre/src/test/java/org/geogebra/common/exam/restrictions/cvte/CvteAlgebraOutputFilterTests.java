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

package org.geogebra.common.exam.restrictions.cvte;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTestSetup;
import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CvteAlgebraOutputFilterTests extends BaseExamTestSetup {

    @BeforeEach
    public void setup() {
        setupApp(SuiteSubApp.GRAPHING);
    }

    @Test
    public void testAlgebraOutputRestrictions() {
        AlgebraOutputFilter outputFilter = new CvteAlgebraOutputFilter(null);

        // For Lines, Rays, Conics, Implicit Equations and Functions created with command or tool,
        // we do not show the calculated equation.
        assertFalse(outputFilter.isAllowed(evaluateGeoElement("Line((0, 0), (1, 2))")));
        assertFalse(outputFilter.isAllowed(evaluateGeoElement("Ray((0, 0), (1, 2))")));
        assertFalse(outputFilter.isAllowed(evaluateGeoElement("Circle((0, 0), 1)")));
        // implicit curve
        assertFalse(outputFilter.isAllowed(evaluateGeoElement(
                "FitImplicit((1...10,(1/(1...10))),3)")));
        // functions: any of the FitPoly / FitLog / ... commands
        assertFalse(outputFilter.isAllowed(evaluateGeoElement(
                "f(x)=FitPoly({(-2,1),(-1,0),(0,1),(1,0)},3)")));

        //  Lines, Rays, Conics, Implicit Equations and Functions created from manual input
        // line
        assertTrue(outputFilter.isAllowed(evaluateGeoElement("x = y")));
        // conic
        assertTrue(outputFilter.isAllowed(evaluateGeoElement("x^2 + y^2 = 4")));
        // implicit equation
        assertTrue(outputFilter.isAllowed(evaluateGeoElement("x^3 + y = 0")));
        // function
        assertTrue(outputFilter.isAllowed(evaluateGeoElement("x")));
    }
}
