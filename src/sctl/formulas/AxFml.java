package sctl.formulas;


public class AxFml extends Formula {
	public String x;
	public Formula fml;
	public String state;
	
	public AxFml(String x, Formula fml, String state) {
		this.x = x;
		this.fml = fml;
		this.state = state;
	}

	@Override
	public String toString() {
		return "AX("+x+","+fml+","+state+")";
	}

	@Override
	public Formula nnf() {
		return new AxFml(x, fml.nnf(), state);
	}

	@Override
	public Formula neg() {
		return new ExFml(x, fml.neg(), state);
	}

	@Override
	public Formula replaceVar(String v, String s) {
		if(v.equals(this.state)) {
			return new AxFml(x, fml.replaceVar(v, s), s);
		}
		return this;
	}
}
