package com.campus.ev.ui.dialogs;

import com.campus.ev.ui.MainFrame;
import com.campus.ev.util.UIHelper;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AboutDialog extends JDialog {

    public AboutDialog(MainFrame mainFrame) {
        super(mainFrame, "About Smart Campus EV Control Center", true);

        setSize(540, 440);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_CARD);
        setLayout(new BorderLayout(12, 12));

        // HEADER
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 6, 20));

        JLabel lblTitle = new JLabel("⚡ Smart Campus EV Charging Control Center");
        lblTitle.setFont(UITheme.FONT_HEADER_MED);
        lblTitle.setForeground(UITheme.ACCENT_CYAN);

        JLabel lblSub = new JLabel("Java AWT/Swing + JDBC + MySQL Academic Project");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        header.add(lblTitle);
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // BODY TEXT
        JTextArea txt = new JTextArea(
            "SYSTEM ARCHITECTURE & COURSEWORK COMPLIANCE:\n" +
            "• Pure Java Swing / AWT: Custom Dark Control Room Theme\n" +
            "• JDBC Layer: Full implementation of Statement, PreparedStatement & CallableStatement\n" +
            "• MySQL 8.0+: Relational schema with foreign keys, indexes, and stored procedures\n" +
            "• Transaction Safety: Atomic commit & rollback on charging session completion\n\n" +
            "SUSTAINABLE DEVELOPMENT GOALS (SDGs):\n" +
            "• SDG 7 (Affordable & Clean Energy): Real-time solar rooftop grid allocation & tariff\n" +
            "• SDG 9 (Industry, Innovation & Infrastructure): Smart charging dispenser telemetry\n" +
            "• SDG 11 (Sustainable Cities & Communities): Zero campus emissions & green carbon offset\n\n" +
            "TEAM CONTRIBUTIONS:\n" +
            "• Member 1: GUI Architecture, Swing Layouts, Custom Map Graphics & Node Inspector\n" +
            "• Member 2: Relational Database Schema, Normalization, JDBC DAO & Stored Procedures\n" +
            "• Member 3: Business Services, Reservation Conflict Engine, Transaction Safety & Reporting"
        );
        txt.setEditable(false);
        txt.setFont(UITheme.FONT_REGULAR);
        txt.setForeground(UITheme.TEXT_PRIMARY);
        txt.setBackground(UITheme.BG_INPUT);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            new EmptyBorder(12, 14, 12, 14)
        ));
        add(txt, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);
        JButton btnClose = UIHelper.createPrimaryButton("Close");
        btnClose.addActionListener(e -> dispose());
        footer.add(btnClose);
        add(footer, BorderLayout.SOUTH);
    }
}
