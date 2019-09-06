package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;

public class GeoInputBoxTest extends BaseUnitTest {

    @Test
    public void symbolicInputBoxUseDefinitionForFunctions() {
        add("f = x+1");
        add("g = 2f(x+2)+1");
        GeoInputBox inputBox1 = (GeoInputBox) add("InputBox(f)");
        GeoInputBox inputBox2 = (GeoInputBox) add("InputBox(g)");
        inputBox2.setSymbolicMode(true, false);
        Assert.assertEquals("x + 1", inputBox1.getText());
        Assert.assertEquals("2f(x + 2) + 1", inputBox2.getTextForEditor());
    }

    @Test
    public void symbolicInputBoxTextShouldBeInLaTeX() {
        add("f = x + 12");
        add("g = 2f(x + 1) + 2");
        GeoInputBox inputBox2 = (GeoInputBox) add("InputBox(g)");
        inputBox2.setSymbolicMode(true, false);
        Assert.assertEquals("2 \\; f\\left(x + 1 \\right) + 2", inputBox2.getText());
    }
}
