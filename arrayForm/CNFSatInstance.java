package arrayForm;
/*
 * Modified from the CnfSatInstance class from http://kahina.org/trac/browser/trunk/src/org/kahina/logic/sat/data/cnf/CnfSatInstance.java?rev=1349
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
	

public class CNFSatInstance
	{
			private int numClauses;
		    private int numVars;
		    private int[] knownAssignments = null;
		    
		    protected int[][] clauses = null;
		    protected int[][] occurrenceMap = null;
		    
		    public CNFSatInstance(){
		    	setNumClauses(0);
		    	setNumVars(0);
		    	clauses = null;
		    	occurrenceMap = null;
		    }
		    
		    public CNFSatInstance(int[][] clauses, int numClauses, int numVars){
		    	this.clauses = clauses;
		    	this.numClauses = numClauses;
		    	this.numVars = numVars;
		    }
		    
		    public int getNumClauses()
		    {
		    	return numClauses;
		    }
	
		    public int[][] getClauses()
		    {
		    	return clauses;
		    }
	
		    public void setNumVars(int numVars)
		    {
		    	this.numVars = numVars;
		    }
	
		    public int getNumVars()
		    {
		    	return numVars;
		    }
	
		    public void setNumClauses(int numClauses)
		    {
		    	this.numClauses = numClauses;
		    }

		    public int[] getKnownAssignments() {
				return knownAssignments;
			}
		    
		    /**
		     * Returns an array where each element represents the 
		     * presence of the literal in the clause
		     * 
		     * @param clauses
		     * @param literal
		     * @return
		     */
		    public int[] getOccurringClauses(int[][] clauses, int literal){
		    	int[] occurringClauses = new int[clauses.length];
		    	for (int i = 0; i < clauses.length; i++)
		    	{
		    		for (int lit : clauses[i])
		    		{
		    			if(lit == literal){
		    				occurringClauses[i] = 1;
		    			}
		    		}
		    	}
		    	return occurringClauses;
		    }
		    
		    public int getNumOccurringClauses(int[][] clauses, int literal){
		    	int ret = 0;
		    	for(int occurrence : getOccurringClauses(clauses, literal)){
		    		ret += occurrence;
		    	}
		    	return ret;
		    }
		    
		    public int getNumOccurringClauses(int[] occurringClauses, int literal){
		    	int ret = 0;
		    	for(int occurrence : occurringClauses){
		    		ret += occurrence;
		    	}
		    	return ret;
		    }
		    
		    /**
		     * Creates a 2D array Map of numClauses by numVars * 2
		     * 0 denotes that the literal is not present in that clause
		     * 1 denotes that the literal is present
		     * Has absolutely no dependence on the calling instance
		     * 
		     * @return An occurrence map corresponding to the clauses, numClauses and numVars given
		     */
			public int[][] computeOccurrenceMap(int[][] clauses, int numVars)
		    {
			    //  entries [0,...,numVars-1] for positive literals
			    //  entries [numVars,...,2*numVars-1] for negative literals
				
		    	occurrenceMap = new int[clauses.length][numVars * 2];
		    	for (int i = 0; i < clauses.length; i++)
		    	{
		    		for (int literal : clauses[i])
		    		{
		    			int pos = literal > 0 ? literal: numVars + Math.abs(literal);
		    			occurrenceMap[i][pos - 1] = 1;
		    		}
		    	}
		    	return occurrenceMap;
		    }
		    
			public int[] occurrenceNum(int[][] occurrenceMap){
				int[] occurrenceNum = new int[occurrenceMap[0].length];
				for(int clause = 0; clause < occurrenceMap.length; clause++){
					for(int var = 0; var < occurrenceMap[0].length; var++){
						if(occurrenceMap[clause][var] == 1)
							occurrenceNum[var]++;
					}
				}
				return occurrenceNum;
			}

			public int sizeClause(int[] clause){
				int ret = 0;
				for(int i : clause)
					ret += i != 0 ? 1 : 0;
				return ret;
			}
			
			public int getVarFromUnit(int[] clause){
				if (sizeClause(clause) != 1)
					return 0;
				else{
					for(int i : clause){
						if(i != 0)
							return i;
					}
				}
				return 0;
			}
			
			/**
			 * Returns a given formula assuming the assignment denoted by var
			 * 
			 * @param clauses
			 * @param var
			 * @return
			 */
			public int[][] givenVar(int[][] clauses, int var){
		    	int[] posOccurrences = getOccurringClauses(clauses, var);
		    	
		    	int numNewClauses = clauses.length - getNumOccurringClauses(posOccurrences, var);
		    	int[][] newClauses = new int [numNewClauses][3];
		    	
		    	int[][] negRemoved = new int[clauses.length][3];
		    	for(int clause = 0; clause < negRemoved.length; clause++){
		    		for(int lit = 0; lit < 3; lit++){
		    			negRemoved[clause][lit] = clauses[clause][lit] == -var ?  0 : clauses[clause][lit];
		    		}
		    	}
		    	
		    	int writeClausePos = 0;
		    	for(int clause = 0; clause < negRemoved.length; clause++){
		    		if(posOccurrences[clause] == 0){
		    			for(int lit = 0; lit < 3; lit++){
		    				newClauses[writeClausePos][lit] = negRemoved[clause][lit];
		    			}
		    			writeClausePos++;
		    		}
		    	}
		    	
		    	return newClauses;
		    }

		    /**
		     * Removes unit clauses from the formula
		     */
		    public int[][] eliminateUnitClauses(int[][] clauses){
		    	boolean unitClauseFound = true;
		    	int[][] newFormula = clauses;
		    	while(unitClauseFound) { //always called on the first iteration
		    		unitClauseFound = false; //assume unit clause not found
		    		for(int[] clause : newFormula){
		    			int var = getVarFromUnit(clause);
		    			if(var != 0){
		    				newFormula = givenVar(newFormula, var);
		    				unitClauseFound = true;
		    			}
		    		}
		    	}
		    	return newFormula;
		    }
		   
		    /**
		     * Dependent on numVars
		     * @param clauses
		     * @return
		     */
		    public int[][] eliminatePureLiterals(int[][] clauses){
		    	int[][] newFormula = clauses;
		    	for(int i = 0; i < numVars; i++){
		    		int numPosOccurr = getNumOccurringClauses(newFormula, i);
		    		int numNegOccurr = getNumOccurringClauses(newFormula, -i);
		    		if(numPosOccurr == 0 && numNegOccurr != 0){ //if the literal i does not appear
		    			System.out.println(i);
		    			newFormula = givenVar(newFormula, -(i + 1));
		    		}
		    		else if( == 0){
		    			System.out.println(-i);
		    			newFormula = givenVar(newFormula, i + 1);
		    		}
		    	}
		    	
		    	return newFormula;
		    }

		    public int[][] simplify(int[][] clauses){
		    	boolean changesMade = true;
		    	int[][] newFormula = clauses;
		    	while(changesMade){
		    		changesMade = false;
		    		int[][] noUnitClauses = eliminateUnitClauses(newFormula);
		    		if(noUnitClauses != newFormula){ //equality can be checked this way because givenVar() and elimnateUnitClauses() only change addresses if changes are made
		    			newFormula = noUnitClauses;
		    			changesMade = true;
		    		}
		    		int[][] noPures = eliminatePureLiterals(newFormula);
		    		if(noPures != newFormula){ //equality can be checked this way because givenVar() and elimnatePureLiterals() only change addresses if changes are made
		    			newFormula = noPures;
		    			changesMade = true;
		    		}
		    	}
		    	return newFormula;
		    }
		    
		    /*			
			public void initialise(){
		    	//TODO: check for empty formula
		    	knownAssignments = new int[numVars];
		    	computeOccurrenceMap();
		    	simplify();
		    	computeOccurrenceMap();
		    }
		    

		    
		    *//**
		     * Removes unit clauses from the sentence by repeatedly calling the givenVar() function
		     * for every unit clause found. Returns true if changes have been made
		     * 
		     * @return True if changes were made
		     *//*
		    public boolean eliminateUnitClauses(){
		    	boolean unitClauseFound = true;
		    	while(unitClauseFound) { //always called on the first iteration
		    		unitClauseFound = false; //assume unit clause not found
			    	for(List<Integer> l : clauses){
			    		if(l.size() == 1){
			    			knownAssignments[l.get(0) > 0 ? l.get(0) - 1 : Math.abs(l.get(0)) - 1] = l.get(0) < 0 ? -1: 1 ;
			    			this.clauses= givenVar(l.get(0));
			    			this.numClauses = clauses.size();
			    			computeOccurrenceMap();
			    			unitClauseFound = true;
			    			break;
			    		}
		    		}
		    	}
		    	return unitClauseFound;
		    }
		    
		    *//**
		     * Examines the formula for all occurrences of each variable.
		     * If the variable occurs but not its negation or vice versa,
		     * the variable is added to the array of known assignments and
		     * removed from the formula.
		     * 
		     * @return Whether a triviality has been found
		     *//*
		    private boolean eliminateTrivialities(){
		    	boolean trivialityFound = false;
		    	for(int i = 0; i < numVars; i++){
		    		if(!occurrenceMap[i].isEmpty() && occurrenceMap[i + numVars].isEmpty()){
		    			this.clauses = givenVar(i + 1);
		    			this.numClauses = clauses.size();
		    			computeOccurrenceMap();
		    			trivialityFound = true;
		    		}else if(occurrenceMap[i].isEmpty() && !occurrenceMap[i + numVars].isEmpty()){
		    			this.clauses = givenVar(-i - 1);
		    			this.numClauses = clauses.size();
		    			computeOccurrenceMap();
		    			trivialityFound = true;
		    		}
		    	}
		    	return trivialityFound;
		    }

		    //TODO: Add empty clause checking
		    private void simplify(){
		    	while(eliminateUnitClauses() || eliminateTrivialities()){
		    		
		    	}
		    }
		    
		    public boolean hasEmptyClauses(){
		    	for(List<Integer> l : clauses){
		    		if (l.isEmpty())
		    			return true;
		    	}
		    	return false;
		    }
		    
 		    public int randomAssignment1(){
		    	return Math.random() > 0.5 ? 1 : -1;
		    }
		   */ 		
		    public String toString(){
		    	return clauses.toString();
		    }
	}
