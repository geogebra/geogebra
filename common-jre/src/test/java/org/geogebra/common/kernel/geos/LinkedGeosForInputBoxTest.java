package org.geogebra.common.kernel.geos;

import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.io.FactoryProviderCommon;
import org.junit.Assert;
import org.junit.Test;

public class LinkedGeosForInputBoxTest extends BaseUnitTest {


    @Test
    public void inputboxOutputTest() {
        add("f = x+1");
        add("g = 2f(x+2)+1");
        GeoInputBox inputBox1 = (GeoInputBox) add("InputBox(f)");
        GeoInputBox inputBox2 = (GeoInputBox) add("InputBox(g)");
        inputBox2.setSymbolicMode(true, false);
        Assert.assertEquals("x + 1", inputBox1.getText());
        Assert.assertEquals("2f(x + 2) + 1", inputBox2.getTextForEditor());
}


    @Test
    public void inputboxTextIsInLaTeX() {
        add("f = x + 12");
        add("g = 2f(x + 1) + 2");
        GeoInputBox inputBox2 = (GeoInputBox) add("InputBox(g)");
        inputBox2.setSymbolicMode(true, false);
        FactoryProvider.setInstance(new FactoryProviderCommon());
        TeXFormula teXFormula = new TeXFormula(inputBox2.getText());
        String s = new TeXAtomSerializer(null).serialize(teXFormula.root);
        Assert.assertEquals("2 f(x+1)+2", s);
    }
}
