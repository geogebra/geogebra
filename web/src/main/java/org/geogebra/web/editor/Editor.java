package org.geogebra.web.editor;

import org.geogebra.web.html5.css.StyleInjector;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;
import org.geogebra.web.keyboard.KeyboardResources;
import org.geogebra.web.keyboard.OnScreenKeyBoard;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.editor.web.JlmEditorLib;
import com.himamis.retex.editor.web.MathFieldListener;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.editor.web.xml.XmlResourcesEditor;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.CreateLibrary;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.font.opentype.Opentype;
import com.himamis.retex.renderer.web.resources.ResourceLoaderW;

public class Editor implements EntryPoint, MathFieldListener {
	private JlmEditorLib library;
	private Opentype opentype;
	public void onModuleLoad() {
		ResourceLoaderW.addResource(
				"/com/himamis/retex/editor/desktop/meta/Octave.xml",
				XmlResourcesEditor.INSTANCE.octave());
		FactoryProvider.INSTANCE = new FactoryProviderGWT();
		library = new JlmEditorLib();
		opentype = Opentype.INSTANCE;
		CreateLibrary.exportLibrary(library, opentype);
		addEditorFunction(this);
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());

	}

	public void edit(Element parent) {
		Canvas canvas = Canvas.createIfSupported();
		String id = "JlmEditorKeyboard" + DOM.createUniqueId();
		parent.setId(id);
		RootPanel parentWidget = RootPanel.get(id);
		Element el = DOM.createDiv();
		el.appendChild(canvas.getCanvasElement());
		MathFieldW fld = new MathFieldW(HTML.wrap(el), canvas.getContext2d(),
				this);
		final OnScreenKeyBoard kb = new OnScreenKeyBoard(new KeyboardContext(),
				false);
		kb.setListener(new UpdateKeyBoardListener() {

			public void keyBoardNeeded(boolean show,
					MathKeyboardListener textField) {

			}

			public void doShowKeyBoard(boolean b,
					MathKeyboardListener textField) {
			}
		});
		kb.setProcessing(new MathFieldProcessing(fld));
		parentWidget.addDomHandler(new MouseUpHandler() {

			public void onMouseUp(MouseUpEvent event) {

			}
		}, MouseUpEvent.getType());
		parentWidget.add(fld.asWidget());
		parentWidget.add(kb);
		kb.show();
		Timer t = new Timer() {

			@Override
			public void run() {
				kb.updateSize();
				kb.setStyleName();
			}
		};
		// t.schedule(0);
		// fld.requestViewFocus();
	}
	private native void addEditorFunction(Editor library) /*-{
		$wnd.tryEditor = function(el) {
			library.@org.geogebra.web.editor.Editor::edit(Lcom/google/gwt/dom/client/Element;)(el);
		}

	}-*/;

	public void onEnter() {
		// TODO Auto-generated method stub

	}

}
