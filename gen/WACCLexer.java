// Generated from /home/st2220/IdeaProjects/WACC_49/antlr_config/WACCLexer.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WACCLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BEGIN=1, END=2, SKP=3, READ=4, FREE=5, RETURN=6, EXIT=7, PRINT=8, PRINTLN=9, 
		CALL=10, TRUE=11, FALSE=12, NULL=13, IS=14, IF=15, THEN=16, ELSE=17, FI=18, 
		WHILE=19, DO=20, DONE=21, PAIR=22, NEWPAIR=23, FST=24, SND=25, INT=26, 
		BOOL=27, CHAR=28, STRING=29, OPEN_PARENTHESES=30, CLOSE_PARENTHESES=31, 
		OPEN_SQUARE_BRACKETS=32, CLOSE_SQUARE_BRACKETS=33, ASSIGN=34, NOT=35, 
		LEN=36, ORD=37, CHR=38, MUL=39, DIV=40, MOD=41, PLUS=42, MINUS=43, GT=44, 
		GEQ=45, LT=46, LEQ=47, EQ=48, NEQ=49, AND=50, OR=51, INTSIGN=52, COMMA=53, 
		SEMICOLON=54, UNDERSCORE=55, APOSTROPHE=56, QUOTATION=57, CHARACTER=58, 
		ESCASPED_CHAR=59, LETTER=60, DIGIT=61, SHARP=62, EOL=63, COMMENT=64;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"BEGIN", "END", "SKP", "READ", "FREE", "RETURN", "EXIT", "PRINT", "PRINTLN", 
			"CALL", "TRUE", "FALSE", "NULL", "IS", "IF", "THEN", "ELSE", "FI", "WHILE", 
			"DO", "DONE", "PAIR", "NEWPAIR", "FST", "SND", "INT", "BOOL", "CHAR", 
			"STRING", "OPEN_PARENTHESES", "CLOSE_PARENTHESES", "OPEN_SQUARE_BRACKETS", 
			"CLOSE_SQUARE_BRACKETS", "ASSIGN", "NOT", "LEN", "ORD", "CHR", "MUL", 
			"DIV", "MOD", "PLUS", "MINUS", "GT", "GEQ", "LT", "LEQ", "EQ", "NEQ", 
			"AND", "OR", "INTSIGN", "COMMA", "SEMICOLON", "UNDERSCORE", "APOSTROPHE", 
			"QUOTATION", "CHARACTER", "ESCASPED_CHAR", "LETTER", "DIGIT", "SHARP", 
			"EOL", "COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'begin'", "'END'", "'skip'", "'read'", "'free'", "'return'", "'exit'", 
			"'print'", "'println'", "'call'", "'true'", "'false'", "'null'", "'is'", 
			"'if'", "'then'", "'else'", "'fi'", "'while'", "'do'", "'done'", "'pair'", 
			"'newpair'", "'fst'", "'snd'", "'int'", "'bool'", "'char'", "'string'", 
			"'('", "')'", "'['", "']'", "'='", "'!'", "'len'", "'ord'", "'chr'", 
			"'*'", "'/'", "'%'", "'+'", "'-'", "'>'", "'>='", "'<'", "'<='", "'=='", 
			"'!='", "'&&'", "'||'", null, "','", "';'", "'_'", "'''", "'\"'", null, 
			null, null, null, "'#'", "'\n'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BEGIN", "END", "SKP", "READ", "FREE", "RETURN", "EXIT", "PRINT", 
			"PRINTLN", "CALL", "TRUE", "FALSE", "NULL", "IS", "IF", "THEN", "ELSE", 
			"FI", "WHILE", "DO", "DONE", "PAIR", "NEWPAIR", "FST", "SND", "INT", 
			"BOOL", "CHAR", "STRING", "OPEN_PARENTHESES", "CLOSE_PARENTHESES", "OPEN_SQUARE_BRACKETS", 
			"CLOSE_SQUARE_BRACKETS", "ASSIGN", "NOT", "LEN", "ORD", "CHR", "MUL", 
			"DIV", "MOD", "PLUS", "MINUS", "GT", "GEQ", "LT", "LEQ", "EQ", "NEQ", 
			"AND", "OR", "INTSIGN", "COMMA", "SEMICOLON", "UNDERSCORE", "APOSTROPHE", 
			"QUOTATION", "CHARACTER", "ESCASPED_CHAR", "LETTER", "DIGIT", "SHARP", 
			"EOL", "COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public WACCLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "WACCLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2B\u0176\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3"+
		"\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3"+
		"\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3"+
		"\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3"+
		"\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3"+
		"\31\3\31\3\31\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3"+
		"\34\3\34\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3"+
		"\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3%\3%\3&\3&\3&\3&\3\'\3"+
		"\'\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3.\3/\3/\3\60\3\60"+
		"\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3\65"+
		"\3\65\5\65\u0151\n\65\3\66\3\66\3\67\3\67\38\38\39\39\3:\3:\3;\3;\3;\5"+
		";\u0160\n;\3<\3<\3=\3=\3>\3>\3?\3?\3@\3@\3A\3A\7A\u016e\nA\fA\16A\u0171"+
		"\13A\3A\3A\3A\3A\2\2B\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27"+
		"\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33"+
		"\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63"+
		"e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081B\3\2\7\5\2$$))^^\13\2$$"+
		"))\62\62^^ddhhppttvv\4\2C\\c|\3\2\62;\3\2\f\f\2\u0178\2\3\3\2\2\2\2\5"+
		"\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2"+
		"\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33"+
		"\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2"+
		"\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2"+
		"\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2"+
		"\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K"+
		"\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2"+
		"\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2"+
		"\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q"+
		"\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2"+
		"\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\3\u0083\3\2\2\2\5\u0089\3\2\2\2\7\u008d"+
		"\3\2\2\2\t\u0092\3\2\2\2\13\u0097\3\2\2\2\r\u009c\3\2\2\2\17\u00a3\3\2"+
		"\2\2\21\u00a8\3\2\2\2\23\u00ae\3\2\2\2\25\u00b6\3\2\2\2\27\u00bb\3\2\2"+
		"\2\31\u00c0\3\2\2\2\33\u00c6\3\2\2\2\35\u00cb\3\2\2\2\37\u00ce\3\2\2\2"+
		"!\u00d1\3\2\2\2#\u00d6\3\2\2\2%\u00db\3\2\2\2\'\u00de\3\2\2\2)\u00e4\3"+
		"\2\2\2+\u00e7\3\2\2\2-\u00ec\3\2\2\2/\u00f1\3\2\2\2\61\u00f9\3\2\2\2\63"+
		"\u00fd\3\2\2\2\65\u0101\3\2\2\2\67\u0105\3\2\2\29\u010a\3\2\2\2;\u010f"+
		"\3\2\2\2=\u0116\3\2\2\2?\u0118\3\2\2\2A\u011a\3\2\2\2C\u011c\3\2\2\2E"+
		"\u011e\3\2\2\2G\u0120\3\2\2\2I\u0122\3\2\2\2K\u0126\3\2\2\2M\u012a\3\2"+
		"\2\2O\u012e\3\2\2\2Q\u0130\3\2\2\2S\u0132\3\2\2\2U\u0134\3\2\2\2W\u0136"+
		"\3\2\2\2Y\u0138\3\2\2\2[\u013a\3\2\2\2]\u013d\3\2\2\2_\u013f\3\2\2\2a"+
		"\u0142\3\2\2\2c\u0145\3\2\2\2e\u0148\3\2\2\2g\u014b\3\2\2\2i\u0150\3\2"+
		"\2\2k\u0152\3\2\2\2m\u0154\3\2\2\2o\u0156\3\2\2\2q\u0158\3\2\2\2s\u015a"+
		"\3\2\2\2u\u015f\3\2\2\2w\u0161\3\2\2\2y\u0163\3\2\2\2{\u0165\3\2\2\2}"+
		"\u0167\3\2\2\2\177\u0169\3\2\2\2\u0081\u016b\3\2\2\2\u0083\u0084\7d\2"+
		"\2\u0084\u0085\7g\2\2\u0085\u0086\7i\2\2\u0086\u0087\7k\2\2\u0087\u0088"+
		"\7p\2\2\u0088\4\3\2\2\2\u0089\u008a\7G\2\2\u008a\u008b\7P\2\2\u008b\u008c"+
		"\7F\2\2\u008c\6\3\2\2\2\u008d\u008e\7u\2\2\u008e\u008f\7m\2\2\u008f\u0090"+
		"\7k\2\2\u0090\u0091\7r\2\2\u0091\b\3\2\2\2\u0092\u0093\7t\2\2\u0093\u0094"+
		"\7g\2\2\u0094\u0095\7c\2\2\u0095\u0096\7f\2\2\u0096\n\3\2\2\2\u0097\u0098"+
		"\7h\2\2\u0098\u0099\7t\2\2\u0099\u009a\7g\2\2\u009a\u009b\7g\2\2\u009b"+
		"\f\3\2\2\2\u009c\u009d\7t\2\2\u009d\u009e\7g\2\2\u009e\u009f\7v\2\2\u009f"+
		"\u00a0\7w\2\2\u00a0\u00a1\7t\2\2\u00a1\u00a2\7p\2\2\u00a2\16\3\2\2\2\u00a3"+
		"\u00a4\7g\2\2\u00a4\u00a5\7z\2\2\u00a5\u00a6\7k\2\2\u00a6\u00a7\7v\2\2"+
		"\u00a7\20\3\2\2\2\u00a8\u00a9\7r\2\2\u00a9\u00aa\7t\2\2\u00aa\u00ab\7"+
		"k\2\2\u00ab\u00ac\7p\2\2\u00ac\u00ad\7v\2\2\u00ad\22\3\2\2\2\u00ae\u00af"+
		"\7r\2\2\u00af\u00b0\7t\2\2\u00b0\u00b1\7k\2\2\u00b1\u00b2\7p\2\2\u00b2"+
		"\u00b3\7v\2\2\u00b3\u00b4\7n\2\2\u00b4\u00b5\7p\2\2\u00b5\24\3\2\2\2\u00b6"+
		"\u00b7\7e\2\2\u00b7\u00b8\7c\2\2\u00b8\u00b9\7n\2\2\u00b9\u00ba\7n\2\2"+
		"\u00ba\26\3\2\2\2\u00bb\u00bc\7v\2\2\u00bc\u00bd\7t\2\2\u00bd\u00be\7"+
		"w\2\2\u00be\u00bf\7g\2\2\u00bf\30\3\2\2\2\u00c0\u00c1\7h\2\2\u00c1\u00c2"+
		"\7c\2\2\u00c2\u00c3\7n\2\2\u00c3\u00c4\7u\2\2\u00c4\u00c5\7g\2\2\u00c5"+
		"\32\3\2\2\2\u00c6\u00c7\7p\2\2\u00c7\u00c8\7w\2\2\u00c8\u00c9\7n\2\2\u00c9"+
		"\u00ca\7n\2\2\u00ca\34\3\2\2\2\u00cb\u00cc\7k\2\2\u00cc\u00cd\7u\2\2\u00cd"+
		"\36\3\2\2\2\u00ce\u00cf\7k\2\2\u00cf\u00d0\7h\2\2\u00d0 \3\2\2\2\u00d1"+
		"\u00d2\7v\2\2\u00d2\u00d3\7j\2\2\u00d3\u00d4\7g\2\2\u00d4\u00d5\7p\2\2"+
		"\u00d5\"\3\2\2\2\u00d6\u00d7\7g\2\2\u00d7\u00d8\7n\2\2\u00d8\u00d9\7u"+
		"\2\2\u00d9\u00da\7g\2\2\u00da$\3\2\2\2\u00db\u00dc\7h\2\2\u00dc\u00dd"+
		"\7k\2\2\u00dd&\3\2\2\2\u00de\u00df\7y\2\2\u00df\u00e0\7j\2\2\u00e0\u00e1"+
		"\7k\2\2\u00e1\u00e2\7n\2\2\u00e2\u00e3\7g\2\2\u00e3(\3\2\2\2\u00e4\u00e5"+
		"\7f\2\2\u00e5\u00e6\7q\2\2\u00e6*\3\2\2\2\u00e7\u00e8\7f\2\2\u00e8\u00e9"+
		"\7q\2\2\u00e9\u00ea\7p\2\2\u00ea\u00eb\7g\2\2\u00eb,\3\2\2\2\u00ec\u00ed"+
		"\7r\2\2\u00ed\u00ee\7c\2\2\u00ee\u00ef\7k\2\2\u00ef\u00f0\7t\2\2\u00f0"+
		".\3\2\2\2\u00f1\u00f2\7p\2\2\u00f2\u00f3\7g\2\2\u00f3\u00f4\7y\2\2\u00f4"+
		"\u00f5\7r\2\2\u00f5\u00f6\7c\2\2\u00f6\u00f7\7k\2\2\u00f7\u00f8\7t\2\2"+
		"\u00f8\60\3\2\2\2\u00f9\u00fa\7h\2\2\u00fa\u00fb\7u\2\2\u00fb\u00fc\7"+
		"v\2\2\u00fc\62\3\2\2\2\u00fd\u00fe\7u\2\2\u00fe\u00ff\7p\2\2\u00ff\u0100"+
		"\7f\2\2\u0100\64\3\2\2\2\u0101\u0102\7k\2\2\u0102\u0103\7p\2\2\u0103\u0104"+
		"\7v\2\2\u0104\66\3\2\2\2\u0105\u0106\7d\2\2\u0106\u0107\7q\2\2\u0107\u0108"+
		"\7q\2\2\u0108\u0109\7n\2\2\u01098\3\2\2\2\u010a\u010b\7e\2\2\u010b\u010c"+
		"\7j\2\2\u010c\u010d\7c\2\2\u010d\u010e\7t\2\2\u010e:\3\2\2\2\u010f\u0110"+
		"\7u\2\2\u0110\u0111\7v\2\2\u0111\u0112\7t\2\2\u0112\u0113\7k\2\2\u0113"+
		"\u0114\7p\2\2\u0114\u0115\7i\2\2\u0115<\3\2\2\2\u0116\u0117\7*\2\2\u0117"+
		">\3\2\2\2\u0118\u0119\7+\2\2\u0119@\3\2\2\2\u011a\u011b\7]\2\2\u011bB"+
		"\3\2\2\2\u011c\u011d\7_\2\2\u011dD\3\2\2\2\u011e\u011f\7?\2\2\u011fF\3"+
		"\2\2\2\u0120\u0121\7#\2\2\u0121H\3\2\2\2\u0122\u0123\7n\2\2\u0123\u0124"+
		"\7g\2\2\u0124\u0125\7p\2\2\u0125J\3\2\2\2\u0126\u0127\7q\2\2\u0127\u0128"+
		"\7t\2\2\u0128\u0129\7f\2\2\u0129L\3\2\2\2\u012a\u012b\7e\2\2\u012b\u012c"+
		"\7j\2\2\u012c\u012d\7t\2\2\u012dN\3\2\2\2\u012e\u012f\7,\2\2\u012fP\3"+
		"\2\2\2\u0130\u0131\7\61\2\2\u0131R\3\2\2\2\u0132\u0133\7\'\2\2\u0133T"+
		"\3\2\2\2\u0134\u0135\7-\2\2\u0135V\3\2\2\2\u0136\u0137\7/\2\2\u0137X\3"+
		"\2\2\2\u0138\u0139\7@\2\2\u0139Z\3\2\2\2\u013a\u013b\7@\2\2\u013b\u013c"+
		"\7?\2\2\u013c\\\3\2\2\2\u013d\u013e\7>\2\2\u013e^\3\2\2\2\u013f\u0140"+
		"\7>\2\2\u0140\u0141\7?\2\2\u0141`\3\2\2\2\u0142\u0143\7?\2\2\u0143\u0144"+
		"\7?\2\2\u0144b\3\2\2\2\u0145\u0146\7#\2\2\u0146\u0147\7?\2\2\u0147d\3"+
		"\2\2\2\u0148\u0149\7(\2\2\u0149\u014a\7(\2\2\u014af\3\2\2\2\u014b\u014c"+
		"\7~\2\2\u014c\u014d\7~\2\2\u014dh\3\2\2\2\u014e\u0151\5W,\2\u014f\u0151"+
		"\5U+\2\u0150\u014e\3\2\2\2\u0150\u014f\3\2\2\2\u0151j\3\2\2\2\u0152\u0153"+
		"\7.\2\2\u0153l\3\2\2\2\u0154\u0155\7=\2\2\u0155n\3\2\2\2\u0156\u0157\7"+
		"a\2\2\u0157p\3\2\2\2\u0158\u0159\7)\2\2\u0159r\3\2\2\2\u015a\u015b\7$"+
		"\2\2\u015bt\3\2\2\2\u015c\u0160\n\2\2\2\u015d\u015e\7^\2\2\u015e\u0160"+
		"\5w<\2\u015f\u015c\3\2\2\2\u015f\u015d\3\2\2\2\u0160v\3\2\2\2\u0161\u0162"+
		"\t\3\2\2\u0162x\3\2\2\2\u0163\u0164\t\4\2\2\u0164z\3\2\2\2\u0165\u0166"+
		"\t\5\2\2\u0166|\3\2\2\2\u0167\u0168\7%\2\2\u0168~\3\2\2\2\u0169\u016a"+
		"\7\f\2\2\u016a\u0080\3\2\2\2\u016b\u016f\5}?\2\u016c\u016e\n\6\2\2\u016d"+
		"\u016c\3\2\2\2\u016e\u0171\3\2\2\2\u016f\u016d\3\2\2\2\u016f\u0170\3\2"+
		"\2\2\u0170\u0172\3\2\2\2\u0171\u016f\3\2\2\2\u0172\u0173\5\177@\2\u0173"+
		"\u0174\3\2\2\2\u0174\u0175\bA\2\2\u0175\u0082\3\2\2\2\6\2\u0150\u015f"+
		"\u016f\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}