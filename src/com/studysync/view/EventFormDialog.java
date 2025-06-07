package com.studysync.view;

import com.studysync.model.CalendarEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class EventFormDialog extends JDialog {
    private final JSpinner timeSpinner;
    private final JTextField descriptionField = new JTextField(20);
    private final JButton saveButton = new JButton("Save");
    private final JButton deleteButton = new JButton("Delete");
    private final CalendarEvent editingEvent;
    private final LocalDate eventDate;
    private final java.util.function.Consumer<CalendarEvent> onSave;
    private final Runnable onDelete;

    // Constructor for new event
    public EventFormDialog(Frame parent, LocalDate date,
                           java.util.function.Consumer<CalendarEvent> onSave) {
        this(parent, date, null, onSave, null);
    }

    // Constructor for editing existing event
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
