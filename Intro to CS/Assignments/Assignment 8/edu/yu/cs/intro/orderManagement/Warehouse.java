package edu.yu.cs.intro.orderManagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Warehouse {

    private HashMap<Product, Integer> currentStock = new HashMap<>(); //Every Item in stock mapped to how many are in stock
    private HashMap<Product, Integer> desiredStock = new HashMap<>(); //Every item mapped to the desired stock
    private HashMap<Integer, Product> itemNumber = new HashMap<>(); //Item number maped to the corresponding item
    private Set<Product> nonRestockable = new HashSet<>(); //items that can't be restocked


    protected Warehouse() {

    }

    protected Set<Product> getAllProductsInCatalog() {
        return currentStock.keySet();
    }

    protected void addNewProductToWarehouse(Product product, int desiredStockLevel) {
        if (nonRestockable.contains(product)) {
            throw new IllegalArgumentException("Non restockable product");
        }
        if (currentStock.containsKey(product)) {
            throw new IllegalArgumentException("Already in warehouse");
        }
        currentStock.put(product, desiredStockLevel);
        desiredStock.put(product, desiredStockLevel);
        itemNumber.put(product.getItemNumber(), product);
    }

    protected void restock(int productNumber, int minimum) {
        Product p = itemNumber.get(productNumber);
        if (nonRestockable.contains(p)) {
            throw new IllegalArgumentException("Non restockable product");
        }
        if (!(currentStock.containsKey(p))) {
            throw new IllegalArgumentException("Product not in catalog");
        }
        if (currentStock.get(p) < minimum) {
            currentStock.put(p, minimum);
        }
        if (currentStock.get(p) < desiredStock.get(p)) {
            currentStock.put(p, desiredStock.get(p));
        }
    }

    protected int setDefaultStockLevel(int productNumber, int quantity) {
        Product p = itemNumber.get(productNumber);
        if (nonRestockable.contains(p)) {
            throw new IllegalArgumentException("Non restockable product");
        }
        if (!(currentStock.containsKey(p))) {
            throw new IllegalArgumentException("Product not in catalog");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException();
        }
        return desiredStock.put(p, quantity);
    }
    
    protected int getStockLevel(int productNumber) {
        Product p = itemNumber.get(productNumber);
        return currentStock.getOrDefault(p, 0);
    }

    protected boolean isInCatalog(int itemNumber) {
        return this.itemNumber.containsKey(itemNumber);
    }

    protected boolean isRestockable(int itemNumber) {
        Product p = this.itemNumber.get(itemNumber);
        if (nonRestockable.contains(p)) {
            return false;
        }
        if (!(currentStock.containsKey(p))) {
            return false;
        }
        return true;
    }

    protected int doNotRestock(int productNumber) {
        Product p = this.itemNumber.get(productNumber);
        nonRestockable.add(p);
        return currentStock.get(p);
    }

    protected boolean canFulfill(int productNumber, int quantity) {
        Product p = this.itemNumber.get(productNumber);
        if (p == null) {
            return false;
        }
        if (currentStock.get(p) < quantity) {
            return false;
        }
        return true;
    }

    protected void fulfill(int productNumber, int quantity) {
        if (!(canFulfill(productNumber, quantity))) {
            throw new IllegalArgumentException();
        }
        Product p = this.itemNumber.get(productNumber);
        currentStock.put(p, currentStock.get(p) - quantity);
    }
}