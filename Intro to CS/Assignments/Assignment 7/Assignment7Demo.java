public class Assignment7Demo {
   public static void main(String[] args) {
      CellSpreadSheet cellsheet = new CellSpreadSheet(6,6);
      cellsheet.setValue('A',1,"100");
      cellsheet.setValue('F',5,"55");
      System.out.println(cellsheet.getSpreadSheetAsCSV(true));
      //fill in some data on column C
      cellsheet.setValue('C',1,"10");
      cellsheet.setValue('C',2,"11");
      cellsheet.setValue('C',3,"12");
      cellsheet.setValue('C',4,"13");
      cellsheet.setValue('C',5,"14");
      cellsheet.setValue('C',6,"15");
      //print out again, showing new values
      System.out.println(cellsheet.getSpreadSheetAsCSV(true));
      cellsheet.expandColumnRange('H');
      //print out again, showing expanded spreadsheet
      System.out.println(cellsheet.getSpreadSheetAsCSV(true));
      //print out some values
      System.out.println("Value of C3 is: " + cellsheet.getValue('C',3));
      System.out.println("Value of F5 is: " + cellsheet.getValue('F',5));
      System.out.println("Value of B3 is: " + cellsheet.getValue('B',3) + "\n");
      //set some cells to formulas
      cellsheet.setValue('D',1,"C1 * F5");
      cellsheet.setValue('E',1,"D1 / C2");
      cellsheet.setValue('A',2,"A1 + C3");
      cellsheet.setValue('B',3,"C6 - C1");
      System.out.println("The value of the formula stored in D1 is: " + cellsheet.evaluateFormula('D',1) + "\n");
      System.out.println(cellsheet.getSpreadSheetAsCSV(true));
      System.out.println(cellsheet.getSpreadSheetAsCSV(false));
   }
}