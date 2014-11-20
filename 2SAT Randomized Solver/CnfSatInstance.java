package project2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CnfSatInstance
{
	private int numClauses;
	private int numVars;

	protected List<List<Integer>> clauses;

	//  entries [0,...,numVars-1] for positive literals
	//  entries [numVars,...,2*numVars-1] for negative literals

	public CnfSatInstance(){
		setNumClauses(0);
		setNumVars(0);
		clauses = new ArrayList<List<Integer>>();
	}

	public boolean parseRandom(){
		ArrayList<Boolean> booleanClause = new ArrayList<Boolean>(); //arraylist to hold boolean values for class
		ArrayList<Boolean> booleanVariable = new ArrayList<Boolean>(); //arraylist to hold boolean values for variables
		List<Integer> randomClause = new ArrayList<Integer>(); //arraylist to hold a random clause to have its literals flipped
		ArrayList<List<Integer>> failedClauses = new ArrayList<List<Integer>>(); //arraylist tohold all failed clauses, where a random one will be picked

//		Creating an array of boolean values for each of the clauses. Initialized to true.
		for(int i = 0;i<getNumClauses();i++){
			booleanClause.add(true);
		}
		
//		Creating an array of boolean values for each of the variables. Set all variables to true first.
		for(int i = 0;i<getNumVars();i++){
			booleanVariable.add(true);
		}
		
		int outerCount = 1; //set count for outer loop
		boolean solved = false; //set boolean status to check if all clauses have been satisfied to break out of loop
		

		while(outerCount <= Math.log(getNumClauses())/Math.log(2)) { //loop for log n with base 2
			int innerCount = 1; //set count for inner loop
			while(innerCount <= 2 * getNumClauses() * getNumClauses()) {
				failedClauses.clear(); //clear all failed clauses as you don't want to keep adding
				for (List<Integer> c : getClauses()) { //a foreach loop to go through every clause and evaluate it
					int c0 = c.get(0); //get the first literal of a clause
					int c1 = c.get(1); //get the second literal of a clause
					boolean a1 = booleanClause.get(Math.abs(c0)); //get the boolean status of a clause
					boolean b1 = booleanClause.get(Math.abs(c1)); //get the boolean status of a clause
					if(!evaluator(c0,c1,a1, b1)) { //run an evaluator on it, and if it fails, 
						failedClauses.add(c); //add the clause to the failed clauses list
					}

				}
				if(!failedClauses.isEmpty()) { //if there are clauses in failed clause list,
					Collections.shuffle(failedClauses); //shuffle the failed clauses to get a random clause
					randomClause.clear(); //remove the last randomclause
//					from the first failed clause (as it is shuffled, it does not matter),
					randomClause.add(failedClauses.get(0).get(0)); //take the first literal
					randomClause.add(failedClauses.get(0).get(1)); //take the second literal
				}

				else { // if there are no clauses in the failedclauses, i.e. no clause failed
					solved = true; //mark boolean value as true,
					break; //and break out of the current loop
				}
				int randomVariable = new Random().nextInt(2); //randomly generate a variable between 0 and 1. 
				int random0 = randomClause.get(0); //get the first literal from the random clause
				int random1 = randomClause.get(1); //get the second literal from the random clause
				if(randomVariable == 1) {
					//flip the bits for the boolean variables in the clause and variable array
					Boolean b = booleanClause.get(Math.abs(random0)); 
					booleanClause.set(Math.abs(random0), !b);
					Boolean a = booleanVariable.get(Math.abs(random0)-1);
					booleanVariable.set(Math.abs(random0)-1, !a);
				}
				else {
					//flip the bits for the boolean variables in the clause and variable array
					Boolean b = booleanClause.get(Math.abs(random1));
					booleanClause.set(Math.abs(random1), !b);
					Boolean a = booleanVariable.get(Math.abs(random1)-1);
					booleanVariable.set(Math.abs(random1)-1, !a);
				}
				//increment inner loop counter
				innerCount++;
			}
			//break out of loop if solved
			if(solved){
				break;
			}
			//increment outer loop counter
			outerCount++;
		}
		//so if the SAT is satisfiable, print out the solution array
		if (solved){
			for (int i = 0; i<booleanVariable.size();i++){
				if (booleanVariable.get(i)){ //if the boolean status of the variable is true, 
					System.out.print("1 "); //print 1
				}
				else { //else print 0
					System.out.print("0 ");
				}
			}
		}
		return solved; //return the boolean status of the satisfiability
	}


	public boolean evaluator(int a, int b, boolean a1, boolean b1) {
		if (a < 0){ //so if a is negative, you want the real boolean value of it. meaning you need to flip the boolean values
			a1 = !a1;
		}
		if (b < 0){ //likewise if b is negative
			b1 = !b1;
		}
		//return a1 or b1 as the clause is an OR of the variables
		return a1 || b1;
	}


	public int getNumClauses()
	{
		return numClauses;
	}

	public List<List<Integer>> getClauses()
	{
		return clauses;
	}

	public void setNumVars(int numVars)
	{
		this.numVars = numVars;
	}

	public int getNumVars()
	{
		return numVars;
	}

	public void setNumClauses(int numClauses)
	{
		this.numClauses = numClauses;
	}
}
