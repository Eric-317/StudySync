package com.studysync.view;

import com.studysync.model.User;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public MainWindow() {
        setTitle("StudySync 學習平台");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showLoginPanel();
    }

    public void showLoginPanel() {
        setContentPane(new LoginPanel(this));
        revalidate();
    }

    public void showRegisterPanel() {
        setContentPane(new RegisterPanel(this));
        revalidate();
    }

    public void showDashboard(User user) {
        setContentPane(new DashboardPanel(user));
        revalidate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
