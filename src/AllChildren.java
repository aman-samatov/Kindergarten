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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class AllChildren extends JFrame {
    public JPanel panelchill;

    private JTable table1;
    private JTextField id;
    private JTextField fio;
    private JTextField dateBorn;
    private JTextField address;
    private JTextField phoneDad;
    private JTextField phoneMom;
    private JTextField param;
    private JComboBox<String> kruzhokCB;
    private JComboBox<String> groupCB;
    private JComboBox<String> nationalityCB;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton reportByIdButton;
    private JButton allChildrenReportButton;
    private JButton chartButton;

    Connection conn = null;
    CallableStatement stored_pro = null;
    Statement statement = null;
    ResultSet rs = null;
    PreparedStatement pst = null;

    public AllChildren() {
        buildUi();
        loadComboBoxes();
        bindActions();
        UpdateJTable();
    }

    private void buildUi() {
        panelchill = new JPanel(new BorderLayout());
        panelchill.setBackground(new Color(242, 242, 242));
        panelchill.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(panelchill.getBackground());
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        contentPanel.add(createTablePanel(), BorderLayout.NORTH);
        contentPanel.add(createEditorPanel(), BorderLayout.CENTER);

        panelchill.add(contentPanel, BorderLayout.CENTER);
    }

    private JComponent createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 6));
        tablePanel.setBackground(panelchill.getBackground());

        JLabel titleLabel = new JLabel("Список всех детей", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
        scrollPane.setPreferredSize(new Dimension(0, 190));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JComponent createEditorPanel() {
        JPanel editorPanel = new JPanel(new GridBagLayout());
        editorPanel.setBackground(panelchill.getBackground());
        editorPanel.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));

        id = new JTextField();
        fio = new JTextField();
        dateBorn = new JTextField();
        address = new JTextField();
        phoneDad = new JTextField();
        phoneMom = new JTextField();
        param = new JTextField();
        param.setEditable(false);

        kruzhokCB = new JComboBox<>();
        groupCB = new JComboBox<>();
        nationalityCB = new JComboBox<>();

        reportByIdButton = createActionButton("Отчет по Id");
        allChildrenReportButton = createActionButton("Отчет");
        chartButton = createActionButton("Диаграмма");
        updateButton = createActionButton("Обновить");
        addButton = createActionButton("Добавить");
        deleteButton = createActionButton("Удалить");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addField(editorPanel, gbc, 0, 0, "Id", id);
        addField(editorPanel, gbc, 1, 0, "Номер телефона папы", phoneDad);
        addField(editorPanel, gbc, 2, 0, "Национальность", nationalityCB);
        addField(editorPanel, gbc, 3, 0, "", param);

        addField(editorPanel, gbc, 0, 2, "Имя", fio);
        addField(editorPanel, gbc, 1, 2, "Номер телефона мамы", phoneMom);
        addControl(editorPanel, gbc, 2, 3, reportByIdButton);
        addControl(editorPanel, gbc, 3, 3, updateButton);

        addField(editorPanel, gbc, 0, 4, "Дата рождения", dateBorn);
        addField(editorPanel, gbc, 1, 4, "Группы", groupCB);
        addControl(editorPanel, gbc, 2, 5, allChildrenReportButton);
        addControl(editorPanel, gbc, 3, 5, addButton);

        addField(editorPanel, gbc, 0, 6, "Адрес", address);
        addField(editorPanel, gbc, 1, 6, "Кружок", kruzhokCB);
        addControl(editorPanel, gbc, 2, 7, chartButton);
        addControl(editorPanel, gbc, 3, 7, deleteButton);

        return editorPanel;
    }

    private void bindActions() {
        updateButton.addActionListener(e -> updateChild());
        addButton.addActionListener(e -> addChild());
        deleteButton.addActionListener(e -> deleteChild());
        reportByIdButton.addActionListener(e -> showById());
        allChildrenReportButton.addActionListener(e -> showMeAllReport());
        chartButton.addActionListener(e -> showVisitChart());

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                populateFieldsFromTable();
            }
        });
    }

    private void updateChild() {
        try {
            conn = Main.ConnectDB();
            stored_pro = conn.prepareCall("{call update_child (?,?,?,?,?,?,?,?,?)}");
            stored_pro.setString(1, id.getText());
            stored_pro.setString(2, fio.getText());
            stored_pro.setString(3, dateBorn.getText());
            stored_pro.setString(4, address.getText());
            stored_pro.setString(5, phoneDad.getText());
            stored_pro.setString(6, phoneMom.getText());
            stored_pro.setString(7, (String) groupCB.getSelectedItem());
            stored_pro.setString(8, (String) kruzhokCB.getSelectedItem());
            stored_pro.setString(9, (String) nationalityCB.getSelectedItem());
            stored_pro.execute();
            JOptionPane.showMessageDialog(this, "Updated");
            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex);
        }
        UpdateJTable();
    }

    private void addChild() {
        try {
            conn = Main.ConnectDB();
            stored_pro = conn.prepareCall("{call insert_child (?,?,?,?,?,?,?,?)}");
            stored_pro.setString(1, fio.getText());
            stored_pro.setString(2, dateBorn.getText());
            stored_pro.setString(3, address.getText());
            stored_pro.setString(4, phoneDad.getText());
            stored_pro.setString(5, phoneMom.getText());
            stored_pro.setString(6, (String) groupCB.getSelectedItem());
            stored_pro.setString(7, (String) kruzhokCB.getSelectedItem());
            stored_pro.setString(8, (String) nationalityCB.getSelectedItem());
            stored_pro.execute();
            JOptionPane.showMessageDialog(this, "Saved");
            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex);
        }
        UpdateJTable();
    }

    private void deleteChild() {
        try {
            conn = Main.ConnectDB();
            stored_pro = conn.prepareCall("{call delete_child (?)}");
            stored_pro.setString(1, id.getText());
            stored_pro.execute();
            JOptionPane.showMessageDialog(this, "Deleted");
            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex);
        }
        UpdateJTable();
    }

    private void populateFieldsFromTable() {
        try {
            conn = Main.ConnectDB();
            int row = table1.getSelectedRow();
            if (row < 0) {
                return;
            }

            String selectedId = table1.getModel().getValueAt(row, 0).toString();
            String query = "select * from View_children where id = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, selectedId);
            rs = pst.executeQuery();

            while (rs.next()) {
                id.setText(rs.getString("id"));
                fio.setText(rs.getString("FIO"));
                dateBorn.setText(rs.getString("dateborn"));
                address.setText(rs.getString("address"));
                phoneDad.setText(rs.getString("tel_dad"));
                phoneMom.setText(rs.getString("tel_mom"));
                param.setText(rs.getString("id"));
                kruzhokCB.setSelectedItem(rs.getString("kruzhok"));
                groupCB.setSelectedItem(rs.getString("groups"));
                nationalityCB.setSelectedItem(rs.getString("nationality"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex);
        }
    }

    private void loadComboBoxes() {
        try {
            conn = Main.ConnectDB();
            resetComboBox(groupCB);
            resetComboBox(kruzhokCB);
            resetComboBox(nationalityCB);
            open_jComboBox2();
            open_jComboBox3();
            open_jComboBox4();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex);
        }
    }

    private void resetComboBox(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        comboBox.addItem(null);
    }

    private void open_jComboBox2() {
        try {
            conn = Main.ConnectDB();
            String sql = "SELECT * FROM [group]";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                groupCB.addItem(rs.getString("groups"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    private void open_jComboBox3() {
        try {
            conn = Main.ConnectDB();
            String sql = "SELECT * FROM kruzhok";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                kruzhokCB.addItem(rs.getString("kruzhok"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    private void open_jComboBox4() {
        try {
            conn = Main.ConnectDB();
            String sql = "SELECT * FROM nationality";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                nationalityCB.addItem(rs.getString("nationality"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    private void showMeAllReport() {
        conn = Main.ConnectDB();

        try {
            File reportFile = new File("reports/AllChildren.jasper");
            if (!reportFile.isFile()) {
                JOptionPane.showMessageDialog(this, "Файл отчета не найден: reports/AllChildren.jasper");
                return;
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(reportFile.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
        } catch (JRException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e);
        }
    }

    private void showById() {
        try {
            if (param.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Выберите ребенка из таблицы.");
                return;
            }

            conn = Main.ConnectDB();
            File reportFile = new File("reports/ChildID.jasper");
            if (!reportFile.isFile()) {
                JOptionPane.showMessageDialog(this, "Файл отчета не найден: reports/ChildID.jasper");
                return;
            }

            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("param", param.getText().trim());

            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(reportFile.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e);
        }
    }

    private void showVisitChart() {
        try {
            conn = Main.ConnectDB();
            String sql = "SELECT kruzhok, COUNT(*) AS cnt FROM View_children GROUP BY kruzhok ORDER BY kruzhok";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            LinkedHashMap<String, Integer> values = new LinkedHashMap<>();
            while (rs.next()) {
                values.put(rs.getString("kruzhok"), rs.getInt("cnt"));
            }

            if (values.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для диаграммы.");
                return;
            }

            JDialog dialog = new JDialog(this, "График", false);
            dialog.setContentPane(new ChartPanel(values));
            dialog.setSize(650, 550);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e);
        }
    }

    private void UpdateJTable() {
        try {
            conn = Main.ConnectDB();
            String sql = "SELECT [id], [FIO], [dateborn], [address], [tel_dad], [tel_mom], [nationality], [kruzhok], [groups] FROM [View_children]";
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        id.setText("");
        fio.setText("");
        dateBorn.setText("");
        address.setText("");
        phoneDad.setText("");
        phoneMom.setText("");
        param.setText("");
        groupCB.setSelectedItem(null);
        kruzhokCB.setSelectedItem(null);
        nationalityCB.setSelectedItem(null);
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
        gbc.weightx = 1.0;
        panel.add(prepareInput(component), gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return label;
    }

    private JComponent prepareInput(JComponent component) {
        component.setPreferredSize(new Dimension(190, 38));
        if (component instanceof JTextField) {
            ((JTextField) component).setColumns(12);
        }
        return component;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setFocusPainted(false);
        return button;
    }

    private static class ChartPanel extends JPanel {
        private final LinkedHashMap<String, Integer> values;

        private ChartPanel(LinkedHashMap<String, Integer> values) {
            this.values = values;
            setBackground(new Color(245, 245, 245));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int left = 58;
            int right = 18;
            int top = 46;
            int bottom = 78;
            int chartX = left;
            int chartY = top;
            int chartWidth = width - left - right;
            int chartHeight = height - top - bottom;

            g2.setColor(new Color(214, 214, 214));
            g2.fillRect(chartX, chartY, chartWidth, chartHeight);
            g2.setColor(new Color(140, 140, 140));
            g2.drawRect(chartX, chartY, chartWidth, chartHeight);

            String title = "Количество посещений кружков";
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            FontMetrics titleMetrics = g2.getFontMetrics();
            g2.drawString(title, chartX + (chartWidth - titleMetrics.stringWidth(title)) / 2, 28);

            int maxValue = 1;
            for (Integer value : values.values()) {
                maxValue = Math.max(maxValue, value);
            }
            int axisMax = Math.max(5, ((maxValue + 4) / 5) * 5);

            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{2f, 3f}, 0f));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            for (int tick = 0; tick <= axisMax; tick += 5) {
                int y = chartY + chartHeight - (int) Math.round((double) tick / axisMax * chartHeight);
                g2.setColor(new Color(105, 105, 105));
                g2.drawLine(chartX, y, chartX + chartWidth, y);
                g2.setColor(new Color(80, 80, 80));
                String tickLabel = String.valueOf(tick);
                FontMetrics tickMetrics = g2.getFontMetrics();
                g2.drawString(tickLabel, chartX - 8 - tickMetrics.stringWidth(tickLabel), y + 4);
            }
            g2.setStroke(oldStroke);

            g2.setColor(new Color(85, 85, 85));
            g2.drawLine(chartX, chartY, chartX, chartY + chartHeight);
            g2.drawLine(chartX, chartY + chartHeight, chartX + chartWidth, chartY + chartHeight);

            int count = values.size();
            int gap = Math.max(22, chartWidth / Math.max(1, count * 4));
            int barWidth = Math.max(70, (chartWidth - gap * (count + 1)) / Math.max(1, count));
            int x = chartX + gap;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            for (Map.Entry<String, Integer> entry : values.entrySet()) {
                int barHeight = (int) Math.round((double) entry.getValue() / axisMax * (chartHeight - 4));
                int y = chartY + chartHeight - barHeight;

                GradientPaint fill = new GradientPaint(
                        x, y, new Color(255, 205, 205),
                        x + barWidth, y, new Color(255, 92, 92)
                );
                g2.setPaint(fill);
                g2.fillRect(x, y, barWidth, barHeight);

                GradientPaint glare = new GradientPaint(
                        x + barWidth / 10f, y, new Color(255, 255, 255, 220),
                        x + barWidth / 4f, y, new Color(255, 255, 255, 0)
                );
                g2.setPaint(glare);
                g2.fillRect(x, y, Math.max(14, barWidth / 4), barHeight);

                g2.setColor(new Color(225, 88, 88));
                g2.drawRect(x, y, barWidth, barHeight);

                String label = shortenLabel(entry.getKey());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(label);
                int textX = x + (barWidth - textWidth) / 2;
                g2.setColor(new Color(80, 80, 80));
                g2.drawString(label, textX, chartY + chartHeight + 20);

                x += barWidth + gap;
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            String xAxisLabel = "Наименование кружков";
            FontMetrics xAxisMetrics = g2.getFontMetrics();
            g2.setColor(Color.BLACK);
            g2.drawString(xAxisLabel, chartX + (chartWidth - xAxisMetrics.stringWidth(xAxisLabel)) / 2, height - 18);

            g2.translate(18, chartY + chartHeight / 2.0);
            g2.rotate(-Math.PI / 2);
            String yAxisLabel = "Количество посещений в %";
            FontMetrics yAxisMetrics = g2.getFontMetrics();
            g2.drawString(yAxisLabel, -yAxisMetrics.stringWidth(yAxisLabel) / 2, 0);

            g2.dispose();
        }

        private String shortenLabel(String label) {
            if (label == null) {
                return "";
            }
            if (label.length() <= 14) {
                return label;
            }
            return label.substring(0, 12) + "...";
        }
    }
}
