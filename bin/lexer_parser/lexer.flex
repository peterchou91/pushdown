%%

%byaccj

%{
	public int line_no = 1;
	private Parser yyparser;
	public Yylex(java.io.Reader r, Parser yyparser) {
		this(r);
		this.yyparser = yyparser;
	}
%}

IDEN = [:jletter:] [:jletterdigit:]*
NEWLINE = \r | \n | \r\n 
WHITESPACE = [  \t]+

%%

/*关键字*/
"states"		{return Parser.STATES;}
"atoms"			{return Parser.ATOMS;}
"transitions"	{return Parser.TRANSITIONS;}
"specs"			{return Parser.SPECS;}
"TRUE"		{return Parser.TOP;}
"FALSE"		{return Parser.BOTTOM;}
"AX"		{return Parser.AX;}
"EX"		{return Parser.EX;}
"AF"		{return Parser.AF;}
"EG"		{return Parser.EG;}
"AR"		{return Parser.AR;}
"EU"		{return Parser.EU;}
"not"		{return Parser.NEG;}
/*操作符和记号*/
"("			{return Parser.LB1;}
")"			{return Parser.RB1;}
"{"			{return Parser.LB3;}
"}"			{return Parser.RB3;}
":"			{return Parser.COLON;}
";"			{return Parser.SEMICOLON;}
","			{return Parser.COMMA;}
"/\\"		{return Parser.AND;}
"\\/"		{return Parser.OR;}
"->"		{return Parser.ARROW;}
{WHITESPACE}	{ }
{NEWLINE}		{line_no++;}
{IDEN}			{yyparser.yylval = new ParserVal(yytext()); return Parser.IDEN;}
<<EOF>>		{System.out.println("end of file"); return -1;}
[^]    { System.err.println("Error: unexpected character '"+yytext()+"'"); return -1; }
















