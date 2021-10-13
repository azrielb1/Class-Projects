package edu.yu.cs.intro.orderManagement;

public class Service implements Item {

    private double pricePerHour;
    private int numberOfHours;
    private int itemNumber;
    private String description;

    public Service(double pricePerHour, int numberOfHours, int serviceID, String description) {
        if (numberOfHours < 0) {
            throw new IllegalArgumentException();
        }
        this.pricePerHour = pricePerHour;
        this.numberOfHours = numberOfHours;
        this.itemNumber = serviceID;
        this.description = description;
    }

    public int getNumberOfHours() {
        return numberOfHours;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return pricePerHour * numberOfHours;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        Service other = (Service) o;
        return this.itemNumber == other.itemNumber;
    }

    public int hashCode() {
        return itemNumber;
    }
}
