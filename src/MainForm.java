import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainForm extends JFrame {
    private JButton buttonYasli;
    private JButton buttonMiddle;
    private JButton button1;
    public JPanel panel;
    private JButton buttonSenior;
    private JButton buttonList;
    private JButton buttonEmpl;

    public MainForm() {
        buildUi();

        buttonEmpl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Employees form1 = new Employees();
                form1.setContentPane(form1.jpanel);
                form1.setTitle("Сотрудники");
                form1.setSize(860, 700);
                form1.setLocationRelativeTo(null);
                form1.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                form1.setVisible(true);
            }
        });

        buttonYasli.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KinderG form2 = new KinderG();
                form2.setContentPane(form2.panel);
                form2.setTitle("Ясли");
                form2.setSize(600, 450);
                form2.setLocationRelativeTo(null);
                form2.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                form2.setVisible(true);
            }
        });

        buttonList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AllChildren form3 = new AllChildren();
                form3.setContentPane(form3.panelchill);
                form3.setTitle("Список всех детей");
                form3.setSize(650, 500);
                form3.setLocationRelativeTo(null);
                form3.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                form3.setVisible(true);
            }
        });

        buttonMiddle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MiddleG form4 = new MiddleG();
                form4.setContentPane(form4.panel);
                form4.setTitle("Список средней группы");
                form4.setSize(650, 500);
                form4.setLocationRelativeTo(null);
                form4.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                form4.setVisible(true);
            }
        });

        buttonSenior.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SeniorG form5 = new SeniorG();
                form5.setContentPane(form5.panel);
                form5.setTitle("Список старшей группы");
                form5.setSize(650, 500);
                form5.setLocationRelativeTo(null);
                form5.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                form5.setVisible(true);
            }
        });

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JuniorG form6 = new JuniorG();
                form6.setContentPane(form6.panel);
                form6.setTitle("Список младшей группы");
                form6.setSize(650, 500);
                form6.setLocationRelativeTo(null);
                form6.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                form6.setVisible(true);
            }
        });
    }

    private void buildUi() {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(215, 212, 172));

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        mainPanel.setBackground(new Color(215, 212, 172));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(215, 212, 172));
        leftPanel.setBorder(new EmptyBorder(14, 14, 14, 8));
        leftPanel.add(createLogoPanel(), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(215, 212, 172));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 8, 0, 0, Color.WHITE),
                new EmptyBorder(24, 14, 24, 18)
        ));

        buttonEmpl = createMenuButton("Сотрудники");
        buttonYasli = createMenuButton("Ясли");
        buttonList = createMenuButton("Список всех детей");
        buttonMiddle = createMenuButton("Средняя группа");
        buttonSenior = createMenuButton("Старшая группа");
        button1 = createMenuButton("Младшая группа");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.ipady = 16;

        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(buttonEmpl, gbc);

        gbc.gridx = 1;
        rightPanel.add(buttonYasli, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        rightPanel.add(buttonList, gbc);

        gbc.gridx = 1;
        rightPanel.add(buttonMiddle, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        rightPanel.add(buttonSenior, gbc);

        gbc.gridx = 1;
        rightPanel.add(button1, gbc);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        panel.add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(232, 239, 249));
        button.setBorder(BorderFactory.createLineBorder(new Color(128, 156, 186)));
        return button;
    }

    private JComponent createLogoPanel() {
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setPreferredSize(new Dimension(390, 390));
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        File logoFile = new File("resources/Logo.png");
        if (logoFile.isFile()) {
            ImageIcon logoIcon = new ImageIcon(logoFile.getPath());
            JLabel logoLabel = new JLabel(scaleIcon(logoIcon, 360, 360));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setVerticalAlignment(SwingConstants.CENTER);
            logoPanel.add(logoLabel, BorderLayout.CENTER);
            return logoPanel;
        }

        JLabel fallbackLabel = new JLabel("Logo not found", SwingConstants.CENTER);
        fallbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        fallbackLabel.setForeground(new Color(120, 120, 120));
        logoPanel.add(fallbackLabel, BorderLayout.CENTER);
        return logoPanel;
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
