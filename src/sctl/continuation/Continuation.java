package sctl.continuation;

import java.util.HashSet;
import sctl.formulas.Formula;

public class Continuation {
	private boolean basic;
	public HashSet<String> states;
	public Formula fml;
	public Continuation contl;
	public Continuation contr;
	public int index;
	public HashSet<ProofPair> truePairs;
	public HashSet<ProofPair> falsePairs;

	public Continuation(Boolean basic, HashSet<String> states, Formula fml,
			Continuation contl, Continuation contr, int index,
			HashSet<ProofPair> truePairs, HashSet<ProofPair> falsePairs) {
		this.basic = basic;
		this.states = states;
		this.fml = fml;
		this.contl = contl;
		this.contr = contr;
		this.index = index;
		this.truePairs = truePairs;
		this.falsePairs = falsePairs;
	}

	public boolean isBasic() {
		return basic;
	}
	
	public void addTruePair(ProofPair tp) {
		this.truePairs.add(tp);
	}
	
	public void addFalsePair(ProofPair fp) {
		this.falsePairs.add(fp);
	}
	
	public HashSet<ProofPair> getTruePairs() {
		return this.truePairs;
	}
	
	public HashSet<ProofPair> getFalsePairs() {
		return this.falsePairs;
	}
	
	public Continuation newContinuation() {
		return new Continuation(basic, states, fml, contl, contr, index, (HashSet<ProofPair>)truePairs.clone(), (HashSet<ProofPair>)falsePairs.clone());
	}
		

	public Continuation newTruePairs(HashSet<ProofPair> tps) {
		return new Continuation(basic, states, fml, contl, contr, index, tps, falsePairs);
	}
	
	public Continuation newFalsePairs(HashSet<ProofPair> fps) {
		return new Continuation(basic, states, fml, contl, contr, index, truePairs, fps);
	}
//	
//	public Continuation addTruePair(ProofPair tp) {
//		ProofPair pp = tp;
//		while(pp != null) {
//			tp = new ProofPair(pp.parent(), pp.child(), tp);
//			pp = pp.nextPair();
//		}
////		this.truePair = tp;
//		return new Continuation(basic, states, fml, contl, contr, index, tp, falsePair);
//		
//	}
//	public Continuation addFalsePair(ProofPair fp) {
//		ProofPair pp = fp;
//		while(pp != null) {
//			fp = new ProofPair(pp.parent(), pp.child(), fp);
//			pp = pp.nextPair();
//		}
////		this.truePair = fp;
//		return new Continuation(basic, states, fml, contl, contr, index, truePair, fp);
//	}

//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("truePairs:\n");
//		ProofPair pp = this.truePair;
//		while(pp != null) {
//			sb.append(pp.parent()+"->"+pp.child()+"\n");
//			pp = pp.nextPair();
//		}
//		sb.append("falsePairs:\n");
//		pp = this.falsePair;
//		while(pp != null) {
//			sb.append(pp.parent()+"->"+pp.child()+"\n");
//			pp = pp.nextPair();
//		}
//		return sb.toString();
//	}
	
}
