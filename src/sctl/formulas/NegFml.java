package sctl.formulas;


public class NegFml extends Formula {
	public Formula fml;
	public NegFml(Formula fml) {
		this.fml = fml;
	}
	
	@Override
	public String toString() {
		return "not ("+fml+")";
	}

	@Override
	public Formula nnf() {
		return fml.nnf().neg();
	}
	@Override
	public Formula neg() {
		return fml;
	}

	@Override
	public Formula replaceVar(String v, String s) {
		return new NegFml(fml.replaceVar(v, s));
	}
}
