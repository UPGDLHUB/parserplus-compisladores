import java.util.*;

public class TheParser {

	private Vector<TheToken> tokens;
	private int currentToken;
	private Map<String, Set> first = new HashMap<String, Set>();
	private Map<String, Set> follow = new HashMap<String, Set>();

	public TheParser(Vector<TheToken> tokens) {
		this.tokens = tokens;
		currentToken = 0;

		SetFirsts();
		SetFollow();
	}

	public void run() {
		RULE_PROGRAM();
	}

	private void SetFirsts(){
		first.put("PROGRAM", new HashSet<>(Arrays.asList("class")));
		first.put("TYPE", new HashSet<>(Arrays.asList("int", "boolean", "float", "void", "char", "String")));
		first.put("METHOD", first.get("TYPE"));
		first.put("PARAMS", new HashSet<>(Arrays.asList("")));
		first.get("PARAMS").addAll(first.get("TYPE"));
		first.put("VARIABLE", first.get("TYPE"));
		first.put("ASSIGNMENT", new HashSet<>(Arrays.asList("identifier")));
		first.put("WHILE", new HashSet<>(Arrays.asList("while")));
		first.put("IF", new HashSet<>(Arrays.asList("if")));
		first.put("RETURN", new HashSet<>(Arrays.asList("return")));
		first.put("CALL_METHOD", new HashSet<>(Arrays.asList("identifier")));
		first.put("C", new HashSet<>(Arrays.asList("(", "identifier", "integer", "octal", "hexadecimal", "binary", "true"
				, "false", "String", "char", "float")));
		first.put("B", new HashSet<>(Arrays.asList("-")));
		first.get("B").addAll(first.get("C"));
		first.put("A", new HashSet<>(Arrays.asList("/", "*")));
		first.get("A").addAll(first.get("B"));
		first.put("E", new HashSet<>(Arrays.asList("-", "+")));
		first.get("E").addAll(first.get("A"));
		first.put("R", new HashSet<>(Arrays.asList("!=", "==", "<", ">")));
		first.get("R").addAll(first.get("E"));
		first.put("Y", new HashSet<>(Arrays.asList("!")));
		first.get("Y").addAll(first.get("R"));
		first.put("X", first.get("Y"));
		first.put("EXPRESSION", first.get("X"));
		first.put("SWITCHCASE", new HashSet<>(Arrays.asList("case")));
		first.put("SWITCH", new HashSet<>(Arrays.asList("switch")));
		first.put("FOR", new HashSet<>(Arrays.asList("for")));
		first.put("DOWHILE", new HashSet<>(Arrays.asList("do")));
		first.put("PARAM_VALUES", first.get("EXPRESSION") );

		first.put("BODY", first.get("VARIABLE"));
		first.get("BODY").addAll(first.get("ASSIGNMENT"));
		first.get("BODY").addAll(first.get("CALL_METHOD"));
		first.get("BODY").addAll(first.get("RETURN"));
		first.get("BODY").addAll(first.get("WHILE"));
		first.get("BODY").addAll(first.get("IF"));
		first.get("BODY").addAll(first.get("DOWHILE"));
		first.get("BODY").addAll(first.get("FOR"));
		first.get("BODY").addAll(first.get("SWITCH"));
	}

	private void SetFollow(){
		follow.put("METHOD", new HashSet<>(Arrays.asList(";", "}")));
		follow.get("METHOD").addAll(first.get("METHOD"));
		follow.get("METHOD").addAll(first.get("VARIABLE"));
		follow.get("METHOD").addAll(first.get("ASSIGNMENT"));

		follow.put("VARIABLE", new HashSet<>(Arrays.asList(";", "}")));
		follow.get("VARIABLE").addAll(first.get("VARIABLE"));
		follow.get("VARIABLE").addAll(first.get("METHOD"));
		follow.get("VARIABLE").addAll(first.get("ASSIGNMENT"));

		follow.put("ASSIGNMENT", new HashSet<>(Arrays.asList(";", "}", ")")));
		follow.get("ASSIGNMENT").addAll(first.get("ASSIGNMENT"));
		follow.get("ASSIGNMENT").addAll(first.get("METHOD"));
		follow.get("ASSIGNMENT").addAll(first.get("VARIABLE"));

		follow.put("CALL_METHOD", new HashSet<>(Arrays.asList(";")));
		follow.put("RETURN", new HashSet<>(Arrays.asList(";")));

		follow.put("WHILE", first.get("BODY"));
		follow.put("IF", first.get("BODY"));
		follow.put("DOWHILE", first.get("BODY"));
		follow.put("FOR", first.get("BODY"));
		follow.put("SWITCH", first.get("BODY"));
		follow.put("SWITCHCASE", new HashSet<>(Arrays.asList("default", "}")));
		follow.get("SWITCHCASE").addAll(first.get("SWITCHCASE"));

		follow.put("TYPE", new HashSet<>(Arrays.asList("identifier")));
		follow.put("PARAMS", new HashSet<>(Arrays.asList(")")));
		follow.put("PARAM_VALUES", new HashSet<>(Arrays.asList(")")));

		follow.put("EXPRESSION", new HashSet<>(Arrays.asList(")", ";", ",")));
		follow.get("EXPRESSION").addAll(follow.get("PARAM_VALUES"));
		follow.get("EXPRESSION").addAll(follow.get("RETURN"));
		follow.get("EXPRESSION").addAll(follow.get("VARIABLE"));

		follow.put("X", new HashSet<>(Arrays.asList("|")));
		follow.get("X").addAll(follow.get("EXPRESSION"));

		follow.put("Y", new HashSet<>(Arrays.asList("&")));
		follow.get("Y").addAll(follow.get("X"));

		follow.put("R", follow.get("Y"));
		follow.put("E", follow.get("R"));
		follow.put("A", follow.get("E"));
		follow.put("B", follow.get("A"));

		follow.put("C", new HashSet<>(Arrays.asList(":")));
		follow.get("C").addAll(follow.get("B"));

		follow.put("BODY", new HashSet<>(Arrays.asList("}", "break")));
		follow.get("BODY").addAll(follow.get("FOR"));
		follow.get("BODY").addAll(follow.get("FOR"));
		follow.get("BODY").addAll(follow.get("IF"));
		follow.get("BODY").addAll(follow.get("WHILE"));
	}

	private void RULE_PROGRAM() {
		System.out.println("- RULE_PROGRAM");

		//Check if current token is part of the set of FIRSTS
		while(!CheckFirsts("PROGRAM")){
			errorFirst("PROGRAM");
			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("PROGRAM")){
				RULE_PROGRAM();
				return;
			}
			//If current token is part of follows, continue execution
			if(CheckFollows("PROGRAM"))
				break;
		}

		if(!tokens.get(currentToken).getType().equals("KEYWORD"))
			error(1);


		if(!tokens.get(currentToken).getValue().equals("class"))
			error(1);
		System.out.println("- CLASS");

		currentToken++;

		if(!tokens.get(currentToken).getType().equals("IDENTIFIER"))
			error(1);
		System.out.println("- IDENTIFIER");

		currentToken++;

		if(!tokens.get(currentToken).getValue().equals("{"))
			error(1);
		System.out.println("- {");

		currentToken++;

		while(!tokens.get(currentToken).getValue().equals("}")){
			if(tokens.get(currentToken + 2).getValue().equals("("))
				RULE_METHODS();
			else{
				if(tokens.get(currentToken).getType().equals("KEYWORD"))
					RULE_VARIABLE();
				else
					RULE_ASSIGNMENT();
				currentToken++;
			}
		}
		System.out.println("- }");
	}

	public void RULE_METHODS(){
		System.out.println("-- RULE_METHODS");

		//Check if current token is part of the set of FIRSTS
		while(!CheckFirsts("METHOD")){
			errorFirst("METHOD");

			//If current token is part of follows, continue execution
			if(CheckFollows("METHOD"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("METHOD")){
				RULE_METHODS();
				return;
			}
		}

		RULE_TYPE();

		if(!tokens.get(currentToken).getType().equals("IDENTIFIER"))
			error(2);

		currentToken++;
		System.out.println("-- IDENTIFIER");

		if(!tokens.get(currentToken).getValue().equals("("))
			error(2);

		currentToken++;
		System.out.println("-- (");

		RULE_PARAMS();

		if(!tokens.get(currentToken).getValue().equals(")"))
			error(2);

		currentToken++;
		System.out.println("-- )");

		if(!tokens.get(currentToken).getValue().equals("{"))
			error(2);

		currentToken++;
		System.out.println("-- {");


		while(!tokens.get(currentToken).getValue().equals("}")){
			System.out.println(tokens.get(currentToken));
			RULE_BODY();
		}

		currentToken++;
		System.out.println("-- }");
	}


	public void RULE_BODY() {
		System.out.println("-- RULE_BODY");

		while(!CheckFirsts("BODY")){
			errorFirst("BODY");

			//If current token is part of follows, continue execution
			if(CheckFollows("BODY"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("BODY")){
				RULE_BODY();
				return;
			}
		}

		if(tokens.get(currentToken).getValue().equals("}")){
			System.out.println("--- }");
			return;
		}

		if(!(tokens.get(currentToken).getType().equals("KEYWORD") || tokens.get(currentToken).getType().equals("IDENTIFIER"))){
			error(3);
		}

		//return, control statements and data types are keywords
		if(tokens.get(currentToken).getType().equals("KEYWORD")){

			boolean isControlStatement = true;

			switch(tokens.get(currentToken).getValue()){
				case "if":
					RULE_IF();
					currentToken++;
					break;
				case "while":
					RULE_WHILE();
					currentToken++;
					break;
				case "for":
					RULE_FOR();
					currentToken++;
					break;
				case "switch":
					RULE_SWITCH();
					currentToken++;
					break;
				case "do":
					RULE_DO_WHILE();
					currentToken++;
					break;
				default:
					isControlStatement = false;
			}

			if(isControlStatement)
				return;

			if(tokens.get(currentToken).getValue().equals("return"))
				RULE_RETURN();
			else
				RULE_VARIABLE();

		}
		else{
			if(tokens.get(currentToken + 1).getValue().equals("="))
				RULE_ASSIGNMENT();
			else
				RULE_CALL_METHOD();
		}

		if(!tokens.get(currentToken).getValue().equals(";"))
			error(3);
		currentToken++;
	}

	public void RULE_FOR(){
		System.out.println("-- RULE_FOR");

		while(!CheckFirsts("FOR")){
			errorFirst("FOR");

			//If current token is part of follows, continue execution
			if(CheckFollows("FOR"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("FOR")){
				RULE_FOR();
				return;
			}
		}

		if(tokens.get(currentToken).getValue().equals("for"))
			currentToken++;
		else
			error(4);
		if(tokens.get(currentToken).getValue().equals("("))
			currentToken++;
		else
			error(4);

		if(!tokens.get(currentToken).getValue().equals(";")){
			RULE_VARIABLE();
			if(!tokens.get(currentToken).getValue().equals(";"))
				error(4);
		}
		currentToken++;

		RULE_EXPRESSION();
		if(!tokens.get(currentToken).getValue().equals(";"))
			error(4);
		currentToken++;

		if(tokens.get(currentToken + 1).getValue().equals("="))
			RULE_ASSIGNMENT();
		else
			RULE_EXPRESSION();

		if(tokens.get(currentToken).getValue().equals(")"))
			currentToken++;
		else
			error(4);

		if(tokens.get(currentToken).getValue().equals("{"))
			currentToken++;
		else{
			RULE_BODY();
			currentToken -= 1;
			System.out.println("-- END FOR");
			return;
		}

		System.out.println("-- {");

		while(!tokens.get(currentToken).getValue().equals("}")){
			System.out.println(tokens.get(currentToken));
			System.out.println("FOR BODY");
			RULE_BODY();
		}
		System.out.println("-- }");
		System.out.println("-- END FOR");
	}

	public void RULE_DO_WHILE(){

		while(!CheckFirsts("DOWHILE")){
			errorFirst("DOWHILE");

			//If current token is part of follows, continue execution
			if(CheckFollows("DOWHILE"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("DOWHILE")){
				RULE_DO_WHILE();
				return;
			}
		}

		if(!tokens.get(currentToken).getValue().equals("do"))
			error(5);

		currentToken++;

		if(!tokens.get(currentToken).getValue().equals("{"))
			error(5);

		currentToken++;

		while(!tokens.get(currentToken).getValue().equals("}")){
			System.out.println(tokens.get(currentToken));
			System.out.println("FOR BODY");
			RULE_BODY();
		}
		currentToken++;

		if(tokens.get(currentToken).getValue().equals("while")){
			currentToken++;
			System.out.println("------ WHILE");
		}
		else return;
		if(tokens.get(currentToken).getValue().equals("(")){
			currentToken++;
			System.out.println("---- (");
		}
		else {
			error(5);
		}
		RULE_EXPRESSION();
		if(tokens.get(currentToken).getValue().equals(")")){
			currentToken++;
			System.out.println("---- )");
		}
		else {
			error(5);
		}

		if(!tokens.get(currentToken).getValue().equals(";"))
			error(5);

		System.out.println("-- END DO WHILE");
	}

	public void RULE_SWITCH(){
		System.out.println("-- RULE SWITCH");

		while(!CheckFirsts("SWITCH")){
			errorFirst("SWITCH");

			//If current token is part of follows, continue execution
			if(CheckFollows("SWITCH"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("SWITCH")){
				RULE_SWITCH();
				return;
			}
		}

		if(tokens.get(currentToken).getValue().equals("switch"))
			currentToken++;
		else
			error(6);

		if(tokens.get(currentToken).getValue().equals("("))
			currentToken++;
		else
			error(6);

		RULE_EXPRESSION();

		if(tokens.get(currentToken).getValue().equals(")"))
			currentToken++;
		else
			error(6);

		if(tokens.get(currentToken).getValue().equals("{")){
			System.out.println("--- {");
			currentToken++;
		}
		else
			error(6);

		while (!tokens.get(currentToken).getValue().equals("default") && !tokens.get(currentToken).getValue().equals("}"))
			SWITCHCASE();

		if(tokens.get(currentToken).getValue().equals("}")){
			System.out.println("--- }");
		}
		else if (tokens.get(currentToken).getValue().equals("default")){
			System.out.println("--- DEFAULT");
			currentToken++;
			if(tokens.get(currentToken).getValue().equals(":"))
				currentToken++;
			else
				error(6);

			while (!tokens.get(currentToken).getValue().equals("break") && !tokens.get(currentToken).getValue().equals("case") && !tokens.get(currentToken).getValue().equals("}"))
				RULE_BODY();

			if(tokens.get(currentToken).getValue().equals("}")){
				return;
			}

			if(tokens.get(currentToken).getValue().equals("break")){
				System.out.println("---- BREAK");
				currentToken++;
				if(tokens.get(currentToken).getValue().equals(";")){
					currentToken++;
				}
				else{
					error(6);
				}
			}

			if(tokens.get(currentToken).getValue().equals("case")){
				SWITCHCASE();
			}

		} else
			error(6);

		System.out.println("-- END SWITCH");
	}

	private void SWITCHCASE(){
		System.out.println("--- SWITCHCASE");

		while(!CheckFirsts("SWITCHCASE")){
			errorFirst("SWITCHCASE");

			//If current token is part of follows, continue execution
			if(CheckFollows("SWITCHCASE"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("SWITCHCASE")){
				SWITCHCASE();
				return;
			}
		}

		if(tokens.get(currentToken).getValue().equals("case"))
			currentToken++;
		else
			error(7);

		RULE_C();

		if(tokens.get(currentToken).getValue().equals(":"))
			currentToken++;
		else
			error(7);


		while (!tokens.get(currentToken).getValue().equals("break") && !tokens.get(currentToken).getValue().equals("case") && !tokens.get(currentToken).getValue().equals("default")&& !tokens.get(currentToken).getValue().equals("}"))
			RULE_BODY();

		if(tokens.get(currentToken).getValue().equals("break")){
			System.out.println("---- BREAK");
			currentToken++;
		}

		if(tokens.get(currentToken).getValue().equals("case")){
			SWITCHCASE();
		}
		if(tokens.get(currentToken).getValue().equals("default")){
			return;
		}
		if(tokens.get(currentToken).getValue().equals("}")){
			return;
		}

		if(tokens.get(currentToken).getValue().equals(";"))
			currentToken++;
		else
			error(7);
	}

	public void RULE_TYPE(){
		System.out.println("---- RULE_TYPE");

		while(!CheckFirsts("TYPE")){
			errorFirst("TYPE");

			//If current token is part of follows, continue execution
			if(CheckFollows("TYPE"))
				return;

			currentToken++;
			if(currentToken >= tokens.size())
				return;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("TYPE")){
				RULE_TYPE();
				return;
			}
		}

		if(!tokens.get(currentToken).getType().equals("KEYWORD"))
			error(8);

		switch(tokens.get(currentToken).getValue()){
			case "int":
				System.out.println("-- INTEGER");
				break;

			case "boolean":
				System.out.println("-- BOOLEAN");
				break;

			case "float":
				System.out.println("-- FLOAT");
				break;

			case "void":
				System.out.println("-- VOID");
				break;

			case "char":
				System.out.println("-- CHAR");
				break;
			case "String":
				System.out.println("-- STRING");
				break;

			default:
				error(8);
		}

		currentToken++;
	}

	public void RULE_PARAMS(){
		System.out.println("----- RULE_PARAMS");

		//Check if current token is part of the set of FIRSTS
		while(!CheckFirsts("PARAMS")){
			errorFirst("PARAMS");

			//If current token is part of follows, continue execution
			if(CheckFollows("PARAMS"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("PARAMS")){
				RULE_PARAMS();
				return;
			}
		}

		//NO PARAMS
		if(tokens.get(currentToken).getValue().equals(")"))
			return;

		//WITH PARAMS
		RULE_TYPE();
		if(tokens.get(currentToken).getType().equals("IDENTIFIER")){
			currentToken++;
			System.out.println("---- IDENTIFIER");
		}
		else{
			error(9);
		}
		while(tokens.get(currentToken).getValue().equals(",")){
			currentToken++;
			System.out.println("---- ,");

			RULE_TYPE();
			if(tokens.get(currentToken).getType().equals("IDENTIFIER")){
				currentToken++;
				System.out.println("---- IDENTIFIER");
			}
			else{
				error(9);
			}
		}
	}

	public void RULE_ASSIGNMENT(){
		System.out.println("---- RULE_ASSIGNMENT");

		//Check if current token is part of the set of FIRSTS
		while(!CheckFirsts("ASSIGNMENT")){
			errorFirst("ASSIGNMENT");

			//If current token is part of follows, continue execution
			if(CheckFollows("ASSIGNMENT"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("ASSIGNMENT")){
				RULE_ASSIGNMENT();
				return;
			}
		}

		if(tokens.get(currentToken).getType().equals("IDENTIFIER")){
			currentToken++;
			System.out.println("---- IDENTIFIER");
		}
		else
			error(10);
		if(tokens.get(currentToken).getValue().equals("=")){
			currentToken++;
			System.out.println("---- =");
		} else{
			error(10);
		}
		RULE_EXPRESSION();
	}

	public void RULE_EXPRESSION() {
		System.out.println("--- RULE_EXPRESSION");

		while(!CheckFirsts("EXPRESSION")){
			errorFirst("EXPRESSION");

			//If current token is part of follows, continue execution
			if(CheckFollows("EXPRESSION"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("EXPRESSION")){
				RULE_EXPRESSION();
				return;
			}
		}

		RULE_X();
		if(tokens.get(currentToken).getValue().equals("|")){
			int counter = 0;
			while(tokens.get(currentToken).getValue().equals("|") && counter < 2){
				currentToken++;
				counter++;
				System.out.println("--- |");
			}
			RULE_X();
		}
	}

	public void RULE_X() {
		System.out.println("---- RULE_X");

		while(!CheckFirsts("X")){
			errorFirst("X");

			//If current token is part of follows, continue execution
			if(CheckFollows("X"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("X")){
				RULE_X();
				return;
			}
		}

		RULE_Y();
		if(tokens.get(currentToken).getValue().equals("&")){
			int counter = 0;
			while(tokens.get(currentToken).getValue().equals("&") && counter < 2){
				currentToken++;
				counter++;
				System.out.println("--- &");
			}
			RULE_Y();
		}
	}

	public void RULE_Y() {
		System.out.println("----- RULE_Y");

		while(!CheckFirsts("Y")){
			errorFirst("Y");

			//If current token is part of follows, continue execution
			if(CheckFollows("Y"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("Y")){
				RULE_Y();
				return;
			}
		}

		while (tokens.get(currentToken).getValue().equals("!")) {
			currentToken++;
			System.out.println("----- !");
		}
		RULE_R();
	}

	public void RULE_R() {
		System.out.println("------ RULE_R");

		while(!CheckFirsts("R")){
			errorFirst("R");

			//If current token is part of follows, continue execution
			if(CheckFollows("R"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("R")){
				RULE_R();
				return;
			}
		}

		RULE_E();

		while (tokens.get(currentToken).getValue().equals("<") || tokens.get(currentToken).getValue().equals(">")) {
			currentToken++;
			System.out.println("------ relational operator");
			RULE_E();
		}

		if(tokens.get(currentToken).getValue().equals("=")){
			int counter = 0;
			while(tokens.get(currentToken).getValue().equals("=") && counter < 2){
				currentToken++;
				counter++;
			}
			if(counter == 2){
				System.out.println("--- relational operator");
				RULE_E();
			}
		}

		if(tokens.get(currentToken).getValue().equals("!")){
			currentToken++;
			if(tokens.get(currentToken).getValue().equals("=")){
				currentToken++;
				System.out.println("--- relational operator");
				RULE_E();
			}
		}
	}

	public void RULE_E() {
		System.out.println("------- RULE_E");

		while(!CheckFirsts("E")){
			errorFirst("E");

			//If current token is part of follows, continue execution
			if(CheckFollows("E"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("E")){
				RULE_E();
				return;
			}
		}

		RULE_A();
		while (tokens.get(currentToken).getValue().equals("-")
				| tokens.get(currentToken).getValue().equals("+")
		) {
			System.out.println("------- + or -");
			if(tokens.get(currentToken+1).getValue().equals("+") || tokens.get(currentToken+1).getValue().equals("-")){
				currentToken++;
				System.out.println("------- ++ or --");
				currentToken++;
				return;
			}
			else {
				currentToken++;
				RULE_A();
			}
		}

	}

	public void RULE_A() {
		System.out.println("-------- RULE_A");

		while(!CheckFirsts("A")){
			errorFirst("A");

			//If current token is part of follows, continue execution
			if(CheckFollows("A"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("A")){
				RULE_A();
				return;
			}
		}

		RULE_B();
		while (tokens.get(currentToken).getValue().equals("/") || tokens.get(currentToken).getValue().equals("*")) {
			currentToken++;
			System.out.println("-------- * or /");
			RULE_B();
		}

	}

	public void RULE_B() {
		System.out.println("--------- RULE_B");

		while(!CheckFirsts("B")){
			errorFirst("B");

			//If current token is part of follows, continue execution
			if(CheckFollows("B"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("B")){
				RULE_B();
				return;
			}
		}

		if (tokens.get(currentToken).getValue().equals("-")) {
			currentToken++;
			System.out.println("--------- -");
		}
		RULE_C();
	}

	public void RULE_C() {
		System.out.println("---------- RULE_C");

		while(!CheckFirsts("C")){
			errorFirst("C");

			//If current token is part of follows, continue execution
			if(CheckFollows("C"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("C")){
				RULE_C();
				return;
			}
		}

		if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
			if(tokens.get(currentToken + 1).getValue().equals("(")){
				RULE_CALL_METHOD();
			}
			else{
				currentToken++;
				System.out.println("---------- IDENTIFIER");
			}
		} else if (tokens.get(currentToken).getType().equals("INTEGER")) {
			currentToken++;
			System.out.println("---------- INTEGER");
		} else if (tokens.get(currentToken).getType().equals("OCTAL")) {
			currentToken++;
			System.out.println("---------- OCTAL");
		} else if (tokens.get(currentToken).getType().equals("HEXADECIMAL")) {
			currentToken++;
			System.out.println("---------- HEXADECIMAL");
		} else if (tokens.get(currentToken).getType().equals("BINARY")) {
			currentToken++;
			System.out.println("---------- BINARY");
		} else if (tokens.get(currentToken).getType().equals("TRUE")) {
			currentToken++;
			System.out.println("---------- TRUE");
		} else if (tokens.get(currentToken).getType().equals("FALSE")) {
			currentToken++;
			System.out.println("---------- FALSE");
		} else if (tokens.get(currentToken).getType().equals("STRING")) {
			currentToken++;
			System.out.println("---------- STRING");
		} else if (tokens.get(currentToken).getType().equals("CHAR")) {
			currentToken++;
			System.out.println("---------- CHAR");
		} else if (tokens.get(currentToken).getType().equals("FLOAT")) {
			currentToken++;
			System.out.println("---------- FLOAT");
		} else if (tokens.get(currentToken).getValue().equals("(")) {
			currentToken++;
			System.out.println("---------- (");
			RULE_EXPRESSION();
			if (tokens.get(currentToken).getValue().equals(")")) {
				currentToken++;
				System.out.println("---------- )");
			} else {
				error(11);
			}
		} else {
			error(11);
		}
	}

	public void RULE_VARIABLE(){
		System.out.println("------ RULE_VARIABLE");

		while(!CheckFirsts("VARIABLE")){
			errorFirst("VARIABLE");

			//If current token is part of follows, continue execution
			if(CheckFollows("VARIABLE"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("VARIABLE")){
				RULE_VARIABLE();
				return;
			}
		}

		RULE_TYPE();
		if(tokens.get(currentToken).getType().equals("IDENTIFIER")){
			currentToken++;
		}

		if(tokens.get(currentToken).getValue().equals(";")){
			return;
		}

		if(!tokens.get(currentToken).getValue().equals("="))
			error(12);

		currentToken++;
		RULE_EXPRESSION();
	}

	public void RULE_WHILE(){
		System.out.println("------ RULE_WHILE");

		while(!CheckFirsts("WHILE")){
			errorFirst("WHILE");

			//If current token is part of follows, continue execution
			if(CheckFollows("WHILE"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("WHILE")){
				RULE_WHILE();
				return;
			}
		}

		if(tokens.get(currentToken).getValue().equals("while")){
			currentToken++;
			System.out.println("------ WHILE");
		}
		else return;
		if(tokens.get(currentToken).getValue().equals("(")){
			currentToken++;
			System.out.println("---- (");
		}
		else {
			error(13);
		}
		RULE_EXPRESSION();
		if(tokens.get(currentToken).getValue().equals(")")){
			currentToken++;
			System.out.println("---- )");
		}
		else {
			error(13);
		}
		if(tokens.get(currentToken).getValue().equals("{")){
			currentToken++;
			System.out.println("---- {");
		}
		else {
			RULE_BODY();
			currentToken -= 1;
			return;
		}

		while(!tokens.get(currentToken).getValue().equals("}")){
			RULE_BODY();
		}
		if(tokens.get(currentToken).getValue().equals("}")){
			System.out.println("---- }");
		}
	}

	public void RULE_IF(){
		System.out.println("------ RULE_IF");

		while(!CheckFirsts("IF")){
			errorFirst("IF");

			//If current token is part of follows, continue execution
			if(CheckFollows("IF"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("IF")){
				RULE_IF();
				return;
			}
		}

		if(tokens.get(currentToken).getValue().equals("if")){
			currentToken++;
			System.out.println("------ IF");
		}
		else error(14);

		if(tokens.get(currentToken).getValue().equals("(")){
			currentToken++;
			System.out.println("---- (");
		}
		else {
			error(14);
		}
		RULE_EXPRESSION();
		if(tokens.get(currentToken).getValue().equals(")")){
			currentToken++;
			System.out.println("---- )");
		}
		else {
			error(14);
		}

		if(tokens.get(currentToken).getValue().equals("{")){
			currentToken++;
			System.out.println("---- {");

			while(!tokens.get(currentToken).getValue().equals("}")) {
				RULE_BODY();
			}

			if(tokens.get(currentToken).getValue().equals("}")){
				currentToken++;
				System.out.println("---- }");
			}
			else {
				error(14);
			}
		} else if(!tokens.get(currentToken).getValue().equals("else")){
			RULE_BODY();
		}
		System.out.println("------ END IF");
		if(tokens.get(currentToken).getValue().equals("else")){
			currentToken++;
			System.out.println("---- ELSE");
			if(tokens.get(currentToken).getValue().equals("if")){
				RULE_IF();
				return;
			}

			if(tokens.get(currentToken).getValue().equals("{")){
				currentToken++;
				System.out.println("---- {");

				while (!tokens.get(currentToken).getValue().equals("}")) {
					RULE_BODY();
				}

				if(tokens.get(currentToken).getValue().equals("}")){
					System.out.println("---- }");
				}
				else {
					error(14);
				}
				System.out.println("------ END } ELSE");
			}
			else {
				RULE_BODY();
				currentToken--;
				System.out.println("------ END ELSE");
			}
		}
	}

	public void RULE_RETURN(){
		System.out.println("------ RULE_RETURN");

		while(!CheckFirsts("RETURN")){
			errorFirst("RETURN");

			//If current token is part of follows, continue execution
			if(CheckFollows("RETURN"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("RETURN")){
				RULE_RETURN();
				return;
			}
		}

		if(tokens.get(currentToken).getValue().equals("return")){
			currentToken++;
			System.out.println("------ RETURN");
		}
		else return;

		if(tokens.get(currentToken).getValue().equals(";")) return;

		RULE_EXPRESSION();
	}

	public void RULE_CALL_METHOD(){
		System.out.println("------ RULE_CALL_METHOD");

		while(!CheckFirsts("CALL_METHOD")){
			errorFirst("CALL_METHOD");

			//If current token is part of follows, continue execution
			if(CheckFollows("CALL_METHOD"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("CALL_METHOD")){
				RULE_CALL_METHOD();
				return;
			}
		}

		if(tokens.get(currentToken).getType().equals("IDENTIFIER")){
			currentToken++;
			System.out.println("------ IDENTIFIER");
		}
		else return;
		if(tokens.get(currentToken).getValue().equals("(")){
			currentToken++;
			System.out.println("---- (");
		}
		else {
			error(15);
		}
		if(!tokens.get(currentToken).getValue().equals(")"))
			RULE_PARAM_VALUES();
		if(tokens.get(currentToken).getValue().equals(")")){
			currentToken++;
			System.out.println("---- )");
		}
		else {
			error(15);
		}
	}

	public void RULE_PARAM_VALUES(){
		System.out.println("------ RULE_PARAM_VALUES");

		while(!CheckFirsts("PARAM_VALUES")){
			errorFirst("PARAM_VALUES");

			//If current token is part of follows, continue execution
			if(CheckFollows("PARAM_VALUES"))
				return;

			currentToken++;
			//If current token is part of firsts, call the rule again
			if(CheckFirsts("PARAM_VALUES")){
				RULE_PARAM_VALUES();
				return;
			}
		}

		RULE_EXPRESSION();
		while (tokens.get(currentToken).getValue().equals(",")){
			currentToken++;
			System.out.println("------ ,");
			RULE_EXPRESSION();
		}
	}

	private boolean CheckFirsts(String key){
		if(tokens.get(currentToken).getType().equals("IDENTIFIER"))
			return first.get(key).contains("identifier");
		else if (tokens.get(currentToken).getType().equals("INTEGER"))
			return first.get(key).contains("integer");
		else if (tokens.get(currentToken).getType().equals("OCTAL"))
			return first.get(key).contains("octal");
		else if (tokens.get(currentToken).getType().equals("HEXADECIMAL"))
			return first.get(key).contains("hexadecimal");
		else if (tokens.get(currentToken).getType().equals("BINARY"))
			return first.get(key).contains("binary");
		else if (tokens.get(currentToken).getType().equals("TRUE"))
			return first.get(key).contains("true");
		else if (tokens.get(currentToken).getType().equals("FALSE"))
			return first.get(key).contains("false");
		else if (tokens.get(currentToken).getType().equals("STRING"))
			return first.get(key).contains("String");
		else if (tokens.get(currentToken).getType().equals("CHAR"))
			return first.get(key).contains("char");
		else if (tokens.get(currentToken).getType().equals("FLOAT"))
			return first.get(key).contains("float");

		var currentTokenValue = tokens.get(currentToken).getValue();

		if(first.get(key).contains("") && !first.get(key).contains(currentTokenValue))
			return true;

        return first.get(key).contains(currentTokenValue);
    }

	private boolean CheckFollows(String key){
		if(tokens.get(currentToken).getType().equals("IDENTIFIER"))
			return follow.get(key).contains("identifier");
		else if (tokens.get(currentToken).getType().equals("INTEGER"))
			return follow.get(key).contains("integer");
		else if (tokens.get(currentToken).getType().equals("OCTAL"))
			return follow.get(key).contains("octal");
		else if (tokens.get(currentToken).getType().equals("HEXADECIMAL"))
			return follow.get(key).contains("hexadecimal");
		else if (tokens.get(currentToken).getType().equals("BINARY"))
			return follow.get(key).contains("binary");
		else if (tokens.get(currentToken).getType().equals("TRUE"))
			return follow.get(key).contains("true");
		else if (tokens.get(currentToken).getType().equals("FALSE"))
			return follow.get(key).contains("false");
		else if (tokens.get(currentToken).getType().equals("STRING"))
			return follow.get(key).contains("String");
		else if (tokens.get(currentToken).getType().equals("CHAR"))
			return follow.get(key).contains("char");
		else if (tokens.get(currentToken).getType().equals("FLOAT"))
			return follow.get(key).contains("float");

		var currentTokenValue = tokens.get(currentToken).getValue();
		return follow.get(key).contains(currentTokenValue);
	}

	private void error(int error) {
		System.out.println("Error " + error +
				" at line " + tokens.get(currentToken));
	}

	private void errorFirst(String key){
		System.out.println("\u001B[35mSyntax Error: Expected member of FIRSTS from " + key + " Got " + tokens.get(currentToken).getValue() + "\u001B[0m");
	}

}

