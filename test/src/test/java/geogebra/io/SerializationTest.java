package geogebra.io;
import geogebra.CommandLineArguments;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.main.AppD;

import java.util.Locale;

import javax.swing.JFrame;

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

}
