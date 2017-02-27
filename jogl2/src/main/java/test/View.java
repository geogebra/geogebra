package test;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.Animator;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.Component3D;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl;

public class View implements GLEventListener {

	protected RendererJogl jogl;

	private Animator animator;

	/** canvas usable for a JPanel */
	public Component3D canvas;

	private GLU glu; // for the GL Utility

	float r, g, b;

	int speed;

	long time;

	public View(float r, float g, float b, int speed) {

		this.r = r;
		this.g = g;
		this.b = b;

		this.speed = speed;

		// TODO Auto-generated method stub
		jogl = new RendererJogl();

		// canvas = view;
		System.out.println("create 3D component");
		RendererJogl.initCaps(false);
		canvas = RendererJogl.createComponent3D(false);

		System.out.println("add gl event listener");
		canvas.addGLEventListener(this);

		System.out.println("create animator");
		animator = RendererJogl.createAnimator(canvas, 60);
		// animator.setRunAsFastAsPossible(true);
		// animator.setRunAsFastAsPossible(false);

		System.out.println("start animator");
		animator.start();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // clear
																				// color
																// and depth
																// buffers
		gl.glLoadIdentity(); // reset the model-view matrix

		// ----- Your OpenGL rendering code here (Render a white triangle for
		// testing) -----
		gl.glTranslatef(0.0f, 0.0f, -6.0f); // translate into the screen
		gl.glColor3f(r, g, b);
		gl.glBegin(GL.GL_TRIANGLES); // draw using triangles

		long delay = System.currentTimeMillis() - time;
		double angle = speed * delay * Math.PI / 2000;
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);

		gl.glVertex3f(c, s, 0.0f);
		gl.glVertex3f(-s, c, 0.0f);
		gl.glVertex3f(-c, -s, 0.0f);

		gl.glEnd();
	}
	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL graphics context
		glu = new GLU(); // get GL Utilities
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
		gl.glClearDepth(1.0f); // set clear depth value to farthest
		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // best
																// perspective
																// correction
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH); // blends colors nicely, and
											// smoothes out
									// lighting

		time = System.currentTimeMillis();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height0) {
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context
		int height = height0;
		if (height == 0) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matches viewport
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION); // choose projection matrix
		gl.glLoadIdentity(); // reset projection matrix
		glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear,
														// zFar

		// Enable the model-view transform
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
	}

}
