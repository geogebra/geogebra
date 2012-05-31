/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.web.gui.view.algebra;

import geogebra.common.awt.Color;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.web.euclidian.event.MouseEvent;
import geogebra.web.main.Application;
import geogebra.web.main.DrawEquationWeb;

import java.util.Iterator;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * RadioButtonTreeItem for the items of the algebra view tree
 * and also for the event handling which is copied from Desktop/AlgebraController.java
 *
 * File created by Arpad Fekete
 */

public class RadioButtonTreeItem extends HorizontalPanel
	implements DoubleClickHandler, ClickHandler, MouseMoveHandler {

	GeoElement geo;
	Kernel kernel;
	Application app;
	AlgebraView av;
	boolean previouslyChecked;
	boolean LaTeX = false;
	boolean thisIsEdited = false;

	SpanElement se;
	RadioButtonHandy radio;
	InlineHTML ihtml;
	TextBox tb;

	private class RadioButtonHandy extends RadioButton {
		public RadioButtonHandy() {
			super(DOM.createUniqueId());
		}

		@Override
		public void onBrowserEvent(Event event) {
			if (event.getTypeInt() == Event.ONCLICK) {
				// Part of AlgebraController.mouseClicked in Desktop
				if (Element.is(event.getEventTarget())) {
					if (Element.as(event.getEventTarget()) == getElement().getFirstChild()) {
						setChecked(previouslyChecked = !previouslyChecked);
						geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
						geo.update();
						geo.getKernel().getApplication().storeUndoInfo();
						geo.getKernel().notifyRepaint();
						return;
					}
				}
			}
		}
	}

	public RadioButtonTreeItem(GeoElement ge) {
		super();
		geo = ge;
		kernel = geo.getKernel();
		app = (Application)kernel.getApplication();
		av = (AlgebraView)app.getAlgebraView();

		setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);

		radio = new RadioButtonHandy();
		radio.setEnabled(ge.isEuclidianShowable());
		radio.setChecked(previouslyChecked = ge.isEuclidianVisible());
		add(radio);

		se = DOM.createSpan().cast();
		se.getStyle().setProperty("display", "-moz-inline-box");
		se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		se.getStyle().setColor( Color.getColorString( geo.getAlgebraColor() ) );
		ihtml = new InlineHTML();
		ihtml.addDoubleClickHandler(this);
		ihtml.addClickHandler(this);
		ihtml.addMouseMoveHandler(this);
		add(ihtml);
		ihtml.getElement().appendChild(se);

		SpanElement se2 = DOM.createSpan().cast();
		se2.setInnerHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
		ihtml.getElement().appendChild(se2);

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

		// if enabled, render with LaTeX
		if (av.isRenderLaTeX() && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			String latexStr = geo.getLaTeXAlgebraDescription(true,
					StringTemplate.latexTemplate);
			if ((latexStr != null) &&
				geo.isLaTeXDrawableGeo(latexStr) &&
				(geo.isGeoList() ? !((GeoList)geo).isMatrix() : true) ) {
				latexStr = inputLatexCosmetics(latexStr);
				DrawEquationWeb.drawEquationAlgebraView(se, latexStr,
					geo.getAlgebraColor(), Color.white);
				LaTeX = true;
			} else {
				se.setInnerHTML(text);
			}
		} else {
			se.setInnerHTML(text);
		}
		//FIXME: geo.getLongDescription() doesn't work
		//geo.getKernel().getApplication().setTooltipFlag();
		//se.setTitle(geo.getLongDescription());
		//geo.getKernel().getApplication().clearTooltipFlag();
	}

	public void update() {
		if (LaTeX) {
			String latexStr = geo.getLaTeXAlgebraDescription(true,
					StringTemplate.latexTemplate);
			latexStr = inputLatexCosmetics(latexStr);
			DrawEquationWeb.updateEquationMathQuill(latexStr, se);
		} else {
			se.setInnerHTML(geo.getAlgebraDescriptionTextOrHTMLDefault());
		}
	}

	public boolean isThisEdited() {
		return thisIsEdited;
	}

	public void cancelEditing() {
		if (LaTeX) {
			DrawEquationWeb.endEditingEquationMathQuill(this, se);
		} else {
			remove(tb);
			add(ihtml);
			stopEditingSimple(tb.getText());
		}
	}

	public String inputLatexCosmetics(String eqstring) {
		// make sure eg FractionText[] works (surrounds with {} which doesn't draw well in MathQuill)
		if (eqstring.length() >= 2)
			if (eqstring.startsWith("{") && eqstring.endsWith("}")) {
				eqstring = eqstring.substring(1, eqstring.length() - 1);
			}

		// remove $s
		eqstring = eqstring.trim();
		while (eqstring.startsWith("$")) eqstring = eqstring.substring(1).trim();
		while (eqstring.endsWith("$")) eqstring = eqstring.substring(0, eqstring.length() - 1).trim();

		// remove all \; and \,
		eqstring = eqstring.replace("\\;","");
		eqstring = eqstring.replace("\\,","");
		return eqstring;
	}

	public void startEditing() {
		thisIsEdited = true;
		if (LaTeX && !(geo.isGeoVector() && geo.isIndependent())) {
			geogebra.web.main.DrawEquationWeb.editEquationMathQuill(this,se);
		} else {
			remove(ihtml);
			tb = new TextBox();
			tb.setText( geo.getAlgebraDescriptionTextOrHTMLDefault() );
			add(tb);

			tb.addKeyUpHandler(new KeyUpHandler() {
				public void onKeyUp(KeyUpEvent kevent) {
					if (kevent.getNativeKeyCode() == 13) {
						remove(tb);
						add(ihtml);
						stopEditingSimple(tb.getText());
					}
				}
			});
		}
	}

	public void stopEditingSimple(String newValue) {

		thisIsEdited = false;
		av.cancelEditing();

		boolean redefine = !geo.isPointOnPath();
		GeoElement geo2 = kernel.getAlgebraProcessor().changeGeoElement(
				geo, newValue, redefine, true);
		if (geo2 != null)
			geo = geo2;

		if ( geo.isGeoVector() && geo.isIndependent() ) {
			String latexStr = geo.getLaTeXAlgebraDescription(true,
					StringTemplate.latexTemplate);
			latexStr = inputLatexCosmetics(latexStr);
			DrawEquationWeb.updateEquationMathQuill(latexStr, se);
		} else {
			se.setInnerHTML(geo.getAlgebraDescriptionTextOrHTMLDefault());
		}
	}

	public void stopEditing(String newValue) {

		thisIsEdited = false;
		av.cancelEditing();

		// Formula Hacks ... Currently only functions are considered
		int ieq = newValue.indexOf('=');
		String newValueFirst = "";
		String newValueLast = "";
		if (ieq != -1) {
			newValueFirst = newValue.substring(0,ieq);
			newValueLast = newValue.substring(ieq);
		} else {
			newValueFirst = "";
			newValueLast = newValue;
		}
		//newValueLast = newValueLast.replace("**","^");
		//newValueLast = newValueLast.replace("cdot","*");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < newValueFirst.length(); i++)
			// i+=2 is not good because it can be f(*x*) or g*(*x*) 
		//	if (newValueFirst.charAt(i) != '*')
				sb.append(newValueFirst.charAt(i));

		boolean switchw = false;
		for (int i = 0; i < newValueLast.length(); i++)
			if (newValueLast.charAt(i) != ' ') {
				if (newValueLast.charAt(i) != '|')
					sb.append(newValueLast.charAt(i));
				else if (switchw = !switchw)
					sb.append("abs(");
				else
					sb.append(")");
			}

		newValue = sb.toString();

		// Formula Hacks ended.

		boolean redefine = !geo.isPointOnPath();
		GeoElement geo2 = kernel.getAlgebraProcessor().changeGeoElement(
				geo, newValue, redefine, true);
		if (geo2 != null)
			geo = geo2;

		String latexStr = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);

		if (latexStr != null && geo.isLaTeXDrawableGeo(latexStr)) {
			latexStr = inputLatexCosmetics(latexStr);
			DrawEquationWeb.updateEquationMathQuill(latexStr, se);
		}
	}

	public void onDoubleClick(DoubleClickEvent evt) {
		if (!av.editing) {
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			app.clearSelectedGeos();
			ev.resetMode();
			if (geo != null && !evt.isControlKeyDown()) {
				app.getAlgebraView().startEditing(geo, evt.isShiftKeyDown());
			}
		}
	}

	public void onClick(ClickEvent evt) {

		Application app = (Application)geo.getKernel().getApplication();
		int mode = app.getActiveEuclidianView().getMode();
		if (//!skipSelection && 
			(mode == EuclidianConstants.MODE_MOVE || mode == EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) ) {
			// update selection	
			if (geo == null){
				app.clearSelectedGeos();
			}
			else {					
				// handle selecting geo
				if (evt.isControlKeyDown()) {
					app.toggleSelectedGeo(geo); 													
					if (app.getSelectedGeos().contains(geo)) av.lastSelectedGeo = geo;
				} else if (evt.isShiftKeyDown() && av.lastSelectedGeo != null) {
					boolean nowSelecting = true;
					boolean selecting = false;
					boolean aux = geo.isAuxiliaryObject();
					boolean ind = geo.isIndependent();
					boolean aux2 = av.lastSelectedGeo.isAuxiliaryObject();
					boolean ind2 = av.lastSelectedGeo.isIndependent();

					if ((aux == aux2 && aux) || (aux == aux2 && ind == ind2)) {
						Iterator<GeoElement> it = geo.getKernel().getConstruction().getGeoSetLabelOrder().iterator();
						boolean direction = geo.getLabel(StringTemplate.defaultTemplate).
								compareTo(av.lastSelectedGeo.getLabel(StringTemplate.defaultTemplate)) < 0;

						while (it.hasNext()) {
							GeoElement geo2 = it.next();
							if ((geo2.isAuxiliaryObject() == aux && aux)
									|| (geo2.isAuxiliaryObject() == aux && geo2.isIndependent() == ind)) {

								if (direction && geo2.equals(av.lastSelectedGeo)) selecting = !selecting;
								if (!direction && geo2.equals(geo)) selecting = !selecting;

								if (selecting) {
									app.toggleSelectedGeo(geo2);
									nowSelecting = app.getSelectedGeos().contains(geo2);
								}
								if (!direction && geo2.equals(av.lastSelectedGeo)) selecting = !selecting;
								if (direction && geo2.equals(geo)) selecting = !selecting;
							}
						}
					}

					if (nowSelecting) {
						app.addSelectedGeo(geo); 
						av.lastSelectedGeo = geo;
					} else {
						app.removeSelectedGeo(av.lastSelectedGeo);
						av.lastSelectedGeo = null;
					}
				} else {							
					app.clearSelectedGeos(false); //repaint will be done next step
					app.addSelectedGeo(geo);
					av.lastSelectedGeo = geo;
				}
			}
		} 
		else if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			// let euclidianView know about the click
			AbstractEvent event2 = MouseEvent.wrapEvent(evt.getNativeEvent());
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
	}

	public void onMouseMove(MouseMoveEvent evt) {
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
}
