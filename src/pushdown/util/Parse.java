package pushdown.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import pushdown.entity.Configuration;
import pushdown.entity.Rule;


public class Parse {
    //输入文件
	private static String filename = "list";//river.input1 testExample.input river.input1
	private static String input = "input/" + filename;
	
	
	private static BufferedReader BR = null;
	public static Configuration c = null;
	private static Set<Rule> rules = new HashSet<Rule>();
	private static Set<Configuration> configurations = new HashSet<Configuration>();
	private static Set<String> symbols = new HashSet<String>();
	
	public static String getFilename(){
		return filename;
	}
	
	public static Set<String> getSymbols() {
		return symbols;
	}

	public static void setSymbols(Set<String> symbols) {
		Parse.symbols = symbols;
	}

	public static Set<Rule> getRules() {
		return rules;
	}

	public static void setRules(Set<Rule> rules) {
		Parse.rules = rules;
	}

	public static Set<Configuration> getConfigurations() {
		return configurations;
	}

	public static void setConfigurations(Set<Configuration> configurations) {
		Parse.configurations = configurations;
	}

	
	public static void init(){
		init(input);
	}
	
	/**
	 * 打开输入文件
	 * @param input
	 */
	public static void init(String input){
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(new File(input)));
			BR = br;
			readRules();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			close();
		}
	}
	
	private static void close() {
		try {
			BR.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 从打开的BufferedReader中读取规则，并将规则rule存入rules
	 * @return 读取的所有规则
	 */
	public static void readRules(){
		
		try {
			if(null == BR){
				throw new Exception("调用readRules方法前,请先调用init方法");
			}
			String line = null;
			while (null != ( line = BR.readLine() ) ) {
				Logger.debug(line);
				if(line.equals("")){
					c = pareConfiguration(BR.readLine());
				}
				Rule rule = parseRlue(line);
				rules.add(rule);
				Logger.debug("parse rules:" +rules.size() + "\n" + rule.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			close();
		}
	}
	/**
	 * 将rule字符串解析成rule,并将其中的configuration存入configurations
	 * @param line rule字符串
	 * @return Rule
	 */
	private static Rule parseRlue(String line) {
        //p1(a1,a4) p2(a2,a3) -> p'1(a2,a3)
		Rule rule = new Rule();
		
		String[] confugrationStrs = line.split("->");
		
		String premiseStr = confugrationStrs[0];
		String configurationStr = confugrationStrs[1];
		
		Set<Configuration> premise = parsePremise(premiseStr);
		Configuration configuration = pareConfiguration(configurationStr);
		
	
		
		rule.setConfiguration(configuration);
		rule.setPremise(premise);
		
		return rule;
	}
	
	/**
	 * 将Configuration字符串解析成Configuration
	 * @param line Configuration字符串
	 * @return Configuration
	 */
	private static Configuration pareConfiguration(String configurationStr) {
		// p'1(a2,a3)
		Configuration c = null;
		
		//去掉configuration字符串中的空格
		configurationStr = configurationStr.trim();
		Logger.debug("configurationStr:" + configurationStr);
		if (!configurationStr.equals("")){
			c = new Configuration();
			String[] tempStrs = configurationStr.split("\\)");
			
			// tempStrs[0]:p'1(a2,a3
			String[] temp = tempStrs[0].split("\\(");
			//temp[0]:p'1( ; temp[1]:a2,a3
			String state = temp[0];
			String[] word = null;
			if( temp.length == 1){
				//p()
				word = new String[1];
				word[0] = "";
			}else{

				if(temp[1].trim().equalsIgnoreCase("#")){
					word = new String[]{"#"};
				}else{
//					word = ((tempStrs[0] + ",x").split("\\(")[1]).split(",");
					if(!temp[1].contains("x")){
						word = (temp[1] + ",x").split(",");
					}else{
						word = temp[1].split(",");
					}
				}
				for(int i = 0; i < word.length - 1;i++){
					symbols.add(word[i]);
				}
			}
			
			c.setState(state);
			c.setWord(word);
			Logger.debug("" + c);
		}else{
			c = null;
		}
		//加入configurations中
		if(c == null){
			if(!configurations.contains(null))
				configurations.add(c);
		}else{
			configurations.add(c);
		}
		
		return c;
	}
	
	/**
	 * 将premise字符串解析成premise,如果premise字符串为空,则premise大小为0
	 * @param line premise字符串
	 * @return premise
	 */
	private static Set<Configuration> parsePremise(String premiseStr) {
		// p1(a1,a4) & p2(a2,a3)
		Set<Configuration> premise = new HashSet<Configuration>();
		for(String temp : premiseStr.split("&")){
			Configuration c = pareConfiguration(temp);
			if ( null != c){
				premise.add(c);
		    }
		}
		return premise;
	}

}
