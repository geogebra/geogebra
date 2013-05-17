package geogebra.html5.js;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.ScriptElement;

public class JavaScriptInjector {
	private static HeadElement head;
	 
    public static void inject(String javascript) {
        ScriptElement element = createScriptElement();
        element.setText(javascript);
        getHead().appendChild(element);
    }
 
    private static ScriptElement createScriptElement() {
        ScriptElement script = Document.get().createScriptElement();
        script.setAttribute("language", "javascript");
        return script;
    }
 
    private static HeadElement getHead() {
        if (JavaScriptInjector.head == null) {
            Element element = Document.get().getElementsByTagName("head")
                    .getItem(0);
            assert element != null : "HTML Head element required";
            JavaScriptInjector.head = HeadElement.as(element);
        }
        return JavaScriptInjector.head;
    }
}
