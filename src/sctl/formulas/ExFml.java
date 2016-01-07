package sctl.formulas;


public class ExFml extends Formula {
	public String x;
	public Formula fml;
	public String state;
	
	public ExFml(String x, Formula fml, String state) {
		this.x = x;
		this.fml = fml;
		this.state = state;
	}

	@Override
	public String toString() {
		return "EX("+x+","+fml+","+state+")";
	}

	@Override
	public Formula nnf() {
		return new ExFml(x, fml.nnf(), state);
	}

	@Override
	public Formula neg() {
		return new AxFml(x, fml.neg(), state);
	}

	@Override
	public Formula replaceVar(String v, String s) {
		if(v.equals(state)) {
			return new ExFml(x,fml.replaceVar(v, s),s);
		}
		return this;
	}
}
