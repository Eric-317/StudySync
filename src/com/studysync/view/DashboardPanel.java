package com.studysync.view;

import com.studysync.model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final User user;

    public DashboardPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getEmail() + "!");
        welcomeLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));

        add(welcomeLabel, BorderLayout.NORTH);
        add(new TaskPanel(), BorderLayout.CENTER);
    }
}
