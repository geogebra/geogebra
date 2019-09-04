
package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;

public class LinkedGeosForInputBoxTest extends BaseUnitTest {


    @Test
    public void symbolicFunctionTest() {
        add("f = x+1");
        add("g = 2f(x+2)+1");
        GeoElement inputBox1 = add("InputBox(f)");
        GeoElement inputBox2 = add("InputBox(g)");
        Assert.assertEquals("x + 1", ((GeoInputBox) inputBox1).getText());
        Assert.assertEquals("2f(x + 2) + 1", ((GeoInputBox)inputBox2).getText());
    }
}
