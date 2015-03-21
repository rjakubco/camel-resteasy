package org.apache.camel.component.resteasy.test.beans;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com).
 */
public class CustomerList {
    static Set<Customer> customerList = new HashSet<>();



    public void addCustomer(Customer customer){
        customerList.add(customer);
    }

    public Customer getCustomer(Integer id){
        for(Customer c : customerList){
            if(Objects.equals(c.getId(), id)){
                return c;
            }
        }

        return null;
    }

    public Customer deleteCustomer(Integer id){
        Customer delete = getCustomer(id);
        Customer customer = new Customer(delete.getName(), delete.getSurname(), delete.getId());
        customerList.remove(getCustomer(id));
        return customer;
    }

    public void add(){
        customerList.add(new Customer("Roman", "Jakubco", 1));
        customerList.add(new Customer("Camel", "Rider", 2));
    }

    public Set<Customer> getCustomerList() {
        return customerList;
    }
}
