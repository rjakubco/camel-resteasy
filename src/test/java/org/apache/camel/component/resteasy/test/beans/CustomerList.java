package org.apache.camel.component.resteasy.test.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Roman Jakubco (rjakubco@redhat.com) on 13/03/15.
 */
public class CustomerList {
    static Set<Customer> customerList = new HashSet<>();



    public void addCustomer(Customer customer){
        customerList.add(customer);
    }

    public Customer getCustomer(int id){
        for(Customer c : customerList){
            if(c.getId() == id){
                return c;
            }
        }

        return null;
    }

    public Customer deleteCustomer(int id){
        Customer delete = getCustomer(id);
        Customer customer = new Customer(delete.getName(), delete.getSurname(), delete.getId());
        customerList.remove(getCustomer(id));
        return customer;
    }

    public void add(){
        this.customerList.add(new Customer("Roman", "Jakubco", 1));
        this.customerList.add(new Customer("Camel", "Rider", 2));
    }

    public Set<Customer> getCustomerList() {
        return customerList;
    }
}
