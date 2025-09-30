// NotificationDemo.js
import React from 'react';
import { Box, Button, Typography, Paper, Grid } from '@mui/material';
import { useNotification } from './NotificationProvider';

const NotificationDemo = () => {
  const notification = useNotification();

  // Sample order data for demos
  const sampleOrderData = {
    orderId: 12345,
    tableNumber: "Table 5",
    customerName: "John Doe",
    totalAmount: 150000,
    items: [
      { name: "Pho Bo", quantity: 2, price: 50000 },
      { name: "Banh Mi", quantity: 1, price: 25000 },
      { name: "Iced Coffee", quantity: 2, price: 25000 }
    ]
  };

  const demoButtons = [
    {
      title: "Order Placed",
      color: "primary",
      action: () => notification.orderPlaced(sampleOrderData)
    },
    {
      title: "Order Confirmed",
      color: "primary", 
      action: () => notification.orderConfirmed(sampleOrderData)
    },
    {
      title: "Order Preparing",
      color: "warning",
      action: () => notification.orderPreparing(sampleOrderData)
    },
    {
      title: "Order Ready",
      color: "success",
      action: () => notification.orderReady(sampleOrderData)
    },
    {
      title: "Order Served",
      color: "success",
      action: () => notification.orderServed(sampleOrderData)
    },
    {
      title: "Payment Received",
      color: "info",
      action: () => notification.paymentReceived(sampleOrderData)
    },
    {
      title: "Order Cancelled",
      color: "error",
      action: () => notification.orderCancelled(sampleOrderData, "Out of ingredients")
    },
    {
      title: "Success Message",
      color: "success",
      action: () => notification.success("Success!", "This is a general success message")
    },
    {
      title: "Info Message",
      color: "info",
      action: () => notification.info("Information", "This is an informational message")
    },
    {
      title: "Warning Message",
      color: "warning",
      action: () => notification.warning("Warning!", "This is a warning message")
    },
    {
      title: "Error Message",
      color: "error",
      action: () => notification.error("Error!", "This is an error message")
    },
    {
      title: "Status Update",
      color: "secondary",
      action: () => notification.statusUpdate(sampleOrderData, "pending", "confirmed")
    }
  ];

  return (
    <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh" p={3}>
      <Paper elevation={3} sx={{ p: 4, maxWidth: 800, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom textAlign="center" color="primary">
          🔔 Notification System Demo
        </Typography>
        
        <Typography variant="body1" sx={{ mb: 3, textAlign: 'center', color: 'text.secondary' }}>
          Test different types of order notifications to see how they appear to customers.
        </Typography>

        <Grid container spacing={2}>
          {demoButtons.map((button, index) => (
            <Grid item xs={12} sm={6} md={4} key={index}>
              <Button
                fullWidth
                variant="contained"
                color={button.color}
                onClick={button.action}
                sx={{ 
                  py: 1.5,
                  fontSize: '0.9rem',
                  fontWeight: 600
                }}
              >
                {button.title}
              </Button>
            </Grid>
          ))}
        </Grid>

        <Box sx={{ mt: 4, p: 2, backgroundColor: '#f5f5f5', borderRadius: 1 }}>
          <Typography variant="h6" gutterBottom>
            📋 Usage Examples:
          </Typography>
          <Typography variant="body2" component="div" sx={{ mb: 1 }}>
            <strong>In Cart component:</strong> <code>notification.orderPlaced(orderData)</code>
          </Typography>
          <Typography variant="body2" component="div" sx={{ mb: 1 }}>
            <strong>Status updates:</strong> <code>notification.statusUpdate(orderData, oldStatus, newStatus)</code>
          </Typography>
          <Typography variant="body2" component="div" sx={{ mb: 1 }}>
            <strong>General messages:</strong> <code>notification.success("Title", "Message")</code>
          </Typography>
          <Typography variant="body2" component="div">
            <strong>Custom notifications:</strong> <code>notification.custom({"{type, title, message, orderInfo}"})</code>
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
};

export default NotificationDemo;