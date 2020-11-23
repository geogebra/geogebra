package org.geogebra.common.kernel.commands;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.algos.AlgoDependentGeoCopy;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.error.ErrorHandler;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.himamis.retex.editor.share.util.Unicode;

public class AlgebraProcessorTests extends BaseUnitTest {

    private AlgebraProcessor processor;

    @Mock
    private ErrorHandler errorHandler;

    @Before
    public void setupTest() {
        processor = getKernel().getAlgebraProcessor();
    }

    @Test
    public void testCopyingPlainVariables() {
        EvalInfo info = new EvalInfo(true).withCopyingPlainVariables(true);
        GeoElementND[] elements = processor.processAlgebraCommandNoExceptionHandling(
                "a=1", false, errorHandler, info, null);
        Assert.assertNotNull(elements);
        Assert.assertEquals(elements.length, 1);
        GeoElementND a = elements[0];

        elements = processor.processAlgebraCommandNoExceptionHandling(
                "a", false, errorHandler, info, null);
        Assert.assertNotNull(elements);
        Assert.assertEquals(elements.length, 1);
        GeoElementND b = elements[0];

        Assert.assertNotEquals(a, b);
        AlgoElement parentAlgo = b.getParentAlgorithm();
        Assert.assertNotNull(parentAlgo);
		assertThat(parentAlgo, is(instanceOf(AlgoDependentGeoCopy.class)));
    }

    @Test
    public void testFunctionLikeMultiplication() {
        GeoElement element = add("x(x + 2)");
        assertThat(element, CoreMatchers.<GeoElement>instanceOf(GeoFunction.class));
    }

    @Test
    public void testExceptionThrowing() {
        shouldFail("x y");
        shouldFail("xy");
        shouldFail("a");
        shouldFail("(1,1)");
        shouldFail("x");
        shouldFail("1+" + Unicode.IMAGINARY);
    }

    private void shouldFail(String string) {
        Throwable err = null;
        try {
            processor.convertToDouble(string);
        } catch (Throwable thrown) {
            err = thrown;
        }
        Assert.assertTrue(err instanceof NumberFormatException);
    }

    @Test
    public void testConversion() {
        shouldParseAs("-1", -1);
        shouldParseAs("-1,500", -1.5);
        shouldParseAs("360deg", 2 * Math.PI);
    }

    private void shouldParseAs(String string, double i) {
        Assert.assertEquals(processor.convertToDouble(string), i, DELTA);
    }
}
