package geogebra.cas.view;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.main.Application;
import geogebra.common.util.StringUtil;
import geogebra.gui.util.ImageSelection;

import java.awt.AWTException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

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
		JMenuItem copyItem = new JMenuItem(table.app.getMenu("Copy"));
		copyItem.setActionCommand("copy");
		copyItem.addActionListener(this);
		add(copyItem);

		JMenuItem copyToLaTeXItem = new JMenuItem(
				table.app.getMenu("CopyToLaTeX"));
		copyToLaTeXItem.setActionCommand("copyToLaTeX");
		copyToLaTeXItem.addActionListener(this);
		add(copyToLaTeXItem);

		JMenuItem copyToImageItem = new JMenuItem(
				table.app.getMenu("CopyToImage"));
		copyToImageItem.setActionCommand("copyToImage");
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
					value.toOutputValueString(StringTemplate.defaultTemplate));
		} else if (ac.equals("copyToLaTeX")) {
			data = new StringSelection(StringUtil.toLaTeXString(
					value.getLaTeXOutput(), true));
		} else if (ac.equals("copyToImage")) {
			try {
				Robot robot = new Robot();
				Point tableLocation = table.getLocationOnScreen();
				Rectangle rowBounds = getRowBounds(table, value.getRowNumber());
				rowBounds.setLocation((int) tableLocation.getX(),
						(int) (tableLocation.getY() + rowBounds.getLocation()
								.getY()));
				BufferedImage image = robot.createScreenCapture(rowBounds);

				data = new ImageSelection(image);
			} catch (AWTException ex) {
				ex.printStackTrace();
			}
		}

		if (data == null) {
			throw new NullPointerException("Transferable data is null");
		}

		sysClip.setContents(data, null);
	}

	/**
	 * Returns the bounds of a {@link JTable} row
	 * 
	 * @param table
	 *            the {@link JTable} where the row is
	 * @param row
	 *            the number of the row where the bounds are needed
	 * @return the bounds of the row
	 */
	private static Rectangle getRowBounds(JTable table, int row) {
		Rectangle result = table.getCellRect(row, -1, true);
		Insets i = table.getInsets();

		result.x = i.left;
		result.width = table.getWidth() - i.left - i.right;

		return result;
	}
}
