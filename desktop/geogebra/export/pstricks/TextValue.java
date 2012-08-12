package geogebra.export.pstricks;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * @author loic
 *
 */
public  class TextValue extends JTextField implements KeyListener {
	private static final long serialVersionUID = 1L;
	// do we allow negative values in the textfeld?
	private boolean ALLOW_NEGATIVE=false;
	JFrame jf;
	String actionCommand="";
	TextValue(JFrame jf, String s, boolean b,String actionCommand){
		super(s,15);
		this.jf=jf;
		this.ALLOW_NEGATIVE=b;
		addKeyListener(this);
		this.actionCommand=actionCommand;
	}
	public double getValue() throws NumberFormatException{
		return Double.parseDouble(getText());
	}
	public void setValue(double d){
		String s=String.valueOf(d);
		setText(s);
	}
	public void keyTyped(KeyEvent e){
		// Accept only numerical characters
		char c = e.getKeyChar();      
		if (!(Character.isDigit(c) ||
				(c == KeyEvent.VK_BACK_SPACE) ||
				(c == KeyEvent.VK_DELETE) ||
				(c=='.'))) {
			if (c!='-'||!ALLOW_NEGATIVE) e.consume();
			//  Only one - in first position
			else if (getText().indexOf('-')!=-1||getCaretPosition()!=0){
				e.consume();
			}
		}
		
		// if character is '.', check there's no other '.' in the number		
		else if (c=='.'&&getText().indexOf('.')!=-1){
		    e.consume();
		}
		
	}
	public void keyPressed(KeyEvent e){
		//
	}
	
	
	public void keyReleased(KeyEvent e){
		//
	}
	@Override
	public String toString(){
		return actionCommand; 
	}
}
