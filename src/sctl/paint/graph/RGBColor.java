package sctl.paint.graph;

public class RGBColor {
	private float red = 1.0f;
	private float green = 1.0f;
	private float blue = 1.0f;
	
	public RGBColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public RGBColor(int red, int green, int blue) {
		this.red = (float)(red/255.0);
		this.green = (float)(green/255.0);
		this.blue = (float)(blue/255.0);
	}
	
	public RGBColor() {
	}
	
	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	public void setColor(int red, int green, int blue) {
		this.red = (float)(red/255.0);
		this.green = (float)(green/255.0);
		this.blue = (float)(blue/255.0);
	}
	
	public float getRed() {
		return red;
	}
	
	public float getGreen() {
		return green;
	}
	
	public float getBlue() {
		return blue;
	}
	public String toString(){
		return "(" + red + "," + green + "," + blue + ")";
	}
}
