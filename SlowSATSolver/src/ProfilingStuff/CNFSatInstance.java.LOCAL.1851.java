package ProfilingStuff;
/*
 * Modified from the CnfSatInstance class from http://kahina.org/trac/browser/trunk/src/org/kahina/logic/sat/data/cnf/CnfSatInstance.java?rev=1349
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
	

public class CNFSatInstance
	{
			private int numClauses;
		    private int numVars;
		    private int[] knownAssignments = null;
		    
		    protected int[][] clauses = null;
		    protected int[][] occurrenceMap = null;
		    protected List<Integer> clauseCache = null;
		    
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

			public void addKnownAssignment(int literal) {

				// lazy init of knownAssignments
				if (this.knownAssignments == null) {
					this.knownAssignments = new int[numVars];
				}

				this.knownAssignments[Math.abs(literal) - 1] = literal;
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
		    
		    /**
		     * Returns a set of clauses containing the literal
		     * @param clauses
		     * @param literal
		     * @return
		     */
		    public Set<Set<Integer>> getOccurringClauses(Set<Set<Integer>>clauses, int literal){
		    	Set<Set<Integer>> occurringClauses = new HashSet<Set<Integer>>();
		    	for (Set<Integer> clause: clauses){
		    		if(clause.contains(literal))
		    			occurringClauses.add(clause);
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
		    
		    public int[] getOccurringClauses(List<List<Integer>> clauses, int literal){
		    	int[] occurringClauses = new int[clauses.size()];
		    	for (int i = 0; i < clauses.size(); i++)
		    	{
		    		for (int lit : clauses.get(i))
		    		{
		    			if(lit == literal){
		    				occurringClauses[i] = 1;
		    			}
		    		}
		    	}
		    	return occurringClauses;
		    }
		    
		    public int getNumOccurringClauses(List<List<Integer>> clauses, int literal){
		    	int ret = 0;
		    	for(int occurrence : getOccurringClauses(clauses, literal)){
		    		ret += occurrence;
		    	}
		    	return ret;
		    }		    		    
		    
		    public Set<Set<Integer>> computeSetCache(int[][]clauses){
		    	Set<Set<Integer>> setCache = new HashSet<Set<Integer>>();
		    	for(int[] clause: clauses){
		    		Set<Integer> newClause = new HashSet<Integer>();
		    		for(int lit : clause){
		    			if (lit != 0)
		    				newClause.add(lit);
		    		}
		    		setCache.add(newClause);
		    	}
		    	return setCache;
		    }
		    
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
			private static int givenCounter = 0;
			private static long totalGivenTime = 0;
			public int[][] givenVar(int[][] clauses, int var){
//				System.out.println("Running given...");
				long started = System.nanoTime();

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
				givenCounter+=1;
				long time = System.nanoTime();
				long timeTaken = time - started;
				totalGivenTime += timeTaken;
//				System.out.println("givenVar() "+givenCounter+" ran in: " + timeTaken/1000000.0 + "ms");
//				System.out.println("total time: " + totalGivenTime/1000000.0 + "ms");
				return newClauses;
		    }

			/**
			 * Takes in a formula cached in a set of sets. MUTATES THE ORIGINAL FORMULA
			 * according to substitution of the given variable.
			 * @param clauses
			 * @param var
			 * @return
			 */
			public Set<Set<Integer>> givenVar(Set<Set<Integer>> clauses, int var){
		    	
				//remove instances of the negative of the literal
				for(Set<Integer> clause: clauses){
		    		Set<Integer> litToRemove = new HashSet<Integer>();
		    		for(int lit : clause){
		    			if (lit == -var)
		    				litToRemove.add(lit);
		    		}
		    		for(int removeLit : litToRemove){
		    			clause.remove(removeLit);
		    		}
		    	}
				
				//remove clauses containing the literal
				Set<Set<Integer>> clausesToRemove = new HashSet<Set<Integer>>();
				for(Set<Integer> clause: clauses){
					if(clause.contains(var)){
						clausesToRemove.add(clause);
					}
				}
				
				for(Set<Integer> clauseToRemove: clausesToRemove){
					clauses.remove(clauseToRemove);
				}
				
				return clauses;
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
		     * Operates on a cached set representation of clauses
		     * @param clauses
		     * @return
		     */
		    public boolean eliminateUnitClauses(Set<Set<Integer>> clauses){
		    	boolean unitClauseFound = true;
		    	boolean changesMade = false;
		    	
		    	Set<Integer> variablesToRemove = new HashSet<Integer>();
		    	while(unitClauseFound) { //always called on the first iteration
		    		unitClauseFound = false; //assume unit clause not found
		    		for(Set<Integer> clause: clauses){
		    			if(clause.size() == 1){
		    				variablesToRemove.add(clause.iterator().next());
		    			}
		    		}
		    	}
		    	if(variablesToRemove.size() > 0){
    				changesMade = true;
    				for(int var : variablesToRemove){
    					givenVar(clauses, var);
    				}
		    	}
		    	return changesMade;
		    }
		    /**
		     * Operates on a cached set representation of clauses.
		     * Returns true if a change is made
		     * @param clauses
		     * @return
		     */
		    public boolean eliminatePureLiterals(Set<Set<Integer>> clauseSet){
		    	boolean changesMade = false;
		    	for(int i = 1; i <= numVars; i++){
//		    		System.out.println("foo " + i);
		    		int numPosOccurr = getOccurringClauses(clauseSet, i).size();
		    		int numNegOccurr = getOccurringClauses(clauseSet, -i).size();
		    		if(numPosOccurr == 0 && numNegOccurr != 0){ //if the literal i does not appear
		    			givenVar(clauseSet, -i);
		    			changesMade = true;
		    		}
		    		else if(numPosOccurr != 0 && numNegOccurr == 0){
		    			givenVar(clauseSet, i);
		    			changesMade = true;
		    		}
		    	}
		    	
		    	return changesMade;
		    }

		    
		    /**
		     * Dependent on numVars
		     * @param clauses
		     * @return
		     */
		    public int[][] eliminatePureLiterals(int[][] clauses){
		    	int[][] newFormula = clauses;
		    	for(int i = 1; i <= numVars; i++){
		    		int numPosOccurr = getNumOccurringClauses(newFormula, i);
		    		int numNegOccurr = getNumOccurringClauses(newFormula, -i);
//		    		System.out.println("FOOBAR\t" + i + "\t" + numPosOccurr);
//		    		System.out.println("FOOBAR\t" + -i + "\t" + numNegOccurr);
		    		if(numPosOccurr == 0 && numNegOccurr != 0){ //if the literal i does not appear
		    			newFormula = givenVar(newFormula, -i);
		    		}
		    		else if(numPosOccurr != 0 && numNegOccurr == 0){
		    			newFormula = givenVar(newFormula, i);
		    		}
		    	}
		    	
		    	return newFormula;
		    }

		    /**
		     * Uses array implementation. Requires allocation and deallocation of 
		     * large amounts of space due to the array implementations that remove the variables.
		     * 
		     * @param clauses
		     * @return
		     */
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
		    			
		    			for(int[] l: newFormula){
		    				for(int i : l){
//		    					System.out.print(i + " ");
		    				}
//		    				System.out.println("");
		    			}
//		    			System.out.println("...");
		    		}
		    	}
		    	return newFormula;
		    }
	
		    /**
		     * Takes in a cachedClause list and modifies it directly for faster processing
		     * 
		     * @param cachedClause
		     * @return
		     */
		    public Set<Set<Integer>> cachedSimplify(int[][] clauses){
		    	Set<Set<Integer>> cache = computeSetCache(clauses);
		    	while(eliminatePureLiterals(cache) || eliminateUnitClauses(cache)){
		    	}
		    	return cache;
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
		    
//			public List<List<Integer>>givenVar(List<List<Integer>> clauses, int var){
//	    	int[] posOccurrences = getOccurringClauses(clauses, var);
//
//	    	//remove occurrences of negatives
//	    	for(List<Integer> clause : clauses){
//	    		clause.remove(new Integer(-var));
//	    	}
//	    	
//	    	for(int clause = 0; clause < clauses.size(); clause++){
//	    		if(posOccurrences[clause] != 0){
//
//	    		}
//	    	}
//	    	
//	    	return clauses;
//	    }
		    public String toString(){
		    	return clauses.toString();
		    }
	}
