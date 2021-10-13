package edu.yu.cs.intro.orderManagement;

import java.util.*;

public class OrderManagementSystem {

    private Warehouse warehouse;
    private HashSet<ServiceProvider> allServiceProviders;
    private HashSet<Service> allServices = new HashSet<>();
    private HashSet<Service> discontinuedServices = new HashSet<>();
    private HashMap<Service, Set<ServiceProvider>> providersForService = new HashMap<>(); // Maps service to all its providers
    private int defaultProductStockLevel; // the default stock level declared when constructing
    private int orderNumber = 0; //keeps track of how many orders have been placed
    private HashMap<Integer, Set<ServiceProvider>> whenAssigned = new HashMap<>(); // which order they were assigned

    public OrderManagementSystem(Set<Product> products, int defaultProductStockLevel,
            Set<ServiceProvider> serviceProviders) {
        this(products, defaultProductStockLevel, serviceProviders, new Warehouse());
    }

    public OrderManagementSystem(Set<Product> products, int defaultProductStockLevel,
            Set<ServiceProvider> serviceProviders, Warehouse warehouse) {
        this.warehouse = warehouse;
        this.defaultProductStockLevel = defaultProductStockLevel;
        for (Product p : products) {
            this.warehouse.addNewProductToWarehouse(p, defaultProductStockLevel);
        }
        this.allServiceProviders = new HashSet<>(serviceProviders);
        for (ServiceProvider serviceProvider : serviceProviders) {
            this.allServices.addAll(serviceProvider.getServices());
        }
        for (Service s : allServices) {
            if (s != null) {
                providersForService.put(s, new HashSet<>());
            }
        }
        for (ServiceProvider serviceProvider : serviceProviders) {
            for (Service s : serviceProvider.getServices()) {
                Set<ServiceProvider> provs = providersForService.get(s);
                provs.add(serviceProvider);
            }
        }
    }

    public void placeOrder(Order order) {
        // check services
        if (validateServices(order.getServices(), order) != 0) {
            throw new IllegalStateException();
        }
        // check products
        if (validateProducts(order.getProducts(), order) != 0) {
            for (Product prod : order.getProducts()) {
                boolean canFulfill = warehouse.canFulfill(prod.getItemNumber(), order.getQuantity(prod));
                if (!canFulfill && !warehouse.isRestockable(prod.getItemNumber())) {
                    throw new IllegalArgumentException();
                }
            }
        }

        // handle service order
        HashSet<ServiceProvider> newlyAssigned = new HashSet<>();
        for (Service s : order.getServices()) {
            int quantity = order.getQuantity(s);
            int count = 0;
            for (ServiceProvider serviceProvider : providersForService.get(s)) {
                if (count < quantity) {
                    serviceProvider.assignToCustomer();
                    newlyAssigned.add(serviceProvider);
                    count++;
                }
            }
        }
        whenAssigned.put(orderNumber, newlyAssigned);
        
        // Fullfill products
        for (Product product : order.getProducts()) {
            int quantity = order.getQuantity(product);
            int productNumber = product.getItemNumber();

            if (warehouse.getStockLevel(productNumber) < quantity) {
                warehouse.restock(productNumber, quantity); // restock if not enough
            }

            warehouse.fulfill(productNumber, quantity);
        }

        //free up providers
        if (orderNumber > 2) {
            for (ServiceProvider sP : whenAssigned.get(orderNumber - 3)) {
                sP.endCustomerEngagement();
            }
        }

        orderNumber++;
        order.setCompleted(true); // mark as completed
    }

    protected int validateServices(Collection<Service> services, Order order) {
        HashSet<ServiceProvider> taken = new HashSet<>();

        for (Service service : services) {
            int howManyProviders = 0;

            if (!(allServices.contains(service))) {
                return service.getItemNumber();
            }
            int amountNeeded = order.getQuantity(service);
            for (ServiceProvider provider : providersForService.get(service)) {
                if ((!provider.isAssigned()) && (!taken.contains(provider))) {
                    if (howManyProviders < amountNeeded) {
                        taken.add(provider);
                    }
                    howManyProviders++;
                }
            }
            if (amountNeeded > howManyProviders) {
                return service.getItemNumber();
            }
        }
        return 0;
    }

    protected int validateProducts(Collection<Product> products, Order order) {
        for (Product product : products) {
            int amountNeeded = order.getQuantity(product);
            boolean canFulfill = warehouse.canFulfill(product.getItemNumber(), amountNeeded);
            if (!canFulfill && !(warehouse.isRestockable(product.getItemNumber()))) {
                return product.getItemNumber();
            }
        }
        return 0;
    }

    protected Set<Product> addNewProducts(Collection<Product> products) {
        Set<Product> toReturn = new HashSet<>(); // the set to return
        for (Product product : products) {
            try {
                warehouse.addNewProductToWarehouse(product, defaultProductStockLevel);
                toReturn.add(product);
            } catch (IllegalArgumentException e) {
                System.out.println(e);
            }
        }
        return toReturn;
    }

    protected void addServiceProvider(ServiceProvider provider) {
        allServiceProviders.add(provider);
        Set<Service> hisServices = provider.getServices();
        for (Service service : hisServices) {
            if (!discontinuedServices.contains(service)) {
                allServices.add(service);
            }
        }
        for (Service service : hisServices) {
            if (service != null) {
                providersForService.putIfAbsent(service, new HashSet<>());
                Set<ServiceProvider> l = providersForService.get(service);
                l.add(provider);
            }
        }
    }

    public Set<Product> getProductCatalog() {
        return warehouse.getAllProductsInCatalog();
    }

    public Set<Service> getOfferedServices() {
        return allServices;
    }

    protected void discontinueItem(Item item) {
        if (item instanceof Service) {
            allServices.remove(item);
            discontinuedServices.add((Service) item);
        } else if (item instanceof Product) {
            warehouse.doNotRestock(item.getItemNumber());
        }
    }

    protected void setDefaultProductStockLevel(Product prod, int level) {
        warehouse.setDefaultStockLevel(prod.getItemNumber(), level);
    }

}