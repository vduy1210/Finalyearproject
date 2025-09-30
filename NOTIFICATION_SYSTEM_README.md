# 🔔 Order Notification System

A comprehensive notification system for your restaurant web application that keeps customers informed about their order status in real-time.

## ✨ Features

### 🎯 Order Status Notifications
- **Order Placed**: Confirms order submission with details
- **Order Confirmed**: Restaurant acknowledges the order
- **Order Preparing**: Kitchen starts working on the order
- **Order Ready**: Food is ready for serving
- **Order Served**: Food has been delivered to table
- **Payment Received**: Payment confirmation
- **Order Cancelled**: Cancellation with reason

### 🎨 Notification Types
- **Success** (✅): Positive actions and confirmations
- **Info** (ℹ️): General information and updates
- **Warning** (⚠️): Important notices requiring attention
- **Error** (❌): Problems and failures

### 📱 Responsive Design
- Mobile-friendly notifications
- Desktop and tablet optimized
- Touch-friendly interaction
- Accessibility support

## 🚀 Quick Start

### 1. Setup (Already Done)
The notification system is already integrated into your app through:
- `NotificationProvider` wrapping your app
- `useNotification` hook for easy access
- Pre-styled components with Material-UI

### 2. Basic Usage

```javascript
import { useNotification } from './NotificationProvider';

function MyComponent() {
  const notification = useNotification();

  const handleOrderPlaced = (orderData) => {
    notification.orderPlaced({
      orderId: orderData.id,
      tableNumber: "Table 5",
      customerName: "John Doe",
      totalAmount: 150000,
      items: [
        { name: "Pho Bo", quantity: 2, price: 50000 },
        { name: "Banh Mi", quantity: 1, price: 25000 }
      ]
    });
  };

  return (
    <button onClick={handleOrderPlaced}>
      Place Order
    </button>
  );
}
```

### 3. Test the System
Visit `/notifications` in your app to see a demo of all notification types.

## 📋 API Reference

### Order Notifications

#### `notification.orderPlaced(orderData)`
Shows order confirmation with full details.

```javascript
notification.orderPlaced({
  orderId: 12345,
  tableNumber: "Table 5",
  customerName: "John Doe", 
  totalAmount: 150000,
  items: [
    { name: "Pho Bo", quantity: 2, price: 50000 }
  ]
});
```

#### `notification.orderConfirmed(orderData)`
Confirms restaurant accepted the order.

#### `notification.orderPreparing(orderData)`
Indicates kitchen started cooking.

#### `notification.orderReady(orderData)`
Food is ready for serving (10-second display).

#### `notification.orderServed(orderData)`
Order delivered to customer.

#### `notification.paymentReceived(orderData)`
Payment processing confirmation.

#### `notification.orderCancelled(orderData, reason)`
Order cancellation with optional reason.

### Status Updates

#### `notification.statusUpdate(orderData, oldStatus, newStatus)`
Generic status change notification.

```javascript
notification.statusUpdate(orderData, "pending", "confirmed");
```

### General Notifications

#### `notification.success(title, message, orderInfo?)`
```javascript
notification.success("Success!", "Operation completed successfully");
```

#### `notification.info(title, message, orderInfo?)`
#### `notification.warning(title, message, orderInfo?)`
#### `notification.error(title, message, orderInfo?)`

### Custom Notifications

#### `notification.custom(notificationObject)`
```javascript
notification.custom({
  type: 'info',
  title: 'Custom Title',
  message: 'Custom message',
  orderInfo: { orderId: 123 },
  autoHideDuration: 5000
});
```

## 🎨 Customization

### Order Data Structure
```javascript
const orderData = {
  orderId: number,           // Required: Order ID
  tableNumber: string,       // Required: Table identifier
  customerName: string,      // Optional: Customer name
  totalAmount: number,       // Optional: Total in VND
  status: string,           // Optional: Current status
  estimatedTime: string,    // Optional: "5-10 minutes"
  items: [                  // Optional: Order items
    {
      name: string,         // Item name
      quantity: number,     // Quantity ordered
      price: number         // Item price
    }
  ]
}
```

### Auto-Hide Durations
- Order Ready: 10 seconds (needs attention)
- Order Placed/Cancelled: 8 seconds
- General notifications: 5-6 seconds

### Styling
Notifications use Material-UI themes and custom CSS in `styles/notification.css`.

To customize colors, edit the CSS variables:
```css
.notification-order-ready {
  border-left: 4px solid #8bc34a; /* Change this color */
}
```

## 🔧 Integration Examples

### In Cart Component (Already Integrated)
```javascript
// When order is successfully placed
notification.orderPlaced({
  orderId: response.orderId,
  tableNumber: tableNumber,
  customerName: name,
  totalAmount: total,
  items: cart.map(item => ({
    name: item.name,
    quantity: item.quantity,
    price: item.price
  }))
});
```

### In Order Management
```javascript
// When staff updates order status
const handleStatusChange = (order, newStatus) => {
  notification.statusUpdate(order, order.status, newStatus);
  
  // Or use specific methods
  if (newStatus === 'ready') {
    notification.orderReady(order);
  }
};
```

### Error Handling
```javascript
try {
  await placeOrder(orderData);
} catch (error) {
  notification.error(
    "Order Failed",
    error.message,
    { tableNumber: "Table 5" }
  );
}
```

## 🎵 Future Enhancements

### Planned Features
1. **Sound Notifications**: Audio alerts for important updates
2. **Push Notifications**: Browser push notifications when tab is inactive
3. **SMS Integration**: Text message notifications for order updates
4. **Real-time Updates**: WebSocket integration for live status updates
5. **Notification History**: View past notifications
6. **Custom Sounds**: Different sounds for different notification types

### Backend Integration
To enable real-time updates, consider:
1. WebSocket connection for live order status
2. Server-sent events for updates
3. Database triggers for status changes
4. Staff dashboard for order management

## 🐛 Troubleshooting

### Common Issues

**Notifications not showing**
- Check if `NotificationProvider` wraps your app
- Verify `useNotification` is called inside the provider
- Check console for JavaScript errors

**Styling issues**
- Ensure `notification.css` is imported
- Check Material-UI theme conflicts
- Verify CSS specificity

**Mobile display problems**
- Test on actual mobile devices
- Check viewport meta tag
- Verify touch interactions

### Debug Mode
Enable console logging by adding to your notification calls:
```javascript
notification.orderPlaced(orderData);
console.log("Notification sent:", orderData);
```

## 📞 Support

For help with implementation:
1. Check the demo at `/notifications`
2. Review the component source code
3. Test with different order scenarios
4. Monitor browser console for errors

## 🚀 Getting Started Checklist

- [x] ✅ Notification system integrated
- [x] ✅ Order placement notifications working
- [x] ✅ Demo page available at `/notifications`
- [x] ✅ Mobile responsive design
- [x] ✅ Material-UI styling applied
- [ ] 🔄 Test with real orders
- [ ] 🔄 Customize colors/styles to match brand
- [ ] 🔄 Add backend integration for real-time updates
- [ ] 🔄 Implement sound notifications
- [ ] 🔄 Add push notification support

---

Your notification system is ready to use! Visit `/notifications` to test all features and see how it works.