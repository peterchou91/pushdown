package sctl.formulas;

public class BottomFml extends Formula {

	@Override
	public String toString() {
		return "FALSE";
	}

	@Override
	public Formula nnf() {
		return this;
	}

	@Override
	public Formula neg() {
		return new TopFml();
	}

	@Override
	public Formula replaceVar(String v, String s) {
		return this;
	}
}
