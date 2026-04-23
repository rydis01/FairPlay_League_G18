package FairplayLeagueG18.controller;

import FairplayLeagueG18.service.UserService;
import FairplayLeagueG18.database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FairplayGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Fairplay League - Adminpanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 550);

        JTabbedPane tabbedPane = new JTabbedPane();

        // lägger till flikarna
        tabbedPane.addTab("Leta Omgång", createMatchPanel());
        tabbedPane.addTab("Registrera", createRegisterPanel());
        tabbedPane.addTab("Logga in", createLoginPanel());

        frame.add(tabbedPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // skapar matchfliken
    private static JPanel createMatchPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        JTextField roundInput = new JTextField(5);
        JButton fetchButton = new JButton("Hämta Omgång");

        topPanel.add(new JLabel("Skriv in omgång (1-30): "));
        topPanel.add(roundInput);
        topPanel.add(fetchButton);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        fetchButton.addActionListener(e -> {
            try {
                int roundId = Integer.parseInt(roundInput.getText());
                String sql = "SELECT Home_team, Away_team, Actual_result FROM Matches WHERE Gameweek_ID = ?";

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, roundId);
                    ResultSet rs = stmt.executeQuery();

                    StringBuilder sb = new StringBuilder("====== OMGÅNG ").append(roundId).append(" ======\n\n");
                    int count = 0;

                    while (rs.next()) {
                        String res = rs.getString("Actual_result");
                        sb.append(rs.getString("Home_team")).append(" vs ").append(rs.getString("Away_team"))
                                .append("\nResultat: ").append(res != null ? res : "Ej spelad").append("\n-------------------------\n");
                        count++;
                    }

                    resultArea.setText(count > 0 ? sb.toString() : "Hittade inga matcher för omgång " + roundId + ".");
                } catch (Exception ex) {
                    resultArea.setText("Databasfel: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                resultArea.setText("Måste vara en siffra.");
            }
        });

        return panel;
    }

    // skapar registreringsfliken
    private static JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JTextField userField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton registerButton = new JButton("Registrera");

        formPanel.add(new JLabel("Användarnamn:"));
        formPanel.add(userField);
        formPanel.add(new JLabel("E-post:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Lösenord (Minst 8 tecken):"));
        formPanel.add(passField);
        formPanel.add(new JLabel(""));
        formPanel.add(registerButton);

        JTextArea feedbackArea = new JTextArea();
        feedbackArea.setEditable(false);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setBackground(new Color(240, 240, 240));
        feedbackArea.setBorder(BorderFactory.createTitledBorder("Systemmeddelande"));

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(feedbackArea, BorderLayout.CENTER);

        registerButton.addActionListener(e -> {
            String user = userField.getText();
            String email = emailField.getText();
            String pass = new String(passField.getPassword());

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                feedbackArea.setText("Fyll i alla fält!");
                return;
            }

            //String result = new UserService().registerUser(user, email, pass);
            //feedbackArea.setText(result);

            //if (result.startsWith("GODKÄNT")) {
                userField.setText("");
                emailField.setText("");
                passField.setText("");
            //}
        });

        return panel;
    }

    // skapar inloggningsfliken
    private static JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Logga in");

        formPanel.add(new JLabel("E-post:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Lösenord:"));
        formPanel.add(passField);
        formPanel.add(new JLabel(""));
        formPanel.add(loginButton);

        JTextArea feedbackArea = new JTextArea();
        feedbackArea.setEditable(false);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setBackground(new Color(240, 240, 240));
        feedbackArea.setBorder(BorderFactory.createTitledBorder("Systemmeddelande"));

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(feedbackArea, BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passField.getPassword());

            if (email.isEmpty() || pass.isEmpty()) {
                feedbackArea.setText("Fyll i e-post och lösenord!");
                return;
            }

            boolean success = new UserService().loginUser(email, pass);

            if (success) {
                feedbackArea.setText("Inloggning lyckades! Välkommen in.");
                emailField.setText("");
                passField.setText("");
            } else {
                feedbackArea.setText("Inloggning misslyckades. Fel e-post eller lösenord.");
            }
        });

        return panel;
    }
}