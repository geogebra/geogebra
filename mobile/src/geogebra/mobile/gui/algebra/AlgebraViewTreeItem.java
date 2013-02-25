package geogebra.mobile.gui.algebra;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.Hits;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.mobile.controller.MobileController;
import geogebra.web.main.DrawEquationWeb;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Taken from the web-project.
 * 
 * @see geogebra.web.gui.view.algebra.RadioButtonTreeItem
 * 
 */
public class AlgebraViewTreeItem extends HorizontalPanel implements ClickHandler, MouseDownHandler
{

	GeoElement geo;
	private Kernel kernel;
	private AlgebraView algebraView;
	private EuclidianController controller;

	boolean previouslyChecked;
	boolean LaTeX = false;
	boolean thisIsEdited = false;
	boolean mout = false;

	SpanElement seMayLatex;
	SpanElement seNoLatex;

	CheckBox check;
	InlineHTML ihtml;
	TextBox tb;

	public AlgebraViewTreeItem(GeoElement ge, AlgebraView av, EuclidianController ac)
	{
		super();
		this.geo = ge;
		this.kernel = this.geo.getKernel();
		this.algebraView = av;
		this.controller = ac;

		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.check = new CheckBox();
		AlgebraViewTreeItem.this.previouslyChecked = ge.isEuclidianVisible();

		this.check.setValue(new Boolean(AlgebraViewTreeItem.this.previouslyChecked), false);

		this.check.addValueChangeHandler(new ValueChangeHandler<Boolean>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event)
			{
				AlgebraViewTreeItem.this.geo.setEuclidianVisible(!AlgebraViewTreeItem.this.geo.isSetEuclidianVisible());
				AlgebraViewTreeItem.this.geo.update();
				AlgebraViewTreeItem.this.geo.getKernel().getApplication().storeUndoInfo();
				AlgebraViewTreeItem.this.geo.getKernel().notifyRepaint();
			}
		});
		add(this.check);

		SpanElement se = DOM.createSpan().cast();
		se.getStyle().setProperty("display", "-moz-inline-box");
		se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		se.getStyle().setColor(GColor.getColorString(this.geo.getAlgebraColor()));
		this.ihtml = new InlineHTML();
		this.ihtml.addClickHandler(this);
		this.ihtml.addMouseDownHandler(this);
		add(this.ihtml);
		this.ihtml.getElement().appendChild(se);

		SpanElement se2 = DOM.createSpan().cast();
		se2.setInnerHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
		this.ihtml.getElement().appendChild(se2);

		String text = "";
		if (this.geo.isIndependent())
		{
			text = this.geo.getAlgebraDescriptionTextOrHTMLDefault();
		}
		else
		{
			switch (this.kernel.getAlgebraStyle())
			{
			case Kernel.ALGEBRA_STYLE_VALUE:
				text = this.geo.getAlgebraDescriptionTextOrHTMLDefault();
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				text = this.geo.addLabelTextOrHTML(this.geo.getDefinitionDescription(StringTemplate.defaultTemplate));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				text = this.geo.addLabelTextOrHTML(this.geo.getCommandDescription(StringTemplate.defaultTemplate));
				break;
			default:
				break;
			}
		}

		// if enabled, render with LaTeX
		if (/* TODO this.av.isRenderLaTeX() && */this.kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE)
		{
			String latexStr = this.geo.getLaTeXAlgebraDescription(true, StringTemplate.latexTemplate);
			if ((latexStr != null) && this.geo.isLaTeXDrawableGeo() && (this.geo.isGeoList() ? !((GeoList) this.geo).isMatrix() : true))
			{
				latexStr =  DrawEquationWeb.inputLatexCosmetics(latexStr);
				this.seMayLatex = se;
				DrawEquationWeb.drawEquationAlgebraView(this.seMayLatex, latexStr, this.geo.getAlgebraColor(), GColor.white);
				this.LaTeX = true;
			}
			else
			{
				this.seNoLatex = se;
				this.seNoLatex.setInnerHTML(text);
			}
		}
		else
		{
			this.seNoLatex = se;
			this.seNoLatex.setInnerHTML(text);
		}
	}

	public void update()
	{
		this.check.setValue(Boolean.valueOf(this.geo.isEuclidianVisible()));

		// check for new LaTeX
		boolean newLaTeX = false;
		String text = null;
		if (/* TODO av.isRenderLaTeX() && */this.kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE)
		{
			text = this.geo.getLaTeXAlgebraDescription(true, StringTemplate.latexTemplate);
			if ((text != null) && this.geo.isLaTeXDrawableGeo() && (this.geo.isGeoList() ? !((GeoList) this.geo).isMatrix() : true))
			{
				newLaTeX = true;
			}
		}
		// check for new text
		if (!newLaTeX)
		{
			if (this.geo.isIndependent())
			{
				text = this.geo.getAlgebraDescriptionTextOrHTMLDefault();
			}
			else
			{
				switch (this.kernel.getAlgebraStyle())
				{
				case Kernel.ALGEBRA_STYLE_VALUE:
					text = this.geo.getAlgebraDescriptionTextOrHTMLDefault();
					break;

				case Kernel.ALGEBRA_STYLE_DEFINITION:
					text = this.geo.addLabelTextOrHTML(this.geo.getDefinitionDescription(StringTemplate.defaultTemplate));
					break;

				case Kernel.ALGEBRA_STYLE_COMMAND:
					text = this.geo.addLabelTextOrHTML(this.geo.getCommandDescription(StringTemplate.defaultTemplate));
					break;
				default:
					break;
				}
			}

			if (this.seMayLatex != null)
			{
				this.seMayLatex.getStyle().setColor(GColor.getColorString(this.geo.getAlgebraColor()));
			}
			else if (this.seNoLatex != null)
			{
				this.seNoLatex.getStyle().setColor(GColor.getColorString(this.geo.getAlgebraColor()));
			}
		}

		// now we have text and how to display it (newLaTeX/LaTeX)
		if (this.LaTeX && newLaTeX)
		{
			text = DrawEquationWeb.inputLatexCosmetics(text);
			// FIXME what does "noEqnArray" do?
			DrawEquationWeb.updateEquationMathQuill(text, this.seMayLatex, false);
		}
		else if (!this.LaTeX && !newLaTeX)
		{
			this.seNoLatex.setInnerHTML(text);
		}
		else if (newLaTeX)
		{
			SpanElement se = DOM.createSpan().cast();
			se.getStyle().setProperty("display", "-moz-inline-box");
			se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			se.getStyle().setColor(GColor.getColorString(this.geo.getAlgebraColor()));
			this.ihtml.getElement().replaceChild(se, this.seNoLatex);
			text = DrawEquationWeb.inputLatexCosmetics(text);
			this.seMayLatex = se;
			DrawEquationWeb.drawEquationAlgebraView(this.seMayLatex, text, this.geo.getAlgebraColor(), GColor.white);
			this.LaTeX = true;
		}
		else
		{
			SpanElement se = DOM.createSpan().cast();
			se.getStyle().setProperty("display", "-moz-inline-box");
			se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			se.getStyle().setColor(GColor.getColorString(this.geo.getAlgebraColor()));
			this.ihtml.getElement().replaceChild(se, this.seMayLatex);
			this.seNoLatex = se;
			this.seNoLatex.setInnerHTML(text);
			this.LaTeX = false;
		}
	}

	public boolean isThisEdited()
	{
		return this.thisIsEdited;
	}

	// TODO
	public void cancelEditing()
	{
		// if (LaTeX) {
		// DrawEquationWeb.endEditingEquationMathQuill(this, seMayLatex);
		// } else {
		// remove(tb);
		// add(ihtml);
		// stopEditingSimple(tb.getText());
		// }
	}

	

	public void startEditing()
	{
		// TODO
		// thisIsEdited = true;
		// if (LaTeX && !(geo.isGeoVector() && geo.isIndependent())) {
		// geogebra.web.main.DrawEquationWeb.editEquationMathQuill(this,seMayLatex);
		// } else {
		// remove(ihtml);
		// tb = new TextBox();
		// tb.setText( geo.getAlgebraDescriptionTextOrHTMLDefault() );
		// add(tb);
		// mout = false;
		// tb.setFocus(true);
		// Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
		// public void execute() {
		// tb.setFocus(true);
		// }
		// });
		// tb.addKeyDownHandler(new KeyDownHandler() {
		// public void onKeyDown(KeyDownEvent kevent) {
		// if (kevent.getNativeKeyCode() == 13) {
		// remove(tb);
		// add(ihtml);
		// stopEditingSimple(tb.getText());
		// } else if (kevent.getNativeKeyCode() == 27) {
		// remove(tb);
		// add(ihtml);
		// stopEditingSimple(null);
		// }
		// }
		// });
		// tb.addBlurHandler(new BlurHandler() {
		// public void onBlur(BlurEvent bevent) {
		// if (mout) {
		// remove(tb);
		// add(ihtml);
		// stopEditingSimple(null);
		// }
		// }
		// });
		// tb.addMouseOverHandler(new MouseOverHandler() {
		// public void onMouseOver(MouseOverEvent moevent) {
		// mout = false;
		// }
		// });
		// tb.addMouseOutHandler(new MouseOutHandler() {
		// public void onMouseOut(MouseOutEvent moevent) {
		// mout = true;
		// tb.setFocus(true);
		// }
		// });
		// }
	}

	public void stopEditingSimple(String newValue)
	{

		this.thisIsEdited = false;
		this.algebraView.cancelEditing();

		if (newValue != null)
		{
			boolean redefine = !this.geo.isPointOnPath();
			GeoElement geo2 = this.kernel.getAlgebraProcessor().changeGeoElement(this.geo, newValue, redefine, true);
			if (geo2 != null)
			{
				this.geo = geo2;
			}
		}

		// maybe it's possible to enter something which is LaTeX
		// note: this should be OK for independent GeoVectors too
		update();
	}

	@Override
	public void onMouseDown(MouseDownEvent evt)
	{
		if (this.algebraView.isEditing())
			return;

		evt.preventDefault();
		evt.stopPropagation();
	}

	@Override
	public void onClick(ClickEvent evt)
	{
		Hits hits = new Hits();
		hits.add(this.geo);
		((MobileController) this.controller).handleEvent(hits);
	}

}