import sys

# There are 3 global variables to define:
#  num_columns: an integer giving the number of columns
#  num_rows: an integer giving the number of rows
#  sheet_contents: a dict storing all non-default spreadsheet contents explicitly.
#            The keys are (column, row) tuples, with integer column and row values where
#            1, not 0, is the first row/column. The dict values are the cell values.
#            Any cell value that parses as an integer should be stored
#            as an integer, otherwise as a string.  For any cells not explicitly set to
#            values, the dictionary should return a single blank space (" "). (Check
#            the documentation of the get() method for dicts.)
#       get(keyname, default)
#
num_columns = -1  # initial value
num_rows = -1    # initial value
sheet_contents = {}


# Examines sys.argv and sets up the 3 global variables that represent the spreadsheet.
# This function must validate all input before populating the dictionary.
# @param args a string array that defaults to sys.argv but can be overridden
# @return True if all data validates, False otherwise
#
def init_spreadsheet(args=sys.argv):
    # validate input
    if len(args) < 3:
        print("Invalid input: must specify at least highest column and row.")
        return False
    
    if not validateRange():
        print("Please specify a valid spreadsheet range, with highest column between A and I and highest row as an integer between 1 and 9")
        return False

    if len(args) % 2 == 0:
        print("Invalid input: must specify the spreadsheet range, followed by cell-value pairs. You entered an odd number of inputs.")
        return False

    if validate_all_cell_labels(args[3:], args[1], int(args[2])) != None:
        print("Invalid cell label: " + validate_all_cell_labels(args[3:], args[1], int(args[2])))
        return False

    if validate_all_cell_values(args[3:]) != None:
        print("Invalid cell value: " + validate_all_cell_values(args[3:]))
        return False
    
    global num_columns
    num_columns = ord(args[1]) - 64

    global num_rows
    num_rows = int(args[2])

    for i in range(3, len(args), 2):
        row = int(args[i][1:])
        col = ord(args[i][0]) - 64
        sheet_contents[(col, row)] = get_integer(args[i+1])
    
    return True


# @param col the column of the desired cell (first column is 'A', not 1 or 0)
# @param row the row of the desired cell (first row is 1, not 0)
# @return the value of the given cell found in contents. It should not throw an error #
def get_cell_value(col, row):
    col_num = ord(col) - 64
    return sheet_contents.get((col_num, row), " ")


# does cellLabel refer to the cell at col, row?
# @param col (integer)
# @param row (integer)
# @param cellLabel a string, such as "B5"
# @return True if it does refer to that cell, False if not #
def is_current(col, row, cellLabel):
    col_letter = chr(col + 64)
    target_label = col_letter + str(row)
    return target_label == cellLabel


# Print out the column headers for the spreadsheet, from 'A' through the last column specified in the user's range input.
# The column headers must be printed as specified in the sample output shown earlier, including a first empty cell for row labels.
# This should also print the row separator line preceding the column headers, but NOT the row separator line following the headers.
# @param lastColumn - the last column to print (first column is 'A', not 1 or 0)
# @param lastRow - the last data row to print (first data row is 1, not 0)
# @return None
#
def print_column_headers(last_column, last_row):
    print(format_row_separator(last_column))
    print("|   |", end="")
    for col in range(ord('A'), ord(last_column)+1):
        print("    " + chr(col) + "    |", end="")
    print()


# format a row separator line of + and – characters as in the sample output.
# You should use string replication (string * int) to generate this for the appropriate number of columns, not a loop.
# This includes separators for the initial column of row labels.
# @param lastColumn - the last column to print (first column is 'A', not 1 or 0)
# @return the properly formatted separator line as a string
#
def format_row_separator(last_column):
    number_of_columns = ord(last_column) - 64
    return_string = "+---+"
    return_string += "---------+" * number_of_columns
    return return_string


# Format the string for the contents of a data cell.
# The contents must be exactly 9 characters, justified properly for the type of data.
# There should be a vertical bar # column separator (|) preceding the contents.
# If this is the last column, there should also be a vertical bar column separator following the contents.
# Thus, the total length of the string will be either 10 or 11 characters.
# @param column - the column of this cell (first column is 'A', not 1 or 0)
# @param row - the row for this cell (first row is 1, not 0)
# @return the formatted data cell string.
#
def format_data_cell(column, row):
    cell_data = get_cell_value(column, row)
    return_string = "|"
    if isinstance(cell_data, str):
        return_string += cell_data
        return_string += " " * (10 - len(return_string))
    elif isinstance(cell_data, int):
        return_string += " " * (9 - len(str(cell_data)))
        return_string += str(cell_data)

    if ord(column)-64 == num_columns:
        return_string += "|"

    return return_string


# Print a complete data row preceded by a row separator line.
# If this is the last row of the spreadsheet, then also print a trailing row separator line
# @param row - the row to be printed (first row is 1, not 0)
# @return None
#
def print_data_row(row):
    print(format_row_separator(chr(num_columns + 64)))

    print("| " + str(row) + " ", end="")
    for col in range(1, num_columns+1):
        print(format_data_cell(chr(col + 64), row), end="")
    print()

    if row == num_rows:
        print(format_row_separator(chr(num_columns + 64)))


# Print a complete spreadsheet based on the data in the 3 global variables.
# You may assume at this point that all data in those variables has been validated.
# @return None
#
def print_sheet():
    print_column_headers(chr(num_columns + 64), num_rows)
    for r in range(1, num_rows+1):
        print_data_row(r)



# Check all cell labels to make sure they are an upper-case character followed by
# an integer, and that both the column and row are within the specified range.
# @param input a list of strings consisting of sys.argv without
# sys.argv[0] and sys.argv[1]
# @param lastCol last valid column (first column is 'A', not 1 or 0)
# @param lastRow last valid row number
# @return the first invalid cell label if there is one, otherwise None
def validate_all_cell_labels(input, lastCol, lastRow):
    for i in range(0, len(input), 2):
        if ord(input[i][0]) < 65 or ord(input[i][0]) > 90:
            return input[i]
        elif input[i][0] > lastCol:
            return input[i]
        elif not is_valid_integer(input[i][1:]):
            return input[i]
        elif int(input[i][1:]) > lastRow:
            return input[i]


# MAJOR CHANGE SINCE LAST SEMESTER
# Checks all cell values to make sure they are printable in 9 characters or less
# @param input a list of strings consisting of sys.argv without sys.argv[0] and sys.argv[1]
# @return the first invalid value if there is one, otherwise None
def validate_all_cell_values(input):
    for i in range(1, len(input), 2):
        if len(str(get_integer(input[i]))) > 9:
            return str(input[i])


# Checks if strings input[0] and input[1] represent integers between 1 and 9
# @param input a list of strings that defaults to sys.argv
# @return True if range is OK, False otherwise
#
def validateRange(input=sys.argv):
    try:
        int(input[2])
    except:
        return False
    if ord(input[1]) - 64 <= 9 and ord(input[1]) - 64 >= 1 and int(input[2]) <= 9 and int(input[2]) >= 1:
        return True
    else:
        return False

# Returns the integer form of a String or the String unchanged if not an integer
# @param arg the String which may or may not represent an integer
# @return the integer value if arg was a string representation of an integer,
# otherwise the arg unchanged
#
def get_integer(arg):
    i = arg
    try:
        i = int(arg)
    except:
        return arg
    finally:
        return i


# checks if the string arg represents an integer
# @param arg
# @return True if it's a valid double, False if not
#
def is_valid_integer(arg):
    i = arg
    try:
        i = int(arg)
        return True
    except:
        return False


# Initializes the spreadsheet and performs all required validation. 
# If the validation passes, prints the spreadsheet.
# @param args defaults to sys.argv
# @return True if all validation passes, False if not
#
def main(args=sys.argv):
    done = init_spreadsheet()
    if not done:
        return False
    else:
        print_sheet()
        return True


if __name__ == "__main__":
    main()