import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {

    public static Connection ConnectDB() {
        Connection conn = null;

        try {
            String dbURL = "jdbc:sqlserver://localhost:1488;databaseName=shop_netbeans;username=sa;password=123;Encrypt=True;TrustServerCertificate=True";
            String user = "sa";
            String pass = "123";
            conn = DriverManager.getConnection(dbURL, user, pass);
            return conn;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
            return null;
        }
    }

    public static void main(String[] args) {
        ConnectDB();
        Authorization form1 = new Authorization();
        form1.setContentPane(form1.jPanel);
        form1.setTitle("Авторизация");
        form1.setSize(780, 560);
        form1.setLocationRelativeTo(null);
        form1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        form1.setVisible(true);
    }
}
