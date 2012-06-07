package geogebra.test.properties;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import geogebra.CommandLineArguments;
import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

import javax.swing.JFrame;

import junit.framework.Assert;

import org.junit.Test;

public class PropertiesIntegrityCheck {
	static Application app = new Application(new CommandLineArguments(
			new String[]{"--silent"}), new JFrame(), false);

	
	@Test
	public void commandProperties() {
		testBundle("/geogebra/properties/command",geogebra.web.properties.CommandConstants.class,true);
	}
	@Test
	public void plainProperties() {
		testBundle("/geogebra/properties/plain",geogebra.web.properties.PlainConstants.class,true);
	}
	@Test
	public void errorProperties() {
		testBundle("/geogebra/properties/error",geogebra.web.properties.ErrorConstants.class,true);
	}
	@Test
	public void menuProperties() {
		testBundle("/geogebra/properties/menu",geogebra.web.properties.MenuConstants.class,true);
	}
	@Test
	public void colorsProperties() {
		testBundle("/geogebra/properties/colors",geogebra.web.properties.ColorsConstants.class,false);
	}
	@Test
	public void symbolsProperties() {
		testBundle("/geogebra/properties/symbols",geogebra.web.properties.SymbolsConstants.class,true);
	}
	private void testBundle(String url,
			Class<?> class1, boolean useCrossReferencing) {
		ResourceBundle cmd = MyResourceBundle.createBundle(url,
				Locale.US);
		Enumeration<String> keys =cmd.getKeys();
		int missing = 0;
		String names = "";
		while(keys.hasMoreElements()){
			Method[] met = class1.getMethods();
			Set<String> metSet = new TreeSet<String>();
			for(int i=0;i<met.length;i++)
				metSet.add(met[i].getName());
			String key = keys.nextElement();
			if(useCrossReferencing)
				key = geogebra.web.main.Application.crossReferencingPropertiesKeys(key);
			if(!metSet.contains(key)){
				missing++;
				names += key+",";
			}
		}
		Assert.assertEquals(names,missing, 0);
		
	}
	
}
