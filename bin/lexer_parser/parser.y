%{
	package lexer_parser;
	import java.io.*;
	import java.util.*;
	import sctl.*;
	import sctl.formulas.*;

%}

%token <sval> IDEN
%token NEWLINE WHITESPACE
%token STATES ATOMS TRANSITIONS SPECS TOP BOTTOM AX EX AF EG AR EU NEG
%token LB1 RB1 LB3 RB3 COLON SEMICOLON COMMA AND OR ARROW
%type <obj>	idens
%type <obj> fml
%type <obj> idens_set
/*
%type <obj> paras
%type <obj> exp

%type <obj> ini_module_paras

%type <obj> state_restrictions
*/

%%
input: states atoms transitions specs	{System.out.println("Input file read complete.");}
;

states: STATES COLON state_lst SEMICOLON	{System.out.println("States read!");}
;

state_lst: IDEN	{m.states.add($1);}
	| state_lst COMMA IDEN	{m.states.add($3);}
	;
	
atoms: ATOMS COLON atom_lst	{System.out.println("Atomics read!");}
;

atom_lst: IDEN LB1 idens RB1 SEMICOLON	{m.atoms.add(new AtomicFml($1, (ArrayList<String>)$3));}
	| atom_lst IDEN LB1 idens RB1 SEMICOLON	{m.atoms.add(new AtomicFml($2, (ArrayList<String>)$4));}
	;
	
idens: IDEN	{ArrayList<String> al = new ArrayList<String>(); al.add($1); $$ = al;}
	| idens COMMA IDEN	{ArrayList<String> al = (ArrayList<String>)$1; $$ = al;}
	;

transitions: TRANSITIONS COLON trans	{System.out.println("Transitions read!");}
;

trans: IDEN ARROW LB3 idens_set RB3 SEMICOLON	{m.transitions.put($1, (HashSet<String>)$4);}
	| trans IDEN ARROW LB3 idens_set RB3 SEMICOLON	{m.transitions.put($2, (HashSet<String>)$5);}
	;
	
idens_set: IDEN	{HashSet<String> hs = new HashSet<String>(); hs.add($1); $$ = hs;}
	| idens_set COMMA IDEN	{HashSet<String> hs = (HashSet<String>)$1; hs.add($3); $$ = hs;}
	;

specs: SPECS COLON fmls	{System.out.println("Specs read!");}
;

fmls: fml SEMICOLON	{m.specs.add((Formula)$1);}
	| fmls fml SEMICOLON	{m.specs.add((Formula)$2);}
	;

fml: TOP	{$$ = new TopFml();}
	| BOTTOM	{$$ = new BottomFml();}
	| IDEN LB1 idens RB1	{$$ = new AtomicFml($1, (ArrayList<String>)$3);}
	| NEG fml	{$$ = new NegFml((Formula)$2);}
	| fml AND fml	{$$ = new AndFml((Formula)$1,(Formula)$3);}
	| fml OR fml	{$$ = new OrFml((Formula)$1,(Formula)$3);}
	| AX LB1 IDEN COMMA fml COMMA IDEN RB1	{$$ = new AxFml($3, (Formula)$5, ($7));}
	| EX LB1 IDEN COMMA fml COMMA IDEN RB1	{$$ = new ExFml($3, (Formula)$5, ($7));}
	| AF LB1 IDEN COMMA fml COMMA IDEN RB1	{$$ = new AfFml($3, (Formula)$5, ($7));}
	| EG LB1 IDEN COMMA fml COMMA IDEN RB1	{$$ = new EgFml($3, (Formula)$5, ($7));}
	| AR LB1 IDEN COMMA IDEN COMMA fml COMMA fml COMMA IDEN RB1	{$$ = new ArFml($3, $5, (Formula)$7, (Formula)$9,($11));}
	| EU LB1 IDEN COMMA IDEN COMMA fml COMMA fml COMMA IDEN RB1	{$$ = new EuFml($3, $5, (Formula)$7, (Formula)$9,($11));}
	| LB1 fml RB1	{$$ = $2;}
	;

/*
以下是旧代码。


input: 	{}
	| input MODEL IDEN LB1 paras RB1 LB3 var_decl ini_decl transition_decl atomic_decl spec_decl RB3	
			{am.name = $3; 
			 am.args = (ArrayList<VarTypeTuple>)$5;
			 }
	;
	
paras: 	{}
	| INT IDEN				{ArrayList<VarTypeTuple> ls = new ArrayList<VarTypeTuple>(); ls.add(new VarTypeTuple($2, "Int")); $$ = ls;}
	| BOOL IDEN				{ArrayList<VarTypeTuple> ls = new ArrayList<VarTypeTuple>(); ls.add(new VarTypeTuple($2, "Bool")); $$ = ls;}
	| paras COMMA INT IDEN	{ArrayList<VarTypeTuple> ls = (ArrayList<VarTypeTuple>)$1; ls.add(new VarTypeTuple($4, "Int")); $$ = ls;}
	| paras COMMA BOOL IDEN	{ArrayList<VarTypeTuple> ls = (ArrayList<VarTypeTuple>)$1; ls.add(new VarTypeTuple($4, "Bool")); $$ = ls;}
	;

exp:  NUMBER	{$$ = new NumValue($1);}
	| TRUE	{$$ = new BoolValue(true);}
	| FALSE	{$$ = new BoolValue(false);}
	| IDEN	{$$ = new VarExpr($1);}
	| IDEN LB2 exp RB2	{$$ = new VarExpr($1, (Expression)$3);}
	| IDEN LB1 exp RB1	{$$ = new StateVarExpr(new StateVar($1), (Expression)$3);}
	| exp MOD exp	{$$ = new ModExpr((Expression)$1, (Expression)$3);}
	| SCALAR IDEN	{$$ = new ScalarValue($2);}
	| exp EQUAL exp	{$$ = new EqExpr((Expression)$1, (Expression)$3);}
	| exp NONEQUAL exp	{$$ = new NegExpr(new EqExpr((Expression)$1, (Expression)$3));}
	| exp PLUS exp	{$$ = new AddExpr((Expression)$1, (Expression)$3);}
	| exp MINUS exp	{$$ = new MinusExpr((Expression)$1, (Expression)$3);}
	| exp MUL exp	{$$ = new MulExpr((Expression)$1, (Expression)$3);}
	| NEGO exp	{$$ = new NegExpr((Expression)$2);}
	| exp ANDO exp	{$$ = new AndExpr((Expression)$1, (Expression)$3);}
	| exp ORO exp	{$$ = new OrExpr((Expression)$1, (Expression)$3);}
	| LB1 exp RB1	{$$ = $2;}
	;
	
//non_det_elems: exp	{HashSet<Expression> hs = new HashSet<Expression>(); hs.add((Expression)$1);$$ = hs;}
//	| non_det_elems COMMA exp	{HashSet<Expression> hs = (HashSet<Expression>)$1; hs.add((Expression)$3); $$ = hs;}
//	;
	
//case_exprs: exp COLON exp SEMICOLON	{LinkedList<ExpPair> ll = new LinkedList<ExpPair>(); ll.addLast(new ExpPair((Expression)$1, (Expression)$3));$$ = ll;}
//	| case_exprs exp COLON exp SEMICOLON	{LinkedList<ExpPair> ll = (LinkedList<ExpPair>)$1; ll.addLast(new ExpPair((Expression)$2, (Expression)$4)); $$ = ll;}
//	;

var_decl: 	{}
	| VAR LB3 vars RB3	{System.out.println("var decl done!!!");}
	;

vars: 	{}
	| IDEN COLON INT SEMICOLON vars	{System.out.println($1+" is of type Number!");}
	| IDEN COLON BOOL SEMICOLON vars	{System.out.println($1+" is of type Bool!");}
	| IDEN COLON LB3 scalars RB3 SEMICOLON vars	{System.out.println($1+" is of type Scalar!");}
	| IDEN COLON ARRAY LB1 exp RB1 OF LB3 exp DOTDOT exp RB3 SEMICOLON vars	{System.out.println($1+" is of type array");}
	| IDEN COLON ARRAY LB1 exp RB1 OF BOOL SEMICOLON vars	{System.out.println($1+" is of type array");}
	| IDEN COLON ARRAY LB1 exp RB1 OF LB3 scalars RB3 SEMICOLON vars	{System.out.println($1+" is of type array");}
	;
	
scalars: SCALAR IDEN	{}
	| scalars COMMA SCALAR IDEN	{}
	;
	
//ini declaration
ini_decl: 	{}
	| INIT LB3 inis RB3	{System.out.println("ini decl done!!!");}
	;
	
inis: 	{}
	| inis exp ASSIGNO exp SEMICOLON	{am.inis.put((Expression)$2, (Expression)$4);}
	| inis FOREACH LB1 IDEN IN exp DOTDOT exp RB1 LB3 inis_foreach RB3 SEMICOLON {fi.var = $4; fi.lrange = (Expression)$6; fi.rrange = (Expression)$8; am.absInis.add(fi); fi = new ForeachInit();}
	;
	
inis_foreach: {}
	| IDEN LB2 exp RB2 ASSIGNO exp SEMICOLON inis_foreach	{fi.inis.add(new TupleExpr(new VarExpr($1, (Expression)$3), (Expression)$6));}
	;
	
ini_module_paras: 	{$$ = new ArrayList<Expression>();}
	| exp	{ArrayList<Expression> ls = new ArrayList<Expression>(); ls.add((Expression)$1); $$ = ls;}
	| ini_module_paras COMMA exp	{ArrayList<Expression> ls = (ArrayList<Expression>)$1; ls.add((Expression)$3); $$ = ls;}
	;
	
transition_decl: 	{}
	| TRANSITION LB3 trans RB3	{System.out.println("transition decl done!!!");}
	;
	
trans:	{} 
	| trans IDEN ARROW IDEN LB3 state_restrictions RB3 SEMICOLON	{am.trans.add(new TransTuple(new BoolValue(true), (ArrayList<TupleExpr>)$6));}
	| trans IDEN ARROW exp COLON IDEN LB3 state_restrictions RB3 SEMICOLON	{am.trans.add(new TransTuple((Expression)$4, (ArrayList<TupleExpr>)$8));}
	| trans IDEN ARROW CASE trans_pairs END SEMICOLON	{}
	| trans FOREACH LB1 IDEN IN exp DOTDOT exp RB1 LB3 abs_trans RB3 SEMICOLON	{ft.var = $4; ft. lrange = (Expression)$6; ft.rrange = (Expression)$8; am.absTrans.add(ft); ft = new ForeachTrans();}
	;
	
abs_trans:	{}
	| abs_trans IDEN ARROW IDEN LB3 state_restrictions RB3 SEMICOLON	{ft.absTrans.add(new TransTuple(new BoolValue(true), (ArrayList<TupleExpr>)$6));}
	| abs_trans IDEN ARROW exp COLON IDEN LB3 state_restrictions RB3 SEMICOLON	{ft.absTrans.add(new TransTuple((Expression)$4, (ArrayList<TupleExpr>)$8));}
	| abs_trans IDEN ARROW CASE abs_trans_pairs END SEMICOLON	{} 
	;
	
abs_trans_pairs:	{}
	| abs_trans_pairs exp COLON IDEN LB3 state_restrictions RB3 SEMICOLON	{ft.absTrans.add(new TransTuple((Expression)$2, (ArrayList<TupleExpr>)$6));}
	| abs_trans_pairs CASEO exp COLON IDEN LB3 state_restrictions RB3 SEMICOLON	{ft.absTrans.add(new TransTuple((Expression)$3, (ArrayList<TupleExpr>)$7));}
	;
	
trans_pairs:  exp COLON IDEN LB3 state_restrictions RB3 SEMICOLON	{am.trans.add(new TransTuple((Expression)$1, (ArrayList<TupleExpr>)$5));}
	| trans_pairs CASEO exp COLON IDEN LB3 state_restrictions RB3 SEMICOLON	{am.trans.add(new TransTuple((Expression)$3, (ArrayList<TupleExpr>)$7));}
	;
	
state_restrictions: {$$ = new ArrayList<TupleExpr>();}
	| state_restrictions exp ASSIGNO exp SEMICOLON	{ArrayList<TupleExpr> ls = (ArrayList<TupleExpr>)$1; ls.add(new TupleExpr((Expression)$2, (Expression)$4)); $$ = ls;}
	;
	
atomic_decl: 	{}
	| ATOMIC LB3 atomics RB3	{System.out.println("atomic decl done!!!");}
	;
	
atomics: 	{}
	| atomics exp LB1 idens RB1 ASSIGNO IF exp THEN TOP ELSE BOTTOM SEMICOLON	{am.atomics.put((Expression)$2, new AtomicTuple((ArrayList<StateVar>)$4, (Expression)$8));}
	| atomics FOREACH LB1 IDEN IN exp DOTDOT exp RB1 LB3 abs_atomics RB3 SEMICOLON	{fa.var = $4; fa.lrange = (Expression)$6; fa.rrange = (Expression)$8; am.absAtomics.add(fa); fa = new ForeachAtomic();}
	;
	
abs_atomics: exp LB1 idens RB1 ASSIGNO IF exp THEN TOP ELSE BOTTOM SEMICOLON 	{fa.absAtomics.put((Expression)$1, new AtomicTuple((ArrayList<StateVar>)$3, (Expression)$7));}
	| abs_atomics exp LB1 idens RB1 ASSIGNO IF exp THEN TOP ELSE BOTTOM SEMICOLON	{fa.absAtomics.put((Expression)$2, new AtomicTuple((ArrayList<StateVar>)$4, (Expression)$8));}
	; 
	
fml: TOP	{$$ = new TopFml();}
	| BOTTOM	{$$ = new BottomFml();}
	| IDEN LB1 idens RB1	{$$ = new AtomicFml($1, null, (ArrayList<AbstractState>)$3);}
	| IDEN LB2 exp RB2 LB1 idens RB1	{$$ = new AtomicFml($1, (Expression)$3, (ArrayList<AbstractState>)$6);}
	| NEG fml	{$$ = new NegFml((Formula)$2);}
	| fml AND fml	{$$ = new AndFml((Formula)$1,(Formula)$3);}
	| fml OR fml	{$$ = new OrFml((Formula)$1,(Formula)$3);}
	| AX LB1 IDEN COMMA fml COMMA IDEN RB1	{$$ = new AxFml($3, (Formula)$5, new StateVar($7));}
	| EX LB1 IDEN COMMA fml COMMA IDEN RB1	{$$ = new ExFml($3, (Formula)$5, new StateVar($7));}
	| AF LB1 IDEN COMMA fml COMMA IDEN RB1	{$$ = new AfFml($3, (Formula)$5, new StateVar($7));}
	| EG LB1 IDEN COMMA fml COMMA IDEN RB1	{$$ = new EgFml($3, (Formula)$5, new StateVar($7));}
	| AR LB1 IDEN COMMA IDEN COMMA fml COMMA fml COMMA IDEN RB1	{$$ = new ArFml($3, $5, (Formula)$7, (Formula)$9,new StateVar($11));}
	| EU LB1 IDEN COMMA IDEN COMMA fml COMMA fml COMMA IDEN RB1	{$$ = new EuFml($3, $5, (Formula)$7, (Formula)$9,new StateVar($11));}
	| FORALL IDEN IN exp DOTDOT exp COMMA fml	{$$ = new ForallFml($2, (Expression)$4, (Expression)$6, (Formula)$8);}
	| EXISTS IDEN IN exp DOTDOT exp COMMA fml	{$$ = new ExistsFml($2, (Expression)$4, (Expression)$6, (Formula)$8);}
	| LB1 fml RB1	{$$ = $2;}
	;
	
idens: IDEN			{ArrayList<StateVar> ls = new ArrayList<StateVar>(); ls.add(new StateVar($1)); $$ = ls;}
	| idens IDEN	{ArrayList<StateVar> ls = (ArrayList<StateVar>)$1; ls.add(new StateVar($2)); $$ = ls;}
	;
	
spec_decl: 	{}
	| SPEC LB3 specs RB3	{System.out.println("spec decl done!!!");}
	;
	
//specs: IDEN ASSIGNO fml SEMICOLON	{am.specs.put($1, (Formula)$3);}
//	| specs IDEN ASSIGNO fml SEMICOLON	{am.specs.put($2, (Formula)$4);}
//	;
specs: 	{}
	| IDEN ASSIGNO fml SEMICOLON specs	{am.specs.put($1, (Formula)$3);}
	;
*/
%%
	public Model m = new Model();

  private Yylex lexer;

  private int yylex () {
    int yyl_return = -1;
    try {
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      System.err.println("IO error :"+e);
    }
    return yyl_return;
  }

  public void yyerror (String error) {
    System.err.println ("Error: " + error +" --line "+lexer.line_no);
  }
  
  public void parse() {
  	this.yyparse();
  }

  public Parser(Reader r) {
    lexer = new Yylex(r, this);
  }
  
  public void printList(ArrayList<String> str) {
		for (String s : str) {
			System.out.print(s+" ");
		}
		System.out.println();
	}

 