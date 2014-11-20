package arrayForm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Tester {
	public static void main(String[] args){
		CNFSatInstance test = CNFparser.parseDimacsCnfFile("src/testcase.cnf");
		Solver solver = new Solver(test);
		System.out.println(solver.getCNF());
		System.out.println(solver.getCNF().getUnsatisfiedClauses());
		System.out.println("..");
		solver.getCNF().simplify();
		System.out.println(solver.getCNF().getUnsatisfiedClauses());
		System.out.println("..");

		System.out.println(solver.getCNF());
		System.out.println(Arrays.toString(solver.getCNF().getKnownAssignments()));
		System.out.println(Arrays.toString(solver.Rolf06()));
	}
}