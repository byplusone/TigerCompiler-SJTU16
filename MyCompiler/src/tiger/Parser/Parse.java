package tiger.Parser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import tiger.Absyn.Exp;
import tiger.errormsg.ErrorMsg;
import tiger.Parser.Grm;
import tiger.Parser.Yylex;


public class Parse {
	public ErrorMsg errorMsg;
	
	public Parse(ErrorMsg err) {
		errorMsg = err;
	}

	public Exp parse(String filename) {
		InputStream inp;
		try {
			inp = new java.io.FileInputStream(filename);
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename);
			return null;
		}
		try {
			Yylex lexer = new Yylex(inp, errorMsg);
			Grm parser = new Grm(lexer, errorMsg);
			parser.parse();
			Exp absyn = parser.parseResult;
			if (ErrorMsg.anyErrors) {
				System.out.println("Error occured, Stop compiling");
				return null;
			}
			else return absyn;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				inp.close();
			}
			catch (java.io.IOException e) {
			}
		}
	}
}
