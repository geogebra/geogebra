package org.geogebra.web.test;

import java.util.Collection;

import org.geogebra.regexp.client.NativeRegExp;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererImplShadersW;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererWithImplW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.FileDropHandlerW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.geogebra.web.html5.util.MyNumberFormat;
import org.geogebra.web.resources.SVGResourcePrototype;
import org.geogebra.web.resources.StyleInjector;
import org.junit.runners.model.InitializationError;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.impl.StubGenerator;
import com.himamis.retex.editor.web.ClickAdapterW;
import com.himamis.retex.renderer.web.graphics.GraphicsFactoryGWT;
import com.himamis.retex.renderer.web.graphics.ImageW;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;
import com.himamis.retex.renderer.web.graphics.JLMContextHelper;

import elemental2.core.Uint8Array;
import elemental2.dom.DomGlobal;
import elemental2.dom.XMLHttpRequest;
import elemental2.webgl.WebGLRenderingContext;
import elemental2.webgl.WebGLShader;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class GgbMockitoTestRunner extends GwtMockitoTestRunner {

    /**
     * Creates a test runner which allows final GWT classes to be mocked.
     * Works by reloading the test class using a custom classloader and substituting the reference.
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
        StubGenerator.replaceMethodWithMock(FileDropHandlerW.class, "registerDropHandler",
                Void.class);
        StubGenerator.replaceMethodWithMock(Canvas.class, "createIfSupported",
                Canvas.class);
        StubGenerator.replaceMethodWithMock(JLMContextHelper.class, "as",
                JLMContext2d.class);
        StubGenerator.replaceMethodWithMock(GraphicsFactoryGWT.class, "createImage",
                ImageW.class);
        StubGenerator.replaceMethodWithMock(RendererWithImplW.class, "getWebGLContext",
                WebGLRenderingContext.class);
        StubGenerator.replaceMethodWithMock(RendererImplShadersW.class, "getShader",
                WebGLShader.class);
        StubGenerator.replaceMethodWithMock(RendererImplShadersW.class, "glLinkProgram",
                Void.class);
        StubGenerator.replaceMethodWithMock(Uint8Array.class, "create",
                Uint8Array.class);
        StubGenerator.replaceMethodWithMock(RendererImplShadersW.class, "createAlphaTexture",
                Integer.class);
        StubGenerator.replaceMethodWithMock(Dom.class, "addEventListener",
                Void.class);
        StubGenerator.replaceMethodWithMock(GlobalHandlerRegistry.class, "addEventListener",
                Void.class);
        StubGenerator.replaceMethodWithMock(CopyPasteW.class, "installCutCopyPaste",
                Void.class);
        StubGenerator.replaceMethodWithMock(MyNumberFormat.class, "toPrecision",
                String.class);
        StubGenerator.replaceMethodWithMock(NativeRegExp.class, "exec",
                String.class);
        StubGenerator.replaceMethodWithMock(ClickAdapterW.class, "listenTo",
                Void.class);
        StubGenerator.replaceMethodWithMock(XMLHttpRequest.class, "send",
                XMLHttpRequest.class);
        StubGenerator.replaceMethodWithMock(StyleInjector.class, "inject",
                Void.class);
        StubGenerator.replaceMethodWithMock(Browser.class, "isSafariByVendor",
                Boolean.class);
        StubGenerator.replaceMethodWithMock(SVGResourcePrototype.class, "withFill",
                SafeUri.class);
        StubGenerator.replaceMethodWithMock(Dom.class, "querySelector", Void.class);
        StubGenerator.replaceMethodWithMock(Dom.class, "querySelectorForElement", Void.class);
        StubGenerator.replaceMethodWithMock(CSSEvents.class, "runOnAnimation", Void.class);
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