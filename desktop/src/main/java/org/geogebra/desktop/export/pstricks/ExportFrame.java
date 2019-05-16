package org.geogebra.desktop.export.pstricks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.geogebra.common.export.pstricks.ExportSettings;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.desktop.main.AppD;

abstract public class ExportFrame extends JFrame implements ExportSettings {
	private static final long serialVersionUID = 1L;
	private static final String TEXT_XUNIT = "textxunit";
	private static final String TEXT_YUNIT = "textyunit";
	private static final String TEXT_WIDTH = "textwidth";
	private static final String TEXT_HEIGHT = "textheight";
	private static final String TEXT_XMAX = "textxmax";
	private static final String TEXT_XMIN = "textxmin";
	private static final String TEXT_YMAX = "textymax";
	private static final String TEXT_YMIN = "textymin";
	protected final TextValue textXUnit, textYUnit, textwidth, textheight;
	protected JLabel labelwidth, labelheight, labelXUnit, labelYUnit,
			labelFontSize, labelFormat;
	protected TextValue textXmin, textXmax, textYmin, textYmax;
	protected JLabel labelXmin, labelXmax, labelYmin, labelYmax;
	final String[] msg = { "10 pt", "11 pt", "12 pt" };
	protected JComboBox comboFontSize, comboFormat, comboFill, cbSliders;
	protected JLabel labelFill;

	// added by Hosszu Henrietta, for Animated PDF
	protected DefaultComboBoxModel comboModel;

	// end changes
	protected JPanel panel;
	protected JButton button, button_copy;
	protected JCheckBox jcbPointSymbol, jcbGrayscale,
			// Andy Zhu - for use in Asymptote Frame
			jcbShowAxes, jcbAsyCompact, jcbAsyCse5, jcbDotColors, jcbPairName;
	// end changes
	protected JScrollPane js;
	protected JTextArea textarea;
	protected AppD app;
	protected double width, height;
	protected JButton buttonSave;
	// private ExportFrame ef;
	protected File currentFile = null;
	private GeoGebraExport ggb;
	protected final Localization loc;
	ListenKey listenKey;
	protected FileExtensions fileExtension = FileExtensions.TEX;
	protected String fileExtensionMsg = "TeX ";

	// definition of the behaviour of the textValues corresponding
	// to xmin, xmax, ymin and ymax.
	// Explaination for xs:
	// if xmin is changed, then both xmin and xmax are changed
	// to be sure that everything is allright even though xmin is set
	// to a higher value than xmax
	// then the width is changed.
	public ExportFrame(final GeoGebraExport ggb, String action) {
		this.ggb = ggb;
		this.app = (AppD) ggb.getApp();
		loc = app.getLocalization();
		ggb.setFrame(this);

		width = ggb.getXmax() - ggb.getXmin();
		height = ggb.getYmax() - ggb.getYmin();
		listenKey = new ListenKey();
		textXUnit = new TextValue(this, String.valueOf(ggb.getXunit()), false,
				ExportFrame.TEXT_XUNIT);
		textYUnit = new TextValue(this, String.valueOf(ggb.getYunit()), false,
				ExportFrame.TEXT_YUNIT);
		textwidth = new TextValue(this, String.valueOf(width), false,
				ExportFrame.TEXT_WIDTH);
		textheight = new TextValue(this, String.valueOf(height), false,
				ExportFrame.TEXT_HEIGHT);
		textXmin = new TextValue(this, String.valueOf(ggb.getXmin()), true,
				ExportFrame.TEXT_XMIN);
		textXmax = new TextValue(this, String.valueOf(ggb.getxmax()), true,
				TEXT_XMAX);
		textYmin = new TextValue(this, String.valueOf(ggb.getymin()), true,
				TEXT_YMIN);
		textYmax = new TextValue(this, String.valueOf(ggb.getymax()), true,
				TEXT_YMAX);
		textXUnit.addKeyListener(listenKey);
		textYUnit.addKeyListener(listenKey);
		textXmin.addKeyListener(listenKey);
		textXmax.addKeyListener(listenKey);
		textwidth.addKeyListener(listenKey);
		textheight.addKeyListener(listenKey);
		textYmin.addKeyListener(listenKey);
		textYmax.addKeyListener(listenKey);

		panel = new JPanel();
		button = new JButton(loc.getMenu(action));
		button_copy = new JButton(loc.getMenu("CopyToClipboard"));
		labelXUnit = new JLabel(loc.getMenu("XUnits"));
		labelYUnit = new JLabel(loc.getMenu("YUnits"));
		labelwidth = new JLabel(loc.getMenu("PictureWidth"));
		labelheight = new JLabel(loc.getMenu("PictureHeight"));
		labelFontSize = new JLabel(loc.getMenu("LatexFontSize"));
		labelXmin = new JLabel(loc.getMenu("xmin"));
		labelXmax = new JLabel(loc.getMenu("xmax"));
		labelYmin = new JLabel(loc.getMenu("ymin"));
		labelYmax = new JLabel(loc.getMenu("ymax"));
		jcbPointSymbol = new JCheckBox(loc.getMenu("DisplayPointSymbol"));
		jcbGrayscale = new JCheckBox(loc.getMenu("PGFExport.Grayscale"));
		// Andy Zhu: for use in Asymptote frame
		jcbShowAxes = new JCheckBox(loc.getMenu("ShowAxesGrid"));
		jcbAsyCompact = new JCheckBox(loc.getMenu("ConciseCode"));
		jcbAsyCse5 = new JCheckBox(loc.getMenu("ConciseUsingCSE5"));
		jcbDotColors = new JCheckBox(loc.getMenu("KeepDotColors"));
		jcbPairName = new JCheckBox(loc.getMenu("UsePairNames"));
		jcbShowAxes.setSelected(true);
		jcbAsyCompact.setSelected(false);
		jcbAsyCse5.setSelected(false);
		jcbAsyCse5.setEnabled(false);
		jcbDotColors.setSelected(false);
		jcbAsyCompact.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jcbAsyCompact.isSelected()) {
					jcbAsyCse5.setEnabled(true);
					jcbPairName.setSelected(true);
				} else {
					jcbAsyCse5.setSelected(false);
					jcbAsyCse5.setEnabled(false);
				}
			}
		});
		final String[] comboFillText = { loc.getMenu("None"),
				loc.getMenu("OnlyOpaqueFills"), loc.getMenu("WithOpacityPen"),
				loc.getMenu("ByLayering") };

		comboFill = new JComboBox(comboFillText);
		labelFill = new JLabel(loc.getMenu("FillType") + ":");
		// end changes
		comboFontSize = new JComboBox(msg);
		jcbPointSymbol.setSelected(true);
		jcbGrayscale.setSelected(false);
		// combo box with all sliders, added by Hoszu Henrietta
		comboModel = new DefaultComboBoxModel();
		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();
		Iterator<GeoElement> it = sortedSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoNumeric() && ((GeoNumeric) geo).isIntervalMinActive()
					&& ((GeoNumeric) geo).isIntervalMaxActive()) {
				comboModel.addElement(geo);
			}
		}
		cbSliders = new JComboBox(comboModel);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ggb.setBeamer(isBeamer());
				ggb.generateAllCode();
			}
		});
		button_copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textarea.copy();
			}
		});
		js = new JScrollPane();
		textarea = new JTextArea();
		buttonSave = new JButton(loc.getMenu("SaveAs"));
		buttonSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentFile = app.getGuiManager().showSaveDialog(fileExtension,
						currentFile, fileExtensionMsg + loc.getMenu("Files"),
						true, false);
				if (currentFile == null) {
					return;
				}
				try {

					FileOutputStream f = new FileOutputStream(currentFile);
					BufferedOutputStream b = new BufferedOutputStream(f);
					/*
					 * java.util.Enumeration en=System.getProperties().keys();
					 * while(en.hasMoreElements()){ String
					 * s=en.nextElement().toString(); System.out.println(s+" "
					 * +System.getProperty(s)); }
					 */
					OutputStreamWriter osw = new OutputStreamWriter(b,
							Charsets.getUtf8());
					StringBuilder sb = new StringBuilder(textarea.getText());
					if (isLaTeX()) {
						int id = sb.indexOf("\\usepackage{");
						if (id != -1) {
							sb.insert(id, "\\usepackage[utf8]{inputenc}\n");
						}
					} else if (isConTeXt()) {
						int id = sb.indexOf("\\usemodule[");
						if (id != -1) {
							sb.insert(id, "\\enableregime[utf]\n");
						}
					}
					osw.write(sb.toString());
					osw.close();
					b.close();
					f.close();
				} catch (FileNotFoundException e1) {
				} catch (UnsupportedEncodingException e2) {
				} catch (IOException e3) {
				}
			}
		});
	}

	protected void centerOnScreen() {
		// center on screen
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

	@Override
	public boolean isGrayscale() {
		return jcbGrayscale.isSelected();
	}

	@Override
	public boolean getExportPointSymbol() {
		return jcbPointSymbol.isSelected();
	}

	@Override
	public double getXUnit() {
		double d;
		try {
			d = textXUnit.getValue();
		} catch (NumberFormatException e) {
			d = 1;
		}
		return d;
	}

	@Override
	public GeoNumeric getcbSlidersItem() {
		return (GeoNumeric) cbSliders.getSelectedItem();
	}

	@Override
	public double getYUnit() throws NumberFormatException {
		double d;
		try {
			d = textYUnit.getValue();
		} catch (NumberFormatException e) {
			d = 1;
		}
		return d;
	}

	@Override
	public double getLatexHeight() {
		return textheight.getValue();
	}

	@Override
	public double getLatexWidth() {
		return textwidth.getValue();
	}

	@Override
	public void write(StringBuilder sb) {
		textarea.setText(new String(sb));
		textarea.selectAll();
	}

	@Override
	public int getFontSize() {
		switch (comboFontSize.getSelectedIndex()) {
		case 0:
			return 10;
		case 1:
			return 11;
		case 2:
			return 12;
		}
		return 10;
	}

	@Override
	public int getFormat() {
		return comboFormat.getSelectedIndex();
	}

	// Andy Zhu - for use in Asymptote frame
	@Override
	public boolean getShowAxes() {
		return jcbShowAxes.isSelected();
	}

	@Override
	public boolean getAsyCompact() {
		return jcbAsyCompact.isSelected();
	}

	@Override
	public boolean getAsyCompactCse5() {
		return jcbAsyCse5.isSelected();
	}

	@Override
	public boolean getKeepDotColors() {
		return jcbDotColors.isSelected();
	}

	@Override
	public boolean getUsePairNames() {
		return jcbPairName.isSelected();
	}

	@Override
	public int getFillType() {
		return comboFill.getSelectedIndex();
	}

	// end changes

	@Override
	public double textYmaxValue() {
		return this.textYmax.getValue();
	}

	@Override
	public double textYminValue() {
		return this.textYmin.getValue();
	}

	protected abstract boolean isLaTeX();

	protected abstract boolean isConTeXt();

	protected abstract boolean isPlainTeX();

	protected abstract boolean isBeamer();

	/*
	 * class EncodingDialog extends JDialog implements ActionListener{ private
	 * static final long serialVersionUID = 1L; private JComboBox menu; private
	 * HashMap encode; private JLabel labelInputenc; private JLabel labelBabel;
	 * private JButton button; private JTextArea zone; String encoding="";
	 * EncodingDialog(ExportFrame ef){ super(ef,true);
	 * setTitle(loc.getPlain("PGFExport.Encoding")); encode=new HashMap();
	 * encode.put("ansinew","windows-1252"); encode.put("ascii","US-ASCII");
	 * encode.put("cp1250","windows-1250"); encode.put("cp1252","windows-1252");
	 * encode.put("cp1257","windows-1257"); encode.put("cp437","Cp437");
	 * encode.put("cp850","Cp850"); encode.put("cp852","Cp852");
	 * encode.put("cp858","Cp858"); encode.put("cp865","Cp865");
	 * encode.put("latin1","ISO-8859-1"); encode.put("latin2","ISO-8859-2");
	 * encode.put("latin3","ISO-8859-3"); encode.put("latin4","ISO-8859-4");
	 * encode.put("latin5","ISO-8859-9"); encode.put("latin9","ISO-8859-15");
	 * encode.put("latin10","ISO-8859-10"); encode.put("utf8","UTF-8" );
	 * encode.put("macce","MacCentralEurope"); encode.put("applemac","");
	 * encode.put("koi8-r","KOI8-R"); menu=new JComboBox();
	 * 
	 * button=new JButton("\u21B5"); button.addActionListener(this);
	 * button.setActionCommand("button"); zone=new JTextArea();
	 * 
	 * java.util.Iterator it=encode.keySet().iterator(); while(it.hasNext()){
	 * String key=it.next().toString(); menu.addItem(key); }
	 * menu.addActionListener(this); menu.setActionCommand("combo");
	 * setLayout(new BorderLayout()); add(menu,BorderLayout.NORTH);
	 * add(button,app.borderEast()); add(zone,BorderLayout.CENTER);
	 * setSize(200,300); setVisible(true); } public void
	 * actionPerformed(ActionEvent e){ String cmd=e.getActionCommand(); if
	 * ("button".equals(cmd)){
	 * encoding=encode.get(menu.getSelectedItem().toString()).toString();
	 * dispose(); } else if ("combo".equals(cmd)){ if (isLaTeX()){ StringBuilder
	 * sb=new StringBuilder(); sb.append("\\usepackage[");
	 * sb.append(encode.get(menu.getSelectedItem().toString()));
	 * sb.append("]{inputenc}\n"); zone.setText(sb.toString()); } else if
	 * (isConTeXt()){
	 * 
	 * } } } String getEncoding(){ return encoding; } }
	 */
	class ListenKey extends KeyAdapter {


		@Override
		public void keyReleased(KeyEvent e) {
			String cmd = e.getSource().toString();
			if (cmd.equals(TEXT_XUNIT)) {
				try {
					double value = textXUnit.getValue();
					ggb.setXunit(value);
					textwidth.setValue(value * width);
				} catch (NumberFormatException e1) {
				}
			} else if (cmd.equals(TEXT_YUNIT)) {
				try {
					double value = textYUnit.getValue();
					ggb.setYunit(value);
					textheight.setValue(value * height);
				} catch (NumberFormatException e1) {
				}
			} else if (cmd.equals(TEXT_WIDTH)) {
				try {
					double value = textwidth.getValue() / width;
					ggb.setXunit(value);
					textXUnit.setValue(value);
				} catch (NumberFormatException e1) {
				}
			} else if (cmd.equals(TEXT_HEIGHT)) {
				try {
					double value = textheight.getValue() / height;
					ggb.setYunit(value);
					textYUnit.setValue(value);
				} catch (NumberFormatException e1) {
				}
			} else if (cmd.equals(TEXT_XMIN)) {
				try {
					double xmax = ggb.getXmax();
					double m = textXmin.getValue();
					if (m > xmax) {
						ggb.setXmax(m);
						ggb.setXmin(xmax);
						width = m - xmax;
						int pos = textXmin.getCaretPosition();
						textXmin.setValue(xmax);
						textXmax.setValue(m);
						textXmax.setCaretPosition(pos);
						textXmax.requestFocus();
					} else {
						ggb.setXmin(m);
						width = xmax - m;
					}
					textwidth.setValue(width * ggb.getXunit());
					ggb.refreshSelectionRectangle();
				} catch (NumberFormatException e1) {
				}
			} else if (cmd.equals(TEXT_XMAX)) {
				try {
					double xmin = ggb.getxmin();
					double m = textXmax.getValue();
					if (m < xmin) {
						ggb.setxmin(m);
						ggb.setxmax(xmin);
						width = xmin - m;
						int pos = textXmax.getCaretPosition();
						textXmin.setValue(m);
						textXmax.setValue(xmin);
						textXmin.setCaretPosition(pos);
						textXmin.requestFocus();
					} else {
						ggb.setxmax(m);
						width = m - xmin;
					}
					textwidth.setValue(width * ggb.getXunit());
					ggb.refreshSelectionRectangle();
				} catch (NumberFormatException e1) {
				}
			} else if (cmd.equals(TEXT_YMIN)) {
				try {
					double ymax = ggb.getymax();
					double m = textYmin.getValue();
					if (m > ymax) {
						ggb.setymax(m);
						ggb.setymin(ymax);
						height = m - ymax;
						int pos = textYmin.getCaretPosition();
						textYmin.setValue(ymax);
						textYmax.setValue(m);
						textYmax.setCaretPosition(pos);
						textYmax.requestFocus();

					} else {
						ggb.setymin(m);
						height = ymax - m;
					}
					textheight.setValue(height * ggb.getYunit());
					ggb.refreshSelectionRectangle();
				} catch (NumberFormatException e1) {
				}
			} else if (cmd.equals(TEXT_YMAX)) {
				try {
					double ymin = ggb.getymin();
					double m = textYmax.getValue();
					if (m < ymin) {
						ggb.setymin(m);
						ggb.setymax(ymin);
						height = ymin - m;
						int pos = textYmax.getCaretPosition();
						textYmin.setValue(m);
						textYmax.setValue(ymin);
						textYmin.setCaretPosition(pos);
						textYmin.requestFocus();
					} else {
						ggb.setymax(m);
						height = m - ymin;
					}
					textheight.setValue(height * ggb.getYunit());
					ggb.refreshSelectionRectangle();
				} catch (NumberFormatException e1) {
				}

			}

		}
	}

	@Override
	public boolean getGnuplot() {
		return false;
	}
}