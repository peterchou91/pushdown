package sctl.paint.treeViewer;

import com.jogamp.opengl.GLAutoDrawable;

import sctl.paint.graph.RGBColor;
import sctl.paint.graph.TreeNode;

public class PickNodeAffect implements AssistAffect {
	private TreeNode node;
	private RGBColor color;
	
	public PickNodeAffect(TreeNode node, RGBColor color) {
		this.color = color;
		this.node = node;
	}

	@Override
	public void affect(GLAutoDrawable gld) {
		node.setPicked(true);
		node.setColor(color.getRed(), color.getGreen(), color.getBlue());
	}

}
