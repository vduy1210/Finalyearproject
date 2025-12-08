package dao;

import database.DatabaseConnector;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetProduct {
    public static List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String query = "SELECT id, name, price FROM products";

        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                list.add(new Product(id, name, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
