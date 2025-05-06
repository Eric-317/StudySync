package com.studysync.view;

import com.studysync.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class RegisterPanel extends JPanel {
    private final UserController userController = new UserController();
    private final MainWindow mainWindow;

    public RegisterPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 248, 255));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setMaximumSize(new Dimension(400, 450));

        JTextField emailField = new JTextField();
        setFixedSize(emailField, 300, 60);
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setBorder(BorderFactory.createTitledBorder("Email Address"));

        JPasswordField passwordField = new JPasswordField();
        setFixedSize(passwordField, 300, 60);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner birthSpinner = new JSpinner(dateModel);
        birthSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        birthSpinner.setBorder(BorderFactory.createTitledBorder("Birth Date"));
        setFixedSize(birthSpinner, 300, 60);

        JSpinner.DateEditor editor = new JSpinner.DateEditor(birthSpinner, "yyyy-MM-dd");
        JFormattedTextField textField = editor.getTextField();
        textField.setHorizontalAlignment(JTextField.CENTER);
        setFixedSize(textField, 300, 30);
        birthSpinner.setEditor(editor);

        JButton registerBtn = new JButton("Sign up");
        registerBtn.setBackground(new Color(66, 133, 244));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFixedSize(registerBtn, 300, 40);

        JButton backBtn = new JButton("Back to login");
        backBtn.setFocusPainted(false);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setFixedSize(backBtn, 300, 40);

        card.add(emailField);
        card.add(Box.createVerticalStrut(10));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(birthSpinner);
        card.add(Box.createVerticalStrut(20));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(backBtn);

        add(card);

        registerBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            Date birth = (Date) birthSpinner.getValue();
            String birthStr = new SimpleDateFormat("yyyy-MM-dd").format(birth);
            if (userController.register(email, password, birthStr)) {
                JOptionPane.showMessageDialog(this, "註冊成功！請登入");
                mainWindow.showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(this, "註冊失敗或帳號已存在");
            }
        });

        backBtn.addActionListener(e -> mainWindow.showLoginPanel());
    }

    private void setFixedSize(JComponent comp, int width, int height) {
        Dimension d = new Dimension(width, height);
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
    }
}
