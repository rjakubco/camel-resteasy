package org.apache.camel.component.resteasy.test.beans;

/**
 * Created by roman on 31/10/14.
 */
public class Product {
    String name;
    int qty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
