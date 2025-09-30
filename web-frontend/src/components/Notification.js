// Notification.js
import React from 'react';
import { Alert, Snackbar, Box, Typography, Chip, IconButton } from '@mui/material';
import { CheckCircle, Info, Warning, Error, Close, LocalShipping, Restaurant, Payment } from '@mui/icons-material';

const Notification = ({ 
  open, 
  onClose, 
  type = 'info', 
  title, 
  message, 
  orderInfo = null,
  autoHideDuration = 6000 
}) => {
  // Icon mapping for different notification types
  const iconMap = {
    success: <CheckCircle />,
    info: <Info />,
    warning: <Warning />,
    error: <Error />,
    order_placed: <Restaurant />,
    order_confirmed: <CheckCircle />,
    order_preparing: <Restaurant />,
    order_ready: <LocalShipping />,
    order_served: <CheckCircle />,
    payment_received: <Payment />
  };

  // Color mapping for different order statuses
  const getStatusColor = (status) => {
    switch (status?.toLowerCase()) {
      case 'pending': return '#ff9800';
      case 'confirmed': return '#2196f3';
      case 'preparing': return '#ff5722';
      case 'ready': return '#4caf50';
      case 'served': return '#8bc34a';
      case 'cancelled': return '#f44336';
      default: return '#2196f3';
    }
  };

  const getAlertSeverity = (type) => {
    if (type.startsWith('order_')) return 'info';
    return type;
  };

  return (
    <Snackbar
      open={open}
      autoHideDuration={autoHideDuration}
      onClose={onClose}
      anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
      sx={{ mt: 2 }}
    >
      <Alert
        severity={getAlertSeverity(type)}
        icon={iconMap[type] || iconMap[type.split('_')[0]] || iconMap.info}
        action={
          <IconButton
            size="small"
            aria-label="close"
            color="inherit"
            onClick={onClose}
          >
            <Close fontSize="small" />
          </IconButton>
        }
        sx={{
          width: '400px',
          maxWidth: '90vw',
          '& .MuiAlert-message': {
            width: '100%'
          }
        }}
      >
        <Box>
          {/* Title */}
          <Typography variant="h6" component="div" sx={{ fontWeight: 600, mb: 1 }}>
            {title}
          </Typography>
          
          {/* Message */}
          <Typography variant="body2" sx={{ mb: orderInfo ? 1.5 : 0 }}>
            {message}
          </Typography>
          
          {/* Order Information */}
          {orderInfo && (
            <Box sx={{ 
              mt: 1, 
              p: 1.5, 
              backgroundColor: 'rgba(255,255,255,0.1)', 
              borderRadius: 1,
              border: '1px solid rgba(255,255,255,0.2)'
            }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1 }}>
                Order Details:
              </Typography>
              
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 1 }}>
                {orderInfo.orderId && (
                  <Chip 
                    label={`Order #${orderInfo.orderId}`} 
                    size="small" 
                    color="primary" 
                    variant="outlined"
                  />
                )}
                {orderInfo.tableNumber && (
                  <Chip 
                    label={orderInfo.tableNumber} 
                    size="small" 
                    color="secondary" 
                    variant="outlined"
                  />
                )}
                {orderInfo.status && (
                  <Chip 
                    label={orderInfo.status} 
                    size="small" 
                    sx={{ 
                      backgroundColor: getStatusColor(orderInfo.status),
                      color: 'white',
                      fontWeight: 600
                    }}
                  />
                )}
              </Box>
              
              {orderInfo.customerName && (
                <Typography variant="body2" sx={{ fontSize: '0.85rem', opacity: 0.9 }}>
                  <strong>Customer:</strong> {orderInfo.customerName}
                </Typography>
              )}
              
              {orderInfo.totalAmount && (
                <Typography variant="body2" sx={{ fontSize: '0.85rem', opacity: 0.9 }}>
                  <strong>Total:</strong> {orderInfo.totalAmount.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'})}
                </Typography>
              )}
              
              {orderInfo.estimatedTime && (
                <Typography variant="body2" sx={{ fontSize: '0.85rem', opacity: 0.9 }}>
                  <strong>Estimated Time:</strong> {orderInfo.estimatedTime}
                </Typography>
              )}
              
              {orderInfo.items && orderInfo.items.length > 0 && (
                <Box sx={{ mt: 1 }}>
                  <Typography variant="body2" sx={{ fontSize: '0.85rem', fontWeight: 600 }}>
                    Items ({orderInfo.items.length}):
                  </Typography>
                  {orderInfo.items.slice(0, 3).map((item, index) => (
                    <Typography key={index} variant="body2" sx={{ fontSize: '0.8rem', opacity: 0.8, pl: 1 }}>
                      • {item.name} x{item.quantity}
                    </Typography>
                  ))}
                  {orderInfo.items.length > 3 && (
                    <Typography variant="body2" sx={{ fontSize: '0.8rem', opacity: 0.7, pl: 1, fontStyle: 'italic' }}>
                      ... and {orderInfo.items.length - 3} more items
                    </Typography>
                  )}
                </Box>
              )}
            </Box>
          )}
        </Box>
      </Alert>
    </Snackbar>
  );
};

export default Notification;