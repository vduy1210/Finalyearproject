package model;

public class OrderDetails extends Order {
    private int productId;
    private int quantity;
    private double unitPrice;

    public OrderDetails() {
        super();
    }

    public OrderDetails(int orderId, int customerId, java.time.LocalDateTime orderDate,
                        double totalAmount, double tax, double discount,
                        int productId, int quantity, double unitPrice) {
        super(orderId, customerId, orderDate, totalAmount, tax, discount);
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters and Setters

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return super.toString() + " | OrderDetails{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
