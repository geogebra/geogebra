package geogebra.mobile.gui.algebra;

import geogebra.common.awt.GColor;
import geogebra.common.gui.view.algebra.AlgebraController;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.mobile.controller.MobileAlgebraController;
import geogebra.web.main.DrawEquationWeb;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.mgwt.ui.client.widget.MCheckBox;

/**
 * Taken from the web-project.
 * 
 * @see geogebra.web.gui.view.algebra.RadioButtonTreeItem
 * 
 */
public class AlgebraViewTreeItem extends HorizontalPanel implements
		ClickHandler, MouseDownHandler
{

	MCheckBox checkBox;
	CheckBox check;

	private GeoElement geo;
	private Kernel kernel;
	private AlgebraView algebraView;
	private AlgebraController algebraController;

	boolean previouslyChecked;
	boolean LaTeX = false;
	boolean thisIsEdited = false;
	boolean mout = false;

	SpanElement seMayLatex;
	SpanElement seNoLatex;

	// RadioButtonHandy radio;
	InlineHTML ihtml;
	TextBox tb;

	public AlgebraViewTreeItem(GeoElement ge, AlgebraView av,
			AlgebraController ac)
	{
		super();
		this.geo = ge;
		this.kernel = this.geo.getKernel();
		this.algebraView = av;
		this.algebraController = ac;

		setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);

		this.check = new CheckBox();
		this.check.setChecked(AlgebraViewTreeItem.this.previouslyChecked = ge
				.isEuclidianVisible());
		this.check.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				AlgebraViewTreeItem.this.check
						.setChecked(AlgebraViewTreeItem.this.previouslyChecked = !AlgebraViewTreeItem.this.previouslyChecked);
				AlgebraViewTreeItem.this.geo
						.setEuclidianVisible(!AlgebraViewTreeItem.this.geo
								.isSetEuclidianVisible());
				AlgebraViewTreeItem.this.geo.update();
				AlgebraViewTreeItem.this.geo.getKernel().getApplication()
						.storeUndoInfo();
				AlgebraViewTreeItem.this.geo.getKernel().notifyRepaint();
			}
		});
		add(this.check);

		SpanElement se = DOM.createSpan().cast();
		se.getStyle().setProperty("display", "-moz-inline-box");
		se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		se.getStyle().setColor(
				GColor.getColorString(this.geo.getAlgebraColor()));
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
		} else
		{
			switch (this.kernel.getAlgebraStyle())
			{
			case Kernel.ALGEBRA_STYLE_VALUE:
				text = this.geo.getAlgebraDescriptionTextOrHTMLDefault();
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				text = this.geo
						.addLabelTextOrHTML(this.geo
								.getDefinitionDescription(StringTemplate.defaultTemplate));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				text = this.geo.addLabelTextOrHTML(this.geo
						.getCommandDescription(StringTemplate.defaultTemplate));
				break;
			default:
				break;
			}
		}

		// if enabled, render with LaTeX
		if (/* TODO this.av.isRenderLaTeX() && */this.kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE)
		{
			String latexStr = this.geo.getLaTeXAlgebraDescription(true,
					StringTemplate.latexTemplate);
			if ((latexStr != null)
					&& this.geo.isLaTeXDrawableGeo(latexStr)
					&& (this.geo.isGeoList() ? !((GeoList) this.geo).isMatrix()
							: true))
			{
				latexStr = inputLatexCosmetics(latexStr);
				this.seMayLatex = se;
				DrawEquationWeb.drawEquationAlgebraView(this.seMayLatex,
						latexStr, this.geo.getAlgebraColor(), GColor.white);
				this.LaTeX = true;
			} else
			{
				this.seNoLatex = se;
				this.seNoLatex.setInnerHTML(text);
			}
		} else
		{
			this.seNoLatex = se;
			this.seNoLatex.setInnerHTML(text);
		}
		// FIXME: geo.getLongDescription() doesn't work
		// geo.getKernel().getApplication().setTooltipFlag();
		// se.setTitle(geo.getLongDescription());
		// geo.getKernel().getApplication().clearTooltipFlag();
	}

	public void update()
	{
		// check for new LaTeX
		boolean newLaTeX = false;
		String text = null;
		if (/* TODO av.isRenderLaTeX() && */this.kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE)
		{
			text = this.geo.getLaTeXAlgebraDescription(true,
					StringTemplate.latexTemplate);
			if ((text != null)
					&& this.geo.isLaTeXDrawableGeo(text)
					&& (this.geo.isGeoList() ? !((GeoList) this.geo).isMatrix()
							: true))
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
			} else
			{
				switch (this.kernel.getAlgebraStyle())
				{
				case Kernel.ALGEBRA_STYLE_VALUE:
					text = this.geo.getAlgebraDescriptionTextOrHTMLDefault();
					break;

				case Kernel.ALGEBRA_STYLE_DEFINITION:
					text = this.geo
							.addLabelTextOrHTML(this.geo
									.getDefinitionDescription(StringTemplate.defaultTemplate));
					break;

				case Kernel.ALGEBRA_STYLE_COMMAND:
					text = this.geo
							.addLabelTextOrHTML(this.geo
									.getCommandDescription(StringTemplate.defaultTemplate));
					break;
				default:
					break;
				}
			}
		}

		// now we have text and how to display it (newLaTeX/LaTeX)
		if (this.LaTeX && newLaTeX)
		{
			text = inputLatexCosmetics(text);
			DrawEquationWeb.updateEquationMathQuill(text, this.seMayLatex);
		} else if (!this.LaTeX && !newLaTeX)
		{
			this.seNoLatex.setInnerHTML(text);
		} else if (newLaTeX)
		{
			SpanElement se = DOM.createSpan().cast();
			se.getStyle().setProperty("display", "-moz-inline-box");
			se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			se.getStyle().setColor(
					GColor.getColorString(this.geo.getAlgebraColor()));
			this.ihtml.getElement().replaceChild(se, this.seNoLatex);
			text = inputLatexCosmetics(text);
			this.seMayLatex = se;
			DrawEquationWeb.drawEquationAlgebraView(this.seMayLatex, text,
					this.geo.getAlgebraColor(), GColor.white);
			this.LaTeX = true;
		} else
		{
			SpanElement se = DOM.createSpan().cast();
			se.getStyle().setProperty("display", "-moz-inline-box");
			se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			se.getStyle().setColor(
					GColor.getColorString(this.geo.getAlgebraColor()));
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

	public static String inputLatexCosmetics(String eqstring)
	{
		// make sure eg FractionText[] works (surrounds with {} which doesn't
		// draw
		// well in MathQuill)
		if (eqstring.length() >= 2)
			if (eqstring.startsWith("{") && eqstring.endsWith("}"))
			{
				eqstring = eqstring.substring(1, eqstring.length() - 1);
			}

		// remove $s
		eqstring = eqstring.trim();
		while (eqstring.startsWith("$"))
			eqstring = eqstring.substring(1).trim();
		while (eqstring.endsWith("$"))
			eqstring = eqstring.substring(0, eqstring.length() - 1).trim();

		// remove all \; and \,
		eqstring = eqstring.replace("\\;", "");
		eqstring = eqstring.replace("\\,", "");

		eqstring = eqstring.replace("\\left\\{", "\\lbrace");
		eqstring = eqstring.replace("\\right\\}", "\\rbrace");
		return eqstring;
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
			GeoElement geo2 = this.kernel.getAlgebraProcessor()
					.changeGeoElement(this.geo, newValue, redefine, true);
			if (geo2 != null)
			{
				this.geo = geo2;
			}
		}

		// maybe it's possible to enter something which is LaTeX
		// note: this should be OK for independent GeoVectors too
		update();
	}

	public void stopEditing(String newValue)
	{

		this.thisIsEdited = false;
		this.algebraView.cancelEditing();

		if (newValue != null)
		{
			// Formula Hacks ... Currently only functions are considered
			StringBuilder sb = new StringBuilder();
			boolean switchw = false;
			for (int i = 0; i < newValue.length(); i++)
				if (newValue.charAt(i) != ' ')
				{
					if (newValue.charAt(i) != '|')
						sb.append(newValue.charAt(i));
					else if (switchw = !switchw)
						sb.append("abs(");
					else
						sb.append(")");
				}
			newValue = sb.toString();

			// Formula Hacks ended.
			boolean redefine = !this.geo.isPointOnPath();
			GeoElement geo2 = this.kernel.getAlgebraProcessor()
					.changeGeoElement(this.geo, newValue, redefine, true);
			if (geo2 != null)
			{
				this.geo = geo2;
			}
		}

		// maybe it's possible to enter something which is non-LaTeX
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
		if (((MobileAlgebraController) this.algebraController)
				.handleEvent(this.geo))
		{
			this.kernel.notifyRepaint();
		}
	}

}