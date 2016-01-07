package sctl.paint.graph;

public class TreeNode {
	private RGBColor oriColor = new RGBColor();
	protected String id;
	protected double size;
	protected XYZ xyz;
	protected RGBColor color;
	protected boolean visible;
	protected String label;
	public String cutInfo;
	public boolean showCutLabel;
	protected boolean showLabel;
	protected boolean showSubtree;
	protected boolean picked;
	protected XYZ force;
	private int level;
	protected int depth;
	
	//add by ZhouQing
	public boolean highLight = false;
	public boolean hitted = false;
	public int referd = 0;
	
	public TreeNode(){
		
	}

	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setOriColor(RGBColor oriColor) {
		this.oriColor = oriColor;
	}

	public RGBColor getOriColor() {
		return oriColor;
	}

	public XYZ getForce() {
		return force;
	}

	public void setForce(XYZ force) {
		this.force = force;
	}
	public void addForce(double xf, double yf, double zf) {
		force.addXyz(xf, yf, zf);
	}
	//add by zhouqing
	protected boolean isCuted;
	
	public void setIsCuted(boolean isCuted){
		this.isCuted = isCuted;
	}
	
	public boolean getIsCuted(){
		return this.isCuted;
	}
	

	public TreeNode(String id, String label) {
		this.xyz = new XYZ(0,0,0);
		this.color = new RGBColor(0,0, 1);
		this.visible = true;
		this.id = id;
		this.size = 0.1;
		this.label = label;
		this.showLabel = false;
		this.showSubtree = true;
		this.picked = false;
		this.force = new XYZ(0,0,0);
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getId() {
		return id;
	}
	
	public void setXYZ(double x, double y, double z) {
		xyz.setX(x);
		xyz.setY(y);
		xyz.setZ(z);
	}
	
	public void setX(double x) {
		xyz.setX(x);
	}
	
	public void setY(double y) {
		xyz.setY(y);
	}
	
	public void setZ(double z) {
		xyz.setZ(z);
	}
	
	public XYZ getXYZ() {
		return this.xyz;
	}
	
	public double getX() {
		return xyz.getX();
	}
	
	public double getY() {
		return xyz.getY();
	}
	
	public double getZ() {
		return xyz.getZ();
	}
	
	public void setColor(float red, float green, float blue) {
		color.setColor(red, green, blue);
	}
	
	public void setColor(RGBColor color) {
		this.color = color;
	}
	
	public RGBColor getColor() {
		return this.color;
	}
	
	public void clearColor() {
		this.color = new RGBColor(oriColor.getRed(), oriColor.getGreen(), oriColor.getBlue());
	}
	
	public double getSize() {
		return size;
	}
	
	public void resetSize() {
		size = 0.1;
	}
	
	public void setSize(double s) {
		size = s;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean v) {
		this.visible = v;
	}
	
	public boolean isLableVisible() {
		return showLabel;
	}
	
	//add
	public boolean isCutLableVisible() {
		return this.showCutLabel;
	}
	
	public void setLabelVisible(boolean v) {
		showLabel = v;
	}
	
	//add 
	public void setCutLabelVisible(boolean v) {
		this.showCutLabel = v;
	}
	
	public boolean isShowSubtree() {
		return showSubtree;
	}

	public void setShowSubtree(boolean showSubtree) {
		this.showSubtree = showSubtree;
	}
	
	public boolean isPicked() {
		return picked;
	}

	public void setPicked(boolean picked) {
		this.picked = picked;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TreeNode) {
			if(id.equals(((TreeNode) obj).id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public int getDepth() {
		// TODO Auto-generated method stub
		return depth;
	}
	
	public void setDepth(int d) {
		this.depth = d;
	}
	
}
