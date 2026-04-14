import javax.swing.*;
import java.awt.*;

public class AllChildren extends JFrame {
    public JPanel panelchill;

    public AllChildren() {
        panelchill = buildPanel("Список всех детей");
    }

    private JPanel buildPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
