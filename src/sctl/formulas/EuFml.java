package sctl.formulas;

public class EuFml extends Formula {
	public String x, y;
	public Formula fml1, fml2;
	public String state;

	public EuFml(String x, String y, Formula fml1, Formula fml2, String state) {
		this.x = x;
		this.y = y;
		this.fml1 = fml1;
		this.fml2 = fml2;
		this.state = state;
	}

	@Override
	public String toString() {
		return "EU("+x+","+y+","+fml1+","+fml2+","+state+")";
	}

	@Override
	public Formula nnf() {
		return new EuFml(x, y, fml1.nnf(), fml2.nnf(), state);
	}

	@Override
	public Formula neg() {
		return new ArFml(x, y, fml1.neg(), fml2.neg(), state);
	}

	@Override
	public Formula replaceVar(String v, String s) {
		if(v.equals(state)) {
			return new EuFml(x,y,fml1.replaceVar(v, s), fml2.replaceVar(v, s), s);
		}
		return this;
	}

}
