package com.campus.ev.ui.grid;

import com.campus.ev.model.ChargingPoint;
import com.campus.ev.model.ChargingStation;
import com.campus.ev.util.UITheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class CampusMapCanvas extends JPanel {

    private List<ChargingStation> stations = new ArrayList<>();
    private List<ChargingPoint> points = new ArrayList<>();
    private String activeFilter = "ALL";
    private ChargingPoint selectedPoint = null;
    private Consumer<ChargingPoint> onPointSelected;

    // Station Layout Coordinates relative to canvas percentage
    private static class StationLayout {
        String code;
        double relX, relY;
        StationLayout(String code, double relX, double relY) {
            this.code = code;
            this.relX = relX;
            this.relY = relY;
        }
    }

    private final Map<String, StationLayout> layoutMap = new HashMap<>();
    private final Map<Rectangle, ChargingPoint> clickableNodes = new HashMap<>();

    public CampusMapCanvas() {
        setBackground(UITheme.BG_CARD);
        setOpaque(true);

        // Pre-defined spatial campus coordinates
        layoutMap.put("STN-MAIN-01",  new StationLayout("STN-MAIN-01", 0.32, 0.48)); // Main Academic Block (Center)
        layoutMap.put("STN-LIB-02",   new StationLayout("STN-LIB-02",  0.50, 0.18)); // Central Library (North)
        layoutMap.put("STN-HSTL-03",  new StationLayout("STN-HSTL-03", 0.28, 0.82)); // Hostel Zone (South)
        layoutMap.put("STN-RSRCH-04", new StationLayout("STN-RSRCH-04",0.12, 0.22)); // Research Park (West)
        layoutMap.put("STN-SPRT-05",  new StationLayout("STN-SPRT-05", 0.82, 0.22)); // Sports Complex (East)
        layoutMap.put("STN-PARK-06",  new StationLayout("STN-PARK-06", 0.72, 0.72)); // Multi-Level Parking (South-East)

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Map.Entry<Rectangle, ChargingPoint> entry : clickableNodes.entrySet()) {
                    if (entry.getKey().contains(e.getPoint())) {
                        selectedPoint = entry.getValue();
                        repaint();
                        if (onPointSelected != null) {
                            onPointSelected.accept(selectedPoint);
                        }
                        return;
                    }
                }
            }
        });
    }

    public void setOnPointSelected(Consumer<ChargingPoint> callback) {
        this.onPointSelected = callback;
    }

    public void setData(List<ChargingStation> stations, List<ChargingPoint> points) {
        this.stations = stations != null ? stations : new ArrayList<>();
        this.points = points != null ? points : new ArrayList<>();
        repaint();
    }

    public void setFilter(String filter) {
        this.activeFilter = filter != null ? filter : "ALL";
        repaint();
    }

    public void setSelectedPoint(ChargingPoint cp) {
        this.selectedPoint = cp;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        clickableNodes.clear();
        int width = getWidth();
        int height = getHeight();

        // 1. Draw Subtle Grid Lines & Background Pattern
        g2.setColor(new Color(255, 255, 255, 6));
        for (int x = 0; x < width; x += 40) {
            g2.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += 40) {
            g2.drawLine(0, y, width, y);
        }

        // 2. Draw Inter-Hub Energy Grid Cables / Pathways
        drawGridPathways(g2, width, height);

        // 3. Draw Station Hubs and Nodes
        for (ChargingStation stn : stations) {
            StationLayout layout = layoutMap.get(stn.getStationCode());
            int cx = layout != null ? (int)(layout.relX * width) : (int)(0.5 * width);
            int cy = layout != null ? (int)(layout.relY * height) : (int)(0.5 * height);

            // Filter points for this station
            List<ChargingPoint> stnPoints = new ArrayList<>();
            for (ChargingPoint p : points) {
                if (p.getStationId() == stn.getStationId()) {
                    if (activeFilter.equalsIgnoreCase("ALL") || p.getStatus().equalsIgnoreCase(activeFilter)) {
                        stnPoints.add(p);
                    }
                }
            }

            drawStationHub(g2, stn, cx, cy, stnPoints);
        }

        // 4. Legend at bottom-left
        drawLegend(g2, height);

        g2.dispose();
    }

    private void drawGridPathways(Graphics2D g2, int w, int h) {
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{6.0f, 6.0f}, 0.0f));
        g2.setColor(new Color(6, 182, 212, 50));

        StationLayout main = layoutMap.get("STN-MAIN-01");
        if (main != null) {
            int mx = (int)(main.relX * w);
            int my = (int)(main.relY * h);

            for (StationLayout other : layoutMap.values()) {
                if (other != main) {
                    int ox = (int)(other.relX * w);
                    int oy = (int)(other.relY * h);
                    g2.drawLine(mx, my, ox, oy);
                }
            }
        }
    }

    private void drawStationHub(Graphics2D g2, ChargingStation stn, int cx, int cy, List<ChargingPoint> stnPoints) {
        int nodeCount = Math.max(1, stnPoints.size());
        int hubW = Math.max(160, nodeCount * 46 + 24);
        int hubH = 82;
        int x = cx - hubW / 2;
        int y = cy - hubH / 2;

        // Station Hub Container Box
        g2.setColor(new Color(15, 23, 42, 220));
        g2.fill(new RoundRectangle2D.Float(x, y, hubW, hubH, 12, 12));

        g2.setColor(stn.isSolarPowered() ? UITheme.ACCENT_PURPLE : UITheme.BORDER);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(x, y, hubW, hubH, 12, 12));

        // Station Header Text
        g2.setFont(UITheme.FONT_HEADER_SMALL);
        g2.setColor(stn.isSolarPowered() ? UITheme.ACCENT_PURPLE : UITheme.TEXT_CYAN);
        String solarTag = stn.isSolarPowered() ? " [☀️ SOLAR]" : "";
        g2.drawString(stn.getStationName() + solarTag, x + 10, y + 20);

        g2.setFont(UITheme.FONT_SMALL);
        g2.setColor(UITheme.TEXT_SECONDARY);
        g2.drawString(stn.getCampusZone() + " • " + stn.getMaxGridCapacityKw() + "kW", x + 10, y + 36);

        // Draw Interactive Charging Point Nodes
        int startX = x + 12;
        int nodeY = y + 42;
        int nodeW = 44;
        int nodeH = 38;

        for (int i = 0; i < stnPoints.size(); i++) {
            ChargingPoint cp = stnPoints.get(i);
            int nodeX = startX + (i * 48);
            Rectangle nodeBounds = new Rectangle(nodeX, nodeY, nodeW, nodeH);
            clickableNodes.put(nodeBounds, cp);

            boolean isSelected = (selectedPoint != null && selectedPoint.getPointId() == cp.getPointId());
            Color statusCol = UITheme.getStatusColor(cp.getStatus());

            // Node Background
            g2.setColor(new Color(statusCol.getRed(), statusCol.getGreen(), statusCol.getBlue(), isSelected ? 80 : 35));
            g2.fill(new RoundRectangle2D.Float(nodeX, nodeY, nodeW, nodeH, 8, 8));

            // Node Border (highlight if selected)
            g2.setColor(isSelected ? Color.WHITE : statusCol);
            g2.setStroke(new BasicStroke(isSelected ? 2.5f : 1.2f));
            g2.draw(new RoundRectangle2D.Float(nodeX, nodeY, nodeW, nodeH, 8, 8));

            // Point Label (CP-XX)
            g2.setFont(UITheme.FONT_REGULAR_BOLD);
            g2.setColor(isSelected ? Color.WHITE : statusCol);
            FontMetrics fm = g2.getFontMetrics();
            int tx = nodeX + (nodeW - fm.stringWidth(cp.getPointCode())) / 2;
            g2.drawString(cp.getPointCode(), tx, nodeY + 18);

            // Power Rating Sub-label
            g2.setFont(UITheme.FONT_SMALL_BOLD);
            g2.setColor(UITheme.TEXT_SECONDARY);
            String pwr = (int)cp.getPowerRatingKw() + "k";
            FontMetrics fmPwr = g2.getFontMetrics();
            int px = nodeX + (nodeW - fmPwr.stringWidth(pwr)) / 2;
            g2.drawString(pwr, px, nodeY + 32);
        }
    }

    private void drawLegend(Graphics2D g2, int height) {
        int lx = 16;
        int ly = height - 26;

        g2.setFont(UITheme.FONT_REGULAR_BOLD);
        
        drawLegendItem(g2, lx, ly, "AVAILABLE", UITheme.ACCENT_EMERALD);
        drawLegendItem(g2, lx + 120, ly, "OCCUPIED", UITheme.ACCENT_CYAN);
        drawLegendItem(g2, lx + 240, ly, "RESERVED", UITheme.ACCENT_AMBER);
        drawLegendItem(g2, lx + 360, ly, "MAINTENANCE", UITheme.ACCENT_ROSE);
        drawLegendItem(g2, lx + 500, ly, "☀️ SOLAR HUB", UITheme.ACCENT_PURPLE);
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, String label, Color col) {
        g2.setColor(col);
        g2.fillOval(x, y - 9, 8, 8);
        g2.setColor(UITheme.TEXT_SECONDARY);
        g2.drawString(label, x + 12, y);
    }
}
