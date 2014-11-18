/*
 * Modified from the CnfSatInstance class from http://kahina.org/trac/browser/trunk/src/org/kahina/logic/sat/data/cnf/CnfSatInstance.java?rev=1349
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
	

public class CnfSatInstance
	{
			private int numClauses;
		    private int numVars;
		    private int[] knownAssignments = null;
		    //TODO: possible time improvement, replace List implementation with Array implementation
		    
		    protected List<List<Integer>> clauses;
		    protected List<Integer>[] occurrenceMap = null;
		    protected List<List<Integer>> MICS = null;
		    //  entries [0,...,numVars-1] for positive literals
		    //  entries [numVars,...,2*numVars-1] for negative literals
	   
		    public CnfSatInstance()
		    {
		    	setNumClauses(0);
		    	setNumVars(0);
		    	clauses = new ArrayList<List<Integer>>();
		    	occurrenceMap = null;
		    }
		    
		    /**
		     * Public method that initialises the instance to make it able to run after parsing
		     */
		    public void initialise(){
		    	knownAssignments = new int[numVars];
		    	computeOccurrenceMap();
		    	simplify();
		    	computeOccurrenceMap();		    	
		    }
		    //generate lit -> clause map for lookup
		    //caching this makes the computation of different views a lot faster
		    @SuppressWarnings("unchecked")
		    private void computeOccurrenceMap()
		    {
//		    	System.out.println("Generating occurrence map for " + (getNumVars() * 2) + " literals ... ");
		    	occurrenceMap = (List<Integer>[]) new List[numVars * 2];
		    	for (int i = 0; i < numVars * 2; i++)
		    	{
		    		occurrenceMap[i] = new LinkedList<Integer>();
		    	}
		    	for (int i = 1; i <= clauses.size(); i++)
		    	{
		    		List<Integer> clause = clauses.get(i-1);
		    		for (int literal : clause)
		    		{
		    			int pos = literal;
		    			if (literal < 0) pos = numVars + Math.abs(literal);
		    			occurrenceMap[pos-1].add(i);
		    		}
		    	}
//		    	for(List<Integer> l : occurrenceMap)
//		    		System.out.println("Occurrence Map: " + l);
//		    	System.out.println("Ready!");
		    }
		    
		    public int getNumClauses()
		    {
		    	return numClauses;
		    }
	
		    public List<List<Integer>> getClauses()
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

			public List<Integer>[] getOccurrenceMap() {
				return occurrenceMap;
			}

			/**
		     * Takes two clauses and computes their resolvent.
		     * The resolvent is the clause (C1 - v) union (C2 - ~v) where C1 and C2 have only v as a
		     * common variable. This is useful as any assignment that satisfies C1 and C2 must
		     * satisfy their resolvent as well.
		     * 
		     * Tested and works
		     * 
		     * @param clause1 clause containing var
		     * @param clause2 clause containing negation of var
		     * @param var common variable to both clauses
		     * @return the resolvent of clause1 and clause2
		     */
		    private List<Integer> resolvent(List<Integer> clause1, List<Integer> clause2, Integer var){
		    	List<Integer> resolvent = new LinkedList<Integer>();
		    	boolean varPresent1 = false;
		    	boolean varPresent2 = false;
		    	
		    	for (Integer i : clause1){
		    		if(!i.equals(var))
		    			resolvent.add(i);
		    		else
		    			varPresent1 = true;
		    	}
		    	
		    	for (Integer i : clause2){
		    		if (!i.equals(new Integer(-var.intValue())))
		    			resolvent.add(i);
		    		else
		    			varPresent2 = true;
		    	}
		    	
		    	if(varPresent1 && varPresent2)
		    		return resolvent;
		    	else{
		    		System.err.println("Error: resolvent did not find correct variables");
		    		return null;
		    	}
		    		
		    }
		    
		    public Integer checkResolvence(List<Integer> clause1, List<Integer> clause2){
		    	Integer variable = new Integer(0);
		    	
		    	for(Integer i: clause1){
		    		for(Integer j: clause2){
		    			if(i.equals(new Integer(-j.intValue())) && variable.equals(new Integer(0)))
		    				variable = i;
		    			else if ((i.equals(new Integer(-j.intValue())) ||  i.equals(new Integer(j.intValue())))){
		    				return new Integer(0);
		    			}
		    		}
		    	}
		    	return variable;
		    }
		    
		    //TODO: improve the resolution function by binary searching a history of past clause comparisons
		    
		    public void createResolution(){
		    	while(resolutionIteration())
		    		computeOccurrenceMap();
		    }
		    
		    //TODO: compare the resolution algorithm with the original PPSZ to ensure fast run-time
		    private boolean resolutionIteration(){
		    	boolean resFound = false;
		    	for(int i = 1; i <= numVars; i++){
		    		
		    		if(!occurrenceMap[i - 1 + numVars].isEmpty()){
		    			
		    			for(int j : occurrenceMap[i - 1 + numVars]){
		    				
		    				for(int k : occurrenceMap[i - 1]){
		    					
			    				int commonVar = (int) checkResolvence(clauses.get(k - 1), clauses.get(j-1));
			    				if(commonVar != 0){
			    					List<Integer> res = resolvent(clauses.get(k-1), clauses.get(j-1), commonVar);
			    					if(clauses.lastIndexOf(res) == -1 && res.size() <= numVars){
			    						clauses.add(res);
			    						resFound = true;
			    						numClauses++;
			    					}
			    				}
		    				}
		    			}
		    		}
		    	}
		    	simplify();
		    	return resFound;
		    }
		    
		    /**
		     * Returns the formula G given var for the CNF SAT sentence G
		     * and a variable var and recomputes the occurrence map.
		     * This is done by removing all clauses with var and removing neg(var) 
		     * from all clauses containing it. Eliminates variables in both 
		     * eliminateUnitClauses() and eliminateTrivialities.
		     * 
		     * Tested and works
		     * @param var Given variable
		     */
		    private List<List<Integer>> givenVar(Integer var){
		    	int givenVariable = (int) var > 0 ? (int) var : Math.abs((int) var) + numVars;
		    	LinkedList<List<Integer>> clausesToRemove = new LinkedList<List<Integer>>();
		    	LinkedList<List<Integer>> copyOfClauses = new LinkedList<List<Integer>>(clauses);
		    	
		    	for(int i = 1; i <= occurrenceMap[givenVariable - 1].size(); i++){
		    		clausesToRemove.add(clauses.get(occurrenceMap[givenVariable - 1].get(i - 1) - 1));
		    	}
		    	
		    	for(List<Integer> l : clausesToRemove)
		    		copyOfClauses.remove(l);
		    	
		    	int negGivenVariable = givenVariable > numVars ? givenVariable - numVars : -givenVariable;
		    	
		    	for(List<Integer> l : clauses){
		    		for(int i = 1; i <= l.size(); i++){
		    			if((int)l.get(i - 1) == negGivenVariable){
		    				l.remove(i - 1);
		    			}
		    		}
		    	}
    			knownAssignments[Math.abs(var) - 1] = var < 0 ? -1: 1 ;
		    	
		    	return copyOfClauses;
		    }
		    /**
		     * Removes unit clauses from the sentence by repeatedly calling the givenVar() function
		     * for every unit clause found. Returns true if changes have been made
		     * 
		     * @return True if changes were made
		     */
		    public boolean eliminateUnitClauses(){
		    	boolean unitClauseFound = true;
		    	while(unitClauseFound) { //always called on the first iteration
		    		unitClauseFound = false; //assume unit clause not found
			    	for(List<Integer> l : clauses){
			    		if(l.size() == 1){
			    			knownAssignments[l.get(0) - 1] = l.get(0) < 0 ? -1: 1 ;
			    			this.clauses= givenVar(l.get(0));
			    			this.numVars = clauses.size();
			    			computeOccurrenceMap();
			    			unitClauseFound = true;
			    			break;
			    		}
		    		}
		    	}
		    	return unitClauseFound;
		    }
		    
		    /**
		     * Examines the formula for all occurrences of each variable.
		     * If the variable occurs but not its negation or vice versa,
		     * the variable is added to the array of known assignments and
		     * removed from the formula.
		     * 
		     * @return Whether a triviality has been found
		     */
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
		    /**
		     * Implements the brute force MICS generation and assignment guessing
		     * algorithm as specified by Baumer and Schuler (2003) in Improving a probabilistic 3-SAT etc etc
		     * MICS is stored in the CNF instance
		     * @return Guess of assignment
		     */
/*
		    public int[] bruteForce(){
		    	MICS = new LinkedList<List<Integer>>();
		    	int[] assignment = new int[numVars];
		    	for(int i = 1; i <= numVars; i++){ //for each variable
		    		
		    		List<Integer> C = null;
		    		List<Integer> CPrime = null;
		    		for(List<Integer> c : clauses){//for each clause
		    			//TODO: Implement the 2-lit clause implication chain that finds all cases of variable = 0 implies lit = 0
		    			if(c.contains(i)){//if found a clause where variable = 0 implies lit = 0
		    				C = c;
		    				break;
		    			}
		    		}
		    		
		    		if(C == null){
		    			assignment[i-1] = 0; //if clause C is not found, deterministically set x to 0
		    		}else{ //otherwise
			    		for(List<Integer> c : clauses){//for each clause
			    			//TODO: Implement the 2-lit clause implication chain that finds all cases of variable = 1 implies lit = 0
			    			if(c.contains(-i)){//if found a clause where variable = 1 implies lit = 0
			    				CPrime = c;
			    				break;
			    			}
			    		}
			    		if(CPrime == null){
			    			assignment[i-1] = 1;
			    		}else
			    			assignment[i-1] = randomAssignment1();
		    		}
		    		
		    		if(assignment[i-1] == 0 && C != null){
		    			MICS.add(C);
		    		}
		    	}
		    }
		    */
		    
 		    public int randomAssignment1(){
		    	return Math.random() > 0.5 ? 1 : -1;
		    }

		    public int[] randomAssignment2(){
		    	double rand = Math.random();
		    	int[] ret = {-1,-1};
		    	if (rand < (double) 1 / 3){
		    		ret[1] = 1;
		    		return ret;
		    	}
		    	else if (rand >= (double) 1 / 3 && rand < (double) 2 / 3){
		    		ret[0] = 1;
		    		return ret;
		    	}
		    	else{
		    		ret[0] = 1;
		    		ret[1] = 1;
		    		return ret;
		    	}
		    }
		    		
		    public String toString(){
		    	return clauses.toString();
		    }
	}
