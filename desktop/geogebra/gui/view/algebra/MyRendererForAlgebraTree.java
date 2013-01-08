package geogebra.gui.view.algebra;

import geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Algebra view cell renderer
 * 
 * @author Markus
 */
public class MyRendererForAlgebraTree extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	protected AppD app;
	private AlgebraTree view;
	protected Kernel kernel;
	private ImageIcon iconShown, iconHidden;

	private ImageIcon latexIcon;
	private String latexStr = null;

	private Font latexFont;

	public MyRendererForAlgebraTree(AppD app, AlgebraTree view) {
		setOpaque(true);
		this.app = app;
		this.kernel = app.getKernel();

		iconShown = app.getImageIcon("shown.gif");
		iconHidden = app.getImageIcon("hidden.gif");

		setOpenIcon(app.getImageIcon("tree-close.png"));
		setClosedIcon(app.getImageIcon("tree-open.png"));

		latexIcon = new ImageIcon();
		this.view = view;
		
		this.setIconTextGap(8);
	}
	

	/**
	 * 
	 * @param geo geo
	 * @return description of the geo
	 */
	protected String getDescription(GeoElement geo){

		return geo.getLabelTextOrHTML();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		// Application.debug("getTreeCellRendererComponent: " + value);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object ob = node.getUserObject();

		if (ob instanceof GeoElement) {
			GeoElement geo = (GeoElement) ob;
			setForeground(geogebra.awt.GColorD.getAwtColor(geo.getAlgebraColor()));

			String text = getDescription(geo);
			
			

			// make sure we use a font that can display the text
			setFont(app.getFontCanDisplayAwt(text, Font.BOLD));
			setText(text);

			if (geo.doHighlighting())
				setBackground(AppD.COLOR_SELECTION);
			else
				setBackground(getBackgroundNonSelectionColor());

			// ICONS
			if (geo.isEuclidianVisible()) {
				setIcon(iconShown);
			} else {
				setIcon(iconHidden);
			}

			// if enabled, render with LaTeX
			if (view.isRenderLaTeX()
					&& kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
				latexFont = new Font(app.getBoldFont().getName(), app
						.getBoldFont().getStyle(), app.getFontSize() - 1);
				latexStr = geo.getLaTeXAlgebraDescription(true,
						StringTemplate.latexTemplate);
				if (latexStr != null && geo.isLaTeXDrawableGeo()) {
					latexStr = "\\;" + latexStr; // add a little space for the
													// icon
					app.getDrawEquation().drawLatexImageIcon(app, latexIcon,
							latexStr, latexFont, false, getForeground(),
							this.getBackground());
					setIcon(joinIcons((ImageIcon) getIcon(), latexIcon));
					setText(" ");
				}
			}

			// sometimes objects do not identify themselves as GeoElement for a
			// second,
			// causing the else-part to give them a border (because they have no
			// children)
			// we have to remove this border to prevent an unnecessary indent
			setBorder(null);

		}
		
		// no GeoElement
		else {
			// has children, display icon to expand / collapse the node
			if (!node.isLeaf()) {
				if (expanded) {
					setIcon(getOpenIcon());
				} else {
					setIcon(getClosedIcon());
				}

				setBorder(null);
			}

			// no children, display no icon
			else {
				// align all elements, therefore add the space the icon would
				// normally take as a padding
				setBorder(BorderFactory.createEmptyBorder(0, getOpenIcon()
						.getIconWidth() + getIconTextGap(), 0, 0));
				setIcon(null);
			}

			setForeground(Color.black);
			setBackground(getBackgroundNonSelectionColor());
			
			String str = (view.getTreeMode() == SortMode.LAYER) ?  app.getPlain("LayerA" , value.toString()) :
				value.toString();
			
			setText(str);

			// make sure we use a font that can display the text
			setFont(app.getFontCanDisplayAwt(str));
		}
		return this;
	}

	/**
	 * 
	 * @param geo
	 * @return algebra description of the geo
	 */
	protected static String getAlgebraDescriptionTextOrHTML(GeoElement geo) {
		return geo
				.getAlgebraDescriptionTextOrHTMLDefault();
	}

	/**
	 * Creates a new ImageIcon by joining them together (leftIcon to rightIcon).
	 * 
	 * @param leftIcon
	 * @param rightIcon
	 * @return
	 */
	private static ImageIcon joinIcons(ImageIcon leftIcon, ImageIcon rightIcon) {

		int w1 = leftIcon.getIconWidth();
		int w2 = rightIcon.getIconWidth();
		int h1 = leftIcon.getIconHeight();
		int h2 = rightIcon.getIconHeight();
		int h = Math.max(h1, h2);
		int mid = h / 2;
		BufferedImage image = new BufferedImage(w1 + w2, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.drawImage(leftIcon.getImage(), 0, mid - h1 / 2, null);
		g2.drawImage(rightIcon.getImage(), w1, mid - h2 / 2, null);
		g2.dispose();

		ImageIcon ic = new ImageIcon(image);
		return ic;
	}

	/**
	 * Overrides setFont to also set the LaTeX font.
	 */
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		// latexFont = font;
		// use a slightly smaller font for LaTeX
	}
} // MyRenderer