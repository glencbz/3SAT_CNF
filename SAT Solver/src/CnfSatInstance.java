/*
 * Modified from the CnfSatInstance class from http://kahina.org/trac/browser/trunk/src/org/kahina/logic/sat/data/cnf/CnfSatInstance.java?rev=1349
 */

import java.util.List;
	

public class CnfSatInstance
	{		
			
			private int numClauses;
		    private int numVars;
		    private int[] knownAssignments = null;
		    //TODO: possible time improvement, replace List implementation with Array implementation
		    protected int[][] originalClauses;
		    protected int[][] clauses = null;
		    protected int[][] occurrenceMap = null;
		    protected List<Integer> clauseCache = null;
		    
		    protected SCCGraph sccGraph = null;
		    		    
		    //  entries [0,...,numVars-1] for positive literals
		    //  entries [numVars,...,2*numVars-1] for negative literals
	   
		    public CnfSatInstance()
		    {
		    	setNumClauses(0);
		    	setNumVars(0);
		    	clauses = null;
		    	originalClauses = null;
		    	occurrenceMap = null;
		    }
		    
		    public CnfSatInstance(int[][] clauses, int numClauses, int numVars){
		    	this.clauses = clauses;
		    	this.numClauses = numClauses;
		    	this.numVars = numVars;
		    }
		    
		    public void initialise(){
	    		this.knownAssignments = new int[numVars];
		    	this.occurrenceMap=computeOccurrenceMap(this.clauses,this.numVars);
		    	this.clauses=simplify(clauses);
		    }
		    		    
		    //Computes the strongly connected components in the implication graph
		    public void computeSCCGraph(){
		    	sccGraph = new SCCGraph(getNumVars() * 2 + 1,clauses,knownAssignments);
		    }
		    
		    public boolean solve(){
		    	if(this.hasEmptyClauses()==true){
		    		return false;
		    	}
		    	else if(this.hasNoClauses()==true){
		    		System.out.println("1) FORMULA SATISFIABLE");
		    		for (int i : this.knownAssignments){
		    			System.out.print((i==0?1:i)+" ");
		    		}
		    		System.out.println();
		    		return true;
		    	}
		    	else{
			    	if(this.sccGraph == null){
			    		computeSCCGraph();
			    	}
			    	boolean out = this.sccGraph.evaluate();
			    	return out;
		    	}
		    }
		    
		    public boolean verifySolution(){
		    	for(int[] clause : originalClauses){
		    		int clauseSum = 0;
		    		for(Integer term: clause){
		    			clauseSum += this.sccGraph.knownAssignments[term>0?term-1:this.getNumVariables()-term-1];
		    		}
		    		if(clauseSum==0){
		    			System.out.println(false);
		    			return false;
		    		}
		    	}
		    	System.out.println(true);
		    	return true;
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
		    
		    public int getNumClauses()
		    {
		    	return numClauses;
		    }
	
		    public int getNumVariables()
		    {
		    	return getNumVars();
		    }
	
		    public int[][] getClauses()
		    {
		    	return clauses;
		    }
		    
		    public int[][] getOriginalClauses() {
				return originalClauses;
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
		    			newFormula = givenVar(newFormula, -(i + 1));
		    		}
		    		else if(numPosOccurr != 0 && numNegOccurr == 0){
		    			newFormula = givenVar(newFormula, i + 1);
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
		    		}
		    	}
		    	return newFormula;
		    }
		    private boolean hasEmptyClauses(){
		    	for(int[] clause : clauses){
		    		if(clause.length==0){
		    			System.out.println("FORMULA UNSATISFIABLE");
		    			return true;
		    		}
		    	}
		    	return false;
		    }
		    
		    private boolean hasNoClauses(){
		    	return this.clauses.length == 0;
		    }
		    
		    public String toString(){
		    	return clauses.toString();
		    }
	}
