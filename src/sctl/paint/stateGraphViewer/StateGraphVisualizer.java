package sctl.paint.stateGraphViewer;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import sctl.paint.graph.StateEdge;
import sctl.paint.graph.StateGraph;
import sctl.paint.graph.StateNode;
import sctl.paint.listener.StateGenerationListener;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

public class StateGraphVisualizer implements StateGenerationListener {
	private JFrame mainFrame;
	protected GLJPanel showPanel;
	protected StateGraphVisualizeListener listener;
	protected StateGraph sg;
	
	public StateGraphVisualizer(String title) {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities glcaps = new GLCapabilities(glp);
		showPanel = new GLJPanel(glcaps);
		sg = new StateGraph(null);
		mainFrame = new JFrame(title);
		listener = new StateGraphVisualizeListener(this);
		showPanel.addGLEventListener(listener);
		showPanel.addMouseListener(listener);
		showPanel.addMouseMotionListener(listener);
		showPanel.addMouseWheelListener(listener);
		mainFrame.getContentPane().add(showPanel, BorderLayout.CENTER);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void show() {
		mainFrame.setSize(1000, 1000);
		mainFrame.setLocation(200, 200);
		mainFrame.setVisible(true);
	}

	@Override
	public void addState(String id, String state, boolean start) {
		if(sg.nodeExists(id)) {
			System.out.println("state already in");
			return;
		}
		StateNode sn = new StateNode(id, state);
		sg.addNode(sn);
		if(start) {
			sg.setStart(sn);
		}
	}

	@Override
	public void removeState(String id) {
		sg.deleteNodeById(id);
	}

	@Override
	public void addEdge(String from, String to) {
		if(sg.edgeExists(from, to)) {
			return;
		}
		StateNode snFrom = sg.getNodeById(from);
		StateNode snTo = sg.getNodeById(to);
		if(snFrom != null && snTo != null) {
			sg.addEdge(new StateEdge(snFrom, snTo));
		}
	}

	@Override
	public void removeEdge(String from, String to) {
		sg.deleteEdgeByIds(from, to);
	}

	@Override
	public void updateLayout() {
		sg.layout();
		new Thread(new UpdateLayoutThread()).start();
	}
	
	private class UpdateLayoutThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				sg.updateLayout(10);
			}
		}
		
	}
}
