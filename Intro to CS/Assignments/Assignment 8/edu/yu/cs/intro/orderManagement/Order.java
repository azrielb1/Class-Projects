package edu.yu.cs.intro.orderManagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Order {
    
    private boolean completed = false;
    private HashMap<Item, Integer> stuffInOrder = new HashMap<>(); //maps to quantity

    public Order() {
        
    }

    public Item[] getItems(){
        Item[] a = new Item[0];
        return (stuffInOrder.keySet()).toArray(a);
    }

    public int getQuantity(Item b) {
        if (stuffInOrder.get(b) != null) {
            return stuffInOrder.get(b);
        } else {
            return 0;
        }
    }

    protected Set<Service> getServices() {
        Set<Service> toReturn = new HashSet<>();
        for (Item i : stuffInOrder.keySet()) {
            if (i instanceof Service) {
                toReturn.add((Service) i);
            }
        }
        return toReturn;
    }

    protected Set<Product> getProducts() {
        Set<Product> toReturn = new HashSet<>();
        for (Item i : stuffInOrder.keySet()) {
            if (i instanceof Product) {
                toReturn.add((Product) i);
            }
        }
        return toReturn;
    }

    public void addToOrder(Item item, int quantity) {
        if (stuffInOrder.get(item) != null) {
            stuffInOrder.put(item, stuffInOrder.get(item) + quantity);
        } else {
            stuffInOrder.put(item, quantity);
        }
    }

    public double getProductsTotalPrice() {
        double total = 0;
        for (Item i : stuffInOrder.keySet()) {
            if (i instanceof Product) {
                total += (stuffInOrder.get(i)) * i.getPrice();
            }
        }
        return total;
    }

    public double getServicesTotalPrice() {
        double total = 0;
        for (Item i : stuffInOrder.keySet()) {
            if (i instanceof Service) {
                total += (stuffInOrder.get(i)) * i.getPrice();
            }
        }
        return total;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}