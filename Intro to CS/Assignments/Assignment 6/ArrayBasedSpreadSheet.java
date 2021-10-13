//Azriel Bachrach
//November 11 2020
//Intro to CS

import java.util.Arrays;

public class ArrayBasedSpreadSheet {
   
   private Object[][] spreadSheet;

   public ArrayBasedSpreadSheet(int rows, int columns) {
     this.spreadSheet = new Object[columns][rows];
   }

   public String getSpreadSheetAsCSV(boolean showFormulas) {
      String csvOutput = "";
      String columnHeaders = "";

      for (char letter = 'A'; columnIndex(letter) < spreadSheet.length; letter++) {
         columnHeaders = columnHeaders.concat(letter + ((columnIndex(letter) != spreadSheet.length - 1) ? "," : ""));
      }
      columnHeaders = columnHeaders.concat("\n");

      for (int row = 0; row < spreadSheet[0].length; row++) {
         for (int column = 0; column < spreadSheet.length; column++) {
            if (spreadSheet[column][row] == null) {
               csvOutput = csvOutput.concat("0.0");
            }
            else if (spreadSheet[column][row] instanceof Double) {
               csvOutput = csvOutput.concat(String.valueOf(spreadSheet[column][row]));
            }
            else if (spreadSheet[column][row] instanceof String) {
               if (showFormulas) {
                  csvOutput = csvOutput.concat((String)spreadSheet[column][row]);
               }
               else if (!showFormulas) {
                  csvOutput = csvOutput.concat(String.valueOf(getValue((char)('A' + column), row + 1)));
               }
            }
               csvOutput = csvOutput.concat((column != (spreadSheet.length - 1)) ? "," : "\n");
         }
      }
      return columnHeaders.concat(csvOutput);
   }

   public void setValue(char column, int row, String value) {
      if (columnIndex(column) >= spreadSheet.length) {
         this.expandColumnRange(column);
      }

      if (row > spreadSheet[0].length) {
         this.expandRowRange(row);
      }

      this.spreadSheet[columnIndex(column)][row - 1] = isValidDouble(value) ? Double.parseDouble(value) : value;
   }

   public Object[][] getCopyOfData() {
      Object[][] temp = new Object[spreadSheet.length][spreadSheet[0].length];
      for (int i = 0; i < spreadSheet.length; i++) {
         for (int j = 0; j < spreadSheet[i].length; j++) {
            temp[i][j] = spreadSheet[i][j];
         }
      }
      return temp;
   }

   public void expandColumnRange(char column) {
      if (columnIndex(column) < spreadSheet.length) {
         return;
      }

      Object[][] temp = new Object[columnIndex(column) + 1][spreadSheet[0].length];

      for (int i = 0; i < spreadSheet.length; i++) {
         for (int j = 0; j < spreadSheet[i].length; j++) {
            temp[i][j] = spreadSheet[i][j];
         }
      }
      spreadSheet = temp;
   }

   public void expandRowRange(int rows) {
      if (rows <= spreadSheet[0].length) {
         return;
      }

      Object[][] temp = new Object[spreadSheet.length][rows];

      for (int i = 0; i < spreadSheet.length; i++) {
         for (int j = 0; j < spreadSheet[i].length; j++) {
            temp[i][j] = spreadSheet[i][j];
         }
      }
      
      spreadSheet = temp;
   }

   public Object[] getCopyOfColumnThroughRow(char c, int throughRow) {
      Object[] copyOfColumn = new Object[throughRow];

      for (int i = 0; i < throughRow; i++) {
         if (i < spreadSheet[0].length) {
            copyOfColumn[i] = this.spreadSheet[columnIndex(c)][i];
         }
         else {
            copyOfColumn[i] = null;
         }
      }
      return copyOfColumn;
   }

   public double getValue(char column,int row) {
      int currentColumnIndex = columnIndex(column);
      int currentRowIndex = row - 1;

      if (currentColumnIndex >= spreadSheet.length) {
         return 0;
      }
      else if (row > spreadSheet[0].length) {
         return 0;
      }
      else if (spreadSheet[currentColumnIndex][currentRowIndex] instanceof Double) {
         return (Double)spreadSheet[currentColumnIndex][currentRowIndex];
      }
      else if (spreadSheet[currentColumnIndex][currentRowIndex] instanceof String) {
         return evaluateFormula(column, row);
      }
      else {
         return 0;
      }
   }

   public double getValue(String cell) {
      char column = cell.charAt(0);
      int row = Integer.parseInt(cell.substring(1));
      return this.getValue(column, row);
   }

   public double evaluateFormula(char column,int row) {
      String str = (String)this.spreadSheet[columnIndex(column)][row - 1];
      String[] formula = str.split(" ");
      double result = Double.MIN_VALUE;
      switch (formula[1]) {
         case "+": 
            result = getValue(formula[0]) + getValue(formula[2]);
            break;
         case "-":
            result = getValue(formula[0]) - getValue(formula[2]);
            break;
         case "*":
            result = getValue(formula[0]) * getValue(formula[2]);
            break;
         case "/":
            result = getValue(formula[0]) / getValue(formula[2]);
            break;
      }
      return result;
   }

   private static boolean isValidDouble(String arg) {
      try{
        Double.parseDouble(arg);
        return true;
      }catch(NumberFormatException e){
        return false;
      }
   }

   private static int columnIndex(char column) {
      return column - 65;
   }

   public String toString() {
   	return this.getSpreadSheetAsCSV(false);
   }
}