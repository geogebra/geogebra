/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.web.gui.view.algebra;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;
import geogebra.common.main.SelectionManager;
import geogebra.web.euclidian.event.MouseEvent;
import geogebra.web.euclidian.event.ZeroOffset;
import geogebra.web.gui.view.algebra.Marble.GeoContainer;
import geogebra.web.main.AppWeb;
import geogebra.web.main.DrawEquationWeb;

import java.util.Iterator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextBox;

/**
 * RadioButtonTreeItem for the items of the algebra view tree
 * and also for the event handling which is copied from Desktop/AlgebraController.java
 *
 * File created by Arpad Fekete
 */

public class RadioButtonTreeItem extends HorizontalPanel
	implements DoubleClickHandler, ClickHandler, MouseMoveHandler, MouseDownHandler, GeoContainer {

	private GeoElement geo;
	private Kernel kernel;
	private AppWeb app;
	private SelectionManager selection; 
	private AlgebraView av;
	private boolean LaTeX = false;
	private boolean thisIsEdited = false;
	private boolean mout = false;

	private SpanElement seMayLatex;
	private SpanElement seNoLatex;

	private Marble radio;
	private InlineHTML ihtml;
	private TextBox tb;
	private boolean needsUpdate;
	
	public void updateOnNextRepaint(){
		this.needsUpdate = true;
	}

	/*private class RadioButtonHandy extends RadioButton {
		public RadioButtonHandy() {
			super(DOM.createUniqueId());
		}

		@Override
		public void onBrowserEvent(Event event) {

			if (av.isEditing())
				return;

			if (event.getTypeInt() == Event.ONCLICK) {
				// Part of AlgebraController.mouseClicked in Desktop
				if (Element.is(event.getEventTarget())) {
					if (Element.as(event.getEventTarget()) == getElement().getFirstChild()) {
						setValue(previouslyChecked = !previouslyChecked);
						geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
						geo.update();
						geo.getKernel().getApplication().storeUndoInfo();
						geo.getKernel().notifyRepaint();
						return;
					}
				}
			}
		}
	}*/

	public RadioButtonTreeItem(GeoElement ge,SafeUri showUrl,SafeUri hiddenUrl) {
		super();
		App.debug(System.currentTimeMillis());
		geo = ge;
		kernel = geo.getKernel();
		app = (AppWeb)kernel.getApplication();
		av = app.getAlgebraView();
		selection = app.getSelectionManager();

		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		radio = new Marble(showUrl, hiddenUrl,this);
		radio.setEnabled(ge.isEuclidianShowable());
		radio.setChecked(ge.isEuclidianVisible());
		add(radio);

		SpanElement se = DOM.createSpan().cast();
		se.getStyle().setProperty("display", "-moz-inline-box");
		se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		se.getStyle().setColor( GColor.getColorString( geo.getAlgebraColor() ) );
		ihtml = new InlineHTML();
		ihtml.addDoubleClickHandler(this);
		ihtml.addClickHandler(this);
		ihtml.addMouseMoveHandler(this);
		ihtml.addMouseDownHandler(this);
		add(ihtml);
		ihtml.getElement().appendChild(se);

		SpanElement se2 = DOM.createSpan().cast();
		se2.setInnerHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
		ihtml.getElement().appendChild(se2);
		App.debug(System.currentTimeMillis());
		String text = "";
		if (geo.isIndependent()) {
			text = geo.getAlgebraDescriptionTextOrHTMLDefault();
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				text = geo.getAlgebraDescriptionTextOrHTMLDefault();
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				text = geo.addLabelTextOrHTML(
					geo.getDefinitionDescription(StringTemplate.defaultTemplate));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				text = geo.addLabelTextOrHTML(
					geo.getCommandDescription(StringTemplate.defaultTemplate));
				break;
			}
		}
		App.debug(System.currentTimeMillis());
		// if enabled, render with LaTeX
		if (av.isRenderLaTeX() && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			String latexStr = geo.getLaTeXAlgebraDescription(true,
					StringTemplate.latexTemplate);
			seNoLatex = se;
			seNoLatex.setInnerHTML(text);
			if ((latexStr != null) &&
				geo.isLaTeXDrawableGeo() &&
				(geo.isGeoList() ? !((GeoList)geo).isMatrix() : true) ) {
				this.needsUpdate = true;
				av.repaintView();
			}
		} else {
			seNoLatex = se;
			seNoLatex.setInnerHTML(text);
		}
		//FIXME: geo.getLongDescription() doesn't work
		//geo.getKernel().getApplication().setTooltipFlag();
		//se.setTitle(geo.getLongDescription());
		//geo.getKernel().getApplication().clearTooltipFlag();
	}

	public void repaint() {
		if(needsUpdate)
			doUpdate();
	}
	
	private void doUpdate() {
		// check for new LaTeX
		needsUpdate = false;
		boolean newLaTeX = false;
		String text = null;
		if (av.isRenderLaTeX()
		        && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			text = geo.getLaTeXAlgebraDescription(true,
			        StringTemplate.latexTemplate);
			if ((text != null) && geo.isLaTeXDrawableGeo()
			        && (geo.isGeoList() ? !((GeoList) geo).isMatrix() : true)) {
				newLaTeX = true;
			}
		}
		// check for new text
		if (!newLaTeX) {
			if (geo.isIndependent()) {
				text = geo.getAlgebraDescriptionTextOrHTMLDefault();
			} else {
				switch (kernel.getAlgebraStyle()) {
				case Kernel.ALGEBRA_STYLE_VALUE:
					text = geo.getAlgebraDescriptionTextOrHTMLDefault();
					break;

				case Kernel.ALGEBRA_STYLE_DEFINITION:
					text = geo
					        .addLabelTextOrHTML(geo
					                .getDefinitionDescription(StringTemplate.defaultTemplate));
					break;

				case Kernel.ALGEBRA_STYLE_COMMAND:
					text = geo
					        .addLabelTextOrHTML(geo
					                .getCommandDescription(StringTemplate.defaultTemplate));
					break;
				}
			}
		}

		// now we have text and how to display it (newLaTeX/LaTeX)
		if (LaTeX && newLaTeX) {
			text = DrawEquationWeb.inputLatexCosmetics(text);
			int tl = text.length();
			text = DrawEquationWeb.stripEqnArray(text);
			DrawEquationWeb.updateEquationMathQuill("\\mathrm{"+text+"}", seMayLatex,
			        tl == text.length());
		} else if (!LaTeX && !newLaTeX) {
			seNoLatex.setInnerHTML(text);
		} else if (newLaTeX) {
			SpanElement se = DOM.createSpan().cast();
			se.getStyle().setProperty("display", "-moz-inline-box");
			se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			se.getStyle()
			        .setColor(GColor.getColorString(geo.getAlgebraColor()));
			ihtml.getElement().replaceChild(se, seNoLatex);
			text = DrawEquationWeb.inputLatexCosmetics(text);
			seMayLatex = se;
			DrawEquationWeb.drawEquationAlgebraView(seMayLatex, "\\mathrm {"+text+"}",
			        geo.getAlgebraColor(), GColor.white);
			LaTeX = true;
		} else {
			SpanElement se = DOM.createSpan().cast();
			se.getStyle().setProperty("display", "-moz-inline-box");
			se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			se.getStyle()
			        .setColor(GColor.getColorString(geo.getAlgebraColor()));
			ihtml.getElement().replaceChild(se, seMayLatex);
			seNoLatex = se;
			seNoLatex.setInnerHTML(text);
			LaTeX = false;
		}

	}

	public boolean isThisEdited() {
		return thisIsEdited;
	}

	public void cancelEditing() {
		if (LaTeX) {
			DrawEquationWeb.endEditingEquationMathQuill(this, seMayLatex);
		} else {
			remove(tb);
			add(ihtml);
			stopEditingSimple(tb.getText());
		}
	}

	public void startEditing() {
		thisIsEdited = true;
		if (LaTeX && !(geo.isGeoVector() && geo.isIndependent())) {
			geogebra.web.main.DrawEquationWeb.editEquationMathQuill(this,seMayLatex);
		} else {
			remove(ihtml);
			tb = new TextBox();
			tb.setText( geo.getAlgebraDescriptionTextOrHTMLDefault() );
			add(tb);
			mout = false;
			tb.setFocus(true);
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					tb.setFocus(true);
				}
			});
			tb.addKeyDownHandler(new KeyDownHandler() {
				public void onKeyDown(KeyDownEvent kevent) {
					if (kevent.getNativeKeyCode() == 13) {
						remove(tb);
						add(ihtml);
						stopEditingSimple(tb.getText());
					} else if (kevent.getNativeKeyCode() == 27) {
						remove(tb);
						add(ihtml);
						stopEditingSimple(null);
					}
				}
			});
			tb.addBlurHandler(new BlurHandler() {
				public void onBlur(BlurEvent bevent) {
					if (mout) {
						remove(tb);
						add(ihtml);
						stopEditingSimple(null);
					}
				}
			});
			tb.addMouseOverHandler(new MouseOverHandler() {
				public void onMouseOver(MouseOverEvent moevent) {
					mout = false;
				}
			});
			tb.addMouseOutHandler(new MouseOutHandler() {
				public void onMouseOut(MouseOutEvent moevent) {
					mout = true;
					tb.setFocus(true);
				}
			});
		}
	}

	public void stopEditingSimple(String newValue) {

		thisIsEdited = false;
		av.cancelEditing();

		if (newValue != null) {
			boolean redefine = !geo.isPointOnPath();
			GeoElement geo2 = kernel.getAlgebraProcessor().changeGeoElement(
					geo, newValue, redefine, true);
			if (geo2 != null)
				geo = geo2;
		}

		// maybe it's possible to enter something which is LaTeX
		// note: this should be OK for independent GeoVectors too
		doUpdate();
	}

	public void stopEditing(String newValue) {

		thisIsEdited = false;
		av.cancelEditing();
		
		if (newValue != null) {
			// Formula Hacks ... Currently only functions are considered
			StringBuilder sb = new StringBuilder();
			boolean switchw = false;
			//ignore first and last bracket, they come from mathrm
			int skip = newValue.startsWith("(") ? 1 : 0;	
			for (int i = skip; i < newValue.length() - skip; i++)
				if (newValue.charAt(i) != ' ') {
					if (newValue.charAt(i) != '|')
						sb.append(newValue.charAt(i));
					else  {
						switchw = !switchw;
						sb.append(switchw ? "abs(" : ")");
					}
				}
			App.debug(sb.toString());

			// Formula Hacks ended.
			boolean redefine = !geo.isPointOnPath();
			GeoElement geo2 = kernel.getAlgebraProcessor().changeGeoElement(
					geo, sb.toString(), redefine, true);
			if (geo2 != null)
				geo = geo2;
		}

		// maybe it's possible to enter something which is non-LaTeX
		doUpdate();
	}

	public void onDoubleClick(DoubleClickEvent evt) {

		if (av.isEditing())
			return;

		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		selection.clearSelectedGeos();
		ev.resetMode();
		if (geo != null && !evt.isControlKeyDown()) {
			av.startEditing(geo, evt.isShiftKeyDown());
		}
	}

	public void onMouseDown(MouseDownEvent evt) {
		if (av.isEditing())
			return;

		evt.preventDefault();
		evt.stopPropagation();
	}

	public void onClick(ClickEvent evt) {

		if (av.isEditing())
			return;

		int mode = app.getActiveEuclidianView().getMode();
		if (//!skipSelection && 
			(mode == EuclidianConstants.MODE_MOVE || mode == EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) ) {
			// update selection	
			if (geo == null){
				selection.clearSelectedGeos();
			}
			else {					
				// handle selecting geo
				if (evt.isControlKeyDown()) {
					selection.toggleSelectedGeo(geo); 													
					if (selection.getSelectedGeos().contains(geo)) av.setLastSelectedGeo(geo);
				} else if (evt.isShiftKeyDown() && av.getLastSelectedGeo() != null) {
					boolean nowSelecting = true;
					boolean selecting = false;
					boolean aux = geo.isAuxiliaryObject();
					boolean ind = geo.isIndependent();
					boolean aux2 = av.getLastSelectedGeo().isAuxiliaryObject();
					boolean ind2 = av.getLastSelectedGeo().isIndependent();

					if ((aux == aux2 && aux) || (aux == aux2 && ind == ind2)) {
						Iterator<GeoElement> it = geo.getKernel().getConstruction().getGeoSetLabelOrder().iterator();
						boolean direction = geo.getLabel(StringTemplate.defaultTemplate).
								compareTo(av.getLastSelectedGeo().getLabel(StringTemplate.defaultTemplate)) < 0;

						while (it.hasNext()) {
							GeoElement geo2 = it.next();
							if ((geo2.isAuxiliaryObject() == aux && aux)
									|| (geo2.isAuxiliaryObject() == aux && geo2.isIndependent() == ind)) {

								if (direction && geo2.equals(av.getLastSelectedGeo())) selecting = !selecting;
								if (!direction && geo2.equals(geo)) selecting = !selecting;

								if (selecting) {
									selection.toggleSelectedGeo(geo2);
									nowSelecting = selection.getSelectedGeos().contains(geo2);
								}
								if (!direction && geo2.equals(av.getLastSelectedGeo())) selecting = !selecting;
								if (direction && geo2.equals(geo)) selecting = !selecting;
							}
						}
					}

					if (nowSelecting) {
						selection.addSelectedGeo(geo); 
						av.setLastSelectedGeo(geo);
					} else {
						selection.removeSelectedGeo(av.getLastSelectedGeo());
						av.setLastSelectedGeo(null);
					}
				} else {							
					selection.clearSelectedGeos(false); //repaint will be done next step
					selection.addSelectedGeo(geo);
					av.setLastSelectedGeo(geo);
				}
			}
		} 
		else if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			// let euclidianView know about the click
			AbstractEvent event2 = MouseEvent.wrapEvent(evt.getNativeEvent(),ZeroOffset.instance);
			app.getActiveEuclidianView().clickedGeo(geo, event2);
			//event.release();
		} else 
			// tell selection listener about click
			app.geoElementSelected(geo, false);


		// Alt click: copy definition to input field
		if (geo != null && evt.isAltKeyDown() && app.showAlgebraInput()) {
			// F3 key: copy definition to input bar
			app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);			
		}

		app.getActiveEuclidianView().mouseMovedOver(null);
		av.setFocus(true);
	}

	public void onMouseMove(MouseMoveEvent evt) {

		if (av.isEditing())
			return;

		// tell EuclidianView to handle mouse over
		EuclidianViewInterfaceCommon ev = geo.getKernel().getApplication().getActiveEuclidianView();
		ev.mouseMovedOver(geo);

		// highlight the geos
		//getElement().getStyle().setBackgroundColor("rgb(200,200,245)");
			
		// implemented by HTML title attribute on the label
		//FIXME: geo.getLongDescription() doesn't work
		//if (geo != null) {
		//	geo.getKernel().getApplication().setTooltipFlag();
		//	se.setTitle(geo.getLongDescription());
		//	geo.getKernel().getApplication().clearTooltipFlag();
		//} else {
		//	se.setTitle("");
		//}
	}

	public GeoElement getGeo() {
	    return geo;
    }
}
