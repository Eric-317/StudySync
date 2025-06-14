package com.studysync.view;

import com.studysync.model.CalendarEvent;
import com.studysync.controller.TaskController;
import com.studysync.model.Task;
import com.studysync.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import com.studysync.controller.CalendarEventController;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * 行事曆面板類
 * 提供月曆顯示、事件管理、任務整合等功能
 * 
 * 主要功能：
 * - 月曆視圖顯示（上一月/下一月導航）
 * - 行事曆事件管理（新增、編輯、刪除事件）
 * - 任務整合顯示（將任務作為事件顯示在行事曆上）
 * - 多使用者支援（事件和任務隔離）
 * - 日期選擇和事件詳細資訊顯示
 * 
 * UI 組件：
 * - 月曆標題和導航按鈕
 * - 7x7 網格月曆顯示
 * - 事件列表顯示面板
 * - 事件新增和編輯對話框
 * 
 * 技術特點：
 * - 支援任務和事件的統一顯示
 * - 即時資料更新和同步
 * - 使用者登入狀態檢查
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class CalendarPanel extends JPanel {

    // Fields from CalendarDemo structure
    private JLabel monthTitleLabel; // Renamed from titleLabel to avoid conflict
    private JPanel calendarGridPanel;
    private JPanel southEventsPanel; // Renamed from southPanel
    private LocalDate displayedMonthFirstDay; // Replaces CalendarDemo's displayedMonth
    private LocalDate currentSelectedDate;    // Retained from CalendarPanel, equivalent to CalendarDemo's selectedDate
    private JPanel eventListDisplayPanel; // Renamed from eventListPanel
    private JLabel detailDateLabel; // For "Details for..."

    // Existing fields from CalendarPanel
    private YearMonth currentYearMonth; // Retained for logic if needed, or can be derived
    private Map<LocalDate, List<CalendarEvent>> dailyEvents = new HashMap<>();
    private TaskController taskController = new TaskController();
    private CalendarEventController eventController = new CalendarEventController(); // 初始化行程 Controller
    private User currentUser; // 添加當前用戶引用
    private boolean userInitialized = false; // 跟蹤用戶是否已設置

    // Styling (can be consolidated later if desired)
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color HEADER_TEXT_COLOR = new Color(70, 70, 70);
    private static final Color DAY_TEXT_COLOR = new Color(100, 100, 100);
    private static final Color SELECTED_DATE_BG_COLOR = new Color(66, 135, 245);
    private static final Color SELECTED_DATE_FG_COLOR = Color.WHITE;
    private static final Color TODAY_BORDER_COLOR = new Color(150, 180, 220); // Kept for "today" indication
    private static final Font MONTH_YEAR_FONT = new Font("Arial", Font.BOLD, 22); // Was MONTH_YEAR_FONT
    private static final Font DAY_HEADER_FONT = new Font("Arial", Font.BOLD, 16); // Was DAY_HEADER_FONT, adjusted size
    private static final Font DATE_FONT = new Font("Arial", Font.BOLD, 16); // Was DATE_FONT, adjusted style and size
    private static final Font TASK_DETAIL_FONT = new Font("Arial", Font.BOLD, 15); // Was TASK_FONT
    private static final Font ADD_BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    public CalendarPanel() {
        this.currentSelectedDate = LocalDate.now();
        this.displayedMonthFirstDay = LocalDate.now().withDayOfMonth(1);
        this.currentYearMonth = YearMonth.from(this.displayedMonthFirstDay); // Keep currentYearMonth in sync

        setLayout(new BorderLayout(0, 2)); // Main panel layout with reduced vertical gap
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Overall padding        // NORTH: Header with month title, navigation, and legend
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel monthHeaderPanel = createMonthHeaderPanel();
        JPanel legendPanel = createLegendPanel();
        
        northPanel.add(monthHeaderPanel, BorderLayout.NORTH);
        northPanel.add(legendPanel, BorderLayout.SOUTH);
        
        add(northPanel, BorderLayout.NORTH);

        // CENTER: Calendar Grid
        this.calendarGridPanel = new JPanel(new GridLayout(0, 7, 5, 5)); // 0 rows means it will adjust
        this.calendarGridPanel.setBackground(BACKGROUND_COLOR);
        this.calendarGridPanel.setBorder(new EmptyBorder(5, 0, 5, 0)); // Reduced padding around grid
        add(this.calendarGridPanel, BorderLayout.CENTER);

        // SOUTH: Events Area
        this.southEventsPanel = createSouthEventsPanel();
        add(this.southEventsPanel, BorderLayout.SOUTH);        this.taskController = new TaskController();
        this.eventController = new CalendarEventController(); // 初始化行程 Controller

        // 只創建UI佈局，但暫不載入數據
        initEmptyCalendarView(); // 初始化空的行事曆視圖
    }
    
    // 初始化空的行事曆視圖（不獲取任何資料）
    private void initEmptyCalendarView() {
        calendarGridPanel.removeAll();
        monthTitleLabel.setText(displayedMonthFirstDay.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));

        // 添加星期幾標題
        String[] dayNames = {"S", "M", "T", "W", "T", "F", "S"};
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setFont(DAY_HEADER_FONT);
            dayLabel.setForeground(DAY_TEXT_COLOR);
            calendarGridPanel.add(dayLabel);
        }

        eventListDisplayPanel.removeAll();
        JLabel waitingLabel = new JLabel("請先登入以查看行事曆事件");
        waitingLabel.setFont(TASK_DETAIL_FONT);
        waitingLabel.setForeground(DAY_TEXT_COLOR);
        waitingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        waitingLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        eventListDisplayPanel.add(waitingLabel);
        
        calendarGridPanel.revalidate();
        calendarGridPanel.repaint();
        eventListDisplayPanel.revalidate();
        eventListDisplayPanel.repaint();
    }

    private JPanel createMonthHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND_COLOR);
        header.setBorder(new EmptyBorder(0, 0, 10, 0)); // Bottom padding for header

        JButton prevMonthButton = createNavButton("<");
        prevMonthButton.addActionListener(e -> {
            displayedMonthFirstDay = displayedMonthFirstDay.minusMonths(1);
            currentYearMonth = currentYearMonth.minusMonths(1);
            // If selected date was in the previous month, keep it, otherwise select 1st
            if (!YearMonth.from(currentSelectedDate).equals(currentYearMonth)) {
                 // currentSelectedDate = displayedMonthFirstDay; // Select 1st of new month
            }
            updateCalendarView();
        });

        JButton nextMonthButton = createNavButton(">");
        nextMonthButton.addActionListener(e -> {
            displayedMonthFirstDay = displayedMonthFirstDay.plusMonths(1);
            currentYearMonth = currentYearMonth.plusMonths(1);
            if (!YearMonth.from(currentSelectedDate).equals(currentYearMonth)) {
                // currentSelectedDate = displayedMonthFirstDay; // Select 1st of new month
            }
            updateCalendarView();
        });

        monthTitleLabel = new JLabel("", SwingConstants.CENTER);
        monthTitleLabel.setFont(MONTH_YEAR_FONT);
        monthTitleLabel.setForeground(HEADER_TEXT_COLOR);
        // Allow clicking month label to jump (optional, from old CalendarPanel)
        monthTitleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String input = JOptionPane.showInputDialog(CalendarPanel.this, "Enter Year-Month (YYYY-MM):", displayedMonthFirstDay.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                if (input != null) {
                    try {
                        YearMonth ymInput = YearMonth.parse(input, DateTimeFormatter.ofPattern("yyyy-MM"));
                        displayedMonthFirstDay = ymInput.atDay(1);
                        currentYearMonth = ymInput;
                        // currentSelectedDate = displayedMonthFirstDay; // Select 1st of new month
                        updateCalendarView();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(CalendarPanel.this, "Invalid format. Please use YYYY-MM.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        header.add(prevMonthButton, BorderLayout.WEST);
        header.add(monthTitleLabel, BorderLayout.CENTER);
        header.add(nextMonthButton, BorderLayout.EAST);
        return header;
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(SELECTED_DATE_BG_COLOR);
        return button;
    }

    private JPanel createSouthEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 5)); // Gap between header and list
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        // Increase preferred height for the south panel (event area)
        panel.setPreferredSize(new Dimension(0, 300)); // Increased height from 220 to 300

        // Header for the south panel (Details for... and +Add Event)
        JPanel southHeader = new JPanel(new BorderLayout());
        southHeader.setBackground(BACKGROUND_COLOR);
        southHeader.setBorder(new EmptyBorder(10, 5, 10, 5)); // Padding

        detailDateLabel = new JLabel("Details for...");
        detailDateLabel.setFont(TASK_DETAIL_FONT);
        detailDateLabel.setForeground(HEADER_TEXT_COLOR);
        southHeader.add(detailDateLabel, BorderLayout.WEST);

        JButton addEventButton = new JButton("+ Add Event");
        addEventButton.setFont(ADD_BUTTON_FONT);
        addEventButton.setForeground(SELECTED_DATE_BG_COLOR);
        addEventButton.setBackground(Color.WHITE); // Match pill style background
        addEventButton.setFocusPainted(false);
        addEventButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SELECTED_DATE_BG_COLOR.brighter(), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        addEventButton.addActionListener(e -> openAddEventDialog());
        southHeader.add(addEventButton, BorderLayout.EAST);

        panel.add(southHeader, BorderLayout.NORTH);

        // Event list display area
        eventListDisplayPanel = new JPanel();
        eventListDisplayPanel.setLayout(new BoxLayout(eventListDisplayPanel, BoxLayout.Y_AXIS));
        eventListDisplayPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(eventListDisplayPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // No border for scrollpane itself
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        // Remove fixed preferred size for JScrollPane, it will now fill the center.
        // Remove the scrollPaneWrapper, add scrollPane directly to the panel's center.
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }    private void updateCalendarView() {
        // 檢查用戶是否已初始化
        if (!userInitialized || currentUser == null) {
            System.err.println("尚未設置用戶，顯示空行事曆視圖");
            initEmptyCalendarView();
            return;
        }

        // 每次更新都先清理並重新載入任務及行程
        dailyEvents.clear();
        loadTasks();

        // 載入當月所有行程
        System.out.println("正在為用戶 " + currentUser.getUid() + " 載入行事曆事件...");
        int daysInMonth = displayedMonthFirstDay.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = displayedMonthFirstDay.withDayOfMonth(day);
            try {
                List<CalendarEvent> events = eventController.getEventsByDate(date);
                System.out.println(date + ": 找到 " + events.size() + " 個事件");
                for (CalendarEvent evt : events) {
                    dailyEvents.computeIfAbsent(date, k -> new ArrayList<>()).add(evt);
                }
            } catch (Exception e) {
                System.err.println("載入日期 " + date + " 的事件時發生錯誤: " + e.getMessage());
                e.printStackTrace();
            }
        }

        calendarGridPanel.removeAll();
        monthTitleLabel.setText(displayedMonthFirstDay.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));

        // Add day headers (S, M, T, W, T, F, S)
        String[] dayNames = {"S", "M", "T", "W", "T", "F", "S"}; // Sunday first
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setFont(DAY_HEADER_FONT);
            dayLabel.setForeground(DAY_TEXT_COLOR);
            calendarGridPanel.add(dayLabel);
        }

        LocalDate firstDayOfDisplayedMonth = displayedMonthFirstDay;
        int dayOfWeekOffset = firstDayOfDisplayedMonth.getDayOfWeek().getValue(); // MON=1, SUN=7
        if (dayOfWeekOffset == 7) dayOfWeekOffset = 0; // Adjust Sunday to be 0 for 0-indexed offset

        for (int i = 0; i < dayOfWeekOffset; i++) {
            calendarGridPanel.add(new JLabel("")); // Empty cell for offset
        }        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = displayedMonthFirstDay.withDayOfMonth(day);
            
            // 檢查該日期是否有任務和事件
            boolean hasAnyEvents = dailyEvents.containsKey(date) && !dailyEvents.get(date).isEmpty();
            boolean hasTask = false;
            boolean hasEvent = false;
            
            if (hasAnyEvents) {
                List<CalendarEvent> eventsOnDate = dailyEvents.get(date);
                for (CalendarEvent event : eventsOnDate) {
                    if (event.isTask()) {
                        hasTask = true;
                    } else {
                        hasEvent = true;
                    }
                    // 如果兩種類型都有了，就不需要繼續檢查
                    if (hasTask && hasEvent) {
                        break;
                    }
                }
            }
            
            DateCellButton dayCellButton = new DateCellButton(String.valueOf(day), date, hasEvent, hasTask);

            // Highlight selected date
            if (date.equals(currentSelectedDate)) {
                dayCellButton.setBackground(SELECTED_DATE_BG_COLOR);
                dayCellButton.setForeground(SELECTED_DATE_FG_COLOR);
            } else {
                dayCellButton.setBackground(BACKGROUND_COLOR); // Default background
                dayCellButton.setForeground(DAY_TEXT_COLOR);   // Default foreground
                if (date.equals(LocalDate.now())) { // Mark today if not selected
                    dayCellButton.setBorder(BorderFactory.createLineBorder(TODAY_BORDER_COLOR, 2));
                } else {
                     dayCellButton.setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); // Ensure default border if not today
                }
            }

            dayCellButton.addActionListener(e -> {
                currentSelectedDate = date;
                updateCalendarView(); // Redraw to update selection and event list
            });
            calendarGridPanel.add(dayCellButton);
        }

        // Fill remaining cells to complete the grid (usually 6 weeks display)
        int totalCellsInGrid = 7 * (1 + 6); // 1 row for day names, 6 for dates
        int currentCellCount = calendarGridPanel.getComponentCount();
        for (int i = currentCellCount; i < totalCellsInGrid; i++) {
            calendarGridPanel.add(new JLabel(""));
        }

        calendarGridPanel.revalidate();
        calendarGridPanel.repaint();

        refreshEventsDisplay(); // Update the south panel
    }

    private void refreshEventsDisplay() {
        eventListDisplayPanel.removeAll();
        detailDateLabel.setText("Details for " + currentSelectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy", Locale.ENGLISH)));

        List<CalendarEvent> eventsForDay = dailyEvents.getOrDefault(currentSelectedDate, new ArrayList<>());
        eventsForDay.sort((e1, e2) -> {
            if (e1.getTime() == null && e2.getTime() == null) return 0;
            if (e1.getTime() == null) return -1;
            if (e2.getTime() == null) return 1;
            return e1.getTime().compareTo(e2.getTime());
        });

        if (eventsForDay.isEmpty()) {
            JLabel noEventsLabel = new JLabel("No events or tasks for this day.");
            noEventsLabel.setFont(TASK_DETAIL_FONT);
            noEventsLabel.setForeground(DAY_TEXT_COLOR);
            noEventsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noEventsLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
            eventListDisplayPanel.add(noEventsLabel);
        } else {
            for (CalendarEvent event : eventsForDay) {
                EventItemPanel itemPanel = new EventItemPanel(
                    currentSelectedDate,
                    event,
                    updatedEvent -> {
                        eventController.updateEvent(updatedEvent);
                        updateCalendarView();
                    },
                    () -> {
                        eventController.deleteEvent(event.getId());
                        updateCalendarView();
                    }
                );
                eventListDisplayPanel.add(itemPanel);
                eventListDisplayPanel.add(Box.createVerticalStrut(2));
            }
        }

        eventListDisplayPanel.revalidate();
        eventListDisplayPanel.repaint();
        if (southEventsPanel.getComponentCount() > 1 && southEventsPanel.getComponent(1) instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) southEventsPanel.getComponent(1);
            sp.revalidate();
            sp.repaint();
        }
    }

    // Existing openAddEventDialog - ensure it updates correctly
    private void openAddEventDialog() {
        // 檢查用戶是否已登入
        if (!userInitialized || currentUser == null) {
            JOptionPane.showMessageDialog(this, 
                "請先登入後再添加事件", 
                "需要登入", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        EventFormDialog dialog = new EventFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            currentSelectedDate,
            newEvent -> {
                // 設置用戶ID並使用 Controller 新增到 DB
                newEvent.setUserId(currentUser.getUid());
                CalendarEvent addedEvent = eventController.addEvent(currentSelectedDate, newEvent);
                if (addedEvent != null) {
                    System.out.println("事件添加成功: ID=" + addedEvent.getId() + ", 用戶ID=" + addedEvent.getUserId());
                    updateCalendarView();
                } else {
                    System.err.println("事件添加失敗");
                    JOptionPane.showMessageDialog(this, 
                        "添加事件失敗，請稍後再試", 
                        "錯誤", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        );
        dialog.setVisible(true);
    }

    // 載入任務
    private void loadTasks() {
        // 檢查用戶是否已初始化
        if (!userInitialized || currentUser == null) {
            System.err.println("尚未設置用戶，跳過任務載入");
            return;
        }

        // 移除先前載入的所有任務事件
        for (List<CalendarEvent> events : dailyEvents.values()) {
            events.removeIf(CalendarEvent::isTask);
        }
        
        try {
            System.out.println("正在為用戶 " + currentUser.getUid() + " 載入任務...");
            List<Task> tasks = taskController.getAllTasks(); // 這裡會獲取當前登入用戶的任務
            System.out.println("找到 " + tasks.size() + " 個任務");
            
            for (Task t : tasks) {
                if (t.getDueTime() != null) { // Ensure task has a due date/time
                    LocalDate date = t.getDueTime().toLocalDate();
                    LocalTime time = t.getDueTime().toLocalTime();
                    CalendarEvent evt = new CalendarEvent(time, t.getTitle(), true, currentUser.getUid()); // 設置用戶ID
                    dailyEvents.computeIfAbsent(date, k -> new ArrayList<>()).add(evt);
                }
            }
        } catch (Exception e) {
            System.err.println("載入任務時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }    // Inner class for custom date button
    private class DateCellButton extends JButton {
        private boolean hasEvent;
        private boolean hasTask;
        @SuppressWarnings("unused") // 保留以備將來使用
        private LocalDate date;

        public DateCellButton(String text, LocalDate date, boolean hasEvent, boolean hasTask) {
            super(text);
            this.date = date;
            this.hasEvent = hasEvent;
            this.hasTask = hasTask;
            setFont(DATE_FONT);
            setFocusPainted(false);
            setOpaque(true); 
            setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); // Default border
            setHorizontalAlignment(SwingConstants.CENTER); // Center the date number
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Draw the button text and background

            if (hasEvent || hasTask) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int dotDiameter = 6;
                int y = getHeight() - dotDiameter - 4; // 4 pixels from the bottom edge

                if (hasEvent && hasTask) {
                    // 如果同時有事件和任務，顯示兩個點
                    int leftX = (getWidth() / 2) - dotDiameter - 2;
                    int rightX = (getWidth() / 2) + 2;
                    
                    // 左邊顯示事件點 (藍色)
                    g2.setColor(SELECTED_DATE_BG_COLOR);
                    g2.fillOval(leftX, y, dotDiameter, dotDiameter);
                    
                    // 右邊顯示任務點 (橘色)
                    g2.setColor(new Color(200, 100, 50));
                    g2.fillOval(rightX, y, dotDiameter, dotDiameter);
                } else if (hasTask) {
                    // 只有任務，顯示橘色點
                    g2.setColor(new Color(200, 100, 50));
                    int x = (getWidth() - dotDiameter) / 2;
                    g2.fillOval(x, y, dotDiameter, dotDiameter);
                } else if (hasEvent) {
                    // 只有事件，顯示藍色點
                    g2.setColor(SELECTED_DATE_BG_COLOR);
                    int x = (getWidth() - dotDiameter) / 2;
                    g2.fillOval(x, y, dotDiameter, dotDiameter);
                }

                g2.dispose();
            }
        }
    }

    // 設置當前用戶
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            System.out.println("CalendarPanel 設置用戶: ID=" + user.getUid() + ", Email=" + user.getEmail());
            taskController.setCurrentUser(user);
            eventController.setCurrentUser(user);
            userInitialized = true; // 標記用戶已初始化
            refreshCalendar(); // 設置用戶後刷新日曆
        } else {
            System.err.println("警告：嘗試設置空用戶到 CalendarPanel");
            userInitialized = false;
            initEmptyCalendarView(); // 如果用戶為 null，顯示空視圖
        }
    }

    // 刷新日曆視圖
    private void refreshCalendar() {
        if (userInitialized && currentUser != null) {
            dailyEvents.clear(); // 清除先前的事件
            loadTasks(); // 重新載入任務
            updateCalendarView(); // 更新視圖
        } else {
            System.err.println("尚未登入，無法刷新日曆");
            initEmptyCalendarView();
        }
    }

    // 創建圖例面板
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        legendPanel.setBackground(BACKGROUND_COLOR);
        legendPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        // 事件圖例
        JPanel eventLegend = createLegendItem(SELECTED_DATE_BG_COLOR, "Events");
        
        // 任務圖例
        JPanel taskLegend = createLegendItem(new Color(200, 100, 50), "Tasks");
        
        // 混合圖例
        JLabel mixedLabel = new JLabel("Both: ");
        mixedLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        mixedLabel.setForeground(DAY_TEXT_COLOR);
        
        // 創建混合圖例的雙點
        JPanel mixedDots = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int dotSize = 6;
                int y = (getHeight() - dotSize) / 2;
                
                // 左邊藍點 (事件)
                g2.setColor(SELECTED_DATE_BG_COLOR);
                g2.fillOval(2, y, dotSize, dotSize);
                
                // 右邊橘點 (任務)
                g2.setColor(new Color(200, 100, 50));
                g2.fillOval(12, y, dotSize, dotSize);
                
                g2.dispose();
            }
        };
        mixedDots.setPreferredSize(new Dimension(20, 12));
        mixedDots.setOpaque(false);
        
        JPanel mixedLegend = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        mixedLegend.setOpaque(false);
        mixedLegend.add(mixedLabel);
        mixedLegend.add(mixedDots);

        legendPanel.add(eventLegend);
        legendPanel.add(taskLegend);
        legendPanel.add(mixedLegend);
        
        return legendPanel;
    }
    
    // 創建單個圖例項目
    private JPanel createLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        item.setOpaque(false);
        
        // 創建色點
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                
                int dotSize = 6;
                int x = (getWidth() - dotSize) / 2;
                int y = (getHeight() - dotSize) / 2;
                g2.fillOval(x, y, dotSize, dotSize);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 12));
        dot.setOpaque(false);
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(DAY_TEXT_COLOR);
        
        item.add(dot);
        item.add(label);
        
        return item;
    }
}
