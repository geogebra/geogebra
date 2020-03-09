package org.geogebra.common.kernel.commands;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class GraphingCommandArgumentFilterTest extends BaseUnitTest {

    @Test(expected = NullPointerException.class)
    public void vhenExceptionThrownAnotherTry() {
       getApp().setGraphingConfig();
       addAvInput("Line((1,2), x)");
    }
}
