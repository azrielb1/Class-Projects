//Azriel Bachrach
//October 1 2020
//Intro to CS

public class TransactionBroker {

  public static void main(String[] args) {
    double balance = 500;
    boolean balanceOver500 = true;
    int penalties = 0;
    
    for (int i=0; i < args.length; i++) {

      if (isValidDouble(args[i])) {
        
        if (args.length > (i + 2)) {
         
         if (isValidOperator(args[i+1])) {
           
          if (isValidDouble(args[i+2])) { //if it is a double after an operator after a double
            balance += operation(Double.parseDouble(args[i]), Double.parseDouble(args[(i+2)]), args[(i+1)]);
            i += 2;
          } else {
            System.out.println("The "+ args[(i+1)] +" operator must be preceded by, and followed by, numeric operands");
            break;
          }

         } else {
          balance += Double.parseDouble(args[i]);
         }
        } else {
          balance += Double.parseDouble(args[i]);
         }
      } else { 
        if (isValidOperator(args[i])) {
          System.out.println("The "+ args[i] +" operator must be preceded by, and followed by, numeric operands");
          break;
        } else {
          System.out.println("\"" + args[i] +"\" Not recognized, Please only enter real numbers or operators");
          
        }
      }
      System.out.println("Your balance: $" + balance);

      if ((balance < 500) && balanceOver500) {
        System.out.println("Your last transaction lowered your balance to $" + balance);
        System.out.println("You have been charged a low-balance penalty of $20.0");
        balance -= 20;
        balanceOver500 = false;
        penalties++;
        System.out.println("Your balance: $" + balance);
      }

      if ((balance >= 500)) {
        balanceOver500 = true;
      }

      if (i == (args.length - 1)) {
        System.out.println("********************");
        System.out.println("Your final balance: $" + balance);
        System.out.println("The total you were charged in penalties: $" + (penalties * 20.00));
      }
    }

  }

  public static boolean isValidDouble(String str){
  //returns true if string can be parsed into double
      try{
        Double.parseDouble(str);
        return true;
      }catch(NumberFormatException e){
        return false;
      }
  }

  public static boolean isValidOperator(String operator){
  //returns true if operator is a valid operator
    if (operator.equalsIgnoreCase("sub") || operator.equalsIgnoreCase("mul") || operator.equalsIgnoreCase("div") || operator.equalsIgnoreCase("mod") || operator.equalsIgnoreCase("pow") || operator.equalsIgnoreCase("add") ) {
      return true;
    }else{
     return false;
    }
  }

  public static double operation(double op1, double op2, String operator){
  //returns the result of the operation specified by operator being run with op1 and op2 as its operands
    if (operator.equalsIgnoreCase("sub")) {
      return (op1 - op2);
    }else if (operator.equalsIgnoreCase("mul")) {
      return (op1 * op2);
    }else if (operator.equalsIgnoreCase("div")) {
      return (op1 / op2);
    }else if (operator.equalsIgnoreCase("mod")) {
      return (op1 % op2);
    }else if (operator.equalsIgnoreCase("pow")) {
      return exponentiate(op1, op2);
    }else if (operator.equalsIgnoreCase("add")) {
      return (op1 + op2);
    }else {
      return 0;
    }
  }

  public static double exponentiate(double base, double exponent) {
  //returns the result of raising "base" to the "exponent" power
    double result = 1;
    for (int count = 0; count < exponent; count++) {
      result *= base;
    }
    return result;
  }

}