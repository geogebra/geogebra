package geogebra.kernel.discrete.signalprocesser.shared;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class JCollapsiblePanel extends JPanel implements MouseListener {
    
    private static final int DIST_FROM_LEFT      = 30;
    private static final int DIST_FROM_TOP       =  2;
    private static final int RECT_WIDTH          = 14;
    private static final int RECT_HEIGHT         = 10;
    private static final int TRIANGLE_MARGINLEFT =  3;
    private static final int TRIANGLE_MARGINTOP  =  2;
    
    private boolean iscollapsed = false;
    private JPanel innercomponent = null;
    
    public JCollapsiblePanel() {
        // Add mouse listener
        super.addMouseListener(this);
        
        // Setup layout
        if ( innercomponent==null ) {
            innercomponent = new JPanel();
        }
        super.setLayout(new BorderLayout());
        super.add( innercomponent , BorderLayout.CENTER );
    }

    public JCollapsiblePanel(LayoutManager layout) {
        // Add mouse listener
        super.addMouseListener(this);
        
        // Setup layout
        if ( innercomponent==null ) {
            innercomponent = new JPanel(layout);
        } else {
            innercomponent.setLayout(layout);
        }
        super.setLayout(new BorderLayout());
        super.add( innercomponent , BorderLayout.CENTER );
    }

    public void setCollapsed(boolean b){
    	iscollapsed=b;
        super.removeAll();
        this.revalidate();
    }    

    public void paint(Graphics g) {
        // Paint as normal
        super.paint(g);
        
        // Get width
        int width = getWidth();
        
        // Paint up and down boxes
        g.setColor(super.getBackground());
        g.fill3DRect(width-DIST_FROM_LEFT, DIST_FROM_TOP, RECT_WIDTH, RECT_HEIGHT, true);
        
        // Draw triangle
        int xcoord[] = new int[3];
        int ycoord[] = new int[3];
        xcoord[0] = width - DIST_FROM_LEFT + TRIANGLE_MARGINLEFT - 1;
        xcoord[1] = width - DIST_FROM_LEFT + RECT_WIDTH - TRIANGLE_MARGINLEFT - 1;
        xcoord[2] = width - DIST_FROM_LEFT + ( RECT_WIDTH / 2 ) - 1;
        if ( iscollapsed==false ) {
            ycoord[0] = DIST_FROM_TOP + TRIANGLE_MARGINTOP;
            ycoord[1] = ycoord[0];
            ycoord[2] = DIST_FROM_TOP + RECT_HEIGHT - TRIANGLE_MARGINTOP;
        } else {
            ycoord[0] = DIST_FROM_TOP + RECT_HEIGHT - TRIANGLE_MARGINTOP - 1;
            ycoord[1] = ycoord[0];
            ycoord[2] = DIST_FROM_TOP + TRIANGLE_MARGINTOP - 1;
        }
        g.setColor(Color.blue);
        g.fillPolygon( xcoord , ycoord , 3 );
    }
    
    public void mouseClicked(MouseEvent e) {
        // Statements ordered as they are for performance reasons
        if ( (getWidth()-DIST_FROM_LEFT)<=e.getX()                               && e.getY()     <=(DIST_FROM_TOP+RECT_HEIGHT) &&
                e.getX()                <=(getWidth()-DIST_FROM_LEFT+RECT_WIDTH) && DIST_FROM_TOP<=e.getY() ) {
            if ( iscollapsed ) {
                iscollapsed = false;
                super.add( innercomponent , BorderLayout.CENTER );
            } else {
                iscollapsed = true;
                super.removeAll();
            }
            this.revalidate();
            //this.repaint();
        }
    }
    
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    
    public void setLayout(LayoutManager layout) {
        // Create inner component if none
        if ( innercomponent==null ) {
            innercomponent = new JPanel();
        }
        
        // Update layout
        //if ( layout instanceof BoxLayout ) {
        //    Reflect.setfield(layout, "target", innercomponent);
        //}
        
        // Set layout
        innercomponent.setLayout(layout);
    }
    
    public Component add(Component comp) {
        return innercomponent.add(comp);
    }
    public Component add(Component comp, int index) {
        return innercomponent.add(comp, index);
    }
    public void add(Component comp, Object constraints) {
        innercomponent.add(comp, constraints);
    }
    public void add(Component comp, Object constraints, int index) {
        innercomponent.add(comp, constraints, index);
    }
    public Component add(String name, Component comp) {
        return innercomponent.add(name, comp);
    }
}
