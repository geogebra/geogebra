package org.geogebra.desktop.gui.view.algebra;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.ScaledIcon;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageManagerD;

/**
 * Algebra view cell renderer
 * 
 * @author Markus
 */
public class AlgebraTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	protected AppD app;
	protected final AlgebraTree view;
	protected Kernel kernel;
	private ScaledIcon iconShown;
	private ScaledIcon iconHidden;

	private ScaledIcon latexIcon;

	/**
	 * @param app application
	 * @param view AV
	 */
	public AlgebraTreeCellRenderer(AppD app, AlgebraTree view) {
		setOpaque(true);
		this.app = app;
		this.kernel = app.getKernel();
		update();
		this.view = view;

		this.setIconTextGap(8);
	}

	/**
	 * Update icons
	 */
	public void update() {
		ImageManagerD imageManager = app.getImageManager();
		iconShown = imageManager.getResponsiveScaledIcon(imageManager.getImageIcon(
				GuiResourcesD.ALGEBRA_SHOWN), 16);
		iconHidden = imageManager.getResponsiveScaledIcon(imageManager.getImageIcon(
				GuiResourcesD.ALGEBRA_HIDDEN), 16);

		setOpenIcon(app.getScaledIcon(GuiResourcesD.TREE_OPENED));
		setClosedIcon(app.getScaledIcon(GuiResourcesD.TREE_CLOSED));

		latexIcon = new ScaledIcon(this);
	}

	/**
	 * @param geo
	 *            geo
	 * @param node tree node
	 * @return description of the geo
	 */
	protected String getDescription(GeoElement geo, GeoMutableTreeNode node) {
		return geo.getLabelTextOrHTML();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean itemSelected, boolean expanded, boolean leaf, int row,
			boolean itemHasFocus) {

		if (value instanceof GeoMutableTreeNode) {
			GeoElement geo = ((GeoMutableTreeNode) value).getGeo();
			setForeground(GColorD.getAwtColor(geo.getAlgebraColor()));

			String text = getDescription(geo, (GeoMutableTreeNode) value);

			// make sure we use a font that can display the text
			setFont(app.getFontCanDisplayAwt(text, Font.BOLD));
			setText(text);

			if (geo.doHighlighting()) {
				setBackground(AppD.COLOR_SELECTION);
			} else {
				setBackground(getBackgroundNonSelectionColor());
			}

			// ICONS
			if (geo.isEuclidianVisible()) {
				setIcon(getIconShown());
			} else {
				setIcon(getIconHidden());
			}

			// if enabled, render with LaTeX
			AlgebraStyle algebraStyle = app.getAlgebraStyle();
			if (view.isRenderLaTeX() && algebraStyle == AlgebraStyle.VALUE
					&& geo.isDefined()
					&& geo.isLaTeXDrawableGeo()) {
				Font latexFont = new Font(app.getBoldFont().getName(),
						app.getBoldFont().getStyle(), app.getFontSize() - 1);
				String latexStr = geo.getLaTeXAlgebraDescription(true,
						StringTemplate.latexTemplate);
				if (latexStr != null) {
					latexStr = "\\;" + latexStr; // add a little space for the icon
					app.getDrawEquation().drawLatexImageIcon(app, latexIcon,
							latexStr, latexFont, false, getForeground(),
							this.getBackground());
					setIcon(ScaledIcon.joinIcons((ScaledIcon) getIcon(), latexIcon, this));
					setText(" ");
				}
			}

			// sometimes objects do not identify themselves as GeoElement for a
			// second,
			// causing the else-part to give them a border (because they have no
			// children)
			// we have to remove this border to prevent an unnecessary indent
			setBorder(null);
		} else { // no GeoElement
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
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
				setBorder(BorderFactory.createEmptyBorder(0,
						getOpenIcon().getIconWidth() + getIconTextGap(), 0, 0));
				setIcon(null);
			}

			setForeground(Color.black);
			setBackground(getBackgroundNonSelectionColor());

			String str = (view.getTreeMode() == SortMode.LAYER)
					? app.getLocalization().getPlain("LayerA", value.toString())
					: value.toString();

			setText(str);

			// make sure we use a font that can display the text
			setFont(app.getFontCanDisplayAwt(str));
		}
		return this;
	}

	public ScaledIcon getIconShown() {
		return iconShown;
	}

	public ScaledIcon getIconHidden() {
		return iconHidden;
	}
} // MyRenderer