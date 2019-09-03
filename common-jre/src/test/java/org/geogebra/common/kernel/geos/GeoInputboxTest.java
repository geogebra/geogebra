package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;

public class GeoInputboxTest extends BaseUnitTest {


    @Test
    public void inputboxTest() {
        add("f = x+1");
        add("g = 2f(x+2)+1");
        GeoInputBox inputBox1 = (GeoInputBox) add("InputBox(f)");
        GeoInputBox inputBox2 = (GeoInputBox) add("InputBox(g)");
        inputBox2.setSymbolicMode(true, false);
        Assert.assertEquals(inputBox1.getText(), "x + 1");
        Assert.assertEquals(inputBox2.getTextForEditor(), "2f(x + 2) + 1");
    }
}
