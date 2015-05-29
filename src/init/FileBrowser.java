/*
AUTHORS:
siderisng sideris@uth.gr
tsokos tsokos@uth.gr
*/
package init;
import javax.swing.*;
import gr.uth.inf.ce325.fileBrowser.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class FileBrowser extends JFrame {
    
 
   
    public static void main(String args[]) {
        
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrameBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrameBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrameBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrameBuilder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        /* Create and display the form aka LET THE MAGIC BEGIN!!!*/
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new FrameBuilder().setVisible(true);
                    
                } catch (IOException ex) {
                    Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
