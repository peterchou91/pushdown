//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "parser.y"
	package lexer_parser;
	import java.io.*;
	import java.util.*;
	import sctl.*;
	import sctl.formulas.*;

//#line 24 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short IDEN=257;
public final static short NEWLINE=258;
public final static short WHITESPACE=259;
public final static short STATES=260;
public final static short ATOMS=261;
public final static short TRANSITIONS=262;
public final static short SPECS=263;
public final static short TOP=264;
public final static short BOTTOM=265;
public final static short AX=266;
public final static short EX=267;
public final static short AF=268;
public final static short EG=269;
public final static short AR=270;
public final static short EU=271;
public final static short NEG=272;
public final static short LB1=273;
public final static short RB1=274;
public final static short LB3=275;
public final static short RB3=276;
public final static short COLON=277;
public final static short SEMICOLON=278;
public final static short COMMA=279;
public final static short AND=280;
public final static short OR=281;
public final static short ARROW=282;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    4,    8,    8,    5,    9,    9,    1,    1,    6,
   10,   10,    3,    3,    7,   11,   11,    2,    2,    2,
    2,    2,    2,    2,    2,    2,    2,    2,    2,    2,
};
final static short yylen[] = {                            2,
    4,    4,    1,    3,    3,    5,    6,    1,    3,    3,
    6,    7,    1,    3,    3,    2,    3,    1,    1,    4,
    2,    3,    3,    8,    8,    8,    8,   12,   12,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    3,    0,    0,    0,
    0,    2,    0,    0,    0,    0,    0,    1,    4,    0,
    0,    0,    0,    0,    8,    0,    0,    0,    0,    0,
   18,   19,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   16,    0,    0,    0,
    6,    9,    0,   13,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   30,    0,    0,   17,    7,    0,    0,
    0,   20,    0,    0,    0,    0,    0,    0,   11,   14,
    0,    0,    0,    0,    0,    0,    0,   12,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   24,   25,   26,   27,    0,    0,    0,    0,    0,    0,
    0,    0,   28,   29,
};
final static short yydgoto[] = {                          2,
   26,   41,   65,    3,    6,   11,   18,    8,   15,   23,
   42,
};
final static short yysindex[] = {                      -238,
 -243,    0, -225, -208, -214, -187,    0, -194, -171, -188,
 -173,    0, -166, -181, -164, -163, -182,    0,    0, -161,
 -176, -184, -158, -240,    0, -265, -161, -175, -180, -172,
    0,    0, -170, -169, -168, -167, -165, -162, -240, -240,
 -230, -240, -160, -150, -258, -148, -159, -161, -147, -145,
 -144, -143, -142, -140, -193, -270,    0, -240, -240, -226,
    0,    0, -157,    0, -261, -148, -236, -156, -155, -154,
 -153, -152, -151,    0, -193, -193,    0,    0, -149, -138,
 -223,    0, -240, -240, -240, -240, -137, -135,    0,    0,
 -146, -274, -222, -219, -213, -141, -139,    0, -127, -126,
 -124, -123, -240, -240, -133, -132, -131, -130, -210, -207,
    0,    0,    0,    0, -240, -240, -201, -198, -122, -121,
 -129, -128,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, -125,    0,    0,    0,    0,    0,
    0,    0, -116,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  139,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, -266,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, -239, -237,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  -25,  -39,   82,    0,    0,    0,    0,    0,    0,    0,
    0,
};
final static int YYTABLESIZE=148;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         55,
   56,   45,   60,   74,   99,   58,   59,   21,   43,   58,
   59,   21,   21,   44,   79,   63,   30,   80,   75,   76,
   44,    1,   67,   31,   32,   33,   34,   35,   36,   37,
   38,   39,   40,    4,   22,    5,   23,   82,   22,   22,
   23,   23,   44,   92,   93,   94,   95,   57,    7,   58,
   59,   77,   91,   58,   59,   80,  100,   58,   59,  101,
   58,   59,    9,  109,  110,  102,   58,   59,  115,   58,
   59,  116,   58,   59,   10,  117,  118,  119,   58,   59,
  120,   58,   59,   12,   13,   14,   58,   59,   16,   17,
   19,   20,   21,   22,   24,   25,   27,   28,   29,   46,
   48,   47,   49,   50,   51,   52,   62,   53,   64,   68,
   54,   69,   70,   71,   72,   66,   73,   61,   90,   96,
   78,   97,   83,   84,   85,   86,   87,   88,   89,  105,
  106,   98,  107,  108,  121,  122,    5,  103,   15,  104,
  111,  112,  113,  114,  123,  124,   10,   81,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         39,
   40,   27,   42,  274,  279,  280,  281,  274,  274,  280,
  281,  278,  279,  279,  276,  274,  257,  279,   58,   59,
  279,  260,   48,  264,  265,  266,  267,  268,  269,  270,
  271,  272,  273,  277,  274,  261,  274,  274,  278,  279,
  278,  279,  279,   83,   84,   85,   86,  278,  257,  280,
  281,  278,  276,  280,  281,  279,  279,  280,  281,  279,
  280,  281,  277,  103,  104,  279,  280,  281,  279,  280,
  281,  279,  280,  281,  262,  115,  116,  279,  280,  281,
  279,  280,  281,  278,  279,  257,  280,  281,  277,  263,
  257,  273,  257,  257,  277,  257,  273,  282,  257,  275,
  273,  282,  273,  273,  273,  273,  257,  273,  257,  257,
  273,  257,  257,  257,  257,  275,  257,  278,  257,  257,
  278,  257,  279,  279,  279,  279,  279,  279,  278,  257,
  257,  278,  257,  257,  257,  257,  262,  279,    0,  279,
  274,  274,  274,  274,  274,  274,  263,   66,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=282;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"IDEN","NEWLINE","WHITESPACE","STATES","ATOMS","TRANSITIONS",
"SPECS","TOP","BOTTOM","AX","EX","AF","EG","AR","EU","NEG","LB1","RB1","LB3",
"RB3","COLON","SEMICOLON","COMMA","AND","OR","ARROW",
};
final static String yyrule[] = {
"$accept : input",
"input : states atoms transitions specs",
"states : STATES COLON state_lst SEMICOLON",
"state_lst : IDEN",
"state_lst : state_lst COMMA IDEN",
"atoms : ATOMS COLON atom_lst",
"atom_lst : IDEN LB1 idens RB1 SEMICOLON",
"atom_lst : atom_lst IDEN LB1 idens RB1 SEMICOLON",
"idens : IDEN",
"idens : idens COMMA IDEN",
"transitions : TRANSITIONS COLON trans",
"trans : IDEN ARROW LB3 idens_set RB3 SEMICOLON",
"trans : trans IDEN ARROW LB3 idens_set RB3 SEMICOLON",
"idens_set : IDEN",
"idens_set : idens_set COMMA IDEN",
"specs : SPECS COLON fmls",
"fmls : fml SEMICOLON",
"fmls : fmls fml SEMICOLON",
"fml : TOP",
"fml : BOTTOM",
"fml : IDEN LB1 idens RB1",
"fml : NEG fml",
"fml : fml AND fml",
"fml : fml OR fml",
"fml : AX LB1 IDEN COMMA fml COMMA IDEN RB1",
"fml : EX LB1 IDEN COMMA fml COMMA IDEN RB1",
"fml : AF LB1 IDEN COMMA fml COMMA IDEN RB1",
"fml : EG LB1 IDEN COMMA fml COMMA IDEN RB1",
"fml : AR LB1 IDEN COMMA IDEN COMMA fml COMMA fml COMMA IDEN RB1",
"fml : EU LB1 IDEN COMMA IDEN COMMA fml COMMA fml COMMA IDEN RB1",
"fml : LB1 fml RB1",
};

//#line 239 "parser.y"
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

 
//#line 319 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 27 "parser.y"
{System.out.println("Input file read complete.");}
break;
case 2:
//#line 30 "parser.y"
{System.out.println("States read!");}
break;
case 3:
//#line 33 "parser.y"
{m.states.add(val_peek(0).sval);}
break;
case 4:
//#line 34 "parser.y"
{m.states.add(val_peek(0).sval);}
break;
case 5:
//#line 37 "parser.y"
{System.out.println("Atomics read!");}
break;
case 6:
//#line 40 "parser.y"
{m.atoms.add(new AtomicFml(val_peek(4).sval, (ArrayList<String>)val_peek(2).obj));}
break;
case 7:
//#line 41 "parser.y"
{m.atoms.add(new AtomicFml(val_peek(4).sval, (ArrayList<String>)val_peek(2).obj));}
break;
case 8:
//#line 44 "parser.y"
{ArrayList<String> al = new ArrayList<String>(); al.add(val_peek(0).sval); yyval.obj = al;}
break;
case 9:
//#line 45 "parser.y"
{ArrayList<String> al = (ArrayList<String>)val_peek(2).obj; yyval.obj = al;}
break;
case 10:
//#line 48 "parser.y"
{System.out.println("Transitions read!");}
break;
case 11:
//#line 51 "parser.y"
{m.transitions.put(val_peek(5).sval, (HashSet<String>)val_peek(2).obj);}
break;
case 12:
//#line 52 "parser.y"
{m.transitions.put(val_peek(5).sval, (HashSet<String>)val_peek(2).obj);}
break;
case 13:
//#line 55 "parser.y"
{HashSet<String> hs = new HashSet<String>(); hs.add(val_peek(0).sval); yyval.obj = hs;}
break;
case 14:
//#line 56 "parser.y"
{HashSet<String> hs = (HashSet<String>)val_peek(2).obj; hs.add(val_peek(0).sval); yyval.obj = hs;}
break;
case 15:
//#line 59 "parser.y"
{System.out.println("Specs read!");}
break;
case 16:
//#line 62 "parser.y"
{m.specs.add((Formula)val_peek(1).obj);}
break;
case 17:
//#line 63 "parser.y"
{m.specs.add((Formula)val_peek(1).obj);}
break;
case 18:
//#line 66 "parser.y"
{yyval.obj = new TopFml();}
break;
case 19:
//#line 67 "parser.y"
{yyval.obj = new BottomFml();}
break;
case 20:
//#line 68 "parser.y"
{yyval.obj = new AtomicFml(val_peek(3).sval, (ArrayList<String>)val_peek(1).obj);}
break;
case 21:
//#line 69 "parser.y"
{yyval.obj = new NegFml((Formula)val_peek(0).obj);}
break;
case 22:
//#line 70 "parser.y"
{yyval.obj = new AndFml((Formula)val_peek(2).obj,(Formula)val_peek(0).obj);}
break;
case 23:
//#line 71 "parser.y"
{yyval.obj = new OrFml((Formula)val_peek(2).obj,(Formula)val_peek(0).obj);}
break;
case 24:
//#line 72 "parser.y"
{yyval.obj = new AxFml(val_peek(5).sval, (Formula)val_peek(3).obj, (val_peek(1).sval));}
break;
case 25:
//#line 73 "parser.y"
{yyval.obj = new ExFml(val_peek(5).sval, (Formula)val_peek(3).obj, (val_peek(1).sval));}
break;
case 26:
//#line 74 "parser.y"
{yyval.obj = new AfFml(val_peek(5).sval, (Formula)val_peek(3).obj, (val_peek(1).sval));}
break;
case 27:
//#line 75 "parser.y"
{yyval.obj = new EgFml(val_peek(5).sval, (Formula)val_peek(3).obj, (val_peek(1).sval));}
break;
case 28:
//#line 76 "parser.y"
{yyval.obj = new ArFml(val_peek(9).sval, val_peek(7).sval, (Formula)val_peek(5).obj, (Formula)val_peek(3).obj,(val_peek(1).sval));}
break;
case 29:
//#line 77 "parser.y"
{yyval.obj = new EuFml(val_peek(9).sval, val_peek(7).sval, (Formula)val_peek(5).obj, (Formula)val_peek(3).obj,(val_peek(1).sval));}
break;
case 30:
//#line 78 "parser.y"
{yyval.obj = val_peek(1).obj;}
break;
//#line 588 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
