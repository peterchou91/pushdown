package pushdown.util;

public class Utils {
	public static String formatLabelString(String label){
		StringBuilder ret = new StringBuilder();
		int com = 0;// number of ','
		for(char c : label.toCharArray()){
			if(c == ','){
				com++;
			}else{
				ret.append(c);
			}
			
		}
		
		if(com > 0){
			ret.replace(ret.length() - 2, ret.length() - 1, "");
		}
		return ret.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(formatLabelString("p(a,#)"));
	}
}
