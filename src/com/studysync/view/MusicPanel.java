package com.studysync.view;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream; // 新增 import
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

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
        loadMusic(musicFiles.get(currentIndex));
    }
    
    private void initMusicList() {
        musicFiles = new ArrayList<>();
        // 修改音樂文件路徑，使用相對於 classpath 的路徑
        musicFiles.add("/com/studysync/assets/music/統神 - 你從桃園新竹Lofi 1hr.wav");
        musicFiles.add("/com/studysync/assets/music/統神 - 欸欸肉羹麵Lofi 1hr.wav");
        musicFiles.add("/com/studysync/assets/music/統神 - 你們聽我講一件事情Lofi 1hr.wav");
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
        currentMusicLabel = new JLabel(getFormattedMusicName(musicFiles.get(currentIndex)), SwingConstants.CENTER);
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
        String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        // 移除副檔名
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return fileName;
    }
    
    private void loadMusic(String filePath) {
        try {
            // 先停止並釋放前一個音樂的資源
            stopMusic();
            
            // 從 classpath 載入音樂資源
            InputStream audioSrc = MusicPanel.class.getResourceAsStream(filePath);
            if (audioSrc == null) {
                throw new IOException("無法找到音樂資源: " + filePath);
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
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = musicFiles.size() - 1;
        }
        loadMusic(musicFiles.get(currentIndex));
        
        // 如果當前是播放狀態，則自動播放新載入的歌曲
        if (isPlaying) {
            audioClip.start();
        }
    }
    
    private void playNext() {
        currentIndex++;
        if (currentIndex >= musicFiles.size()) {
            currentIndex = 0;
        }
        loadMusic(musicFiles.get(currentIndex));
        
        // 如果當前是播放狀態，則自動播放新載入的歌曲
        if (isPlaying) {
            audioClip.start();
        }
    }
    
    private void startMarqueeEffect() {
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
            for (File file : selectedFiles) {
                try {
                    // 複製文件到資產目錄
                    Path targetDir = Paths.get("src/com/studysync/assets/music");
                    
                    // 確保目標目錄存在
                    Files.createDirectories(targetDir);
                    
                    Path targetPath = targetDir.resolve(file.getName());
                    Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    
                    // 將新音樂添加到列表
                    String musicPath = targetPath.toString();
                    musicFiles.add(musicPath);
                    
                    JOptionPane.showMessageDialog(this, 
                            "已成功添加音樂：" + file.getName(), 
                            "新增音樂", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "添加音樂時發生錯誤：" + ex.getMessage(), 
                            "錯誤", 
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
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
                addNewMusic();
                // 更新列表
                listModel.clear();
                for (String musicFile : musicFiles) {
                    listModel.addElement(getFormattedMusicName(musicFile));
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = musicList.getSelectedIndex();
                if (selectedIndex != -1) {
                    // 確認刪除
                    int option = JOptionPane.showConfirmDialog(
                            dialog,
                            "確定要刪除選中的音樂嗎？",
                            "確認刪除",
                            JOptionPane.YES_NO_OPTION);
                    
                    if (option == JOptionPane.YES_OPTION) {
                        // 如果正在播放要刪除的音樂，先停止播放
                        if (selectedIndex == currentIndex && isPlaying) {
                            togglePlayPause();
                        }
                        
                        // 刪除音樂文件
                        try {
                            File musicFile = new File(musicFiles.get(selectedIndex));
                            boolean deleted = musicFile.delete();
                            
                            if (deleted || !musicFile.exists()) {
                                // 從列表中移除
                                musicFiles.remove(selectedIndex);
                                listModel.remove(selectedIndex);
                                
                                // 調整當前索引
                                if (currentIndex >= selectedIndex) {
                                    currentIndex = Math.max(0, currentIndex - 1);
                                }
                                
                                // 如果刪除後沒有音樂了
                                if (musicFiles.isEmpty()) {
                                    stopMusic();
                                    JOptionPane.showMessageDialog(dialog,
                                            "已刪除所有音樂，請添加新的音樂。",
                                            "提示",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    dialog.dispose();
                                } else if (selectedIndex == currentIndex) {
                                    // 重新加載當前音樂
                                    loadMusic(musicFiles.get(currentIndex));
                                }
                            } else {
                                JOptionPane.showMessageDialog(dialog,
                                        "無法刪除文件：" + musicFile.getName(),
                                        "錯誤",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog,
                                    "刪除音樂時發生錯誤：" + ex.getMessage(),
                                    "錯誤",
                                    JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
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
}
