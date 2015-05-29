/*
 *AUTHORS:
 SIDERIS GEORGE
 */
package gr.uth.inf.ce325.fileBrowser;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class Listener extends MouseAdapter implements TreeSelectionListener, TreeExpansionListener, ListSelectionListener, PopupMenuListener, ActionListener {

    //useful variables such as our tree and list models and FileTreeList object 
    //to be used as a toolbox
    private final FileTreeList ourTree;
    private JTree TreeL;
    private JList<File> ListR;
    private JMenuItem pasteButton;
    JTextField textF;
    private DefaultListModel<File> Model;
    JPopupMenu popup;

    JButton prevButton;

    //Used for defining tree depth exploration
    private final int DEFAULT_EXPLORE = 3;
    private final int DOUBLE_CLICK = 2;

    // CONSTRUCTORS
    public Listener(FileTreeList ft) {
        ourTree = ft;
    }

    public Listener(FileTreeList ft, JList<File> ListR, JTree JT) {
        ourTree = ft;
        this.ListR = ListR;
        TreeL = JT;
    }

    public Listener(FileTreeList ft, JList<File> ListR, JTree JT, JButton pb) {
        ourTree = ft;
        this.ListR = ListR;
        TreeL = JT;
        prevButton = pb;
    }

    public Listener(FileTreeList ft, JList<File> ListR, JTree JT, JPopupMenu popupmenu) {
        ourTree = ft;
        this.ListR = ListR;
        TreeL = JT;
        popup = popupmenu;
    }

    public Listener(FileTreeList ft, JList<File> ListR, JTree JT, JMenuItem pasteButton) {
        ourTree = ft;
        this.ListR = ListR;
        TreeL = JT;
        this.pasteButton = pasteButton;
    }

    public Listener(FileTreeList ft, JList<File> ListR, JTree JT, JTextField textF, DefaultListModel<File> model) {
        ourTree = ft;
        this.ListR = ListR;
        TreeL = JT;
        this.textF = textF;
        Model = model;
    }

    public Listener(FileTreeList ft, JButton pB) {
        ourTree = ft;
        prevButton = pB;
    }
    //-------METHODS-------------

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            check(e);
        }
    }

    //Called When someone presses the mouse
    public void mousePressed(MouseEvent e) {

        if (ListR.getSelectedIndex() == -1) {
            ListR.removeSelectionInterval(ListR.getSelectedIndex(), ListR.getSelectedIndex());

        }
        //if double click
        if (e.getClickCount() == DOUBLE_CLICK && !e.isConsumed()) {
            //consume event
            e.consume();
            //find value selected
            File fileClicked = ListR.getSelectedValue();
            if (fileClicked != null) {
                //if file is a directory
                if (fileClicked.isDirectory()) {
                    //explore file tree and update our tree model
                    DefaultMutableTreeNode curr = ourTree.getNodeFromPath(fileClicked.getAbsolutePath());
                    doTreeWork(curr);
                    //also update our list
                    ListR.setModel(ourTree.getLMod(curr));
                    //set our new visible path to Jtree to the value selected from list
                    TreePath tpath = new TreePath(ourTree.getTreeMod().getPathToRoot(curr));
                    TreeL.scrollPathToVisible(tpath);
                    TreeL.setSelectionPath(tpath);

                } else {
                    try {
                        ourTree.ExecuteFile(fileClicked);
                    } catch (IOException ex) {
                        Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {

            @SuppressWarnings("unchecked")
            JList<File> nL = (JList<File>) e.getSource();
            ListR = nL;
            check(e);
        }

    }//if we choose another value to JTree update our list model

    public void check(MouseEvent e) {
        if (e.isPopupTrigger()) { //if the event shows the menu
            int index = ListR.locationToIndex(e.getPoint());
            if (index != -1 && !ListR.getCellBounds(index, index).contains(e.getPoint())) {
                index = -1;
            }
            if (index != -1) {
                ListR.setSelectedIndex(ListR.locationToIndex(e.getPoint())); //select the item
            }

            popup.show(ListR, e.getX(), e.getY()); //and show the menu
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        prevButton.setEnabled(true);    //enable the "previous" button
        TreePath pth = e.getPath();
        DefaultMutableTreeNode temp = (DefaultMutableTreeNode) pth.getLastPathComponent();

        ListR.setModel(ourTree.getLMod(temp));//update list

    }

    //if we expand our tree model popuate the tree 
    @Override
    public void treeExpanded(TreeExpansionEvent event) {

        prevButton.setEnabled(true);
        TreePath pth = event.getPath();
        DefaultMutableTreeNode curr = (DefaultMutableTreeNode) pth.getLastPathComponent();
        doTreeWork(curr);

    }

    //if tree collapses... do nothing
    @Override
    public void treeCollapsed(TreeExpansionEvent event) {

        //-----------------------------------//
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    //this function does all the tree work. It populates the tree
    //and also checks for already searched nodes to save us time
    private void doTreeWork(DefaultMutableTreeNode curr) {

        int index = 0;
        TreeNode temp;
        int nofKidz = curr.getChildCount();
        if (nofKidz == 0) {
            File fp = new File(ourTree.getNodePath(curr));
            ourTree.populateTree(curr, fp, DEFAULT_EXPLORE);
        } else {
            while (index != nofKidz) {
                temp = curr.getChildAt(index);
                if (temp.getChildCount() == 0) {
                    File fp = new File(ourTree.getNodePath((DefaultMutableTreeNode) temp));
                    ourTree.populateTree((DefaultMutableTreeNode) temp, fp, DEFAULT_EXPLORE);
                }
                index++;

            }
        }
        ourTree.getTreeMod().reload(curr);

    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
        //System.out.println("Canceled");
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
        //System.out.println("Becoming Invisible");
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
        //System.out.println("Becoming Visible");
    }

    @SuppressWarnings("fallthrough")
    public void actionPerformed(ActionEvent actionEvent) {

        if (actionEvent.getActionCommand() == null) {
            return;
        }

        if (actionEvent.getActionCommand().equals("Prev")) { //if the previous button is pressed
            DefaultMutableTreeNode thisNode;
            TreePath pth = TreeL.getSelectionPath();
            if (pth == null) {
                return;
            } else {
                thisNode = (DefaultMutableTreeNode) pth.getLastPathComponent();
            }
            File current = new File(ourTree.getNodePath(thisNode));

            thisNode = ourTree.getNodeFromPath(current.getAbsolutePath());
            if ((thisNode == ourTree.getRoot()) || (thisNode.getParent() == ourTree.getRoot())) {
                thisNode = ourTree.getRoot();
                ourTree.getLMod(thisNode);
                TreeL.setSelectionPath(pth);
                TreeL.scrollPathToVisible(pth);
                ourTree.getTreeMod().reload(thisNode);
                JOptionPane.showMessageDialog(ListR, "Back to the rabbit hole..."); //returned to root
                return;
            }

            pth = new TreePath(thisNode.getParent());
            ourTree.getLMod(thisNode);  //reload tree
            TreeL.setSelectionPath(pth);    //reload selection
            TreeL.scrollPathToVisible(pth);

            ourTree.getTreeMod().reload(thisNode);//go to last path
            return;
        }

        if (actionEvent.getSource().toString().contains("JTextField") || actionEvent.getSource().toString().contains("JButton")) { //if the user searches for something
            DefaultMutableTreeNode curr;
            Model.removeAllElements();
            TreePath tp = TreeL.getSelectionPath();
            if (tp == null) {
                curr = ourTree.getRoot();
                JOptionPane.showMessageDialog(ListR, "You Shall Always Start From Somewhere In Order To Get "
                        + "Somewhere Else.\nPlease Choose Folder To Search File In");
                return;
            } else {
                curr = (DefaultMutableTreeNode) tp.getLastPathComponent();  //get current path
            }
            String path = ourTree.getNodePath(curr);
            JOptionPane.showMessageDialog(ListR, "Let The Hunt Begin... Please Wait");
            ourTree.SearchForFile(textF.getText(), new File(path)); //search for file or path
            JOptionPane.showMessageDialog(ListR, "Finished Searching");
            return;

        }

        //in case of right click popup menu and action
        File myFile;
        myFile = ListR.getSelectedValue();

        String name;
        DefaultMutableTreeNode curr;
        TreePath tp;

        //according to his selection do the appropriate command
        if (null != actionEvent.getActionCommand()) {
            switch (actionEvent.getActionCommand()) {
                case "Copy":        //if we want copy
                    if (myFile == null) {
                        return;
                    }
                    pasteButton.setEnabled(true);       //now we can enable the paste button
                    ourTree.CopyFile(myFile);
                    break;
                case "Cut":     //in case of cut

                    if (myFile == null) {
                        return;
                    }
                    ourTree.CatFile(myFile);
                    pasteButton.setEnabled(true);   //now we can enable the paste button
                    break;
                case "Paste": {         //in case of pasth
                    if (myFile == null) {
                        tp = TreeL.getSelectionPath();      //paste to selection

                        if (tp == null) {
                            curr = ourTree.getRoot();
                        } else {
                            curr = (DefaultMutableTreeNode) tp.getLastPathComponent();
                        }
                        myFile = new File(ourTree.getNodePath(curr));

                    }
                    try {           //paste to selected
                        ourTree.PasteFile(myFile);
                        TreePath tpath = new TreePath(ourTree.getTreeMod().getPathToRoot(ourTree.getNodeFromPath(myFile.getAbsolutePath())));
                        ourTree.getTreeMod().reload(ourTree.getNodeFromPath(myFile.getAbsolutePath()));
                        TreeL.scrollPathToVisible(tpath);
                        TreeL.setSelectionPath(tpath);
                    } catch (IOException ex) {
                        Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ourTree.getLMod(ourTree.getNodeFromPath(myFile.getAbsolutePath())); //reload

                }

                break;
                case "Rename":          //in case of rename
                    name = JOptionPane.showInputDialog(ListR, "What will you call yourself?", "The Fawn said ", 3); //let the user decide the new name
                    if (name == null) {
                        name = ListR.getSelectedValue().getName();
                    }
                    File newFile = ourTree.RenameFile(myFile, name);

                    ourTree.getLMod(ourTree.getNodeFromPath(newFile.getParentFile().getAbsolutePath()));        //reload
                    if (newFile.isDirectory()) {
                        ourTree.getTreeMod().reload(ourTree.getNodeFromPath(newFile.getAbsolutePath()));
                        TreePath tpath = new TreePath(ourTree.getTreeMod().getPathToRoot(ourTree.getNodeFromPath(newFile.getAbsolutePath()).getParent()));
                        ourTree.getTreeMod().reload(ourTree.getNodeFromPath(myFile.getAbsolutePath()));
                        TreeL.scrollPathToVisible(tpath);
                        TreeL.setSelectionPath(tpath);
                    }
                    break;

                case "Execute":     //in case of execute
                    if (myFile == null) {
                        return;
                    }

                    try {
                        ourTree.ExecuteFile(myFile);
                    } catch (IOException ex) {
                        JOptionPane.showInternalMessageDialog(ListR, " Couldn't Execute File", "Error!!!", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case "Delete":      //in case of delete
                    if (myFile == null) {
                        return;
                    }
                    int choice;
                    choice = JOptionPane.showConfirmDialog(popup, "Off with their heads!?", "Queen Of Hearts Question", DOUBLE_CLICK);      //confirm deletion
                    if (choice == JOptionPane.YES_OPTION) {
                        ourTree.Delete(myFile);
                        File parent = myFile.getParentFile();
                        ourTree.getLMod(ourTree.getNodeFromPath(parent.getAbsolutePath()));
                        ourTree.getTreeMod().reload(ourTree.getNodeFromPath(parent.getAbsolutePath()));
                    }
                    break;
                case "Exit":            //exit the program
                    ourTree.Exit();
                    break;
                case "Create New Text File":            //create new file
                    name = JOptionPane.showInputDialog(ListR, "File, thy name is", "Important Question", 3); // ask for name
                    if (name == null) {
                        break;//if exited dont create file
                    }
                    tp = TreeL.getSelectionPath();

                    if (tp == null) {
                        curr = ourTree.getRoot();
                    } else {
                        curr = (DefaultMutableTreeNode) tp.getLastPathComponent();
                    }
                     {
                        try {
                            ourTree.CreateFile(curr, name);
                        } catch (IOException ex) {
                            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        ourTree.getLMod(curr);
                        break;
                    }
                case "Create New Folder":           //create new folder
                    name = JOptionPane.showInputDialog(ListR, "Folder, thy name is", "Important Question", 3); //ask for name
                    if (name == null) {
                        break;  //if exited dont create file
                    }
                    tp = TreeL.getSelectionPath();

                    if (tp == null) {
                        curr = ourTree.getRoot();
                    } else {
                        curr = (DefaultMutableTreeNode) tp.getLastPathComponent();
                    }
                     {
                        try {
                            ourTree.CreateFolder(curr, name);
                            ourTree.getLMod(curr);
                            ourTree.getTreeMod().reload(curr);
                        } catch (IOException ex) {
                            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case "Information":     //in case of info
                    File f = new File(ListR.getSelectedValue().getAbsolutePath());

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    boolean exe = f.canExecute();
                    boolean read = f.canRead();
                    boolean write = f.canWrite();
                    String info = "Name: " + f.getName() + "\nPath: " + f.getAbsolutePath() + "\nSize is: " + f.length() + " Bytes\nLast modified: " + sdf.format(f.lastModified()) + "\nCan execute: " + exe + "\nCan write: " + write + "\nCan read: " + read;

                    //show info in message dialog
                    JOptionPane.showMessageDialog(ListR, info, "Information about this file", JOptionPane.INFORMATION_MESSAGE);
                    break;

            }
        }
    }
}
