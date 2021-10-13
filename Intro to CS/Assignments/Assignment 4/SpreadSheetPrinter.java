//Azriel Bachrach
//October 15 2020
//Intro to CS

import java.util.Arrays;

public class SpreadSheetPrinter {

   public static void main(String[] args) {
      
      if (args.length % 2 == 1) {
         System.out.println("Invalid input: must specify the spreadsheet range, followed by cell-value pairs. You entered an odd number of inputs.");
         return;
      }

      if (args.length < 2) {
         System.out.println("Invalid input: must specify at least highest column and row.");
         return;
      }

      if (!(validateRange(args))) {
         System.out.println("Please specify a valid spreadsheet range, with highest column between A and Z and highest row as an integer");
         return;
      }
      
      char lastColumn = (args[0].charAt(0));
      int lastRow = (getInteger(args[1]));
      String[] cellInputs = Arrays.copyOfRange(args, 2, args.length);

      if ((validateAllCellLabels(cellInputs, lastColumn, lastRow)) != null) {
         System.out.println("Invalid cell label: " + validateAllCellLabels(cellInputs, lastColumn, lastRow));
         return;
      }

      if ((validateAllCellValues(cellInputs)) != null) {
         System.out.println("Invalid cell value: " + validateAllCellValues(cellInputs));
         return;
      }

      printColumnHeaders(lastColumn, lastRow);

      for (int row=1; row <= lastRow; row++) {
         System.out.print(row + "\t"); //prints the row number
         for (char col='A'; col <= lastColumn; col++) {
            System.out.print(getCellValue(col, row, cellInputs) + "\t");
         }
         System.out.println(); //Go to next row
      }
   }

   //Returns the value of the specified cell or a space if there is no value
   public static String getCellValue(char col, int row, String[] input) {
      for (int i=0; i < input.length; i+=2) {
         if (isCurrent(col, row, input[i])) {
            return input[i+1];
         }
      }
      return " ";
   }

   //Returns true if cellLabel refers to cell col row 
   public static boolean isCurrent(char col, int row, String cellLabel) {
      if (cellLabel.equalsIgnoreCase(col + Integer.toString(row))) {
         return true;
      } else {
         return false;
      }
   }

   //Prints out the column headers for the spreadsheet
   public static void printColumnHeaders(char lastColumn, int lastRow) {
      int numberOfSpaces = String.valueOf(lastRow).length();
      for (int i=0; i < numberOfSpaces; i++) {
         System.out.print(" ");
      }
      for (char letter = 'A'; letter <= lastColumn; letter++) {
         System.out.print("\t" + letter);
      }
      System.out.println();
   }

   //Returns the integer form of the String or -1 if not an integer
   public static int getInteger(String arg) {
      try{
        Integer.parseInt(arg);
        return Integer.parseInt(arg);
      }catch(NumberFormatException e){
        return (-1);
      }
   }

   //Makes sure all cell labels are valid
   public static String validateAllCellLabels(String[] input, char lastCol, int lastRow) {
      for (int i=0; i < input.length; i+=2) {
         if ((input[i].charAt(0)) < 65 || (input[i].charAt(0)) > 90) {
            return input[i];
         } else if ((input[i].charAt(0)) > lastCol) {
            return input[i];
         } else if (getInteger((input[i].subSequence(1, input[i].length())).toString()) == -1 ) {
            return input[i];
         } else if (getInteger((input[i].subSequence(1, input[i].length())).toString()) >  lastRow) {
            return input[i];
         }
      }
      return null;
   }

   //Makes sure all cell values are valid numbers
   public static String validateAllCellValues(String[] input) {
      for (int i=1; i < input.length; i+=2) {
         if (!(isValidDouble(input[i]))) {
            return input[i];
         }
      }
      return null;
   }

   //Returns true if args[0] is a valid column label and if args[1] is a valid row label
   public static boolean validateRange(String[] args) {
      if ((args[0].charAt(0)) < 65 || (args[0].charAt(0)) > 90) {
         return false;
      } else if (!((getInteger(args[1])) >= 1)) {
         return false;
      } else if ((args[0].length()) > 1) {
         return false;
      } else return true;
   }

   //returns true if string can be parsed into double
   public static boolean isValidDouble(String arg) {
      try{
        Double.parseDouble(arg);
        return true;
      }catch(NumberFormatException e){
        return false;
      }
   }
}