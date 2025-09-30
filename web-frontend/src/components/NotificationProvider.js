// NotificationProvider.js
import React, { createContext, useContext, useState, useEffect } from 'react';
import Notification from '../components/Notification';
import notificationService from '../services/notificationService';

const NotificationContext = createContext();

export const useNotification = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within a NotificationProvider');
  }
  return context;
};

export const NotificationProvider = ({ children }) => {
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    // Subscribe to notification service
    const unsubscribe = notificationService.addListener((notification) => {
      const id = Date.now() + Math.random();
      setNotifications(prev => [...prev, { ...notification, id }]);
    });

    return unsubscribe;
  }, []);

  const removeNotification = (id) => {
    setNotifications(prev => prev.filter(notification => notification.id !== id));
  };

  const contextValue = {
    // Direct access to notification service methods
    orderPlaced: notificationService.orderPlaced.bind(notificationService),
    orderConfirmed: notificationService.orderConfirmed.bind(notificationService),
    orderPreparing: notificationService.orderPreparing.bind(notificationService),
    orderReady: notificationService.orderReady.bind(notificationService),
    orderServed: notificationService.orderServed.bind(notificationService),
    paymentReceived: notificationService.paymentReceived.bind(notificationService),
    orderCancelled: notificationService.orderCancelled.bind(notificationService),
    statusUpdate: notificationService.statusUpdate.bind(notificationService),
    success: notificationService.success.bind(notificationService),
    info: notificationService.info.bind(notificationService),
    warning: notificationService.warning.bind(notificationService),
    error: notificationService.error.bind(notificationService),
    custom: notificationService.custom.bind(notificationService),
  };

  return (
    <NotificationContext.Provider value={contextValue}>
      {children}
      {/* Render all notifications */}
      {notifications.map((notification) => (
        <Notification
          key={notification.id}
          open={true}
          onClose={() => removeNotification(notification.id)}
          type={notification.type}
          title={notification.title}
          message={notification.message}
          orderInfo={notification.orderInfo}
          autoHideDuration={notification.autoHideDuration}
        />
      ))}
    </NotificationContext.Provider>
  );
};