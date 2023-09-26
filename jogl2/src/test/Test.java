package test;

/*

import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.Animator;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.Component3D;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLEventListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test implements GLEventListener {
	
	static {
		RendererJogl.initCaps();
	}



	private Component3D canvas;
	
	public Test(){
		
		canvas = new Component3D();

		canvas.addGLEventListener(this);


		Animator animator = new Animator( canvas, 60 );
		animator.start();



		JFrame frame = new JFrame("Test"); 
		frame.setSize(600, 600); 
		
		
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		jp.add(BorderLayout.CENTER, canvas);
		frame.add(jp);
		//frame.add(canvas); 
		
		
		frame.setVisible(true); 

		frame.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) { 
				System.exit(0); 
			} 
		}); 
		
	}

	public static void main(String[] args) { 

		new Test();

	}
	
	

	@Override
	public void display(GLAutoDrawable drawable) {
		update(); 
		render(drawable); 

		//canvas.repaint(); 		
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		
		System.out.println(RendererJogl.getGLInfos(drawable)); 
		
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	
    private double theta = 0; 
    private double s = 0; 
    private double c = 0; 
    
    private void update() { 
        theta += 0.01; 
        s = Math.sin(theta); 
        c = Math.cos(theta); 
    } 

    private void render(GLAutoDrawable drawable) { 
        GL2 gl = drawable.getGL().getGL2(); 

        gl.glClear(GL.GL_COLOR_BUFFER_BIT); 

        // draw a triangle filling the window 
        gl.glBegin(GL.GL_TRIANGLES); 
        gl.glColor3f(1, 0, 0); 
        gl.glVertex2d(-c, -c); 
        gl.glColor3f(0, 1, 0); 
        gl.glVertex2d(0, c); 
        gl.glColor3f(0, 0, 1); 
        gl.glVertex2d(s, -s); 
        gl.glEnd(); 
    } 
	
}*/
