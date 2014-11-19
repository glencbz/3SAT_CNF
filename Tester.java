package arrayForm;


public class Tester {
	public static void main(String[] args){
		CNFSatInstance test = CNFparser.parseDimacsCnfFile("src/largeUnsat.cnf");
		for(int[] l : test.getClauses()){
			for(int i : l){
				System.out.print(i + " ");
			}
			System.out.println("");
		}
		
		System.out.println("......");

		System.out.println(test.simplify(test.getClauses()).length);
		for(int[] l: test.simplify(test.getClauses())){
			for(int i : l){
				System.out.print(i + " ");
			}
			System.out.println("");
		}
		
	}
}