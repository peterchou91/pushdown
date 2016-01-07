package sctl.paint.treeViewer;

import java.util.LinkedList;

import com.jogamp.opengl.GLAutoDrawable;

import sctl.paint.graph.Tree;
import sctl.paint.graph.TreeNode;

public class ClearColorAffect implements AssistAffect {
	private Tree tree;
	
	public ClearColorAffect(Tree tree) {
		this.tree = tree;
	}
	
	@Override
	public void affect(GLAutoDrawable gld) {
		// TODO Auto-generated method stub
		LinkedList<TreeNode> looked = new LinkedList<TreeNode>();
		looked.addLast(tree.getRoot());
		while(!looked.isEmpty()) {
			TreeNode n = looked.removeFirst();
			n.setPicked(false);
			n.clearColor();
			for(TreeNode tn : tree.getChildrenNodes(n)) {
				looked.addLast(tn);
			}
		}
	}

}
