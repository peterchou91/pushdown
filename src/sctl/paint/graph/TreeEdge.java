package sctl.paint.graph;

public class TreeEdge {
	private TreeNode pn;
	private TreeNode cn;
	private RGBColor color;
	private float size;
	
	public TreeEdge(TreeNode pn, TreeNode cn) {
		this.pn = pn;
		this.cn = cn;
		this.color = new RGBColor();
		this.size = 2.0f;
	}
	
	public void setColor(float red, float green, float blue) {
		color.setColor(red, green, blue);
	}
	
	public void clearColor() {
		color = new RGBColor();
	}
	
	public void setSize(float s) {
		size = s;
	}
	
	public float getSize() {
		return size;
	}
	
	public TreeNode getFrom() {
		return pn;
	}
	
	public TreeNode getTo() {
		return cn;
	}
}
