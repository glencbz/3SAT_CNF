package arrayForm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Tester {
	public static void main(String[] args){
		CNFSatInstance test = CNFparser.parseDimacsCnfFile("src/cnf.txt");
		CNFSatInstance given1 = test.givenVarOccur(test.getClauses(), 4);
		
		for(int[] l : test.getClauses()){
			for(int i : l){
				System.out.print(i + " ");
			}
			System.out.println("");
		}
		System.out.println("....");

		for(int[] l : test.getOccurrenceMap()){
			for(int i : l){
				System.out.print(i + " ");
			}
			System.out.println("");
		}		

		System.out.println(Arrays.toString(test.getOccurrenceNums()));
		
		for(int[] l : given1.getClauses()){
			for(int i : l){
				System.out.print(i + " ");
			}
			System.out.println("");
		}
		
		System.out.println("...");
		
		System.out.println(Arrays.toString(test.getDeleted()));
		System.out.println(Arrays.toString(given1.getDeleted()));
		
		System.out.println("......");
		int[] occurrenceNumMap = new int[test.getNumVars() * 2];
		for(int[] l : test.computeOccurrenceMap(test.getClauses(), test.getNumVars(), occurrenceNumMap)){
			System.out.println(Arrays.toString(l));
		}
		
		System.out.println(Arrays.toString(occurrenceNumMap));
		
		System.out.println("......");
	
/*		for(int i : test.getOccurringClauses(test.getClauses(), 3))
			System.out.println(i);
		
		System.out.println("end of cocurring clauses");
		System.out.println(test.getNumOccurringClauses(test.getClauses(), -3));*/
		
		for(int[] l: test.simplify(test.getClauses())){
			for(int i : l){
				System.out.print(i + " ");
			}
			System.out.println("");
		}
		

	}
}