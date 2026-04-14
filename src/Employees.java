import net.proteanit.sql.DbUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Employees extends JFrame {
    public JPanel jpanel;

    private JTable table1;
    private JTextField id;
    private JTextField address;
    private JTextField FIO;
    private JTextField pNumber;
    private JTextField date;
    private JTextField param;
    private JComboBox<String> kruzhok;
    private JComboBox<String> position;
    private JComboBox<String> group;
    private JButton обновитьButton;
    private JButton добавитьButton;
    private JButton удалитьButton;
    private JButton отчетПоНомеруButton;
    private JButton просмотретьОтчетButton;

    Connection conn = null;
    ResultSet rs = null;
    Statement statement = null;
    PreparedStatement pst = null;
    CallableStatement stored_pro = null;

    public Employees() {
        buildUi();
        loadComboBoxes();
        bindActions();
        UpdateJTable2();
    }

    private void buildUi() {
        jpanel = new JPanel(new BorderLayout());
        jpanel.setBackground(new Color(240, 240, 240));
        jpanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 14));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));

        contentPanel.add(createTablePanel(), BorderLayout.NORTH);
        contentPanel.add(createEditorPanel(), BorderLayout.CENTER);
        contentPanel.add(createBottomButtonsPanel(), BorderLayout.SOUTH);

        jpanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JComponent createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 6));
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(12, 12, 0, 12));

        JLabel titleLabel = new JLabel("Список всех сотрудников", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tablePanel.add(titleLabel, BorderLayout.NORTH);

        table1 = new JTable();
        table1.setRowHeight(24);
        table1.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table1);
        scrollPane.setPreferredSize(new Dimension(0, 110));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JComponent createEditorPanel() {
        JPanel editorPanel = new JPanel(new GridBagLayout());
        editorPanel.setOpaque(false);
        editorPanel.setBorder(new EmptyBorder(6, 12, 12, 12));

        id = new JTextField();
        address = new JTextField();
        FIO = new JTextField();
        pNumber = new JTextField();
        date = new JTextField();
        param = new JTextField();
        param.setVisible(false);

        kruzhok = new JComboBox<>();
        position = new JComboBox<>();
        group = new JComboBox<>();

        обновитьButton = createActionButton("Обновить");
        добавитьButton = createActionButton("Добавить");
        удалитьButton = createActionButton("Удалить");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addField(editorPanel, gbc, 0, 0, "№", id);
        addField(editorPanel, gbc, 1, 0, "Адрес", address);
        addControl(editorPanel, gbc, 2, 1, обновитьButton);

        addField(editorPanel, gbc, 0, 2, "ФИО", FIO);
        addField(editorPanel, gbc, 1, 2, "Номер телефона", pNumber);
        addControl(editorPanel, gbc, 2, 3, добавитьButton);

        addField(editorPanel, gbc, 0, 4, "Дата рождения", date);
        addField(editorPanel, gbc, 1, 4, "Кружок", kruzhok);
        addControl(editorPanel, gbc, 2, 5, удалитьButton);

        addField(editorPanel, gbc, 0, 6, "Должность", position);
        addField(editorPanel, gbc, 1, 6, "Группа", group);
        addControl(editorPanel, gbc, 2, 7, param);

        return editorPanel;
    }

    private JComponent createBottomButtonsPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 12, 12, 12));

        отчетПоНомеруButton = createActionButton("Отчет по номеру");
        просмотретьОтчетButton = createActionButton("Просмотреть отчет");

        bottomPanel.add(отчетПоНомеруButton);
        bottomPanel.add(просмотретьОтчетButton);
        return bottomPanel;
    }

    private void bindActions() {
        обновитьButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conn = Main.ConnectDB();
                    stored_pro = conn.prepareCall("{call update_employee (?,?,?,?,?,?,?,?)}");
                    stored_pro.setString(1, id.getText());
                    stored_pro.setString(2, FIO.getText());
                    stored_pro.setString(3, date.getText());
                    stored_pro.setString(4, (String) position.getSelectedItem());
                    stored_pro.setString(5, address.getText());
                    stored_pro.setString(6, pNumber.getText());
                    stored_pro.setString(7, (String) kruzhok.getSelectedItem());
                    stored_pro.setString(8, (String) group.getSelectedItem());
                    stored_pro.execute();
                    JOptionPane.showMessageDialog(null, "Updated");
                    clearFields();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex);
                }
                UpdateJTable2();
            }
        });

        добавитьButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conn = Main.ConnectDB();
                    stored_pro = conn.prepareCall("{call insert_employee (?,?,?,?,?,?,?)}");
                    stored_pro.setString(1, FIO.getText());
                    stored_pro.setString(2, date.getText());
                    stored_pro.setString(3, (String) position.getSelectedItem());
                    stored_pro.setString(4, address.getText());
                    stored_pro.setString(5, pNumber.getText());
                    stored_pro.setString(6, (String) kruzhok.getSelectedItem());
                    stored_pro.setString(7, (String) group.getSelectedItem());
                    stored_pro.execute();
                    JOptionPane.showMessageDialog(null, "Saved");
                    clearFields();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex);
                }
                UpdateJTable2();
            }
        });

        удалитьButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conn = Main.ConnectDB();
                    stored_pro = conn.prepareCall("{call delete_employee (?)}");
                    stored_pro.setString(1, id.getText());
                    stored_pro.execute();
                    JOptionPane.showMessageDialog(null, "Deleted");
                    clearFields();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex);
                }
                UpdateJTable2();
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                try {
                    conn = Main.ConnectDB();
                    int row = table1.getSelectedRow();
                    String Id = table1.getModel().getValueAt(row, 0).toString();
                    String query = "select * from View_employee where id='" + Id + "'";
                    PreparedStatement pst = conn.prepareStatement(query);
                    ResultSet rs = pst.executeQuery();
                    while (rs.next()) {
                        id.setText(rs.getString("id"));
                        FIO.setText(rs.getString("FIO"));
                        date.setText(rs.getString("dateborn"));
                        position.setSelectedItem(rs.getString("position"));
                        address.setText(rs.getString("adress"));
                        pNumber.setText(rs.getString("tel_number"));
                        kruzhok.setSelectedItem(rs.getString("kruzhok"));
                        group.setSelectedItem(rs.getString("groups"));
                        param.setText(rs.getString("id"));
                    }
                    pst.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        });
    }

    private void loadComboBoxes() {
        try {
            conn = Main.ConnectDB();
            position.addItem(null);
            kruzhok.addItem(null);
            group.addItem(null);
            open_position();
            open_kruzhok();
            open_group();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void clearFields() {
        id.setText("");
        FIO.setText("");
        date.setText("");
        position.setSelectedItem(null);
        address.setText("");
        pNumber.setText("");
        kruzhok.setSelectedItem(null);
        group.setSelectedItem(null);
        param.setText("");
    }

    private void UpdateJTable2() {
        try {
            conn = Main.ConnectDB();
            String sql = "SELECT [id], [FIO], [dateborn], [position], [adress], [tel_number], [kruzhok], [groups] FROM [View_employee]";
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            this.table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void open_position() {
        try {
            String sql = "Select * from position";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                String Position = rs.getString("position");
                position.addItem(Position);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void open_kruzhok() {
        try {
            String sql = "Select *from kruzhok";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                String Kruzhok = rs.getString("kruzhok");
                kruzhok.addItem(Kruzhok);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void open_group() {
        try {
            String sql = "Select *from [group]";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                String groups = rs.getString("groups");
                group.addItem(groups);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int column, int row, String labelText, JComponent field) {
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        panel.add(createLabel(labelText), gbc);

        gbc.gridy = row + 1;
        panel.add(prepareInput(field), gbc);
    }

    private void addControl(JPanel panel, GridBagConstraints gbc, int column, int row, JComponent component) {
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.weightx = 0.95;
        panel.add(prepareInput(component), gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return label;
    }

    private JComponent prepareInput(JComponent component) {
        component.setPreferredSize(new Dimension(220, 36));
        if (component instanceof JTextField) {
            ((JTextField) component).setColumns(14);
        }
        return component;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setFocusPainted(false);
        return button;
    }
}
