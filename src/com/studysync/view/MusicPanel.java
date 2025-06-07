package com.studysync.view;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream; // 新增 import
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays; // Add for Arrays.asList

import com.studysync.util.AppSettings; // Import AppSettings

public class MusicPanel extends JPanel {
    private List<String> musicFiles;
    private int currentIndex = 0;
    private Clip audioClip;
    private FloatControl volumeControl;
    private boolean isPlaying = false;    // UI components
    private JLabel titleLabel;
    private JLabel currentMusicLabel;
    private JButton playPauseButton;
    private JButton prevButton;
    private JButton nextButton;
    private JButton musicListButton;
    private JSlider volumeSlider;
    private JLabel volumeLabel;
    
    private Timer marqueeTimer;
    private String fullTitle;
    private int marqueeIndex = 0;
    
    public MusicPanel() {
        initMusicList();
        initUI();
        // loadMusic(musicFiles.get(currentIndex)); // Removed: Load only if not empty
        updatePlaybackControlsState(); // Set initial state of controls
    }
    
    private void initMusicList() {
        musicFiles = new ArrayList<>();
        // Load custom music paths from AppSettings
        String savedPaths = AppSettings.getInstance().getCustomMusicPaths();
        if (savedPaths != null && !savedPaths.isEmpty()) {
            musicFiles.addAll(Arrays.asList(savedPaths.split(";")));
        }
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 252, 255));
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        // 標題
        titleLabel = new JLabel("音樂播放器", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 32));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // 中央面板，包含當前播放的音樂名稱和專輯圖
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        
        // 音樂名稱標籤
        currentMusicLabel = new JLabel("請新增音樂", SwingConstants.CENTER); // Default text
        currentMusicLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 20));
        currentMusicLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
          // 播放狀態視覺化面板（模擬唱片播放效果）
        JPanel musicVisualPanel = new JPanel() {
            private Timer animationTimer;
            private double rotation = 0;
            
            {
                // 建立動畫計時器
                animationTimer = new Timer(40, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 只有在播放狀態時才旋轉
                        if (isPlaying) {
                            rotation += 0.02;
                            if (rotation > 2 * Math.PI) {
                                rotation = 0;
                            }
                            repaint();
                        }
                    }
                });
                animationTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                try {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // 縮小唱片尺寸，只用面板的一部分
                    int diameter = Math.min(getWidth(), getHeight()) - 80;
                    int x = (getWidth() - diameter) / 2;
                    int y = (getHeight() - diameter) / 2;

                    // 設定繪圖的中心點
                    g2d.translate(getWidth() / 2, getHeight() / 2);

                    // 如果是播放狀態，進行旋轉
                    if (isPlaying) {
                        g2d.rotate(rotation);
                    }

                    // 將繪圖點移回左上角
                    g2d.translate(-getWidth() / 2, -getHeight() / 2);

                    // 繪製唱片外圈
                    g2d.setColor(new Color(50, 50, 50));
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.fillOval(x, y, diameter, diameter);

                    // 繪製唱片標籤區域
                    g2d.setColor(new Color(100, 150, 255));
                    int innerDiameter = (int)(diameter * 0.35);
                    int innerX = x + (diameter - innerDiameter) / 2;
                    int innerY = y + (diameter - innerDiameter) / 2;
                    g2d.fillOval(innerX, innerY, innerDiameter, innerDiameter);

                    // 繪製唱片環紋
                    g2d.setColor(new Color(30, 30, 30));
                    for (int i = 1; i <= 7; i++) {
                        int ringDiameter = (int)(diameter * (0.4 + i * 0.07));
                        int ringX = x + (diameter - ringDiameter) / 2;
                        int ringY = y + (diameter - ringDiameter) / 2;
                        g2d.drawOval(ringX, ringY, ringDiameter, ringDiameter);
                    }

                    // 繪製中心點
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(getWidth() / 2 - 5, getHeight() / 2 - 5, 10, 10);

                } finally {
                    g2d.dispose();
                }
            }
            
            @Override
            public void removeNotify() {
                super.removeNotify();
                // 停止動畫計時器以釋放資源
                if (animationTimer != null) {
                    animationTimer.stop();
                    animationTimer = null;
                }
            }
        };
        musicVisualPanel.setPreferredSize(new Dimension(220, 220));
          centerPanel.add(currentMusicLabel, BorderLayout.NORTH);
        centerPanel.add(musicVisualPanel, BorderLayout.CENTER);        // 控制面板
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // 調整邊距        // 播放控制按鈕
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10)); // 改用GridLayout，2行3列佈局
        buttonPanel.setOpaque(false);
        
        prevButton = createControlButton("上一首");
        playPauseButton = createControlButton("播放");
        nextButton = createControlButton("下一首");
        musicListButton = createControlButton("音樂列表");
        
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playPrevious();
            }
        });
        
        playPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePlayPause();
            }
        });
        
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playNext();
            }
        });
        
        musicListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMusicListDialog();
            }
        });
        
        // 第一行放三個控制按鈕
        buttonPanel.add(prevButton);
        buttonPanel.add(playPauseButton);
        buttonPanel.add(nextButton);
        
        // 第二行只在中間放音樂列表按鈕
        buttonPanel.add(new JLabel("")); // 左側空白
        buttonPanel.add(musicListButton);
        buttonPanel.add(new JLabel("")); // 右側空白
          // 音量控制
        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        volumePanel.setOpaque(false);
        volumePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // 調整上邊距
        
        volumeLabel = new JLabel("音量: ");
        volumeLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 80);
        volumeSlider.setOpaque(false);
        volumeSlider.setPreferredSize(new Dimension(200, 30));
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setVolume(volumeSlider.getValue());
            }
        });
        
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);
        
        controlPanel.add(buttonPanel, BorderLayout.CENTER);
        controlPanel.add(volumePanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }
      private JButton createControlButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(110, 35)); // 調整按鈕大小
        button.setBackground(new Color(245, 248, 255));
        button.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 230), 1));
        return button;
    }
    
    private String getFormattedMusicName(String resourcePath) {
        // 從資源路徑中獲取檔案名稱
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            return "請新增音樂";
        }
        String fileName = new File(resourcePath).getName(); // Works for both classpath and absolute paths
        // 移除副檔名
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return fileName;
    }
    
    private void loadMusic(String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                currentMusicLabel.setText("無效的音樂路徑");
                if (marqueeTimer != null) marqueeTimer.stop();
                return;
            }
            // 先停止並釋放前一個音樂的資源
            stopMusic();

            InputStream audioSrc;
            // Check if it's a classpath resource or an absolute file path
            if (filePath.startsWith("/")) { // Classpath resource (from original assets or copied)
                audioSrc = MusicPanel.class.getResourceAsStream(filePath);
                if (audioSrc == null) {
                    throw new IOException("無法找到音樂資源: " + filePath);
                }
            } else { // Absolute file path (user-imported)
                File musicFile = new File(filePath);
                if (!musicFile.exists()) {
                    throw new IOException("音樂檔案不存在: " + filePath);
                }
                audioSrc = new FileInputStream(musicFile);
            }

            // 使用 BufferedInputStream 以支援 mark/reset
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            
            // 設置音量控制
            if (audioClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
                setVolume(volumeSlider.getValue());
            }
            
            // 更新顯示的音樂名稱
            currentMusicLabel.setText(getFormattedMusicName(filePath));
            
            // 設置播放完畢後的處理
            audioClip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP && audioClip.getMicrosecondPosition() >= audioClip.getMicrosecondLength()) {
                        // 播放至結束，自動播放下一首
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                playNext();
                            }
                        });
                    }
                }
            });
            
            startMarqueeEffect(); // 開始跑馬燈效果
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            JOptionPane.showMessageDialog(this, "載入音樂檔案時發生錯誤: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void stopMusic() {
        if (marqueeTimer != null) { // Stop marquee when music stops or changes
            marqueeTimer.stop();
        }
        if (audioClip != null && audioClip.isOpen()) {
            audioClip.stop();
            audioClip.close();
        }
    }
    
    private void setVolume(int volume) {
        if (volumeControl != null) {
            // 將JSlider的值(0-100)轉換為分貝值(-80.0 到 6.0 dB)
            float volumeValue = (float) (volume / 100.0);
            float dB = (float) (Math.log10(volumeValue) * 20.0);
            if (volume == 0) {
                dB = volumeControl.getMinimum();
            }
            // 確保在有效範圍內
            if (dB < volumeControl.getMinimum()) {
                dB = volumeControl.getMinimum();
            } else if (dB > volumeControl.getMaximum()) {
                dB = volumeControl.getMaximum();
            }
            volumeControl.setValue(dB);
        }
    }
    
    private void togglePlayPause() {
        if (isPlaying) {
            if (audioClip != null) {
                audioClip.stop();
            }
            playPauseButton.setText("播放");
            isPlaying = false;
        } else {
            if (audioClip != null) {
                audioClip.start();
            }
            playPauseButton.setText("暫停");
            isPlaying = true;
        }
    }
    
    private void playPrevious() {
        if (musicFiles.isEmpty()) return;
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = musicFiles.size() - 1;
        }
        loadMusic(musicFiles.get(currentIndex));
        
        if (isPlaying && audioClip != null) {
            audioClip.start();
        }
    }
    
    private void playNext() {
        if (musicFiles.isEmpty()) return;
        currentIndex++;
        if (currentIndex >= musicFiles.size()) {
            currentIndex = 0;
        }
        loadMusic(musicFiles.get(currentIndex));
        
        if (isPlaying && audioClip != null) {
            audioClip.start();
        }
    }
    
    private void startMarqueeEffect() {
        if (musicFiles.isEmpty() || currentIndex < 0 || currentIndex >= musicFiles.size()) {
            if (marqueeTimer != null) marqueeTimer.stop();
            currentMusicLabel.setText(getFormattedMusicName(null)); // Or "請新增音樂"
            return;
        }
        fullTitle = getFormattedMusicName(musicFiles.get(currentIndex));
        marqueeIndex = 0;

        if (marqueeTimer != null) {
            marqueeTimer.stop();
        }

        marqueeTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fullTitle.length() > 20) { // 如果標題過長才滾動
                    marqueeIndex = (marqueeIndex + 1) % fullTitle.length();
                    String displayText = fullTitle.substring(marqueeIndex) + " " + fullTitle.substring(0, marqueeIndex);
                    currentMusicLabel.setText(displayText);
                } else {
                    currentMusicLabel.setText(fullTitle);
                }
            }
        });
        marqueeTimer.start();
    }
      // 釋放資源
    public void dispose() {
        stopMusic();
    }
      /**
     * 新增音樂文件到播放列表
     */
    private void addNewMusic() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("選擇音樂文件");
        fileChooser.setMultiSelectionEnabled(true);

        // 設置文件過濾器，只顯示音頻文件
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".wav") || name.endsWith(".mp3") || name.endsWith(".aiff");
            }

            @Override
            public String getDescription() {
                return "音頻文件 (*.wav, *.mp3, *.aiff)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            boolean musicAdded = false;

            // 獲取桌面路徑並建立 StudySyncMusic 資料夾
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            File appMusicDir = new File(desktopPath, "StudySyncMusic");
            if (!appMusicDir.exists()) {
                if (!appMusicDir.mkdirs()) {
                    JOptionPane.showMessageDialog(this,
                            "無法在桌面建立 StudySyncMusic 資料夾。",
                            "錯誤",
                            JOptionPane.ERROR_MESSAGE);
                    return; // 無法建立資料夾，則終止操作
                }
            }

            for (File file : selectedFiles) {
                try {
                    Path sourcePath = file.toPath();
                    Path targetPath = new File(appMusicDir, file.getName()).toPath();

                    // 複製檔案到桌面上的 StudySyncMusic 資料夾
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                    String musicPath = targetPath.toString(); // 儲存複製後檔案的絕對路徑
                    if (!musicFiles.contains(musicPath)) { // Avoid duplicates
                        musicFiles.add(musicPath);
                        musicAdded = true;
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "複製音樂檔案 '" + file.getName() + "' 到桌面時發生錯誤: " + ex.getMessage(),
                            "錯誤",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    // 即使單一檔案複製失敗，也繼續處理其他檔案
                }
            }

            if (musicAdded) {
                // Save updated music list to AppSettings
                AppSettings.getInstance().setCustomMusicPaths(String.join(";", musicFiles));
                JOptionPane.showMessageDialog(this,
                        "已成功添加音樂到桌面 StudySyncMusic 資料夾。",
                        "新增音樂",
                        JOptionPane.INFORMATION_MESSAGE);

                // If the list was empty, load the first added song
                if (musicFiles.size() == Arrays.stream(selectedFiles).filter(f -> new File(appMusicDir, f.getName()).exists()).count() && !musicFiles.isEmpty()) {
                    currentIndex = 0;
                    loadMusic(musicFiles.get(currentIndex));
                }
                updatePlaybackControlsState();
            } else if (selectedFiles.length > 0) { // User selected files, but none were new
                 JOptionPane.showMessageDialog(this,
                        "選擇的音樂已存在於播放清單中或無法複製。",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * 顯示音樂列表對話框
     */
    private void showMusicListDialog() {
        // 創建對話框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "音樂列表", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // 創建音樂列表模型
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String musicFile : musicFiles) {
            listModel.addElement(getFormattedMusicName(musicFile));
        }
        
        // 創建列表組件
        JList<String> musicList = new JList<>(listModel);
        musicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        musicList.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
        
        // 雙擊播放選中的音樂
        musicList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = musicList.getSelectedIndex();
                    if (index != -1) {
                        currentIndex = index;
                        loadMusic(musicFiles.get(currentIndex));
                        if (!isPlaying) {
                            togglePlayPause(); // 自動開始播放
                        }
                        dialog.dispose();
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(musicList);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // 創建按鈕面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton addButton = new JButton("新增音樂");
        JButton deleteButton = new JButton("刪除音樂");
        JButton closeButton = new JButton("關閉");

        // 設置按鈕樣式
        for (JButton button : new JButton[]{addButton, deleteButton, closeButton}) {
            button.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
            button.setFocusPainted(false);
        }
        
        // 添加按鈕動作
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean wasEmpty = musicFiles.isEmpty();
                addNewMusic(); // Modifies MusicPanel.this.musicFiles
                // 更新列表
                listModel.clear();
                for (String musicFile : musicFiles) {
                    listModel.addElement(getFormattedMusicName(musicFile));
                }
                MusicPanel.this.updatePlaybackControlsState();
                if (wasEmpty && !musicFiles.isEmpty()) {
                    MusicPanel.this.currentIndex = 0; // Load the first added song
                    MusicPanel.this.loadMusic(musicFiles.get(MusicPanel.this.currentIndex));
                } else if (!musicFiles.isEmpty() && (MusicPanel.this.audioClip == null || !MusicPanel.this.audioClip.isOpen())) {
                    // If list was not empty but nothing loaded, or became non-empty again
                     MusicPanel.this.loadMusic(musicFiles.get(MusicPanel.this.currentIndex)); // Ensure current is loaded
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndexInList = musicList.getSelectedIndex();
                if (selectedIndexInList != -1) {
                    // 確認刪除
                    int option = JOptionPane.showConfirmDialog(
                            dialog,
                            "確定要刪除選中的音樂嗎？ (注意：這只會從播放列表中移除)", // Clarified deletion scope
                            "確認刪除",
                            JOptionPane.YES_NO_OPTION);
                    
                    if (option == JOptionPane.YES_OPTION) {
                        String musicPathToRemove = musicFiles.get(selectedIndexInList); // Get path before removing from listModel if it's different index

                        boolean wasPlayingDeletedSong = (selectedIndexInList == currentIndex && isPlaying);

                        // 從列表中移除
                        musicFiles.remove(selectedIndexInList);
                        listModel.remove(selectedIndexInList);

                        // Save updated music list to AppSettings
                        AppSettings.getInstance().setCustomMusicPaths(String.join(";", musicFiles));

                        // 調整當前索引
                        if (musicFiles.isEmpty()) {
                            currentIndex = 0;
                            stopMusic(); // stopMusic is also called in updatePlaybackControlsState
                        } else {
                            if (currentIndex >= selectedIndexInList) {
                                currentIndex = Math.max(0, currentIndex - 1);
                            }
                             // Ensure currentIndex is valid
                            if (currentIndex >= musicFiles.size()) {
                                currentIndex = musicFiles.size() - 1;
                            }
                        }
                        
                        MusicPanel.this.updatePlaybackControlsState();
                        
                        if (musicFiles.isEmpty()) {
                            JOptionPane.showMessageDialog(dialog,
                                    "已刪除所有音樂，請添加新的音樂。",
                                    "提示",
                                    JOptionPane.INFORMATION_MESSAGE);
                            // dialog.dispose(); // Optional: close dialog if list becomes empty
                        } else {
                            // If the deleted song was playing or list re-ordered
                            loadMusic(musicFiles.get(currentIndex));
                            if (wasPlayingDeletedSong) {
                                if (MusicPanel.this.isPlaying) { // If panel is still in playing state
                                   if (audioClip != null) audioClip.start();
                                } else {
                                   playPauseButton.setText("播放");
                                }
                            }
                        }
                         // Note: Actual file deletion from assets is not handled here robustly.
                         // The user was informed it's removed from playlist.
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "請先選擇要刪除的音樂",
                            "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        // 添加按鈕到面板
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // 顯示對話框
        dialog.setVisible(true);
    }

    // New helper method
    private void updatePlaybackControlsState() {
        if (musicFiles.isEmpty()) {
            if (isPlaying || (audioClip != null && audioClip.isRunning())) {
                stopMusic(); // Stop music if list becomes empty
            }
            currentMusicLabel.setText("請新增音樂");
            isPlaying = false;
            playPauseButton.setText("播放");
            playPauseButton.setEnabled(false);
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            if (marqueeTimer != null) {
                marqueeTimer.stop();
            }
        } else {
            playPauseButton.setEnabled(true);
            prevButton.setEnabled(true);
            nextButton.setEnabled(true);
            // currentMusicLabel will be updated by loadMusic
            // If music was just added to an empty list, loadMusic would be called separately.
        }
    }
}
