package com.campus.ev.util;

import java.awt.Color;
import java.awt.Font;

public class UITheme {

    // Backgrounds & Surfaces (Dark Control Center Palette)
    public static final Color BG_DARKEST = new Color(11, 15, 25);     // #0b0f19
    public static final Color BG_PANEL   = new Color(15, 23, 42);     // #0f172a (Slate 900)
    public static final Color BG_CARD    = new Color(24, 34, 56);     // #182238 (Slate 850)
    public static final Color BG_INPUT   = new Color(30, 41, 59);     // #1e293b (Slate 800)
    public static final Color BG_HOVER   = new Color(45, 59, 84);     // #2d3b54
    public static final Color BORDER     = new Color(71, 85, 105);    // #475569 (Slate 600)
    public static final Color BORDER_GLOW= new Color(6, 182, 212, 120);

    // Accent Colors (High Contrast & Vibrant)
    public static final Color ACCENT_CYAN    = new Color(56, 189, 248);  // #38bdf8 (Bright Sky Cyan)
    public static final Color ACCENT_EMERALD = new Color(52, 211, 153);  // #34d399 (Bright Emerald Green)
    public static final Color ACCENT_AMBER   = new Color(251, 191, 36);  // #fbbf24 (Bright Amber Gold)
    public static final Color ACCENT_ROSE    = new Color(251, 113, 133); // #fb7185 (Bright Coral Rose)
    public static final Color ACCENT_BLUE    = new Color(96, 165, 250);  // #60a5fa (Bright Blue)
    public static final Color ACCENT_PURPLE  = new Color(192, 132, 252); // #c084fc (Bright Violet Purple)

    // Text Colors (High Readability & Clear Contrast)
    public static final Color TEXT_PRIMARY   = new Color(255, 255, 255); // #ffffff Pure Crisp White
    public static final Color TEXT_SECONDARY = new Color(226, 232, 240); // #e2e8f0 Bright Slate White
    public static final Color TEXT_MUTED     = new Color(178, 190, 205); // #b2becd Clean Legible Light Gray
    public static final Color TEXT_CYAN      = new Color(103, 232, 249); // #67e8f9 High-glow Cyan

    // Status Colors
    public static Color getStatusColor(String status) {
        if (status == null) return TEXT_MUTED;
        switch (status.toUpperCase()) {
            case "AVAILABLE":
            case "ACTIVE":
            case "CONFIRMED":
            case "PAID":
            case "OPERATIONAL":
                return ACCENT_EMERALD;
            case "OCCUPIED":
            case "CHARGING":
            case "CHECKED_IN":
                return ACCENT_CYAN;
            case "RESERVED":
            case "PENDING":
            case "PAUSED":
                return ACCENT_AMBER;
            case "MAINTENANCE":
            case "OFFLINE":
            case "SUSPENDED":
            case "CANCELLED":
            case "FAULT_STOPPED":
                return ACCENT_ROSE;
            case "COMPLETED":
                return ACCENT_BLUE;
            default:
                return TEXT_SECONDARY;
        }
    }

    public static Color getStatusBgColor(String status) {
        Color base = getStatusColor(status);
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), 45);
    }

    // Typography (Significantly Enlarged & High-DPI Friendly)
    private static final String FONT_FAMILY = "Segoe UI";

    public static final Font FONT_HEADER_LARGE = new Font(FONT_FAMILY, Font.BOLD, 26);
    public static final Font FONT_HEADER_MED   = new Font(FONT_FAMILY, Font.BOLD, 19);
    public static final Font FONT_HEADER_SMALL = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font FONT_TELEMETRY    = new Font("Consolas", Font.BOLD, 24);
    public static final Font FONT_TELEMETRY_SM = new Font("Consolas", Font.BOLD, 17);
    public static final Font FONT_REGULAR_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_REGULAR      = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_SMALL        = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_SMALL_BOLD   = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_MONO_CODE    = new Font("Consolas", Font.PLAIN, 14);
}
