package sctl.paint.treeViewer;

import javax.swing.JOptionPane;

import com.jogamp.opengl.GLAutoDrawable;

import sctl.paint.graph.TreeNode;

public class ShowCutHideLabelAffect implements AssistAffect {

	private TreeNode n;
	
	public ShowCutHideLabelAffect(TreeNode n) {
		this.n = n;
	}
	@Override
	public void affect(GLAutoDrawable gld) {
//		System.out.println("Showing label of "+n.getId());
		//n.setCutLabelVisible(!n.isCutLableVisible());
		JOptionPane.showMessageDialog(null, n.cutInfo, "cut", JOptionPane.PLAIN_MESSAGE);
	}

}
