package geogebra.export.pstricks;
import geogebra.main.AppD;

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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

abstract public class ExportFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	private final String TEXT_XUNIT="textxunit";
	private final String TEXT_YUNIT="textyunit";
	private final String TEXT_WIDTH="textwidth";
	private final String TEXT_HEIGHT="textheight";
	private final String TEXT_XMAX="textxmax";
	private final String TEXT_XMIN="textxmin";
	private final String TEXT_YMAX="textymax";
	private final String TEXT_YMIN="textymin";
	protected TextValue textXUnit,textYUnit,textwidth,textheight;
	protected JLabel labelwidth,labelheight,labelXUnit,labelYUnit,labelFontSize,labelFormat; 
	protected TextValue textXmin,textXmax, textYmin,textYmax;
	protected JLabel labelXmin,labelXmax,labelYmin,labelYmax;
	final String[] msg={"10 pt","11 pt","12 pt"};
	protected JComboBox comboFontSize,comboFormat
	 // Andy Zhu TODO
	 ,comboFill;
	protected JLabel labelFill;
	static final int FILL_NONE = 0, FILL_OPAQUE = 1, FILL_OPACITY_PEN = 2, FILL_LAYER = 3;
	 // end changes
	protected JPanel panel;
	protected JButton button,button_copy;
	protected JCheckBox jcbPointSymbol, jcbGrayscale,  
	 // Andy Zhu - for use in Asymptote Frame
	                    jcbShowAxes, jcbAsyCompact, jcbAsyCse5, jcbDotColors, jcbPairName;
	 // end changes
	protected JScrollPane js;
	protected JTextArea textarea;
	protected AppD app;
	protected double width,height;
	protected JButton buttonSave;
//	private ExportFrame ef;
	protected File currentFile=null;
	private GeoGebraExport ggb;
	ListenKey listenKey;
	//definition of the behaviour of the textValues corresponding
	//to xmin, xmax, ymin and ymax.
	//Explaination for xs:
	//if xmin is changed, then both xmin and xmax are changed
	//to be sure that everything is allright even though xmin is set
	//to a higher value than xmax
	//then the width is changed.
	public ExportFrame(final GeoGebraExport ggb,String action){
		this.ggb=ggb;
		this.app= ggb.getApp();
		width=ggb.getXmax()-ggb.getXmin();
		height=ggb.getYmax()-ggb.getYmin();
		listenKey=new ListenKey(this);
		textXUnit=new TextValue(this,String.valueOf(ggb.getXunit()),false,this.TEXT_XUNIT);
		textYUnit=new TextValue(this,String.valueOf(ggb.getYunit()),false,this.TEXT_YUNIT);
		textwidth=new TextValue(this,String.valueOf(width),false,this.TEXT_WIDTH);
		textheight=new TextValue(this,String.valueOf(height),false,this.TEXT_HEIGHT);
		textXmin=new TextValue(this,String.valueOf(ggb.getXmin()),true,this.TEXT_XMIN);
		textXmax=new TextValue(this,String.valueOf(ggb.getxmax()),true,TEXT_XMAX);
		textYmin=new TextValue(this,String.valueOf(ggb.getymin()),true,TEXT_YMIN);
		textYmax=new TextValue(this,String.valueOf(ggb.getymax()),true,TEXT_YMAX);
		textXUnit.addKeyListener(listenKey);
		textYUnit.addKeyListener(listenKey);
		textXmin.addKeyListener(listenKey);
		textXmax.addKeyListener(listenKey);
		textwidth.addKeyListener(listenKey);
		textheight.addKeyListener(listenKey);
		textYmin.addKeyListener(listenKey);
		textYmax.addKeyListener(listenKey);
		
		panel=new JPanel();
		button=new JButton(app.getPlain(action));
		button_copy=new JButton(app.getPlain("CopyToClipboard"));
		labelXUnit=new JLabel(app.getPlain("XUnits"));
		labelYUnit=new JLabel(app.getPlain("YUnits"));
		labelwidth=new JLabel(app.getPlain("PictureWidth"));
		labelheight=new JLabel(app.getPlain("PictureHeight"));
		labelFontSize=new JLabel(app.getPlain("LatexFontSize"));
		labelXmin=new JLabel(app.getPlain("xmin"));
 		labelXmax=new JLabel(app.getPlain("xmax"));
 		labelYmin=new JLabel(app.getPlain("ymin"));
 		labelYmax=new JLabel(app.getPlain("ymax"));
		jcbPointSymbol=new JCheckBox(app.getPlain("DisplayPointSymbol"));
		jcbGrayscale=new JCheckBox(app.getPlain("PGFExport.Grayscale"));
		 // Andy Zhu: for use in Asymptote frame
		jcbShowAxes   = new JCheckBox(app.getMenu("ShowAxesGrid"));
		jcbAsyCompact = new JCheckBox(app.getMenu("ConciseCode"));
		jcbAsyCse5    = new JCheckBox(app.getMenu("ConciseUsingCSE5"));
		jcbDotColors  = new JCheckBox(app.getMenu("KeepDotColors"));
		jcbPairName   = new JCheckBox(app.getMenu("UsePairNames"));
		jcbShowAxes.setSelected(true);
		jcbAsyCompact.setSelected(false);
		jcbAsyCse5.setSelected(false);
		jcbAsyCse5.setEnabled(false);
		jcbDotColors.setSelected(false);
		jcbAsyCompact.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(jcbAsyCompact.isSelected()) {
					jcbAsyCse5.setEnabled(true);
				    jcbPairName.setSelected(true);
				}
				else {
					jcbAsyCse5.setSelected(false);
					jcbAsyCse5.setEnabled(false);
				}
			}
		});
		final String[] comboFillText={app.getMenu("None"),app.getMenu("OnlyOpaqueFills"),app.getMenu("WithOpacityPen"), app.getMenu("ByLayering")};

		comboFill = new JComboBox(comboFillText);
		labelFill = new JLabel(app.getMenu("FillType")+":");
		 // end changes
		comboFontSize=new JComboBox(msg);
		jcbPointSymbol.setSelected(true);
		jcbGrayscale.setSelected(false);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ggb.setBeamer(isBeamer());
				ggb.generateAllCode();
			}
		});
		button_copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					textarea.copy();
				}
		});
		js=new JScrollPane();
		textarea=new JTextArea();
		buttonSave=new JButton(app.getMenu("SaveAs"));
		buttonSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
        		currentFile =
		            app.getGuiManagerD().showSaveDialog(
		                AppD.FILE_EXT_TEX, currentFile,
		                "TeX " + app.getMenu("Files"), true, false);
		        if (currentFile == null)
		            return;
				try{
					
					FileOutputStream f = new FileOutputStream(currentFile);
					BufferedOutputStream b = new BufferedOutputStream(f);
/*		        		java.util.Enumeration en=System.getProperties().keys();
					while(en.hasMoreElements()){
						String s=en.nextElement().toString();
						System.out.println(s+" "+System.getProperty(s));
					}*/
					OutputStreamWriter osw = new  OutputStreamWriter(b, "UTF-8" );
					StringBuilder sb=new StringBuilder(textarea.getText());
					if (isLaTeX()){
						int id=sb.indexOf("\\usepackage{");
						if (id!=-1){
							sb.insert(id,"\\usepackage[utf8]{inputenc}\n");
						}
					}
					else if (isConTeXt()){
						int id=sb.indexOf("\\usemodule[");
						if (id!=-1){
							sb.insert(id,"\\enableregime[utf]\n");
						}
					}
					osw.write(sb.toString());
					osw.close();
					b.close();
					f.close();
				}
				catch(FileNotFoundException e1){}
				catch(UnsupportedEncodingException e2){}
				catch(IOException e3){}
			}
		});
	}
	protected void centerOnScreen() {
		//	center on screen
		pack();				
		setLocationRelativeTo(app.getMainComponent());
	}
	public boolean isGrayscale(){
		return jcbGrayscale.isSelected();
	}
	public boolean getExportPointSymbol(){
		return jcbPointSymbol.isSelected();
	}
	public double getXUnit(){
		double d;
		try{
			d=textXUnit.getValue();	
		}
		catch(NumberFormatException e){d=1;}
		return d;
	}
	public double getYUnit()throws NumberFormatException{
		double d;
		try{
			d=textYUnit.getValue();	
		}
		catch(NumberFormatException e){d=1;}
		return d;
	}
	public double getLatexHeight(){
		return textheight.getValue();
	}
	public double getLatexWidth(){
		return textwidth.getValue();
	}
	public void write(StringBuilder sb){
		textarea.setText(new String(sb));
		textarea.selectAll();
	}
	public int getFontSize(){
		switch(comboFontSize.getSelectedIndex()){
			case 0:
				return 10;
			case 1:
				return 11;
			case 2:
				return 12;
		}
		return 10;
	}
	protected int getFormat(){
		return comboFormat.getSelectedIndex();
	}
	 // Andy Zhu - for use in Asymptote frame
	protected boolean getShowAxes(){
	    return jcbShowAxes.isSelected();
	}
	protected boolean getAsyCompact(){
	    return jcbAsyCompact.isSelected();
	}
	protected boolean getAsyCompactCse5(){
	    return jcbAsyCse5.isSelected();
	}
	protected boolean getKeepDotColors(){
	    return jcbDotColors.isSelected();
	}
	protected boolean getUsePairNames(){
        return jcbPairName.isSelected();
    }
	protected int getFillType(){
	    return comboFill.getSelectedIndex();
	}
	 // end changes

	protected abstract boolean isLaTeX();
	protected abstract boolean isConTeXt();
	protected abstract boolean isPlainTeX();
	
	protected abstract boolean isBeamer();
/*class EncodingDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JComboBox menu;
	private HashMap encode;
	private JLabel labelInputenc;
	private JLabel labelBabel;
	private JButton button;
	private JTextArea zone;
	String encoding="";
	EncodingDialog(ExportFrame ef){
		super(ef,true);
		setTitle(app.getPlain("PGFExport.Encoding"));
		encode=new HashMap();
		encode.put("ansinew","windows-1252");
		encode.put("ascii","US-ASCII");
		encode.put("cp1250","windows-1250");
		encode.put("cp1252","windows-1252");		
		encode.put("cp1257","windows-1257");
		encode.put("cp437","Cp437");
		encode.put("cp850","Cp850");
		encode.put("cp852","Cp852");
		encode.put("cp858","Cp858");
		encode.put("cp865","Cp865");
		encode.put("latin1","ISO-8859-1");
		encode.put("latin2","ISO-8859-2");
		encode.put("latin3","ISO-8859-3");
		encode.put("latin4","ISO-8859-4");
		encode.put("latin5","ISO-8859-9");
		encode.put("latin9","ISO-8859-15");
		encode.put("latin10","ISO-8859-10");
		encode.put("utf8","UTF-8" );
		encode.put("macce","MacCentralEurope");
		encode.put("applemac","");
		encode.put("koi8-r","KOI8-R");
		menu=new JComboBox();
		
		button=new JButton("\u21B5");
		button.addActionListener(this);
		button.setActionCommand("button");
		zone=new JTextArea();

		java.util.Iterator it=encode.keySet().iterator();
		while(it.hasNext()){
			String key=it.next().toString();
			menu.addItem(key);
		}
		menu.addActionListener(this);
		menu.setActionCommand("combo");
		setLayout(new BorderLayout());
		add(menu,BorderLayout.NORTH);
		add(button,BorderLayout.EAST);
		add(zone,BorderLayout.CENTER);
		setSize(200,300);
		setVisible(true);
		}
	public void actionPerformed(ActionEvent e){
		String cmd=e.getActionCommand();
		if (cmd.equals("button")){
			encoding=encode.get(menu.getSelectedItem().toString()).toString();
			dispose();
		}
		else if (cmd.equals("combo")){
			if (isLaTeX()){
				StringBuilder sb=new StringBuilder();
				sb.append("\\usepackage[");
				sb.append(encode.get(menu.getSelectedItem().toString()));
				sb.append("]{inputenc}\n");
				zone.setText(sb.toString());
			}
			else if (isConTeXt()){
				
			}
		}
	}
	String getEncoding(){
		return encoding;
	}
	}*/
	class ListenKey extends KeyAdapter{
		ExportFrame ef;
		ListenKey(ExportFrame ef){
			this.ef=ef;
		}
		@Override
		public void keyReleased(KeyEvent e){
			String cmd=e.getSource().toString();
			if (cmd.equals(TEXT_XUNIT)){
				try{
					double value = textXUnit.getValue();
					ggb.setXunit(value);
					textwidth.setValue(value*width);
				}
				catch(NumberFormatException e1){
				}
			}
			else if (cmd.equals(TEXT_YUNIT)){
				try{
					double value=textYUnit.getValue();
					ggb.setYunit(value);
					textheight.setValue(value*height);
				}
				catch(NumberFormatException e1){
				}
			}	
			else if (cmd.equals(TEXT_WIDTH)){
				try{
					double value = textwidth.getValue()/width;
					ggb.setXunit(value);
					textXUnit.setValue(value);
				}
				catch(NumberFormatException e1){}
			}
			else if (cmd.equals(TEXT_HEIGHT)){
				try{
					double value = textheight.getValue()/height;
					ggb.setYunit(value);
					textYUnit.setValue(value);
				}
				catch(NumberFormatException e1){}
			}
			else if (cmd.equals(TEXT_XMIN)){
				try{
					double xmax = ggb.getXmax();
					double m=textXmin.getValue();
					if(m>xmax){
						ggb.setXmax(m);
						ggb.setXmin(xmax);
						width=m-xmax;
						int pos=textXmin.getCaretPosition();
						textXmin.setValue(xmax);
						textXmax.setValue(m);
						textXmax.setCaretPosition(pos);
						textXmax.requestFocus();
					}
					else{
						ggb.setXmin(m);
						width=xmax-m;
					}
					textwidth.setValue(width*ggb.getXunit());
					ggb.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
			else if (cmd.equals(TEXT_XMAX)){
				try{
					double xmin = ggb.getxmin();
					double m=textXmax.getValue();
					if(m<xmin){
						ggb.setxmin(m);
						ggb.setxmax(xmin);
						width=xmin-m;
						int pos=textXmax.getCaretPosition();
						textXmin.setValue(m);
						textXmax.setValue(xmin);
						textXmin.setCaretPosition(pos);
						textXmin.requestFocus();
					}
					else{
						ggb.setxmax(m);
						width=m-xmin;
					}
					textwidth.setValue(width*ggb.xunit);
					ggb.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
			else if (cmd.equals(TEXT_YMIN)){
				try{
					double ymax = ggb.getymax();
					double m=textYmin.getValue();
					if(m>ymax){
						ggb.setymax(m);
						ggb.setymin(ymax);
						height=m-ymax;
						int pos=textYmin.getCaretPosition();
						textYmin.setValue(ymax);
						textYmax.setValue(m);
						textYmax.setCaretPosition(pos);
						textYmax.requestFocus();

					}
					else{
						ggb.setymin(m);
						height=ymax-m;
					}
					textheight.setValue(height*ggb.yunit);
					ggb.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
			else if(cmd.equals(TEXT_YMAX)){
				try{
					double ymin = ggb.getymin();
					double m=textYmax.getValue();
					if(m<ymin){
						ggb.setymin(m);
						ggb.setymax(ymin);
						height=ymin-m;
						int pos=textYmax.getCaretPosition();
						textYmin.setValue(m);
						textYmax.setValue(ymin);
						textYmin.setCaretPosition(pos);
						textYmin.requestFocus();
					}
					else{
						ggb.setymax(m);
						height=m-ymin;
					}
					textheight.setValue(height*ggb.yunit);
					ggb.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}

			}
			
		}
	}
} 