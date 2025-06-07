package com.studysync.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.RenderingHints; // Added for anti-aliasing
import java.awt.BasicStroke;  // Added for thicker stroke
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import com.studysync.util.AppSettings; // 新增 import

public class PomodoroPanel extends JPanel {
    private int initialMinutes = 25; // Default initial minutes
    private int initialSeconds = 0;  // Default initial seconds
    private static final int CIRCLE_DIAMETER = 200; // Fixed diameter for the circle
    private int secondsLeft = initialMinutes * 60 + initialSeconds;
    private boolean running = false;
    private Timer timer;
    private final JLabel timerLabel = new JLabel();
    private final JButton pauseBtn = new JButton("Pause");
    private final JButton resetBtn = new JButton("Reset");
    private JButton setVideoButton; // 新增按鈕

    public PomodoroPanel() {
        try { // Added try for the entire constructor
            setLayout(new BorderLayout());
            setBackground(new Color(250, 252, 255));
            setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

            JLabel title = new JLabel("Pomodoro Timer", SwingConstants.CENTER);
            title.setFont(new Font("Dialog", Font.BOLD, 32));
            title.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
            add(title, BorderLayout.NORTH);

            // Changed centerPanel to use GridBagLayout for precise centering of timerLabel
            JPanel centerPanel = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create(); // Create a copy for local settings
                    try {
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int diameter = CIRCLE_DIAMETER;
                        int x = (getWidth() - diameter) / 2;
                        int y = (getHeight() - diameter) / 2;
                        g2d.setColor(new Color(200, 220, 255));
                        g2d.setStroke(new BasicStroke(3f)); // Set line thickness
                        g2d.drawOval(x, y, diameter, diameter);
                    } catch (Exception e) { // Added catch for paintComponent drawing operations
                        System.err.println("Error during custom painting in PomodoroPanel's centerPanel: " + e.getMessage());
                        e.printStackTrace();
                        // Optionally, draw an error indicator or message if g2d is still usable
                        // For example: g.setColor(Color.RED); g.drawString("Paint Error", 10, 10);
                    }
                    finally {
                        g2d.dispose(); // Dispose the graphics copy
                    }
                }
            };
            centerPanel.setOpaque(false);
            // Preferred and Minimum size can be removed if we want centerPanel to fill BorderLayout.CENTER space,
            // the circle drawing is relative to centerPanel's current size.
            // centerPanel.setPreferredSize(new Dimension(CIRCLE_DIAMETER + 40, CIRCLE_DIAMETER + 40));
            // centerPanel.setMinimumSize(new Dimension(CIRCLE_DIAMETER + 40, CIRCLE_DIAMETER + 40));

            timerLabel.setFont(new Font("Dialog", Font.BOLD, 48));
            timerLabel.setForeground(new Color(30, 30, 30));
            updateTimerLabel();

            // Add timerLabel to centerPanel using GridBagConstraints for centering
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0, 5, 0, 0); // Top, Left, Bottom, Right insets (increased left inset)
            centerPanel.add(timerLabel, gbc);

            timerLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try { // Added outer try for the entire mouseClicked logic
                        if (!running) { // Only allow setting time if timer is not running
                            // Custom panel for minutes and seconds input
                            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                            JTextField minutesField = new JTextField(String.valueOf(initialMinutes), 3);
                            JTextField secondsField = new JTextField(String.valueOf(initialSeconds), 3);

                            JLabel minutesLabel = new JLabel("分鐘:");
                            minutesLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 16)); // 設定字型
                            inputPanel.add(minutesLabel);
                            inputPanel.add(minutesField);

                            JLabel secondsLabel = new JLabel("秒鐘:");
                            secondsLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 16)); // 設定字型
                            inputPanel.add(secondsLabel);
                            inputPanel.add(secondsField);

                            int result = JOptionPane.showConfirmDialog(
                                    PomodoroPanel.this,
                                    inputPanel,
                                    "設定番茄鐘時間",
                                    JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.PLAIN_MESSAGE
                            );

                            if (result == JOptionPane.OK_OPTION) {
                                try { // Inner try for number parsing remains
                                    int newMinutes = Integer.parseInt(minutesField.getText().trim());
                                    int newSeconds = Integer.parseInt(secondsField.getText().trim());

                                    if (newMinutes >= 0 && newMinutes <= 180 && newSeconds >= 0 && newSeconds <= 59) { // Basic validation
                                        if (newMinutes == 0 && newSeconds == 0) {
                                            JOptionPane.showMessageDialog(
                                                    PomodoroPanel.this,
                                                    "總時間不能為0。",
                                                    "輸入錯誤",
                                                    JOptionPane.ERROR_MESSAGE
                                            );
                                            return;
                                        }
                                        initialMinutes = newMinutes;
                                        initialSeconds = newSeconds;
                                        secondsLeft = initialMinutes * 60 + initialSeconds;
                                        updateTimerLabel();
                                        // Ensure timer is reset and button states are correct
                                        running = false;
                                        pauseBtn.setText("Start");
                                        if (timer != null && timer.isRunning()) {
                                            timer.stop();
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(
                                                PomodoroPanel.this,
                                                "請輸入有效的分鐘數 (0-180) 和秒數 (0-59)。",
                                                "輸入錯誤",
                                                JOptionPane.ERROR_MESSAGE
                                        );
                                    }
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(
                                            PomodoroPanel.this,
                                            "請輸入有效的數字。",
                                            "輸入錯誤",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                } // Removed the general Exception catch here to avoid catching exceptions from JOptionPane itself if !running is false.
                            }
                        } else {
                            JOptionPane.showMessageDialog(
                                    PomodoroPanel.this,
                                    "請先暫停番茄鐘才能設定時間。",
                                    "提示",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        }
                    } catch (Exception ex) { // Added outer catch for general exceptions
                        JOptionPane.showMessageDialog(
                                PomodoroPanel.this,
                                "處理點擊設定時間時發生未預期錯誤: " + ex.getMessage(),
                                "錯誤",
                                JOptionPane.ERROR_MESSAGE
                        );
                        ex.printStackTrace();
                    }
                }
            });

            // Removed focusLabel related code
            // centerPanel.add(Box.createVerticalGlue()); // No longer needed with GridBagLayout for centering

            add(centerPanel, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 32, 10)); // Added small vertical gap for btnPanel
            btnPanel.setOpaque(false);
            pauseBtn.setFont(new Font("Dialog", Font.BOLD, 20));
            pauseBtn.setForeground(new Color(66, 135, 245));
            pauseBtn.setBackground(Color.WHITE);
            pauseBtn.setFocusPainted(false);
            pauseBtn.setPreferredSize(new Dimension(140, 48));
            pauseBtn.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 2));
            resetBtn.setFont(new Font("Dialog", Font.BOLD, 20));
            resetBtn.setForeground(new Color(66, 135, 245));
            resetBtn.setBackground(Color.WHITE);
            resetBtn.setFocusPainted(false);
            resetBtn.setPreferredSize(new Dimension(140, 48));
            resetBtn.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 2));
            btnPanel.add(pauseBtn);
            btnPanel.add(resetBtn);

            // 新增設定影片按鈕
            setVideoButton = new JButton("設定影片");
            setVideoButton.setFont(new Font("微軟正黑體", Font.BOLD, 20)); // 修改字型為微軟正黑體
            setVideoButton.setForeground(new Color(66, 135, 245));
            setVideoButton.setBackground(Color.WHITE);
            setVideoButton.setPreferredSize(new Dimension(140, 48));
            setVideoButton.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 2)); // 修改邊框
            setVideoButton.setFocusPainted(false);
            btnPanel.add(setVideoButton);

            add(btnPanel, BorderLayout.SOUTH);

            timer = new Timer(1000, ev -> { // Renamed ActionEvent to ev to avoid conflict
                try { // Added try for the timer's action
                    if (running && secondsLeft > 0) {
                        secondsLeft--;
                        updateTimerLabel();
                        if (secondsLeft == 0) {
                            running = false;
                            pauseBtn.setText("Start");
                            playCompletionVideo(); // Play video first
                            JOptionPane.showMessageDialog(PomodoroPanel.this, "時間到！休息一下吧！"); // Show message after video
                        }
                    }
                } catch (Exception ex) { // Added catch for general exceptions in timer action
                    JOptionPane.showMessageDialog(
                        PomodoroPanel.this,
                        "計時器執行時發生未預期錯誤: " + ex.getMessage(),
                        "錯誤",
                        JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                    // Gracefully stop the timer and update UI
                    if (timer != null) timer.stop();
                    running = false;
                    pauseBtn.setText("Start");
                    updateTimerLabel(); // Reflect the stopped state
                }
            });
            // timer.start(); // REMOVED: Do not start timer immediately
            running = false; // Timer is not running initially
            pauseBtn.setText("Start"); // Initial button text is "Start"

            pauseBtn.addActionListener(e -> {
                try { // Added try for the pause button's action
                    if (running) { // If timer is running, pause it
                        running = false;
                        pauseBtn.setText("Start");
                    } else { // If timer is paused or not started, start/resume it
                        running = true;
                        if (!timer.isRunning()) { // Start the timer if it hasn't been started yet
                            timer.start();
                        }
                        pauseBtn.setText("Pause");
                    }
                } catch (Exception ex) { // Added catch for general exceptions in pause button action
                    JOptionPane.showMessageDialog(
                        PomodoroPanel.this,
                        "處理開始/暫停按鈕時發生未預期錯誤: " + ex.getMessage(),
                        "錯誤",
                        JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                }
            });

            resetBtn.addActionListener(ev -> { // Renamed ActionEvent to ev
                try { // Added try for the reset button's action
                    secondsLeft = initialMinutes * 60 + initialSeconds; // Use the potentially updated initialMinutes and initialSeconds
                    updateTimerLabel();
                    running = false; // Stop running
                    pauseBtn.setText("Start"); // Reset button text
                    if (timer != null && timer.isRunning()) { // Stop the timer if it's running
                        timer.stop();
                    }
                } catch (Exception ex) { // Added catch for general exceptions in reset button action
                    JOptionPane.showMessageDialog(
                        PomodoroPanel.this,
                        "處理重設按鈕時發生未預期錯誤: " + ex.getMessage(),
                        "錯誤",
                        JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                }
            });

            // 設定影片按鈕的動作
            setVideoButton.addActionListener(e -> choosePomodoroVideo());

        } catch (Exception ex) { // Added catch for the entire constructor
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                null, // Parent might not be available or fully initialized
                "初始化番茄鐘面板時發生嚴重錯誤: " + ex.getMessage(),
                "嚴重錯誤",
                JOptionPane.ERROR_MESSAGE
            );
            // Fallback UI in case of critical initialization error
            removeAll();
            setLayout(new BorderLayout()); // Ensure layout manager for fallback
            add(new JLabel("面板載入失敗，請檢查日誌: " + ex.getMessage()), BorderLayout.CENTER);
            // No revalidate/repaint needed here as it's during construction.
        }
    }

    private void updateTimerLabel() {
        int min = secondsLeft / 60;
        int sec = secondsLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", min, sec));
    }

    private void playCompletionVideo() {
        try {
            String videoPath = AppSettings.getInstance().getPomodoroVideoPath();
            if (videoPath == null || videoPath.trim().isEmpty()) {
                // 如果沒有設定影片路徑，可以選擇不播放或提示使用者設定
                // JOptionPane.showMessageDialog(this, "未設定番茄鐘完成影片。", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            File videoFile = new File(videoPath);
            if (videoFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(videoFile);
                } else {
                    JOptionPane.showMessageDialog(this, "桌面環境不支援開啟檔案。", "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "影片檔案不存在: " + videoFile.getAbsolutePath(), "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "無法播放影片: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { // 捕捉其他可能的例外
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "播放影片時發生未知錯誤: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void choosePomodoroVideo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("選擇番茄鐘完成時播放的影片");
        // 可以設定檔案過濾器，只顯示影片檔案
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String name = f.getName().toLowerCase();
                // 根據需要支援的影片格式調整
                return name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".mov") || name.endsWith(".mkv");
            }

            @Override
            public String getDescription() {
                return "影片檔案 (*.mp4, *.avi, *.mov, *.mkv)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            AppSettings.getInstance().setPomodoroVideoPath(selectedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "番茄鐘影片已設定為: " + selectedFile.getName(), "設定成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
