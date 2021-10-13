//Azriel Bachrach
//November 25 2020
//Intro to CS

import java.util.Arrays;

public class CellSpreadSheet implements CellProvider {

   private Cell[][] spreadSheet;

   public CellSpreadSheet(int columns, int rows) {
      this.spreadSheet = new Cell[columns][rows];
   }

   public String getSpreadSheetAsCSV(boolean showFormulas) {
      String csvOutput = "";
      String columnHeaders = "";

      for (char letter = 'A'; columnIndex(letter) < spreadSheet.length; letter++) {
         columnHeaders = columnHeaders.concat(letter + ((columnIndex(letter) != spreadSheet.length - 1) ? "," : "\n"));
      }

      for (int row = 0; row < spreadSheet[0].length; row++) {
         for (int column = 0; column < spreadSheet.length; column++) {
            if (spreadSheet[column][row] == null) {
               csvOutput = csvOutput.concat("0.0");
            }
            else if (spreadSheet[column][row] instanceof Cell) {
               if (showFormulas) {
                  csvOutput = csvOutput.concat(spreadSheet[column][row].getStringValue());
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

      if (isValidDouble(value)) {
         this.spreadSheet[columnIndex(column)][row - 1] = new DoubleCell(Double.parseDouble(value));
      } else {
         this.spreadSheet[columnIndex(column)][row - 1] =  new FormulaCell(value, this);
      }
   }

   public Cell[][] getCopyOfData() {
      Cell[][] temp = new Cell[spreadSheet.length][spreadSheet[0].length];
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

      Cell[][] temp = new Cell[columnIndex(column) + 1][spreadSheet[0].length];

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

      Cell[][] temp = new Cell[spreadSheet.length][rows];

      for (int i = 0; i < spreadSheet.length; i++) {
         for (int j = 0; j < spreadSheet[i].length; j++) {
            temp[i][j] = spreadSheet[i][j];
         }
      }
      
      spreadSheet = temp;
   }

   public Cell[] getCopyOfColumnThroughRow(char c, int throughRow) {
      Cell[] copyOfColumn = new Cell[throughRow];

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
      else if (spreadSheet[currentColumnIndex][currentRowIndex] != null) {
         return spreadSheet[currentColumnIndex][currentRowIndex].getNumericValue();
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

   public Cell getCell(char column, int row) {
      if (columnIndex(column) < spreadSheet.length && row <= spreadSheet[0].length) {
         return spreadSheet[columnIndex(column)][row - 1]; 
      } else {
         return null;
      }
   }

   public double evaluateFormula(char column,int row) {
      return this.getValue(column, row);
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
}
