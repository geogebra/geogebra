package org.geogebra.web.test;

import java.util.Collection;

import org.junit.runners.model.InitializationError;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.impl.StubGenerator;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class GgbMockitoTestRunner extends GwtMockitoTestRunner {

    /**
     * Creates a test runner which allows final GWT classes to be mocked. Works by reloading the test
     * class using a custom classloader and substituting the reference.
     *
     * @param unitTestClass test class
     */
    public GgbMockitoTestRunner(Class<?> unitTestClass) throws InitializationError {
        super(unitTestClass);
        StubGenerator.replaceMethodWithMock(Js.class, "asPropertyMap",
                JsPropertyMap.class);
        StubGenerator.replaceMethodWithMock(DomGlobal.class, "setInterval",
                Double.class);
        StubGenerator.replaceMethodWithMock(DomGlobal.class, "setTimeout",
                Double.class);
    }

    @Override
    protected Collection<Class<?>> getClassesToStub() {
        Collection<Class<?>> classesToStub = super.getClassesToStub();
        classesToStub.remove(Widget.class);
        classesToStub.remove(Panel.class);
        classesToStub.remove(ComplexPanel.class);
        classesToStub.remove(FlowPanel.class);
        classesToStub.add(TextAreaElement.class);
        return classesToStub;
    }
}