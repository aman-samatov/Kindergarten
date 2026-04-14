import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class Authorization extends JFrame {
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton войтиButton;
    private JButton закрытьButton;
    public JPanel jPanel;
    Connection conn = null;
    ResultSet rs = null;
    CallableStatement stored_pro = null;

    public Authorization() {
        buildUi();

        войтиButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conn = Main.ConnectDB();
                    stored_pro = conn.prepareCall("{call select_login(?, ?)}");
                    stored_pro.setString(1, textField1.getText());
                    stored_pro.setString(2, passwordField1.getText());
                    rs = stored_pro.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Correct user name and password");
                        rs.close();
                        stored_pro.close();
                        conn.close();
                        MainForm form = new MainForm();
                        form.setVisible(true);
                        form.setContentPane(form.panel);
                        form.setTitle("Kindergarten");
                        form.setSize(930, 500);
                        form.setLocationRelativeTo(null);
                        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(null, "Wrong user name or password");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        });

        закрытьButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void buildUi() {
        jPanel = new JPanel(new BorderLayout(20, 20));
        jPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        jPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Авторизация", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        jPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        contentPanel.add(createIllustrationPanel());

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel loginLabel = new JLabel("Логин");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        textField1 = new JTextField();
        textField1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        textField1.setPreferredSize(new Dimension(220, 34));

        JLabel passwordLabel = new JLabel("Пароль");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passwordField1 = new JPasswordField();
        passwordField1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        passwordField1.setPreferredSize(new Dimension(220, 34));

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        войтиButton = new JButton("Войти");
        закрытьButton = new JButton("Закрыть");
        buttonsPanel.add(войтиButton);
        buttonsPanel.add(закрытьButton);

        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(loginLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(textField1);
        formPanel.add(Box.createVerticalStrut(28));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passwordField1);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(buttonsPanel);
        formPanel.add(Box.createVerticalGlue());

        contentPanel.add(formPanel);
        jPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JComponent createIllustrationPanel() {
        JPanel illustrationPanel = new JPanel(new BorderLayout());
        illustrationPanel.setPreferredSize(new Dimension(260, 230));
        illustrationPanel.setOpaque(true);
        illustrationPanel.setBackground(Color.WHITE);
        illustrationPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        File imageFile = new File("resources/Auth.png");
        if (imageFile.isFile()) {
            ImageIcon imageIcon = new ImageIcon(imageFile.getPath());
            JLabel imageLabel = new JLabel(scaleIcon(imageIcon, 420, 320));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setVerticalAlignment(SwingConstants.CENTER);
            illustrationPanel.add(imageLabel, BorderLayout.CENTER);
            return illustrationPanel;
        }

        JLabel fallbackLabel = new JLabel("Authorization", SwingConstants.CENTER);
        fallbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        fallbackLabel.setForeground(new Color(120, 120, 120));
        illustrationPanel.add(fallbackLabel, BorderLayout.CENTER);
        return illustrationPanel;
    }

    private ImageIcon scaleIcon(ImageIcon icon, int maxWidth, int maxHeight) {
        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();
        if (iconWidth <= 0 || iconHeight <= 0) {
            return icon;
        }

        double scale = Math.min((double) maxWidth / iconWidth, (double) maxHeight / iconHeight);
        int width = Math.max(1, (int) Math.round(iconWidth * scale));
        int height = Math.max(1, (int) Math.round(iconHeight * scale));
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}
