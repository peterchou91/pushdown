package sctl.paint.graph;

public class StateEdge {
	private StateNode pn;
	private StateNode cn;
	private RGBColor color;
	private float size;
	
	public StateEdge(StateNode pn, StateNode cn) {
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
	
	public StateNode getFrom() {
		return pn;
	}
	
	public StateNode getTo() {
		return cn;
	}
}
