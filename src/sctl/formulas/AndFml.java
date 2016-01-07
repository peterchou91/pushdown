package sctl.formulas;


public class AndFml extends Formula {
	public Formula fml1;
	public Formula fml2;
	
	public AndFml(Formula fml1, Formula fml2) {
		this.fml1 = fml1;
		this.fml2 = fml2;
	}

	@Override
	public String toString() {
		return fml1.toString()+" /\\ "+fml2.toString();
	}

	@Override
	public Formula nnf() {
		return new AndFml(fml1.nnf(), fml2.nnf());
	}

	@Override
	public Formula neg() {
		return new OrFml(fml1.neg(), fml2.neg());
	}

	@Override
	public Formula replaceVar(String v, String s) {
		return new AndFml(fml1.replaceVar(v, s), fml2.replaceVar(v, s));
	}
}
