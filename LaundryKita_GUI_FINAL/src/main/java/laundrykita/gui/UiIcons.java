package laundrykita.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

public final class UiIcons {
    private UiIcons() {
    }

    public static Icon icon(String name, Color color, int size) {
        return new VectorIcon(name, color, size);
    }

    public static Image appIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(UiTheme.ACCENT);
        g.fillRoundRect(0, 0, size, size, size / 3, size / 3);
        g.setColor(new Color(255, 255, 255, 235));
        g.setFont(new Font("Segoe UI", Font.BOLD, Math.round(size * 0.34f)));
        FontMetrics fm = g.getFontMetrics();
        String text = "LK";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);
        g.dispose();
        return image;
    }

    private static final class VectorIcon implements Icon {
        private final String name;
        private final Color color;
        private final int size;

        VectorIcon(String name, Color color, int size) {
            this.name = name;
            this.color = color;
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            g2.scale(size / 18.0, size / 18.0);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            draw(g2, name);
            g2.dispose();
        }

        private static void draw(Graphics2D g, String name) {
            switch (name) {
                case "dashboard" -> {
                    g.draw(new RoundRectangle2D.Float(2.5f, 2.5f, 5.4f, 5.4f, 1.6f, 1.6f));
                    g.draw(new RoundRectangle2D.Float(10.1f, 2.5f, 5.4f, 5.4f, 1.6f, 1.6f));
                    g.draw(new RoundRectangle2D.Float(2.5f, 10.1f, 5.4f, 5.4f, 1.6f, 1.6f));
                    g.draw(new RoundRectangle2D.Float(10.1f, 10.1f, 5.4f, 5.4f, 1.6f, 1.6f));
                }
                case "orders" -> {
                    g.draw(new RoundRectangle2D.Float(4f, 2.5f, 10f, 13f, 2f, 2f));
                    g.draw(new Line2D.Float(6.8f, 6.2f, 11.2f, 6.2f));
                    g.draw(new Line2D.Float(6.8f, 9f, 11.2f, 9f));
                    g.draw(new Line2D.Float(6.8f, 11.8f, 9.8f, 11.8f));
                }
                case "plus" -> {
                    g.draw(new Line2D.Float(9f, 3.5f, 9f, 14.5f));
                    g.draw(new Line2D.Float(3.5f, 9f, 14.5f, 9f));
                }
                case "customers" -> {
                    g.draw(new Ellipse2D.Float(6f, 2.8f, 6f, 6f));
                    GeneralPath body = new GeneralPath();
                    body.moveTo(3.4f, 15.4f);
                    body.curveTo(3.8f, 11.8f, 6f, 10.6f, 9f, 10.6f);
                    body.curveTo(12f, 10.6f, 14.2f, 11.8f, 14.6f, 15.4f);
                    g.draw(body);
                }
                case "services" -> {
                    GeneralPath tag = new GeneralPath();
                    tag.moveTo(3f, 8.6f);
                    tag.lineTo(8.6f, 3f);
                    tag.lineTo(13.6f, 3f);
                    tag.lineTo(15f, 4.4f);
                    tag.lineTo(15f, 9.4f);
                    tag.lineTo(9.4f, 15f);
                    tag.closePath();
                    g.draw(tag);
                    g.draw(new Ellipse2D.Float(11.2f, 5.2f, 1.8f, 1.8f));
                }
                case "home" -> {
                    GeneralPath roof = new GeneralPath();
                    roof.moveTo(2.8f, 8.8f);
                    roof.lineTo(9f, 3.2f);
                    roof.lineTo(15.2f, 8.8f);
                    g.draw(roof);
                    GeneralPath walls = new GeneralPath();
                    walls.moveTo(5f, 8.2f);
                    walls.lineTo(5f, 15f);
                    walls.lineTo(13f, 15f);
                    walls.lineTo(13f, 8.2f);
                    g.draw(walls);
                    g.draw(new RoundRectangle2D.Float(7.6f, 11.2f, 2.8f, 3.8f, 0.8f, 0.8f));
                }
                case "user" -> {
                    g.draw(new Ellipse2D.Float(6.6f, 2.8f, 4.8f, 4.8f));
                    GeneralPath body = new GeneralPath();
                    body.moveTo(4.2f, 15.2f);
                    body.curveTo(4.7f, 11.9f, 6.6f, 10.8f, 9f, 10.8f);
                    body.curveTo(11.4f, 10.8f, 13.3f, 11.9f, 13.8f, 15.2f);
                    g.draw(body);
                }
                case "logout" -> {
                    GeneralPath door = new GeneralPath();
                    door.moveTo(9.5f, 3f);
                    door.lineTo(4f, 3f);
                    door.lineTo(4f, 15f);
                    door.lineTo(9.5f, 15f);
                    g.draw(door);
                    g.draw(new Line2D.Float(8.5f, 9f, 15f, 9f));
                    g.draw(new Line2D.Float(12.4f, 6.4f, 15f, 9f));
                    g.draw(new Line2D.Float(12.4f, 11.6f, 15f, 9f));
                }
                case "refresh" -> {
                    g.draw(new java.awt.geom.Arc2D.Float(3.2f, 3.2f, 11.6f, 11.6f, 55f, 285f, java.awt.geom.Arc2D.OPEN));
                    g.draw(new Line2D.Float(12.8f, 2.6f, 15.2f, 4.4f));
                    g.draw(new Line2D.Float(15.2f, 4.4f, 13.4f, 6.6f));
                }
                case "search" -> {
                    g.draw(new Ellipse2D.Float(3f, 3f, 8.2f, 8.2f));
                    g.draw(new Line2D.Float(10.9f, 10.9f, 15.2f, 15.2f));
                }
                case "calendar" -> {
                    g.draw(new RoundRectangle2D.Float(3f, 4.2f, 12f, 11.2f, 1.8f, 1.8f));
                    g.draw(new Line2D.Float(3f, 7.6f, 15f, 7.6f));
                    g.draw(new Line2D.Float(6.4f, 2.6f, 6.4f, 4.8f));
                    g.draw(new Line2D.Float(11.6f, 2.6f, 11.6f, 4.8f));
                    g.draw(new Line2D.Float(6.8f, 10.5f, 6.8f, 10.7f));
                    g.draw(new Line2D.Float(9f, 10.5f, 9f, 10.7f));
                    g.draw(new Line2D.Float(11.2f, 10.5f, 11.2f, 10.7f));
                }
                case "clock" -> {
                    g.draw(new Ellipse2D.Float(2.6f, 2.6f, 12.8f, 12.8f));
                    g.draw(new Line2D.Float(9f, 5.6f, 9f, 9.4f));
                    g.draw(new Line2D.Float(9f, 9.4f, 11.8f, 10.8f));
                }
                case "drop" -> {
                    GeneralPath drop = new GeneralPath();
                    drop.moveTo(9f, 2.4f);
                    drop.curveTo(9f, 2.4f, 14f, 8.1f, 14f, 11.1f);
                    drop.curveTo(14f, 13.9f, 11.8f, 15.6f, 9f, 15.6f);
                    drop.curveTo(6.2f, 15.6f, 4f, 13.9f, 4f, 11.1f);
                    drop.curveTo(4f, 8.1f, 9f, 2.4f, 9f, 2.4f);
                    drop.closePath();
                    g.draw(drop);
                }
                case "check" -> {
                    g.draw(new Line2D.Float(3.6f, 9.3f, 7.4f, 13f));
                    g.draw(new Line2D.Float(7.4f, 13f, 14.4f, 5.2f));
                }
                case "check-circle" -> {
                    g.draw(new Ellipse2D.Float(2.6f, 2.6f, 12.8f, 12.8f));
                    g.draw(new Line2D.Float(5.8f, 9.2f, 8.2f, 11.6f));
                    g.draw(new Line2D.Float(8.2f, 11.6f, 12.6f, 6.6f));
                }
                case "wallet" -> {
                    g.draw(new RoundRectangle2D.Float(2.8f, 4.6f, 12.4f, 9.4f, 2.2f, 2.2f));
                    g.draw(new Line2D.Float(2.8f, 7.4f, 15.2f, 7.4f));
                    g.draw(new RoundRectangle2D.Float(11f, 9.6f, 4.2f, 2.6f, 1.2f, 1.2f));
                }
                case "receipt" -> {
                    g.draw(new RoundRectangle2D.Float(4.2f, 2.6f, 9.6f, 12.8f, 1.8f, 1.8f));
                    g.draw(new Line2D.Float(6.6f, 6.2f, 11.4f, 6.2f));
                    g.draw(new Line2D.Float(6.6f, 9f, 11.4f, 9f));
                    g.draw(new Line2D.Float(6.6f, 11.8f, 9.6f, 11.8f));
                }
                case "inbox" -> {
                    g.draw(new RoundRectangle2D.Float(2.6f, 3.2f, 12.8f, 11.6f, 2f, 2f));
                    GeneralPath tray = new GeneralPath();
                    tray.moveTo(2.6f, 9.8f);
                    tray.lineTo(6.3f, 9.8f);
                    tray.lineTo(7.4f, 11.4f);
                    tray.lineTo(10.6f, 11.4f);
                    tray.lineTo(11.7f, 9.8f);
                    tray.lineTo(15.4f, 9.8f);
                    g.draw(tray);
                }
                case "alert" -> {
                    GeneralPath triangle = new GeneralPath();
                    triangle.moveTo(9f, 2.8f);
                    triangle.lineTo(15.4f, 14.6f);
                    triangle.lineTo(2.6f, 14.6f);
                    triangle.closePath();
                    g.draw(triangle);
                    g.draw(new Line2D.Float(9f, 7f, 9f, 10.2f));
                    g.draw(new Line2D.Float(9f, 12.4f, 9f, 12.6f));
                }
                default -> g.draw(new Ellipse2D.Float(4f, 4f, 10f, 10f));
            }
        }
    }
}
