package sctl;

import java.util.ArrayList;
import java.util.HashSet;

public class Test {

	/**
	 * @param args
	 */
	
	public void test(ArrayList<String> strs) {
		if (strs.size() == 1) {
			System.out.println(strs.remove(0));
		} else {
			System.out.println(strs.remove(0));
			test(strs);
		}
	}
	
	public void testArray(int[] a) {
		a[0] = 1;
	}
	public static void main(String[] args) {
		
//		HashSet<AtomicFml> hs = new HashSet<AtomicFml>();
//		hs.add(new AtomicFml("safe", new ArrayList<String>()));
//		
//		System.out.println(hs.contains(new AtomicFml("safe", new ArrayList<String>())));
//		int[] a = new int[3];
//		for(int i : a) {
//			System.out.print(i);
//		}
//		Test t = new Test();
//		t.testArray(a);
//		for(int i : a) {
//			System.out.print(i);
//		}
		HashSet<String> hs = new HashSet<String>();
		hs.remove("test");
	}

}
