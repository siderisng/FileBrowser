/*
 *AUTHORS:
 SIDERIS GEORGE
 */
package gr.uth.inf.ce325.fileBrowser;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
class FileRenderer extends DefaultListCellRenderer {

    private final boolean pad;
    private final Border padBorder = new EmptyBorder(3, 3, 3, 3);
    private String SLASH;

    FileRenderer(boolean pad) {
        this.pad = pad;
    }

    //used for JList appearance customization
    @Override
    public Component getListCellRendererComponent(
            @SuppressWarnings("rawtypes") JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        if (System.getProperty("os.name").contains("Windows")) {
            SLASH = "\\";
        }//if unix, max etc... 
        else {
            SLASH = "/";
        }

        Component c = super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        JLabel l = (JLabel) c;
        l.setHorizontalTextPosition(JLabel.CENTER);     //center text horizontally
        l.setVerticalTextPosition(JLabel.BOTTOM);       //and vertically
        l.setHorizontalAlignment(JLabel.CENTER);        //center  thumbs horizontally
        File f = (File) value;

        //Set files text and icon
        Dimension dim = new Dimension(150, 150);        //set dimensions of button
        l.setPreferredSize(dim);
        l.setText(f.getName());
        ImageIcon icon;
        FileNameMap fileNameMap = URLConnection.getFileNameMap();       //check filetype
        String mimeType = null;
        try {
            mimeType = fileNameMap.getContentTypeFor(f.getCanonicalPath());
        } catch (IOException ex) {
            Logger.getLogger(FileRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (f.isDirectory()) {
            icon = new ImageIcon("Images" + SLASH + "folder.png");
        } else if (mimeType == null) {
            icon = new ImageIcon("Images" + SLASH + "unknown.png");
        } else if (mimeType.contains("Images" + SLASH + "image")) {
            icon = new ImageIcon("Images" + SLASH + "picture.png");
        } else if (mimeType.contains("Images" + SLASH + "text")) {
            icon = new ImageIcon("Images" + SLASH + "text.png");
        } else if (mimeType.contains("pdf")) {
            icon = new ImageIcon("Images" + SLASH + "pdf.png");
        } else if (mimeType.contains("zip")) {
            icon = new ImageIcon("Images" + SLASH + "zip.png");
        } else if (mimeType.contains("xml")) {
            icon = new ImageIcon("Images" + SLASH + "xml.png");
        } else {
            icon = new ImageIcon("Images" + SLASH + "unknown.png");
        }

        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);

        l.setIcon(icon);
        if (pad) {
            l.setBorder(padBorder);
        }

        return l;
    }
}
