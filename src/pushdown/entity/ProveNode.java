package pushdown.entity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ProveNode{
	
	private boolean root = false;
	
	public boolean proved = false; 
	
	public boolean highLight = false;
	
	public boolean hitted = false;
	
	public int refered = 0;
	
	
	
	public int level = 0;
	public boolean isCut;
	
	public int pid;
	public int id;
	private Configuration configuration;
	public Rule fromRule;
	
	public Rule rule;//for model view
	
	private Set<ProveNode> children = new HashSet<ProveNode>();
	
	public ProveNode parent = null;
	
	private static int count = 0;
	{
		id = count++;
	}
	
	public ProveNode(){
		
	}

	public int getPid(){
		return pid;
	}
	
	public void setPid(int pid){
		this.pid = pid;
	}
	
	public int getId(){
		return id;
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Set<ProveNode> getChildren() {
		return children;
	}

	public void setChildren(Set<ProveNode> children) {
		this.children = children;
	}
	
	@Override
	public String toString(){
		StringBuilder ret = new StringBuilder();
		ret.append(this.configuration == null?"#" : configuration.toString());
		ret.append("pid :").append(pid).append(" id:").append(id);
		return ret.toString();
	}
	
	public String getLabel(){
		if(rule == null){
			//for model view
			return ConfigurationString();
		}else{
			return rule.toString();
		}
	}
	public String ConfigurationString(){
		StringBuilder ret = new StringBuilder();
		ret.append(this.configuration == null?"#" : configuration.toString());
		
		return ret.toString();
	}

	
	public boolean isRoot() {
		// TODO Auto-generated method stub
		return false;
	}

//	public boolean isProved() {
//	    if(children.size() == 0) return false;
//		for(ProveNode child : children){
//			if(!child.proved) return false;
//		}
//		return true;
//	}
	
	public String getFromInfo(){
		StringBuilder sb = new StringBuilder();
		sb.append("inferred by: \n").append(fromRule).append("\n");
		sb.append(fromRulesToString());
		sb.append("------------------\n");
		return sb.toString();
	}

	private String fromRulesToString() {
		if(fromRule == null) return "No from rule\n";
		StringBuilder ret = new StringBuilder();
		Queue<Rule> queue = new LinkedList<Rule>();
		queue.offer(fromRule);
		while(!queue.isEmpty()){
			Rule curRule = queue.poll();
			if(curRule.fromRules != null && curRule.fromRules.size() != 0){
				for(Rule rule : curRule.fromRules){
					ret.append(rule).append("\n");
					if(rule.fromRules != null &&rule.fromRules.size() != 0){
						queue.offer(rule);
					}
				}
				ret.append("-- -- -- -- --\n");
			}
		}
		return ret.toString();
	}

	public static void main(String[] args) {
		Queue<String> queue = new LinkedList<String>();
		queue.offer("1");
		queue.offer("2");
		queue.offer("3");
		System.out.println(queue.poll());
		System.out.println(queue.poll());
		System.out.println(queue.poll());
		System.out.println(queue.isEmpty());
	}
	
}
