package arrayForm;


public class Tester {
	public static void main(String[] args){
		CNFSatInstance test = CNFparser.parseDimacsCnfFile("src/testcase.cnf");
		for(int[] l : test.getClauses()){
			for(int i : l){
				System.out.print(i + " ");
			}
			System.out.println("");
		}
		
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
		
		System.out.println(".....");
		//System.out.println(test.cachedSimplify(test.getClauses()));
	}
}