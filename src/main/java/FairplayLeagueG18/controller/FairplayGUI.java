package FairplayLeagueG18.controller;

import FairplayLeagueG18.service.CouponService;
import FairplayLeagueG18.service.RoundService;
import FairplayLeagueG18.service.UserService;
import FairplayLeagueG18.database.DatabaseManager;
import FairplayLeagueG18.model.Round;
import FairplayLeagueG18.model.RoundStatus;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FairplayGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    // skapar huvudfönstret
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Fairplay League - Adminpanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        // lägger till flikarna
        tabbedPane.addTab("Leta Omgång", createMatchPanel());
        tabbedPane.addTab("Registrera", createRegisterPanel());
        tabbedPane.addTab("Logga in", createLoginPanel());
        tabbedPane.addTab("Lägg Tip", createBettingPanel());

        frame.add(tabbedPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // skapar matchfliken
    private static JPanel createMatchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // topp-panel för sökning
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JTextField roundInput = new JTextField(5);
        roundInput.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton fetchButton = new JButton("Hämta Omgång");
        fetchButton.setFont(new Font("SansSerif", Font.BOLD, 12));

        topPanel.add(new JLabel("Skriv in omgång (1-30): "));
        topPanel.add(roundInput);
        topPanel.add(fetchButton);

        // skapar tabellmodell
        String[] columnNames = {"Hemmalag", "Bortalag", "Resultat"};
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // skapar tabell
        JTable matchTable = new JTable(tableModel);
        matchTable.setRowHeight(30);
        matchTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        matchTable.getTableHeader().setBackground(new Color(220, 220, 220));
        matchTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        matchTable.setShowGrid(true);
        matchTable.setGridColor(new Color(200, 200, 200));

        // centrerar text
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < matchTable.getColumnCount(); i++) {
            matchTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(matchTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(null, "Matchkupong",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 12)));

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);

        // action listener för att hämta matcher
        fetchButton.addActionListener(e -> {
            try {
                int roundId = Integer.parseInt(roundInput.getText());
                String sql = "SELECT Home_team, Away_team, Actual_result FROM Matches WHERE Gameweek_ID = ?";

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, roundId);
                    ResultSet rs = stmt.executeQuery();

                    tableModel.setRowCount(0);
                    statusLabel.setText(" ");

                    int count = 0;
                    while (rs.next()) {
                        String home = rs.getString("Home_team");
                        String away = rs.getString("Away_team");
                        String res = rs.getString("Actual_result");

                        tableModel.addRow(new Object[]{home, away, (res != null ? res : "-")});
                        count++;
                    }

                    if (count == 0) {
                        statusLabel.setText("Hittade inga matcher för omgång " + roundId + ".");
                    }

                } catch (Exception ex) {
                    statusLabel.setText("Databasfel: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Omgången måste vara en siffra.");
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

        // action listener för registrering
        registerButton.addActionListener(e -> {
            String user = userField.getText();
            String email = emailField.getText();
            String pass = new String(passField.getPassword());

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                feedbackArea.setText("Fyll i alla fält!");
                return;
            }

            userField.setText("");
            emailField.setText("");
            passField.setText("");
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

        // action listener för inloggning
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

    // skapar flik för kuponginlämning
    private static JPanel createBettingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // topp-panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JTextField roundInput = new JTextField(5);
        JButton fetchButton = new JButton("Hämta Kupong");
        fetchButton.setOpaque(true);
        fetchButton.setBorderPainted(false);
        fetchButton.setBackground(new Color(34, 139, 34));
        fetchButton.setForeground(Color.WHITE);

        topPanel.add(new JLabel("Välj omgång (1-30): "));
        topPanel.add(roundInput);
        topPanel.add(fetchButton);

        // mitten-panel för listan av matcher
        JPanel matchesContainer = new JPanel();
        matchesContainer.setLayout(new BoxLayout(matchesContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(matchesContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // botten-panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton submitButton = new JButton("Lämna in Kupong");
        submitButton.setPreferredSize(new Dimension(200, 40));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(submitButton, BorderLayout.SOUTH);

        submitButton.setEnabled(false);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // lista för knappgrupper
        java.util.List<ButtonGroup> buttonGroups = new java.util.ArrayList<>();

        // En lista för att hålla reda på vilka Match-ID som hör till varje knappgrupp
        java.util.List<Integer> matchIds = new java.util.ArrayList<>();

        // action listener för att hämta kupong
        fetchButton.addActionListener(e -> {
            try {
                int roundId = Integer.parseInt(roundInput.getText());

                // kollar status på omgången
                FairplayLeagueG18.service.RoundService roundService = new FairplayLeagueG18.service.RoundService();
                FairplayLeagueG18.model.Round round = roundService.getRound(roundId);

                if (round == null) {
                    statusLabel.setText("Hittade ingen omgång med det numret.");
                    return;
                }

                boolean isRoundOpen = (round.getStatus() == FairplayLeagueG18.model.RoundStatus.Open);

                if (!isRoundOpen) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Omgång " + roundId + " är stängd för inlämning!");
                } else {
                    statusLabel.setText(" ");
                }

                // Vi hämtar även Match_ID i SQL-frågan
                String sql = "SELECT Match_ID, Home_team, Away_team FROM Matches WHERE Gameweek_ID = ?";

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, roundId);
                    ResultSet rs = stmt.executeQuery();

                    matchesContainer.removeAll();
                    buttonGroups.clear();
                    matchIds.clear(); // Rensa gamla ID:n

                    int count = 0;
                    while (rs.next()) {
                        // Spara match_id
                        int matchId = rs.getInt("Match_ID");
                        matchIds.add(matchId);

                        String home = rs.getString("Home_team");
                        String away = rs.getString("Away_team");

                        // skapar rad
                        JPanel matchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
                        matchRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                        JLabel matchLabel = new JLabel((count + 1) + ". " + home + " - " + away);
                        matchLabel.setPreferredSize(new Dimension(250, 30));
                        matchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

                        // skapar radioknappar
                        JRadioButton btn1 = new JRadioButton("1");
                        JRadioButton btnX = new JRadioButton("X");
                        JRadioButton btn2 = new JRadioButton("2");

                        btn1.setEnabled(isRoundOpen);
                        btnX.setEnabled(isRoundOpen);
                        btn2.setEnabled(isRoundOpen);

                        ButtonGroup group = new ButtonGroup();
                        group.add(btn1);
                        group.add(btnX);
                        group.add(btn2);
                        buttonGroups.add(group);

                        btn1.setActionCommand("1");
                        btnX.setActionCommand("X");
                        btn2.setActionCommand("2");

                        matchRow.add(matchLabel);
                        matchRow.add(btn1);
                        matchRow.add(btnX);
                        matchRow.add(btn2);

                        matchesContainer.add(matchRow);
                        count++;
                    }

                    if (count > 0 && isRoundOpen) {
                        submitButton.setEnabled(true);
                    } else {
                        submitButton.setEnabled(false);
                    }

                    matchesContainer.revalidate();
                    matchesContainer.repaint();

                } catch (Exception ex) {
                    statusLabel.setText("Databasfel: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Måste vara en siffra.");
            }
        });

        // action listener för inlämning
        submitButton.addActionListener(e -> {
            java.util.Map<Integer, String> tips = new java.util.HashMap<>();

            for (int i = 0; i < buttonGroups.size(); i++) {
                ButtonGroup group = buttonGroups.get(i);
                ButtonModel selected = group.getSelection();

                if (selected != null) {
                    // Istället för loop-index 'i', hämtar vi det riktiga matchId:t från vår lista
                    int actualMatchId = matchIds.get(i);
                    tips.put(actualMatchId, selected.getActionCommand());
                }
            }

            if (tips.size() != 8) {
                statusLabel.setText("Du måste tippa alla 8 matcher innan du lämnar in!");
                return;
            }

            try {
                int roundId = Integer.parseInt(roundInput.getText());
                int currentUserId = 1;

                FairplayLeagueG18.service.CouponService couponService = new FairplayLeagueG18.service.CouponService();
                couponService.submitCoupon(currentUserId, roundId, tips);

                statusLabel.setForeground(new Color(34, 139, 34));
                statusLabel.setText("Kupong inlämnad! Lycka till.");
                submitButton.setEnabled(false);

            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Något gick fel vid inlämning.");
            }
        });

        return panel;
    }
}