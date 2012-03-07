package geogebra.web.helper;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.ScriptElement;

public class JavaScriptInjector {
	private static HeadElement head;
	 
    public static void inject(String javascript) {
        HeadElement head = getHead();
        ScriptElement element = createScriptElement();
        element.setText(javascript);
        head.appendChild(element);
    }
 
    private static ScriptElement createScriptElement() {
        ScriptElement script = Document.get().createScriptElement();
        script.setAttribute("language", "javascript");
        return script;
    }
 
    private static HeadElement getHead() {
        if (head == null) {
            Element element = Document.get().getElementsByTagName("head")
                    .getItem(0);
            assert element != null : "HTML Head element required";
            HeadElement head = HeadElement.as(element);
            JavaScriptInjector.head = head;
        }
        return JavaScriptInjector.head;
    }
}
