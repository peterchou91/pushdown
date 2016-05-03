package pushdown.util;

import java.util.HashMap;
import java.util.Map;

import sctl.paint.graph.RGBColor;

public class ColorPicker {
	
	public static Map<String,RGBColor> colorMap = new HashMap<String,RGBColor>();
	
	static{
		colorMap.put("black", new RGBColor(0,0,0));
		colorMap.put("green", new RGBColor((float)0,(float)(255/255.0),(float)0));
		colorMap.put("red",new RGBColor((float)1.0,0,0));
	}
	
	

}
