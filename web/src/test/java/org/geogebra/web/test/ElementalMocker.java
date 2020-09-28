package org.geogebra.web.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLHtmlElement;
import elemental2.webstorage.WebStorageWindow;
import jsinterop.base.JsPropertyMap;

public class ElementalMocker {

	public static void setupElemental() {
		try {
			DomGlobal.window = new CastableWindow();
			setFinalStatic(DomGlobal.class.getField("document"), new CastableDocument());
			DomGlobal.document.documentElement = new HTMLHtmlElement();
		} catch (Exception e) {
			System.err.println("Failed to set up elemental2 mocks");
			e.printStackTrace();
		}
	}

	private static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}

	private static class CastableWindow extends WebStorageWindow implements JsPropertyMap<Object> {
		//
	}

	private static class CastableDocument extends HTMLDocument	implements JsPropertyMap<Object> {

	}
}
