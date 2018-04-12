package org.geogebra.desktop.factories;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.giac.CASgiac.CustomFunctions;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.cas.giac.CASgiacD;

public class LoggingCASFactoryD extends CASFactory {
	private static HashMap<String, String> rawResponses = new HashMap<>();

	@Override
	public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
		return new CASgiacD(parser){
			private String lastInput;

			@Override
			protected void debug(String prefix, String giacString) {
				if (prefix.contains("input")) {
					lastInput = giacString;
				} else {
					rawResponses.put(lastInput,
							StringUtil.toJavaString(giacString));
					lastInput = null;
				}
				Log.debug(prefix + giacString);
			}
		};
	}

	public static void printResponses(AsyncOperation<String> sh) {
		sh.callback("{");
		boolean first = true;
		for (Entry<String, String> entry : rawResponses.entrySet()) {
			if (!first) {
				sh.callback(",\n");
			}
			first = false;
			sh.callback("\"" + entry.getKey() + "\" : \""
					+ entry.getValue() + "\"");
		}
		sh.callback("}");
	}

	public static void printCustomFunctions(AsyncOperation<String> sh) {
		sh.callback("[");
		boolean first = true;
		for (CustomFunctions fn : CustomFunctions.values()) {
			if (fn == CustomFunctions.RESTART) {
				continue;
			}
			if (!first) {
				sh.callback(",\n");
			}
			first = false;
			sh.callback(
					"\"" + StringUtil.toJavaString(fn.definitionString) + "\"");
		}
		sh.callback("]");
	}

}
