import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;  // Only one import for DefaultTableModel
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;


public class Projekt extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTabbedPane tabbedPane2;
    private JTable table1;
    private JTabbedPane tabbedPane3;
    private JButton KrajiAdd;
    private JTextField KrajIme;
    private JTextField KrajPosta;
    private JButton KrajiRemoveSelected;
    private JTable table2;
    private JTable table3;
    private JButton OsebjeAdd;
    private JTextField OsebjeIme;
    private JTextField OsebjePriimek;
    private JTextField OsebjeVloga;
    private JTextField OsebjeTelefon;
    private JTextField OsebjeEmail;
    private JTextField OsebjeEmso;
    private JComboBox comboBox1;
    private JButton OsebjeRemoveSelected;
    private JButton SobaAdd;
    private JTextField SobaCena;
    private JTextField SobaKapaciteta;
    private JComboBox comboBox2;
    private JButton PrenocisceAdd;
    private JTextField PrenocisceIme;
    private JTextField PrenocisceNaslov;
    private JTextField PrenocisceOcena;
    private JTextField PrenocisceTelefon;
    private JTextField PrenocisceEmail;
    private JComboBox PrenocisceKraj;
    private JComboBox PrenocisceTip;
    private JButton TipPrenociscaAdd;
    private JTextField TipPrenociscaTip;
    private JButton SobaRemoveSelected;
    private JTable table4;
    private JButton TipPrenociscaRemoveSelected;
    private JTable table5;
    private JButton PrenociscaRemoveSelected;
    private JTable table6;

    private static final String URL = "jdbc:postgresql://ep-black-unit-a8nv3z9b-pooler.eastus2.azure.neon.tech/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_0NOprj8XVGnk"; // Change this immediately

    public Projekt() {
        setContentPane(panel1);
        setTitle("Kraji Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Load the data into ComboBox on startup
        loadPrenociscaToComboBox();
        loadPrenociscaToComboBox2();
        loadKrajiToComboBox();
        loadTipiToComboBox();
        loadDataIntoTable();

        // Add new location (kraj)
        KrajiAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String krajIme = KrajIme.getText();
                String krajPosta = KrajPosta.getText();

                if (krajIme.isEmpty() || krajPosta.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter all fields!");
                    return;
                }

                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    PreparedStatement stmt = connection.prepareStatement("SELECT insert_kraj(?, ?)");
                    stmt.setString(1, krajIme);
                    stmt.setString(2, krajPosta);
                    stmt.execute();

                    JOptionPane.showMessageDialog(null, "Data inserted successfully!");
                    loadDataIntoTable();  // Reload the table after insertion
                    KrajIme.setText("");
                    KrajPosta.setText("");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error inserting data: " + ex.getMessage());
                }
            }
        });
        OsebjeAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get selected prenocisca ID from ComboBox
                Object selectedItem = comboBox1.getSelectedItem();
                int prenocisceId = -1;

                if (selectedItem != null && !selectedItem.equals("Please select...")) {
                    try {
                        // Extract the ID part before the colon (e.g., "1" from "1: Hotel XYZ")
                        prenocisceId = Integer.parseInt(selectedItem.toString().split(":")[0].trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid prenocisca selected!");
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a valid prenocisca!");
                    return;
                }

                // Get other personal details for Osebje
                String OsebjeImeStr = OsebjeIme.getText();
                String OsebjePriimekStr = OsebjePriimek.getText();
                String OsebjeVlogaStr = OsebjeVloga.getText();
                String OsebjeTelefonStr = OsebjeTelefon.getText();
                String OsebjeEmailStr = OsebjeEmail.getText();
                String OsebjeEmsoStr = OsebjeEmso.getText();

                if (OsebjeImeStr.isEmpty() || OsebjePriimekStr.isEmpty() || OsebjeVlogaStr.isEmpty() || OsebjeTelefonStr.isEmpty() || OsebjeEmsoStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter all fields!");
                    return;
                }

                // If email is empty, set it to null (or "/")
                if (OsebjeEmailStr.isEmpty()) {
                    OsebjeEmailStr = "/";  // You can set to null if needed
                }

                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    // PreparedStatement to insert data using the stored procedure
                    PreparedStatement stmt = connection.prepareStatement("SELECT insert_osebje(?, ?, ?, ?, ?, ?, ?)");
                    stmt.setString(1, OsebjeImeStr);
                    stmt.setString(2, OsebjePriimekStr);
                    stmt.setString(3, OsebjeVlogaStr);
                    stmt.setString(4, OsebjeTelefonStr);
                    stmt.setString(5, OsebjeEmailStr);
                    stmt.setString(6, OsebjeEmsoStr);
                    stmt.setInt(7, prenocisceId);  // Add prenocisceId as foreign key
                    stmt.execute();

                    JOptionPane.showMessageDialog(null, "Data inserted successfully!");
                    loadOsebjeDataIntoTable();  // Reload the table after insertion
                    // Clear text fields
                    OsebjeIme.setText("");
                    OsebjePriimek.setText("");
                    OsebjeVloga.setText("");
                    OsebjeTelefon.setText("");
                    OsebjeEmail.setText("");
                    OsebjeEmso.setText("");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error inserting data: " + ex.getMessage());
                }
            }
        });
        PrenocisceAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ime = PrenocisceIme.getText();
                String naslov = PrenocisceNaslov.getText();
                String telefon = PrenocisceTelefon.getText();
                String email = PrenocisceEmail.getText();
                String ocenaStr = PrenocisceOcena.getText();
                Object selectedKraj = PrenocisceKraj.getSelectedItem();
                Object selectedTip = PrenocisceTip.getSelectedItem();

                if (ime.isEmpty() || naslov.isEmpty() || telefon.isEmpty() || email.isEmpty() || ocenaStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter all fields!");
                    return;
                }

                int krajId = Integer.parseInt(selectedKraj.toString().split(":")[0].trim());
                int tipPrenociscaId = Integer.parseInt(selectedTip.toString().split(":")[0].trim());
                int ocena = Integer.parseInt(ocenaStr);

                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    PreparedStatement stmt = connection.prepareStatement("SELECT insert_prenocisca(?, ?, ?, ?, ?, ?, ?)");
                    stmt.setString(1, ime);
                    stmt.setString(2, naslov);
                    stmt.setInt(3, ocena);
                    stmt.setString(4, telefon);
                    stmt.setString(5, email);
                    stmt.setInt(6, krajId);
                    stmt.setInt(7, tipPrenociscaId);
                    stmt.execute();
                    JOptionPane.showMessageDialog(null, "Prenocisca added successfully!");
                    loadPrenociscaDataIntoTable();  // Reload the table after insertion
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding Prenocisca: " + ex.getMessage());
                }
            }
        });
        SobaAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cenaStr = SobaCena.getText();
                String kapacitetaStr = SobaKapaciteta.getText();
                Object selectedPrenocisce = comboBox2.getSelectedItem();

                if (cenaStr.isEmpty() || kapacitetaStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter all fields!");
                    return;
                }

                int prenocisceId = Integer.parseInt(selectedPrenocisce.toString().split(":")[0].trim());
                String cena = SobaCena.getText();
                int kapaciteta = Integer.parseInt(kapacitetaStr);

                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    PreparedStatement stmt = connection.prepareStatement("SELECT insert_soba(?, ?, ?)");
                    stmt.setString(1, cena);
                    stmt.setInt(2, kapaciteta);
                    stmt.setInt(3, prenocisceId);
                    stmt.execute();
                    JOptionPane.showMessageDialog(null, "Soba added successfully!");
                    loadSobaDataIntoTable();  // Reload the table after insertion
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding Soba: " + ex.getMessage());
                }
            }
        });
        // Add TipPrenocisca
        TipPrenociscaAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tip = TipPrenociscaTip.getText();
                if (tip.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a tip!");
                    return;
                }

                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    PreparedStatement stmt = connection.prepareStatement("SELECT insert_tip_prenocisca(?)");
                    stmt.setString(1, tip);
                    stmt.execute();
                    JOptionPane.showMessageDialog(null, "Tip Prenocisca added successfully!");
                    loadTipDataIntoTable();  // Reload the table after insertion
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding Tip Prenocisca: " + ex.getMessage());
                }
            }
        });

// Remove TipPrenocisca
        TipPrenociscaRemoveSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table4.getSelectedRows();

                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(null, "Please select a row to remove");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected rows?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        for (int row : selectedRows) {
                            int id = (Integer) table4.getValueAt(row, 0);
                            PreparedStatement stmt = connection.prepareStatement("SELECT delete_tip_prenocisca(?)");
                            stmt.setInt(1, id);
                            stmt.execute();
                        }
                        loadTipDataIntoTable();  // Reload table after deletion
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting Tip Prenocisca: " + ex.getMessage());
                    }
                }
            }
        });


        // Remove selected rows
        KrajiRemoveSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table1.getSelectedRows();

                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(null, "Please select a row to remove");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected rows?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        for (int row : selectedRows) {
                            int id = (Integer) table1.getValueAt(row, 0);
                            PreparedStatement stmt = connection.prepareStatement("SELECT delete_kraj(?)");
                            stmt.setInt(1, id);
                            stmt.execute();
                        }
                        loadDataIntoTable();  // Reload the table after deletion
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting data: " + ex.getMessage());
                    }
                }
            }
        });
        OsebjeRemoveSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected rows in the table
                int[] selectedRows = table2.getSelectedRows();

                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(null, "Please select a row to remove");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected rows?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        for (int row : selectedRows) {
                            int id = (Integer) table2.getValueAt(row, 0);  // Assuming the ID is in the first column

                            // Call the stored procedure to delete the record from the database
                            PreparedStatement stmt = connection.prepareStatement("SELECT delete_osebje(?)");
                            stmt.setInt(1, id);
                            stmt.execute();

                            // Optionally, update the table after deletion (reload the data)
                            loadOsebjeDataIntoTable();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting data: " + ex.getMessage());
                    }
                }
            }
        });
        PrenociscaRemoveSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table5.getSelectedRows();

                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(null, "Please select a row to remove");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected rows?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        for (int row : selectedRows) {
                            int id = (Integer) table5.getValueAt(row, 0);
                            PreparedStatement stmt = connection.prepareStatement("SELECT delete_prenocisca(?)");
                            stmt.setInt(1, id);
                            stmt.execute();
                        }
                        loadPrenociscaDataIntoTable();  // Reload table after deletion
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting Prenocisca: " + ex.getMessage());
                    }
                }
            }
        });
        SobaRemoveSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table3.getSelectedRows();

                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(null, "Please select a row to remove");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected rows?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        for (int row : selectedRows) {
                            int id = (Integer) table3.getValueAt(row, 0);
                            PreparedStatement stmt = connection.prepareStatement("SELECT delete_soba(?)");
                            stmt.setInt(1, id);
                            stmt.execute();
                        }
                        loadSobaDataIntoTable();  // Reload table after deletion
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting Soba: " + ex.getMessage());
                    }
                }
            }
        });

        tabbedPane2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane2.getSelectedIndex();
                if (selectedIndex == 0) {
                    loadDataIntoTable();
                } else if (selectedIndex == 1) {
                    loadOsebjeDataIntoTable();
                } else if (selectedIndex == 2) {
                    loadSobaDataIntoTable();
                } else if (selectedIndex == 3) {
                    loadPrenociscaDataIntoTable();
                } else if (selectedIndex == 4) {
                    loadTipDataIntoTable();
                } else if (selectedIndex == 5) {
                    loadLogDataIntoTable();
                }
            }
        });


        // Action listener for the "OsebjeRemoveSelected" button


    }

    public static void main(String[] args) {
        new Projekt();
    }

    private void loadPrenociscaToComboBox() {
        comboBox1.removeAllItems();  // Clear any existing data
        comboBox1.addItem("Please select...");

        String query = "SELECT * FROM select_prenocisca2()";  // Adjust this query according to your table structure

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");  // Get the ID of prenocisca
                String name = rs.getString("ime");  // Get the name of prenocisca
                comboBox1.addItem(id + ": " + name);  // Add formatted value to the comboBox
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading prenocisca: " + e.getMessage());
        }
    }

    private void loadPrenociscaToComboBox2() {
        comboBox2.removeAllItems();  // Clear any existing data
        comboBox2.addItem("Please select...");

        String query = "SELECT * FROM select_prenocisca2()";  // Adjust this query according to your table structure

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");  // Get the ID of prenocisca
                String name = rs.getString("ime");  // Get the name of prenocisca
                comboBox2.addItem(id + ": " + name);  // Add formatted value to the comboBox
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading prenocisca: " + e.getMessage());
        }
    }

    private void loadKrajiToComboBox() {
        PrenocisceKraj.removeAllItems();  // Clear any existing data
        PrenocisceKraj.addItem("Please select...");

        String query = "SELECT * FROM select_kraji2()";  // Adjust this query according to your table structure

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");  // Get the ID of prenocisca
                String name = rs.getString("ime");  // Get the name of prenocisca
                PrenocisceKraj.addItem(id + ": " + name);  // Add formatted value to the comboBox
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading prenocisca: " + e.getMessage());
        }
    }

    private void loadTipiToComboBox() {
        PrenocisceTip.removeAllItems();  // Clear any existing data
        PrenocisceTip.addItem("Please select...");

        String query = "SELECT * FROM select_tip_prenocisca2()";  // Adjust this query according to your table structure

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");  // Get the ID of prenocisca
                String name = rs.getString("tip");  // Get the name of prenocisca
                PrenocisceTip.addItem(id + ": " + name);  // Add formatted value to the comboBox
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading prenocisca: " + e.getMessage());
        }
    }

    private void loadDataIntoTable() {
        String procedureCall = "{CALL select_kraj()}";
        DefaultTableModel tableModel = new DefaultTableModel();
        table1.setModel(tableModel);

        tableModel.addColumn("ID");
        tableModel.addColumn("Ime");
        tableModel.addColumn("Posta");

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ime = resultSet.getString("ime");
                String posta = resultSet.getString("posta");
                tableModel.addRow(new Object[]{id, ime, posta});
            }

            // Reattach the TableModelListener after reloading the data
            tableModel.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    if (e.getType() == TableModelEvent.UPDATE && column != 0) { // Ignore ID column (index 0)
                        DefaultTableModel model = (DefaultTableModel) table1.getModel();

                        int id = (int) model.getValueAt(row, 0); // ID Column (assumed index 0)
                        String ime = (String) model.getValueAt(row, 1); // Name Column (assumed index 1)
                        String posta = (String) model.getValueAt(row, 2); // Posta Column (assumed index 2)


                        JOptionPane.showMessageDialog(null, "Table edited!");
                        updateDatabaseWithProcedure(id, ime, posta);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    private void loadOsebjeDataIntoTable() {
        String procedureCall = "SELECT * FROM select_osebje()";
        DefaultTableModel tableModel = new DefaultTableModel();
        table2.setModel(tableModel);

        tableModel.addColumn("ID");
        tableModel.addColumn("Ime");
        tableModel.addColumn("Priimek");
        tableModel.addColumn("Vloga");
        tableModel.addColumn("Telefon");
        tableModel.addColumn("Email");
        tableModel.addColumn("EMSO");

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall);
             ResultSet resultSet = stmt.executeQuery()) {

            boolean hasData = false;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ime = resultSet.getString("ime");
                String priimek = resultSet.getString("priimek");
                String vloga = resultSet.getString("vloga");
                String telefon = resultSet.getString("telefon");
                String email = resultSet.getString("email");
                String emso = resultSet.getString("emso");

                tableModel.addRow(new Object[]{id, ime, priimek, vloga, telefon, email, emso});
                hasData = true;
            }

            if (!hasData) {
                JOptionPane.showMessageDialog(null, "No data found for Osebje!");
            }

            // Reattach the TableModelListener after reloading the data
            tableModel.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    if (e.getType() == TableModelEvent.UPDATE && column != 0) { // Ignore ID column (index 0)
                        DefaultTableModel model = (DefaultTableModel) table2.getModel();

                        int id = (int) model.getValueAt(row, 0); // ID Column (assumed index 0)
                        String ime = (String) model.getValueAt(row, 1); // Name Column (assumed index 1)
                        String priimek = (String) model.getValueAt(row, 2); // Priimek Column (assumed index 2)
                        String vloga = (String) model.getValueAt(row, 3); // Vloga Column (assumed index 3)
                        String telefon = (String) model.getValueAt(row, 4); // Telefon Column (assumed index 4)
                        String email = (String) model.getValueAt(row, 5); // Email Column (assumed index 5)
                        String emso = (String) model.getValueAt(row, 6); // EMSO Column (assumed index 6)

                        JOptionPane.showMessageDialog(null, "🔄 Table Edited! ID: " + id + ", Ime: " + ime + ", Priimek: " + priimek + ", Vloga: " + vloga);
                        updateOsebjeDatabaseWithProcedure(id, ime, priimek, vloga, telefon, email, emso);
                    }
                }
            });

            table2.revalidate();
            table2.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    private void loadPrenociscaDataIntoTable() {
        String procedureCall = "Select * from select_prenocisca()";
        DefaultTableModel tableModel = new DefaultTableModel();
        table5.setModel(tableModel);

        tableModel.addColumn("ID");
        tableModel.addColumn("Ime");
        tableModel.addColumn("Naslov");
        tableModel.addColumn("Ocena");
        tableModel.addColumn("Telefon");
        tableModel.addColumn("Email");


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ime = resultSet.getString("ime");
                String naslov = resultSet.getString("naslov");
                int ocena = resultSet.getInt("ocena");
                String telefon = resultSet.getString("telefon");
                String email = resultSet.getString("email");

                tableModel.addRow(new Object[]{id, ime, naslov, ocena, telefon, email});
            }

            // Reattach the TableModelListener after reloading the data
            tableModel.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    if (e.getType() == TableModelEvent.UPDATE && column != 0) { // Ignore ID column (index 0)
                        DefaultTableModel model = (DefaultTableModel) table5.getModel();

                        int id = (int) model.getValueAt(row, 0); // ID Column (assumed index 0)
                        String ime = (String) model.getValueAt(row, 1); // Name Column (assumed index 1)
                        String naslov = (String) model.getValueAt(row, 2); // Posta Column (assumed index 2)
                        Object ocenaValue = model.getValueAt(row, 3);
                        int ocena;
                        if (ocenaValue instanceof String) {
                            try {
                                ocena = Integer.parseInt((String) ocenaValue);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Invalid number format for ocena.");
                                return; // Exit if conversion fails
                            }
                        } else if (ocenaValue instanceof Integer) {
                            ocena = (Integer) ocenaValue;
                        } else {
                            ocena = 0; // Default value if unexpected type
                        }

                        String telefon = (String) model.getValueAt(row, 4);
                        String email = (String) model.getValueAt(row, 5);


                        JOptionPane.showMessageDialog(null, "Table edited!");
                        updatePrenociscaDatabaseWithProcedure(id, ime, naslov, ocena, telefon, email);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    private void loadSobaDataIntoTable() {
        String procedureCall = "Select * from select_soba()";
        DefaultTableModel tableModel = new DefaultTableModel();
        table3.setModel(tableModel);

        tableModel.addColumn("ID");
        tableModel.addColumn("Cena");
        tableModel.addColumn("Kapaciteta");


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String cena = resultSet.getString("cena");
                int kapaciteta = resultSet.getInt("kapaciteta");

                tableModel.addRow(new Object[]{id, cena, kapaciteta});
            }

            // Reattach the TableModelListener after reloading the data
            tableModel.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    if (e.getType() == TableModelEvent.UPDATE && column != 0) { // Ignore ID column (index 0)
                        DefaultTableModel model = (DefaultTableModel) table3.getModel();

                        int id = (int) model.getValueAt(row, 0); // ID Column (assumed index 0)
                        String cena = (String) model.getValueAt(row, 1); // Name Column (assumed index 1)
                        Object kapacitetaValue = model.getValueAt(row, 2);
                        int kapaciteta;
                        if (kapacitetaValue instanceof String) {
                            try {
                                kapaciteta = Integer.parseInt((String) kapacitetaValue);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Invalid number format for ocena.");
                                return; // Exit if conversion fails
                            }
                        } else if (kapacitetaValue instanceof Integer) {
                            kapaciteta = (Integer) kapacitetaValue;
                        } else {
                            kapaciteta = 0; // Default value if unexpected type
                        }


                        JOptionPane.showMessageDialog(null, "Table edited!");
                        updateSobaDatabaseWithProcedure(id, cena, kapaciteta);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    private void loadTipDataIntoTable() {
        String procedureCall = "Select * from select_tip_prenocisca()";
        DefaultTableModel tableModel = new DefaultTableModel();
        table4.setModel(tableModel);

        tableModel.addColumn("ID");
        tableModel.addColumn("TIP");


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String tip = resultSet.getString("tip");

                tableModel.addRow(new Object[]{id, tip});
            }

            // Reattach the TableModelListener after reloading the data
            tableModel.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    if (e.getType() == TableModelEvent.UPDATE && column != 0) { // Ignore ID column (index 0)
                        DefaultTableModel model = (DefaultTableModel) table4.getModel();

                        int id = (int) model.getValueAt(row, 0); // ID Column (assumed index 0)
                        String tip = (String) model.getValueAt(row, 1);

                        JOptionPane.showMessageDialog(null, "Table edited!");
                        updateTipDatabaseWithProcedure(id, tip);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    private void loadLogDataIntoTable() {
        String procedureCall = "Select * from select_log()";
        DefaultTableModel tableModel = new DefaultTableModel();
        table6.setModel(tableModel);

        tableModel.addColumn("ID");
        tableModel.addColumn("IME TABELE");
        tableModel.addColumn("OPERACIJA");
        tableModel.addColumn("ČAS SPREMEBER");


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("log_id");
                String tableName = resultSet.getString("table_name");
                String operacija = resultSet.getString("operation");
                Timestamp cas = resultSet.getTimestamp("changed_at");

                tableModel.addRow(new Object[]{id, tableName, operacija, cas});
            }
            ;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    private void updateDatabaseWithProcedure(int id, String ime, String posta) {
        String procedureCall = "SELECT update_kraj(?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall)) {

            stmt.setInt(1, id);
            stmt.setString(2, ime);
            stmt.setObject(3, posta);
            stmt.execute();

            JOptionPane.showMessageDialog(null, "Data updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating database: " + e.getMessage());
        }
    }

    private void updateOsebjeDatabaseWithProcedure(int id, String ime, String priimek, String vloga, String telefon, String email, String emso) {
        String procedureCall = "{CALL update_osebje(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall)) {

            stmt.setInt(1, id);
            stmt.setString(2, ime);
            stmt.setString(3, priimek);
            stmt.setString(4, vloga);
            stmt.setString(5, telefon);
            stmt.setString(6, email);
            stmt.setString(7, emso);
            stmt.execute();

            System.out.println("✅ Database updated successfully for ID " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating database: " + e.getMessage());
        }
    }

    private void updatePrenociscaDatabaseWithProcedure(int id, String ime, String naslov, int ocena, String telefon, String email) {
        String procedureCall = "SELECT update_prenocisca(?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall)) {

            stmt.setInt(1, id);
            stmt.setString(2, ime);
            stmt.setObject(3, naslov);
            stmt.setInt(4, ocena);
            stmt.setString(5, telefon);
            stmt.setString(6, email);

            stmt.execute();

            JOptionPane.showMessageDialog(null, "Data updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating database: " + e.getMessage());
        }
    }

    private void updateSobaDatabaseWithProcedure(int id, String cena, int kapaciteta) {
        String procedureCall = "SELECT update_soba(?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall)) {

            stmt.setInt(1, id);
            stmt.setString(2, cena);
            stmt.setInt(3, kapaciteta);

            stmt.execute();

            JOptionPane.showMessageDialog(null, "Data updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating database: " + e.getMessage());
        }
    }

    private void updateTipDatabaseWithProcedure(int id, String tip) {
        String procedureCall = "SELECT update_tip_prenocisca(?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = connection.prepareCall(procedureCall)) {

            stmt.setInt(1, id);
            stmt.setString(2, tip);

            stmt.execute();

            JOptionPane.showMessageDialog(null, "Data updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating database: " + e.getMessage());
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1 = new JTabbedPane();
        panel1.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Data", panel2);
        tabbedPane2 = new JTabbedPane();
        panel2.add(tabbedPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("Kraji", panel3);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel3.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        KrajiRemoveSelected = new JButton();
        KrajiRemoveSelected.setText("Remove Selected");
        panel3.add(KrajiRemoveSelected, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("Osebje", panel4);
        OsebjeRemoveSelected = new JButton();
        OsebjeRemoveSelected.setText("Delete");
        panel4.add(OsebjeRemoveSelected, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel4.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table2 = new JTable();
        table2.setColumnSelectionAllowed(false);
        table2.setIntercellSpacing(new Dimension(5, 1));
        scrollPane2.setViewportView(table2);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("Sobe", panel5);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel5.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table3 = new JTable();
        scrollPane3.setViewportView(table3);
        SobaRemoveSelected = new JButton();
        SobaRemoveSelected.setText("Remove");
        panel5.add(SobaRemoveSelected, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("Prenočišča", panel6);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel6.add(scrollPane4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table5 = new JTable();
        scrollPane4.setViewportView(table5);
        PrenociscaRemoveSelected = new JButton();
        PrenociscaRemoveSelected.setText("Remove");
        panel6.add(PrenociscaRemoveSelected, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("Tip Prenočišča", panel7);
        final JScrollPane scrollPane5 = new JScrollPane();
        panel7.add(scrollPane5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table4 = new JTable();
        scrollPane5.setViewportView(table4);
        TipPrenociscaRemoveSelected = new JButton();
        TipPrenociscaRemoveSelected.setText("Remove");
        panel7.add(TipPrenociscaRemoveSelected, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("Logs", panel8);
        final JScrollPane scrollPane6 = new JScrollPane();
        panel8.add(scrollPane6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table6 = new JTable();
        scrollPane6.setViewportView(table6);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Dashboard", panel9);
        tabbedPane3 = new JTabbedPane();
        panel9.add(tabbedPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel10.setAlignmentX(0.5f);
        tabbedPane3.addTab("Kraji", panel10);
        KrajiAdd = new JButton();
        KrajiAdd.setText("Add");
        panel10.add(KrajiAdd, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        KrajIme = new JTextField();
        panel10.add(KrajIme, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        KrajPosta = new JTextField();
        panel10.add(KrajPosta, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Ime");
        panel10.add(label1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Pošta");
        panel10.add(label2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(2, 8, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane3.addTab("Osebje", panel11);
        OsebjeAdd = new JButton();
        OsebjeAdd.setText("Add");
        panel11.add(OsebjeAdd, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OsebjeIme = new JTextField();
        panel11.add(OsebjeIme, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        OsebjePriimek = new JTextField();
        panel11.add(OsebjePriimek, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        OsebjeVloga = new JTextField();
        panel11.add(OsebjeVloga, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        OsebjeTelefon = new JTextField();
        panel11.add(OsebjeTelefon, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        OsebjeEmail = new JTextField();
        panel11.add(OsebjeEmail, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        OsebjeEmso = new JTextField();
        panel11.add(OsebjeEmso, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Ime");
        panel11.add(label3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Priimek");
        panel11.add(label4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Vloga");
        panel11.add(label5, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Telefon");
        panel11.add(label6, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Email");
        panel11.add(label7, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Emšo");
        panel11.add(label8, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBox1 = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        comboBox1.setModel(defaultComboBoxModel1);
        panel11.add(comboBox1, new GridConstraints(1, 7, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane3.addTab("Soba", panel12);
        SobaAdd = new JButton();
        SobaAdd.setText("Add");
        panel12.add(SobaAdd, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        SobaCena = new JTextField();
        panel12.add(SobaCena, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        SobaKapaciteta = new JTextField();
        panel12.add(SobaKapaciteta, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        comboBox2 = new JComboBox();
        panel12.add(comboBox2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label9 = new JLabel();
        label9.setText("Cena");
        panel12.add(label9, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Kapaciteta");
        panel12.add(label10, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(2, 8, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane3.addTab("Prenočišča", panel13);
        PrenocisceAdd = new JButton();
        PrenocisceAdd.setText("Add");
        panel13.add(PrenocisceAdd, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        PrenocisceIme = new JTextField();
        panel13.add(PrenocisceIme, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        PrenocisceNaslov = new JTextField();
        panel13.add(PrenocisceNaslov, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        PrenocisceOcena = new JTextField();
        panel13.add(PrenocisceOcena, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        PrenocisceTelefon = new JTextField();
        panel13.add(PrenocisceTelefon, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        PrenocisceEmail = new JTextField();
        panel13.add(PrenocisceEmail, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        PrenocisceKraj = new JComboBox();
        panel13.add(PrenocisceKraj, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        PrenocisceTip = new JComboBox();
        panel13.add(PrenocisceTip, new GridConstraints(1, 7, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Ime");
        panel13.add(label11, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Naslov");
        panel13.add(label12, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Ocena");
        panel13.add(label13, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Telefon");
        panel13.add(label14, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Email");
        panel13.add(label15, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("Kraj");
        panel13.add(label16, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("Tip");
        panel13.add(label17, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane3.addTab("Tip Prenočišča", panel14);
        TipPrenociscaAdd = new JButton();
        TipPrenociscaAdd.setText("Add");
        panel14.add(TipPrenociscaAdd, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TipPrenociscaTip = new JTextField();
        panel14.add(TipPrenociscaTip, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label18 = new JLabel();
        label18.setText("Tip");
        panel14.add(label18, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
