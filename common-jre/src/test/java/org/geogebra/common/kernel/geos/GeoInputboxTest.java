package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;

public class GeoInputboxTest extends BaseUnitTest {


    @Test
    public void inputboxTest() {
        add("f = x+1");
        add("g = 2f(x+2)+1");
        GeoElement inputBox1 = add("InputBox(f)");
        GeoElement inputBox2 = add("InputBox(g)");
        Assert.assertEquals(((GeoInputBox) inputBox1).getText(), "x + 1");
        Assert.assertEquals(((GeoInputBox)inputBox2).getText(), "2f(x + 2) + 1");
    }
}
