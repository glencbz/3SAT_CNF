package sat;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import sat.env.*;
import sat.formula.*;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();



	
	// TODO: add the main method that reads the .cnf file and calls SATSolver.solve to determine the satisfiability
    
    public static void main(String args[]){
    	String fileName = "src/s8.cnf";
    	Formula CNF = parseCNF(fileName);
		System.out.println(CNF);
    }
    
	// TODO: put your test cases for SATSolver.solve here
	/**
	 * Parses the given file into a Formula to be solved in SATsolver.solve()
	 * @param fileName File name of .cnf to be parsed, in String format
	 * @return Formula specified by file
	 */
    public static Formula parseCNF(String fileName){
    	Formula CNF = new Formula();
		try
		{
            Scanner in = new Scanner(new File(fileName));
            String problemLine = in.nextLine();
            //ignore comment header
            while (problemLine.matches("c.*"))
            {
                problemLine = in.nextLine();
            }
            //process the problem line
            String[] params = problemLine.split("\\s");

            if (!params[0].equals("p"))
            {
                System.err.println("ERROR: Dimacs CNF file appears to miss the problem line!");
                System.err.println("       Returning empty SAT instance!");
                in.close();
                return null;
            }
            if (!params[1].equals("cnf"))
            {
                System.err.println("ERROR: Parsing a non-CNF Dimacs file with the Dimacs CNF parser!");
                System.err.println("       Returning empty SAT instance!");
            }
            
//			int numVars = Integer.parseInt(params[2]);
//          int numClauses= Integer.parseInt(params[3]);
           
            String currentLine;
            String[] tokens;
            
            Clause currentClause = new Clause();

            while (in.hasNext())
            {
                currentLine = in.nextLine();
                if(!currentLine.isEmpty()){
	                tokens = currentLine.split("\\s+");
	                
	                for (int i = 0; i < tokens.length; i++)
	                {
	                    int literal = Integer.parseInt(tokens[i]);
	                    if (literal == 0)
	                    {
	                        CNF = CNF.addClause(currentClause);
	                        currentClause = new Clause();
	                    }
	                    else
	                    {	
	                    	if(literal > 0){
	                    		currentClause = currentClause.add(PosLiteral.make(String.valueOf(literal)));
	                    	}
	                    	else{
	                    		currentClause = currentClause.add(NegLiteral.make(String.valueOf(Math.abs(
	                    				literal))));
	                    	}
	                    }
	                }
	               
	            }
            }
            if (!currentClause.isEmpty()) 
            	CNF.addClause(currentClause);
            in.close();
        }
        catch (FileNotFoundException e)
        {
            System.err.println("ERROR: Dimacs CNF file not found: " + fileName);
            System.err.println("       Returning empty SAT instance!");
        }
		return CNF;
    }
    
    @Test
    public void testSATSolver1(){
    	// (a v b)
    	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())  
    			|| Bool.TRUE == e.get(b.getVariable())	);
    	
    	
    }
    
    
    @Test
    public void testSATSolver2(){
    	// (~a)
    	Environment e = SATSolver.solve(makeFm(makeCl(na)));
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
    	
    }
    
    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
    
    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
    
    
    
}