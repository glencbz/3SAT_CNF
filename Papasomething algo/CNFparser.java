package project2d;


/*
 * Modified from the CNF parser found on
 * http://kahina.org/trac/browser/trunk/src/org/kahina/logic/sat/io/cnf/DimacsCnfParser.java?rev=1349
 * 
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CNFparser
	{
	public static CnfSatInstance parseDimacsCnfFile(String fileName)
		{
			CnfSatInstance sat = new CnfSatInstance();
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
	                return sat;
	            }
	            if (!params[1].equals("cnf"))
	            {
	                System.err.println("ERROR: Parsing a non-CNF Dimacs file with the Dimacs CNF parser!");
	                System.err.println("       Returning empty SAT instance!");
	            }
	            sat.setNumVars(Integer.parseInt(params[2]));
	            sat.setNumClauses(Integer.parseInt(params[3]));
	           
	            String currentLine;
	            String[] tokens;
	            List<Integer> currentClause = new LinkedList<Integer>();
	            //read in clauses and comment lines which encode symbol definitions
	            while (in.hasNext())
	            {
	                currentLine = in.nextLine();
	                tokens = currentLine.split("\\s");
	                if (!tokens[0].equals("c")&&!tokens[0].equals(""))
	                {
	                    for (int i = 0; i < tokens.length; i++)
	                    {
	                        Integer literal = Integer.parseInt(tokens[i]);                   	
	                        if (literal == 0)
	                        {
	                            sat.getClauses().add(currentClause);
	                            currentClause = new LinkedList<Integer>();
	                        }
	                        else
	                        {
	                        	if(literal < 0) 
	        						literal = literal + 1;
	        					else 
	        						literal = literal - 1;
	                            currentClause.add(literal);
	                        }
	                    }
	                   
	                }
	            }
	            if (!currentClause.isEmpty()) 
	            	sat.getClauses().add(currentClause);
	            in.close();
	        }
	        catch (FileNotFoundException e)
	        {
	            System.err.println("ERROR: Dimacs CNF file not found: " + fileName);
	            System.err.println("       Returning empty SAT instance!");
	        }
	        return sat;
	    }
	}