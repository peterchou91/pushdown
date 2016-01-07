package pushdown.main;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import pushdown.entity.Configuration;
import pushdown.entity.ProveNode;
import pushdown.entity.Rule;
import pushdown.util.Constants;
import pushdown.util.Logger;

public class Test {
	
	public static List<Integer> l1 = new ArrayList<Integer>();
	static List<Set<Integer>> list = new ArrayList<Set<Integer>>();
	
	static{
		for(int i = 0; i < 100 ;i++){
			l1.add(i);
		}
		Set<Integer> s1 = new HashSet<Integer>();
		s1.add(1);
		s1.add(2);
		list.add(s1);
		
		Set<Integer> s2 = new HashSet<Integer>();
		s2.add(3);
		s2.add(4);
		list.add(s2);
		
		Set<Integer> s3 = new HashSet<Integer>();
		s3.add(5);
		s3.add(6);
		list.add(s3);
	}
	
	public static List<Set<Integer>> deepFirst(List<Set<Integer>> list){
		List<Set<Integer>> retList = new ArrayList<Set<Integer>>();
		Stack<Integer> stack = new Stack<Integer>();
		int i = 0;
		dfs(list.get(i),i,stack,list.size(),retList);
		
		return retList;
	}
	
	private static void dfs(Set<Integer> s,int level,Stack<Integer> stack,int n,List<Set<Integer>> retlist) {
		for(Integer integer : s){
			stack.push(integer);
			System.out.println("push:" + integer);
			if(level == n - 1){
				Set<Integer> tempSet = new HashSet<Integer>();
				tempSet.addAll(stack);
				retlist.add(tempSet);
			}
			if( level + 1 < n){
				dfs(list.get(level+1),level+1,stack,n,retlist);
			}
			int tempInt = stack.pop();
			System.out.println("pop:" + tempInt);
		}
		
	}
	public static String[] matchedWord(String[] targetWord,String[] word){
		String[] ret = null;
		int len = targetWord.length < word.length ? targetWord.length : word.length;
		for(int i = 0; i < len; i++){
			if(targetWord[i].equals(word[i])){
				continue;
			}else if(word[i].equals("x")){
				ret = Arrays.copyOfRange(targetWord, i, targetWord.length);
			}
		}
		if(null == ret && word[len].equals("x")){
			ret = new String[]{"#"};
		}
		return ret;
	}
	public static void main(String[] args) {
//		List<Set<Integer>> retList =  deepFirst(list);
//		for(Set<Integer> s : retList){
//			System.out.println(s);
//		}
		/*int[] targetWord = new int[]{0,1,2,3,4,5,6,7};
		System.out.println(Arrays.toString(Arrays.copyOfRange(targetWord, 1, 4)));*/
		
//		System.out.println(Arrays.toString(matchedWord(new String[]{"b"},new String[]{"b","x"})));
/*		Set<String> introStateSet = new HashSet<String>();
		Set<String> neutralStateSet = new HashSet<String>();
		introStateSet.add("p0");
		neutralStateSet.add("p0");
		System.out.println(introStateSet);
		System.out.println(neutralStateSet);
		System.out.println(introStateSet.containsAll(neutralStateSet));*/
		LinkedList<Integer> queue = new LinkedList<Integer>();
			queue.offer(1);
			queue.offer(null);
			queue.offer(2);
			queue.offer(null);
			Integer e = null;
			while(queue.size() != 0){
				e = queue.poll();
				if(e == null){
					Logger.debug("null");
				}else
					Logger.debug(e.toString());
				
				
			}
			
		}
		
	
}
