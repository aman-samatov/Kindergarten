import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Employees extends JFrame {
    public JPanel jpanel;

    private JTable table1;
    private JTextField id;
    private JTextField address;
    private JTextField fio;
    private JTextField phoneNumber;
    private JTextField dateBorn;
    private JTextField reportParam;
    private JComboBox<String> kruzhok;
    private JComboBox<String> position;
    private JComboBox<String> group;
    private JButton updateButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton reportByNumberButton;
    private JButton viewReportButton;

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
        jpanel.setBackground(new Color(242, 242, 242));
        jpanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(new Color(242, 242, 242));
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        contentPanel.add(createTablePanel(), BorderLayout.NORTH);
        contentPanel.add(createEditorPanel(), BorderLayout.CENTER);
        contentPanel.add(createBottomButtonsPanel(), BorderLayout.SOUTH);

        jpanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JComponent createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 6));
        tablePanel.setBackground(jpanel.getBackground());

        JLabel titleLabel = new JLabel("Список всех сотрудников", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));
        tablePanel.add(titleLabel, BorderLayout.NORTH);

        table1 = new JTable();
        table1.setRowHeight(26);
        table1.setFillsViewportHeight(true);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setGridColor(new Color(200, 200, 200));

        JTableHeader header = table1.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(table1);
        scrollPane.setPreferredSize(new Dimension(0, 145));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JComponent createEditorPanel() {
        JPanel editorPanel = new JPanel(new GridBagLayout());
        editorPanel.setBackground(jpanel.getBackground());
        editorPanel.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));

        id = new JTextField();
        address = new JTextField();
        fio = new JTextField();
        phoneNumber = new JTextField();
        dateBorn = new JTextField();
        reportParam = new JTextField();
        reportParam.setVisible(false);

        kruzhok = new JComboBox<>();
        position = new JComboBox<>();
        group = new JComboBox<>();

        updateButton = createActionButton("Обновить");
        addButton = createActionButton("Добавить");
        deleteButton = createActionButton("Удалить");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addField(editorPanel, gbc, 0, 0, "№", id);
        addField(editorPanel, gbc, 1, 0, "Адрес", address);
        addControl(editorPanel, gbc, 2, 1, updateButton);

        addField(editorPanel, gbc, 0, 2, "ФИО", fio);
        addField(editorPanel, gbc, 1, 2, "Номер телефона", phoneNumber);
        addControl(editorPanel, gbc, 2, 3, addButton);

        addField(editorPanel, gbc, 0, 4, "Дата рождения", dateBorn);
        addField(editorPanel, gbc, 1, 4, "Кружок", kruzhok);
        addControl(editorPanel, gbc, 2, 5, deleteButton);

        addField(editorPanel, gbc, 0, 6, "Должность", position);
        addField(editorPanel, gbc, 1, 6, "Группа", group);
        addControl(editorPanel, gbc, 2, 7, reportParam);

        return editorPanel;
    }

    private JComponent createBottomButtonsPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        bottomPanel.setBackground(jpanel.getBackground());
        bottomPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        reportByNumberButton = createActionButton("Отчет по номеру");
        viewReportButton = createActionButton("Просмотреть отчет");

        bottomPanel.add(reportByNumberButton);
        bottomPanel.add(viewReportButton);
        return bottomPanel;
    }

    private void bindActions() {
        updateButton.addActionListener(e -> updateEmployee());
        addButton.addActionListener(e -> addEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        reportByNumberButton.addActionListener(e -> showById());
        viewReportButton.addActionListener(e -> showMeAllReport());

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                populateFieldsFromTable();
            }
        });
    }

    private void updateEmployee() {
        try {
            conn = Main.ConnectDB();
            stored_pro = conn.prepareCall("{call update_employee (?,?,?,?,?,?,?,?)}");
            stored_pro.setString(1, id.getText());
            stored_pro.setString(2, fio.getText());
            stored_pro.setString(3, dateBorn.getText());
            stored_pro.setString(4, (String) position.getSelectedItem());
            stored_pro.setString(5, address.getText());
            stored_pro.setString(6, phoneNumber.getText());
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

    private void addEmployee() {
        try {
            conn = Main.ConnectDB();
            stored_pro = conn.prepareCall("{call insert_employee (?,?,?,?,?,?,?)}");
            stored_pro.setString(1, fio.getText());
            stored_pro.setString(2, dateBorn.getText());
            stored_pro.setString(3, (String) position.getSelectedItem());
            stored_pro.setString(4, address.getText());
            stored_pro.setString(5, phoneNumber.getText());
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

    private void deleteEmployee() {
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

    private void populateFieldsFromTable() {
        try {
            conn = Main.ConnectDB();
            int row = table1.getSelectedRow();
            if (row < 0) {
                return;
            }

            String selectedId = table1.getModel().getValueAt(row, 0).toString();
            String query = "select * from View_employee where id = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, selectedId);
            rs = pst.executeQuery();

            while (rs.next()) {
                id.setText(rs.getString("id"));
                fio.setText(rs.getString("FIO"));
                dateBorn.setText(readColumn(rs, "dateBorn", "dateborn"));
                position.setSelectedItem(rs.getString("position"));
                address.setText(rs.getString("adress"));
                phoneNumber.setText(readColumn(rs, "phoneNumber", "tel_number"));
                kruzhok.setSelectedItem(rs.getString("kruzhok"));
                group.setSelectedItem(rs.getString("groups"));
                reportParam.setText(rs.getString("id"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private String readColumn(ResultSet resultSet, String primaryName, String fallbackName) throws SQLException {
        try {
            return resultSet.getString(primaryName);
        } catch (SQLException ignored) {
            return resultSet.getString(fallbackName);
        }
    }

    private void loadComboBoxes() {
        try {
            conn = Main.ConnectDB();
            resetComboBox(position);
            resetComboBox(kruzhok);
            resetComboBox(group);
            open_position();
            open_kruzhok();
            open_group();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void resetComboBox(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        comboBox.addItem(null);
    }

    private void clearFields() {
        id.setText("");
        fio.setText("");
        dateBorn.setText("");
        position.setSelectedItem(null);
        address.setText("");
        phoneNumber.setText("");
        kruzhok.setSelectedItem(null);
        group.setSelectedItem(null);
        reportParam.setText("");
    }

    private void showReportStub(boolean filteredById) {
        String message = filteredById
                ? "Для кнопки «Отчет по номеру» нужен JasperReports шаблон."
                : "Для кнопки «Просмотреть отчет» нужен JasperReports шаблон.";
        JOptionPane.showMessageDialog(this, message);
    }

    private void showById() {
        try {
            if (reportParam.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Выберите сотрудника из таблицы.");
                return;
            }

            conn = Main.ConnectDB();
            String reportPath = new File("reports/EmplID.jasper").getAbsolutePath();
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("param", Long.parseLong(reportParam.getText().trim()));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(reportPath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, conn);
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e);
        }
    }

    private void showMeAllReport() {
        conn = Main.ConnectDB();

        try {
            String reportPath = new File("reports/Simple_Blue.jasper").getAbsolutePath();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(reportPath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
        } catch (JRException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e);
        }
    }

    private void UpdateJTable2() {
        try {
            conn = Main.ConnectDB();
            String sql = "SELECT [id], [FIO], [dateborn], [position], [adress], [tel_number], [kruzhok], [groups] FROM [View_employee]";
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            table1.setModel(DbUtils.resultSetToTableModel(rs));
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
                position.addItem(rs.getString("position"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void open_kruzhok() {
        try {
            String sql = "Select * from kruzhok";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                kruzhok.addItem(rs.getString("kruzhok"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void open_group() {
        try {
            String sql = "Select * from [group]";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                group.addItem(rs.getString("groups"));
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
        gbc.weightx = 0.85;
        panel.add(prepareInput(component), gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return label;
    }

    private JComponent prepareInput(JComponent component) {
        component.setPreferredSize(new Dimension(230, 38));
        if (component instanceof JTextField) {
            ((JTextField) component).setColumns(14);
        }
        return component;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(230, 38));
        return button;
    }
}
