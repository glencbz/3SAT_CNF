/*
 * Modified from the CnfSatInstance class from http://kahina.org/trac/browser/trunk/src/org/kahina/logic/sat/data/cnf/CnfSatInstance.java?rev=1349
 */

package UsingHQSimplify;

import java.util.HashSet;
import java.util.Set;
	

public class CnfSatInstance
	{		

			//Contains data regarding the original formula given to us
			private int numClauses;
		    private int numVars;
		    
		    //Contains the boolean assignments we give to each variable
		    private int[] knownAssignments = null;

		    //Contains the formula given to us
		    protected int[][] originalClauses;
		    //Contains the modified formula as we process our formula
		    protected int[][] clauses = null;
		    //Stores the occurring clauses of each literal in the formula
		    protected int[][] occurrenceMap = null;
		    //Stores the occurrence of each literal in the formula
		    protected int[] occurrenceCount = null;
		    //Tracks if any clause is removed during the simplification process
		    protected boolean[] clauseRemoved = null;
		    
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
		    	this.originalClauses = clauses;
		    	this.numClauses = numClauses;
		    	this.numVars = numVars;
		    }
		    
		    public void initialise(){
	    		this.knownAssignments = new int[numVars];
		    	computeOccurrenceMap(this.clauses,this.numVars);
	    		this.simplify();
		    }
		    		    
		    //Computes the strongly connected components in the implication graph
		    public void computeSCCGraph(){
		    	sccGraph = new SCCGraph(getNumVars() * 2 + 1,clauses,knownAssignments,this.clauseRemoved);
		    }
		    
		    public boolean solve(){
		    	if(this.hasEmptyClauses()==true){
		    		return false;
		    	}
		    	else if(this.hasNoClauses()==true){
		    		System.out.println("FORMULA SATISFIABLE");

		    		for (int i = 0 ; i < this.knownAssignments.length ; i++){
		    			System.out.print(this.knownAssignments[i]<0?0:knownAssignments[i]);
		    			System.out.print(" ");
			    		if(i%1000 == 999){
			    			System.out.println();
			    		}
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
		    
		    public int getNumOccurringClauses(int literal){
		    	return this.occurrenceCount[literal<0?Math.abs(literal)+getNumVars()-1:literal-1];
		    }
		    
		    /**
		     * Creates a 2D array Map of numClauses by numVars * 2
		     * 0 denotes that the literal is not present in that clause
		     * 1 denotes that the literal is present
		     * Has absolutely no dependence on the calling instance
		     * 
		     * @return An occurrence map corresponding to the clauses, numClauses and numVars given
		     */
			public void computeOccurrenceMap(int[][] clauses, int numVars)
		    {
			    //  entries [0,...,numVars-1] for positive literals
			    //  entries [numVars,...,2*numVars-1] for negative literals
				clauseRemoved = new boolean[clauses.length];
				occurrenceCount = new int[numVars*2];
		    	occurrenceMap = new int[numVars*2][clauses.length];
		    	for (int i = 0; i < clauses.length; i++)
		    	{
		    		for (int literal : clauses[i])
		    		{
		    			int pos = literal > 0 ? literal: numVars + Math.abs(literal);
		    			occurrenceMap[pos-1][i] = 1;
		    			occurrenceCount[pos-1]++;
		    		}
		    	}
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

			public void addKnownAssignment(int literal) {

				if (literal > 0) {
					knownAssignments[Math.abs(literal)-1] = 1;
				}

				if (literal < 0) {
					knownAssignments[Math.abs(literal)-1] = -1;
				}

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
		    public int[] getOccurringClauses(int literal){
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
			public void givenVar(int var){

				this.knownAssignments[var<0?-var-1:var-1] = var<0?-1:1;
				this.occurrenceCount[var<0?Math.abs(var)+getNumVars()-1:var-1] = 0;
				int neg = -var;
				
		    	int[] posOccurrences = occurrenceMap[var<0?Math.abs(var)+numVars-1:var-1];
		    	int[] negOccurrences = occurrenceMap[neg<0?Math.abs(var)+numVars-1:neg-1];
//		    	int numNewClauses = clauses.length - getNumOccurringClauses(posOccurrences, var);
//		    	int[][] newClauses = new int [numNewClauses][2];
		    	
		    	for (int clause = 0;clause<posOccurrences.length;clause++){
		    		if(posOccurrences[clause] == 1){
		    			this.clauseRemoved[clause] = true;
		    			for(int i : clauses[clause]){
		    				if(i!=0){
		    					if(this.occurrenceCount[i<0?Math.abs(i)+getNumVars()-1:i-1]!=0){
					    			this.occurrenceCount[i<0?Math.abs(i)+getNumVars()-1:i-1]--;
		    					}

		    				}
		    			}
		    		}
		    	}
		    	for (int clause = 0;clause<negOccurrences.length;clause++){
		    		if(negOccurrences[clause] == 1){
		    			for(int j = 0;j<this.clauses[clause].length;j++){
		    				if(clauses[clause][j] == neg){
		    					clauses[clause][j] = 0;
				    			this.occurrenceCount[neg<0?-neg-1:neg-1]--;
		    				}
		    			}
		    		}
		    	}
		    	
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
		    public boolean eliminateUnitClauses(){
		    	boolean unitClauseFound = true;
		    	while(unitClauseFound) { //always called on the first iteration
		    		unitClauseFound = false; //assume unit clause not found
		    		for(int i = 0; i < clauses.length;i++){
		    			int var = getVarFromUnit(clauses[i]);
		    			if(var != 0 && clauseRemoved[i]!=true){
		    				givenVar(var);
		    				this.clauseRemoved[i] = true;
		    				unitClauseFound = true;
		    			}
		    		}
		    	}
		    	return unitClauseFound;

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
		    		System.out.println("foo " + i);
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
		    public boolean eliminatePureLiterals(){
		    	for(int i = 1; i <= numVars; i++){
		    		int numPosOccurr = getNumOccurringClauses(i);
		    		int numNegOccurr = getNumOccurringClauses(-i);
//		    		System.out.println("FOOBAR\t" + i + "\t" + numPosOccurr);
//		    		System.out.println("FOOBAR\t" + -i + "\t" + numNegOccurr);
		    		if(numPosOccurr == 0 && numNegOccurr != 0){ //if the literal i does not appear
		    			givenVar(-i);
		    			return true;
		    		}
		    		else if(numPosOccurr != 0 && numNegOccurr == 0){
		    			givenVar(i);
		    			return true;
		    		}
		    	}
		    	return false;
		    }

		    /**
		     * Uses array implementation. Requires allocation and deallocation of 
		     * large amounts of space due to the array implementations that remove the variables.
		     * 
		     * @param clauses
		     * @return
		     */
		    public void simplify(){
		    	
		    	while(eliminateUnitClauses()||eliminatePureLiterals()){
		    		
		    	}
		    	/*
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
		    			
		    			/*for(int[] l: newFormula){
		    				for(int i : l){
		    					System.out.print(i + " ");
		    				}
		    				System.out.println("");
		    			}
		    			System.out.println("...");*/
//		    		}
//		    	}
//		    	return newFormula;
		    }
		    
		    public Set<Set<Integer>> cachedSimplify(int[][] clauses){
		    	Set<Set<Integer>> cache = computeSetCache(clauses);
		    	while(eliminatePureLiterals(cache) || eliminateUnitClauses(cache)){
		    	}
		    	return cache;
		    }
		    
		    private boolean hasEmptyClauses(){
		    	for(int i = 0;i<clauses.length;i++){
		    		boolean emptyClause = true;
		    		if(clauseRemoved[i] == false){
		    			for(int literal : clauses[i]){
			    			if(literal!=0){
			    				emptyClause = false;
			    			}
			    		}
			    		if(emptyClause){
			    			System.out.println("FORMULA UNSATISFIABLE");
			    			return true;
			    		}
		    		}
		    		
		    	}
		    	return false;
		    }
		    
		    private boolean hasNoClauses(){
		    	for(boolean i : this.clauseRemoved){
		    		if(!i){
		    			return false;
		    		}
		    	}
		    	return true;
		    }
		    
		    public String toString(){
		    	StringBuffer out = new StringBuffer();
		    	for (int[] clause:clauses){
		    		out.append("[ ");
		    		for(int literal: clause){
		    			out.append(literal+" ");
		    		}
		    		out.append("]");
		    	}
		    	return out.toString();
		    }
	}
