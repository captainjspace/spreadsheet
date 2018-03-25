import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

class Parser {

	static int SPACE = 32;
	static String s="(((9/3*(7*(5+(((7+5)+2)+((7+5)-3)+(9+7)))/(7+5)))+10)*100)*(9/3*(7*(5+(((7+5)+2)+((7+5)-3)+(9+7)))/(7+5)))";

	
	public static void main (String [] args) {

		Parser p = new Parser();
		
		try { 
			List<ExpVal> nodeTree = p.buildTree(s);
			
			int stack = 1;
			
			for (ExpVal n : nodeTree) {
			
				String space = new String(new char[n.stackLocation*2]).replace('\0', '_');
				if (stack != n.stackLocation) {
					stack = n.stackLocation;
					System.out.printf("%n%s", space );
				}
				System.out.printf(" %s %s", n.nodeType, n.element);
				System.out.printf(" %s %s", n.nodeType, n.element);
			}
			
			int maxDepth = nodeTree
					.stream()
					.max((e1,e2) -> e1.stackLocation > e2.stackLocation ? 1 : -1).get().stackLocation;

			System.out.printf("MaxDepth: %d%n", maxDepth);
			calc(nodeTree,maxDepth);
		} catch (Exception i) {
			i.printStackTrace();
		}
	}

	public static double calc(List<ExpVal> n, int maxDepth) {
		//get bottom of tree
		List<ExpVal> workList = n.stream().filter( v -> (v.stackLocation == maxDepth)).collect(Collectors.toList());
		for (ExpVal v: workList) {
			System.out.printf(" %s %s", v.nodeType, v.element);
			switch (v.nodeType)
			case MathDelim.O_P:
			case MathDelim.DIV:
				Function f = new Function
			case MathDelim.MUL:
			case MathDelim.PLUS:
			case MathDelim.MINUS:
				//do pluse *-1
			case MathDelim.C_P:
				eval();
		}
		return 0.0;
	}
	
	public enum MathDelim {

		O_P('('),
		C_P(')'),
		DIV('/'),
		MUL('*'),
		PLUS('+'),
		MINUS('-'),
		VALUE(' ');

		char op;

		public char op() {
			return this.op;
		}

		MathDelim(char s) {
			this.op=s;
		}

		public String sOp() {
			return String.valueOf(this.op);
		}

		public static MathDelim getByString(String s) {
			for (MathDelim m : MathDelim.values()) {
				if (m.sOp().equals(s)) {
					return m;
				}
			}
			return null;
		}
	}

	public class ExpVal {
		
		public int stackLocation;
		public MathDelim nodeType;
		public String element;
		public ExpVal(int s, MathDelim d, String t) { this.stackLocation=s; this.nodeType = d; this.element=t ;};
		public String toString() {
			return String.format("%d --- %s %n", this.stackLocation, this.element);
		}
	}

	public List<ExpVal> buildTree(String s) throws Exception {

		final StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(s));
		final List<String> tokens = new ArrayList<>();
		//final Map<Integer,ExpVal> stacky = new TreeMap<>();
		final List<ExpVal> stacky= new ArrayList<>();

		//add delimeters
		Arrays.asList(MathDelim.values()).stream().forEach( md -> {
			tokenizer.ordinaryChar(md.op());
		});

		int exprStack=0;
		String tokenData="";
		int tkIdx=0;

		while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
			System.out.println(tokenizer.toString());
			tkIdx++;	
			switch(tokenizer.ttype) {
			case StreamTokenizer.TT_NUMBER:
				stacky.add(new ExpVal(exprStack, MathDelim.VALUE, String.valueOf(tokenizer.nval)));
				break;
			case StreamTokenizer.TT_WORD:
				throw new RuntimeException("String Values still present");
			default:  // operator
				if (tokenizer.ttype == 40) exprStack++; 
				tokens.add(String.valueOf((char) tokenizer.ttype));
				String op = String.valueOf((char)tokenizer.ttype);
				stacky.add(new ExpVal(exprStack, MathDelim.getByString(op), op));
				if ( tokenizer.ttype == 41 ) exprStack--;
			}
		}
		return stacky;
	}
}
