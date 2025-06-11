package com.studysync.view;

import com.studysync.controller.TaskController;
import com.studysync.model.Task;
import com.studysync.model.User;
import com.studysync.service.CategoryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任務管理面板類
 * 提供任務的顯示、新增、編輯、刪除等功能介面
 * 
 * 主要功能：
 * - 任務列表顯示和篩選（全部、今日、已完成）
 * - 分類管理（新增、編輯、刪除分類）
 * - 任務操作（新增任務、標記完成、刪除任務）
 * - 支援多種篩選條件組合
 * - 即時更新任務狀態
 * 
 * UI 組件：
 * - 篩選按鈕組（All、Today、Completed）
 * - 分類下拉選單及管理按鈕
 * - 任務列表滾動面板
 * - 新增任務按鈕
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class TaskPanel extends JPanel {    // 修改為 public 以便其他類訪問
    public final TaskController controller;
    private final CategoryService categoryService;
    private final JPanel taskListPanel = new JPanel();
    private JScrollPane scrollPane; // Make scrollPane a member field
    private final JButton allBtn = new JButton("All");
    private final JButton todayBtn = new JButton("Today");
    private final JButton completedBtn = new JButton("Completed");
    private final JComboBox<String> categoryComboBox = new JComboBox<>();
    private final JButton addCategoryBtn = new JButton("新增");
    private final JButton editCategoryBtn = new JButton("編輯");
    private final JButton deleteCategoryBtn = new JButton("刪除");
    private String currentCategory = "全部分類"; // Keep this for category filtering logic
    private String currentFilter = "all"; // Keep this for general filtering logic
    private final Font chineseFont = new Font("微軟正黑體", Font.PLAIN, 15);
    private final Font chineseFontBold = new Font("微軟正黑體", Font.BOLD, 15);
    private final Font titleFont = new Font("微軟正黑體", Font.BOLD, 28);
    public TaskPanel() {
        // Initialize final fields outside the main try-catch block
        this.controller = new TaskController();
        this.categoryService = controller.getCategoryService();

        try { // Added try for the rest of the constructor
            setLayout(new BorderLayout());
            setBackground(new Color(245, 248, 255));

            // 從資料庫讀取類別並填入下拉選單
            try {
                List<String> categories = categoryService.getAllCategories();
                for (String category : categories) {
                    categoryComboBox.addItem(category);
                }
            } catch (Exception e) {
                System.err.println("加載類別時發生錯誤: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "加載類別時發生錯誤: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                // 加載預設類別
                String[] defaultCategories = {"全部分類", "Studying", "Homework", "Writing", "Meeting", "Reading"};
                for (String category : defaultCategories) {
                    categoryComboBox.addItem(category);
                }
            }

            int sidePadding = 16;

            // 單個標題區塊
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            titlePanel.setBackground(Color.WHITE);
            titlePanel.setBorder(new EmptyBorder(12, sidePadding, 4, 0));

            JLabel tasksLabel = new JLabel("Tasks");
            tasksLabel.setFont(titleFont);
            tasksLabel.setForeground(new Color(30, 30, 30));
            titlePanel.add(tasksLabel);        // 分類選單區塊
            JPanel categoryPanel = new JPanel();
            categoryPanel.setBackground(Color.WHITE);
            categoryPanel.setBorder(new EmptyBorder(4, 0, 4, 0));
            categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.X_AXIS));
            // 添加左側填充以對齊標題和篩選按鈕
            categoryPanel.add(Box.createHorizontalStrut(sidePadding));

            // 創建一個水平面板來容納所有元素
            JPanel horizontalPanel = new JPanel();        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
            horizontalPanel.setBackground(Color.WHITE);        // 分類下拉選單
            categoryComboBox.setFont(chineseFont);
            categoryComboBox.setPreferredSize(new Dimension(120, 32)); // 寬度調整為120
            categoryComboBox.setBackground(Color.WHITE);
            categoryComboBox.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 230), 1));
            horizontalPanel.add(categoryComboBox);

            // 添加固定的間距
            horizontalPanel.add(Box.createHorizontalStrut(10));

            // 設置按鈕的樣式
            Dimension buttonSize = new Dimension(70, 32); // 還原為原來的尺寸
            Font buttonFont = new Font("微軟正黑體", Font.PLAIN, 14);

            addCategoryBtn.setPreferredSize(buttonSize);
            addCategoryBtn.setFont(buttonFont);
            addCategoryBtn.setToolTipText("新增類別");
            addCategoryBtn.setFocusPainted(false);
            addCategoryBtn.setBackground(new Color(230, 240, 255));
            addCategoryBtn.setForeground(new Color(60, 100, 200));
            addCategoryBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230), 1));
            addCategoryBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            editCategoryBtn.setPreferredSize(buttonSize);
            editCategoryBtn.setFont(buttonFont);
            editCategoryBtn.setToolTipText("編輯類別");
            editCategoryBtn.setFocusPainted(false);
            editCategoryBtn.setBackground(new Color(230, 240, 255));
            editCategoryBtn.setForeground(new Color(60, 100, 200));
            editCategoryBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230), 1));
            editCategoryBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            deleteCategoryBtn.setPreferredSize(buttonSize);
            deleteCategoryBtn.setFont(buttonFont);
            deleteCategoryBtn.setToolTipText("刪除類別");
            deleteCategoryBtn.setFocusPainted(false);
            deleteCategoryBtn.setBackground(new Color(230, 240, 255));
            deleteCategoryBtn.setForeground(new Color(60, 100, 200));
            deleteCategoryBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230), 1));
            deleteCategoryBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // 將按鈕添加到水平面板並添加間距
            horizontalPanel.add(addCategoryBtn);
            horizontalPanel.add(Box.createHorizontalStrut(5));
            horizontalPanel.add(editCategoryBtn);
            horizontalPanel.add(Box.createHorizontalStrut(5));
            horizontalPanel.add(deleteCategoryBtn);
            // 將水平面板添加到分類面板
            categoryPanel.add(horizontalPanel);        // 添加類別按鈕事件
            addCategoryBtn.addActionListener(e -> {
                try { // Added try for addCategoryBtn ActionListener
                    String newCategory = JOptionPane.showInputDialog(this, "輸入新類別名稱：", "新增類別", JOptionPane.PLAIN_MESSAGE);
                    if (newCategory != null && !newCategory.trim().isEmpty()) {
                        // 檢查是否已存在
                        boolean exists = false;
                        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                            if (categoryComboBox.getItemAt(i).equals(newCategory)) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            // 保存到資料庫
                            if (categoryService.addCategory(newCategory)) {
                                categoryComboBox.addItem(newCategory);
                                categoryComboBox.setSelectedItem(newCategory);
                                JOptionPane.showMessageDialog(this, "類別新增成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this, "類別新增失敗，請稍候再試！", "錯誤", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "此類別已存在！", "錯誤", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "新增類別時發生未預期錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });

            editCategoryBtn.addActionListener(e -> {
                try { // Added try for editCategoryBtn ActionListener
                    if (categoryComboBox.getSelectedItem() == null || "全部分類".equals(categoryComboBox.getSelectedItem().toString())) {
                        JOptionPane.showMessageDialog(this, "請先選擇一個要編輯的類別(不能編輯「全部分類」)", "提示", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    String oldCategory = categoryComboBox.getSelectedItem().toString();
                    String newCategory = JOptionPane.showInputDialog(this, "修改類別名稱：", oldCategory);

                    if (newCategory != null && !newCategory.trim().isEmpty() && !newCategory.equals(oldCategory)) {
                        // 檢查是否已存在
                        boolean exists = false;
                        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                            if (categoryComboBox.getItemAt(i).equals(newCategory)) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            // 更新資料庫
                            if (categoryService.updateCategory(oldCategory, newCategory)) {
                                int index = categoryComboBox.getSelectedIndex();
                                categoryComboBox.removeItemAt(index);
                                categoryComboBox.insertItemAt(newCategory, index);
                                categoryComboBox.setSelectedIndex(index);

                                // 更新有此類別的任務
                                updateTasksCategory(oldCategory, newCategory);
                                JOptionPane.showMessageDialog(this, "類別更新成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this, "類別更新失敗，請稍候再試！", "錯誤", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "此類別已存在！", "錯誤", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "編輯類別時發生未預期錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });

            deleteCategoryBtn.addActionListener(e -> {
                try { // Added try for deleteCategoryBtn ActionListener
                    if (categoryComboBox.getSelectedItem() == null || "全部分類".equals(categoryComboBox.getSelectedItem().toString())) {
                        JOptionPane.showMessageDialog(this, "請先選擇一個要刪除的類別(不能刪除「全部分類」)", "提示", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    String category = categoryComboBox.getSelectedItem().toString();
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "確定要刪除類別「" + category + "」嗎？\n注意：使用此類別的任務將被設為無類別",
                            "刪除類別",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        // 從資料庫刪除
                        if (categoryService.deleteCategory(category)) {
                            categoryComboBox.removeItem(category);
                            categoryComboBox.setSelectedIndex(0); // 選擇「全部分類」

                            // 將有此類別的任務設為無類別
                            updateTasksCategory(category, null);
                            JOptionPane.showMessageDialog(this, "類別刪除成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "類別刪除失敗，請稍候再試！", "錯誤", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "刪除類別時發生未預期錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });// 篩選按鈕區
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new EmptyBorder(4, 0, 8, 0));
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));

        // 添加左側填充以對齊標題
        filterPanel.add(Box.createHorizontalStrut(sidePadding));
        filterPanel.add(createAlignedButtonPanel());

        setActiveFilterButton(allBtn);
        allBtn.addActionListener(e -> { try { setActiveFilterButton(allBtn); currentFilter = "all"; refreshTasks(currentFilter); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "處理'All'篩選時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE); ex.printStackTrace(); } });
        todayBtn.addActionListener(e -> { try { setActiveFilterButton(todayBtn); currentFilter = "today"; refreshTasks(currentFilter); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "處理'Today'篩選時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE); ex.printStackTrace(); } });
        completedBtn.addActionListener(e -> { try { setActiveFilterButton(completedBtn); currentFilter = "completed"; refreshTasks(currentFilter); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "處理'Completed'篩選時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE); ex.printStackTrace(); } });

        // 監聽分類下拉選單
        categoryComboBox.addActionListener(e -> {
            try { // Added try for categoryComboBox ActionListener
                currentCategory = (String) categoryComboBox.getSelectedItem();
                refreshTasks(currentFilter);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "處理分類選擇時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // header 組合所有區塊
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.add(titlePanel);
        header.add(categoryPanel);
        header.add(filterPanel);

        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(Color.WHITE);
        scrollPane = new JScrollPane(taskListPanel); // Initialize member field
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // 任務欄區域保留格線
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(210, 215, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(900, 750)); // 調整為適應1000x1000的視窗大小

        JButton addBtn = new JButton("+ Add Task");
        addBtn.setBackground(new Color(66, 135, 245));
        addBtn.setForeground(Color.WHITE);        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("微軟正黑體", Font.BOLD, 18));
        addBtn.setPreferredSize(new Dimension(180, 50));
        addBtn.setBorder(null);
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.setFocusable(false);
        addBtn.setOpaque(true);
        addBtn.setBorderPainted(false);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(16, 0, 16, sidePadding));
        bottomPanel.add(addBtn);

        setBackground(new Color(245, 248, 255));
        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);        addBtn.addActionListener(e -> {
            try { // Added try for addBtn ActionListener
                TaskFormDialog dialog = new TaskFormDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(this),
                        controller,
                        () -> {
                            try { // Added try for the callback
                                refreshTasks(currentFilter); // 直接刷新
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(this, "刷新任務時發生錯誤(回呼): " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                                ex.printStackTrace();
                            }
                        }
                );
                dialog.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "開啟新增任務對話框時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // 首次加載時直接刷新
        try { // Added try for initial refreshTasks
            refreshTasks(currentFilter);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "初始化刷新任務時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    } catch (Exception ex) { // Added catch for the entire constructor
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
            null, // Parent might not be available or fully initialized
            "初始化任務面板時發生嚴重錯誤: " + ex.getMessage(),
            "嚴重錯誤",
            JOptionPane.ERROR_MESSAGE
        );
        // Fallback UI in case of critical initialization error
        removeAll();
        setLayout(new BorderLayout()); // Ensure layout manager for fallback
        add(new JLabel("任務面板載入錯誤，請檢查日誌: " + ex.getMessage()), BorderLayout.CENTER);
    }
}

    private void setActiveFilterButton(JButton activeBtn) {
        try { // Added try for setActiveFilterButton
            JButton[] buttons = {allBtn, todayBtn, completedBtn};
            for (JButton b : buttons) {
                if (b == activeBtn) {
                    b.setBackground(new Color(230, 240, 255));
                    b.setForeground(new Color(66, 135, 245));
                    b.setBorder(BorderFactory.createLineBorder(new Color(66, 135, 245), 2));
                } else {
                    b.setBackground(Color.WHITE);
                    b.setForeground(new Color(80, 80, 80));
                    b.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230), 1));
                }
            }
        } catch (Exception ex) {
            System.err.println("設定篩選按鈕狀態時發生錯誤: " + ex.getMessage());
            ex.printStackTrace();
            // Optionally show a message, but this is more of an internal UI update method
        }
    }    public void refreshTasks() {
        refreshTasks(this.currentFilter, this.currentCategory);
    }

    public void refreshTasks(String filterType) {
        refreshTasks(filterType, this.currentCategory);
    }

    public void refreshTasks(String filterType, String category) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("TaskPanel: Refreshing tasks with filter " + filterType + " and category " + category);
                this.currentFilter = filterType;
                this.currentCategory = category;

                User currentUser = controller.getCurrentUser();
                if (currentUser == null) {
                    System.out.println("TaskPanel: No current user, clearing task list.");
                    taskListPanel.removeAll();
                    taskListPanel.add(new JLabel("Please log in to see tasks."));
                    // Make sure to revalidate/repaint even when no user
                    taskListPanel.revalidate();
                    taskListPanel.repaint();
                    if (scrollPane != null) {
                        scrollPane.revalidate();
                        scrollPane.repaint();
                    }
                    TaskPanel.this.revalidate();
                    TaskPanel.this.repaint();
                    return;
                }

                List<Task> tasks = controller.getTasks(filterType, category, currentUser); 
                System.out.println("TaskPanel: Fetched " + tasks.size() + " tasks.");

                taskListPanel.removeAll();
                if (tasks.isEmpty()) {
                    JLabel noTasksLabel = new JLabel("No tasks found for this filter/category.");
                    noTasksLabel.setFont(chineseFont);
                    noTasksLabel.setForeground(Color.GRAY);
                    taskListPanel.add(noTasksLabel);
                } else {
                    for (Task task : tasks) {
                        taskListPanel.add(new TaskItemPanel(task, this)); 
                        taskListPanel.add(Box.createVerticalStrut(8)); 
                    }
                }
            } catch (Exception e) {
                System.err.println("Error refreshing tasks: " + e.getMessage());
                e.printStackTrace();
                taskListPanel.removeAll();
                JLabel errorLabel = new JLabel("Error loading tasks. Please try again.");
                errorLabel.setFont(chineseFont);
                errorLabel.setForeground(Color.RED);
                taskListPanel.add(errorLabel);
            } finally {
                System.out.println("TaskPanel: Revalidating and repainting UI components after refresh.");
                taskListPanel.revalidate();
                taskListPanel.repaint();

                if (scrollPane != null) {
                    scrollPane.revalidate();
                    scrollPane.repaint();
                    if (scrollPane.getViewport() != null) {
                        scrollPane.getViewport().revalidate();
                        scrollPane.getViewport().repaint();
                    }
                }

                TaskPanel.this.revalidate();
                TaskPanel.this.repaint();

                Window window = SwingUtilities.getWindowAncestor(TaskPanel.this);
                if (window != null) {
                    window.revalidate();
                    window.repaint();
                }
                System.out.println("TaskPanel: UI refresh complete.");
            }
        });
    }

    // Getter for currentFilter to be used by TaskItemPanel
    public String getCurrentFilter() {
        return currentFilter;
    }

    // Getter for currentCategory to be used by TaskItemPanel or other external refresh triggers
    public String getCurrentCategory() {
        return currentCategory;
    }

    private void updateTasksCategory(String oldCategory, String newCategory) {
        try { // Added try for updateTasksCategory
            controller.updateTasksCategory(oldCategory, newCategory);
            refreshTasks(currentFilter);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "更新任務類別時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }    // 新增方法來創建左對齊的按鈕面板
    private JPanel createAlignedButtonPanel() {
        try { // Added try for createAlignedButtonPanel
            JPanel alignedButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            alignedButtonPanel.setBackground(Color.WHITE);
            JButton[] buttons = {allBtn, todayBtn, completedBtn};
            for (int i = 0; i < buttons.length; i++) {
                JButton b = buttons[i];
                b.setFocusPainted(false);
                b.setBackground(Color.WHITE);
                b.setFont(chineseFontBold); // 已是微軟正黑體
                b.setPreferredSize(new Dimension(110, 36));
                b.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 230), 1));
                alignedButtonPanel.add(b);
                if (i < buttons.length - 1) {
                    alignedButtonPanel.add(Box.createHorizontalStrut(10)); // 減少間距
                }
            }
            return alignedButtonPanel;
        } catch (Exception ex) {
            System.err.println("創建對齊按鈕面板時發生錯誤: " + ex.getMessage());
            ex.printStackTrace();
            // Fallback: return an empty panel or a panel with an error message
            JPanel errorPanel = new JPanel();
            errorPanel.add(new JLabel("按鈕面板載入錯誤"));
            return errorPanel;
        }
    }

    // REMOVE forceRefreshWithRetry method
    /*
    // 多次嘗試刷新，確保UI更新
    private void forceRefreshWithRetry(int maxRetries) {
        try { // Added try for forceRefreshWithRetry
            System.out.println("開始進行強制刷新，最大嘗試次數: " + maxRetries);

            // 立即刷新一次
            refreshTasks(currentFilter);

            // 然後設置一個延遲計時器，多次嘗試刷新
            for (int i = 1; i <= maxRetries; i++) {
                final int retryCount = i;
                javax.swing.Timer timer = new javax.swing.Timer(i * 200, event -> { // Explicitly use javax.swing.Timer
                    try { // Added try for the Timer's ActionListener
                        System.out.println("第 " + retryCount + " 次延遲刷新");
                        refreshTasks(currentFilter);
                    } catch (Exception ex) {
                        System.err.println("延遲刷新任務時發生錯誤 (嘗試 " + retryCount + "): " + ex.getMessage());
                        ex.printStackTrace();
                        // Optionally show a non-intrusive error, or just log
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        } catch (Exception ex) {
            System.err.println("強制刷新任務 (forceRefreshWithRetry) 時發生錯誤: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "嘗試刷新任務時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }
    */
    
    // 設置當前用戶
    public void setCurrentUser(User user) {
        try { // Added try for setCurrentUser
            if (user != null) {
                controller.setCurrentUser(user);
                refreshTasks(currentFilter); // 設置用戶後立即刷新任務列表
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "設置當前用戶時發生錯誤: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
