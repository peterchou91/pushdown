package sctl.paint.stateGraphViewer;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import sctl.paint.graph.RGBColor;
import sctl.paint.graph.StateEdge;
import sctl.paint.graph.StateNode;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class StateGraphVisualizeListener implements GLEventListener,MouseListener, MouseWheelListener, MouseMotionListener {
	private StateGraphVisualizer visual;
	protected GLU glu = new GLU();
	private GLUT glut = new GLUT();
	private double eyex = 0;
	private double eyey = 0;
	private double eyez = 5;
	
	private double phi = 0; // 0--360
	private double theta = 90; // 0--180
	private int dragStartX = 0;
	private int dragStartY = 0;
	
	private StateNode nodeSelected = null;
	private Point mousePosition = null;
	
	public StateGraphVisualizeListener(StateGraphVisualizer visual) {
		this.visual = visual;
	}
	
	@Override
	public void display(GLAutoDrawable gld) {
		GL2 gl = (gld).getGL().getGL2();
		gl.glLoadIdentity();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		
		/**
		 * painting code
		 */
		for(StateNode sn : visual.sg.getNodes()) {
			gl.glPushMatrix();
			gl.glTranslated(sn.getX(), sn.getY(), sn.getZ());
			RGBColor color = sn.getColor();
			gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
			glut.glutSolidSphere(sn.getSize(), 16, 16);
			gl.glPopMatrix();
			for(StateEdge se : visual.sg.getPostEdges(sn)) {
				StateNode psn = se.getTo();
				gl.glPushMatrix();
				gl.glColor3f(1.0f, 1.0f, 1.0f);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3d(sn.getX(), sn.getY(), sn.getZ());
				gl.glVertex3d(psn.getX(), psn.getY(), psn.getZ());
				gl.glEnd();
				gl.glPopMatrix();
			}
		}
		gl.glFlush();
		selectNode(gl, mousePosition);
		mousePosition = null;
		setCamera(gld);
	}
	
	private void selectNode(GL2 gl, Point p) {
		if (p == null) {
			return;
		}
		FloatBuffer projection = FloatBuffer.allocate(16);
		FloatBuffer modelview = FloatBuffer.allocate(16);
		IntBuffer viewport = IntBuffer.allocate(4);
		FloatBuffer bz = FloatBuffer.allocate(1);
		FloatBuffer objxyz = FloatBuffer.allocate(3);
		gl.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, modelview);
		gl.glGetFloatv(GLMatrixFunc.GL_PROJECTION_MATRIX, projection);
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
		// float x = visibleEvent.getX();
		int x = (int) p.getX();
		int y = (int) (viewport.get(3) - p.getY());
		gl.glReadPixels(x, y, 1, 1, GL2ES2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, bz);
		float z = bz.get(0);
		glu.gluUnProject(x, y, z, modelview, projection, viewport, objxyz);
		// System.out.println("x, y, z:"+objxyz.get(0)+", "+objxyz.get(1)+",
		// "+objxyz.get(2));
		StateNode n = visual.sg.getNearestNode(objxyz.get(0), objxyz.get(1), objxyz.get(2));
		if(n == null) {
			if(nodeSelected  != null) {
//				nodeSelected.resetSize();
				nodeSelected.clearColor();
				nodeSelected = null;
			}
		} else {
			if(nodeSelected != null) {
				if(!n.getId().equals(nodeSelected.getId())) {
//					nodeSelected.resetSize();
					nodeSelected.clearColor();
					nodeSelected = n;
//					nodeSelected.setSize(0.15);
					nodeSelected.setColor(new RGBColor(0,1,0));
				}
			} else {
				nodeSelected = n;
//				nodeSelected.setSize(0.15);
				nodeSelected.setColor(new RGBColor(0,1,0));
			}
		}
	}

	
	public void setCamera(GLAutoDrawable gld) {
		GL2 gl = gld.getGL().getGL2();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		visual.showPanel.repaint();
	}

	@Override
	public void dispose(GLAutoDrawable gld) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable gld) {
		GL2 gl = gld.getGL().getGL2();

		// GLUT glut = new GLUT();
		gld.setAutoSwapBufferMode(true);
		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
		gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
//		gl.glClearColor(238.0f / 255.0f, 226.0f / 255.0f, 185.0f / 255.0f, 0.0f);
		gl.glClearColor(0, 0, 0, 0);
		gl.glClearDepth(1.0);
//		gl.glShadeModel(GL2.GL_SMOOTH);
//		gl.glEnable(GL2.GL_LIGHTING);
//		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL2GL3.GL_POLYGON_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glHint(GL2GL3.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_DEPTH_TEST);

		gl.glViewport(0, 0, gld.getSurfaceWidth(), gld.getSurfaceHeight());
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		int width = gld.getSurfaceWidth();
		int height = gld.getSurfaceHeight();
		if(width == 0 || height == 0) {
			glu.gluPerspective(60.0f, 1, 1.0f, 10000.0f);
		} else {
			glu.gluPerspective(60.0f, width / height, 1.0f, 10000.0f);
		}
//		glu.gluPerspective(60.0f, gld.getSurfaceWidth() / gld.getSurfaceHeight(), 1.0f, 10000.0f);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		// gl.glLightfv(arg0, arg1, arg2, arg3);
//		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
//		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, whiteLight, 0);
//		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, whiteLight, 0);
		// glu.gluOrtho2D(0.0, 500.0, 0.0, 300.0);
		gld.swapBuffers();
	}

	@Override
	public void reshape(GLAutoDrawable gld, int x, int y, int width, int height) {
		GL2 gl = gld.getGL().getGL2();
		
		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60.0f, width / height, 1.0f, 10000.0f);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int dragedX = e.getX() - dragStartX;
		int dragedY = e.getY() - dragStartY;
//		System.out.println("dragedX: "+dragedX+", dragedY: "+dragedY);
		this.phi = ((dragedX/10)+phi) % 360;
		this.theta = ((dragedY/10)+theta) % 360;
		double r = Math.sqrt(Math.pow(eyex,2)+Math.pow(eyey, 2)+Math.pow(eyez,2));
		eyez = r*Math.sin(theta*Math.PI/180)*Math.cos(phi*Math.PI/180);
		eyex = r*Math.sin(theta*Math.PI/180)*Math.sin(phi*Math.PI/180);
		eyey = r*Math.cos(theta*Math.PI/180);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int step = e.getWheelRotation();
		double current = Math.sqrt(Math.pow(eyex, 2) + Math.pow(eyey, 2) + Math.pow(eyez, 2));
		eyex += eyex / current * step;
		eyey += eyey / current * step;
		eyez += eyez / current * step;
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.mousePosition = e.getPoint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.dragStartX = e.getX();
		this.dragStartY = e.getY();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
