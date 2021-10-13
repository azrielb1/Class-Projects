//Azriel Bachrach
//November 25 2020
//Intro to CS

public class DoubleCell implements Cell {

	private double cellValue;

	public DoubleCell(double d) {
		this.cellValue = d;
	}

	public double getNumericValue() {
		return cellValue;
	}

	public String getStringValue() {
		return Double.toString(cellValue);
	}
}