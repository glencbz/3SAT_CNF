import java.util.Arrays;


public class Main {
	public static void main(String[] args){
		CNFsolver test = new CNFsolver(CNFparser.parseDimacsCnfFile("src/cnf.txt"));
		System.out.println(test.getCNF());
		
		int[] initialGuess = test.randomVector(test.getCNF().getNumVars());
		
		long started = System.nanoTime();
		
		int[] assignment = null;
		if (!test.getCNF().hasEmptyClauses()){
			for(int i = 0; i <= 100; i++){
				assignment = Rolf06(test);
				if (assignment != null)
					break;
			}
		}
		else{
			System.out.println("unsat");
		}
		if(assignment != null)
			System.out.println(Arrays.toString(assignment));
		
		long time = System.nanoTime();
		long timeTaken = time - started;
		System.out.println("Time:" + timeTaken/1000000.0 + "ms");
	}
	
	public static boolean randomWalkTest(CNFsolver test, int[] initialGuess){
		int[] initialAssignment = initialGuess;
		
		int[] guess = test.randomWalk(initialAssignment);
		if (test.verifyAssignment(guess)){
			System.out.println("SATISFIED BITCH");
			for(int i : guess)
				System.out.print(i + ",");
			System.out.println("");
		}
		else
			System.out.println("Unsatisfiable :(");
		return test.verifyAssignment(test.randomWalk(test.randomVector(test.getCNF().getNumVars())));
	}

    public static boolean PPSZsearch(CNFsolver test, int[] initialGuess){
    	//TODO: fix the problem that creating the resolution tends to make the processing slower
    	test.getCNF().oneRoundResolution();
    	int numVars = test.getCNF().getNumVars();
    	boolean satisfied = false;
    	for(int i = 0; i <= 1000; i++){
    		int [] guess = test.PPSZmodify(initialGuess);
    		if(test.verifyAssignment(guess)){
    			satisfied = true;
    			System.out.println("SATISFIED BIATCH");
    			for (int j : guess)
    				System.out.print(j + ",");
    			System.out.println("");
    			return true;
    		}
    	}
    	System.out.println(satisfied ? "": "UNSATISFIABLE :(");
    	return false;
    }
	public static int[] Rolf06(CNFsolver test){
		int[] initialAssignment = test.randomVector(test.getCNF().getNumVars());
		int[] ans = test.PPSZmodify(initialAssignment);
		if (test.verifyAssignment(ans))
			return ans;
		ans = test.randomWalk(initialAssignment);
		if (test.verifyAssignment(ans))
			return ans;
		return null; 
	}
}
