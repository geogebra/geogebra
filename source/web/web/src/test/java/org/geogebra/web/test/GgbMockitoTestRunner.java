package org.geogebra.web.test;

import java.util.Collection;

import org.geogebra.regexp.client.NativeRegExp;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererImplShadersW;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.RendererWithImplW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.factories.FormatFactoryW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.FileDropHandlerW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.geogebra.web.resources.SVGResourcePrototype;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.richtext.impl.CarotaEditor;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.core.client.impl.SchedulerImpl;
import org.gwtproject.dom.client.SelectElement;
import org.gwtproject.dom.client.TextAreaElement;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.safehtml.shared.SafeUri;
import org.gwtproject.user.cellview.client.CellBasedWidgetImplStandard;
import org.gwtproject.user.cellview.client.CellTable;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.impl.DOMImplStandard;
import org.gwtproject.user.client.impl.DOMImplStandardBase;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.CellPanel;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.DeckLayoutPanel;
import org.gwtproject.user.client.ui.DeckPanel;
import org.gwtproject.user.client.ui.DockLayoutPanel;
import org.gwtproject.user.client.ui.DockPanel;
import org.gwtproject.user.client.ui.FocusPanel;
import org.gwtproject.user.client.ui.HTMLTable;
import org.gwtproject.user.client.ui.HorizontalPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.LayoutPanel;
import org.gwtproject.user.client.ui.ResizeComposite;
import org.gwtproject.user.client.ui.ResizeLayoutPanel;
import org.gwtproject.user.client.ui.SimpleLayoutPanel;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.SplitLayoutPanel;
import org.gwtproject.user.client.ui.StackPanel;
import org.gwtproject.user.client.ui.UIObject;
import org.gwtproject.user.client.ui.VerticalPanel;
import org.gwtproject.user.client.ui.impl.FocusImpl;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.impl.StubGenerator;
import com.himamis.retex.editor.web.ClickAdapterW;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;
import com.himamis.retex.renderer.web.graphics.GraphicsFactoryGWT;
import com.himamis.retex.renderer.web.graphics.ImageW;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;
import com.himamis.retex.renderer.web.graphics.JLMContextHelper;

import elemental2.core.JsDate;
import elemental2.core.Uint8Array;
import elemental2.dom.Document;
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
     * @throws InitializationError if the test class is malformed.
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
        StubGenerator.replaceMethodWithMock(Graphics2DW.class, "initFontParser",
                Void.class);
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
        StubGenerator.replaceMethodWithMock(FormatFactoryW.class, "toPrecision",
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
        StubGenerator.replaceMethodWithMock(Dom.class, "createDiv", Void.class);
        StubGenerator.replaceMethodWithMock(Dom.class, "querySelectorForElement", Void.class);
        StubGenerator.replaceMethodWithMock(CSSEvents.class, "runOnAnimation", Void.class);
        StubGenerator.replaceMethodWithMock(SelectElement.class, "as", SelectElement.class);
        StubGenerator.replaceMethodWithMock(CopyPasteW.class, "writeToExternalClipboard",
                Object.class);
        StubGenerator.replaceMethodWithMock(ResizeComposite.class, "onResize",
                Void.class);
        StubGenerator.replaceMethodWithMock(CancelEventTimer.class, "killTouch", Void.class);
        StubGenerator.replaceMethodWithMock(GPopupPanel.class, "getContainerElement", Void.class);
        StubGenerator.replaceMethodWithMock(MathFieldW.class, "addFocusListener", Void.class);
        StubGenerator.replaceMethodWithMock(CarotaEditor.class, "addInsertFilter", Void.class);
        StubGenerator.replaceMethodWithMock(MathFieldW.class, "checkCode", Void.class);
        StubGenerator.replaceMethodWithMock(Document.class, "createTextNode", Void.class);
    }

    @Override
    protected Collection<Class<?>> getClassesToStub() {
        Collection<Class<?>> classes = super.getClassesToStub();
        classes.add(Composite.class);
        classes.add(DOM.class);
        classes.add(UIObject.class);
        classes.add(HTMLTable.class);
        classes.add(Image.class);
        classes.add(AbsolutePanel.class);
        classes.add(CellPanel.class);
        classes.add(CellTable.class);
        classes.add(DeckLayoutPanel.class);
        classes.add(DeckPanel.class);
        classes.add(DockLayoutPanel.class);
        classes.add(DockPanel.class);
        classes.add(FocusPanel.class);
        classes.add(HorizontalPanel.class);
        classes.add(LayoutPanel.class);
        classes.add(ResizeLayoutPanel.class);
        classes.add(SimpleLayoutPanel.class);
        classes.add(SimplePanel.class);
        classes.add(SplitLayoutPanel.class);
        classes.add(StackPanel.class);
        classes.add(VerticalPanel.class);
        classes.add(TextAreaElement.class);
        classes.add(DOMImplStandardBase.class);
        classes.add(DOMImplStandard.class);
        classes.add(DomEvent.Type.class);
        classes.add(SchedulerImpl.class);
        classes.add(FocusImpl.class);
        classes.add(JsDate.class);
        classes.add(CellBasedWidgetImplStandard.class);
        return classes;
    }

    @Override
    public void run(final RunNotifier notifier) {
        getTestClass().getJavaClass().getClassLoader().setDefaultAssertionStatus(false);
        super.run(notifier);
    }
}