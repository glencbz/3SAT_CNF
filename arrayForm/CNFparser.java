/*
 * Modified from the CNF parser found on
 * http://kahina.org/trac/browser/trunk/src/org/kahina/logic/sat/io/cnf/DimacsCnfParser.java?rev=1349
 * 
 * 
 */
package arrayForm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CNFparser
	{
	public static CNFSatInstance parseDimacsCnfFile(String fileName)
		{
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
	            
	            int numVars = Integer.parseInt(params[2]);
	            int numClauses = Integer.parseInt(params[3]);
	            //create an array to hold the clauses for 3 SAT
	            int[][] clauses = new int[numClauses][3];
	            int clauseCount = 0;
	            int currentClauseCount = 0;
	            int literal = 0;
	            String currentLine;
	            String[] tokens;
	            
	            //read in clauses and comment lines which encode symbol definitions
	            while (in.hasNext())
	            {
	                currentLine = in.nextLine();
	                tokens = currentLine.split("\\s+");

	                if (!tokens[0].equals("c") && !currentLine.matches("\\s+"))
	                {
	                    for (int i = 0; i < tokens.length; i++)
	                    {
	                    	try{
	                        literal = Integer.parseInt(tokens[i]);
	                    	}catch(NumberFormatException e){
	                    		continue;
	                    	}
	                    	
	                    	if (literal == 0){
	                        	clauseCount++;
	                        	currentClauseCount = 0;
	                        }
	                        else
	                        {
	                        	try{
	                            clauses[clauseCount][currentClauseCount] = literal;
	                            currentClauseCount++;
	                        	}catch(ArrayIndexOutOfBoundsException e){
	                        		System.out.println("Number of literals did not match 3 SAT.");
	                        		in.close();
	                        		return null;
	                        	}
	                        }
	                    }
	                   
	                }
	            }
	            in.close();
	            if (literal != 0)
	            	clauseCount++;
				if(numClauses != clauseCount){
					System.out.println("Number of clauses did not match expected number");
					return null;
				}
				
		        return new CNFSatInstance(clauses, numClauses, numVars);
			}
	        catch (FileNotFoundException e)
	        {
	            System.err.println("ERROR: Dimacs CNF file not found: " + fileName);
	            System.err.println("       Returning empty SAT instance!");
	            return null;
	        }
	    }
	}