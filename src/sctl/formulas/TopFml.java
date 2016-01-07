package sctl.formulas;


public class TopFml extends Formula {


	@Override
	public String toString() {
		return "TRUE";
	}
	
	@Override
	public Formula nnf() {
		return this;
	}

	@Override
	public Formula neg() {
		return new BottomFml();
	}

	@Override
	public Formula replaceVar(String v, String s) {
		return this;
	}
}
