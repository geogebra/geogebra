package org.geogebra.web.test;

import com.google.gwt.user.client.ui.*;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.runners.model.InitializationError;

import java.util.Collection;

public class GgbMockitoTestRunner extends GwtMockitoTestRunner {

    /**
     * Creates a test runner which allows final GWT classes to be mocked. Works by reloading the test
     * class using a custom classloader and substituting the reference.
     *
     * @param unitTestClass
     */
    public GgbMockitoTestRunner(Class<?> unitTestClass) throws InitializationError {
        super(unitTestClass);
    }

    @Override
    protected Collection<Class<?>> getClassesToStub() {
        Collection<Class<?>> classesToStub = super.getClassesToStub();
        classesToStub.remove(Widget.class);
        classesToStub.remove(Panel.class);
        classesToStub.remove(ComplexPanel.class);
        classesToStub.remove(FlowPanel.class);

        return classesToStub;
    }
}