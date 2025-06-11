package com.studysync.view;

import com.studysync.util.DBUtil;
import com.studysync.util.DatabaseInitializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseSelectionDialog extends JDialog {
    private JRadioButton sqliteRadio;
    private JRadioButton mysqlRadio;
    private JButton continueButton;
    private JButton cancelButton;
    private boolean configurationComplete = false;
    
    public DatabaseSelectionDialog(JFrame parent) {
        super(parent, "選擇資料庫類型", true);
        initializeComponents();
        setupLayout();        setupEventHandlers();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        sqliteRadio = new JRadioButton("SQLite（推薦 - 零配置）", true);
        mysqlRadio = new JRadioButton("MySQL（需要配置）");
        
        ButtonGroup group = new ButtonGroup();
        group.add(sqliteRadio);
        group.add(mysqlRadio);
        
        continueButton = new JButton("繼續");
        cancelButton = new JButton("取消");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 標題面板
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("請選擇要使用的資料庫類型");
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // 選項面板
        JPanel optionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;
        
        // SQLite 選項
        gbc.gridx = 0; gbc.gridy = 0;
        optionPanel.add(sqliteRadio, gbc);
        
        gbc.gridy = 1;
        JLabel sqliteDesc = new JLabel("• 無需額外配置，資料存儲在本地檔案");
        sqliteDesc.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        sqliteDesc.setForeground(Color.GRAY);
        optionPanel.add(sqliteDesc, gbc);
        
        gbc.gridy = 2;
        JLabel sqliteDesc2 = new JLabel("• 適合個人使用和專案分享");
        sqliteDesc2.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        sqliteDesc2.setForeground(Color.GRAY);
        optionPanel.add(sqliteDesc2, gbc);
        
        // MySQL 選項
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 20, 10, 20);
        optionPanel.add(mysqlRadio, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 20, 10, 20);
        JLabel mysqlDesc = new JLabel("• 需要配置資料庫伺服器資訊");
        mysqlDesc.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        mysqlDesc.setForeground(Color.GRAY);
        optionPanel.add(mysqlDesc, gbc);
        
        gbc.gridy = 5;
        JLabel mysqlDesc2 = new JLabel("• 適合伺服器部署和多用戶環境");
        mysqlDesc2.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        mysqlDesc2.setForeground(Color.GRAY);
        optionPanel.add(mysqlDesc2, gbc);
        
        add(optionPanel, BorderLayout.CENTER);
        
        // 按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(continueButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sqliteRadio.isSelected()) {
                    // 使用 SQLite
                    try {
                        DBUtil.useSQLite();
                        DatabaseInitializer.createTables();
                        configurationComplete = true;
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                            DatabaseSelectionDialog.this,
                            "SQLite 初始化失敗。可能是缺少 SQLite JDBC 驅動。\n\n" +
                            "請下載並安裝:\n" +
                            "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar\n\n" +
                            "錯誤詳情: " + ex.getMessage(),
                            "SQLite 配置失敗",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } else if (mysqlRadio.isSelected()) {
                    // 配置 MySQL
                    showMySQLConfig();
                }
            }
        });
        
        cancelButton.addActionListener(e -> {
            configurationComplete = false;
            dispose();
        });
    }
    
    private void showMySQLConfig() {
        DatabaseConfigDialog configDialog = new DatabaseConfigDialog((JFrame)getParent());
        configDialog.setVisible(true);
        
        if (configDialog.isConfigurationSuccessful()) {
            DBUtil.configureMysql(
                configDialog.getDatabaseUrl(),
                configDialog.getDatabaseUsername(),
                configDialog.getDatabasePassword()
            );
            
            // 創建 MySQL 資料表
            DatabaseInitializer.createTables();
            configurationComplete = true;
            dispose();
        }
    }
    
    public boolean isConfigurationComplete() {
        return configurationComplete;
    }
}
