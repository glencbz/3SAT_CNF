import java.util.ArrayList;
import java.util.List;

/**
 * A class written to process the 2-SAT CNF solver
 * @author Glen Choo, Goh Jia Hao, Lau Siaw Young, Nikhil Sharma, Tan Hao Qin
 *
 */
public class CnfSatInstance
		{		

			//Contains data regarding the original formula given to us
			private int numClauses;
		    private int numVars;
		    //Strongly connected component graph class used in the algorithm
		    protected SCCGraph sccGraph = null;		    
		    
		    //  entries [0,...,numVars-1] for positive literals
		    //  entries [numVars,...,2*numVars-1] for negative literals
		    
		    //Contains the boolean assignments we give to each variable
		    private int[] knownAssignments = null; 
		    //Stores the occurring clauses of each literal in the formula
		    protected List<Integer>[] occurrenceMap = null;
		    //Stores the occurrence of each literal in the formula
		    protected int[] occurrenceCount = null;
		    
		    //  entries [0,...,numClauses-1] for clauses
		    
		    //Contains the formula given to us
		    protected int[][] originalClauses = null;
		    //Contains the modified formula as we process our formula
		    protected int[][] clauses = null;
		    //Tracks if any clause is removed during the simplification process
		    protected boolean[] clauseRemoved = null;
		    //Tracks number of literals in each clause
		    protected int[] clauseSize = null;
		   	   
		    public CnfSatInstance()
		    {	
		    	setNumClauses(0);
		    	setNumVars(0);
		    	clauses = null;
		    	originalClauses = null;
		    }
		    
		    public CnfSatInstance(int[][] clauses, int numClauses, int numVars)
		    {	
		    	this.clauses = clauses;
		    	this.originalClauses = clauses;
		    	this.numClauses = numClauses;
		    	this.numVars = numVars;
		    }
		    
		    /*
		     * Function that initialises the data pre-processing before the solving of the problem
		     * Computes the occurrence map as well as problem simplification
		     */
		    public void initialise()
		    {	
		    	this.knownAssignments = new int[numVars];
		    	computeOccurrenceMap(this.clauses,this.numVars);
	    		this.simplify();
		    }
		    
		    /**
		     * Creates a 2D array Map of numVars * 2(numLiterals) by numClauses
		     * 0 denotes that the literal is not present in that clause
		     * 1 denotes that the literal is present
		     * Has absolutely no dependence on the calling instance
		     * Also computes occurrence count and clause size
		     * 
		     * @return An occurrence map corresponding to the clauses, numClauses and numVars given
		     */
			@SuppressWarnings("unchecked")
			private void computeOccurrenceMap(int[][] clauses, int numVars)
			{	
			    //  entries [0,...,numVars-1] for positive literals
			    //  entries [numVars,...,2*numVars-1] for negative literals
				clauseRemoved = new boolean[clauses.length];
				clauseSize = new int[clauses.length];
				occurrenceCount = new int[numVars*2];
		    	occurrenceMap = new List[numVars*2];
		    	
		    	for(int i = 0; i<numVars*2; i++){
		    		occurrenceMap[i] = new ArrayList<Integer>();
		    	}
		    	for (int i = 0; i < clauses.length; i++)
		    	{
		    		for (int literal : clauses[i])
		    		{
		    			int pos = literal > 0 ? literal: numVars + Math.abs(literal);
		    			occurrenceMap[pos-1].add(i);
		    			occurrenceCount[pos-1]++;	
		    		}
		    		clauseSize[i] = sizeClause(clauses[i]);
		    	}
		    }
		    
			/**
 		     * Loops two different functions, eliminateUnitClauses(), eliminatePureLiterals()
		     * to simplify the given problem. Loop runs until problem cannot be simplified anymore.
			 */
		    private void simplify()
		    {
		    	while(eliminateUnitClauses()||eliminatePureLiterals())
		    	{
		    	}	
		    }
			
		    /**
		     * Detects unit clauses from the formula and runs givenVar(literal) if a unit clause is found
		     * @return a boolean that checks if an elimination actually takes place
		     */
		    private boolean eliminateUnitClauses()
		    {
		    	boolean unitClauseFound = true;
		    	while(unitClauseFound) //always called on the first iteration
		    	{ 
		    		unitClauseFound = false; //assume unit clause not found
		    		for(int i = 0; i < clauses.length;i++)
		    		{
		    			if(clauseSize[i]==1){
		    				int var = getVarFromUnit(clauses[i]);
		    				if(var != 0 && clauseRemoved[i]!=true)
			    			{
			    				givenVar(var);
			    				unitClauseFound = true;
			    			}
		    			}
		    		}
		    	}
		    	return unitClauseFound;
		    }
		    
		    /**
		     * Returns the size of the clause
		     * @param clause the clause to be considered
		     * @return the size of the clause
		     */
		    private int sizeClause(int[] clause)
		    {
				int ret = 0;
				for(int i : clause){
					ret += i != 0 ? 1 : 0;
				}
				return ret;
			}
			
		    /**
		     * Get the literal from a unit clause
		     * @param clause, the clause to be considered
		     * @return the literal in the clause
		     */
			private int getVarFromUnit(int[] clause){
				for(int i : clause){
					if(i != 0){
						return i;
					}
				}
				return 0;
			}
		    
		    /**
		     * Checks for pure literals in the formula and runs givenVar(literal) if a pure literal is there
		     * @return a boolean that checks if an elimination actually takes place
		     */
		    private boolean eliminatePureLiterals()
		    {
		    	for(int i = 1; i <= numVars; i++)
		    	{
		    		int numPosOccurr = getNumOccurringClauses(i);
		    		int numNegOccurr = getNumOccurringClauses(-i);
		    		if(numPosOccurr == 0 && numNegOccurr != 0) //if the literal i does not appear
		    		{ 
		    			givenVar(-i);
		    			return true;
		    		}
		    		else if(numPosOccurr != 0 && numNegOccurr == 0)
		    		{
		    			givenVar(i);
		    			return true;
		    		}
		    	}
		    	return false;
		    }
		    
		    /**
		     * Calculates the number of times a literal appears in the current formula
		     * @param literal, the literal we are looking for
		     * @return the literal's occurrence count
		     */
		    private int getNumOccurringClauses(int literal){
		    	return this.occurrenceCount[literal<0?Math.abs(literal)+getNumVars()-1:literal-1];
		    }
		    
		    /**
		     * Reduces a variable from a formula by eliminating all clauses containing the given literal
		     * and removing all of its negation from the clauses
		     * @param var the literal of the formula to be removed
		     */
			private void givenVar(int var){

				this.knownAssignments[var<0?-var-1:var-1] = var<0?-1:1;
				this.occurrenceCount[var<0?Math.abs(var)+getNumVars()-1:var-1] = 0;
				int neg = -var;
				
		    	List<Integer> posOccurrences = occurrenceMap[var<0?Math.abs(var)+numVars-1:var-1];
		    	List<Integer> negOccurrences = occurrenceMap[neg<0?Math.abs(var)+numVars-1:neg-1];
		    	
		    	for (int clause:posOccurrences)
		    	{
		    			this.clauseRemoved[clause] = true;
		    			for(int i : clauses[clause])
		    			{
		    				if(i!=0)
		    				{
		    					if(this.occurrenceCount[i<0?Math.abs(i)+getNumVars()-1:i-1]!=0)
		    					{
					    			this.occurrenceCount[i<0?Math.abs(i)+getNumVars()-1:i-1]--;
		    					}

		    				}
		    			}	
		    	}	
		    	for (int clause :negOccurrences)
		    	{
		    			for(int j = 0;j<this.clauses[clause].length;j++)
		    			{
		    				if(clauses[clause][j] == neg)
		    				{
		    					clauses[clause][j] = 0;
		    					clauseSize[clause]--;
				    			occurrenceCount[neg<0?-neg-1:neg-1]--;
		    				}
		    			}
		    		}
		    	}
			
			/**
			 * Solves the SAT problem to be used after initialising the problem
			 * Attempts to find empty formula or empty solution before
			 * running the SCC algorithm to calculated the Strongly Connected Components Graph
			 * @return the satisfiability of the problem
			 */
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

		    /**
		     * Checks if the formula has empty clauses
		     * @return true if there are empty clauses, otherwise false
		     */
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
		    
		    /**
		     * Checks if the formula is empty
		     * @return true is formula is empty, otherwise false
		     */
		    private boolean hasNoClauses(){
		    	for(boolean i : this.clauseRemoved){
		    		if(!i){
		    			return false;
		    		}
		    	}
		    	return true;
		    }
		    
		    /**
		     * Computes the SCC Graph and attempts to solve the problem using the SCC Algorithm
		     */
		    private void computeSCCGraph()
		    {
		    	sccGraph = new SCCGraph(getNumVars() * 2 + 1,clauses,knownAssignments,this.clauseRemoved);
		    }
		    
		    /**
		     * Checks if a solution calculated is correct using the original clauses provided
		     * USED FOR TESTING PURPOSES ONLY WORKS IF SOLUTION IS CALCULATED DURING INITIALISATION STAGE
		     * @return if the solution is correct
		     */
		    public boolean verifySolution(){
		    	for(int[] clause : originalClauses){
		    		int clauseSum = 0;
		    		for(Integer term: clause){
		    			clauseSum += this.sccGraph.knownAssignments[term>0?term-1:this.getNumVars()-term-1];
		    		}
		    		if(clauseSum==0){
		    			System.out.println(false);
		    			return false;
		    		}
		    	}
		    	System.out.println(true);
		    	return true;
		    }
		    
		    public int getNumClauses()
		    {
		    	return numClauses;
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
		     * Prints the clauses in the CNF instance
		     */
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
