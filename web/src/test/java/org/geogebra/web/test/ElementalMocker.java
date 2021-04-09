package org.geogebra.web.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import elemental2.dom.Console;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLHtmlElement;
import elemental2.webstorage.WebStorageWindow;

public class ElementalMocker {

	public static void setupElemental() {
		try {
			DomGlobal.console = new Console();
			DomGlobal.window = new WebStorageWindow();
			setFinalStatic(DomGlobal.class.getField("document"), new HTMLDocument());
			DomGlobal.document.documentElement = new HTMLHtmlElement();
		} catch (Exception e) {
			System.err.println("Failed to set up elemental2 mocks");
			e.printStackTrace();
		}
	}

	private static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);
		Field modifiersField = null;
		try {
			modifiersField = Field.class.getDeclaredField("modifiers");
		} catch (NoSuchFieldException e) {
			try {
				Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
				boolean accessibleBeforeSet = getDeclaredFields0.isAccessible();
				getDeclaredFields0.setAccessible(true);
				Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
				getDeclaredFields0.setAccessible(accessibleBeforeSet);
				for (Field ff : fields) {
					if ("modifiers".equals(ff.getName())) {
						modifiersField = ff;
						break;
					}
				}
				if (modifiersField == null) {
					throw e;
				}
			} catch (NoSuchMethodException | InvocationTargetException ex) {
				e.addSuppressed(ex);
				throw e;
			}
		}
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}
}
