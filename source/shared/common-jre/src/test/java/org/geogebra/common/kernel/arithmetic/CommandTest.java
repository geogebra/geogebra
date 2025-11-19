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
