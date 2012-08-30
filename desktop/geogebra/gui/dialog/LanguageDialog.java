package geogebra.gui.dialog;

import geogebra.common.util.Language;
import geogebra.common.util.Unicode;
import geogebra.gui.dialog.options.OptionsUtil;
import geogebra.gui.menubar.LanguageActionListener;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Dialog to select language
 * 
 * @author G. Sturr
 * 
 */
public class LanguageDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AppD app;
	private static AbstractAction setFlagIconAction;
	private HashMap<Language, JLabel> lblList;
	private Locale oldLocale;

	private JButton btnOK;

	private JButton btnCancel;

	/*******************************************
	 * Construct the dialog
	 * 
	 * @param app
	 */
	public LanguageDialog(AppD app) {
		super(app.getFrame(), true);

		this.app = app;
		oldLocale = app.getLocale();
		lblList = new HashMap<Language, JLabel>();

		initActions();

		getContentPane().add(createLanguageSelectionPanel(),
				BorderLayout.CENTER);
		createButtonPanel();
		//getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);

		setLabels();
		pack();
		this.setLocationRelativeTo(app.getFrame());

	}

	private JPanel createButtonPanel() {
		btnOK = new JButton();
		btnOK.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});

		btnCancel = new JButton();
		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (app.getLocale() != oldLocale) {
					app.setLocale(oldLocale);
				}
				setVisible(false);
			}
		});

		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		p.add(btnCancel);
		p.add(btnOK);
		p.add(Box.createHorizontalStrut(10));

		return p;
	}

	/**
	 * Create a list with all languages which can be selected.
	 * 
	 * @param menu
	 * @param al
	 */
	private JPanel createLanguageSelectionPanel() {

		JPanel list = new JPanel();
		list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

		JCheckBox mi;
		JLabel lbl;
		ActionListener al = new LanguageActionListener(app);
		ButtonGroup bg = new ButtonGroup();

		String currentLocale = app.getLocale().toString();

		// change en_GB into enGB
		currentLocale = currentLocale.replaceAll("_", "");
		StringBuilder sb = new StringBuilder(20);

		lblList.clear();

		for (Language loc : Language.values()) {

			// enforce to show specialLanguageNames first
			// because here getDisplayLanguage doesn't return a good result
			String text = loc.name;

			char ch = text.charAt(0);

			if (ch == Unicode.LeftToRightMark || ch == Unicode.RightToLeftMark) {
				ch = text.charAt(1);
			} else {
				// make sure brackets are correct in Arabic, ie not )US)
				sb.setLength(0);
				sb.append(Unicode.LeftToRightMark);
				sb.append(text);
				sb.append(Unicode.LeftToRightMark);
				text = sb.toString();
			}

			mi = new JCheckBox(text);
			mi.setFocusable(false);
			lbl = new JLabel();
			lbl.setBackground(Color.white);
			lbl.setOpaque(true);

			lblList.put(loc, lbl);

			// make sure eg Malayalam, Georgian drawn OK (not in standard Java
			// font)
			lbl.setFont(app.getFontCanDisplayAwt(text, false, Font.PLAIN,
					app.getGUIFontSize()));

			if (loc.locale.equals(currentLocale)) {
				mi.setSelected(true);
				lbl.setIcon(app.getFlagIcon(app.getFlagName(false)));
			} else {
				lbl.setIcon(app.getEmptyIcon());
			}
			mi.setActionCommand(loc.locale);
			mi.addActionListener(al);
			mi.addActionListener(setFlagIconAction);

			bg.add(mi);

			JPanel item = OptionsUtil.flowPanel(2, 0, 10, lbl, mi);
			item.setBackground(Color.white);
			item.setOpaque(true);
			// item.setBorder(BorderFactory.createEmptyBorder(2, 20, 2, 2));
			list.add(item);

		}

		JScrollPane scroller = new JScrollPane(list);
		Dimension d = list.getPreferredSize();
		d.height = 300;
		d.width += 30; // hack to include scroller insets

		scroller.setPreferredSize(d);
		scroller.getVerticalScrollBar().setUnitIncrement(30);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		 
		JPanel p = new JPanel(new BorderLayout());
		p.add(scroller, BorderLayout.CENTER);
		// p.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		return p;
	}

	private void initActions() {

		setFlagIconAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						setFlag();
						setLabels();
					}
				});

			}
		};

	}

	/**
	 * Sets list icons to show the flag for the current (selected) locale or
	 * show an empty icon otherwise.
	 */
	protected void setFlag() {

		String currentLocale = app.getLocale().toString();
		// change en_GB into enGB
		currentLocale = currentLocale.replaceAll("_", "");

		ImageIcon ic = app.getFlagIcon(app.getFlagName(false));

		for (Language loc : lblList.keySet()) {

			if (ic != null && loc.locale.equals(currentLocale)) {

				lblList.get(loc).setIcon(
						app.getFlagIcon(app.getFlagName(false)));
			} else {
				lblList.get(loc).setIcon(app.getEmptyIcon());
			}
		}
	}

	/**
	 * 
	 */
	public void setLabels() {
		setTitle(app.getMenu("Language"));

		btnCancel.setText(app.getPlain("Cancel"));
		btnOK.setText(app.getPlain("OK"));
	}

}
