package arrayForm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;	

public class CNFSatInstance
	{
			private int numClauses;
		    private int numVars;
		    
		    private int[] knownAssignments = null;
		    protected int[][] clauses = null;
		    protected int[][] occurrenceMap = null;
		    private int[] occurrenceNums = null;
		    private int[] deleted = null; 
		    private boolean changeMade = false;
		    
		    public boolean isChangeMade() {
				return changeMade;
			}

			public void setChangeMade(boolean changeMade) {
				this.changeMade = changeMade;
			}
			History previousState = null;
		    
			public CNFSatInstance(int numClauses, int numVars,
					int[] knownAssignments, int[][] clauses,
					int[][] occurrenceMap, int[] occurrenceNums, int[] deleted,
					History previousState, boolean changeMade) {
				super();
				this.numClauses = numClauses;
				this.numVars = numVars;
				this.knownAssignments = knownAssignments;
				this.clauses = clauses;
				this.occurrenceMap = occurrenceMap;
				this.occurrenceNums = occurrenceNums;
				this.deleted = deleted;
				this.previousState = previousState;
				this.changeMade = changeMade;
			}

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
		    	this.knownAssignments = new int[numVars];
		    	deleted = new int[numClauses];
		    	occurrenceNums = new int[numVars * 2];
		    	occurrenceMap = computeOccurrenceMap(clauses, numVars, occurrenceNums);
		    }
		    
/*		    public CNFSatInstance(int[][] clauses, int numVars, int[][] occurrenceMap, int[] occurrenceNums, int[] deleted, int[] knownAssignments){
		    	this.clauses = clauses;
		    	this.numClauses = clauses.length;
		    	this.numVars = numVars;
		    	this.occurrenceMap = occurrenceMap;
		    	this.occurrenceNums = occurrenceNums;
		    	this.deleted = deleted;
		    	this.knownAssignments = knownAssignments;
		    }
	*/	    
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
		     * Also takes in an array occurrenceNumMap
		     * @param clauses
		     * @param numVars
		     * @param occurrenceNumMap
		     * @return
		     */
		    public int[][] computeOccurrenceMap(int[][] clauses, int numVars, int[] occurrenceNumMap){
			    //  entries [0,...,numVars-1] for positive literals
			    //  entries [numVars,...,2*numVars-1] for negative literals
		    	int[][] occurMap = new int[numVars * 2][clauses.length];
		    	for (int i = 0; i < clauses.length; i++){
		    		for (int literal : clauses[i]){
		    			if(literal != 0){
			    			int pos = literal > 0 ? literal - 1: numVars + Math.abs(literal) - 1;
			    			occurMap[pos][occurrenceNumMap[pos]] = i + 1;
			    			occurrenceNumMap[pos]++;
		    			}
		    		}
		    	}
		    	return occurMap;
		    }
		    
			public int sizeClause(int[] clause){
				int ret = 0;
				for(int i : clause)
					ret += i != 0 ? 1 : 0;
				return ret;
			}
			
			public int[][] getOccurrenceMap() {
				return occurrenceMap;
			}

			public int[] getOccurrenceNums() {
				return occurrenceNums;
			}

			public int[] getDeleted() {
				return deleted;
			}
			public CNFSatInstance givenVarMutator(int var){
				int posPosition = var > 0? var - 1 : -var + numVars - 1;
				if (occurrenceNums[posPosition] == 0){
					this.setChangeMade(false);
					return this;
				}
				int negPosition = var > 0 ? var + numVars - 1 : -var - 1;
				
			    int knownAssignmentsChanges = 0;
			    int lastGivenVar = var;
			    Map<Integer,int[]> clausesChanges = new HashMap<Integer, int[]>();
			    Map<Integer, int[]> occurrenceMapChanges = new HashMap<Integer, int[]>();
			    Map<Integer, Integer> occurrenceNumsChanges = new HashMap<Integer, Integer>();
			    List<Integer> deletedChanges = new LinkedList<Integer>();
			
				int[] posOccurrences = occurrenceMap[posPosition];
				
		    	for(int clause: posOccurrences){
		    		if (clause == 0)
		    			break;
		    		deleted[clause-1] = 1;
		    		deletedChanges.add(clause-1);
		    	}
		    	
		    	int[] negOccurrences = occurrenceMap[negPosition];
		    	
				for(int clause : negOccurrences){
		    		if (clause == 0)
		    			break;
		    		occurrenceMapChanges.put(clause, copyArray(clauses[clause - 1]));
		    		for(int i = 0; i < 3; i++){
		    			if(clauses[clause - 1][i] == -var)
		    				clauses[clause - 1][i] = 0;
		    		}
		    	}
				
				knownAssignmentsChanges = knownAssignments[Math.abs(var) - 1];
				knownAssignments[Math.abs(var) - 1] = var > 0 ? 1 : -1; 
				
				occurrenceMapChanges.put(posPosition, copyOccurrenceMapLine(occurrenceMap[posPosition]));
				occurrenceMapChanges.put(negPosition, copyOccurrenceMapLine(occurrenceMap[negPosition]));
				
				occurrenceMap[posPosition] = new int[occurrenceMap[0].length];
				occurrenceMap[negPosition] = new int[occurrenceMap[0].length];
				
				occurrenceNumsChanges.put(posPosition, occurrenceNums[posPosition]);
				occurrenceNumsChanges.put(negPosition, occurrenceNums[negPosition]);
				
				occurrenceNums[posPosition] = 0;
				occurrenceNums[negPosition] = 0;
				

		    	return new CNFSatInstance(this.numClauses, this.numVars,
						this.knownAssignments, this.clauses,
						this.occurrenceMap, this.occurrenceNums, this.deleted, new History(
						knownAssignmentsChanges, lastGivenVar,
						clausesChanges, occurrenceMapChanges,
						occurrenceNumsChanges, deletedChanges), true);
		    }

			public void undoChanges(){
				knownAssignments[Math.abs(this.previousState.getLastGivenVar()) - 1] = previousState.getKnownAssignmentsChanges();
				int posPosition = this.previousState.getLastGivenVar() > 0? this.previousState.getLastGivenVar() - 1 : -this.previousState.getLastGivenVar()+ numVars - 1;
				int negPosition = this.previousState.getLastGivenVar() > 0 ? this.previousState.getLastGivenVar() + numVars - 1 : -this.previousState.getLastGivenVar() - 1;
				
				occurrenceMap[posPosition] = previousState.getOccurrenceMapChanges().get(posPosition);
				occurrenceMap[negPosition] = previousState.getOccurrenceMapChanges().get(negPosition);
				
				occurrenceNums[posPosition] = previousState.getOccurrenceNumsChanges().get(posPosition);
				occurrenceNums[negPosition] = previousState.getOccurrenceNumsChanges().get(negPosition);
				
				for(int i : previousState.getDeletedChanges()){
					deleted[i] = 0;
				}
				
				int[] negOccurrences = occurrenceMap[negPosition];
				for(int clause : negOccurrences){
					if (clause == 0)
						break;
					clauses[clause-1] = previousState.getOccurrenceMapChanges().get(clause);
				}
			}
			
			/*public CNFSatInstance givenVarOccur(int var){

				int posPosition = var > 0? var - 1 : -var + numVars - 1;
				int negPosition = var > 0 ? var + numVars - 1 : -var - 1;
				long started = System.nanoTime();

				int[] newKnownAssignments = copyArray(knownAssignments);
				newKnownAssignments[Math.abs(var) - 1] = var > 0 ? 1 : -1; 
				
				int[][] newOccurrenceMap = copyOccurrenceMap(occurrenceMap);
				newOccurrenceMap[posPosition] = new int[newOccurrenceMap[0].length];
				newOccurrenceMap[negPosition] = new int[newOccurrenceMap[0].length];
				
				int[] newOccurrenceNums = copyArray(occurrenceNums);
				newOccurrenceNums[posPosition] = 0;
				newOccurrenceNums[negPosition] = 0;
				
				int[] posOccurrences = occurrenceMap[posPosition];
				
				int[] newDeleted = copyArray(deleted);
		    	for(int clause: posOccurrences){
		    		if (clause == 0)
		    			break;
		    		newDeleted[clause-1] = 1;
		    	}
		    		
		    	
		    	int[] negOccurrences = occurrenceMap[negPosition];
		    	
		    	int[][] newClauses = copy2DArray(clauses);
				long time = System.nanoTime();
				long timeTaken = time - started;
				System.out.println("Time:" + timeTaken/1000000.0 + "ms");
		    	for(int clause : negOccurrences){
		    		if (clause == 0)
		    			break;
		    		for(int i = 0; i < 3; i++){
		    			if(newClauses[clause - 1][i] == -var)
		    				newClauses[clause - 1][i] = 0;
		    		}
		    	}
		    	
		    	
		    	return new CNFSatInstance(newClauses, numVars, newOccurrenceMap, newOccurrenceNums, newDeleted, newKnownAssignments);
		    }
			*/
		    /**
		     * Removes a unit clause from the formula
		     */
		    public CNFSatInstance eliminateUnitClauses(){
		    	CNFSatInstance eliminatedInstance = this;
		    	eliminatedInstance.setChangeMade(false);
	    		int[][] newClauses = eliminatedInstance.getClauses();
	    		for(int i = 0; i < newClauses.length; i++){
	    			if(eliminatedInstance.getDeleted()[i] != 1){
		    			int var = getVarFromUnit(newClauses[i]);
		    			if(var != 0){		   
		    				
		    				eliminatedInstance = eliminatedInstance.givenVarMutator(var);
		    				break;
		    			}
	    			}
	    		}
//	    		System.out.println("Unit clause eliminated: " + eliminatedInstance.isChangeMade());
		    	return eliminatedInstance;
		    }

		    /**
		     * Dependent on numVars
		     * @param clauses
		     * @return
		     */
		    public CNFSatInstance eliminatePureLiterals(){
		    	CNFSatInstance eliminatedInstance = this;
		    	eliminatedInstance.setChangeMade(false);
		    	for(int i = 0; i < numVars; i++){
		    		if(occurrenceNums[i] == 0 && occurrenceNums[i + numVars]!= 0){ //if the literal i does not appear
		    			eliminatedInstance = eliminatedInstance.givenVarMutator(- (i + 1));
		    			break;
		    		}
		    		else if(occurrenceNums[i] != 0 && occurrenceNums[i + numVars] == 0){
		    			System.out.println(i + 1);
		    			eliminatedInstance = eliminatedInstance.givenVarMutator(i + 1);
		    			break;
		    		}
		    	}
//		    	System.out.println("Pure literals eliminated: " + eliminatedInstance.isChangeMade());
		    	return eliminatedInstance;
		    }



			public CNFSatInstance simplify(){
		    	boolean changesMade = true;
		    	CNFSatInstance simplifiedInstance = this; 
		    	
		    	while(changesMade){
		    		changesMade = false;
		    		CNFSatInstance eliminateUnits = simplifiedInstance.eliminateUnitClauses();
		    		
		    		if(eliminateUnits.isChangeMade()){ //equality can be checked this way because givenVar() and elimnateUnitClauses() only change addresses if changes are made
		    			simplifiedInstance = eliminateUnits;
		    			changesMade = true;
		    		}
		    		
		    		CNFSatInstance eliminatePures = simplifiedInstance.eliminatePureLiterals();
		    		if(eliminatePures.isChangeMade()){ //equality can be checked this way because givenVar() and elimnatePureLiterals() only change addresses if changes are made
		    			simplifiedInstance = eliminatePures;	
		    			changesMade = true;
		    		}
//		    		System.out.println(changesMade);
		    	}
		    	return simplifiedInstance;
		    }



			public int[][] copy2DArray(int[][] original){
		    	int[][] copy = new int[original.length][original[0].length];
		    	for(int i = 0; i < original.length; i++){
		    		for(int j = 0; j < original[0].length; j++){
		    			copy[i][j] = original[i][j];
		    		}
		    	}
		    	return copy;
		    }
		    public int[][] copyOccurrenceMap(int[][] original){
		    	int[][] copy = new int[original.length][original[0].length];
		    	for(int i = 0; i < original.length; i++){
		    		for(int j = 0; j < original[0].length; j++){
		    			if(original[i][j] == 0)
		    				break;
		    			copy[i][j] = original[i][j];
		    		}
		    	}
		    	return copy;
		    }
		    
		    public int[] copyOccurrenceMapLine(int[] original){
		    	int[] copy = new int[original.length];
		    	for(int i = 0; i < original.length; i++){
		    		if (original[i] == 0)
		    			break;
		    		copy[i] = original[i];
		    	}
		    	return copy;
		    }
		    
		    public int[] copyArray(int[] original){
		    	int[] copy = new int[original.length];
		    	for(int i = 0; i < original.length; i++)
		    		copy[i] = original[i];
		    	return copy;
		    }
		    

			/**
			 * Depreciated
			 * @param clause
			 * @return
			 */
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
		    
			public String toString(){
				String ret = "";
				for(int i = 0; i < clauses.length; i++){
					if(deleted[i] == 0){
						for(int j = 0; j < clauses[i].length; j++){
							ret += clauses[i][j] + " ";
						}
						ret += "\n";
					}
				}
				return ret;
			}
			
		    @Deprecated
		    /**
		     * Depreciated
		     * @param occurrenceMap
		     * @return
		     */
			public int[] occurrenceNum(int[][] occurrenceMap){
				int[] occurrenceNum = new int[occurrenceMap[0].length];
				for(int clause = 0; clause < occurrenceMap.length; clause++){
					for(int var = 0; var < occurrenceMap[0].length; var++){
						if(occurrenceMap[clause][var] != 0)
							occurrenceNum[var]++;
					}
				}
				return occurrenceNum;
			}
		    @Deprecated
		    /**
		     * DEPRECIATED
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
		    @Deprecated
		    /**
		     * Depreciated
		     * @param clauses
		     * @param literal
		     * @return
		     */
		    public int getNumOccurringClauses(int[][] clauses, int literal){
		    	int ret = 0;
		    	for(int occurrence : getOccurringClauses(clauses, literal)){
		    		ret += occurrence;
		    	}
		    	return ret;
		    }
		    @Deprecated
		    /**
		     * Depreciated
		     * @param occurringClauses
		     * @param literal
		     * @return
		     */
		    public int getNumOccurringClauses(int[] occurringClauses, int literal){
		    	int ret = 0;
		    	for(int occurrence : occurringClauses){
		    		ret += occurrence;
		    	}
		    	return ret;
		    }
		    /**
		     * Returns a set of clauses containing the literal
		     * @param clauses
		     * @param literal
		     * @return
		     *//*
		    public Set<Set<Integer>> getOccurringClauses(Set<Set<Integer>>clauses, int literal){
		    	Set<Set<Integer>> occurringClauses = new HashSet<Set<Integer>>();
		    	for (Set<Integer> clause: clauses){
		    		if(clause.contains(literal))
		    			occurringClauses.add(clause);
		    	}
		    	return occurringClauses;
		    }		    */
		    /*		    
		    public int getNumOccurringClauses(Set<int[]> clauses, int var){
		    	int numOccur = 0;
		    	for(int[] clause : clauses){
		    		for(int lit : clause){
		    			if (lit == var){
		    				numOccur++;
		    				break;
		    			}
		    		}
		    	}
		    	return numOccur;
		    }*/
		    
/*		    public int[] getOccurringClauses(List<List<Integer>> clauses, int literal){
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
		    }		*/
		    
		    /*public Set<int[]> computeSetCache(int[][]clauses){
		    	Set<int[]> setCache = new HashSet<int[]>();
		    	for(int[] clause: clauses){
		    		int[] newClause = new int[3];
		    		for(int i = 0; i < 3; i++){
		    			newClause[i] = clause[i];
		    		}
		    		setCache.add(newClause);
		    	}
		    	return setCache;
		    }*/
		    /*			*//**
			 * Takes in a formula cached in a set of int arrays. MUTATES THE ORIGINAL FORMULA
			 * according to substitution of the given variable.
			 * @param clauses
			 * @param var
			 * @return
			 *//*
			public Set<int[]> givenVar(Set<int[]> clauses, int var){
		    	
				//remove instances of the negative of the literal
				for(int[] clause: clauses){
		    		for(int i = 0; i < 3; i++){
		    			clause[i] = clause[i] == -var ? 0 : clause[i];
		    		}
		    	}
				
				//remove clauses containing the literal
				List<int[]> clausesToRemove = new ArrayList<int[]>();
				for(int[] clause: clauses){
					for(int i = 0; i < 3; i++){
						if(clause[i] == var){
							clausesToRemove.add(clause);
							break;
						}
					}
				}
				
				for(int[] clauseToRemove: clausesToRemove){
					clauses.remove(clauseToRemove);
				}
				
				return clauses;
		    }
*/
		    
		    
		    /**
		     * Takes in a cachedClause list and modifies it directly for faster processing
		     * 
		     * @param cachedClause
		     * @return
		     */
/*		    public Set<int[]> cachedSimplify(int[][] clauses){
		    	Set<int[]> cache = computeSetCache(clauses);
		    	while(eliminatePureLiterals(cache) || eliminateUnitClauses(cache)){
		    	}
		    	return cache;
		    }
		    
		    public boolean eliminateUnitClauses(Set<int[]> clauses){
		    	boolean unitClauseFound = true;
		    	boolean changesMade = false;
		    	
		    	List<Integer> variablesToRemove = new ArrayList<Integer>();
		    	while(unitClauseFound) { //always called on the first iteration
		    		unitClauseFound = false; //assume unit clause not found
		    		for(int[] clause: clauses){
		    			int varToRemove = getVarFromUnit(clause);
		    			if(varToRemove != 0)
		    				variablesToRemove.add(varToRemove);
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
		    *//**
		     * Operates on a cached set representation of clauses.
		     * Returns true if a change is made
		     * @param clauses
		     * @return
		     *//*
		    public boolean eliminatePureLiterals(Set<int[]> clauseSet){
		    	boolean changesMade = false;
		    	for(int i = 1; i <= numVars; i++){
		    		int numPosOccurr = getNumOccurringClauses(clauseSet, i);
		    		int numNegOccurr = getNumOccurringClauses(clauseSet, -i);
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
		    }*/
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
		   
	}

class History{
    int knownAssignmentsChanges = 0;
    int lastGivenVar = 0;
    Map<Integer,int[]> clausesChanges = null;
    Map<Integer, int[]> occurrenceMapChanges = null;
    Map<Integer, Integer> occurrenceNumsChanges = null;
    List<Integer> deletedChanges = null;
    
	public History(int knownAssignmentsChanges, int lastGivenVar,
			Map<Integer, int[]> clausesChanges,
			Map<Integer, int[]> occurrenceMapChanges,
			Map<Integer, Integer> occurrenceNumsChanges,
			List<Integer> deletedChanges) {
		super();
		this.knownAssignmentsChanges = knownAssignmentsChanges;
		this.lastGivenVar = lastGivenVar;
		this.clausesChanges = clausesChanges;
		this.occurrenceMapChanges = occurrenceMapChanges;
		this.occurrenceNumsChanges = occurrenceNumsChanges;
		this.deletedChanges = deletedChanges;
	}
	public int getKnownAssignmentsChanges() {
		return knownAssignmentsChanges;
	}
	public void setKnownAssignmentsChanges(int knownAssignmentsChanges) {
		this.knownAssignmentsChanges = knownAssignmentsChanges;
	}
	public int getLastGivenVar() {
		return lastGivenVar;
	}
	public void setLastGivenVar(int lastGivenVar) {
		this.lastGivenVar = lastGivenVar;
	}
	public Map<Integer, int[]> getClausesChanges() {
		return clausesChanges;
	}
	public void setClausesChanges(Map<Integer, int[]> clausesChanges) {
		this.clausesChanges = clausesChanges;
	}
	public Map<Integer, int[]> getOccurrenceMapChanges() {
		return occurrenceMapChanges;
	}
	public void setOccurrenceMapChanges(Map<Integer, int[]> occurrenceMapChanges) {
		this.occurrenceMapChanges = occurrenceMapChanges;
	}
	public Map<Integer, Integer> getOccurrenceNumsChanges() {
		return occurrenceNumsChanges;
	}
	public void setOccurrenceNumsChanges(Map<Integer, Integer> occurrenceNumsChanges) {
		this.occurrenceNumsChanges = occurrenceNumsChanges;
	}
	public List<Integer> getDeletedChanges() {
		return deletedChanges;
	}
	public void setDeletedChanges(List<Integer> deletedChanges) {
		this.deletedChanges = deletedChanges;
	}

    
    
}
