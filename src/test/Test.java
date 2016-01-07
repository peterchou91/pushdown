package test;

import java.util.ArrayList;
import java.util.List;

public class Test {
	
	public static void hanoi(int n,String one,String two,String three){
		if(n == 1){
			move(n,one,three);
		}else{
			hanoi(n - 1,one,three,two);
			move(n,one,three);
			hanoi(n - 1,two,one,three);
		}
	}
	
	private static void move(int n, String one, String three) {
		System.out.println(n + ":" + one + "-->" + three);
		
	}

	public static void main(String[] args) {
		//hanoi(3,"one","two","three");
		List<String> list = new ArrayList<String>();
		System.out.println(list.hashCode());
		list.add("2");
		System.out.println(list.hashCode());
	}
}
