package edu.yu.cs.intro.orderManagement;

import java.util.*;

public class ServiceProvider {
    
    private String name;
    private int id;
    private HashSet<Service> services = new HashSet<>();
    private boolean busy;

    public ServiceProvider(String name, int id, Set<Service> services) {
        this.name = name;
        this.id = id;
        this.services = new HashSet<Service>(services);
        this.busy = false;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return this.id;
    }

    protected void assignToCustomer() {
        if (busy) {
            throw new IllegalStateException("provider is currently assigned to a job");
        }
        this.busy = true;
    }

    protected void endCustomerEngagement() {
        if (!busy) {
            throw new IllegalStateException();
        }
        this.busy = false;
    }

    protected boolean addService(Service s) {
        return services.add(s);
    }

    protected boolean removeService(Service s) {
        return services.remove(s);
    }

    public Set<Service> getServices() {
        return new HashSet<Service>(services);
    }

    protected boolean isAssigned() {
        return this.busy;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass()!=o.getClass()) {
            return false;
        }
        ServiceProvider other = (ServiceProvider) o;
        return this.id == other.id;
    }

    public int hashCode() {
        return id;
    }
}
