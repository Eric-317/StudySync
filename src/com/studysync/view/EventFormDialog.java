package com.studysync.view;

import com.studysync.model.CalendarEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * 事件表單對話框類
 * 提供新增和編輯日曆事件的介面
 * 
 * 主要功能：
 * - 新增新的日曆事件
 * - 編輯現有的日曆事件
 * - 設置事件時間和描述
 * - 刪除現有事件
 * - 表單驗證和資料處理
 * 
 * UI 組件：
 * - 時間選擇器（小時:分鐘格式）
 * - 事件描述輸入欄位
 * - 儲存和刪除按鈕
 * 
 * @author StudySync Team
 * @version 1.0
 */
public class EventFormDialog extends JDialog {
    /** 時間選擇器組件 */
    private final JSpinner timeSpinner;
    
    /** 事件描述輸入欄位 */
    private final JTextField descriptionField = new JTextField(20);
    
    /** 儲存按鈕 */
    private final JButton saveButton = new JButton("Save");
    
    /** 刪除按鈕 */
    private final JButton deleteButton = new JButton("Delete");
    
    /** 正在編輯的事件（null表示新增事件） */
    private final CalendarEvent editingEvent;
    
    /** 事件日期 */
    private final LocalDate eventDate;
    
    /** 儲存事件的回調函數 */
    private final java.util.function.Consumer<CalendarEvent> onSave;
    
    /** 刪除事件的回調函數 */
    private final Runnable onDelete;

    /**
     * 建構新事件對話框
     * 
     * @param parent 父視窗
     * @param date 事件日期
     * @param onSave 儲存事件的回調函數
     */
    public EventFormDialog(Frame parent, LocalDate date,
                           java.util.function.Consumer<CalendarEvent> onSave) {
        this(parent, date, null, onSave, null);
    }

    /**
     * 建構編輯事件對話框
     * 
     * @param parent 父視窗
     * @param date 事件日期
     * @param event 要編輯的事件（null表示新增事件）
     * @param onSave 儲存事件的回調函數
     * @param onDelete 刪除事件的回調函數
     */
    public EventFormDialog(Frame parent, LocalDate date,
                           CalendarEvent event,
                           java.util.function.Consumer<CalendarEvent> onSave,
                           Runnable onDelete) {
        super(parent, event == null ? "Add Event" : "Edit Event", true);
        this.editingEvent = event;
        this.eventDate = date;
        this.onSave = onSave;
        this.onDelete = onDelete;
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Time
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Time (HH:mm):"), gbc);
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(editor);
        if (event != null && event.getTime() != null) {
            Date dateVal = Date.from(event.getTime().atDate(LocalDate.now())
                .atZone(java.time.ZoneId.systemDefault()).toInstant());
            timeSpinner.setValue(dateVal);
        }
        gbc.gridx = 1;
        form.add(timeSpinner, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        if (event != null) {
            descriptionField.setText(event.getDescription());
        }
        form.add(descriptionField, gbc);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(saveButton);
        if (event != null) {
            buttons.add(deleteButton);
        }
        add(buttons, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            Date dateVal = (Date) timeSpinner.getValue();
            LocalTime time = dateVal.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalTime();
            String desc = descriptionField.getText().trim();
            if (editingEvent == null) {
                CalendarEvent newEvent = new CalendarEvent(time, desc, false);
                onSave.accept(newEvent);
            } else {
                editingEvent.setTime(time);
                editingEvent.setDescription(desc);
                onSave.accept(editingEvent);
            }
            dispose();
        });

        if (event != null && onDelete != null) {
            deleteButton.addActionListener(e -> {
                onDelete.run();
                dispose();
            });
        }
    }
}
