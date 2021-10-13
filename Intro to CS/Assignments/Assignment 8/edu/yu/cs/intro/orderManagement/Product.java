package edu.yu.cs.intro.orderManagement;

public class Product implements Item {

    private String description;
    private double itemPrice;
    private int itemNumber;

    public Product(String name, double price, int productID) {
        this.description = name;
        this.itemPrice = price;
        this.itemNumber = productID;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return itemPrice;
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
        Product otherItem = (Product) o;
        return this.itemNumber == otherItem.itemNumber;
    }

    public int hashCode() {
        return itemNumber;
    }
}