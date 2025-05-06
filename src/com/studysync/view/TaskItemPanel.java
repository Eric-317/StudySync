package com.studysync.view;

import com.studysync.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class TaskItemPanel extends JPanel {
    private final Color studyingColor = new Color(102, 153, 255);
    private final Color homeworkColor = new Color(102, 204, 153);
    private final Color writingColor = new Color(255, 204, 102);
    private final Color meetingColor = new Color(178, 153, 255);

    public TaskItemPanel(Task task) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel circle = new JLabel("●");
        circle.setFont(new Font("Dialog", Font.PLAIN, 14));
        circle.setForeground(getColorByCategory(task.getCategory()));

        JLabel title = new JLabel(task.getTitle());
        title.setFont(new Font("Dialog", Font.BOLD, 14));

        String timeStr = task.isToday()
                ? "Today, " + task.getDueTime().format(DateTimeFormatter.ofPattern("h:mm a"))
                : task.getDueTime().format(DateTimeFormatter.ofPattern("MMM d"));

        JLabel timeLabel = new JLabel(timeStr);
        timeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(100, 100, 100));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(title);
        infoPanel.add(timeLabel);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(circle);
        leftPanel.add(infoPanel);

        JLabel tag = new JLabel(task.getCategory());
        tag.setOpaque(true);
        tag.setBackground(getColorByCategory(task.getCategory()));
        tag.setForeground(Color.WHITE);
        tag.setFont(new Font("Dialog", Font.PLAIN, 12));
        tag.setBorder(new EmptyBorder(3, 8, 3, 8));

        add(leftPanel, BorderLayout.WEST);
        add(tag, BorderLayout.EAST);
    }

    private Color getColorByCategory(String cat) {
        return switch (cat.toLowerCase()) {
            case "studying" -> studyingColor;
            case "homework" -> homeworkColor;
            case "writing" -> writingColor;
            case "meeting" -> meetingColor;
            default -> new Color(180, 180, 180);
        };
    }
}
