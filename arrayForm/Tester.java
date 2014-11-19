package arrayForm;


public class Tester {
	public static void main(String[] args){
		CNFSatInstance test = CNFparser.parseDimacsCnfFile("src/cnf.txt");
		for(int[] l : test.getClauses()){
			for(int i : l){
				System.out.print(i + " ");
			}
			System.out.println("");
		}
		
		System.out.println("......");
		System.out.println("problem");
		System.out.println(test.eliminateUnitClauses(test.getClauses()).length);
		System.out.println("problem");
		for(int[] l: test.simplify(test.eliminateUnitClauses(test.getClauses()))){
			for(int i : l){
				System.out.print(i + " ");
			}
			System.out.println("");
		}
		
	}
}