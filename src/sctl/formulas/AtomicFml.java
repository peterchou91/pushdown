package sctl.formulas;

import java.util.ArrayList;

public class AtomicFml extends Formula {
	private String predicate;
	private ArrayList<String> states;

	public AtomicFml(String predicate, ArrayList<String> states) {
		this.predicate = predicate;
		this.states = states;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int states_size = states.size();
		for (int i = 0; i < states_size - 1; i++) {
			sb.append(states.get(i) + ",");
		}
		sb.append(states.get(states_size - 1));

		return predicate + "(" + sb.toString() + ")";
	}

	@Override
	public Formula nnf() {
		return this;
	}

	@Override
	public Formula neg() {
		return new NegFml(this);
	}

	@Override
	public boolean equals(Object o) {
		AtomicFml af = (AtomicFml) o;
		if (this.predicate.equals(af.predicate)) {
			int s = this.states.size();
			if (s == af.states.size()) {
				for (int i = 0; i < s; i++) {
					if (!this.states.get(i).equals(af.states.get(i))) {
						return false;
					}
				}
				return true;
			}
			return true;
		}
		return false;
	}

	@Override
	public Formula replaceVar(String v, String s) {
		ArrayList<String> sts = new ArrayList<String>();
		for(String s1: states) {
			if(v.equals(s1)) {
//				System.out.println("replacing "+v+" by "+s);
				sts.add(s);
			} else {
				sts.add(s1);
			}
		}
		return new AtomicFml(predicate, sts);
	}

}
