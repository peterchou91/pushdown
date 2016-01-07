package sctl.formulas;


public class AfFml extends Formula {
	public String x;
	public Formula fml;
	public String state;
	
	public AfFml(String x, Formula fml, String state) {
		this.x = x;
		this.fml = fml;
		this.state = state;
	}

	@Override
	public String toString() {
		return "AF("+x+","+fml+","+state+")";
	}

	@Override
	public Formula nnf() {
		return new AfFml(x, fml.nnf(), state);
	}

	@Override
	public Formula neg() {
		return new EgFml(x, fml.neg(), state);
	}

	@Override
	public Formula replaceVar(String v, String s) {
		if(v.equals(state)) {
			return new AfFml(x,fml.replaceVar(v, s),s);
		}
		return this;
		
	}

}
