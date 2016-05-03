package pushdown.entity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pushdown.exceptions.EllegalFormException;
import pushdown.util.Constants;
import pushdown.util.Logger;


public class Rule implements Comparable {
	private static int count = 0;
	
	public Set<Rule> fromRules = new HashSet<Rule>();
	
	//1,2,3
	public int fromType = 0;
	

	int id = 0;
	{
		id = ++count;
	}
	private int form = 0;
	
	private Set<Configuration> premise = null;
	private Configuration configuration = null;
	public Set<Configuration> getPremise() {
		return premise;
	}
	public void setPremise(Set<Configuration> premise) {
		this.premise = premise;
	}
	public Configuration getConfiguration() {
		return configuration;
	}
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public Rule(Set<Configuration> premise,Configuration configuration){
		this.premise = premise;
		this.configuration = configuration;
	}
	public Rule(Configuration premise,Configuration configuration){
		Set<Configuration> l = new HashSet<Configuration>();
		l.add(premise);
		this.premise = l;
		this.configuration = configuration;
	}
	public Rule(){
		
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for ( Configuration c : premise ){
			sb.append(c).append(" ");
		}
		if(sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
		}
		
		sb.append("\n");
		int len1 = sb.length();
		for ( Configuration c : premise ){
			if(null != c){
				int len = 3;
				len += c.getState().length();
				for ( String symbol : c.getWord()){
					len += symbol.length();
				}
				++len;
				for(int i = 0 ; i < len ; i++){
					sb.append("-");
				}
			}
		}
		
		int len2 = sb.length();
		if (len1 == len2){
			sb = addUnderlineForConf(sb,configuration);
		}
		if(this.getForm() == Constants.ELIMINATION){
			sb.append("ELIMINATION");
		}else if (this.getForm() == Constants.INTRODUCTION){
			sb.append("INTRODUCTION");
		}else if (this.getForm() == Constants.NEUTRAL){
			sb.append("NEUTRAL");
		}else{
//			sb.append(this.getForm());
		}
		
		sb.append("\n");
		
		sb.append(configuration);
		return sb.toString();
	}
	
	public String toComplementationString(){
		StringBuilder sb = new StringBuilder();
		for ( Configuration c : premise ){
			sb.append(c.toComplementationString()).append(" ");
		}
		if(sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
		}
		
		sb.append("\n");
		int len1 = sb.length();
		for ( Configuration c : premise ){
			if(null != c){
				int len = 3;
				len += c.getState().length() + 1;
				for ( String symbol : c.getWord()){
					len += symbol.length();
				}
				++len;
				for(int i = 0 ; i < len ; i++){
					sb.append("-");
				}
			}
		}
		
		int len2 = sb.length();
		if (len1 == len2){
			sb = addUnderlineForConf(sb,configuration);
		}
		if(this.getForm() == Constants.ELIMINATION){
			sb.append("ELIMINATION");
		}else if (this.getForm() == Constants.INTRODUCTION){
			sb.append("INTRODUCTION");
		}else if (this.getForm() == Constants.NEUTRAL){
			sb.append("NEUTRAL");
		}else{
//			sb.append(this.getForm());
		}
		
		sb.append("\n");
		
		sb.append(configuration.toComplementationString());
		return sb.toString();
	}
	
	private StringBuilder addUnderlineForConf(StringBuilder sb,
			Configuration configuration2) {
		int len = 3;
		len += configuration.getState().length();
		for ( String symbol : configuration.getWord()){
			len += symbol.length();
		}
		++len;
		for(int i = 0 ; i < len ; i++){
			sb.append("-");
		}
		return sb;
	}
	public int getForm(){
		return this.form;
	}
	
	public void setForm(int form){
		try {
			if( form <0 || form > 3){
				throw new EllegalFormException("a rule can only be an introduction ,elimination or neutral");
			}else{
				this.form = form;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void toSmallStep() {
		if(this.getForm() == 0){
				
			int wordExInPre = 0;
			Configuration gama = null;
			for(Configuration c : premise){
				String[] word = c.getWord();
				Logger.debug("state:" + c.getState());
				Logger.debug("word[0]:" + word[0]);
				if(word[0].equals("x")){
					wordExInPre += 1;
				}else{
					gama = c;
				}
			}
			Logger.debug("wordExInPre=" + wordExInPre);
			Logger.debug("premise size:" + premise.size());
			Logger.debug("this.configuration.getWord().length" + this.configuration.getWord().length);
			for(String s : this.configuration.getWord()){
				System.out.println(s);
			}
			
			int wordExInCon = this.configuration.getWord()[0].equals("x") ? 1:0;
			
			if(((wordExInPre == this.premise.size() || this.getPremise().size() == 0) 
					&& this.configuration.getWord().length == 2) 
					|| (this.premise.size() == 0 && this.configuration.getWord()[0].equals("#"))){
				this.setForm(Constants.INTRODUCTION);
				
			}else if ((wordExInPre == this.premise.size() - 1 && gama.getWord().length == 2 )&& this.configuration.getWord()[0].equals("x") ){
				this.setForm(Constants.ELIMINATION);
			}else if ((wordExInPre == this.premise.size() || premise.size() == 0)&& this.configuration.getWord()[0].equals("x")){
				this.setForm(Constants.NEUTRAL);
			}else{
				Logger.debug("the rule can't be translated to small step");
				Logger.debug(this.toString());
				Logger.debug("");
			}
		}
		
	}
	@Override
	public int hashCode(){
		int result = 3;
		int f1 = 0;
		int f2 = 0;
		int f3 = 0;
		/*private int form = 0;
		
		private List<Configuration> premise = null;
		private Configuration configuration = null;*/
		f1 = form;
		if(null != premise){
			f2 = Arrays.hashCode(premise.toArray(new Configuration[premise.size()]));
		}
		
		if(null != configuration){
			f3 = configuration.hashCode();
		}
		
		result = 31 * result + f1;
		result = 31 * result + f2;
		result = 31 * result + f3;
		
		return result;
	}
	
	@Override
	public boolean equals(Object object){
		if(object != null && object instanceof Rule){
			Rule rule = (Rule)object;
			if(this.form == rule.getForm() && this.configuration.equals(rule.getConfiguration()) && this.premiseEqual(rule.getPremise())){
				return true;
			}
		}
		return false;
		
	}
	private boolean premiseEqual(Set<Configuration> premise2) {
		if(null == premise2 || this.getPremise().size() != premise2.size()){
			return false;
		}
		premise2.equals(premise2);
		HashSet<Configuration> premiseHs = new HashSet<Configuration>(this.getPremise());
		HashSet<Configuration> premise2Hs = new HashSet<Configuration>(premise2);
		if(!premiseHs.equals(premise2Hs)){
			return false;
		}
		return true;
	}
	@Override
	public int compareTo(Object o) {
		return this.toString().compareTo(o.toString());
	}
	
}
