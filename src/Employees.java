import javax.swing.*;
import java.awt.*;

public class Employees extends JFrame {
    public JPanel jpanel;

    public Employees() {
        jpanel = buildPanel("Сотрудники");
    }

    private JPanel buildPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
