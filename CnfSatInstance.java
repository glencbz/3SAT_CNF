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
		    
		    //TODO: possible time improvement, replace List implementation with Array implementation
		    
		    protected List<List<Integer>> clauses;
		    protected List<Integer>[] occurrenceMap = null;
	 
		    //  entries [0,...,numVars-1] for positive literals
		    //  entries [numVars,...,2*numVars-1] for negative literals
	   
		    public CnfSatInstance()
		    {
		    	setNumClauses(0);
		    	setNumVars(0);
		    	clauses = new ArrayList<List<Integer>>();
		    	occurrenceMap = null;
		    }
		    
		    //generate lit -> clause map for lookup
		    //caching this makes the computation of different views a lot faster
		    @SuppressWarnings("unchecked")
		    public void computeOccurrenceMap()
		    {
		    	System.out.println("Generating occurrence map for " + (getNumVars() * 2) + " literals ... ");
		    	occurrenceMap = (List<Integer>[]) new List[getNumVars() * 2];
		    	for (int i = 0; i < getNumVars() * 2; i++)
		    	{
		    		occurrenceMap[i] = new LinkedList<Integer>();
		    	}
		    	for (int i = 1; i <= clauses.size(); i++)
		    	{
		    		List<Integer> clause = clauses.get(i-1);
		    		for (int literal : clause)
		    		{
		    			int pos = literal;
		    			if (literal < 0) pos = getNumVars() + Math.abs(literal);
		    			occurrenceMap[pos-1].add(i);
		    		}
		    	}
		    	for(List<Integer> l : occurrenceMap)
		    		System.out.println("Occurrence Map: " + l);
		    	System.out.println("Ready!");
		    }
		    
		    public int getNumClauses()
		    {
		    	return numClauses;
		    }
	
		    public int getNumVariables()
		    {
		    	return getNumVars();
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

		    /**
		     * Takes two clauses and computes their resolvent.
		     * The resolvent is the clause (C1 - v) union (C2 - ~v) where C1 and C2 have only v as a
		     * common variable. This is useful as any assignment that satisfies C1 and C2 must
		     * satisfy their resolvent as well.
		     * 
		     * @param clause1 clause containing var
		     * @param clause2 clause containing negation of var
		     * @param var common variable to both clauses
		     * @return the resolvent of clause1 and clause2
		     */
		    public List<Integer> resolvent(List<Integer> clause1, List<Integer> clause2, Integer var){
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
			    					if(clauses.lastIndexOf(res) == -1){
			    						clauses.add(res);
			    						resFound = true;
			    						numClauses++;
			    					}
			    				}
		    				}
		    			}
		    		}
		    	}
		    	return resFound;
		    }
		    
		    public String toString(){
		    	return clauses.toString();
		    }
	}
