package test;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
/**
 * This is a basic JOGL app. Feel free to
 * reuse this code or modify it.
 */
public class FocusExample extends JFrame {
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);list.add(2);list.add(3);
		for(Integer i :list) System.out.println(i);
	}
}