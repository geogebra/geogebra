package geogebra.io;
import java.util.Locale;

import javax.swing.JFrame;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.main.AppD;
import org.junit.Assert;
import org.junit.Test;
public class SerializationTest {
	
	@Test
	public void testSerializationSpeed(){
		AppD app = new AppD(new CommandLineArguments(
				new String[]{"--silent"}), new JFrame(), false);
		app.setLanguage(Locale.US);
		long l = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder(1000);
		FunctionVariable fv = new FunctionVariable(app.getKernel());
		ExpressionNode n = fv.wrap().plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv);
		for(int i = 0;i<100000;i++){
			sb.append(n.toValueString(StringTemplate.defaultTemplate));
		}
		System.out.println(System.currentTimeMillis() - l);
		
		l = System.currentTimeMillis();
		StringBuilder sbm = new StringBuilder(1000);
		ExpressionNode nm = fv.wrap().subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv);
		for(int i = 0;i<100000;i++){
			sbm.append(nm.toValueString(StringTemplate.defaultTemplate));
		}
		System.out.println(System.currentTimeMillis() - l);
	}
	
	@Test
	public void testCannonicNumber(){
		Assert.assertEquals("0", StringUtil.cannonicNumber("0.0"));
		Assert.assertEquals("0", StringUtil.cannonicNumber(".0"));
		Assert.assertEquals("1.0E2", StringUtil.cannonicNumber("1.0E2"));
		Assert.assertEquals("1", StringUtil.cannonicNumber("1.00"));
	}

}
