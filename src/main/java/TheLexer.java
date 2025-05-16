import java.io.*;
import java.util.Map;
import java.util.Vector;
import java.util.Arrays;

/**
 * Lexer class to analyze the input file
 * This one is an initial version that uses a DFA to recognize binary numbers
 *
 * @author javiergs
 * @author BraulioSG
 * @author DaniEsparza1712
 * @author DanielRPrado
 * @version 0.1
 */
public class TheLexer {
	
	private File file;
	private Automata dfa;
	private Vector<TheToken> tokens;
    private String[] KEYWORDS = {"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "continue", "const", "default", "do", "double", "else", "enum", "exports", "extends", "final", "finally", "float", "for", "if", "implements", "import", "instanceof", "int", "interface", "long", "module", "native", "new", "package", "private", "protected", "public", "requires", "return", "short", "static", "super", "switch", "synchronized", "string", "String", "throw", "throws", "transient", "try", "var", "void", "volatile", "while"};
	
	public TheLexer(File file) {
        this.file = file;
		tokens = new Vector<>();
		dfa = new Automata();

		dfa.addTransition("s0", "0", "s1");

        //Binary
		dfa.addMultipleTransitions("s1", "bB", "s4");
		dfa.addMultipleTransitions("s4", "01", "binary");
		dfa.addMultipleTransitions("binary", "01", "binary");

        //Hexadecimals
        dfa.addMultipleTransitions("s1", "xX", "s3");
        dfa.addMultipleTransitions("s3", "0123456789ABCDEFabcdef", "hex");
        dfa.addMultipleTransitions("hex", "0123456789ABCDEFabcdef", "hex");

        //octales
        dfa.addMultipleTransitions("s1", "01234567", "octal");
        dfa.addMultipleTransitions("octal", "01234567", "octal");

        //enteros
        dfa.addMultipleTransitions("s0", "123456789", "integer");
        dfa.addMultipleTransitions("integer", "0123456789", "integer");
        dfa.addTransition("integer", "_", "int_underscore");
        dfa.addTransition("int_underscore", "_", "int_underscore");
        dfa.addMultipleTransitions("int_underscore", "0123456789", "integer");

        //decimals
        dfa.addTransition("s0", ".", "s2");
        dfa.addTransition("s1", ".", "decimal");
        dfa.addTransition("integer", ".", "decimal");
        dfa.addMultipleTransitions("s2", "0123456789", "decimal");
        dfa.addMultipleTransitions("decimal", "0123456789", "decimal");
        dfa.addTransition("decimal", "_", "dec_underscore");
        dfa.addTransition("dec_underscore", "_", "dec_underscore");
        dfa.addMultipleTransitions("dec_underscore", "0123456789", "decimal");
        dfa.addMultipleTransitions("decimal", "eE", "s6");
        dfa.addMultipleTransitions("integer", "eE", "s6");
        dfa.addMultipleTransitions("s6", "+-", "s7");
        dfa.addMultipleTransitions("s6", "0123456789", "decimal_exp");
        dfa.addMultipleTransitions("s7", "0123456789", "decimal_exp");
        dfa.addMultipleTransitions("decimal_exp", "0123456789", "decimal_exp");

        //Identifiers
        dfa.addMultipleTransitions("s0", "$_abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ", "identifier");
        dfa.addMultipleTransitions("identifier", "$_abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ012345679", "identifier");
        dfa.addMultipleTransitions("identifier", "áéíóúÁÉÍÓÚäëïöüÄËÏÖÜâêîôûÂÊÎÔÛ", "identifier");

        //Strings
        dfa.addTransition("s0", "\"", "string_content");
        dfa.addTransition("string_content", "\\", "escape");
        dfa.addMultipleTransitions("escape", "'\"\\nrybfV0", "string_content");
        dfa.addMultipleTransitions("string_content", "abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ", "string_content");
        dfa.addMultipleTransitions("string_content", "0123456789", "string_content");
        dfa.addMultipleTransitions("string_content", " ¿?¡!#$%&/()={}[]<>.,;:-_+*''¨", "string_content");
        dfa.addMultipleTransitions("string_content", "áéíóúÁÉÍÓÚäëïöüÄËÏÖÜâêîôûÂÊÎÔÛ", "string_content");
        dfa.addMultipleTransitions("string_content", "\"", "string");

        //Chars
        dfa.addTransition("s0", "\'", "char_content");
        dfa.addMultipleTransitions("char_content", "$_abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ012345679", "char_end");
        dfa.addTransition("char_content", "\\", "char_escape");
        dfa.addMultipleTransitions("char_escape", "nrtbfx", "char_end");
        dfa.addTransition("char_escape", "'", "char_quote");
        dfa.addTransition("char_quote", "'", "char_end"); 
        dfa.addTransition("char_end", "\'", "char");


		dfa.addAcceptState("binary", "BINARY");
        dfa.addAcceptState("hex", "HEX");
        dfa.addAcceptState("octal", "OCTAL");
		dfa.addAcceptState("s1", "INTEGER");
        dfa.addAcceptState("integer", "INTEGER");
        dfa.addAcceptState("decimal", "DECIMAL");
        dfa.addAcceptState("decimal_exp", "DECIMAL");
        dfa.addAcceptState("identifier", "IDENTIFIER");
        dfa.addAcceptState("string", "STRING");
        dfa.addAcceptState("char", "CHAR");
        dfa.addAcceptState("char_quote", "CHAR");
	}
	
	public void run() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while ((line = reader.readLine()) != null) {
			algorithm(line);
		}
	}
	
	private void algorithm(String line) {
		String currentState = "s0";
		String nextState;
		String string = "";
		int index = 0;
        String prevTheToken = "";
		
		while (index < line.length()) {
			char currentChar = line.charAt(index);
			if (!((isOperator(currentChar) && currentState != "s6" )|| isDelimiter(currentChar) || isSpace(currentChar) || (currentChar == '.' && (currentState == "identifier" || prevTheToken == "DELIMITER"))) || currentState == "string_content") {
				nextState = dfa.getNextState(currentState, currentChar);
				string = string + currentChar;
				currentState = nextState;
			} else {
				if (dfa.isAcceptState(currentState)) {
					String stateName = dfa.getAcceptStateName(currentState);
                    if(stateName == "IDENTIFIER" && Arrays.asList(this.KEYWORDS).contains(string)){
                        stateName = "KEYWORD";
                    }
					tokens.add(new TheToken(string, stateName));
                    prevTheToken = stateName;
				} else if (currentState != "s0") {
					tokens.add(new TheToken(string, "ERROR"));
                    prevTheToken = "ERROR";
				}
				if (isOperator(currentChar)) {
					tokens.add(new TheToken(currentChar + "", "OPERATOR"));
                    prevTheToken = "OPERATOR";
				} else if (isDelimiter(currentChar)) {
					tokens.add(new TheToken(currentChar + "", "DELIMITER"));
                    prevTheToken = "DELIMITER";
				}
                if (currentChar == '.' && (currentState == "identifier" || prevTheToken == "DELIMITER")){
                    tokens.add(new TheToken(currentChar + "", "OPERATOR"));
                    prevTheToken = "OPERATOR";
                }
				currentState = "s0";
				string = "";
			}
			index++;
		}
		// last word
		if (dfa.isAcceptState(currentState)) {
			String stateName = dfa.getAcceptStateName(currentState);
			tokens.add(new TheToken(string, stateName));
		} else if (currentState != "s0") {
			tokens.add(new TheToken(string, "ERROR"));
		}
	}
	
	private boolean isSpace(char c) {
		return c == ' ' || c == '\t' || c == '\n';
	}
	
	private boolean isDelimiter(char c) {
		return c == ',' || c == ';' || c == '[' || c == ']' || c == '{' || c == '}' || c == '(' || c == ')' || c == '<' || c == '>' || c == ':';
	}
	
	private boolean isOperator(char c) {
		return c == '=' || c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '&' || c == '|' || c == '^' || c == '<' || c == '>' || c == '!';
	}
	
	public void printTokens() {
		for (TheToken token : tokens) {
			System.out.printf("%10s\t|\t%s\n", token.getValue(), token.getType());
		}
	}
	
	public Vector<TheToken> getTokens() {
		return tokens;
	}
	
}
