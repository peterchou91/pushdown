package sctl.paint.treeViewer;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import sctl.paint.graph.RGBColor;
import sctl.paint.graph.TreeEdge;
import sctl.paint.graph.TreeNode;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import pushdown.main.PDModel;

public class SubTreeVisualizeListener
		implements GLEventListener, KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {
	public static final RGBColor red = new RGBColor(1,0,0);
	public static final RGBColor green = new RGBColor(0,1,0);
	public static final RGBColor blue = new RGBColor(0,0,1);
	public static final RGBColor highlight = new RGBColor(198.0f/255,145.0f/255,69.0f/255);
//	public static final RGBColor rootColor = new RGBColor(56.0f/255,94.0f/255,15.0f/255);
	public static final RGBColor rootColor = new RGBColor(0,0,0);
	public static final RGBColor oriColor = new RGBColor(0,0,0);
	public static final RGBColor hoverColor = new RGBColor(186.0f/255,52.0f/255,10.0f/255);
	public static final RGBColor stepColor = new RGBColor(148.0f/255,0,211.0f/255);
	public static final RGBColor fromColor = new RGBColor(12.0f/255,22.0f/255,146.0f/255);
	public static final RGBColor toColor = new RGBColor(0,1,0);
	
	private TreeVisualizer visual;

	private float[] lightPosition = new float[4];
	private float[] whiteLight = new float[4];
	protected GLU glu = new GLU();
	private GLUT glut = new GLUT();
	private double eyex = 0;
	private double eyey = 0;
	private double eyez = 20;
	
	private double phi = 0; // 0--360
	private double theta = 90; // 0--180
	private int dragStartX = 0;
	private int dragStartY = 0;
	
	private IntBuffer viewport = IntBuffer.allocate(4);
	private FloatBuffer modelview = FloatBuffer.allocate(4);
	private FloatBuffer projection = FloatBuffer.allocate(4);
	private FloatBuffer screenPoint = FloatBuffer.allocate(3);

	protected TreeNode nodeSelected = null;
	private Point mousePosition = null;
	protected boolean showingPop = false;
	private TextRenderer tr = new TextRenderer(new Font("SansSerif", Font.PLAIN, 30));
	// private RGBColor preColor = null;
	private LinkedList<AssistAffect> affect = new LinkedList<AssistAffect>();

	
	public SubTreeVisualizeListener(TreeVisualizer visual) {
		this.visual = visual;
	}

	public void setEye(double x, double y, double z) {
		eyex = x;
		eyey = y;
		eyez = z;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mousePosition = e.getPoint();
		if (e.isMetaDown()) {
			showingPop = true;
			if (nodeSelected == null) {
//				System.out.println("show back pop");
				visual.backPop.show(e.getComponent(), e.getX(), e.getY());
			} else {
				visual.nodePop.show(e.getComponent(), e.getX(), e.getY());
			}
			// visual.backPop.setVisible(true);
		} else {
			showingPop = false;
			if (e.getClickCount() == 2) {
				// hide or show subtree
			} else {
				// pick or unpick node
				if (nodeSelected != null) {
					if (nodeSelected.isPicked()) {
						this.addAffect(new UnPickNodeAffect(nodeSelected));
					} else {
						this.addAffect(new PickNodeAffect(nodeSelected,nodeSelected.getColor()));
					}
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.dragStartX = e.getX();
		this.dragStartY = e.getY();
	}



	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			// this.rotateYZ = ((this.rotateYZ+5) % 36)*Math.PI/18;
			eyey++;
			break;
		case KeyEvent.VK_DOWN:
			// this.rotateYZ = ((this.rotateYZ-5) % 36)*Math.PI/18;
			eyey--;
			break;
		case KeyEvent.VK_LEFT:
			// this.rotateXZ = ((this.rotateXZ-5) % 36)*Math.PI/18;
			eyex--;
			break;
		case KeyEvent.VK_RIGHT:
			// this.rotateXZ = ((this.rotateXZ+5) % 36)*Math.PI/18;
			eyex++;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	TreeNode subRoot;

	@Override
	public void display(GLAutoDrawable gld) {
		GL2 gl = (gld).getGL().getGL2();
		gl.glLoadIdentity();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		
		
		int i = 0;
		
		subRoot = visual.listener.subRoot == null? subRoot : visual.listener.subRoot;
		
//		System.out.println(visual.listener.subwindowNodes.size());
//		System.out.println(visual.listener.subwindowEdges.size());
		if(!visual.listener.subwindowNodes.isEmpty()) 
		for(TreeNode tn : visual.listener.subwindowNodes){
			if(tn == null) continue;
			if (tn.isVisible()) {
//				System.out.println("sub    " + tn);
				RGBColor color = tn.getColor();
				gl.glPushMatrix();
				if (tn.getIsCuted()){
					gl.glColor3f(0.5f,0.5f ,0.5f );
				}else{
					if(tn.highLight){
						gl.glColor3f( (float)(250/255.0),(float)(128/255.0),(float)(10/255.0));//orange
						if(tn.hitted){
							gl.glColor3f((float)(130.0/250.0),(float)(130.0/250.0),(float)(130.0/250.0));//red
						}
					}else{
						if(tn.hitted){
							gl.glColor3f((float)1.0,0,0);
						}else{
							gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
						}
					}
				}
				if(tn.referd > 0){
					gl.glColor3f( (float)(250/255.0),(float)(128/255.0),(float)(10/255.0));//orange
				}
				

				gl.glTranslated(tn.getX() - subRoot.getX(), 
						tn.getY() - subRoot.getY(),
						tn.getZ() - subRoot.getZ());
				
				//draw nodes
				if(tn.hitted){
					glut.glutSolidSphere(4 * tn.getSize(), 36, 36);
				}else{
					glut.glutSolidSphere(tn.getSize(), 36, 36);
				}
				
				i++;
				if(tn.isLableVisible()) {
					
					tr.begin3DRendering();
					tr.draw3D(tn.getLabel(), 0, 0, 0, 0.005f);
					tr.flush();
					tr.end3DRendering();
					

				}
				if(tn.isCutLableVisible()) {

					tr.begin3DRendering();
					tr.draw3D(tn.cutInfo, 0, 0, 0, 0.005f);
					tr.flush();
					tr.end3DRendering();

				}
				gl.glPopMatrix();
				
				
			}
		}
		
		if(!visual.listener.subwindowEdges.isEmpty())
		for (TreeEdge e : visual.listener.subwindowEdges) {
			TreeNode ton = e.getTo();
			TreeNode tn = e.getFrom();
			if (ton.isVisible()) {
				gl.glPushMatrix();
				gl.glLineWidth(e.getSize());
				gl.glColor3f(0,0,0);
				//draw lines
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3d(tn.getX() - subRoot.getX(), 
						tn.getY() - subRoot.getY(),
						tn.getZ() - subRoot.getZ());
				gl.glVertex3d(ton.getX() - subRoot.getX(), 
						ton.getY() - subRoot.getY(),
						ton.getZ() - subRoot.getZ());
				
				double dx = ton.getX() - tn.getX();
	            double dy = ton.getY() - tn.getY();
	            double dz = ton.getZ() - tn.getZ();
	            double d = Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2)+Math.pow(dz, 2));
	            double x = tn.getX()+dx*((d-0.1)/d);
	            double y = tn.getY()+dy*((d-0.1)/d);
	            double z = tn.getZ()+dz*((d-0.1)/d);
	            
	            double x1 = tn.getX()+dx*((d-0.2)/d);
	            double y1 = tn.getY()+dy*((d-0.2)/d);
	            double z1 = tn.getZ()+dz*((d-0.2)/d);
	            
	            //arrow
				gl.glVertex3d(x - subRoot.getX(), 
						y - subRoot.getY(), 
						z - subRoot.getZ());
				gl.glVertex3d(x1 + 0.04 - subRoot.getX(), 
						y1 - subRoot.getY(), 
						z1 - subRoot.getZ());
				
				gl.glVertex3d(x - subRoot.getX(), 
						y - subRoot.getY(), 
						z - subRoot.getZ());
				gl.glVertex3d(x1 - 0.04 - subRoot.getX(),
						y1 - subRoot.getY(), 
						z1 - subRoot.getZ());
				
				gl.glVertex3d(x - subRoot.getX(), 
						y - subRoot.getY(), 
						z - subRoot.getZ());
				gl.glVertex3d(x1 - subRoot.getX(),
						y1 - subRoot.getY(), 
						z1 - 0.04 - subRoot.getZ());
				
				gl.glEnd();
				gl.glPopMatrix();

			}
		}

		if(!showingPop) {
			selectNode(gl, mousePosition);
			mousePosition = null;
		}

		// show color, label or else
		if (!affect.isEmpty()) {
			affect.removeFirst().affect(gld);
		}

//		gl.glLoadIdentity();
		gl.glFlush();
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
		TreeNode n = visual.tree.getNearestNode(objxyz.get(0), objxyz.get(1), objxyz.get(2));
		if(n == null) {
			if(nodeSelected  != null) {
				nodeSelected.resetSize();
				nodeSelected = null;
			}
		} else {
			if(nodeSelected != null) {
				if(!n.getId().equals(nodeSelected.getId())) {
					nodeSelected.resetSize();
					nodeSelected = n;
					nodeSelected.setSize(0.15);
				}
			} else {
				nodeSelected = n;
				nodeSelected.setSize(0.15);
			}
		}
	}

	protected void addAffect(AssistAffect aa) {
		affect.addLast(aa);
	}

	public void setCamera(GLAutoDrawable gld) {
		GL2 gl = gld.getGL().getGL2();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		visual.subShowPanel.repaint();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable gld) {
		tr.setColor(0,0,0,1);
		lightPosition[0] = 10.0f;
		lightPosition[1] = 10.0f;
		lightPosition[2] = 10.0f;
		lightPosition[3] = 0.0f;
		whiteLight[0] = 0.8f;
		whiteLight[1] = 0.8f;
		whiteLight[2] = 0.8f;
		whiteLight[3] = 1.0f;
		GL2 gl = gld.getGL().getGL2();

		// GLUT glut = new GLUT();
		gld.setAutoSwapBufferMode(true);
		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
		gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
//		gl.glClearColor(238.0f / 255.0f, 226.0f / 255.0f, 185.0f / 255.0f, 0.0f);
		gl.glClearColor(1, 1, 1, 0);
//		gl.glClearColor(227.0f/255, 237.0f/255, 205.0f/255, 0);

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
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();

		gld.swapBuffers();

	}

	@Override
	public void reshape(GLAutoDrawable gld, int x, int y, int width, int height) {
		GL2 gl = gld.getGL().getGL2();
		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		if(width == 0 || height == 0) {
			glu.gluPerspective(60.0f, 1, 1.0f, 10000.0f);
		} else {
			glu.gluPerspective(60.0f, width / height, 1.0f, 10000.0f);
		}
		
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		glu.gluLookAt(eyex, eyey, eyez, 0, 0, 0, 0, 1, 0);

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		int step = e.getWheelRotation();
		double current = Math.sqrt(Math.pow(eyex, 2) + Math.pow(eyey, 2) + Math.pow(eyez, 2));
		if(current < 1.5 && step < 0) {
			return;
		}
		eyex += eyex / current * step;
		eyey += eyey / current * step;
		eyez += eyez / current * step;
//		System.out.println(eyex + "," + eyey + "," + eyez);
//		visual.subShowPanel.repaint();
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
//		visual.subShowPanel.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition = e.getPoint();
		// System.out.println("Mouse moving: " + mousePosition.getX() + "," +
		// mousePosition.getY());
//		visual.subShowPanel.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
