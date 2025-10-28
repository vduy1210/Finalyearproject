package dao;

import database.DatabaseConnector;
import model.CustomerTier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerTierDAO {

    // Get all tiers ordered by min_points
    public List<CustomerTier> getAllTiers() throws SQLException {
        List<CustomerTier> tiers = new ArrayList<>();
        String sql = "SELECT * FROM customer_tiers ORDER BY min_points ASC";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                CustomerTier tier = new CustomerTier(
                    rs.getInt("id"),
                    rs.getString("tier_name"),
                    rs.getFloat("min_points"),
                    rs.getFloat("max_points"),
                    rs.getFloat("discount_percent"),
                    rs.getString("description")
                );
                tiers.add(tier);
            }
        }
        return tiers;
    }

    // Get tier by name
    public CustomerTier getTierByName(String tierName) throws SQLException {
        String sql = "SELECT * FROM customer_tiers WHERE tier_name = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tierName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CustomerTier(
                        rs.getInt("id"),
                        rs.getString("tier_name"),
                        rs.getFloat("min_points"),
                        rs.getFloat("max_points"),
                        rs.getFloat("discount_percent"),
                        rs.getString("description")
                    );
                }
            }
        }
        return null;
    }

    // Determine tier based on points
    public CustomerTier getTierByPoints(float points) throws SQLException {
        String sql = "SELECT * FROM customer_tiers WHERE ? >= min_points AND ? <= max_points";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setFloat(1, points);
            ps.setFloat(2, points);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CustomerTier(
                        rs.getInt("id"),
                        rs.getString("tier_name"),
                        rs.getFloat("min_points"),
                        rs.getFloat("max_points"),
                        rs.getFloat("discount_percent"),
                        rs.getString("description")
                    );
                }
            }
        }
        // Default to Bronze if not found
        return getTierByName("Bronze");
    }

    // Update tier configuration
    public void updateTier(CustomerTier tier) throws SQLException {
        String sql = "UPDATE customer_tiers SET min_points = ?, max_points = ?, discount_percent = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setFloat(1, tier.getMinPoints());
            ps.setFloat(2, tier.getMaxPoints());
            ps.setFloat(3, tier.getDiscountPercent());
            ps.setString(4, tier.getDescription());
            ps.setInt(5, tier.getId());
            ps.executeUpdate();
        }
    }

    // Add new tier
    public void addTier(CustomerTier tier) throws SQLException {
        String sql = "INSERT INTO customer_tiers (tier_name, min_points, max_points, discount_percent, description) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tier.getTierName());
            ps.setFloat(2, tier.getMinPoints());
            ps.setFloat(3, tier.getMaxPoints());
            ps.setFloat(4, tier.getDiscountPercent());
            ps.setString(5, tier.getDescription());
            ps.executeUpdate();
        }
    }

    // Delete tier
    public void deleteTier(int tierId) throws SQLException {
        String sql = "DELETE FROM customer_tiers WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, tierId);
            ps.executeUpdate();
        }
    }

    // Get discount percent for a customer based on their accumulated points
    public float getDiscountForCustomer(int customerId) throws SQLException {
        String sql = "SELECT t.discount_percent " +
                    "FROM customers c " +
                    "JOIN customer_tiers t ON c.accumulatedPoint >= t.min_points AND c.accumulatedPoint <= t.max_points " +
                    "WHERE c.id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("discount_percent");
                }
            }
        }
        return 0; // Default no discount
    }

    // Get tier name for a customer based on their accumulated points
    public String getTierNameForCustomer(int customerId) throws SQLException {
        String sql = "SELECT t.tier_name " +
                    "FROM customers c " +
                    "JOIN customer_tiers t ON c.accumulatedPoint >= t.min_points AND c.accumulatedPoint <= t.max_points " +
                    "WHERE c.id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tier_name");
                }
            }
        }
        return "Bronze"; // Default tier
    }
}
