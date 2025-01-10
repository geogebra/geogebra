package org.geogebra.web.html5.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

import org.geogebra.web.html5.main.DefaultExportedApi;
import org.geogebra.web.html5.main.TS;
import org.junit.Test;

import jsinterop.annotations.JsIgnore;

public class TypescriptGenerator {

	static HashMap<String, String> types = new HashMap<>();

	static {
		types.put("long", "number");
		types.put("double", "number");
		types.put("int", "number");
		types.put("element", "Element");
		types.put("jspropertymap<java.lang.object>", "{[key:string]: any}");
		types.put("jsrunnable", "()=>void");
		types.put("stringconsumer", "(str:string)=>void");
		types.put("jsarray<java.lang.string>", "string[]");
		types.put("promise<java.lang.string>", "Promise<string>");
		types.put("[ljava.lang.string;", "string[]");
	}

	@Test
	public void generate() {
		Method[] methods = DefaultExportedApi.class.getMethods();
		Arrays.sort(methods, Comparator.comparing(Method::getName));
		for (Method mtd: methods) {
			if (mtd.getAnnotation(JsIgnore.class) == null
					&& mtd.getDeclaringClass() == DefaultExportedApi.class) {
				System.out.println(mtd.getName() + ": (" + mapTypes(mtd.getParameters()) + ") => "
						+ getType(mtd.getGenericReturnType(), null) + ";");
			}
		}
	}

	private String mapTypes(Parameter[] parameters) {
		return Arrays.stream(parameters)
				.map(p -> p.getName() + ":" + getType(p.getType(),
						p.getAnnotation(TS.class)))
				.collect(Collectors.joining(", ")).replace(":?", "?:");
	}

	private String getType(Type returnType, TS annotation) {
		if (annotation != null) {
			return annotation.value();
		}
		String simpleName;
		if (returnType instanceof Class) {
			simpleName = ((Class) returnType).getName();
		} else {
			simpleName = returnType.getTypeName();
		}
		simpleName = simpleName.toLowerCase(Locale.ROOT)
				.replaceAll("^[a-zA-Z0-9.]*\\.", "");
		return types.getOrDefault(simpleName, simpleName);
	}
}
