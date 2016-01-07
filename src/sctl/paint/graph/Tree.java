package sctl.paint.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import sctl.paint.treeViewer.TreeVisualizeListener;

public class Tree{
	private TreeNode root;
//	private HashSet<TreeNode> nodes;
//	private ArrayList<TreeEdge> edges;
	public HashMap<TreeNode, ArrayList<TreeEdge>> treeStruct;
	
	private float ka = 3f;
	private float kr = 1f;
	private float kg = 1f;
	private float ko = 1;
	private int height = 0;
	
	private static Random random = new Random();
	
	public Tree(TreeNode root) {
		this();
		this.root = root;
//		nodes = new HashSet<TreeNode>();
//		edges = new ArrayList<TreeEdge>();
	}
	
	public Tree() {
		treeStruct = new HashMap<TreeNode, ArrayList<TreeEdge>>();
		
	}
	
	public void setHeight() {
		TreeNode r = this.root;
		System.out.println(root == null);
		r.setDepth(0);
		height = 0;
		LinkedList<TreeNode> heighted = new LinkedList<TreeNode>();
		heighted.addLast(r);
		while(!heighted.isEmpty()) {
			TreeNode tn = heighted.removeFirst();
			for(TreeEdge te : treeStruct.get(tn)) {
				int fromDepth = tn.getDepth();
				te.getTo().setDepth(fromDepth+1);
				if(height  < fromDepth+1) {
					height = fromDepth+1;
				}
				heighted.addLast(te.getTo());
			}
//			nodes.remove(tn);
//			treeStruct.remove(tn);
		}
	}
	
	public TreeNode getRoot() {
		return root;
	}
	
	public void setRoot(TreeNode n) {
		root = n;
		n.setXYZ(0, 0, 0);
		layout();
		
	}
	
	private void addTreeEdge(TreeEdge e, HashMap<TreeNode, ArrayList<TreeEdge>> treeStruct) {
		for(TreeNode n : treeStruct.keySet()) {
			if(n.equals(e.getFrom())) {
				treeStruct.get(n).add(e);
				return;
			}
		}
		ArrayList<TreeEdge> tmp_edges = new ArrayList<TreeEdge>();
		tmp_edges.add(e);
		treeStruct.put(e.getFrom(), tmp_edges);
	}
	
	private void deleteTreeEdge(TreeEdge e, HashMap<TreeNode, ArrayList<TreeEdge>> treeStruct) {
		for(TreeNode n : treeStruct.keySet()) {
			if(n.equals(e.getFrom())) {
				ArrayList<TreeEdge> tmp_edges = treeStruct.get(n);
				for(TreeEdge te : tmp_edges) {
					if(te.equals(e)) {
						if(tmp_edges.size() == 1) {
							treeStruct.remove(n);
							return;
						} else {
							tmp_edges.remove(e);
							return;
						}
					}
				}
				return;
			}
		}
	}
	
	private void deleteSubTree(TreeNode r) {
		//delete all subtrees of node r
		LinkedList<TreeNode> deleted = new LinkedList<TreeNode>();
		deleted.addLast(r);
		while(!deleted.isEmpty()) {
			TreeNode tn = deleted.removeFirst();
			for(TreeEdge te : treeStruct.get(tn)) {
//				edges.remove(te);
				deleted.addLast(te.getTo());
			}
//			nodes.remove(tn);
			treeStruct.remove(tn);
		}
		//delete edge pointing to r
		for(TreeNode n : treeStruct.keySet()) {
			ArrayList<TreeEdge> tes = treeStruct.get(n);
			for(TreeEdge e : tes) {
				if(e.getTo().equals(r)) {
					deleteTreeEdge(e, treeStruct);
					return;
				}
			}
		}
	}
	
	public void closeSubTree(TreeNode r) {
		LinkedList<TreeNode> closed = new LinkedList<TreeNode>();
		for(TreeEdge e : treeStruct.get(r)) {
			closed.addLast(e.getTo());
		}
		while(!closed.isEmpty()) {
			TreeNode tn = closed.removeFirst();
			for(TreeEdge te : treeStruct.get(tn)) {
				closed.addLast(te.getTo());
			}
			tn.setVisible(false);
		}
	}
	
	public void expandSubTree(TreeNode r) {
		LinkedList<TreeNode> closed = new LinkedList<TreeNode>();
		for(TreeEdge e : treeStruct.get(r)) {
			closed.addLast(e.getTo());
		}
		while(!closed.isEmpty()) {
			TreeNode tn = closed.removeFirst();
			for(TreeEdge te : treeStruct.get(tn)) {
				closed.addLast(te.getTo());
			}
			tn.setVisible(true);
		}
	}
	
	public ArrayList<TreeNode> getChildrenNodes(TreeNode n) {
		ArrayList<TreeNode> childrenNodes = new ArrayList<TreeNode>();
		for(TreeEdge e : treeStruct.get(n)) {
			childrenNodes.add(e.getTo());
		}
		return childrenNodes;
	}
	
	public ArrayList<TreeEdge> getEdges(TreeNode n) {
		return treeStruct.get(n);
	}

	public void addNode(TreeNode n) {
		//set the first node added to be root
		if(root == null) {
			root = n;
		}
		ArrayList<TreeEdge> treeEdges = treeStruct.get(n);
		System.out.println("to add n:" + n.getId());
		if(treeEdges == null) {
			treeStruct.put(n, new ArrayList<TreeEdge>());
		}else{
		}
		System.out.println("tree.addNode tree key size:" +treeStruct.keySet().size());
		for(TreeNode tn : treeStruct.keySet()){
			System.out.println("node id -- " + tn.getId() + " " + tn.toString());
		}
		n.setX(random.nextDouble());
		n.setY(random.nextDouble());
		n.setZ(random.nextDouble());
		
	}
	
	public TreeNode getNodeById(String id) {
		System.out.println(treeStruct.hashCode() + " treeStruct key size:" + treeStruct.keySet().size() + " search:" + id);
		for(TreeNode tn : treeStruct.keySet()) {
			System.out.println("tree:" + tn.getId());
			if(tn.getId().equals(id)) {
				return tn;
			}
		}
		return null;
	}

	public void addEdge(TreeEdge e) {
		addTreeEdge(e, treeStruct);
	}
	
	public void addEdge(String fromId, String toId) {
		System.out.println("fromId:" + fromId + " toId:" + toId);
		TreeNode fn = null;
		TreeNode tn = null;

		fn = getNodeById(fromId);
		tn = getNodeById(toId);
		System.out.println(fn == null);
		System.out.println(fn.getId());
		System.out.println(tn == null);
		System.out.println(tn.getId());
//		addEdge(new TreeEdge(fn, tn));
		tn.setDepth(fn.getDepth()+1);
		System.out.println("fn:" + fn.getId() + "  tn:" + tn.getId());
		treeStruct.get(fn).add(new TreeEdge(fn, tn));
		this.setHeight();
		this.setDepthColor();
//		System.out.println("TreeEdge added: "+fn.getId()+"--"+tn.getId());
	}

	public void deleteNode(TreeNode n) {
		deleteSubTree(n);
	}
	
	public void deleteNode(String id) {
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(root);
		while(!looked.isEmpty()) {
			TreeNode tn = looked.removeFirst();
			if(tn.getId().equals(id)) {
				deleteNode(tn);
				return;
			} else {
				for(TreeEdge e : treeStruct.get(tn)) {
					looked.addLast(e.getTo());
				}
			}
		}
	}

	public void deleteEdge(TreeEdge e) {
//		edges.remove(e);
		deleteTreeEdge(e, treeStruct);
	}
	
	public void deleteEdge(String fromId, String toId) {
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(root);
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			for(TreeEdge e : treeStruct.get(n)) {
				if(e.getFrom().getId().equals(fromId) && e.getTo().getId().equals(toId)) {
					deleteTreeEdge(e, treeStruct);
					return;
				} else {
					looked.addLast(e.getTo());
				}
			}
		}
	}
	
	public TreeNode getNearestNode(double x, double y, double z) {
		double dist = 0.1;
		TreeNode rn = null;
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.add(root);
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			double tmpDist = Math.sqrt(Math.pow(n.getX()-x, 2)+Math.pow(n.getY()-y, 2)+Math.pow(n.getZ()-z, 2));
			if(tmpDist<dist) {
				rn = n;
				dist = tmpDist;
			}
			for(TreeEdge e : treeStruct.get(n)) {
				looked.addLast(e.getTo());
			}
		}
		
		return rn;
	}

	public void layout() {

		Random random = new Random();
		LinkedList<TreeNode> layouted = new LinkedList<TreeNode>();
		layouted.add(root);
		while(!layouted.isEmpty()) {
			TreeNode pn = layouted.removeFirst();
//			System.out.println(pn.getId()+": "+pn.getX()+","+pn.getY()+","+pn.getZ());
			ArrayList<TreeEdge> tmp_edges = treeStruct.get(pn);
			int childrenCount = tmp_edges.size();
			int i = 0;
			for(TreeEdge e : tmp_edges) {
				i++;
				TreeNode n = e.getTo();
//				n.setXYZ(random.nextDouble(),random.nextDouble(),random.nextDouble());
				layouted.addLast(n);
			}
		}
	}
	public void updateLayout(int times) {
		updateLayout(times,"",this.treeStruct);
	}
	public void updateLayout(int times,String way) {
		updateLayout(times,way,this.treeStruct);
	}
	public void updateLayout(int times,String way,HashMap<TreeNode, ArrayList<TreeEdge>> treeStruct) {
		while(times > 0) {
			times --;
			//resistance force
			for(TreeNode sn : treeStruct.keySet()) {
				for(TreeNode n : treeStruct.keySet()) {
					if(!sn.getId().equals(n.getId())) {
						XYZ snp = sn.getXYZ();
						XYZ np = n.getXYZ();
						double d2 = Math.pow(snp.getX()-np.getX(), 2)+Math.pow(snp.getY()-np.getY(), 2)+Math.pow(snp.getZ()-np.getZ(), 2);
//						System.out.println("distance: "+d2);
						if(d2 == 0) {
							d2 = 1;
						}

						double dx = snp.getX()-np.getX();
						double dy = snp.getY() - np.getY();
						double dz = snp.getZ() - np.getZ();
						dx = dx == 0? 1 : dx;
						dy = dy == 0? 1 : dy;
						dz = dz == 0? 1 : dz;
						sn.addForce(kr*(dx)*Math.pow(d2, -1.5), kr*(dy)*Math.pow(d2, -1.5), kr*(dz)*Math.pow(d2, -1.5));
					}
				}
			}
			//attraction force
			for(TreeNode sn : treeStruct.keySet()) {
				for(TreeEdge te : treeStruct.get(sn)) {
					
					TreeNode dn = te.getTo();
					XYZ snp = sn.getXYZ();
					XYZ dnp = dn.getXYZ();

					sn.addForce(ka*(dnp.getX() - snp.getX()), ka*(dnp.getY() - snp.getY()), ka*(dnp.getZ() - snp.getZ()));
					dn.addForce(ka*(snp.getX() - dnp.getX()), ka*(snp.getY() - dnp.getY()), ka*(snp.getZ() - dnp.getZ()));
				}

			}
			

			
			//set move
			for(TreeNode sn : treeStruct.keySet()) {
				XYZ force = sn.getForce();
				XYZ p = sn.getXYZ();
				double stepX = force.getX()*0.01;
				double stepY = force.getY()*0.01;
				double stepZ = force.getZ()*0.01;
				
				sn.setXYZ(p.getX()+force.getX()*0.01, p.getY()+force.getY()*0.01, p.getZ()+force.getZ()*0.01);
//				System.out.println(way + "( " + stepX + "," + stepY + "," + stepY +  " )");
				sn.setForce(new XYZ(0,0,0));

			}
			/*
			if(!"".equals(way)){
				for(TreeNode sn : treeStruct.keySet()) {
					XYZ force = sn.getForce();
					XYZ p = sn.getXYZ();
					double stepX = force.getX()*0.01;
					double stepY = force.getY()*0.01;
					double stepZ = force.getZ()*0.01;
					
					sn.setXYZ(p.getX()+force.getX()*0.01, p.getY()+force.getY()*0.01, p.getZ()+force.getZ()*0.01);
//					System.out.println(way + "( " + stepX + "," + stepY + "," + stepY +  " )");
					sn.setForce(new XYZ(0,0,0));
	
				}
			}
			*/
			root.setXYZ(0, 0, 0);
			
//			System.out.println("update once complete");
		}
	}
	
	public void setDepthColor() {
		RGBColor fromColor = TreeVisualizeListener.fromColor;
		RGBColor toColor = TreeVisualizeListener.toColor;
		
		float dr = toColor.getRed() - fromColor.getRed();
		float dg = toColor.getGreen() - fromColor.getGreen();
		float db = toColor.getBlue() - fromColor.getBlue();
		
		for(TreeNode tn : this.treeStruct.keySet()) {
			tn.setOriColor(new RGBColor(fromColor.getRed()+dr*tn.getDepth()/height, fromColor.getGreen()+dg*tn.getDepth()/height, fromColor.getBlue()+db*tn.getDepth()/height));
		}
		
		root.setOriColor(TreeVisualizeListener.rootColor);
	}

}
