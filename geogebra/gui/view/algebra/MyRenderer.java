package geogebra.gui.view.algebra;

import geogebra.euclidian.Drawable;
import geogebra.euclidian.FormulaDimension;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Algebra view cell renderer 
 * @author Markus
 */
public class MyRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;				

	protected Application app;
	private AlgebraView view;
	private Kernel kernel;
	private ImageIcon iconShown, iconHidden;

	private ImageIcon latexIcon;
	private String latexStr = null;

	private Font latexFont;

	public MyRenderer(Application app, AlgebraView view) {
		setOpaque(true);		
		this.app = app;
		this.kernel = app.getKernel();

		iconShown = app.getImageIcon("shown.gif");
		iconHidden = app.getImageIcon("hidden.gif");

		setOpenIcon(app.getImageIcon("tree-close.png"));
		setClosedIcon(app.getImageIcon("tree-open.png"));

		latexIcon = new ImageIcon();
		this.view = view;
	}

	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean selected,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {	

		//Application.debug("getTreeCellRendererComponent: " + value);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;			
		Object ob = node.getUserObject();

		if (ob instanceof GeoElement) {	
			GeoElement geo = (GeoElement) ob;	
			setForeground(geo.getAlgebraColor());

			String text = null;
			if (geo.isIndependent()) {
				text = getAlgebraDescriptionTextOrHTML(geo);
			} else {
				switch (kernel.getAlgebraStyle()) {
				case Kernel.ALGEBRA_STYLE_VALUE:
					text = getAlgebraDescriptionTextOrHTML(geo);
					break;

				case Kernel.ALGEBRA_STYLE_DEFINITION:
					text = geo.addLabelTextOrHTML(geo.getDefinitionDescription());
					break;

				case Kernel.ALGEBRA_STYLE_COMMAND:
					text = geo.addLabelTextOrHTML(geo.getCommandDescription());
					break;
				}	
			}

			// make sure we use a font that can display the text
			setFont(app.getFontCanDisplay(text, Font.BOLD));
			setText(text);

			if (geo.doHighlighting()) 
				setBackground(Application.COLOR_SELECTION);
			else 
				setBackground(getBackgroundNonSelectionColor());

			// ICONS               
			if (geo.isEuclidianVisible()) {
				setIcon(iconShown);
			} else {
				setIcon(iconHidden);
			}

			// if enabled, render with LaTeX
			if(view.isRenderLaTeX()  && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE){
				latexFont = new Font(app.getBoldFont().getName(), app.getBoldFont().getStyle(), app.getFontSize() - 1);
				latexStr = geo.getLaTeXAlgebraDescription(true);
				if(latexStr != null && geo.isLaTeXDrawableGeo(latexStr)){
					latexStr = "\\;" + latexStr; // add a little space for the icon
					drawLatexImageIcon(latexIcon, latexStr, latexFont, false, getForeground(), this.getBackground() );
					setIcon(joinIcons((ImageIcon) getIcon(),latexIcon));
					setText(" ");
				}
			}

			// sometimes objects do not identify themselves as GeoElement for a second,
			// causing the else-part to give them a border (because they have no children)
			// we have to remove this border to prevent an unnecessary indent
			setBorder(null);

		}								
		// no GeoElement
		else {			
			// has children, display icon to expand / collapse the node
			if(!node.isLeaf()) {
				if (expanded) {
					setIcon(getOpenIcon());
				} else {
					setIcon(getClosedIcon());
				}

				setBorder(null);
			}

			// no children, display no icon
			else {
				// align all elements, therefore add the space the icon would normally take as a padding 
				setBorder(BorderFactory.createEmptyBorder(0, getOpenIcon().getIconWidth() + getIconTextGap(), 0, 0));
				setIcon(null);
			}

			setForeground(Color.black);
			setBackground(getBackgroundNonSelectionColor());
			String str = value.toString();
			setText(str);

			// make sure we use a font that can display the text
			setFont(app.getFontCanDisplay(str));


		}		

		return this;
	}

	/**
	 * 
	 * @param geo
	 * @return algebra description of the geo
	 */
	protected String getAlgebraDescriptionTextOrHTML(GeoElement geo){
		return geo.getAlgebraDescriptionTextOrHTML();
	}


	/**
	 * Draw a LaTeX image in the cell icon. Drawing is done twice. First draw gives 
	 * the needed size of the image. Second draw renders the image with the correct
	 * dimensions.
	 */
	private void drawLatexImageIcon(ImageIcon latexIcon, String latex, Font font, boolean serif, Color fgColor, Color bgColor) {

		// Create image with dummy size, then draw into it to get the correct size
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		FormulaDimension d = new FormulaDimension();
		d = app.getDrawEquation().drawEquation(app, null, g2image, 0, 0, latex, font, serif, fgColor,
				bgColor, true);

		// Now use this size and draw again to get the final image
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		app.getDrawEquation().drawEquation(app, null, g2image, 0, 0, latex, font, serif, fgColor,
				bgColor, true);

		latexIcon.setImage(image);

	}

	/**
	 * Creates a new ImageIcon by joining them together (leftIcon to rightIcon).
	 * 
	 * @param leftIcon
	 * @param rightIcon
	 * @return
	 */
	private ImageIcon joinIcons(ImageIcon leftIcon, ImageIcon rightIcon){

		int w1 = leftIcon.getIconWidth();
		int w2 = rightIcon.getIconWidth();
		int h1 = leftIcon.getIconHeight();
		int h2 = rightIcon.getIconHeight();
		int h = Math.max(h1, h2);
		int mid = h/2;
		BufferedImage image = new BufferedImage(w1+w2, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.drawImage(leftIcon.getImage(), 0, mid - h1/2, null);
		g2.drawImage(rightIcon.getImage(), w1,  mid - h2/2, null);
		g2.dispose(); 

		ImageIcon ic = new ImageIcon(image);
		return ic;
	}

	/**
	 * Overrides setFont to also set the LaTeX font.
	 */
	@Override
	public void setFont(Font font){
		super.setFont(font);
		//latexFont = font;
		// use a slightly smaller font for LaTeX

	}


} // MyRenderer