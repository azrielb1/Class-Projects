//Azriel Bachrach
//November 25 2020
//Intro to CS

public class FormulaCell implements Cell {

   private String formula;
   private CellProvider cellProvider;

   public FormulaCell(String f, CellProvider c) {
      this.formula = f;
      this.cellProvider = c;
   }

   public String getStringValue() {
      return formula;
   }

   public double getNumericValue() {
      String[] brokenFormula = formula.split(" ");
      Cell cell1 = cellProvider.getCell(brokenFormula[0].charAt(0), Integer.parseInt(brokenFormula[0].substring(1)));
      Cell cell2 = cellProvider.getCell(brokenFormula[2].charAt(0), Integer.parseInt(brokenFormula[2].substring(1)));
      double cell1value = 0;
      double cell2value = 0;
      if (cell1 != null) {
         cell1value = cell1.getNumericValue();
      }
      if (cell2 != null) {
         cell2value = cell2.getNumericValue();
      }
      double result = Double.MIN_VALUE;
      switch (brokenFormula[1]) {
         case "+": 
            result = cell1value + cell2value;
            break;
         case "-":
            result = cell1value - cell2value;
            break;
         case "*":
            result = cell1value * cell2value;
            break;
         case "/":
            result = cell1value / cell2value;
            break;
      }
      return result;
   }
}