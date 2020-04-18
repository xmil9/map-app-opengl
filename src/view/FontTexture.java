//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class FontTexture {

    ///////////////
    
    public static class CharInfo {
        private final int startX;
        private final int width;

        public CharInfo(int startX, int width) {
            this.startX = startX;
            this.width = width;
        }

        public int startX() {
            return startX;
        }

        public int width() {
            return width;
        }
    }

    ///////////////
    
    private static final String IMAGE_FORMAT = "png";
    private static final int CHAR_PADDING = 2;
    private final Font font;
    private final String charSetName;
    private final Map<Character, CharInfo> charMap;
    private Texture texture;
    private int height;
    private int width;

    public FontTexture(Font font, String charSetName) throws Exception {
        this.font = font;
        this.charSetName = charSetName;
        this.charMap = new HashMap<>();
        buildTexture();
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Texture texture() {
        return texture;
    }

    public CharInfo charInfo(char c) {
        return charMap.get(c);
    }

    private String getAllAvailableChars(String charsetName) {
        CharsetEncoder ce = Charset.forName(charsetName).newEncoder();
        StringBuilder strBuilder = new StringBuilder();
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (ce.canEncode(c)) {
                strBuilder.append(c);
            }
        }
        return strBuilder.toString();
    }

    private void buildTexture() throws Exception {
        // Get the font metrics for each character for the selected font by using image
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = img.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setFont(font);
        FontMetrics fontMetrics = g2D.getFontMetrics();

        String allChars = getAllAvailableChars(charSetName);
        this.width = 0;
        this.height = fontMetrics.getHeight();
        for (char c : allChars.toCharArray()) {
            // Get the size for each character and update global image size
            CharInfo charInfo = new CharInfo(width, fontMetrics.charWidth(c));
            charMap.put(c, charInfo);
            width += charInfo.width() + CHAR_PADDING;
        }
        g2D.dispose();

        // Create the image associated to the charset
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2D = img.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setFont(font);
        fontMetrics = g2D.getFontMetrics();
        g2D.setColor(Color.WHITE);
		int startX = 0;
        for (char c : allChars.toCharArray()) {
            CharInfo charInfo = charMap.get(c);
            g2D.drawString("" + c, startX, fontMetrics.getAscent());
            startX += charInfo.width() + CHAR_PADDING;
        }
        g2D.dispose();

        ByteBuffer buf = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, IMAGE_FORMAT, out);
            out.flush();
            byte[] data = out.toByteArray();
            buf = ByteBuffer.allocateDirect(data.length);
            buf.put(data, 0, data.length);
            buf.flip();
        }
        texture = new Texture(buf);
    }
}
