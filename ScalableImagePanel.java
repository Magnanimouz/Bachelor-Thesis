/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author Glenn
 */
public class ScalableImagePanel extends javax.swing.JPanel {

    private static final boolean USE_HD_SCALING = true;

    private Image original;
    private Image scaled;
    private boolean toFit;

    public ScalableImagePanel() {
        this(null, true);
    }

    public ScalableImagePanel(Image original) {
        this(original, true);
    }

    public ScalableImagePanel(Image original, boolean toFit) {
        initComponents();
        this.original = original;
        setToFit(toFit);
    }

    public void setImage(Image image) {
        scaled = null;
        original = image;
        updateUI();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return original == null ? super.getPreferredSize() : new Dimension(original.getWidth(this), original.getHeight(this));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image toDraw;
        if (scaled != null) {
            toDraw = scaled;
        } else {
            toDraw = original;
        }

        if (toDraw != null) {
            int x = (getWidth() - toDraw.getWidth(this)) /2;
            int y = (getHeight() - toDraw.getHeight(this)) /2;
            g.drawImage(toDraw, x, y, this);
        }
    }

    @Override
    public void invalidate() {
        if (original != null) {
            generateScaledInstance();
            super.invalidate();
        }
    }

    private void generateScaledInstance() {
        scaled = null;
        scaled = getScaledInstance(original, getSize(), isToFit());
    }

    private BufferedImage toBufferedImage(Image original) {
        Dimension size = new Dimension(original.getWidth(this), original.getHeight(this));
        BufferedImage image = createCompatibleImage(size);
        Graphics2D g2d = image.createGraphics();
        g2d.drawImage(original, 0, 0, this);
        g2d.dispose();
        return image;
    }

    private Image getScaledInstance(Image original, Dimension size, boolean toFit) {
        Dimension originalSize = new Dimension(original.getWidth(this), original.getHeight(this));
        return getScaledInstance(toBufferedImage(original), getScaleFactor(originalSize, size, toFit), RenderingHints.VALUE_INTERPOLATION_BILINEAR, USE_HD_SCALING);
    }

    private Dimension getSize(Dimension original, Dimension target, boolean toFit) {
        double factor = getScaleFactor(original, target, toFit);
        Dimension size = new Dimension(original);
        size.width *= factor;
        size.height *= factor;
        return size;
    }

    private double getScaleFactor(Dimension original, Dimension target, boolean toFit) {
        double scaleWidth = getFactor(original.width, target.width);
        double scaleHeight = getFactor(original.height, target.height);
        if (toFit) {
            return Math.min(scaleWidth, scaleHeight);
        } else {
            return Math.max(scaleWidth, scaleHeight);
        }
    }

    private double getFactor(int originalSize, int targetSize) {
        return (double)targetSize / (double)originalSize;
    }

    private BufferedImage createCompatibleImage(Dimension size) {
        return createCompatibleImage(size.width, size.height);
    }

    private BufferedImage createCompatibleImage(int width, int height) {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        if (gc == null) {
            gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }

        BufferedImage image = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        image.coerceData(true);
        return image;
    }

    private BufferedImage getScaledInstance(BufferedImage image, double factor, Object hint, boolean hd) {
        int width = (int) Math.round(image.getWidth() * factor);
        int height = (int) Math.round(image.getHeight() * factor);

        return getScaledInstance(image, width, height, hint, hd, (factor <= 1.0d));
    }

    private BufferedImage getScaledInstance(BufferedImage image, int targetW, int targetH, Object hint, boolean hd, boolean scaledDown) {
        int type = (image.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

        if (targetH <= 0 || targetW <= 0) {
            return new BufferedImage(1, 1, type);
        }

        int w,h;
        if (hd) {
            w = image.getWidth();
            h = image.getHeight();
        } else {
            w = targetW;
            h = targetH;
        }

        return scaledDown
                ? performDownScaling(targetW, targetH, hint, hd, type, image, w, h)
                : performUpScaling(targetW, targetH, hint, hd, type, image, w, h);
    }

    private BufferedImage performUpScaling(int targetW, int targetH, Object hint, boolean hd, int type, BufferedImage result, int w, int h) {
        do {
            if (hd && w < targetW) {
                w = Math.min(w*2, targetW);
            }
            if (hd && h < targetH) {
                h = Math.min(h*2, targetH);
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2d = tmp.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2d.drawImage(result, 0, 0, w, h, null);
            g2d.dispose();

            result = tmp;

        } while (w != targetW || h != targetH);
        return result;
    }

    private BufferedImage performDownScaling(int targetW, int targetH, Object hint, boolean hd, int type, BufferedImage result, int w, int h) {
        do {
            if (hd && w > targetW) {
                w = Math.max(w/2, targetW);
            }
            if (hd && h > targetH) {
                h = Math.max(h/2, targetH);
            }

            BufferedImage tmp = new BufferedImage(Math.max(w, 1), Math.max(h, 1), type);
            Graphics2D g2d = tmp.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2d.drawImage(result, 0, 0, w, h, null);
            g2d.dispose();

            result = tmp;

        } while (w != targetW || h != targetH);
        return result;
    }


    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">//GEN-BEGIN:getSet
    public boolean isToFit() {
        return toFit;
    }

    public void setToFit(boolean value) {
        if (value != toFit) {
            toFit = value;
            invalidate();
        }
    }// </editor-fold>//GEN-END:getSet

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
