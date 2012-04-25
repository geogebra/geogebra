package geogebra.web.gui.view.algebra;

import geogebra.common.awt.Color;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.web.euclidian.event.MouseEvent;
import geogebra.web.main.Application;

import java.util.Iterator;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.RadioButton;

public class RadioButtonTreeItem extends HorizontalPanel
	implements DoubleClickHandler, ClickHandler, MouseMoveHandler {

	GeoElement geo;
	Kernel kernel;
	Application app;
	AlgebraView av;
	boolean previouslyChecked;
	boolean LaTeX = false;

	SpanElement se;
	RadioButtonHandy radio;
	InlineHTML ihtml;

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

		// if enabled, render with LaTeX
		if (av.isRenderLaTeX() && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			String latexStr = geo.getLaTeXAlgebraDescription(true,
					StringTemplate.latexTemplate);
			if (latexStr != null && geo.isLaTeXDrawableGeo(latexStr)) {
				geogebra.web.main.DrawEquationWeb.drawEquationAlgebraView(se, latexStr,
					geo.getAlgebraColor(), Color.white);
				LaTeX = true;
			} else {
				se.setInnerHTML(ge.getAlgebraDescriptionTextOrHTML(
						StringTemplate.defaultTemplate));
			}
		} else {
			se.setInnerHTML(ge.getAlgebraDescriptionTextOrHTML(
					StringTemplate.defaultTemplate));
		}
		//FIXME: geo.getLongDescription() doesn't work
		//geo.getKernel().getApplication().setTooltipFlag();
		//se.setTitle(geo.getLongDescription());
		//geo.getKernel().getApplication().clearTooltipFlag();
	}

	public void startEditing() {
		if (LaTeX) {
			geogebra.web.main.DrawEquationWeb.editEquationMathQuill(this,se);
		}
	}

	public void stopEditing(String newValue) {

		// TODO: How to make simple formula from latex formula??
		/*
		boolean redefine = !geo.isPointOnPath();
		GeoElement geo2 = kernel.getAlgebraProcessor().changeGeoElement(
				geo, newValue, redefine, true);
		if (geo2 != null)
			geo = geo2;
		*/
	}

	public void onDoubleClick(DoubleClickEvent evt) {
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		app.clearSelectedGeos();
		ev.resetMode();
		if (geo != null && !evt.isControlKeyDown()) {
			app.getAlgebraView().startEditing(geo, evt.isShiftKeyDown());
		}
		return;
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
					app.clearSelectedGeos();
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
