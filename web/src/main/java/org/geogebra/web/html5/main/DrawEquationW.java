package org.geogebra.web.html5.main;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.gui.view.algebra.GeoContainer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.HasForegroundColor;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Insets;
import com.himamis.retex.renderer.web.DrawingFinishedCallback;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.graphics.ColorW;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

public class DrawEquationW extends DrawEquation {

	static boolean scriptloaded = false;
 




	private static Object initJLaTeXMath = null;

	public DrawEquationW() {
	}




	
	public static TeXIcon xcreateIcon(String latex, int size,
			int texIconStyle) {

		TeXFormula formula = null;
		try {
			formula = new TeXFormula(latex);
		} catch (Throwable t) {
			String[] msg = t.getMessage().split("\\n");
			formula = new TeXFormula("\\text{" + msg[msg.length - 1] + "}");
		}

		TeXIcon icon = null;
		try {
			icon = formula.new TeXIconBuilder()
					.setStyle(TeXConstants.STYLE_DISPLAY).setType(texIconStyle)
					.setSize(size).build();

			icon.setInsets(new Insets(5, 5, 5, 5));
			return icon;
		} catch (Exception e) {
			formula = new TeXFormula("\\text{Invalid LaTeX syntax.}");
			icon = formula.new TeXIconBuilder()
					.setStyle(TeXConstants.STYLE_DISPLAY).setType(texIconStyle)
					.setSize(size).build();

			icon.setInsets(new Insets(5, 5, 5, 5));
		}
		return icon;
	}

	@Override
	public GDimension drawEquation(App app1, GeoElementND geo,
			final GGraphics2D g2,
	        int x, int y, String latexString0, GFont font, boolean serif,
	        final GColor fgColor, GColor bgColor, boolean useCache,
			boolean updateAgain, final Runnable callback) {

			String eqstring = latexString0;

		TeXIcon icon = createIcon(eqstring, convertColor(fgColor), font,
				font.getLaTeXStyle(serif),
				null, null, app1);

			Graphics2DW g3 = new Graphics2DW(((GGraphics2DW) g2).getContext());
			g3.setDrawingFinishedCallback(new DrawingFinishedCallback() {

				public void onDrawingFinished() {
					((GGraphics2DW) g2).updateCanvasColor();
					if (callback != null) {
						callback.run();
					}

				}
			});
			icon.paintIcon(new HasForegroundColor() {
				@Override
				public Color getForegroundColor() {
					return FactoryProvider.INSTANCE.getGraphicsFactory()
							.createColor(fgColor.getRed(), fgColor.getGreen(),
									fgColor.getBlue());
				}
			}, g3, x, y);
			((GGraphics2DW) g2).updateCanvasColor();
			g3.maybeNotifyDrawingFinishedCallback();
			return new GDimensionW(icon.getIconWidth(), icon.getIconHeight());

	}

	private static void ensureJLMFactoryExists() {
		if (FactoryProvider.INSTANCE == null) {
			FactoryProvider.INSTANCE = new FactoryProviderGWT();
		}
	}














	/**
	 * In case we're in (editing) newCreationMode, then this method shall decide
	 * whether to show the autocomplete suggestions or hide them...
	 */
	public static native void showOrHideSuggestions(GeoContainer rbti,
			Element parentElement) /*-{
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var querr = elSecond.lastChild;

		if (querr.GeoGebraSuggestionPopupCanShow !== undefined) {
			// when the suggestions should pop up, we make them pop up,
			// when not, there may be two possibilities: we should hide the old,
			// or we should not hide the old... e.g. up/down arrows should not hide...
			// is there any other case? (up/down will unset later here)
			if (querr.GeoGebraSuggestionPopupCanShow === true) {
				@org.geogebra.web.html5.main.DrawEquationW::popupSuggestions(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;)(rbti);
			} else {
				@org.geogebra.web.html5.main.DrawEquationW::hideSuggestions(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;)(rbti);
			}
		}
	}-*/;

	public static void popupSuggestions(GeoContainer rbti) {
		rbti.popupSuggestions();
	}

	public static void hideSuggestions(GeoContainer rbti) {
		rbti.hideSuggestions();
	}

	public static void shuffleSuggestions(GeoContainer rbti, boolean down) {
		rbti.shuffleSuggestions(down);
	}







	public static void scrollJSOIntoView(JavaScriptObject jo,
	        GeoContainer rbti, Element parentElement,
	        boolean newCreationMode) {

		Element joel = Element.as(jo);
		joel.scrollIntoView();
		Element el = rbti.getScrollElement();
		// Note: the following hacks should only be made in
		// new creation mode! so boolean introduced...
		if (newCreationMode) {
			// if the cursor is on the right or on the left,
			// it would be good to scroll some more, to show the "X" closing
			// sign and the blue border of the window! How to know that?
			// let's compare their places, and if the difference is little,
			// scroll to the left/right!
			if (joel.getAbsoluteLeft() - parentElement.getAbsoluteLeft() < 50) {
				// InputTreeItem class in theory
				el.setScrollLeft(0);
			} else if (parentElement.getAbsoluteRight()
			        - joel.getAbsoluteRight() < 50) {
				// InputTreeItem class in theory
				el.setScrollLeft(el.getScrollWidth() - el.getClientWidth());
			} else if (joel.getAbsoluteLeft() - el.getAbsoluteLeft() < 50) {
				// we cannot show the "X" sign all the time anyway!
				// but it would be good not to keep the cursor on the
				// edge...
				// so if it is around the edge by now, scroll!
				el.setScrollLeft(el.getScrollLeft() - 50
						+ joel.getAbsoluteLeft() - el.getAbsoluteLeft());
			} else if (el.getAbsoluteRight()
			        - joel.getAbsoluteRight() < 50) {
				// similarly
				el.setScrollLeft(el.getScrollLeft() + 50
						- el.getAbsoluteRight()
				                + joel.getAbsoluteRight());
			}
		}
	}



	public static DrawEquationW getNonStaticCopy(GeoContainer rbti) {
		return (DrawEquationW) rbti.getApplication().getDrawEquation();
	}

	/*
	 * needed for avoid the pixelated appearance of LaTeX texts at printing
	 */
	private static double printScale = 1;

	public static void setPrintScale(double t) {
		printScale = t;
	}

	public static Canvas paintOnCanvas(GeoElement geo, String text0, Canvas c,
			int fontSize) {
		if (geo == null) {
			return c == null ? Canvas.createIfSupported() : c;
		}
		final GColor fgColor = geo.getAlgebraColor();
		if (c == null) {
			c = Canvas.createIfSupported();
		} else {
			c.getContext2d().fillRect(0, 0, c.getCoordinateSpaceWidth(),
					c.getCoordinateSpaceHeight());
		}
		JLMContext2d ctx = (JLMContext2d) c.getContext2d();
		AppW app = ((AppW) geo.getKernel().getApplication());
		app.getDrawEquation().checkFirstCall(app);
		GFont font = AwtFactory.prototype.newFont("geogebra", GFont.PLAIN,
				fontSize - 3);
		TeXIcon icon = app.getDrawEquation().createIcon(
				"\\mathsf{\\mathrm {" + text0 + "}}",
				app.getDrawEquation().convertColor(fgColor),
				font, font.getLaTeXStyle(false),
				null, null, app);
		Graphics2DInterface g3 = new Graphics2DW(ctx);

		double ratio = app.getPixelRatio() * printScale;
		int width = Math.min(icon.getIconWidth(), 20000);
		c.setCoordinateSpaceWidth((int) (width * ratio));
		c.setCoordinateSpaceHeight((int) (icon.getIconHeight() * ratio));
		c.getElement().getStyle().setWidth(width, Unit.PX);
		c.getElement().getStyle().setHeight(icon.getIconHeight(), Unit.PX);

		// c.getElement().getStyle().setMargin(4, Unit.PX);
		ctx.scale2(ratio, ratio);

		icon.paintIcon(new HasForegroundColor() {
			@Override
			public Color getForegroundColor() {
				return FactoryProvider.INSTANCE.getGraphicsFactory()
						.createColor(fgColor.getRed(), fgColor.getGreen(),
								fgColor.getBlue());
			}
		}, g3, 0, 0);
		return c;
	}



	@Override
	public GDimension measureEquation(App app, GeoElement geo0, int minValue,
			int minValue2, String text, GFont font, boolean serif) {
		return this.measureEquationJLaTeXMath(app, geo0, 0, 0, text, font,
				serif, null, null);
	}

	@Override
	public void checkFirstCall(App app) {
		ensureJLMFactoryExists();
		if (initJLaTeXMath == null) {

			StringBuilder initJLM = DrawEquation.getJLMCommands();
			initJLaTeXMath = new TeXFormula(initJLM.toString());
		}

	}

	@Override
	public Color convertColor(GColor color) {
		return new ColorW(color.getRed(), color.getGreen(), color.getBlue());
	}

	@Override
	protected Image getCachedDimensions(String text, GeoElementND geo,
			Color fgColor, GFont font, int style, int[] ret) {
		// TODO Auto-generated method stub
		return null;
	}
}
