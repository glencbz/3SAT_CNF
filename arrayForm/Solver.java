package arrayForm;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Solver {
	
	CNFSatInstance CNF = null;
		
	public Solver(CNFSatInstance cNF) {
		CNF = cNF;
	}



	public CNFSatInstance getCNF() {
		return CNF;
	}



	public void setCNF(CNFSatInstance cNF) {
		CNF = cNF;
	}



	public int[] randomVector(int numVars){
		int[] ret = new int[numVars];
		for(int i = 0; i < numVars; i++){
			ret[i] = Math.random() > 0.5 ? 1 : -1;
		}
		return ret;
	}
	
	public int[] PPSZ(int[] initialAssignment){
		int numVars = CNF.getNumVars();
		List<Integer> pi = permutatePi(numVars);
		
		int[] assignments = new int[numVars];
		int[] knownAssignments = CNF.getKnownAssignments();
		
		for (int i = 0; i < knownAssignments.length; i++) //copying of array to prevent modification of knownAssignments array
			assignments[i] = knownAssignments[i];
		
		for(int i : pi){
			if(CNF.hasEmptyClause())
				break;
			if(assignments[i-1] == 0){ //if the entry in the array of assignments is not already assigned
				if(CNF.getOccurrenceNums()[i - 1] == 1)
					assignments[i-1] = 1;
				else if(CNF.getOccurrenceNums()[i + numVars - 1] == 1)
					assignments[i-1] = -1;
				else
					assignments[i-1] = initialAssignment[i-1];
				
			}
			CNF.givenVarMutator(assignments[i-1] * i);
		}
		
		return assignments;
	}
	
	public int[] randomWalk(int[] initialAssignment){
		for (int i = 0; i < initialAssignment.length; i ++){
			if(CNF.getKnownAssignments()[i] != 0)
				initialAssignment[i] = CNF.getKnownAssignments()[i];
		}
		System.out.println(Arrays.toString(initialAssignment));
		int[] assignment = new int[initialAssignment.length] ;
		for(int i = 0; i < initialAssignment.length; i++)
			assignment[i] = initialAssignment[i];
		Set<Integer> unsatisfiedClauses = CNF.getUnsatisfiedClauses();
		for(int i = 0; i <= 3 * CNF.getNumVars(); i++){
			if(!verifyAssignment(assignment)){
				Collections.shuffle(unsatisfiedClauses);
				System.out.println(unsatisfiedClauses);
				int clauseToFlip= unsatisfiedClauses.get(0);
				System.out.println(clauseToFlip);
				int posToFlip = new Random().nextInt(CNF.maxClauseSize);
				int varToFlip = Math.abs(CNF.getClauses()[clauseToFlip][posToFlip]);
				if(varToFlip != 0){
					if(CNF.getKnownAssignments()[varToFlip - 1] == 0){
						assignment[varToFlip - 1] = -assignment[varToFlip - 1];
						if(assignment[varToFlip - 1] == -1){
							CNF.removeFromUnsatisfiedClauses(unsatisfiedClauses, Math.abs(varToFlip));
							CNF.restoreToUnsatisfiedClauses(unsatisfiedClauses, -Math.abs(varToFlip));
						}
						if(assignment[varToFlip - 1] == 1){
							CNF.removeFromUnsatisfiedClauses(unsatisfiedClauses, -Math.abs(varToFlip));
							CNF.restoreToUnsatisfiedClauses(unsatisfiedClauses, Math.abs(varToFlip));
						}
					}
				}
			}
			else
				break;
		}
		return assignment;
	}

	public int[] Rolf06(){
		int[] initialAssignment = randomVector(CNF.getNumVars());
		int[] ans = PPSZ(initialAssignment);
		if (verifyAssignment(ans))
			return ans;
		System.out.println(Arrays.toString(ans));
		CNF.undoAllChanges();
		ans = randomWalk(initialAssignment);
		System.out.println(Arrays.toString(ans));
		if (verifyAssignment(ans))
			return ans;
		return null; 
	}

	public boolean verifyAssignment(int[] assignment){
		if(CNF.getNumVars() != assignment.length){
			 System.err.println("Error. Assignment does not contain the required number of variables");
			 return false;
		}
		else{
			int[] satisfiedClauses = new int[CNF.getNumClauses()];
			
			for(int i = 0; i < assignment.length; i++){
				if(assignment[i] == 1){ //if i is true
					for(int j: CNF.getOccurrenceMap()[i]){ //check the list in the occurrence map corresponding to i
						if(j == 0)
							break;
						satisfiedClauses[j-1] = 1; //set the appropriate entry in satisfiedClauses (the entry j - 1corresponding to the int in occurenceMap[i]) to 1
					}
				}
				if(assignment[i] == -1){
					for(int j: CNF.getOccurrenceMap()[i + CNF.getNumVars()]){ //check the list in the occurrence map corresponding to -i
						if(j == 0)
							break;
						satisfiedClauses[j-1] = 1; //set the appropriate entry in satisfiedClauses (the entry j - 1corresponding to the int in occurenceMap[i + numVars]) to 1
					}
					
				}
			}
			
			for (int i : satisfiedClauses){
				if (i != 1)
					return false;
			}
			return true;
		}
	}
	

	
	public List<Integer> permutatePi(int numVars){
		List<Integer> ret = new LinkedList<Integer>();
		for(int i = 1; i <= numVars; i++)
			ret.add(i);
		Collections.shuffle(ret);
		return ret;
	}
}
