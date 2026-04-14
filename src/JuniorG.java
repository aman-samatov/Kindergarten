import javax.swing.*;
import java.awt.*;

public class JuniorG extends JFrame {
    public JPanel panel;

    public JuniorG() {
        panel = buildPanel("Список младшей группы");
    }

    private JPanel buildPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
