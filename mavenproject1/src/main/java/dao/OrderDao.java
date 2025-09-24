package dao;

import model.Order;
import model.OrderDetails;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDao {
    
    private WebOrderDao webOrderDao;
    
    public OrderDao() {
        this.webOrderDao = new WebOrderDao();
    }

    public boolean createOrder(Order order, List<OrderDetails> details) {
        // Delegate to WebOrderDao for web orders
        return webOrderDao.createWebOrder(order, details);
    }

    // ===== Reporting utilities =====
    public double getTotalRevenue(LocalDateTime from, LocalDateTime to) {
        // Delegate to WebOrderDao for web order revenue
        return webOrderDao.getTotalRevenue(from, to);
    }

    public List<Order> listOrders(LocalDateTime from, LocalDateTime to) {
        // Delegate to WebOrderDao for web orders
        return webOrderDao.listWebOrders(from, to);
    }
}