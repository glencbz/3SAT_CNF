package sat;

import sat.formula.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JiaHao on 18/11/14.
 */
public class AwesomeCnfParser {

    public static void main(String[] args) {
        ArrayList<Formula> parsedFormulas = AwesomeCnfParser.parseFile("src/sat/s8.cnf");
        System.out.println(parsedFormulas);

    }


    public static ArrayList<Formula> parseFile(String filename) {

        try {

            ArrayList<String> allStrings = new ArrayList<String>();

            // These two arrayLists are used to track the total number
            // of variables for each problem, with the index representing
            // the problem number

            ArrayList<Integer> allVariables = new ArrayList<Integer>();
            ArrayList<Integer> allClauses = new ArrayList<Integer>();

            Scanner in = new Scanner(new File(filename));


            /**
             * Go through file and split it into lines
             * Deal with comment strings, and split into problems
             */
            while (in.hasNext()) {
                String currentLine = in.nextLine();

                // Ignore empty lines
                if (currentLine.length() > 0) {


                    // Ignore comments
                    char cChar = 'c';
                    char pChar = 'p';
                    if (currentLine.charAt(0) == cChar) {
                        continue;

                    } else if (currentLine.charAt(0) == pChar) {
                        // Deal with problems
                        // Add new string into arraylist
                        String newString = "";
                        allStrings.add(newString);

                        // Search for decimals and use it to assign to allVariables and allClauses
                        String regex = "\\d+";
                        Pattern myPattern = Pattern.compile(regex);
                        Matcher myMatcher = myPattern.matcher(currentLine);

                        /**
                         *  Used to tell if its the first or second integer,
                         *  so that we will know whether it is no. of variables
                         *  or clauses
                         */
                        int findCounter = 0;
                        while (myMatcher.find()) {
                            Integer currentInt = new Integer(myMatcher.group(0));
                            if (findCounter == 0) {
                                allVariables.add(currentInt);
                            } else {
                                allClauses.add(currentInt);
                            }
                            findCounter += 1;
                        }

                        continue;

                    } else {

                        // Split into different problems and combines all problems to one string
                        /**
                         * Evey time a problem is found, a new empty string will be created
                         * in the allStrings.
                         *
                         * Hence, this will concatenate all strings into the last element in the
                         * allStrings array
                         */
                        String lastString;
                        /**
                         * If not the first integer in a problem, somehow this will add an additional
                         * spacing in front of the first integer in a problem element string
                          */
                        if (allStrings.get(allStrings.size()-1).length() == 0) {
                            lastString = allStrings.get(allStrings.size()-1);
                        } else {
                            lastString = allStrings.get(allStrings.size()-1) + " ";
                        }

                        // concatenates all strings of a problem into one,
                        // with clauses split by zeros
                        allStrings.remove(allStrings.size() - 1);
                        allStrings.add(lastString + currentLine);
                    }
                }
            }


            // Formula list of all formulas
            ArrayList<Formula> allFormulas = new ArrayList<Formula>();

            // At the top level, split allStrings into individual problems
            for (int i = 0; i < allStrings.size(); i++) {
                String currentString = allStrings.get(i);

                /**
                 * This splits all strings of the current problem into
                 * individual clause literal strings
                 */
                String[] clauseLiterals = currentString.split(" 0");


//                System.out.println(Arrays.toString(clauseLiterals));

                // Now we parse each clause literal string into a clause
                // do this for all clause literal strings
                Formula parsedFormula = new Formula();
                for (int j = 0; j < clauseLiterals.length; j++) {

                    Clause newClause = new Clause();

                    // This regex strings excludes individual zeros
                    // obsolete as currentString.split() above already
                    // removes zeroes
//                    String regex = "(-?[1-9]\\d*)";
                    String regex = "(-?\\d+)";

                    Pattern myPattern = Pattern.compile(regex);
                    Matcher myMatcher = myPattern.matcher(clauseLiterals[j]);

                    while (myMatcher.find()) {
                        // parses each integer into a literal and adds it into the clause
                        int n = Integer.parseInt(myMatcher.group(0));
                        Literal myLiteral;
                        if (n > 0) {
                            myLiteral = PosLiteral.make(Integer.toString(n));
                        } else {
                            myLiteral = NegLiteral.make(Integer.toString(n*-1));
                        }
                        newClause = newClause.add(myLiteral);

                    }

                    // Add all clauses into the parsedFormula, for a particular problem
                    parsedFormula = parsedFormula.addClause(newClause);
                }


                // Add all formulas into allFormulas,
                // which is to be returned

                allFormulas.add(parsedFormula);

            }

            return allFormulas;















        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File not found");
        }

    return null;


    }


}
