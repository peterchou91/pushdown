package pushdown.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import pushdown.entity.Configuration;
import pushdown.entity.ProveNode;
import pushdown.entity.Rule;
import pushdown.util.Constants;
import pushdown.util.Logger;
import pushdown.util.Parse;
import sctl.paint.graph.TreeNode;
import sctl.paint.treeViewer.TreeVisualizer;


public class PDModel {
	
	//
	public static String targetConfiguration = "p(#)";
	
	public static int maxDepth = 0;
	private static Set<Rule> rules = new HashSet<Rule>();
	
	private static Set<Rule> addedRules = null;
	
	private static Set<Configuration> addedConfigurations = new HashSet<Configuration>();
	
	private static Set<Configuration> configurations = null;
	
	private static Set<Rule> elimRules = new HashSet<Rule>();
	private static Set<Rule> introRules = new HashSet<Rule>();
	private static Set<Rule> neutralRules = new HashSet<Rule>();
	
	private static Set<Rule> addElimRules = new HashSet<Rule>();
	private static Set<Rule> addIntroRules = new HashSet<Rule>();
	private static Set<Rule> addNeutralRules = new HashSet<Rule>();
	
	private static Map<String,Set<Rule>> wordIntroRuleMap = new HashMap<String,Set<Rule>>();
	
	private static List<Rule> specialNeutralRule = new ArrayList<Rule>();
	
	private static Set<String> symbols = null;
	
	private static Set<Rule> multiRules = new HashSet<Rule>();
	
	private static Map<Configuration,List<Rule>> conRulesMap = new HashMap<Configuration,List<Rule>>();
	
	private static Set<Rule> complementationRules = new HashSet<Rule>();
	
	private static HashMap<String, Set<Rule>> constateRuleMap = new HashMap<String,Set<Rule>>();
	private static HashMap<String, Set<Rule>> constateRuleMapForCom = new HashMap<String,Set<Rule>>();
	
	static ProveNode oriPn = null;
	static ProveNode pn = null;
	
	public static TreeVisualizer proofViewer = new TreeVisualizer("PushDown");
	public static TreeVisualizer oriProofViewer = new TreeVisualizer("oriPushDown");
	public static TreeVisualizer modelViewer = new TreeVisualizer("model");
	
	public static Map<Rule,ProveNode> rule2PnModel = new HashMap<Rule,ProveNode>();
	
//	private static List<Rule> ama = new ArrayList<Rule>();
	
	public static List<Rule> l = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PDModel.doProve();
	}

	public static void doProve() {
		Logger.debug("start1");
		Parse.init();
		rules = Parse.getRules();

		configurations = Parse.getConfigurations();
		symbols = Parse.getSymbols();
		Logger.debug("******* symbols:" + symbols.toString());
		
		Logger.debug("----------rules size:" + rules.size() + "---------");
		HashSet<Rule> hsRules = new HashSet<Rule>(rules);
		Logger.debug("------------------------------");
		
		//original prove path 
		Configuration c = Parse.pareConfiguration(targetConfiguration); //new Configuration();
		
		
		HashMap<String, ProveNode> oriCon2PnHm = new HashMap<String,ProveNode>();

		PDModel.oriProofViewer.show(Constants.ori);		
		oriPn = findProvePath(c,rulesToStateRuleMap(rules),oriCon2PnHm,Constants.ori);
//		System.exit(0);
//		Logger.debug(configurations.size()+"");
		for(Rule rule : rules){
			rule.toSmallStep();
			switch(rule.getForm()){
			case Constants.ELIMINATION:
				elimRules.add(rule);
				break;
			case Constants.INTRODUCTION:
				introRules.add(rule);
				String key = rule.getConfiguration().getWord()[0];
				if(wordIntroRuleMap.containsKey(key)){
					wordIntroRuleMap.get(key).add(rule);
				}else{
					Set<Rule> temp = new HashSet<Rule>();
					temp.add(rule);
					wordIntroRuleMap.put(rule.getConfiguration().getWord()[0], temp);
				}
				break;
			case Constants.NEUTRAL:
				neutralRules.add(rule);
				if(rule.getPremise().size() == 0){
					specialNeutralRule.add(rule);
				}
				break;
			}
		}

		 
		APS2STAPS();
		
		Comparator<Rule> comparator = new Comparator<Rule>() {
			@Override
		    public int compare(Rule r1, Rule r2) {
		        if(r1.getForm() == r2.getForm()){
		        	return 0;
		        }else if(r1.getForm() > r2.getForm()){
		        	return 1;
		        }else return -1;
		    }
		}; 
		Rule[] rulesArray = rules.toArray(new Rule[rules.size()]);
		Arrays.sort(rulesArray, comparator);
		for(Rule rule : rulesArray){
			Logger.debug(rule.toString(), Constants.SAMLLSTEP);
			Logger.debug(rule);
			Logger.debug();
		}
		
		saturation();
		complementation();
		
		for(Rule rule : complementationRules){
			Logger.debug(rule.toComplementationString(), Constants.COMPLEMENTATION);
			Logger.debug(rule);
			Logger.debug();
		}
		
		//initialize model
	    List<ProveNode> model = new ArrayList<ProveNode>();
	    
	    int idCount = 0;
	    for(Rule r : introRules){
	    	ProveNode pn = new ProveNode();
	    	pn.id = idCount++;
	    	pn.rule = r;
	    	model.add(pn);
	    	rule2PnModel.put(r, pn);
	    }
		
		HashMap<String, ProveNode> con2PnHm = new HashMap<String,ProveNode>();
//		PDModel.proofViewer.show(Constants.pd);
//		pn = findProvePath(c,constateRuleMap, con2PnHm,Constants.pd);
		
		
		//find cut nodes
		Set<String> diffSet = new HashSet<String>();
		for( String oriCon : oriCon2PnHm.keySet()){
			if(!con2PnHm.keySet().contains(oriCon)){
				diffSet.add(oriCon);
			}
		}
		for(String key : diffSet){
			oriCon2PnHm.get(key).isCut = true;
		}
		
		//ProveNode pn1 = findProvePath(c1,s1,constateRuleMapForCom);
		
//		Logger.debug("findProvePath end");
//		System.out.println("print path pn:");
//		Logger.debug(pn == null);
//		printProvePath(pn);
//		System.out.println("print path oriPn:");
//		Logger.debug(oriPn == null);
//		printProvePath(oriPn);

		Logger.close();
	}
	
	private static void initTreeVisualizer(List<ProveNode> model, TreeVisualizer pf) {
		System.out.println(" initTreeVisualizer model start");
		int countNode = 0;
		int countEdge = 0;
		ProveNode parent = model.get(0);
		pf.addNodeForPD(parent, false, false,0);
		for(int i = 1; i < model.size(); ++i){
			ProveNode elem = model.get(i);
			maxDepth = Math.max(maxDepth,elem.level + 1);
			pf.addNodeForPD(elem, false, false,0);
			countNode++;
			pf.addEdge(parent.getId() + "", elem.getId() + "");//pushdown model needs no edge
			++countEdge;
			parent = elem;
		}
//		Logger.debug("model countNode:" + countNode + " countEdge:" + countEdge);
		
	}

	public static void initTreeVisualizer(ProveNode pn,TreeVisualizer pf){
		if (pn == null || pf == null){
			return;
		}
		Stack<ProveNode> stack = new Stack<ProveNode>();
		stack.push(pn);
		pf.addNodeForPD(pn, true,false,0);
		int count = 0;
		int countNode = 0;
		int countEdge = 0;
		while(!stack.isEmpty()){
			ProveNode elem = stack.pop();
			if(elem != null && elem.getChildren() != null && elem.getChildren().size() != 0){
				for(ProveNode child : elem.getChildren()){
					maxDepth = Math.max(maxDepth,elem.level + 1);
					pf.addNodeForPD(child, false, child.isCut,elem.level + 1);
					countNode++;
					pf.addEdge(elem.getId() + "", child.getId() + "");
					++countEdge;
					child.level = elem.level + 1;
					stack.push(child);
				}
			}
			count++;
		}
//		Logger.debug("countNode:" + countNode + " countEdge:" + countEdge);
	}
	private static void printProvePath(ProveNode pn) {
		if(pn == null) return;
		LinkedList<ProveNode> queue = new LinkedList<ProveNode>();
		
		prinPN(pn,queue);
		
	}

	private static void prinPN(ProveNode pn,LinkedList<ProveNode> queue) {
		pn.level = 0;
		queue.offer(pn);
//		queue.offer(new ProveNode());
		ProveNode e = null;
		int preLevel = 0;
		while(queue.size() != 0){
			e = queue.poll();
			if(e.level != preLevel){
				Logger.debug();
			}
			System.out.print(e.toString());
//			Logger.debug(e.getFromInfo());
			System.out.print(' ');

			if(e.getChildren() != null && e.getChildren().size() > 0){
				for(ProveNode p : e.getChildren()){

					p.level = e.level + 1;
					
					queue.offer(p);
				}
			}
			preLevel = e.level;
			
		}
		
	}

	public static void APS2STAPS() {
		
		for( Rule rule : rules){
			if(!(rule.getForm() > 0)){
				for(Configuration cf : rule.getPremise()){
					TransformCf2SmallStepCf(cf);
				}
				TransformCf2SmallStepCf(rule.getConfiguration());
			}
		}
		if(addedConfigurations != null)
		    configurations.addAll(addedConfigurations);
		
		if(addedRules != null)
			rules.addAll(addedRules);
		
		for(Rule rule : rules){
			rule.toSmallStep();
			switch(rule.getForm()){
			case Constants.ELIMINATION:
				elimRules.add(rule);
				break;
			case Constants.INTRODUCTION:
				introRules.add(rule);
				String key = rule.getConfiguration().getWord()[0];
				if(wordIntroRuleMap.containsKey(key)){
					wordIntroRuleMap.get(key).add(rule);
				}else{
					Set<Rule> temp = new HashSet<Rule>();
					temp.add(rule);
					wordIntroRuleMap.put(rule.getConfiguration().getWord()[0], temp);
				}
				break;
			case Constants.NEUTRAL:
				neutralRules.add(rule);
				if(rule.getPremise().size() == 0){
					specialNeutralRule.add(rule);
				}
				break;
			default:
				Logger.debug("瑙勫垯:\n" + rule +"涓嶈兘杞崲鎴�small step");
			}
		}
	}

	public static void TransformCf2SmallStepCf(Configuration cf) {
		if(null != cf){
			if(cf.getWord().length >= 0){
//				Logger.debug("************ " + cf.toString()+ " ****************");
				if(null == addedConfigurations){
					addedConfigurations = new HashSet<Configuration>();
				}
				if(addedRules == null){
					addedRules = new HashSet<Rule>();
				}
				String[] word = cf.getWord();
				String state = cf.getState();
				if(word.length != 1){
	
					StringBuilder preState = new StringBuilder(state);
					for(int i = 0; i < word.length - 1;i++){
				
						Configuration premiseC = new Configuration(preState + word[i],"");
						addedConfigurations.add(premiseC);
						Configuration conclusionC = new Configuration(preState.toString(),word[i]);
						addedConfigurations.add(conclusionC);
						
						Rule ruleIntroduciton = new Rule(premiseC,conclusionC);
						ruleIntroduciton.setForm(Constants.INTRODUCTION);
						introRules.add(ruleIntroduciton);
						addedRules.add(ruleIntroduciton);
						
//						Logger.debug("added Introduction rule:\n" + ruleIntroduciton.toString());
						
						Rule ruleElimination = new Rule(conclusionC,premiseC);
						ruleElimination.setForm(Constants.ELIMINATION);
						elimRules.add(ruleElimination);
						addedRules.add(ruleElimination);
//						Logger.debug("added Elimination rule:\n" + ruleElimination.toString());
					
						preState.append(word[i]);
					}
					cf.setState(preState.toString());
					cf.setWord("x");
//					Logger.debug("new Configuration:\n" + cf.toString());
				}
//				Logger.debug("***********************************************************");
			}
		}
	}


	public static void saturation1 () {
		
		for(Rule elimRule : elimRules){
			Set<Configuration> premise = elimRule.getPremise();
			Configuration target = null;
			for(Configuration c : premise){
				if(c.getWord().length == 2){
					target = c;
					break;
				}
			}
//			Logger.debug("target:" + target);
			if(null != target){
				for(Rule introRule : introRules){
					Logger.debug(introRule.getConfiguration().toString());
					if(target.equals(introRule.getConfiguration())){
						Rule tempNeutralRule = new Rule();
						if (tempNeutralRule.fromRules == null){
							tempNeutralRule.fromRules = new HashSet<Rule>();
						}
						
						tempNeutralRule.fromRules.add(introRule);
						tempNeutralRule.fromRules.add(elimRule);
						tempNeutralRule.fromType = 1;
						
						tempNeutralRule.setForm(Constants.NEUTRAL);
						tempNeutralRule.setConfiguration(elimRule.getConfiguration());
						Set<Configuration> tempPremise = new HashSet<Configuration>();
						for(Configuration c : premise){
							if(!c.equals(target)){
								tempPremise.add(c);
							}
						}
						tempPremise.addAll(introRule.getPremise());
						tempNeutralRule.setPremise(tempPremise);
						
						
						neutralRules.add(tempNeutralRule);
						rules.add(tempNeutralRule);
						addNeutralRules.add(tempNeutralRule);
						Logger.debug("saturation 1:added Neutral rule:\n "+ tempNeutralRule);
					}
				}
			}
			
		}
	}
		
	
	public static void saturation2 () {
			for(String key : wordIntroRuleMap.keySet()){
				if(!"#".equals(key)){
					Logger.debug("saturation2 key:" + key);
					Set<String> introStateSet = new HashSet<String>();
					
					HashMap<String,Rule> tempStateRuleMap = new HashMap<String,Rule>();
					for(Rule r : wordIntroRuleMap.get(key)){
//						Logger.debug(r.toString());
						introStateSet.add(r.getConfiguration().getState());
						tempStateRuleMap.put(r.getConfiguration().getState(), r);
					}
					

					for(Rule neutralRule : neutralRules){
						Set<String> neutralStateSet = new HashSet<String>();
						for(Configuration c : neutralRule.getPremise()){
							neutralStateSet.add(c.getState());
						}
						Logger.debug("introStateSet:" + introStateSet +"\n neutralStateSet:" + neutralStateSet);
						Logger.debug("introStateSet.containsAll(neutralStateSet):" + introStateSet.containsAll(neutralStateSet));
						if(introStateSet.containsAll(neutralStateSet)){
							Logger.debug("true");
							Rule rule = new Rule();
							
							rule.fromType = 2;
							//rule.fromRules.addAll(tempStateRuleMap.values());
							for(String state : neutralStateSet){
								rule.fromRules.add(tempStateRuleMap.get(state));
							}
							rule.fromRules.add(neutralRule);
							
							rule.setForm(Constants.INTRODUCTION);
							Configuration c = new Configuration(neutralRule.getConfiguration().getState(),key);
							Set<Configuration> tempPremise = new HashSet<Configuration>();
							for(Map.Entry<String,Rule> entry : tempStateRuleMap.entrySet()){
								if(neutralStateSet.contains(entry.getKey())){
									tempPremise.addAll(entry.getValue().getPremise());
								}
							}
							rule.setConfiguration(c);
							rule.setPremise(tempPremise);
							
							rules.add(rule);
							addIntroRules.add(rule);
							Logger.debug("saturation 2:added inroduction rule:\n "+ rule);
						}
					}
				}
					
			}
			
			Logger.debug("saturation2 specialNeutralRule --------------------------");
			for(Rule rule : specialNeutralRule){
//				Logger.debug("" + rule);
				for(String symbol :symbols ){
					//2
					if(!symbol.equals("#")){
						Rule r1 = new Rule();
						r1.fromType = 21;
						r1.fromRules.add(rule);
						r1.setForm(Constants.INTRODUCTION);
						Configuration c = new Configuration();
						c.setState(rule.getConfiguration().getState());
						c.setWord(symbol);
						r1.setConfiguration(c);
						r1.setPremise(rule.getPremise());
						
						rules.add(r1);
						addIntroRules.add(r1);
						Logger.debug("saturation 2:added inroduction rule:\n "+ r1);
					}
				}
				
				//3
				Rule r2 = new Rule();
				
				r2.fromType = 3;
				r2.fromRules.add(rule);
				
				r2.setForm(Constants.INTRODUCTION);
				Configuration c2 = new Configuration();
				c2.setState(rule.getConfiguration().getState());
				c2.setWord("#");
				r2.setConfiguration(c2);
				r2.setPremise(rule.getPremise());
				
				rules.add(r2);
				addIntroRules.add(r2);
				Logger.debug("saturation 3:added inroduction rule:\n "+ r2);
			}
			
			
		}
		
	
	public static void saturation3 () {
			String key = "#";
			Set<String> introStateSet = new HashSet<String>();
			
			if(null != wordIntroRuleMap.get(key)){
				for(Rule r : wordIntroRuleMap.get(key)){
					introStateSet.add(r.getConfiguration().getState());
				}	
			}else{

			}
			
			
			for(Rule neutralRule : neutralRules){
//				Logger.debug("neutralRule " + neutralRule);
				Set<String> neutralStateSet = new HashSet<String>();
				for(Configuration c : neutralRule.getPremise()){
					neutralStateSet.add(c.getState());
				}
				if(introStateSet.equals(neutralStateSet)){
					Rule rule = new Rule();
					rule.fromType = 3;
					rule.fromRules.add(neutralRule);
					
					rule.setForm(Constants.INTRODUCTION);
					Configuration c = new Configuration(neutralRule.getConfiguration().getState(),key);
					Set<Configuration> tempPremise = new HashSet<Configuration>();
					
					rule.setConfiguration(c);
					rule.setPremise(tempPremise);
					
					rules.add(rule);
					addIntroRules.add(rule);
					Logger.debug("saturation 3:added inroduction rule:\n "+ rule);
				}
			}
					
	}
		
	
	public static void saturation(){
		int oldSize = -1;
		int newSize = rules.size();
		while(oldSize != newSize){
			Logger.debug("oldSize:" + oldSize + ";newSize:" + newSize);
			Logger.debug("rules:" + rules.size() + ";introRules:" + introRules.size() + ";elimRules:" + elimRules.size() + ";neutralRules:" + neutralRules.size());
			saturation1();
			saturation2();
			saturation3();
			
			
			introRules.addAll(addIntroRules);
			elimRules.addAll(addElimRules);
			neutralRules.addAll(addNeutralRules);
			
			Logger.debug("after introRules");
			for(Rule r : introRules){
				Logger.debug(r.toString());
			}
			
			Logger.debug("after elimRules");
			for(Rule r : elimRules){
				Logger.debug(r.toString());
			}
			
			Logger.debug("after neutralRules");
			for(Rule r : neutralRules){
				Logger.debug(r.toString());
			}
			
			for(Rule rule :introRules){
				String key = rule.getConfiguration().getWord()[0];
				if(wordIntroRuleMap.containsKey(key)){
					wordIntroRuleMap.get(key).add(rule);
					Logger.debug("add to wordIntroRuleMap:" + rule);
				}else{
					Set<Rule> temp = new HashSet<Rule>();
					temp.add(rule);
					wordIntroRuleMap.put(rule.getConfiguration().getWord()[0], temp);
					Logger.debug("put to wordIntroRuleMap:" + rule);
				}
			}
			
			oldSize = newSize;
			newSize = rules.size();
			
		}
		
		Logger.debug("***********alternating multi-automaton start*******************");
		introRules.addAll(addIntroRules);
		for(Rule rule : rules){
			if(rule.getForm() == Constants.INTRODUCTION){
				multiRules.add(rule);
			}
		}
		for(Rule rule : rules){
			if(rule.getForm() == Constants.INTRODUCTION){
				multiRules.add(rule);
				if(conRulesMap.containsKey(rule.getConfiguration())){
					if(!conRulesMap.get(rule.getConfiguration()).contains(rule)){
						conRulesMap.get(rule.getConfiguration()).add(rule);
					}
				}else{
					List<Rule> tempList = new ArrayList<Rule>();
					tempList.add(rule);
					conRulesMap.put(rule.getConfiguration(),tempList);
				}
				
				
				if(constateRuleMap.containsKey(rule.getConfiguration().getState())){
					if(!constateRuleMap.get(rule.getConfiguration().getState()).contains(rule)){
						constateRuleMap.get(rule.getConfiguration().getState()).add(rule);
					}
				}else{
					Set<Rule> tempSet = new HashSet<Rule>();
					tempSet.add(rule);
					constateRuleMap.put(rule.getConfiguration().getState(),tempSet);
				}
				Logger.debug(rule.toString(), Constants.MULTI);
				Logger.debug("");
			}
			Logger.debug(rule.toString());
		}
		Logger.debug("***********alternating multi-automaton end********");
	}
	public static Map<String,Set<Rule>> rulesToStateRuleMap(Set<Rule> rules){
		if(null == rules || rules.size() == 0) return null;
		Map<String,Set<Rule>> constateRuleMap = new HashMap<String,Set<Rule>>();
		for(Rule rule : rules){
			if(constateRuleMap.containsKey(rule.getConfiguration().getState())){
				if(!constateRuleMap.get(rule.getConfiguration().getState()).contains(rule)){
					constateRuleMap.get(rule.getConfiguration().getState()).add(rule);
				}
			}else{
				Set<Rule> tempSet = new HashSet<Rule>();
				tempSet.add(rule);
				constateRuleMap.put(rule.getConfiguration().getState(),tempSet);
			}
		}
		return constateRuleMap;
	}
	public static List<Set<Configuration>> deepFirst(List<Set<Configuration>> list){
		List<Set<Configuration>> retList = new ArrayList<Set<Configuration>>();
		Stack<Configuration> stack = new Stack<Configuration>();
		int i = 0;
		dfs(list,i,stack,list.size(),retList);
		return retList;
	}
	
	private static void dfs(List<Set<Configuration>> list,int level,Stack<Configuration> stack,int n,List<Set<Configuration>> retlist) {
		for(Configuration Configuration : list.get(level)){
			stack.push(Configuration);
//			Logger.debug("push:" + Configuration);
			if(level == n - 1){
				Set<Configuration> tempSet = new HashSet<Configuration>();
				tempSet.addAll(stack);
				retlist.add(tempSet);
			}
			if( level + 1 < n){
				dfs(list,level+1,stack,n,retlist);
			}
			stack.pop();
		}
		
	}
	
	public static void complementation(){
		Logger.debug("-------------------------complementation ------------------");
		for(Configuration key : conRulesMap.keySet()){
			Configuration c2add = new Configuration();
			c2add.setState(key.getState());
			c2add.setWord(key.getWord());
			
			Logger.debug("key: \n" + key);
			List<Rule> tempRules = conRulesMap.get(key);
			List<Set<Configuration>> list = new ArrayList<Set<Configuration>>();
			for(Rule rule : tempRules){
				if(rule .getPremise() != null && rule.getPremise().size() > 0){
					list.add(rule.getPremise());
				}
			}
			List<Set<Configuration>> retList = null;
			if(list.size() > 0){
				retList = deepFirst(list);
			}
			if(null != retList){
				for(Set<Configuration> tempPremise :retList){
					Set<Configuration> pre2add = new HashSet<Configuration>();
					Rule tempCompleRule = new Rule();
					
					tempCompleRule.setConfiguration(c2add);
					tempCompleRule.setPremise(tempPremise);	
					complementationRules.add(tempCompleRule);
					tempCompleRule.toSmallStep();
					//Logger.debug(tempCompleRule.toString(),Constants.COMPLEMENTATION);
					Logger.debug("tempCompleRule:\n" + tempCompleRule);
				}
			}
			
		}
		for(Rule rule : complementationRules){
			rule.toSmallStep();
			
		}
		for(Rule rule : complementationRules){
			if(rule.getForm() == Constants.INTRODUCTION){
				
				if(constateRuleMapForCom.containsKey(rule.getConfiguration().getState())){
					if(!constateRuleMapForCom.get(rule.getConfiguration().getState()).contains(rule)){
						constateRuleMapForCom.get(rule.getConfiguration().getState()).add(rule);
					}
				}else{
					Set<Rule> tempSet = new HashSet<Rule>();
					tempSet.add(rule);
					constateRuleMapForCom.put(rule.getConfiguration().getState(),tempSet);
				}
			}
		}
	}
	
	public static String[] matchedWord(String[] targetWord,String[] word){
//		Logger.debug("targetWord:" + Arrays.toString(targetWord) + ";word:" + Arrays.toString(word));
		String[] ret = null;
		if(targetWord.length == 1 && word.length == 1 && targetWord[0].equals("#") && word[0].equals("#")){
			//targetWord:T(#),word:T(#)
			return new String[]{"#","#"};
		}else if(word.length == 1 && word[0].equals("#")) {
			//绫讳技word:T(#)
			return null;
		}
		int len = targetWord.length < word.length ? targetWord.length : word.length;
		int i = 0;
		for(i = 0; i < len; i++){
			if(targetWord[i].equals(word[i])){
				continue;
			}else{
				break;
			}
		}
		
		if(i == word.length - 1){
		//matched
			if(i < targetWord.length){
				ret = Arrays.copyOfRange(targetWord, i, targetWord.length);
			}else{
				ret = new String[]{"#"};
			}
		}else{
			ret = null;
		}
		return ret;
	}
	
	public static ProveNode findProvePath(Configuration c,Map<String, Set<Rule>> ruleMap,HashMap<String,ProveNode> Con2PnHm,String way){
		Logger.debug("start to findProvePath");
		if(null == c) return null;
		boolean proved = false;
		ProveNode retProveNode = new ProveNode();
		retProveNode.setConfiguration(c);
		//proofViewer.addNodeForPD(retProveNode, true,false);
		Stack<ProveNode> stack = new Stack<ProveNode>();
		stack.push(retProveNode);
		Con2PnHm.put(retProveNode.ConfigurationString(),retProveNode);
		String[] targetWord = null;
		int level = 0;
		
		//同步代码
		if(Constants.pd.equals(way)){
			proofViewer.addNodeForPD(retProveNode, false,false,level);
			PDModel.proofViewer.updateLayout(way);
		}else if(Constants.ori.equals(way)){
			oriProofViewer.addNodeForPD(retProveNode, false,false,level);
			PDModel.oriProofViewer.updateLayout(way);
		}
		
		int con = 0;
		while(!stack.empty()){
			if(TreeVisualizer.stop){
				return null;
			}
			if(con == 100){
				return null;
//				try{
//					Thread.sleep(3000);
//				}catch(Exception e){
//					e.printStackTrace();
//				}
			}
			con++;
			proved = false;
			ProveNode e = stack.pop();
//			Logger.debug("pop:" + e.toString());

			Set<Rule> tempSet = ruleMap.get(e.getConfiguration().getState());
			targetWord = e.getConfiguration().getWord();
			int count = 0;
			for(Rule rule : tempSet){
				if(proved){
					break;
				}
				count ++;
//				Logger.debug("rule to choose:\n"+ rule);
				String[] word = rule.getConfiguration().getWord();
				String[] ret = matchedWord(targetWord,word);
				
				level++;
				
				if(null != ret){
					//found the rule
					proved = true;
//					Logger.debug("choosed rule:\n" + rule);
					if(modelViewer.pn2TnMap != null){
						for(Entry<ProveNode, TreeNode> entry:modelViewer.pn2TnMap.entrySet()){
							if(entry.getValue().hitted){
								entry.getValue().highLight = true;
							}
							entry.getValue().hitted = false;
						}
						modelViewer.pn2TnMap.get(rule2PnModel.get(rule)).hitted = true;
					}
//					Logger.debug("ret String[]:" + Arrays.toString(ret));
					if(rule.getPremise() != null && rule.getPremise().size() != 0){
						for(Configuration prem : rule.getPremise()){
							Configuration tempCon = new Configuration();
							tempCon.setState(prem.getState());
							String[] tempWord = new String[prem.getWord().length - 1 + ret.length];
							for(int i = 0;i < prem.getWord().length - 1;i++){
								tempWord[i] = prem.getWord()[i];
							}
							for(int i = 0;i < ret.length;i++){
								tempWord[prem.getWord().length - 1 + i] = ret[i];
							}
							tempCon.setWord(tempWord);
							ProveNode tempPn = new ProveNode();
							tempPn.fromRule = rule;
							tempPn.setConfiguration(tempCon);
							e.getChildren().add(tempPn);
							tempPn.setPid(e.id);
							tempPn.parent = e;
							String provedNodeId = addProveNode(tempPn,way,e,level);
							if(null == provedNodeId) stack.push(tempPn);
							Con2PnHm.put(tempPn.ConfigurationString(),tempPn);
						}
					}else{
						ProveNode leaf = new ProveNode();
						leaf.setConfiguration(null);
						leaf.setPid(e.id);
						leaf.fromRule = rule;
						leaf.proved = true;
						e.getChildren().add(leaf);
						leaf.parent = e;
						updateProvedProperty(leaf);
//						System.out.println("add leaf:" + leaf.getId());
						addProveNode(leaf,way,e,level);
						updateProvedNodeMap(leaf,way);
					}
					try {
						if(Constants.pd.equals(way))
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{
					if(count == tempSet.size() && !proved ){
						//find prove tree failed 
						return null;
					}
				}
			}
		}

		if(Constants.pd.equals(way)){
			proofViewer.transferProveNode = new ProveNode();
		}else if(Constants.ori.equals(way)){
			oriProofViewer.transferProveNode = new ProveNode();
		}
		return retProveNode;
	}


	/**
	 * update node's proved property
	 * @param leaf update  bottom-up
	 */
	private static void updateProvedProperty(ProveNode leaf) {
		ProveNode cur = leaf.parent;
		while(cur != null){
			
			boolean proved = true;
			if(null != cur.getChildren() && cur.getChildren().size() != 0){
				for(ProveNode n : cur.getChildren()){
					proved &= n.proved;
					if(!proved) break;
				}
			}
			cur.proved = proved;
			if(!cur.proved) break; 
			cur = cur.parent;
		}
	}
	
	private static Set<ProveNode> provedNodeSet = new HashSet<ProveNode>();
	private static Set<ProveNode> oriProvedNodeSet = new HashSet<ProveNode>();
	
	/**
	 * update set not map,the set is a set of proved node,update set bottom-up
	 * @param leaf leaf node
	 * @param way pushdown proof
	 */
	private static void updateProvedNodeMap(ProveNode leaf,String way) {
		ProveNode cur = leaf;
		while(cur != null){
			if(cur.proved) {
				if(Constants.pd.equals(way)) provedNodeSet.add(cur);
				else if(Constants.ori.equals(way)) oriProvedNodeSet.add(cur);
				System.out.println("add to proved set:" + cur.getId() + cur.toString());
			}
			else break;
			cur = cur.parent;
		}
	}

	/**
	 * add ProveNode to TreeNode according to 'way'
	 * @param tempPn
	 * @param way
	 * @param e
	 * @param level
	 * @return node id which is proved before or null
	 */
	private static String addProveNode(ProveNode tempPn, String way,ProveNode e,int level) {
		String provedNodeId = provedBefore(tempPn,provedNodeSet,way);
		if(Constants.pd.equals(way)){
			addProveNodeSyn(tempPn,e,proofViewer,level,provedNodeId);
		}else if(Constants.ori.equals(way)){
			addProveNodeSyn(tempPn,e,oriProofViewer,level,provedNodeId);
		}
		return provedNodeId;
	}
	
	/**
	 * add ProveNode to TreeNode
	 * @param tempPn
	 * @param e
	 * @param proofViewer
	 * @param level
	 * @param provedNodeId
	 */
	public static void addProveNodeSyn(ProveNode tempPn,ProveNode e,TreeVisualizer proofViewer,int level,String provedNodeId){
		boolean b = true;
		while(b){
			if(proofViewer.transferProveNode == null){
				
				proofViewer.addNodeForPD(tempPn, false,false,level);
				if(provedNodeId == null){
					proofViewer.addEdge(e.getId() + "",tempPn.getId() + "");
				}else{
					proofViewer.addEdge(e.getId() + "",provedNodeId + "");
				}
				
				proofViewer.transferProveNode = tempPn;
				proofViewer.level = level;
				break;
			}
		}
	}

	/**
	 * 
	 * @param tempPn
	 * @param provedNodeSet
	 * @param way
	 * @return node id which is proved before
	 */
	private static String provedBefore(ProveNode tempPn,Set<ProveNode> provedNodeSet,String way) {
		String provedNodeId = null;
		Set<ProveNode> set = null;
		Map<ProveNode,TreeNode> pn2TnMap = null;
		if(Constants.pd.equals(way)) {
			set = provedNodeSet;
			pn2TnMap = proofViewer.pn2TnMap;
		}
		else if(Constants.ori.equals(way)){
			set = oriProvedNodeSet;
			pn2TnMap = oriProofViewer.pn2TnMap;
		}
		for(ProveNode pn : set){
			if(tempPn.getConfiguration() == null && pn.getConfiguration() == null){
				System.out.println("proved:#");
				provedNodeId = pn.getId() + "";
				pn.refered += 1;
				pn2TnMap.get(pn).referd = pn.refered;
				break;
			}else if(tempPn.getConfiguration() == null || pn.getConfiguration() == null){
				continue;
			}else if(tempPn.getConfiguration().toString().equals(pn.getConfiguration().toString())){
				System.out.println("proved:" + tempPn.getConfiguration().toString());
				provedNodeId = pn.getId() + "";
				pn.refered += 1;
				pn2TnMap.get(pn).referd = pn.refered;
				break;
			}
		}
		return provedNodeId;
	}
}
