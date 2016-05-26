package sctl.paint.treeViewer;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

import pushdown.entity.ProveNode;
import pushdown.main.PDModel;
import pushdown.util.Constants;
import sctl.paint.graph.RGBColor;
import sctl.paint.graph.Tree;
import sctl.paint.graph.TreeEdge;
import sctl.paint.graph.TreeNode;
import sctl.paint.listener.ProofEdgeInfo;
import sctl.paint.listener.ProofNodeInfo;
import sctl.paint.listener.TreeGenerationListener;

public class TreeVisualizer implements TreeGenerationListener {
	private JFrame mainFrame;
	protected GLJPanel showPanel;
	protected JPanel subShowPanel;
	protected JPopupMenu backPop;
	protected JPopupMenu nodePop;
	public TreeVisualizeListener listener;
	public Tree tree;
	protected TreeNode root;
	protected TreeControlPanel tcp;
	
	public Map<ProveNode,TreeNode> pn2TnMap = null;
	
	public ProveNode transferProveNode = null;
	public int level = -1;
	
	public HashMap<TreeNode, ArrayList<TreeEdge>> treeStructCopy = null;
	
	public static List<Thread> threads = new ArrayList<Thread>();
	
	public static boolean stop = false;
	
	
	public TreeVisualizer(String title) {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities glcaps = new GLCapabilities(glp);
		showPanel = new GLJPanel(glcaps);
		System.out.println("init showPanel");
		subShowPanel = new JPanel();
		iniBackPop();
		iniNodePop();
		tree = new Tree();
		
		mainFrame = new JFrame(title);	
		tcp = new TreeControlPanel();
		tcp.setMinimumSize(new Dimension(300, 600));
		showPanel.setMinimumSize(new Dimension(600,600));
		subShowPanel.setPreferredSize(new Dimension(300,300));


		mainFrame.getContentPane().setLayout(null);
		mainFrame.getContentPane().add(showPanel);
		mainFrame.getContentPane().add(subShowPanel);
		showPanel.setBounds(new Rectangle(0,0,1000,700));
		subShowPanel.setBounds(new Rectangle(0,700,1000,300));
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				listener = new TreeVisualizeListener(TreeVisualizer.this);
				showPanel.addGLEventListener(listener);
				showPanel.addMouseListener(listener);
				showPanel.addKeyListener(listener);
				showPanel.addMouseMotionListener(listener);
				showPanel.addMouseWheelListener(listener);
				

			}
			
		});
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	public void show(String way) {
		mainFrame.setSize(1000, 1000);
		mainFrame.setLocation(200, 200);
		if(way.equals("")){
			mainFrame.setLocation(0, 0);
		}else if(way.equals(Constants.pd)){
			mainFrame.setLocation( 1000,0);
		}
		mainFrame.setVisible(true);
	}
	public void show() {
		mainFrame.setSize(1000, 1000);
		mainFrame.setLocation(200, 200);
		mainFrame.setVisible(true);
	}
	
	private void iniBackPop() {
		backPop = new JPopupMenu();
		JMenuItem resetEye = new JMenuItem("Reset Eye Coords");
		resetEye.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.setEye(0, 0, 5);
//				listener.showingPop = false;
			}
		});
		JMenuItem clearColor = new JMenuItem("Clear Color");
		clearColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new ClearColorAffect(tree));
//				listener.showingPop = false;
			}
		});
//		JMenuItem resetTree = new JMenuItem("Reset Tree");
		JMenuItem resetRoot = new JMenuItem("Reset Root");
		resetRoot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new ResetRootAffect(tree, root));
			}
		});
		
		JMenuItem showAllLabel = new JMenuItem("Show All Labels");
		showAllLabel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new ShowAllLabelAffect(tree));
			}
			
		});
		JMenuItem hideAllLabel = new JMenuItem("Hide All Labels");
		hideAllLabel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new HideAllLabelAffect(tree));
			}
			
		});
		
		backPop.add(clearColor);
		backPop.add(resetEye);
		backPop.add(resetRoot);
		backPop.add(showAllLabel);
		backPop.add(hideAllLabel);
//		backPop.add(resetTree);
	}
	private void iniNodePop() {
		nodePop = new JPopupMenu();
		
		JMenuItem showCutLabel = new JMenuItem("Show Cut");
		showCutLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new ShowCutHideLabelAffect(listener.nodeSelected));
//				listener.showingPop = false;
			}
		});
		
		JMenuItem showLabel = new JMenuItem("Show Label");
		showLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new ShowHideLabelAffect(listener.nodeSelected));
//				listener.showingPop = false;
			}
		});
		JMenuItem setRoot = new JMenuItem("Set as Root");
		setRoot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new SetAsRootAffect(tree, listener.nodeSelected));
//				listener.showingPop = false;
			}
		});
		JMenuItem hideShowSubTree = new JMenuItem("Hide/Show Subtree");
		hideShowSubTree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.addAffect(new HideExpandSubTreeAffect(listener.nodeSelected));
//				listener.showingPop = false;
			}
		});
		
		nodePop.add(showCutLabel);
		nodePop.add(showLabel);
		nodePop.add(setRoot);
		nodePop.add(hideShowSubTree);
	}

	@Override
	public void addNode(ProofNodeInfo pni) {
		TreeNode n = new TreeNode(pni.getId(), pni.getFml().toString());
		tree.addNode(n);
		if(pni.isRoot()) {
			root = n;
			tree.setRoot(n);
//			System.out.println("Root added.");
		} else {
//			System.out.println("Node added.");
		}
	}
	
	public void addNodeForPD(ProveNode pn,boolean isRoot,boolean isCuted,int level) {
		TreeNode n = new TreeNode(pn.getId() + "", "     " + pn.getLabel());
		n.cutInfo = pn.getFromInfo();
		n.setIsCuted(isCuted);
		n.setLevel(level);
		tree.addNode(n);
		if(isRoot) {
			root = n;
			tree.setRoot(n);
//			System.out.println("Root added.");
		} else {
//			System.out.println("Node added.");
		}
		
		if(pn2TnMap == null) pn2TnMap = new HashMap<ProveNode,TreeNode>();
		pn2TnMap.put(pn, n);
	}
	


	@Override
	public void removeNode(ProofNodeInfo pni) {
		tree.deleteNode(pni.getId());
	}

	@Override
	public void addEdge(ProofEdgeInfo pei) {
		tree.addEdge(pei.getFrom(), pei.getTo());
//		System.out.println("TreeEdge adding: "+pei.getFrom()+"--"+pei.getTo());
	}
	public void addEdge(String fromId,String toId) {
		tree.addEdge(fromId, toId);
		
	}
	@Override
	public void removeEdge(ProofEdgeInfo pei) {
		tree.deleteEdge(pei.getFrom(), pei.getTo());
	}
	
	/*
	 * (non-Javadoc)∆Ù∂Øupdatelayoutœﬂ≥Ã
	 * @see sctl.paint.listener.TreeGenerationListener#updateLayout(java.lang.String)
	 */
	@Override
	public void updateLayout(String way) {
		if(tree != null){
//			tree.layout();
//			System.out.println("start thread: " + way);
			Thread thread = new Thread(new UpdateLayoutThread(way));
			threads.add(thread);
			thread.start();
		}
	}
	
//	public void layoutAgain() {
//		new Thread(new UpdateLayoutThread()).start();
//	}
	
	private class UpdateLayoutThread implements Runnable {
		private String way;
		List<TreeNode> redNodes = new ArrayList<TreeNode>();
		List<TreeNode> blackNodes = new ArrayList<TreeNode>();
		public UpdateLayoutThread(){
			
		}
		public UpdateLayoutThread(String way){
			this.way = way;
		}

		@Override
		public void run() {
			long currentTime = System.currentTimeMillis();
			while(true) {
				redNodes.clear();blackNodes.clear();
				if(transferProveNode != null){
//					tree.setDepthColor();
					
					LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
					looked.addLast(tree.getRoot());
					while(!looked.isEmpty()) {
						TreeNode n = looked.removeFirst();
						n.setPicked(false);
						if(n.getLabel().trim().equals("s(a)")){
							redNodes.add(n);
						}
						if(n.getLabel().trim().equals(PDModel.targetConfiguration)){
							blackNodes.add(n);
						}
						n.clearColor();
						for(TreeNode tn : tree.getChildrenNodes(n)) {
							looked.addLast(tn);
						}
					}
					
					tree.setDepthColor(redNodes, new RGBColor(163,3,36), new RGBColor(253,132,149));
					tree.setDepthColor(blackNodes, new RGBColor(0,0,0),new RGBColor(182,178,192));
//					tree.setDepthColor();
					System.out.println(redNodes.size());
					System.out.println(blackNodes.size());
					
					copyTreeStruct(tree.treeStruct);
					transferProveNode = null;
				}
				if("".equals(way)){
					tree.updateLayout(1);
				}else if(treeStructCopy != null){
					tree.updateLayout(1,way,treeStructCopy);
				}
//				if(System.currentTimeMillis() - currentTime > 1000){
//					stop = true;
//					break;
//				}
			}
		}
		private void copyTreeStruct(HashMap<TreeNode, ArrayList<TreeEdge>> treeStruct2) {
			if(treeStructCopy == null){
				treeStructCopy = new HashMap<TreeNode, ArrayList<TreeEdge>>();
			}
			treeStructCopy.clear();
			for(Entry<TreeNode, ArrayList<TreeEdge>> entry : treeStruct2.entrySet()){
				
				ArrayList<TreeEdge> newEdge = new ArrayList<TreeEdge>();
				for(TreeEdge e : entry.getValue()){
					newEdge.add(e);
				}
				treeStructCopy.put(entry.getKey(), newEdge);
				
//				treeStruct.put(entry.getKey(), entry.getValue());
			}
			
		}
		
		
	}
	
	public static void main(String[] args) {
//		TreeVisualizer tv = new TreeVisualizer("test");
//		tv.show();
		

	}

	@Override
	public void updateLayout() {
		updateLayout("");
		
	}
}
