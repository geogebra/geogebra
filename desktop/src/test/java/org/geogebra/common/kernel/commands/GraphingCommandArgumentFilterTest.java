package org.geogebra.common.kernel.commands;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class GraphingCommandArgumentFilterTest extends BaseUnitTest {

    @Test(expected = NullPointerException.class)
    public void parallelLineWithPointandLine() {
       getApp().setGraphingConfig();
       addAvInput("A = (1,1)");
       addAvInput("B = (2,2)");
       addAvInput("C = (3,2)");
       addAvInput("f:Line(B,C)");
       addAvInput("g:Line(A,f)");
    }

    @Test(expected = NullPointerException.class)
    public void parallelLineWithPointandFunction() {
        getApp().setGraphingConfig();
        addAvInput("A = (1,2)");
        addAvInput("f(x) = x");
        addAvInput("g:Line(A,f)");
    }
}
