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

package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CommandTest extends BaseAppTestSetup {

    @ParameterizedTest
    @CsvSource(delimiterString = "->", value = {
            "Integral(cos(t)) -> \\int \\operatorname{cos} \\left( t \\right)\\,\\mathrm{d}t",
            "Integral(cos(t), 1, 2) -> \\int\\limits_{1}^{2}\\operatorname{cos} "
                    + "\\left( t \\right)\\,\\mathrm{d}t",
            "Integral(cos(t), t, 1, 2) -> \\int\\limits_{1}^{2}\\operatorname{cos} "
                    + "\\left( t \\right)\\,\\mathrm{d}t"
    })
    public void testIntegralCommandToLaTeXString(String input, String expectedOutput) {
        setupApp(SuiteSubApp.CAS);
        Command command = parseExpression(input).getTopLevelCommand();
        assertThat(command.toString(StringTemplate.numericLatex), is(expectedOutput));
    }
}
