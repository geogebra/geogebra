/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.desktop.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.CmdGetTime;
import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.inputfield.MyFormattedTextField;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;
import org.geogebra.desktop.main.LocalizationD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Panel with title, author and date of construction. Forwards all updates to
 * kernel and notifies attached ActionListeners about kernel changes. Thus, it
 * can be used to edit the aforementioned values in the kernel.
 * 
 * @author Markus Hohenwarter
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class TitlePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField titleField, authorField;

	private JFormattedTextField dateField;

	private ArrayList<ActionListener> listeners = new ArrayList<>();

	private Construction cons;
	private LocalizationD loc;

	public TitlePanel(AppD app) {
		cons = app.getKernel().getConstruction();
		loc = app.getLocalization();

		setLayout(new BorderLayout(5, 5));
		titleField = new MyTextFieldD(app);
		authorField = new MyTextFieldD(app);
		dateField = new MyFormattedTextField((GuiManagerD) app.getGuiManager(),
				DateFormat.getDateInstance(DateFormat.LONG));
		dateField.setColumns(12);
		dateField.setFocusLostBehavior(JFormattedTextField.PERSIST);
		dateField.setFont(app.getPlainFont());

		updateData();

		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.add(new JLabel(loc.getMenu("Title") + ": "), loc.borderWest());
		p.add(titleField, BorderLayout.CENTER);
		add(p, BorderLayout.NORTH);

		p = new JPanel(new BorderLayout(5, 5));
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(new JLabel(loc.getMenu("Author") + ": "), loc.borderWest());
		p1.add(authorField, BorderLayout.CENTER);
		p.add(p1, BorderLayout.CENTER);

		p1 = new JPanel(new BorderLayout());
		p1.add(new JLabel(loc.getMenu("Date") + ": "), loc.borderWest());
		p1.add(dateField, BorderLayout.CENTER);

		p.add(p1, loc.borderEast());
		add(p, BorderLayout.CENTER);

		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// setBorder(BorderFactory.createTitledBorder(app
		// .getPlain("Document info")));

		ActionListener lst = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireTextFieldUpdate((JTextField) e.getSource());
			}
		};
		titleField.addActionListener(lst);
		addDocListener(titleField);
		authorField.addActionListener(lst);
		addDocListener(authorField);
		dateField.addActionListener(lst);
		addDocListener(dateField);

		FocusAdapter focusListener = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				fireTextFieldUpdate((JTextField) e.getSource());
			}
		};
		titleField.addFocusListener(focusListener);
		authorField.addFocusListener(focusListener);
		dateField.addFocusListener(focusListener);
	}

	private void addDocListener(final JTextField authorField2) {
		authorField2.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				fireTextFieldUpdate(authorField2);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				fireTextFieldUpdate(authorField2);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				fireTextFieldUpdate(authorField2);
			}
		});

	}

	public void updateData() {
		titleField.setText(cons.getTitle());
		authorField.setText(loadAuthor());

		dateField.setText(configureDate(cons.getDate()));
	}

	public String configureDate(String src) {

		// If no date specified use current date
		if ("".equals(src)) {

			App app = cons.getApplication();

			// in form 23 September 2012 (some languages don't want eg 25e, 25a
			// so omit "th" for all)
			String format = app.getLocalization().isRightToLeftReadingOrder()
					? "\\Y " + Unicode.LEFT_TO_RIGHT_MARK + "\\F"
							+ Unicode.LEFT_TO_RIGHT_MARK + " \\j"
					: "\\j \\F \\Y";

			return CmdGetTime.buildLocalizedDate(format, new Date(),
					app.getLocalization());
		}

		return src;

	}

	public String loadAuthor() {
		String author = cons.getAuthor();
		if ("".equals(author)) {
			author = GeoGebraPreferencesD.getPref()
					.loadPreference(GeoGebraPreferencesD.AUTHOR, "");
			cons.setAuthor(author);
		}
		return author;
	}

	private boolean saveAuthor(String author) {
		boolean kernelChanged = !author.equals(cons.getAuthor());
		if (kernelChanged) {
			cons.setAuthor(author);
			GeoGebraPreferencesD.getPref()
					.savePreference(GeoGebraPreferencesD.AUTHOR, author);
		}
		return kernelChanged;
	}

	/**
	 * Updates the kernel if the user makes changes to the text fields.
	 */
	private void fireTextFieldUpdate(JTextField tf) {
		String text = tf.getText();
		boolean kernelChanged = false;

		if (tf == titleField) {
			if (text.equals(cons.getTitle())) {
				return;
			}
			cons.setTitle(text);
			kernelChanged = true;
		} else if (tf == authorField) {
			kernelChanged = saveAuthor(tf.getText());
		} else if (tf == dateField) {
			if (text.equals(cons.getDate())) {
				return;
			}
			cons.setDate(text);
			kernelChanged = true;
		}

		if (kernelChanged) {
			notifyListeners();
		}
	}

	public void addActionListener(ActionListener lst) {
		listeners.add(lst);
	}

	private void notifyListeners() {
		int size = listeners.size();
		for (int i = 0; i < size; i++) {
			listeners.get(i).actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, "TitleChanged"));
		}
	}

}
