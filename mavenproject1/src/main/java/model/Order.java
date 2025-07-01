package model;

import java.time.LocalDateTime;

public class Order {
    private int orderId;
    private int customerId;
    private int staffId;
    private LocalDateTime orderDate;
    private double totalAmount;
    private double tax;
    private double discount;

    public Order() {}

    public Order(int orderId, int customerId, int staffId, LocalDateTime orderDate,
                 double totalAmount, double tax, double discount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.staffId = staffId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.tax = tax;
        this.discount = discount;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", staffId=" + staffId +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", tax=" + tax +
                ", discount=" + discount +
                '}';
    }
}
