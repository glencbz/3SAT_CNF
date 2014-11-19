package ArrayFormDPLL;


public class Tester {
	public static void main(String[] args) {
		CNFSatInstance test = CNFparser.parseDimacsCnfFile("src/sat/largeunsat.cnf");
		for (int[] l : test.getClauses()) {
			for (int i : l) {
				System.out.print(i + " ");
			}
			System.out.println("");
		}

		System.out.println("......");
	}

}
/*		for(int i : test.getOccurringClauses(test.getClauses(), 3))
			System.out.println(i);
		
		System.out.println("end of cocurring clauses");
		System.out.println(test.getNumOccurringClauses(test.getClauses(


		*/

// 3 2 1
// 3 1 5