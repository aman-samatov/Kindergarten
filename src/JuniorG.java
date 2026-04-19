import net.proteanit.sql.DbUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class JuniorG extends JFrame {
    public JPanel panel;
    private JTable table1;

    Connection conn = null;
    Statement statement = null;
    ResultSet rs = null;

    public JuniorG() {
        buildUi("Список младшей группы");
        UpdateJTable();
    }

    private void buildUi(String title) {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(242, 242, 242));
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 8));
        contentPanel.setBackground(panel.getBackground());
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        table1 = new JTable();
        table1.setRowHeight(26);
        table1.setFillsViewportHeight(true);
        table1.setGridColor(new Color(200, 200, 200));
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table1.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(table1);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);
    }

    private void UpdateJTable() {
        try {
            conn = Main.ConnectDB();
            String sql = "SELECT * FROM [View_id3]";
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
