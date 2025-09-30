// notificationService.js
class NotificationService {
  constructor() {
    this.listeners = [];
  }

  // Add a listener for notifications
  addListener(callback) {
    this.listeners.push(callback);
    // Return unsubscribe function
    return () => {
      this.listeners = this.listeners.filter(listener => listener !== callback);
    };
  }

  // Emit notification to all listeners
  emit(notification) {
    this.listeners.forEach(listener => listener(notification));
  }

  // Order placed notification
  orderPlaced(orderData) {
    this.emit({
      type: 'order_placed',
      title: '🎉 Order Placed Successfully!',
      message: `Your order has been placed and is being processed.`,
      orderInfo: {
        orderId: orderData.orderId,
        tableNumber: orderData.tableNumber,
        customerName: orderData.customerName,
        totalAmount: orderData.totalAmount,
        status: 'Pending',
        estimatedTime: '15-20 minutes',
        items: orderData.items
      },
      autoHideDuration: 8000
    });
  }

  // Order confirmed notification
  orderConfirmed(orderData) {
    this.emit({
      type: 'order_confirmed',
      title: '✅ Order Confirmed!',
      message: `Your order #${orderData.orderId} has been confirmed by the restaurant.`,
      orderInfo: {
        orderId: orderData.orderId,
        tableNumber: orderData.tableNumber,
        customerName: orderData.customerName,
        status: 'Confirmed',
        estimatedTime: '10-15 minutes'
      },
      autoHideDuration: 6000
    });
  }

  // Order preparing notification
  orderPreparing(orderData) {
    this.emit({
      type: 'order_preparing',
      title: '👨‍🍳 Order in Kitchen!',
      message: `Your order #${orderData.orderId} is now being prepared.`,
      orderInfo: {
        orderId: orderData.orderId,
        tableNumber: orderData.tableNumber,
        status: 'Preparing',
        estimatedTime: '5-10 minutes'
      },
      autoHideDuration: 5000
    });
  }

  // Order ready notification
  orderReady(orderData) {
    this.emit({
      type: 'order_ready',
      title: '🍽️ Order Ready!',
      message: `Your order #${orderData.orderId} is ready to be served!`,
      orderInfo: {
        orderId: orderData.orderId,
        tableNumber: orderData.tableNumber,
        status: 'Ready',
        estimatedTime: 'Now'
      },
      autoHideDuration: 10000
    });
  }

  // Order served notification
  orderServed(orderData) {
    this.emit({
      type: 'order_served',
      title: '🎉 Enjoy Your Meal!',
      message: `Your order #${orderData.orderId} has been served. Enjoy!`,
      orderInfo: {
        orderId: orderData.orderId,
        tableNumber: orderData.tableNumber,
        status: 'Served'
      },
      autoHideDuration: 5000
    });
  }

  // Payment received notification
  paymentReceived(orderData) {
    this.emit({
      type: 'payment_received',
      title: '💳 Payment Received!',
      message: `Payment for order #${orderData.orderId} has been processed successfully.`,
      orderInfo: {
        orderId: orderData.orderId,
        totalAmount: orderData.totalAmount,
        status: 'Paid'
      },
      autoHideDuration: 6000
    });
  }

  // Order cancelled notification
  orderCancelled(orderData, reason = '') {
    this.emit({
      type: 'error',
      title: '❌ Order Cancelled',
      message: `Order #${orderData.orderId} has been cancelled. ${reason}`,
      orderInfo: {
        orderId: orderData.orderId,
        tableNumber: orderData.tableNumber,
        status: 'Cancelled'
      },
      autoHideDuration: 8000
    });
  }

  // Generic success notification
  success(title, message, orderInfo = null) {
    this.emit({
      type: 'success',
      title,
      message,
      orderInfo,
      autoHideDuration: 5000
    });
  }

  // Generic info notification
  info(title, message, orderInfo = null) {
    this.emit({
      type: 'info',
      title,
      message,
      orderInfo,
      autoHideDuration: 5000
    });
  }

  // Generic warning notification
  warning(title, message, orderInfo = null) {
    this.emit({
      type: 'warning',
      title,
      message,
      orderInfo,
      autoHideDuration: 6000
    });
  }

  // Generic error notification
  error(title, message, orderInfo = null) {
    this.emit({
      type: 'error',
      title,
      message,
      orderInfo,
      autoHideDuration: 8000
    });
  }

  // Custom notification
  custom(notification) {
    this.emit({
      autoHideDuration: 5000,
      ...notification
    });
  }

  // Status update notification - handles any status change
  statusUpdate(orderData, oldStatus, newStatus) {
    const statusMessages = {
      'pending': { 
        title: '⏳ Order Received', 
        message: 'Your order is being reviewed...',
        estimatedTime: '15-20 minutes'
      },
      'confirmed': { 
        title: '✅ Order Confirmed', 
        message: 'Your order has been confirmed!',
        estimatedTime: '10-15 minutes'
      },
      'preparing': { 
        title: '👨‍🍳 Kitchen Alert', 
        message: 'Your order is being prepared...',
        estimatedTime: '5-10 minutes'
      },
      'ready': { 
        title: '🍽️ Ready to Serve', 
        message: 'Your order is ready!',
        estimatedTime: 'Now'
      },
      'served': { 
        title: '🎉 Bon Appétit!', 
        message: 'Your order has been served. Enjoy your meal!',
        estimatedTime: null
      },
      'cancelled': { 
        title: '❌ Order Cancelled', 
        message: 'Unfortunately, your order has been cancelled.',
        estimatedTime: null
      }
    };

    const statusInfo = statusMessages[newStatus.toLowerCase()] || {
      title: '📋 Status Update',
      message: `Order status changed to: ${newStatus}`,
      estimatedTime: null
    };

    this.emit({
      type: newStatus.toLowerCase() === 'cancelled' ? 'error' : 'info',
      title: statusInfo.title,
      message: statusInfo.message,
      orderInfo: {
        orderId: orderData.orderId,
        tableNumber: orderData.tableNumber,
        customerName: orderData.customerName,
        status: newStatus,
        estimatedTime: statusInfo.estimatedTime,
        totalAmount: orderData.totalAmount
      },
      autoHideDuration: newStatus.toLowerCase() === 'ready' ? 10000 : 6000
    });
  }
}

// Create and export a singleton instance
const notificationService = new NotificationService();
export default notificationService;