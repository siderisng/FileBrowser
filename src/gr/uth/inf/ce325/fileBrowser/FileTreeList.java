/*
 *AUTHORS:
 TSOKOS FOTIS
 */
package gr.uth.inf.ce325.fileBrowser;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public final class FileTreeList {

	// used for different os config
	private String SLASH;
	private String ROOTDIR;

	// config file used for file execution
	private File CONFIG;

	// Used for chosing between the appropriate files and function
	// in copy cut move paste methods
	private int Selected;
	private int SelectedType;

	private final int COPY = 1;
	private final int CUT = 0;
	private final int DIR = 1;
	private final int FILE = 0;

	// files or dirs to move or copy
	private File fileToMove;
	private File fileToCopy;
	private File dirToCopy;
	private File dirToMove;

	// Used for defining the depth of the tree we are going to traverse
	private final int INIT_DEPTH = 3;
	private final int EXPLORE_ALL = -2;

	// our main tree model
	private final DefaultTreeModel treeModel;
	// our main list model
	private final DefaultListModel<File> listModel;
	private final File Root; // Root Directory
	private final DefaultMutableTreeNode root;// root node

	// CONSTRUCTOR
	public FileTreeList(File c) {
		// Not yet copied or moved a file
		Selected = -1;

		CONFIG = c;
		// init vars
		listModel = new DefaultListModel<>();

		// init root directory to home
		// set slash value and root directory
		// if windows set windows slash
		if (System.getProperty("os.name").contains("Windows")) {

			SLASH = "\\";
			ROOTDIR = "c:\\";

		}// if unix, max etc...
		else {

			SLASH = "/";
			ROOTDIR = "/";
		}

		// init tree
		Root = new File(ROOTDIR);
		root = new DefaultMutableTreeNode(Root.getName());
		treeModel = new DefaultTreeModel(root);
		populateTree(root, Root, INIT_DEPTH);
		// reload tree
		treeModel.reload();
	}

	// METHODS-------------------
	// GetTreeMode : Returns our tree model
	public DefaultTreeModel getTreeMod() {
		return treeModel;
	}

	// getListMod : returns our list of files
	public DefaultListModel<File> getListMod() {
		return listModel;
	}

	// populates tree after node curr(meaning file currfile) for a given depth
	public void populateTree(DefaultMutableTreeNode curr, File currFile,
			int depth) {
		boolean explores = false;
		DefaultMutableTreeNode tempNode;
		int index = 0;
		// if we need to explore all tree set explores to true
		if (depth == EXPLORE_ALL) {
			explores = true;
		}

		// for every directory in given file create a tree node
		if (currFile.list() != null) {
			for (File child : currFile.listFiles()) {
				// insert node only if file is a directory
				if (child.isDirectory()) {
					tempNode = new DefaultMutableTreeNode(child.getName());
					treeModel.insertNodeInto(tempNode, curr, index);
					index++;
					if (!explores) {
						depth--;
					}
					// if we haven't reached given depth or if we need to
					// explore all tree...
					if ((depth > 0) || (explores)) {
						populateTree(tempNode, child, depth);
						depth++;
					}
				}
			}
		}
	}

	// Returns root node of tree
	public DefaultMutableTreeNode getRoot() {
		return root;
	}

	// gives us the actual path of a node
	public String getNodePath(DefaultMutableTreeNode currNode) {
		String path = currNode.toString();
		TreeNode temp = currNode;

		while ((temp = temp.getParent()) != null) {

			path = temp.toString() + SLASH + path;
		}

		return path;
	}

	// Given a path this function returns the DefaultMutableTreeNode
	public DefaultMutableTreeNode getNodeFromPath(String path) {
		DefaultMutableTreeNode node = null;
		int index = 0;
		ArrayList<String> folders;
		folders = new ArrayList<>();
		// split path to files in it
		path = path.replace(SLASH, "/");
		for (String folder : path.split("/")) {
			folder = folder.trim();
			folders.add(0, folder);
			index++;
		}
		index -= 2;

		// Starting from current node search for the nodes that are in the asked
		// path
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = root.children();

		while (e.hasMoreElements()) {

			node = e.nextElement();

			if (folders.get(index).equals(node.getUserObject().toString())) {
				@SuppressWarnings("unchecked")
				Enumeration<DefaultMutableTreeNode> nE = (Enumeration<DefaultMutableTreeNode>) node
						.children();
				e = nE;
				index--;

				if (index == -1) {
					break;
				}
			}
		}
		return node;
	}

	// Reloads list model based on current file
	public DefaultListModel<File> getLMod(DefaultMutableTreeNode curr) {

		File thisFolder = new File(getNodePath(curr));
		listModel.clear();
		thisFolder = thisFolder.getAbsoluteFile();
		// show every file included in this folder
		if (thisFolder.list() != null) {
			for (File temp : thisFolder.listFiles()) {
				listModel.addElement(temp);
			}
		}

		// Reload tree
		treeModel.nodeChanged(curr);
		treeModel.reload(curr);

		return listModel;
	}

	// searchs the configuration file and finds out the defined program to
	// open a file. Accepted config form is <type>=<ProgramPath>
	private String findExec(String fileType) throws IOException {
		String execP = null;

		// if file exists search in each line for our type of file
		if (CONFIG.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(CONFIG))) {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
					// Check for match
					if (line != null)
						if (line.startsWith(fileType.trim())) {
							execP = line.substring(line.lastIndexOf("=") + 1,
									line.length());
						}
				}
			} catch (IOException ex) {
				throw ex;
			}
		}
		// finally check if program actually exists in this system
		if (execP != null)
			if (!(new File(execP)).exists())
				execP = null;

		return execP;
	}

	// Executes a file
	public void ExecuteFile(File runFile) throws IOException {
		// get file type
		String exec = null;
		String fileType = runFile.getName().substring(
				runFile.getName().indexOf(".") + 1, runFile.getName().length());
		// find default program to execute
		try{
			exec = findExec(fileType);
		}catch (IOException ex){throw ex;}
		
		// if we haven't found a program run with default system's setting's
		if (exec == null) {
			Desktop.getDesktop().open(runFile);
		}// else execute with user's preference
		else {
			ProcessBuilder pb = new ProcessBuilder(exec,
					runFile.getAbsolutePath());
			pb.start();

		}
	}

	// copies a file
	public void CopyFile(File tf) {
		File thisFile = tf;
		// Set selected to copy
		Selected = COPY;
		// check if dir or file
		if (thisFile.isDirectory()) {
			SelectedType = DIR;
			dirToCopy = thisFile;
		} else {
			SelectedType = FILE;
			fileToCopy = thisFile;
		}
	}

	// used for moving file from one to dir to another
	public void CatFile(File fC) {
		// set selected to cut
		Selected = CUT;
		File thisFile = fC;
		// check if dir or file
		if (thisFile.isDirectory()) {
			SelectedType = DIR;
			dirToMove = fC;
		} else {
			SelectedType = FILE;
			fileToMove = thisFile;
		}
	}

	// Checks if is cut or copied file and then calls the appropriate method
	public void PasteFile(File ph) throws IOException {
		if (ph == null) {
			return;
		}
		if (Selected == CUT) {
			MoveFile(ph);
		} else if (Selected == COPY) {
			PasteCopyFile(ph);
		}
	}

	// pastes the copied file in current folder
	public void PasteCopyFile(File ph) throws IOException {

		String newpath = "";
		if (ph == null) {
			return;
		}
		// if ph isn't a directory paste it to parent dir
		if (!ph.isDirectory()) {
			if (SelectedType == DIR) {
				newpath = ph.getParentFile().getAbsolutePath() + SLASH
						+ dirToCopy.getName();

			}
			if (SelectedType == FILE) {
				newpath = ph.getParentFile().getAbsolutePath() + SLASH
						+ fileToCopy.getName();
			}
		} else {
			if (SelectedType == DIR) {
				newpath = ph.getAbsolutePath() + SLASH + dirToCopy.getName();

			}
			if (SelectedType == FILE) {
				newpath = ph.getAbsolutePath() + SLASH + fileToCopy.getName();
			}
		}
		// if file exists delete it
		File pasteHere = new File(newpath);
		if (pasteHere.exists()) {
			Delete(pasteHere);
		}
		// If it's only a file just paste it
		if (SelectedType == FILE) {

			InputStream inStream = null;
			OutputStream outStream = null;
			byte[] buffer = new byte[1024];

			int length;

			try {
				inStream = new FileInputStream(fileToCopy);
				outStream = new FileOutputStream(pasteHere);
				while ((length = inStream.read(buffer)) > 0) {

					outStream.write(buffer, 0, length);

				}
			} catch (IOException e) {

				System.out
						.println("Sorry An Error Occured. Please Try Again Later: "
								+ e.getMessage());
			} finally {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			}
			// else start pasting files from copied directory
		} else {
			// create new dir
			pasteHere.mkdir();
			// start pasting files
			ReCopy(dirToCopy, pasteHere);
		}
	}

	// recursively paste files in destination folder
	private void ReCopy(File src, File dest) throws IOException {
		File toCreate;
		File newFolder = dest;
		String newpath;

		// for every file
		if (src.listFiles() != null)
			for (File k : src.listFiles()) {
				// if not directory just paste it
				if (!k.isDirectory()) {
					newpath = newFolder.getAbsolutePath() + SLASH + k.getName();
					toCreate = new File(newpath);
					FileChannel outputChannel = null;
					FileChannel inputChannel = null;
					toCreate.createNewFile();

					try {
						inputChannel = new FileInputStream(k).getChannel();
						outputChannel = new FileOutputStream(toCreate)
								.getChannel();
						outputChannel.transferFrom(inputChannel, 0,
								inputChannel.size());
					} finally {
						if (inputChannel != null) {
							inputChannel.close();
						}
						if (outputChannel != null) {
							outputChannel.close();
						}
					}
					// else search for every folder using recursion
				} else {
					String folderName = dest.getAbsolutePath() + SLASH
							+ k.getName();
					File CpiedFolder = new File(folderName);
					// first make new folder
					CpiedFolder.mkdir();
					ReCopy(k, CpiedFolder);

				}
			}

	}

	// MoveFile works exactly like PasteCopyFile
	// the only difference is that it deletes src file afterwards
	public void MoveFile(File ph) throws IOException {
		String newpath = "";
		// TODO
		// ADD LINUX CHECK CLAUSE HERE
		if (ph == null) {
			return;
		}
		if (!ph.isDirectory()) {
			if (SelectedType == DIR) {
				newpath = ph.getParentFile().getAbsolutePath() + SLASH
						+ dirToMove.getName();

			}
			if (SelectedType == FILE) {
				newpath = ph.getParentFile().getAbsolutePath() + SLASH
						+ fileToMove.getName();
			}
		} else {
			if (SelectedType == DIR) {
				newpath = ph.getAbsolutePath() + SLASH + dirToMove.getName();

			}
			if (SelectedType == FILE) {
				newpath = ph.getAbsolutePath() + SLASH + fileToMove.getName();
			}
		}

		File pasteHere = new File(newpath);

		if (pasteHere.exists()) {
			pasteHere.delete();
		}
		if (SelectedType == FILE) {

			InputStream inStream = null;
			OutputStream outStream = null;
			byte[] buffer = new byte[1024];

			int length;

			try {
				inStream = new FileInputStream(fileToMove);
				outStream = new FileOutputStream(pasteHere);
				while ((length = inStream.read(buffer)) > 0) {

					outStream.write(buffer, 0, length);

				}
			} catch (IOException e) {

				System.out
						.println("Sorry An Error Occured. Please Try Again Later: "
								+ e.getMessage());
			} finally {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
				fileToMove.delete();
			}
		} else {

			pasteHere.mkdir();
			ReCopy(dirToMove, pasteHere);
			// here lies difference from CopyPasteFile
			Delete(dirToMove);
			// No more difference
		}

	}

	// Renames a file
	public File RenameFile(File oP, String name) {
		String oldpath = oP.getAbsolutePath();
		String newpath = oldpath.substring(0, (oldpath.lastIndexOf("\\") + 1))
				+ name;

		// rename file
		File newName = new File(newpath);
		File oldFile = new File(oldpath);
		oldFile.renameTo(newName);
                
		// if file is a directory we need to inform the tree about the change
		if (newName.isDirectory()) {
			DefaultMutableTreeNode Kid = getNodeFromPath(oP.getAbsolutePath());
			DefaultMutableTreeNode Father = (DefaultMutableTreeNode) Kid
					.getParent();
			// delete old node
			treeModel.removeNodeFromParent(Kid);
			// store new value to Jtree
			Kid = new DefaultMutableTreeNode(name);
			// update changes to tree
			treeModel.insertNodeInto(Kid, Father, 0);
		}
		// returns new name for various logistic operations in Listener class
		return newName;
	}

	// Creates a brand new file
	public void CreateFile(DefaultMutableTreeNode currNode, String Name)
			throws IOException {

		// create file
		File newF = new File(getNodePath(currNode).trim() + SLASH + Name.trim() /*
																				 * +
																				 * ".txt"
																				 */);
		try {
			newF.createNewFile();
		} catch (IOException e) {
			throw e;
		}

	}

	// Creates a brand new file
	public void CreateFolder(DefaultMutableTreeNode currNode, String Name)
			throws IOException {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(Name);
		File newF;

		// insert new node into tree
		treeModel.insertNodeInto(newNode, currNode, 0);

		// create file
		newF = new File(getNodePath(currNode).trim() + SLASH + Name.trim());
		newF.mkdir();
	}

	public void Exit() {
		System.exit(0);
	}

	// Deletes the file asked
	public void Delete(File ph) {

		// get parent file to return to him after deletion
		// if file doesn't exist return
		if (!ph.exists()) {
			return;
		}

		// if it a directory use ReDelete Function
		if (ph.isDirectory()) {
			populateTree(getNodeFromPath(ph.getAbsolutePath()), ph, EXPLORE_ALL);
			ReDelete(ph);
			// remove this node from parent
			treeModel
					.removeNodeFromParent(getNodeFromPath(ph.getAbsolutePath()));
			// delete this dir
			ph.delete();
		}// else just delete file
		else {
			ph.delete();

		}
	}

	// Recursivelly deletes every file in dir
	private void ReDelete(File ph) {
		File thisFile = ph;
		// first Delete Files
		if (thisFile.listFiles() != null) {
			for (File k : thisFile.listFiles()) {
				if (!k.isDirectory()) {
					k.delete();
				} else {

					ReDelete(k);

				}
			}
		}

		ph.delete();

	}

	// Used for searching accepts as input user's search string and
	// returns a regular expression based on it
	private String RegexStringCreator(String toReg) {

		if (!toReg.startsWith("*")) {
			toReg = "^" + toReg;
		}
		if (!toReg.endsWith("*")) {

			toReg = toReg + "$";
		}

		toReg = toReg.replace("*", "(.*)");

		return toReg;

	}

	// Recursively find file from given name
	// this method is only used for single searches
	public void reFind(String name, File currNode) {
		Pattern nameF = Pattern.compile(name);

		// search for file in current node's list of files
		if (currNode.listFiles() != null) {
			for (File k : currNode.listFiles()) {
				Matcher finder = nameF
						.matcher(k.getName().toLowerCase().trim());
				// every time you find a file matching the criteria add it to
				// list
				if (finder.find()) {
					listModel.addElement(k);
				}
				if (k.isDirectory()) {
					reFind(name, k);
				}
			}
		}

	}

	// Searches for file. Can also search for patterns
	public void SearchForFile(String name, File currNode) {
		int index = 0;
		if (name == null) {
			return;
		}
		String path = name;
		// init list model
		listModel.clear();

		// populate tree after current node
		ArrayList<String> folders;

		folders = new ArrayList<>();

		// if it's only a file name search folders for it's name
		if (!path.contains(SLASH)) {
			path = RegexStringCreator(path.toLowerCase());

			reFind(path, currNode);

		}// if it's a path do something more complicated
		else {

			// if file exists just add it to list and end method
			if (new File(path).exists()) {
				listModel.addElement(new File(path));
				return;
			} else if (new File(currNode + SLASH + path).exists()) {
				listModel.addElement(new File(path));
				return;
			}
			// if it doesn't exist we mustn't give up hope
			// it might me a regular expression
			// for example something like /pat+/t*/Fi*/*.txt

			// useful for parsing
			if (path.startsWith(SLASH)) {
				path = path.substring(1, path.length());
			}

			// split to path folders
			path = path.replace(SLASH, "/");
			for (String folder : path.split("/")) {
				folder = folder.trim();
				folder = RegexStringCreator(folder).toLowerCase();
				folders.add(0, folder);
				index++;
			}

			index -= 1;

			// find every file that meats criteria
			reFindFolder(folders, currNode, index);

		}
	}

	// follow path to find asked file
	// this method is used if given search form is a path (which contains
	// regular expressions)
	public void reFindFolder(ArrayList<String> folders, File currNode, int index) {
		String nameTF = folders.get(index);
		Pattern nameF = Pattern.compile(nameTF);

		if (currNode.listFiles() != null) {
			for (File k : currNode.listFiles()) {

				Matcher finder = nameF.matcher(k.getName().toLowerCase());
				if (finder.find()) {
					// add element to list only if you ve reached the end of the
					// path
					if (index == 0) {
						listModel.addElement(k);
					}// else search furthermore
					else if (k.isDirectory()) {
						int newInt = index - 1;
						reFindFolder(folders, k, newInt);
					}
				}
			}
		}
	}

}
