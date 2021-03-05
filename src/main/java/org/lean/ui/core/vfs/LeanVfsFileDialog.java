package org.lean.ui.core.vfs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.core.plugins.IPlugin;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.vfs.HopVfs;
import org.apache.hop.history.AuditList;
import org.apache.hop.history.AuditManager;
import org.apache.hop.ui.core.gui.HopNamespace;
import org.apache.hop.ui.hopgui.file.HopFileTypePluginType;
import org.apache.hop.ui.hopgui.file.HopFileTypeRegistry;
import org.apache.hop.ui.hopgui.file.IHopFileType;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElement;
import org.lean.ui.core.ConstUi;
import org.lean.ui.core.PropsUi;
import org.lean.ui.core.component.TextVar;
import org.lean.ui.core.dialog.IDirectoryDialog;
import org.lean.ui.core.dialog.IFileDialog;
import org.lean.ui.core.gui.GuiResource;
import org.lean.ui.core.gui.GuiToolbarWidgets;
import org.lean.ui.core.gui.vaadin.components.toolbar.LeanToolbar;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.leangui.file.LeanFileTypeRegistry;
import org.lean.ui.leangui.file.presentation.ILeanFileType;
import org.springframework.expression.spel.ast.ValueRef;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@GuiPlugin(description = "Allows you to browse to server of VFS locations")
@VaadinSessionScope
public class LeanVfsFileDialog extends Dialog implements IFileDialog, IDirectoryDialog {

    private static final Class<?> PKG = LeanVfsFileDialog.class;

    private LeanGuiLayout leanGuiLayout;

    public static final String BOOKMARKS_AUDIT_TYPE = "vfs-bookmarks";

    public static final String BOOKMARKS_TOOLBAR_PARENT_ID = "LeanVfsFileDialog-BookmarksToolbar";
    private static final String BOOKMARKS_ITEM_ID_BOOKMARKS = "0000-bookmarks";
    private static final String BOOKMARKS_ITEM_ID_BOOKMARK_ADD = "0010-bookmark-add";
    private static final String BOOKMARKS_ITEM_ID_BOOKMARK_GOTO = "0020-bookmark-goto";
    private static final String BOOKMARKS_ITEM_ID_BOOKMARK_REMOVE = "0030-bookmark-remove";

    public static final String NAVIGATE_TOOLBAR_PARENT_ID = "LeanVfsFileDialog-NavigateToolbar";
    private static final String NAVIGATE_ITEM_ID_NAVIGATE_HOME = "0000-navigate-home";
    private static final String NAVIGATE_ITEM_ID_NAVIGATE_UP = "0010-navigate-up";
    private static final String NAVIGATE_ITEM_ID_NAVIGATE_PREVIOUS = "0100-navigate-previous";
    private static final String NAVIGATE_ITEM_ID_NAVIGATE_NEXT = "0110-navigate-next";
    private static final String NAVIGATE_ITEM_ID_REFRESH_ALL = "9999-refresh-all";

    public static final String BROWSER_TOOLBAR_PARENT_ID = "LeanVfsFileDialog-BrowserToolbar";
    private static final String BROWSER_ITEM_ID_CREATE_FOLDER = "0020-create-folder";
    private static final String BROWSER_ITEM_ID_SHOW_HIDDEN = "0200-show-hidden";

    private IVariables variables;
    private String text;
    private String fileName;
    private String filterPath;
    private String[] filterExtensions;
    private String[] filterNames;

    private PropsUi props;

    private List wBookmarks;
    private TextVar wFilename;

    private Text wDetails;
    private TreeGrid wBrowser;

    private boolean showingHiddenFiles;

    Map<String, FileObject> fileObjectsMap;

    private Map<String, String> bookmarks;
    private FileObject activeFileObject;
    private FileObject activeFolder;

    private Image folderImage;
    private Image fileImage;

    private static LeanVfsFileDialog instance;
    private FileObject selectedFile;

    private java.util.List<String> navigationHistory;
    private int navigationIndex;

    private LeanToolbar navigateToolbar, bookmarksToolbar, browserToolbar;

    private GuiToolbarWidgets navigateToolbarWidgets;
    private GuiToolbarWidgets browserToolbarWidgets;
    private GuiToolbarWidgets bookmarksToolbarWidgets;
    private Button wOk;
    private SplitLayout sashForm;
    private ComboBox<String> wFilters;
    private String message;

    private boolean browsingDirectories;
    private boolean savingFile;
    private String saveFilename;

    private int sortIndex = 0;
    private boolean ascending = true;

    private String usedNamespace;

    private LeanVfsFileDialog(){

    }

    public LeanVfsFileDialog(
            LeanGuiLayout leanGuiLayout,
            IVariables variables,
            FileObject fileObject,
            boolean browsingDirectories,
            boolean savingFile) {

        this.leanGuiLayout = leanGuiLayout;
        this.variables = variables;
        this.browsingDirectories = browsingDirectories;
        this.savingFile = savingFile;
        this.setWidth("50%");
        this.setHeight("50%");

        this.fileName = fileName == null ? null : HopVfs.getFilename(fileObject);

        if (this.variables == null) {
            this.variables = leanGuiLayout.getVariables();
        }
        props = PropsUi.getInstance();

        if (props.useGlobalFileBookmarks()) {
            usedNamespace = LeanGuiLayout.DEFAULT_LEAN_GUI_NAMESPACE;
        } else {
            usedNamespace = HopNamespace.getNamespace();
        }

        try {

            bookmarks =
                    AuditManager.getActive().loadMap(usedNamespace, BOOKMARKS_AUDIT_TYPE);
        } catch (Exception e) {
            LogChannel.GENERAL.logError("Error loading bookmarks", e);
            bookmarks = new HashMap<>();
        }

        try {
            AuditList auditList =
                    AuditManager.getActive().retrieveList(usedNamespace, BOOKMARKS_AUDIT_TYPE);
            navigationHistory = auditList.getNames();
        } catch (Exception e) {
            LogChannel.GENERAL.logError("Error loading navigation history", e);
            navigationHistory = new ArrayList<>();
        }
        navigationIndex = navigationHistory.size() - 1;

        fileImage = GuiResource.getInstance().getImageFile();
        folderImage = GuiResource.getInstance().getImageFolder();

        VerticalLayout fileDialogLayout = new VerticalLayout();

        // set the dialog's top bar
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setId("vfs-file-dialog-toplayout");
        topLayout.setHeight(ConstUi.HBAR_HEIGHT);
        topLayout.setWidthFull();

        navigateToolbar = new LeanToolbar(LeanToolbar.ORIENTATION.HORIZONTAL);
        navigateToolbarWidgets = new GuiToolbarWidgets(leanGuiLayout.getLeanGuiLayoutId());
        navigateToolbarWidgets.registerGuiPluginObject(this);
        navigateToolbarWidgets.createToolbarWidgets(navigateToolbar, NAVIGATE_TOOLBAR_PARENT_ID);
        wFilename = new TextVar();
        wFilename.setWidthFull();

        topLayout.add(navigateToolbar, wFilename);

        if(!browsingDirectories){
            wFilters = new ComboBox<String>();
            if(filterNames != null){
                wFilters.setItems(filterNames);
            }
            topLayout.add(wFilters);
        }


        // set the dialog body
        SplitLayout bodyLayout = new SplitLayout();
        bodyLayout.setId("vfs-file-dialog-bodylayout");
        bodyLayout.setSizeFull();

        VerticalLayout bookmarksVL = new VerticalLayout();
        bookmarksVL.setId("bookmarks-layout");
        bookmarksVL.setSizeFull();
        bookmarksToolbar = new LeanToolbar(LeanToolbar.ORIENTATION.HORIZONTAL);
        bookmarksToolbar.setId("bookmarks-toolbar");
        bookmarksToolbarWidgets = new GuiToolbarWidgets(leanGuiLayout.getLeanGuiLayoutId());
        bookmarksToolbarWidgets.createToolbarWidgets(bookmarksToolbar, BOOKMARKS_TOOLBAR_PARENT_ID);
        Div bookmarksContentDiv = new Div();
        bookmarksVL.add(bookmarksToolbar, bookmarksContentDiv);

        VerticalLayout browserVL = new VerticalLayout();
        browserVL.setId("browser-layout");
        browserVL.setSizeFull();
        browserToolbar = new LeanToolbar(LeanToolbar.ORIENTATION.HORIZONTAL);
        browserToolbar.setId("browser-toolbar");
        browserToolbarWidgets = new GuiToolbarWidgets(leanGuiLayout.getLeanGuiLayoutId());
        browserToolbarWidgets.createToolbarWidgets(browserToolbar, BROWSER_TOOLBAR_PARENT_ID);

        Div browserDiv = new Div();
        browserDiv.setId("browser-main");
        browserDiv.setSizeFull();
        browserDiv.add(new Label("Lean File Browser"));

        browserVL.add(browserToolbar, browserDiv);
        bodyLayout.addToPrimary(bookmarksVL);
        bodyLayout.addToSecondary(browserVL);

        // set the dialog bottom layout
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setId("vfs-file-dialog-bottomlayout");

        Button openButton = new Button("Open");
        Button cancelButton = new Button("Cancel");
        FlexLayout buttonWrapper = new FlexLayout(openButton, cancelButton);
//        FlexLayout cancelButtonWrapper = new FlexLayout(cancelButton);
        buttonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//        cancelButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        bottomLayout.add(buttonWrapper);

        fileDialogLayout.add(topLayout, bodyLayout, bottomLayout);
        add(fileDialogLayout);
    }

    /**
     * Gets the active instance of this dialog
     *
     * @return value of instance
     */

    public static LeanVfsFileDialog getInstance(){ return instance; }

//    @Override
//    public void open(){
//
//    }

    private void browserColumnSelected(final int index) {
        if (index == sortIndex) {
            ascending = !ascending;
        } else {
            sortIndex = index;
            ascending = true;
        }
//        wBrowser.setSortColumn(wBrowser.getColumn(index));
//        wBrowser.setSortDirection(ascending ? SWT.DOWN : SWT.UP);

        refreshBrowser();
    }

    private void fileFilterSelected(/*Event event*/) {
        refreshBrowser();
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    //  @GuiToolbarElement(
    //    root = BOOKMARKS_TOOLBAR_PARENT_ID,
    //    id = BOOKMARKS_ITEM_ID_BOOKMARK_GOTO,
    //    toolTip = "Browse to the selected bookmark",
    //    image = "ui/images/arrow-right.svg"
    //  )
    public void browseToSelectedBookmark() {
        String name = getSelectedBookmark();
        if (name == null) {
            return;
        }
        String path = bookmarks.get(name);
        if (path != null) {
            navigateTo(path, true);
        }
    }

    private String getSelectedBookmark() {
/*
        int selectionIndex = wBookmarks.getSelectionIndex();
        if (selectionIndex < 0) {
            return null;
        }
        String name = wBookmarks.getItems()[selectionIndex];
        return name;
*/
        return null;
    }

    /**
     * User double clicked on a bookmark
     *
//     * @param event
     */
    private void bookmarkDefaultSelection(/*Event event*/) {
        browseToSelectedBookmark();
    }

    private void okButton() {
        try {
            activeFileObject = HopVfs.getFileObject(wFilename.getValue());
            ok();
        } catch (Throwable e) {
            showError("Error parsing filename: '" + wFilename.getValue(), e);
        }
    }

    private void enteredFilenameOrFolder() {
        if (StringUtils.isNotEmpty(saveFilename)) {
            try {
                FileObject fullObject = HopVfs.getFileObject(wFilename.getValue());
                if (!fullObject.isFolder()) {
                    // We're saving a filename and now if we hit enter we want this to select the file and
                    // close the dialog
                    //
                    activeFileObject = fullObject;
                    ok();
                    return;
                }
            } catch (Exception e) {
                // Ignore error, just try to refresh and the error will be listed in the message widget
            }
        }
        refreshBrowser();
    }

    private FileObject getSelectedFileObject() {
/*
        TreeItem[] selection = wBrowser.getSelection();
        if (selection == null || selection.length != 1) {
            return null;
        }

        String path = getTreeItemPath(selection[0]);
        FileObject fileObject = fileObjectsMap.get(path);
        return fileObject;
*/
        return null;
    }

    /**
     * Something is selected in the browser
     *
//     * @param e
     */
    private void fileSelected(/*Event e*/) {
        FileObject fileObject = getSelectedFileObject();
        if (fileObject != null) {
            selectedFile = fileObject;
            showFilename(selectedFile);
        }
    }

    private void showFilename(FileObject fileObject) {
        try {
            wFilename.setValue(HopVfs.getFilename(fileObject));

            FileContent content = fileObject.getContent();

            String details = "";

            if (fileObject.isFolder()) {
                details += "Folder: " + HopVfs.getFilename(fileObject) + Const.CR;
            } else {
                details += "Name: " + fileObject.getName().getBaseName() + "   ";
                details += "Folder: " + HopVfs.getFilename(fileObject.getParent()) + "   ";
                details += "Size: " + content.getSize();
                if (content.getSize() >= 1024) {
                    details += " (" + getFileSize(fileObject) + ")";
                }
                details += Const.CR;
            }
            details += "Last modified: " + getFileDate(fileObject) + Const.CR;
            details += "Readable: " + (fileObject.isReadable() ? "Yes" : "No") + "  ";
            details += "Writable: " + (fileObject.isWriteable() ? "Yes" : "No") + "  ";
            details += "Executable: " + (fileObject.isExecutable() ? "Yes" : "No") + Const.CR;
            if (fileObject.isSymbolicLink()) {
                details += "This is a symbolic link" + Const.CR;
            }
            Map<String, Object> attributes = content.getAttributes();
            if (attributes != null && !attributes.isEmpty()) {
                details += "Attributes: " + Const.CR;
                for (String key : attributes.keySet()) {
                    Object value = attributes.get(key);
                    details += "   " + key + " : " + (value == null ? "" : value.toString()) + Const.CR;
                }
            }
            showDetails(details);
        } catch (Throwable e) {
            showError("Error getting information on file " + fileObject.toString(), e);
        }
    }

    /**
     * Double clicked on a file or folder
     *
//     * @param event
     */
    private void fileDefaultSelected(/*Event event*/) {
        FileObject fileObject = getSelectedFileObject();
        if (fileObject == null) {
            return;
        }

        try {
            navigateTo(HopVfs.getFilename(fileObject), true);

            if (fileObject.isFolder()) {
                // Browse into the selected folder...
                //
                refreshBrowser();
            } else {
                // Take this file as the user choice for this dialog
                //
                okButton();
            }
        } catch (Throwable e) {
            showError("Error handling default selection on file " + fileObject.toString(), e);
        }
    }

    private void getData() {

        // Take the first by default: All types
        //
        if (!browsingDirectories) {
//            wFilters.select(0);
        }

        refreshBookmarks();

        if (StringUtils.isEmpty(fileName)) {
            if (StringUtils.isEmpty(filterPath)) {
                // Default to the user home directory
                //
                fileName = System.getProperty("user.home");
            } else {
                fileName = filterPath;
            }
        }

        showDetails(message);

        navigateTo(fileName, true);
        browserColumnSelected(0);
    }

    private void showDetails(String details) {
        wDetails.setText(Const.NVL(details, Const.NVL(message, "")));
    }

    private void refreshBookmarks() {
        // Add the bookmarks
        //
        java.util.List<String> bookmarkNames = new ArrayList<>(bookmarks.keySet());
        Collections.sort(bookmarkNames);
//        wBookmarks.setItems(bookmarkNames.toArray(new String[0]));
    }



    /**
     * Gets text
     *
     * @return value of text
     */
    public String getText() {
        return text;
    }

//    /** @param text The text to set */
    @Override
    public void setText(String s) {

    }

    /**
     * Gets variables
     *
     * @return value of variables
     */
    public IVariables getVariables() {
        return variables;
    }

    /** @param variables The variables to set */
    public void setVariables(IVariables variables) {
        this.variables = variables;
    }

    /**
     * Gets fileName
     *
     * @return value of fileName
     */
    @Override
    public String getFileName() {
        this.open();
        return fileName;
    }

    /** @param fileName The fileName to set */
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets filterExtensions
     *
     * @return value of filterExtensions
     */
    public String[] getFilterExtensions() {
        return filterExtensions;
    }

    /** @param filterExtensions The filterExtensions to set */
    public void setFilterExtensions(String[] filterExtensions) {
        this.filterExtensions = filterExtensions;
    }

    /**
     * Gets filterNames
     *
     * @return value of filterNames
     */
    public String[] getFilterNames() {
        return filterNames;
    }

    /** @param filterNames The filterNames to set */
    public void setFilterNames(String[] filterNames) {
        this.filterNames = filterNames;
    }

    /**
     * Gets bookmarks
     *
     * @return value of bookmarks
     */
    public Map<String, String> getBookmarks() {
        return bookmarks;
    }

    /** @param bookmarks The bookmarks to set */
    public void setBookmarks(Map<String, String> bookmarks) {
        this.bookmarks = bookmarks;
    }

    /**
     * Gets activeFileObject
     *
     * @return value of activeFileObject
     */
    public FileObject getActiveFileObject() {
        return activeFileObject;
    }

    /** @param activeFileObject The activeFileObject to set */
    public void setActiveFileObject(FileObject activeFileObject) {
        this.activeFileObject = activeFileObject;
    }

    /**
     * Gets activeFolder
     *
     * @return value of activeFolder
     */
    public FileObject getActiveFolder() {
        return activeFolder;
    }

    /** @param activeFolder The activeFolder to set */
    public void setActiveFolder(FileObject activeFolder) {
        this.activeFolder = activeFolder;
    }

    /**
     * Gets filterPath
     *
     * @return value of filterPath
     */
    @Override
    public String getFilterPath() {
        return filterPath;
    }

    /** @param filterPath The filterPath to set */
    public void setFilterPath(String filterPath) {
        this.filterPath = filterPath;
    }

    /**
     * Gets showingHiddenFiles
     *
     * @return value of showingHiddenFiles
     */
    public boolean isShowingHiddenFiles() {
        return showingHiddenFiles;
    }

    /** @param showingHiddenFiles The showingHiddenFiles to set */
    public void setShowingHiddenFiles(boolean showingHiddenFiles) {
        this.showingHiddenFiles = showingHiddenFiles;
    }

    /**
     * Gets message
     *
     * @return value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets browsingDirectories
     *
     * @return value of browsingDirectories
     */
    public boolean isBrowsingDirectories() {
        return browsingDirectories;
    }

    /** @param browsingDirectories The browsingDirectories to set */
    public void setBrowsingDirectories(boolean browsingDirectories) {
        this.browsingDirectories = browsingDirectories;
    }

    /**
     * Gets savingFile
     *
     * @return value of savingFile
     */
    public boolean isSavingFile() {
        return savingFile;
    }

    /** @param savingFile The savingFile to set */
    public void setSavingFile(boolean savingFile) {
        this.savingFile = savingFile;
    }

    /**
     * Gets saveFilename
     *
     * @return value of saveFilename
     */
    public String getSaveFilename() {
        return saveFilename;
    }

    /** @param saveFilename The saveFilename to set */
    public void setSaveFilename(String saveFilename) {
        this.saveFilename = saveFilename;
    }

    private void saveBookmarks() {
        try {
            AuditManager.getActive()
                    .saveMap(usedNamespace, BOOKMARKS_AUDIT_TYPE, bookmarks);
        } catch (Throwable e) {
            showError("Error saving bookmarks: '" + activeFileObject.toString(), e);
        }
    }

    public void navigateTo(String filename, boolean saveHistory) {
        if (saveHistory) {
            // Add to navigation history
            //
            if (navigationIndex >= 0) {
                if (navigationIndex < navigationHistory.size()) {
                    // Clear history above the index...
                    //
                    navigationHistory.subList(navigationIndex, navigationHistory.size());
                }
            }

            navigationHistory.add(filename);
            navigationIndex = navigationHistory.size() - 1;
        }

        if (StringUtils.isEmpty(saveFilename)) {
            wFilename.setValue(filename);
        } else {
            try {
                // Save the "saveFilename" entered text by the user?
                //
                String oldFull = wFilename.getValue();
                if (StringUtils.isNotEmpty(oldFull)) {
                    try {
                        FileObject oldFullObject = HopVfs.getFileObject(oldFull);
                        if (!oldFullObject.isFolder()) {
                            saveFilename = oldFullObject.getName().getBaseName();
                        }
                    } catch (Exception e) {
                        // This wasn't a valid filename, ignore the error to reduce spamming
                    }
                } else {
                    // First call, set to filter path plus saveFilename
                    //
                    if (StringUtils.isNotEmpty(filterPath)) {
                        wFilename.setValue(filterPath + "/" + saveFilename);
                    }
                }

                if (HopVfs.getFileObject(filename).isFolder()) {
                    String fullPath = FilenameUtils.concat(filename, saveFilename);
                    wFilename.setValue(fullPath);
                    // Select the saveFilename part...
                    //
                    int start = fullPath.lastIndexOf(saveFilename);
                    int end = fullPath.lastIndexOf(".");
//                    wFilename.getTextWidget().setSelection(start, end);
//                    wFilename.setFocus();
                }
            } catch (Exception e) {
                wFilename.setValue(filename);
            }
        }
        refreshBrowser();
//        refreshStates();
//        resizeTableColumn();
    }

/*
    private void showDetails(String details) {
        wDetails.setText(Const.NVL(details, Const.NVL(message, "")));
    }

    private void refreshBookmarks() {
        // Add the bookmarks
        //
        java.util.List<String> bookmarkNames = new ArrayList<>(bookmarks.keySet());
        Collections.sort(bookmarkNames);
        wBookmarks.setItems(bookmarkNames.toArray(new String[0]));
    }
*/

    private void refreshBrowser() {
        String filename = wFilename.getValue();
        if (StringUtils.isEmpty(filename)) {
            return;
        }

        // Browse to the selected file location...
        //
        try {
            activeFileObject = HopVfs.getFileObject(filename);
            if (activeFileObject.isFolder()) {
                activeFolder = activeFileObject;
            } else {
                activeFolder = activeFileObject.getParent();
            }
//            wBrowser.removeAll();

            fileObjectsMap = new HashMap<>();

//            TreeItem parentFolderItem = new TreeItem(wBrowser, SWT.NONE);
//            parentFolderItem.setImage(folderImage);
//            parentFolderItem.setText(activeFolder.getName().getBaseName());
//            fileObjectsMap.put(getTreeItemPath(parentFolderItem), activeFolder);

//            populateFolder(activeFolder, parentFolderItem);
//
//            parentFolderItem.setExpanded(true);
        } catch (Throwable e) {
            showError("Error browsing to location: " + filename, e);
        }
    }

    private void showError(String string, Throwable e) {
        showDetails(
                string
                        + Const.CR
                        + Const.getSimpleStackTrace(e)
                        + Const.CR
                        + Const.CR
                        + Const.getClassicStackTrace(e));
    }

    private String getTreeItemPath(/*TreeItem item*/) {
/*
        String path = "/" + item.getText();
        TreeItem parentItem = item.getParentItem();
        while (parentItem != null) {
            path = "/" + parentItem.getText() + path;
            parentItem = parentItem.getParentItem();
        }
        String filename = item.getText(0);
        if (StringUtils.isNotEmpty(filename)) {
            path += filename;
        }
        return path;
*/
        return null;
    }

    /**
     * Child folders are always shown at the top. Files below it sorted alphabetically
     *
     * @param folder
//     * @param folderItem
     */
    private void populateFolder(FileObject folder/*, TreeItem folderItem*/) throws FileSystemException {

        FileObject[] children = folder.getChildren();

        Arrays.sort(
                children,
                (child1, child2) -> {
                    try {
                        int cmp;
                        switch (sortIndex) {
                            case 0:
                                String name1 = child1.getName().getBaseName();
                                String name2 = child2.getName().getBaseName();
                                cmp = name1.compareToIgnoreCase(name2);
                                break;
                            case 1:
                                long time1 = child1.getContent().getLastModifiedTime();
                                long time2 = child2.getContent().getLastModifiedTime();
                                cmp = Long.compare(time1, time2);
                                break;
                            case 2:
                                long size1 = child1.getContent().getSize();
                                long size2 = child2.getContent().getSize();
                                cmp = Long.compare(size1, size2);
                                break;

                            default:
                                cmp = 0;
                        }
                        if (ascending) {
                            return -cmp;
                        } else {
                            return cmp;
                        }
                    } catch (Exception e) {
                        return 0;
                    }
                });

        // First the child folders
        //
        for (FileObject child : children) {
            if (child.isFolder()) {
                String baseFilename = child.getName().getBaseName();
                if (!showingHiddenFiles && baseFilename.startsWith(".")) {
                    continue;
                }
/*
                TreeItem childFolderItem = new TreeItem(folderItem, SWT.NONE);
                childFolderItem.setImage(folderImage);
                childFolderItem.setText(child.getName().getBaseName());
                fileObjectsMap.put(getTreeItemPath(childFolderItem), child);
*/
            }
        }
        if (!browsingDirectories) {
            for (final FileObject child : children) {
                if (child.isFile()) {
                    String baseFilename = child.getName().getBaseName();
                    if (!showingHiddenFiles && baseFilename.startsWith(".")) {
                        continue;
                    }

                    boolean selectFile = false;

                    // Check file extension...
                    //
//                    String selectedExtensions = filterExtensions[wFilters.getSelectionIndex()];
//                    String[] exts = selectedExtensions.split(";");
/*
                    for (String ext : exts) {
                        if (FilenameUtils.wildcardMatch(baseFilename, ext)) {
                            selectFile = true;
                        }
                    }
*/

                    // Hidden file?
                    //
/*
                    if (selectFile) {
                        TreeItem childFileItem = new TreeItem(folderItem, SWT.NONE);
                        childFileItem.setImage(getFileImage(child));
                        childFileItem.setFont(org.apache.hop.ui.core.gui.GuiResource.getInstance().getFontBold());
                        childFileItem.setText(0, child.getName().getBaseName());
                        childFileItem.setText(1, getFileDate(child));
                        childFileItem.setText(2, getFileSize(child));
                        fileObjectsMap.put(getTreeItemPath(childFileItem), child);

                        // Gray out if the file is not readable
                        //
                        if (!child.isReadable()) {
                            childFileItem.setForeground(org.apache.hop.ui.core.gui.GuiResource.getInstance().getColorGray());
                        }

                        if (child.equals(activeFileObject)) {
                            wBrowser.setSelection(childFileItem);
                            wBrowser.showSelection();
                        }
                    }
*/
                }
            }
        }
    }

    private Image getFileImage(FileObject file) {
        try {
            ILeanFileType fileType =
                    LeanFileTypeRegistry.getInstance().findLeanFileType(file.getName().getBaseName());
            if (fileType != null) {
                IPlugin plugin =
                        PluginRegistry.getInstance().getPlugin(HopFileTypePluginType.class, fileType);
                if (plugin != null && plugin.getImageFile() != null) {
                    return GuiResource.getInstance().getImage(plugin.getImageFile(), ConstUi.SMALL_ICON_SIZE, ConstUi.SMALL_ICON_SIZE);
                }
            }
        } catch (HopException e) {
            // Ignore
        }

        return fileImage;
    }

    private String getFileSize(FileObject child) {
        try {
            long size = child.getContent().getSize();
            String[] units = {"", " kB", " MB", " GB", " TB", " PB", " XB", " YB", " ZB"};
            for (int i = 0; i < units.length; i++) {
                double unitSize = Math.pow(1024, i);
                double maxSize = Math.pow(1024, i + 1);
                if (size < maxSize) {
                    return new DecimalFormat("0.#").format(size / unitSize) + units[i];
                }
            }
            return Long.toString(size);
        } catch (Exception e) {
            LogChannel.GENERAL.logError("Error getting size of file : " + child.toString(), e);
            return "?";
        }
    }

    private String getFileDate(FileObject child) {
        try {
            long lastModifiedTime = child.getContent().getLastModifiedTime();
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(lastModifiedTime));
        } catch (Exception e) {
            LogChannel.GENERAL.logError(
                    "Error getting last modified date of file : " + child.toString(), e);
            return "?";
        }
    }

    private void cancel() {
        activeFileObject = null;
        dispose();
    }

    private void ok() {
        try {
            if (activeFileObject.isFolder()) {
                filterPath = HopVfs.getFilename(activeFileObject);
                fileName = null;
            } else {
                filterPath = HopVfs.getFilename(activeFileObject.getParent());
                fileName = activeFileObject.getName().getBaseName();
            }
            dispose();
        } catch (FileSystemException e) {
            showError("Error finding parent folder of file: '" + activeFileObject.toString(), e);
        }
    }

    private void dispose() {
        instance = null;
        try {
            AuditManager.getActive()
                    .storeList(
                            usedNamespace, BOOKMARKS_AUDIT_TYPE, new AuditList(navigationHistory));
        } catch (Exception e) {
            LogChannel.GENERAL.logError("Error storing navigation history", e);
        }
//        props.setScreen(new WindowProperty(shell));

        // We no longer need the toolbar or the objects it used to listen to the buttons
        //
//        bookmarksToolbarWidgets.dispose();
//        browserToolbarWidgets.dispose();
//
//        shell.dispose();
    }

    @GuiToolbarElement(
            root = BOOKMARKS_TOOLBAR_PARENT_ID,
            id = BOOKMARKS_ITEM_ID_BOOKMARK_ADD,
            toolTip = "Add the selected file or folder as a new bookmark",
            image = "frontend/images/bookmark-add.svg"
    )
    public void addBookmark(){}

    @GuiToolbarElement(
            root = BOOKMARKS_TOOLBAR_PARENT_ID,
            id = BOOKMARKS_ITEM_ID_BOOKMARK_REMOVE,
            toolTip = "Remove the selected bookmark",
            image = "frontend/images/delete.svg"
    )
    public void removeBookmark(){}


    @GuiToolbarElement(
            root = NAVIGATE_TOOLBAR_PARENT_ID,
            id = NAVIGATE_ITEM_ID_NAVIGATE_HOME,
            toolTip = "Navigate to the user home directory",
            image = "frontend/images/home.svg"
    )
    public void navigateHome(){ navigateTo(System.getProperty("user.home"), true); }

    @GuiToolbarElement(
            root = NAVIGATE_TOOLBAR_PARENT_ID,
            id = NAVIGATE_ITEM_ID_REFRESH_ALL,
            toolTip = "Refresh",
            image = "frontend/images/refresh.svg"
    )
    public void refreshAll(){
        refreshBookmarks();
        refreshBrowser();
    }

    @GuiToolbarElement(
            root = NAVIGATE_TOOLBAR_PARENT_ID,
            id = NAVIGATE_ITEM_ID_NAVIGATE_UP,
            toolTip = "Navigate to the parent folder",
            image = "frontend/images/navigate-up.svg"
    )
    public void navigateUp(){
        try {
            FileObject fileObject = HopVfs.getFileObject(wFilename.getValue());
            if (fileObject.isFile()) {
                fileObject = fileObject.getParent();
            }
            FileObject parent = fileObject.getParent();
            if (parent != null) {
                navigateTo(HopVfs.getFilename(parent), true);
            }
        } catch (Throwable e) {
            showError("Error navigating up: '" + activeFileObject.toString(), e);
        }
    }

    @GuiToolbarElement(
            root = BROWSER_TOOLBAR_PARENT_ID,
            id = BROWSER_ITEM_ID_CREATE_FOLDER,
            toolTip = "Create folder",
            image = "frontend/images/folder-add.svg")
    public void createFolder() {
        String folder = "";
/*
        EnterStringDialog dialog =
                new EnterStringDialog(
                        folder,
                        "Create directory",
                        "Please enter name of the folder to create in : " + activeFolder);
        folder = dialog.open();
        if (folder != null) {
            String newPath = activeFolder.toString();
            if (!newPath.endsWith("/") && !newPath.endsWith("\\")) {
                newPath += "/";
            }
            newPath += folder;
            try {
                FileObject newFolder = HopVfs.getFileObject(newPath);
                newFolder.createFolder();
                refreshBrowser();
            } catch (Throwable e) {
                showError("Error creating folder '" + newPath + "'", e);
            }
        }
*/
    }

    @GuiToolbarElement(
            root = NAVIGATE_TOOLBAR_PARENT_ID,
            id = NAVIGATE_ITEM_ID_NAVIGATE_PREVIOUS,
            toolTip = "Navigate to previous path from your history",
            image = "frontend/images/navigate-back.svg",
            separator = true)
    public void navigateHistoryPrevious() {
        if (navigationIndex - 1 >= 0) {
            navigationIndex--;
            navigateTo(navigationHistory.get(navigationIndex), false);
        }
    }

    @GuiToolbarElement(
            root = NAVIGATE_TOOLBAR_PARENT_ID,
            id = NAVIGATE_ITEM_ID_NAVIGATE_NEXT,
            toolTip = "Navigate to next path from your history",
            image = "frontend/images/navigate-forward.svg")
    public void navigateHistoryNext() {
        if (navigationIndex + 1 < navigationHistory.size() - 1) {
            navigationIndex++;
            navigateTo(navigationHistory.get(navigationIndex), false);
        }
    }

    @GuiToolbarElement(
            root = BROWSER_TOOLBAR_PARENT_ID,
            id = BROWSER_ITEM_ID_SHOW_HIDDEN,
            toolTip = "Show or hide hidden files and directories",
            image = "frontend/images/show.svg",
            separator = true)
    public void showHideHidden() {
        showingHiddenFiles = !showingHiddenFiles;

        refreshBrowser();
    }

}
