import java.util.LinkedList;
import java.util.List;


public class CNFsolver {
	
	static CnfSatInstance CNF = null;
	
	public static void main(String[] args){
		CNFsolver test = new CNFsolver(CNFparser.parseDimacsCnfFile("src/cnf.txt"));
		System.out.println(test.getCNF());
		test.getCNF().computeOccurrenceMap();
		test.getCNF().createResolution();
		System.out.println(test.getCNF());
		System.out.println(test.getCNF().eliminateUnitClauses());
		System.out.println(test.getCNF());
		int[] testVars = {0,1,0,1};
		System.out.println(verifyAssignment(testVars));
	}
	
	public static int[] generateRandomAssignment(int numVars){
		int[] assignment = new int[numVars];
		for(int i = 0; i < assignment.length; i++)
			assignment[i] = Math.random() > 0.5 ? 1 : 0;
		return assignment;
	}
	
	/**
	 * Takes in an assignment in the form of an array of 0s and 1s.
	 * 
	 * @param assignment
	 * @return Whether the assignment satisfies the formula
	 */
	
	public static boolean verifyAssignment(int[] assignment){
		if(CNF.getNumVariables() != assignment.length){
			 System.err.println("Error. Assignment does not contain the required number of variables");
			 return false;
		}
		else{
			int[] satisfiedClauses = new int[CNF.getNumClauses()];
			
			for(int i = 0; i < assignment.length; i++){
				if(assignment[i] == 1){ //if i is true
					for(int j: CNF.getOccurrenceMap()[i]){ //check the list in the occurrence map corresponding to i
						satisfiedClauses[j-1] = 1; //set the appropriate entry in satisfiedClauses (the entry j - 1corresponding to the int in occurenceMap[i]) to 1
					}
				}
				if(assignment[i] == 0){
					for(int j: CNF.getOccurrenceMap()[i + CNF.getNumVariables()]){ //check the list in the occurrence map corresponding to -i
						satisfiedClauses[j-1] = 1; //set the appropriate entry in satisfiedClauses (the entry j - 1corresponding to the int in occurenceMap[i + numVars]) to 1
					}
					
				}
			}
			
			for (int i : satisfiedClauses){
				System.out.println(i);
				if (i != 1)
					return false;
			}
			return true;
		}
	}
	
	CNFsolver(CnfSatInstance CNF){
		this.CNF = CNF;
	}

	public static CnfSatInstance getCNF() {
		return CNF;
	}
	
	
}
