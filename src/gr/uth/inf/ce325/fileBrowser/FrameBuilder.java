/*
 *AUTHORS:
 SIDERIS GEORGE
 TSOKOS FOTIS
 */
package gr.uth.inf.ce325.fileBrowser;


import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

@SuppressWarnings("serial")
public class FrameBuilder extends javax.swing.JFrame {

    private static String SLASH;
    //Root Folder        
    FileTreeList ourTree;
    private final File CONFIG = new File(".ce325fb.config");

    //Menu options
    private JMenu File;
    private JMenu Edit;
    private JMenuBar MainMenu;

    //scroll panes
    //Left
    private JScrollPane scrollL;
    private JTree treeL;
    //Right
    private JScrollPane scrollR;
    private JList<File> listR;
    //main split pane
    private JSplitPane splitPane;
    //Search tools
    private JTextField textF;
    private JButton searchButton;
    //prev tools
    private JButton prevButton;
    
    
    public FrameBuilder() throws IOException {
        initComponents();       //initialize the components of the framebuilder
        Image image = ImageIO.read(new File("Images"+SLASH+"rabbit.png"));
        this.setIconImage(image);
        this.setTitle("CE325 G13 FileBrowser");
        this.setAutoRequestFocus(true);

    }

    

    private void initComponents() throws IOException {

    	//choose slash based on operating system
        if (System.getProperty("os.name").contains("Windows")) {
        	SLASH = "\\";
        }//if unix, max etc... 
        else {
        	SLASH = "/";
        }

        //init basic components
        splitPane = new JSplitPane(); 
        scrollL = new JScrollPane();    //left scrollpane
        scrollR = new JScrollPane();    //right scrollpane
        textF = new JTextField();       //textfield for searc
        MainMenu = new JMenuBar();      //menubar on top (file,edit...) 
        File = new JMenu();
        Edit = new JMenu();
        
        //init tree
        ourTree = new FileTreeList(CONFIG);     //initialize tree based on our config
        treeL = new JTree(ourTree.getTreeMod());

        //init list model
        DefaultListModel<File> model = ourTree.getLMod(ourTree.getRoot());
        listR = new JList<File>(model);

        //use setCellRenderer to set up our list's appearance
        boolean vertical = false;
        FileRenderer cellrend = new FileRenderer(!vertical);
        listR.setCellRenderer(cellrend);

        if (!vertical) {
            listR.setLayoutOrientation(JList.HORIZONTAL_WRAP); //horizontally display the items of the list
            listR.setVisibleRowCount(-1);
        } else {
            listR.setVisibleRowCount(9);
        }

        

        //initialize menu list
        initmenus();

        //set exit_on_close as default when X

        searchButton = new JButton();
        searchButton.addActionListener(new Listener(ourTree, listR, treeL, textF, model));
        textF.addActionListener(new Listener(ourTree, listR, treeL, textF, model));
        
        
        //previous button init
        //used to return to the previous file
        prevButton = new JButton();
        prevButton.addActionListener(new Listener(ourTree,listR,treeL));
        prevButton.setText("Previous");
        prevButton.setActionCommand("Prev");
        prevButton.setEnabled(false);
        
        
        
        //add listeners to tree
        treeL.addTreeSelectionListener(new Listener(ourTree, listR, treeL,prevButton));
        treeL.addTreeExpansionListener(new Listener(ourTree,prevButton));
        treeL.setRootVisible(true);
        
        
        listR.putClientProperty("List.isFileList", Boolean.TRUE);  //used for not selecting a file when clicking out of bounds
        
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //set components to the right place and fix appearance
        splitPane.setDividerLocation(200);      //size of left pane
        scrollL.setViewportView(treeL);
        splitPane.setLeftComponent(scrollL);
        scrollR.setViewportView(listR);
        splitPane.setRightComponent(scrollR);
        searchButton.setText("Search");         //search button
        
        //Building Alice in wonderland Theme
         int choice = JOptionPane.showConfirmDialog(this, "Tea Party Music Enabled?", "For your hearing pleasure", 2);
         if (choice == JOptionPane.YES_OPTION) {
            try {
             // Open an audio input stream.
             File soundFile = new File("Music"+SLASH+"song.wav");
             AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
             // Get a sound clip resource.
             Clip clip = AudioSystem.getClip();
             // Open audio clip and load samples from the audio input stream.
             clip.open(audioIn);
             clip.start();
             //make it loop because White Rabbit is a pretty good song
             clip.loop(Clip.LOOP_CONTINUOUSLY); 
          } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | IllegalArgumentException e) {
             JOptionPane.showMessageDialog(this,"Couldn't Play song... it will never be the same... " + e.getMessage());
          }
         }
         
         //Put an inspirational quote on start.  
        ImageIcon alice = new ImageIcon("Images"+SLASH+"rabbit.png");
        JOptionPane.showMessageDialog(this,"-Would you tell me, please, which way I ought to go from here?\n" +
            "-That depends a good deal on where you want to get to.\n"+
            "-I don't much care where\n"+
           "-Then it doesn't matter which way you go.","ALICE IN BROWSERLAND",JOptionPane.PLAIN_MESSAGE,alice);
       //Theme ends here
        
        
        //layout building
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(splitPane, GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(textF, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.DEFAULT_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.DEFAULT_SIZE)
                        .addGap(0, 600, Short.MAX_VALUE)
                        .addComponent(prevButton,100,150,150))
                        .addGap(0, 600, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 384, GroupLayout.DEFAULT_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(searchButton, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                .addComponent(textF)
                                .addComponent(prevButton, 50, 60, 80))
                                
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        //helpful for screen appearacne
        Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = ((int) tk.getScreenSize().getWidth());
        int ySize = ((int) tk.getScreenSize().getHeight());
        this.setSize(xSize, ySize);
        pack();
    }

    //initialize menu basic commands like copy paste cut delete execute
    private void initmenus() {

        //RIGHT CLICK POPUP MENU
        ActionListener actionListener = new Listener(ourTree, listR, treeL);

        //create icons
        ImageIcon iconExit = new ImageIcon("Images"+SLASH+"close.png");
        ImageIcon iconCopy = new ImageIcon("Images"+SLASH+"copy.gif");
        ImageIcon iconPaste = new ImageIcon("Images"+SLASH+"paste.png");
        ImageIcon iconCut = new ImageIcon("Images"+SLASH+"cut.jpg");
        ImageIcon iconCreate = new ImageIcon("Images"+SLASH+"create.png");
        ImageIcon iconCreateFolder = new ImageIcon("Images"+SLASH+"createfolder.jpg");
        ImageIcon iconCreateText = new ImageIcon("Images"+SLASH+"createtext.png");
        ImageIcon iconRename = new ImageIcon("Images"+SLASH+"rename.png");
        ImageIcon iconDelete = new ImageIcon("Images"+SLASH+"delete.png");
        ImageIcon iconInfo = new ImageIcon("Images"+SLASH+"info.gif");
        ImageIcon iconRun = new ImageIcon("Images"+SLASH+"run.png");

        
        //create menu items and add them to menus and add listeners to each and every one of them
        
        JPopupMenu popupMenu = new JPopupMenu("Edit");
        
        JMenuItem executeMenuItem = new JMenuItem("Execute",iconRun);
        popupMenu.add(executeMenuItem);
        executeMenuItem.addActionListener(new Listener(ourTree, listR, treeL));
        executeMenuItem.setArmed(true);
        popupMenu.addSeparator();

        popupMenu.addPopupMenuListener(new Listener(ourTree, listR, treeL));

        JMenuItem pasteMenuItem = new JMenuItem("Paste",iconPaste);
        pasteMenuItem.addActionListener(new Listener(ourTree, listR, treeL));
        //Don't enable paste button yet
        pasteMenuItem.setEnabled(false);
        popupMenu.add(pasteMenuItem);
        pasteMenuItem.addActionListener(new Listener(ourTree, listR, treeL));

        JMenuItem cutMenuItem = new JMenuItem("Cut",iconCut);
        popupMenu.add(cutMenuItem);
        cutMenuItem.addActionListener(new Listener(ourTree, listR, treeL, pasteMenuItem));

        JMenuItem copyMenuItem = new JMenuItem("Copy",iconCopy);
        popupMenu.add(copyMenuItem);
        copyMenuItem.addActionListener(new Listener(ourTree, listR, treeL, pasteMenuItem));

        JMenuItem renameMenuItem = new JMenuItem("Rename",iconRename);
        popupMenu.add(renameMenuItem);
        renameMenuItem.addActionListener(actionListener);

        JMenuItem deleteMenuItem = new JMenuItem("Delete",iconDelete);
        popupMenu.add(deleteMenuItem);
        deleteMenuItem.addActionListener(actionListener);

        JMenu createMenu = new JMenu("Create");
        createMenu.setIcon(iconCreate);
        JMenuItem createFile = new JMenuItem("Create New Text File",iconCreateText);
        JMenuItem createFolder = new JMenuItem("Create New Folder",iconCreateFolder);
        createMenu.add(createFile);
        createMenu.add(createFolder);
        popupMenu.addSeparator();
        popupMenu.add(createMenu);

        JMenuItem infoMenuItem = new JMenuItem("Information",iconInfo);
        popupMenu.add(infoMenuItem);
        infoMenuItem.addActionListener(actionListener);

        createFile.addActionListener(actionListener);
        createFolder.addActionListener(actionListener);

        //TOOLBAR MENUS
        
                
        JMenuItem exit = new JMenuItem("Exit", iconExit);
        exit.addActionListener(new Listener(ourTree, listR, treeL));

        JMenu create = new JMenu("Create");
        create.setIcon(iconCreate);
        JMenuItem createF = new JMenuItem("Create New Text File",iconCreateText);
        JMenuItem createFol = new JMenuItem("Create New Folder",iconCreateFolder);
        create.add(createF);
        create.add(createFol);

        JMenuItem cut = new JMenuItem("Cut",iconCut);
        JMenuItem copy = new JMenuItem("Copy",iconCopy);
        JMenuItem paste = new JMenuItem("Paste",iconPaste);
        JMenuItem delete = new JMenuItem("Delete",iconDelete);
        JMenuItem rename = new JMenuItem("Rename",iconRename);
        JMenuItem info = new JMenuItem("Information",iconInfo);
        cut.addActionListener(new Listener(ourTree, listR, treeL, pasteMenuItem));
        copy.addActionListener(new Listener(ourTree, listR, treeL, pasteMenuItem));

        rename.addActionListener(new Listener(ourTree, listR, treeL));
        paste.addActionListener(new Listener(ourTree, listR, treeL));
        delete.addActionListener(new Listener(ourTree, listR, treeL));
        createF.addActionListener(actionListener);
        createFol.addActionListener(actionListener);
        info.addActionListener(actionListener);

        File.setText("File");

        File.add(exit);
        MainMenu.add(File);
        Edit.setText("Edit");
        Edit.add(cut);
        Edit.add(copy);
        Edit.add(paste);
        Edit.add(rename);
        Edit.add(delete);
        Edit.add(infoMenuItem);
        Edit.addSeparator();
        Edit.add(create);

        MainMenu.add(Edit);
        setJMenuBar(MainMenu);

        listR.addMouseListener(new Listener(ourTree, listR, treeL, popupMenu));

    }

    
}
