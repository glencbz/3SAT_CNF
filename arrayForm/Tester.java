package arrayForm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Tester {
	public static void main(String[] args){
		CNFSatInstance test = CNFparser.parseDimacsCnfFile("src/cnf.txt");
		System.out.println(test);
		System.out.println(Arrays.toString(test.getOccurrenceNums()));

		System.out.println("....");
		CNFSatInstance given1 = test.givenVarMutator(1);

		System.out.println(given1);
		System.out.println(Arrays.toString(given1.getOccurrenceNums()));
		System.out.println("....");
		
		given1.undoChanges();
		System.out.println(given1);
		System.out.println(Arrays.toString(given1.getOccurrenceNums()));
		System.out.println("....");
		
		
		long started = System.nanoTime();
		CNFSatInstance simplified = test.simplify();
		
		long time = System.nanoTime();
	long timeTaken = time - started;
	System.out.println("Time:" + timeTaken/1000000.0 + "ms");
	System.out.println(simplified);
	System.out.println("...");
	}
}