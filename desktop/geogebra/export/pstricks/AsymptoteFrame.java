package geogebra.export.pstricks;

import geogebra.main.AppD;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;

/**
 * @author Andy Zhu
 */
public class AsymptoteFrame extends ExportFrame {
    private static final long serialVersionUID = 1L;
    protected JComboBox comboFontSizeMore;
    final String[] format = { "LaTeX (article class)", "LaTeX (beamer class)" },
             fontsizelist = { "7", "8", "9", "10", "11", "12" };

    /**
     * Initializes GUI.
     * 
     * @param ggb2asy
     */
    public AsymptoteFrame(final GeoGebraToAsymptote ggb2asy) {
        super(ggb2asy, ggb2asy.app.getMenu("GenerateCode"));
        fileExtension=AppD.FILE_EXT_ASY;
        fileExtensionMsg="Asymptote ";
        initGui();
        
    }

    /**
     * Opens GUI frame.
     * 
     */
    protected void initGui() {
        comboFormat = new JComboBox(format); // prevent break with extended
                                             // class

        comboFontSizeMore = new JComboBox(fontsizelist);
        comboFontSizeMore.setSelectedIndex(10 - Integer.parseInt(fontsizelist[0]));

        setTitle(app.getMenu("GeogebraToAsymptoteExport"));
        js.getViewport().add(textarea);
        panel.setLayout(new GridBagLayout());
        panel.add(labelXUnit, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(textXUnit, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(labelwidth, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(textwidth, new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(labelYUnit, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(textYUnit, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(labelheight, new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(textheight, new GridBagConstraints(3, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(labelXmin, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(textXmin, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(labelXmax, new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(textXmax, new GridBagConstraints(3, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(labelYmin, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(textYmin, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(labelYmax, new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(textYmax, new GridBagConstraints(3, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(labelFontSize, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(comboFontSizeMore, new GridBagConstraints(1, 4, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(labelFill, new GridBagConstraints(2, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(comboFill, new GridBagConstraints(3, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));
        panel.add(jcbAsyCompact, new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(jcbAsyCse5, new GridBagConstraints(2, 5, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(jcbShowAxes, new GridBagConstraints(2, 6, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(jcbGrayscale, new GridBagConstraints(0, 6, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(jcbDotColors, new GridBagConstraints(0, 7, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(jcbPairName, new GridBagConstraints(2, 7, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(button, new GridBagConstraints(0, 8, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(buttonSave, new GridBagConstraints(2, 8, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(button_copy, new GridBagConstraints(3, 8, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(js, new GridBagConstraints(0, 9, 4, 5, 1.0, 20.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        5, 5, 5, 5), 0, 0));
        textXUnit.setPreferredSize(new Dimension(110, textXUnit.getFont()
                .getSize() + 6));
        textYUnit.setPreferredSize(new Dimension(110, textYUnit.getFont()
                .getSize() + 6));
        js.setPreferredSize(new Dimension(400, 400));
        getContentPane().add(panel);
        centerOnScreen();
        setVisible(true);
    }

    // end changes.
    @Override
	protected boolean isBeamer() {
        // if (comboFormat.getSelectedIndex()==1) return true;
        return false;
    }

    @Override
	protected boolean isLaTeX() {
        return true;
    }

    @Override
	protected boolean isPlainTeX() {
        return false;
    }

    @Override
	protected boolean isConTeXt() {
        return false;
    }

    @Override
	public int getFontSize() {
        // overwrite to start at a minimum font of 7
        return comboFontSizeMore.getSelectedIndex()
                + Integer.parseInt(fontsizelist[0]);
    }
}
