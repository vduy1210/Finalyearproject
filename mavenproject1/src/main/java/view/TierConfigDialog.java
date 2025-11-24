package view;

import dao.CustomerTierDAO;
import model.CustomerTier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TierConfigDialog extends JDialog {
    private CustomerTierDAO tierDAO;
    private DefaultTableModel tierModel;
    private JTable tierTable;
    
    private static final Color BACKGROUND_COLOR = new Color(0xDD, 0xE3, 0xEA);
    private static final Color WHITE = Color.WHITE;
    private static final Color COLOR_TEXT = new Color(33, 37, 41);
    private static final Color COLOR_BORDER = new Color(230, 235, 241);

    public TierConfigDialog(Frame owner) {
        super(owner, "Tier Configuration", true);
        tierDAO = new CustomerTierDAO();
        
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(WHITE);
        titlePanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel("Customer Tier Configuration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_TEXT);
        JLabel subtitleLabel = new JLabel("Configure tier levels and discount rates based on accumulated points");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        JPanel titleContent = new JPanel(new GridLayout(2, 1, 0, 5));
        titleContent.setOpaque(false);
        titleContent.add(titleLabel);
        titleContent.add(subtitleLabel);
        titlePanel.add(titleContent, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);
        
        // Table
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        tierModel = new DefaultTableModel(
            new String[]{"ID", "Tier Name", "Min Points", "Max Points", "Discount %", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tierTable = new JTable(tierModel);
        tierTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tierTable.setRowHeight(40);
        tierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tierTable.setShowGrid(false);
        tierTable.setBackground(WHITE);
        tierTable.setSelectionBackground(new Color(232, 244, 253));
        
        // Hide ID column
        tierTable.getColumnModel().getColumn(0).setMinWidth(0);
        tierTable.getColumnModel().getColumn(0).setMaxWidth(0);
        tierTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(tierTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 15, 20));
        
        RoundedButton editBtn = new RoundedButton("Edit Selected");
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editBtn.addActionListener(_ -> editSelectedTier());
        
        RoundedButton addBtn = new RoundedButton("Add New Tier");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.addActionListener(_ -> addNewTier());
        
        RoundedButton deleteBtn = new RoundedButton("Delete");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.setBackground(new Color(220, 53, 69));
        deleteBtn.addActionListener(_ -> deleteSelectedTier());
        
        RoundedButton closeBtn = new RoundedButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setBackground(new Color(108, 117, 125));
        closeBtn.addActionListener(_ -> dispose());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(closeBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        loadTiers();
        
        setSize(900, 500);
        setLocationRelativeTo(owner);
    }
    
    private void loadTiers() {
        tierModel.setRowCount(0);
        try {
            List<CustomerTier> tiers = tierDAO.getAllTiers();
            for (CustomerTier tier : tiers) {
                tierModel.addRow(new Object[]{
                    tier.getId(),
                    tier.getTierName(),
                    tier.getMinPoints(),
                    tier.getMaxPoints(),
                    tier.getDiscountPercent() + "%",
                    tier.getDescription()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading tiers: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editSelectedTier() {
        int row = tierTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a tier to edit.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tierModel.getValueAt(row, 0);
        String name = (String) tierModel.getValueAt(row, 1);
        float minPoints = Float.parseFloat(tierModel.getValueAt(row, 2).toString());
        float maxPoints = Float.parseFloat(tierModel.getValueAt(row, 3).toString());
        String discountStr = tierModel.getValueAt(row, 4).toString().replace("%", "");
        float discount = Float.parseFloat(discountStr);
        String description = (String) tierModel.getValueAt(row, 5);
        
        JTextField nameField = new JTextField(name);
        nameField.setEnabled(false); // Don't allow changing tier name
        JTextField minPointsField = new JTextField(String.valueOf(minPoints));
        JTextField maxPointsField = new JTextField(String.valueOf(maxPoints));
        JTextField discountField = new JTextField(String.valueOf(discount));
        JTextField descField = new JTextField(description);
        
        Object[] fields = {
            "Tier Name:", nameField,
            "Min Points:", minPointsField,
            "Max Points:", maxPointsField,
            "Discount %:", discountField,
            "Description:", descField
        };
        
        int result = JOptionPane.showConfirmDialog(this, fields, "Edit Tier", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                CustomerTier tier = new CustomerTier();
                tier.setId(id);
                tier.setTierName(name);
                tier.setMinPoints(Float.parseFloat(minPointsField.getText().trim()));
                tier.setMaxPoints(Float.parseFloat(maxPointsField.getText().trim()));
                tier.setDiscountPercent(Float.parseFloat(discountField.getText().trim()));
                tier.setDescription(descField.getText().trim());
                
                tierDAO.updateTier(tier);
                loadTiers();
                JOptionPane.showMessageDialog(this, 
                    "Tier updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid number format. Please check your inputs.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error updating tier: " + ex.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addNewTier() {
        JTextField nameField = new JTextField();
        JTextField minPointsField = new JTextField("0");
        JTextField maxPointsField = new JTextField("999");
        JTextField discountField = new JTextField("0");
        JTextField descField = new JTextField();
        
        Object[] fields = {
            "Tier Name:", nameField,
            "Min Points:", minPointsField,
            "Max Points:", maxPointsField,
            "Discount %:", discountField,
            "Description:", descField
        };
        
        int result = JOptionPane.showConfirmDialog(this, fields, "Add New Tier", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                CustomerTier tier = new CustomerTier();
                tier.setTierName(nameField.getText().trim());
                tier.setMinPoints(Float.parseFloat(minPointsField.getText().trim()));
                tier.setMaxPoints(Float.parseFloat(maxPointsField.getText().trim()));
                tier.setDiscountPercent(Float.parseFloat(discountField.getText().trim()));
                tier.setDescription(descField.getText().trim());
                
                tierDAO.addTier(tier);
                loadTiers();
                JOptionPane.showMessageDialog(this, 
                    "Tier added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid number format. Please check your inputs.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error adding tier: " + ex.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteSelectedTier() {
        int row = tierTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a tier to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) tierModel.getValueAt(row, 0);
        String name = (String) tierModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete tier '" + name + "'?\nCustomers with this tier will be set to Bronze.", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                tierDAO.deleteTier(id);
                loadTiers();
                JOptionPane.showMessageDialog(this, 
                    "Tier deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error deleting tier: " + ex.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
