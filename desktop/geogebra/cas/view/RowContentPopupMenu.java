package geogebra.cas.view;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.main.Application;
import geogebra.common.util.StringUtil;
import geogebra.gui.util.ImageSelection;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Provides a popup menu for copying the text of a {@link GeoCasCell} to the
 * clipboard or to LaTeX.
 * 
 * @author Johannes Renner
 */
public class RowContentPopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final GeoCasCell value;
	private final CASTable table;

	/**
	 * initializes the menu
	 * 
	 * @param value
	 *            the {@link GeoCasCell} containing the value to copy
	 * @param table
	 *            needed to get the {@link Application}
	 */
	public RowContentPopupMenu(GeoCasCell value, CASTable table) {
		this.value = value;
		this.table = table;

		initMenu();
	}

	private void initMenu() {
		JMenuItem copyItem = new JMenuItem(table.getApplication().getMenu("Copy"));
		copyItem.setActionCommand("copy");
		copyItem.addActionListener(this);
		add(copyItem);
		addSeparator();

		JMenuItem copyToLatexItem = new JMenuItem(
				table.getApplication().getMenu("CopyAsLaTeX"));
		copyToLatexItem.setActionCommand("copyAsLatex");
		copyToLatexItem.addActionListener(this);
		add(copyToLatexItem);
		
		JMenuItem copyToLibreOfficeItem = new JMenuItem(
				table.getApplication().getMenu("CopyAsLibreOfficeMath"));
		copyToLibreOfficeItem.setActionCommand("copyAsLibreOfficeMath");
		copyToLibreOfficeItem.addActionListener(this);
		add(copyToLibreOfficeItem);

		JMenuItem copyToImageItem = new JMenuItem(
				table.getApplication().getMenu("CopyAsImage"));
		copyToImageItem.setActionCommand("copyAsImage");
		copyToImageItem.addActionListener(this);
		add(copyToImageItem);
	}

	/**
	 * handles the {@link ActionEvent}s
	 */
	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();

		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable data = null;

		if (ac.equals("copy")) {
			data = new StringSelection(
					value.getOutput(StringTemplate.defaultTemplate));
		} else if (ac.equals("copyAsLatex")) {
			String latexOutput = value.getLaTeXOutput();
			data = new StringSelection(StringUtil.toLaTeXString(latexOutput,
					true));
			
		} else if (ac.equals("copyAsLibreOfficeMath")) {
			String libreofficeOutput = value.getOutput(StringTemplate.libreofficeTemplate);
			data = new StringSelection(libreofficeOutput);
			
		}else if (ac.equals("copyAsImage")) {
			ImageIcon latexIcon = new ImageIcon();
			Application app = (Application)table.getApplication();
			Font latexFont = new Font(app.getPlainFont().getName(),
					app.getPlainFont().getStyle(), app
							.getPlainFont().getSize() - 1);

			app.getDrawEquation().drawLatexImageIcon(app,
					latexIcon, value.getLaTeXOutput(), latexFont, false,
					geogebra.awt.Color.getAwtColor(value.getAlgebraColor()), table.getBackground());

			data = new ImageSelection(latexIcon.getImage());
		}

		if (data == null) {
			throw new NullPointerException("Transferable data is null");
		}

		sysClip.setContents(data, null);
	}
}
