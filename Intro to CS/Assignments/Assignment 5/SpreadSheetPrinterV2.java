//Azriel Bachrach
//November 8 2020
//Intro to CS

import java.util.Arrays;

public class SpreadSheetPrinterV2 {

   public static final int PP = 0;
   public static final int CSV = 1;
   public static int FORMAT;

   public static void main(String[] args) {

      if (args.length < 3) {
         System.out.println("Invalid input: must specify at least a format (csv or pp), as well as the highest column and row");
         return;
      }

      if (!((args[0].equals("csv")) || (args[0].equals("pp")))) {
      	System.out.println("The first argument must specify either csv or pp");
      	return;	
      }

      if (!(validateRange(args))) {
         System.out.println("Please specify a valid spreadsheet range, with highest column between A and Z and highest row as an integer > 0");
         return;
      }
      
      if (args.length % 2 == 0) {
         System.out.println("Invalid input: must specify the format, spreadsheet range, and then cell-value pairs. You entered an even number of inputs");
         return;
      }
      
      FORMAT = (args[0].equals("csv")) ? CSV : PP;
      char lastColumn = (args[1].charAt(0));
      int lastRow = (getInteger(args[2]));
      String[] cellInputs = Arrays.copyOfRange(args, 3, args.length);

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
         if (FORMAT == PP) {
            System.out.print(row + "\t"); //prints the row number
            for (char col='A'; col <= lastColumn; col++) {
               System.out.print(getCellValue(col, row, cellInputs) + "\t");
            }
         }
         else if (FORMAT == CSV) {
           for (char col='A'; col <= lastColumn; col++) {
               System.out.print(
                  ((getCellValue(col, row, cellInputs)).equals(" ") ? "" : getCellValue(col, row, cellInputs)) + ((col != lastColumn) ? "," : ""));
            } 
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
      if (FORMAT == PP) {
         for (int i=0; i < numberOfSpaces; i++) {
            System.out.print(" ");
         }
         for (char letter = 'A'; letter <= lastColumn; letter++) {
            System.out.print("\t" + letter);
         }
      }
      else if (FORMAT == CSV) {
         for (char letter = 'A'; letter <= lastColumn; letter++) {
            System.out.print(letter + ((letter != lastColumn) ? "," : ""));
         }
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

   //Returns true if args[1] is a valid column label and if args[2] is a valid row label
   public static boolean validateRange(String[] args) {
      if ((args[1].charAt(0)) < 65 || (args[1].charAt(0)) > 90) {
         return false;
      } else if (!((getInteger(args[2])) >= 1)) {
         return false;
      } else if ((args[1].length()) > 1) {
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