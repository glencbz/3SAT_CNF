package project2d;

/*
 * Modified from the CnfSatInstance class from http://kahina.org/trac/browser/trunk/src/org/kahina/logic/sat/data/cnf/CnfSatInstance.java?rev=1349
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
	

public class CnfSatInstance
	{
			private int numClauses;
		    private int numVars;
		    
		    //TODO: possible time improvement, replace List implementation with Array implementation
		    
		    protected List<List<Integer>> clauses;
		    protected List<Integer>[] occurrenceMap = null;
		    protected ImplicationGraph implicationGraph = null;
		    protected SCCGraph sccGraph;
		    
		    //  entries [0,...,numVars-1] for positive literals
		    //  entries [numVars,...,2*numVars-1] for negative literals
	   
		    public CnfSatInstance()
		    {
		    	setNumClauses(0);
		    	setNumVars(0);
		    	clauses = new ArrayList<List<Integer>>();
		    	occurrenceMap = null;
		    }
		    
		    //Computes the implication graph given the clauses in the cnf
		    public void computeImplicationGraph(){
		    	System.out.println("Generating implication graph for " + (getNumVars() * 2) + " literals ... ");
		    	implicationGraph = new ImplicationGraph(getNumVars() * 2 + 1);
		    	for(int i = 0;i < getNumVars() * 2 + 1; i++){
		    		implicationGraph.edges[i] = new LinkedList<Integer>();
		    	}
		    	for(List<Integer> clause: clauses){
		    		implicationGraph.addClause(clause);
		    	}
		    	System.out.println(implicationGraph);
		    }
		    
		    public boolean parseRandom(){
		    	System.out.println("parsing random");
		    	ArrayList<Boolean> confArray = new ArrayList<Boolean>();
		    	ArrayList<List<Integer>> falseClauses = new ArrayList<List<Integer>>();
		    	System.out.println(getNumVars());
		    	System.out.println(getNumClauses());
		    	for(int i = 0;i < getNumClauses(); i++){
		    		confArray.add(false);
		    		System.out.println("adding false " + i);
		    	}
		    	int count = 1;
		        boolean done = false;
		        List<Integer> randomClause = new ArrayList<Integer>();
		        
		        
		        while(count <= Math.log(getNumClauses())/Math.log(2)) {
		        	System.out.println("increasing count");
		        	int count2 = 1;
		        	Collections.shuffle(confArray);
		        	while(count2 <= 2 * getNumClauses() * getNumClauses()) {
		        		falseClauses.clear();
		        		for (List<Integer> c : getClauses()) {
		        			if(!evaluater(c.get(0),c.get(1),confArray.get(Math.abs(c.get(0))), confArray.get(Math.abs(c.get(1))))) {
		                        falseClauses.add(c);
		                    }
//		        			if(!confArray.get(Math.abs(c.get(0)))||confArray.get(Math.abs(c.get(1)))) {
//		                        falseClauses.add(c);
//		                    }
						}
		        		System.out.println(falseClauses);
		        		if(!falseClauses.isEmpty()) {
		                    Collections.shuffle(falseClauses);
		                    randomClause.add(falseClauses.get(0).get(0));
		                    randomClause.add(falseClauses.get(0).get(1));
		                }
		        		
		        		else {
		        			done = true;
		        			break;
		        		}
		        		
		                    //Shuffle literals
		                    int r = new Random().nextInt(2)+1;
		                    System.out.println("shuffling literals");
		                    if(r == 1) {
		                        Boolean b = confArray.get(Math.abs(randomClause.get(0)));
//		                        System.out.println(b);
		                        confArray.set(Math.abs(randomClause.get(0)), !b);
		                      
		                    }
		                    else {
		                        Boolean b = confArray.get(Math.abs(randomClause.get(1)));
//		                        System.out.println(b);
		                        confArray.set(Math.abs(randomClause.get(1)), !b);
		                   
		                    }
		                
		                count2++;
		                
		            }
//		        	System.out.println(done);
		            if(done){
		                break;
		            }
		            
		            count++;
		        }
//		        System.out.println(done);
		        ArrayList<Integer> answer = new ArrayList<Integer>(confArray.size());
		        for (int i = 0; i<confArray.size();i++){
		        	if (confArray.get(i)){
		        		answer.add(1);
		        	}
		        	else {
		        		answer.add(0);
		        	}
		        }
		        System.out.println(answer);
		        return done;
		    }
	
		    
		    public boolean evaluater(int a, int b, boolean a1, boolean b1)
		    {
		        if (a < 0){
		        	a1 = !a1;
		        }
		        if (b < 0){
		        	b1 = !b1;
		        }
		        return a1 || b1;
		    }
		    
		    //Computes the strongly connected components in the implication graph
		    public void computeSCCGraph(){
		    	if(implicationGraph == null){
		    		computeImplicationGraph();
		    	}
		    	this.sccGraph = new SCCGraph(this.implicationGraph);
		    }
		    
		    public boolean solve(){
		    	if(this.sccGraph == null){
		    		computeSCCGraph();
		    	}
		    	boolean out = this.sccGraph.evaluate();
		    	System.out.println(sccGraph);
		    	return out;
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
