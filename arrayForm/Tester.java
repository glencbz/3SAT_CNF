package arrayForm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Tester {
	public static void main(String[] args){
		CNFSatInstance test = CNFparser.parseDimacsCnfFile("src/largeSat.cnf");
		//System.out.println(test);
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