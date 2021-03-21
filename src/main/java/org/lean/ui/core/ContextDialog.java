package org.lean.ui.core;

import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.apache.hop.core.SwtUniversalImage;
import org.apache.hop.core.config.HopConfig;
import org.apache.hop.core.gui.AreaOwner;
import org.apache.hop.core.gui.Point;
import org.apache.hop.core.gui.Rectangle;
import org.apache.hop.core.gui.plugin.GuiPlugin;
//import org.apache.hop.core.gui.plugin.action.GuiAction;
import org.apache.hop.core.gui.plugin.action.GuiAction;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.history.AuditManager;
import org.apache.hop.history.AuditState;
import org.apache.hop.ui.core.gui.HopNamespace;
import org.eclipse.swt.internal.C;
import org.lean.core.VaadinUniversalImage;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElement;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElementType;
import org.lean.ui.core.dialog.ErrorDialog;
import org.lean.ui.core.gui.GuiToolbarWidgets;
import org.lean.ui.core.gui.vaadin.components.toolbar.LeanToolbar;
import org.lean.ui.layout.LeanGuiLayout;
import org.lean.ui.util.VaadinSvgImageUtil;

import java.util.*;
import java.util.List;

@GuiPlugin(description = "This dialog presents you all the actions you can take in a given context")
//@VaadinSessionScope
public class ContextDialog extends Dialog {

    private static final String CONTEXT_DIALOG_ID = UUID.randomUUID().toString();
    private static LeanGuiLayout leanGuiLayout;
    public static final String CATEGORY_OTHER = "Other";

    public static final String GUI_PLUGIN_TOOLBAR_PARENT_ID = "ContextDialog-Toolbar";
    public static final String TOOLBAR_ITEM_COLLAPSE_ALL = "ContextDialog-Toolbar-10010-CollapseAll";
    public static final String TOOLBAR_ITEM_EXPAND_ALL = "ContextDialog-Toolbar-10020-ExpandAll";
    public static final String TOOLBAR_ITEM_ENABLE_CATEGORIES =
            "ContextDialog-Toolbar-10030-EnableCategories";
    public static final String TOOLBAR_ITEM_CLEAR_SEARCH = "ContextDialog-Toolbar-10040-ClearSearch";

    public static final String AUDIT_TYPE_TOOLBAR_SHOW_CATEGORIES = "ContextDialogShowCategories";
    public static final String AUDIT_TYPE_CONTEXT_DIALOG = "ContextDialog";
    public static final String AUDIT_NAME_CATEGORY_STATES = "CategoryStates";

    private Point location;
    private List<GuiAction> actions;
    private String contextId;
    private PropsUi props;
    private TextField wSearch;
    private TextArea wlTooltip;
//    private Canvas wCanvas;
//    private ScrolledComposite wScrolledComposite;

    private VerticalLayout contextDialogVl;

    private int iconSize;

    private int margin;
    private int xMargin;
    private int yMargin;

    private boolean shiftClicked;
    private boolean ctrlClicked;
    private boolean focusLost;

    /** All context items. */
    private final List<ContextDialog.Item> items = new ArrayList<>();

    /** List of filtered items. */
    private final List<ContextDialog.Item> filteredItems = new ArrayList<>();

    private ContextDialog.Item selectedItem;

    private GuiAction selectedAction;

    private List<AreaOwner<ContextDialog.OwnerType, Object>> areaOwners = new ArrayList<>();

//    private Color highlightColor;

    private int heightOffSet = 0;
    private int totalContentHeight = 0;
    private int previousTotalContentHeight = 0;
//    private Font headerFont;
//    private Font itemsFont;
    private ContextDialog.Item firstShownItem;
    private ContextDialog.Item lastShownItem;
    private LeanToolbar toolBar;
    private GuiToolbarWidgets toolBarWidgets;

    private static ContextDialog activeInstance;

    private FlexLayout itemsLayout;

    private enum OwnerType {
        CATEGORY,
        ITEM,
    }

    private class CategoryAndOrder {
        String category;
        String order;
        boolean collapsed;

        public CategoryAndOrder(String category, String order, boolean collapsed) {
            this.category = category;
            this.order = order;
            this.collapsed = collapsed;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ContextDialog.CategoryAndOrder that = (ContextDialog.CategoryAndOrder) o;
            return category.equals(that.category);
        }

        @Override
        public int hashCode() {
            return Objects.hash(category);
        }

        /**
         * Gets category
         *
         * @return value of category
         */
        public String getCategory() {
            return category;
        }

        /** @param category The category to set */
        public void setCategory(String category) {
            this.category = category;
        }

        /**
         * Gets order
         *
         * @return value of order
         */
        public String getOrder() {
            return order;
        }

        /** @param order The order to set */
        public void setOrder(String order) {
            this.order = order;
        }

        /**
         * Gets collapsed
         *
         * @return value of collapsed
         */
        public boolean isCollapsed() {
            return collapsed;
        }

        /** @param collapsed The collapsed to set */
        public void setCollapsed(boolean collapsed) {
            this.collapsed = collapsed;
        }

        public void flipCollapsed() {
            collapsed = !collapsed;
        }
    }

    private List<ContextDialog.CategoryAndOrder> categories;

    private static class Item {
        private GuiAction action;
        private Image image;
        private boolean selected;
        private AreaOwner<ContextDialog.OwnerType, Object> areaOwner;

        public Item(GuiAction action, Image image) {
            this.action = action;
            this.image = image;
            this.selected = false;
        }

        public GuiAction getAction() {
            return action;
        }

        public String getText() {
            return action.getShortName();
        }

        public Image getImage() {
            return image;
        }

        /**
         * Gets selected
         *
         * @return value of selected
         */
        public boolean isSelected() {
            return selected;
        }

        /** @param selected The selected to set */
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        /**
         * Gets areaOwner
         *
         * @return value of areaOwner
         */
        public AreaOwner getAreaOwner() {
            return areaOwner;
        }

        /** @param areaOwner The areaOwner to set */
        public void setAreaOwner(AreaOwner areaOwner) {
            this.areaOwner = areaOwner;
        }

/*
        public void dispose() {
            if (image != null) {
                image.dispose();
            }
        }
*/
    }

    public ContextDialog(LeanGuiLayout leanGuiLayout,
            String title, Point location, List<GuiAction> actions, String contextId) {
        super();

        this.leanGuiLayout = leanGuiLayout;

        this.setWidth("50vw");
        this.setHeight("50vh");
        this.setResizable(true);
//        this.setText(title);
        this.location = location;
        this.actions = actions;
        this.contextId = contextId;

        props = PropsUi.getInstance();

        shiftClicked = false;
        ctrlClicked = false;

        // Make the icons a bit smaller to fit more
        //
//        iconSize = (int) Math.round(props.getZoomFactor() * props.getIconSize() * 0.75);
        iconSize = 100;
        margin = (int) (Const.MARGIN * props.getZoomFactor());
//        highlightColor = new Color(parent.getDisplay(), 201, 232, 251);

        contextDialogVl = new VerticalLayout();
        add(contextDialogVl);

        // Let's take a look at the list of actions and see if we've got categories to use...
        //
        categories = new ArrayList<>();
        for (GuiAction action : actions) {
            if (StringUtils.isNotEmpty(action.getCategory())) {
                ContextDialog.CategoryAndOrder categoryAndOrder =
                        new ContextDialog.CategoryAndOrder(
                                action.getCategory(), Const.NVL(action.getCategoryOrder(), "0"), false);
                if (!categories.contains(categoryAndOrder)) {
                    categories.add(categoryAndOrder);
                }
            } else {
                // Add an "Other" category
                ContextDialog.CategoryAndOrder categoryAndOrder = new ContextDialog.CategoryAndOrder(CATEGORY_OTHER, "9999", false);
                if (!categories.contains(categoryAndOrder)) {
                    categories.add(categoryAndOrder);
                }
            }
        }

        categories.sort(Comparator.comparing(o -> o.order));

        // Add a description label at the bottom...
        //
        wlTooltip = new TextArea();
        wlTooltip.setWidthFull();
        wlTooltip.setHeight("10%");
        wlTooltip.setReadOnly(true);


        itemsLayout = new FlexLayout();
        itemsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        itemsLayout.setSizeFull();
//        HorizontalLayout tmpDiv = new HorizontalLayout();
//        tmpDiv.setSizeFull();



        // Create a toolbar at the top of the main composite...
        //
        toolBar = new LeanToolbar(LeanToolbar.ORIENTATION.HORIZONTAL);
        toolBarWidgets = new GuiToolbarWidgets(leanGuiLayout.getLeanGuiLayoutId());
        toolBarWidgets.registerGuiPluginObject(this);
        toolBarWidgets.createToolbarWidgets(toolBar, GUI_PLUGIN_TOOLBAR_PARENT_ID);
        contextDialogVl.add(toolBar);

        recallToolbarSettings();

        // Add a search bar at the top...
        //
        Label searchLabel = new Label("Search");
        TextField wlSearch = new TextField();
        wlSearch.setWidthFull();
        HorizontalLayout searchHL = new HorizontalLayout();
        searchHL.setHeight(ConstUi.HBAR_HEIGHT);
        searchHL.setWidthFull();
        searchHL.add(searchLabel, wlSearch);
        contextDialogVl.add(searchHL);

        contextDialogVl.add(itemsLayout);

        contextDialogVl.add(wlTooltip);


    }


    public GuiAction getSelectedAction(){
        return selectedAction;
    }

    public GuiAction openContextDialog() {

        xMargin = 3 * margin;
        yMargin = 2 * margin;

        // Load the action images
        //
        items.clear();
        for (GuiAction action : actions) {
            ClassLoader classLoader = action.getClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            Image image = new Image("frontend/images/" + action.getImage(), "");
            image.setWidth(iconSize + "px");
            image.setHeight(iconSize + "px");
            ContextDialog.Item item = new ContextDialog.Item(action, image);
            items.add(item);

            Div itemDiv = new Div(image);
            itemDiv.getStyle().set("margin", "15px");
            itemDiv.getElement().addEventListener("mouseover", e -> {
                wlTooltip.setValue(action.getTooltip());
            });
            itemDiv.addClickListener(e -> {
                selectedAction = item.getAction();
                close();
            });


            itemsLayout.add(itemDiv);
        }

        // The rest of the dialog is used to draw the actions...
        //
//        wScrolledComposite = new ScrolledComposite(shell, SWT.V_SCROLL);
//        wCanvas = new Canvas(wScrolledComposite, SWT.NO_BACKGROUND);
//        wScrolledComposite.setContent(wCanvas);
//        FormData fdCanvas = new FormData();
//        fdCanvas.left = new FormAttachment(0, 0);
//        fdCanvas.right = new FormAttachment(100, 0);
//        fdCanvas.top = new FormAttachment(searchComposite, 0);
//        fdCanvas.bottom = new FormAttachment(wlTooltip, 0);
//        wScrolledComposite.setLayoutData(fdCanvas);

//        itemsFont = wCanvas.getFont();

//        int fontHeight = wCanvas.getFont().getFontData()[0].getHeight() + 1;
//        headerFont =
//                new Font(
//                        getParent().getDisplay(),
//                        props.getDefaultFont().getName(),
//                        fontHeight,
//                        props.getGraphFont().getStyle() | SWT.BOLD | SWT.ITALIC);

        // TODO: Calculate a more dynamic size based on number of actions, screen size
        // and so on
        //
        int width = (int) Math.round(800 * props.getZoomFactor());
        int height = (int) Math.round(600 * props.getZoomFactor());

        // Position the dialog where there was a click to be more intuitive
        //
//        if (location != null) {
//            shell.setSize(width, height);
//            shell.setLocation(location.x, location.y);
//        } else {
//            BaseTransformDialog.setSize(shell, width, height, false);
//        }

        // Add all the listeners
        //
//        shell.addListener(SWT.Resize, event -> updateVerticalBar());
//        shell.addListener(SWT.Deactivate, event -> onFocusLost());
//        shell.addListener(SWT.Close, event -> storeDialogSettings());

/*
        wSearch.addModifyListener(event -> onModifySearch());

        KeyAdapter keyAdapter =
                new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent event) {
                        onKeyPressed(event);
                    }
                };
        wSearch.addKeyListener(keyAdapter);

        wSearch.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {
                        // Pressed enter
                        //
                        if (selectedItem != null) {
                            selectedAction = selectedItem.getAction();
                        }
                        dispose();
                    }
                });

        wCanvas.addPaintListener(event -> onPaint(event));
        wCanvas.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent event) {

                        AreaOwner<ContextDialog.OwnerType, Object> areaOwner =
                                AreaOwner.getVisibleAreaOwner(areaOwners, event.x, event.y);
                        if (areaOwner == null) {
                            return;
                        }
                        switch (areaOwner.getParent()) {
                            case CATEGORY:
                                // Clicked on a category header: expand or unfold
                                //
                                ContextDialog.CategoryAndOrder categoryAndOrder = (ContextDialog.CategoryAndOrder) areaOwner.getOwner();
                                categoryAndOrder.flipCollapsed();
                                wCanvas.redraw();
                                break;
                            case ITEM:
                                // See which item we clicked on...
                                //
                                ContextDialog.Item item = (ContextDialog.Item) areaOwner.getOwner();
                                if (item != null) {
                                    selectedAction = item.getAction();

                                    shiftClicked = (event.stateMask & SWT.SHIFT) != 0;
                                    ctrlClicked =
                                            (event.stateMask & SWT.CONTROL) != 0
                                                    || (Const.isOSX() && (event.stateMask & SWT.COMMAND) != 0);

                                    dispose();
                                }
                            default:
                                break;
                        }
                    }
                });
        if (!EnvironmentUtils.getInstance().isWeb()) {
            wCanvas.addMouseMoveListener(
                    (MouseEvent event) -> {
                        // Do we mouse over an action?
                        //
                        ContextDialog.Item item = findItem(event.x, event.y);
                        if (item != null) {
                            selectItem(item, false);
                        }
                    });
        }
        wCanvas.addKeyListener(keyAdapter);

        // If the shell is re-sized we need to recalculate things...
        //
        shell.addListener(
                SWT.Resize,
                e -> {
                    shell.layout(true, true);
                    totalContentHeight = 0;
                    previousTotalContentHeight = 0;
                    wCanvas.redraw();
                    updateVerticalBar();
                });

        // OS Specific listeners...
        //
        if (OsHelper.isWindows()) {
            wScrolledComposite
                    .getVerticalBar()
                    .addListener(
                            SWT.Selection,
                            e -> {
                                wCanvas.redraw();
                            });
        }

        // Layout all the widgets in the shell.
        //
        shell.layout();

        // Set the active instance.
        //
        activeInstance = this;

        // Manually set canvas size otherwise canvas never gets drawn.
        wCanvas.setSize(10, 10);

        // Show the dialog now
        //
        shell.open();

        // Filter all actions by default
        //
        this.filter(null);

        // Force focus on the search bar
        //
        wSearch.setFocus();

        // Wait until the dialog is closed
        //
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        activeInstance = null;
*/

        open();
        return selectedAction;
    }

    /**
     * Gets the currently active instance
     *
     * @return The currently active instance or null if the dialog is not showing.
     */
    public static ContextDialog getInstance() {
        return activeInstance;
    }

    private void recallToolbarSettings() {
/*
        Button categoriesCheckBox = getCategoriesCheckBox();
        if (categoriesCheckBox != null) {
            String strUseCategories = HopConfig.getGuiProperty(AUDIT_TYPE_TOOLBAR_SHOW_CATEGORIES);
//            categoriesCheckBox.setSelection("Y".equalsIgnoreCase(Const.NVL(strUseCategories, "Y")));
        }

        AuditState auditState =
                AuditManager.retrieveState(
                        LogChannel.UI,
                        HopNamespace.getNamespace(),
                        AUDIT_TYPE_CONTEXT_DIALOG,
                        AUDIT_NAME_CATEGORY_STATES);
        if (auditState != null) {
            Map<String, Object> states = auditState.getStateMap();
            for (ContextDialog.CategoryAndOrder category : categories) {
                Object expanded = states.get(category.getCategory());
                if (expanded == null) {
                    category.setCollapsed(false);
                } else {
                    category.setCollapsed("N".equalsIgnoreCase(expanded.toString()));
                }
            }
        }
*/
    }

    private void storeDialogSettings() {
        // Save the shell size and location in case the position isn't a mouse click
        //
        if (location == null) {
//            props.setScreen(new WindowProperty(shell));
        }

/*
        Button categoriesCheckBox = getCategoriesCheckBox();
        if (categoriesCheckBox != null) {

//            HopConfig.setGuiProperty(
//                    AUDIT_TYPE_TOOLBAR_SHOW_CATEGORIES, categoriesCheckBox.getSelection() ? "Y" : "N");
            try {
                HopConfig.getInstance().saveToFile();
            } catch (Exception e) {
                new ErrorDialog("Error", "Error saving GUI options to hop-config.json", e);
            }
        }
*/

        // Store the category states: expanded or not
        //
        Map<String, Object> states = new HashMap<>();
        for (ContextDialog.CategoryAndOrder category : categories) {
            states.put(category.getCategory(), category.isCollapsed() ? "N" : "Y");
        }
        AuditManager.storeState(
                LogChannel.UI,
                HopNamespace.getNamespace(),
                AUDIT_TYPE_CONTEXT_DIALOG,
                AUDIT_NAME_CATEGORY_STATES,
                states);
    }

//    public boolean isDisposed() {
//        return shell.isDisposed();
//    }

    public void dispose() {

        // Store the toolbar settings
        storeDialogSettings();

        // Close the dialog window
//        shell.close();

        // Clean up the images...
        //
        for (ContextDialog.Item item : items) {
//            item.dispose();
        }
//        highlightColor.dispose();
//        headerFont.dispose();
    }

    @GuiToolbarElement(
            root = GUI_PLUGIN_TOOLBAR_PARENT_ID,
            id = TOOLBAR_ITEM_COLLAPSE_ALL,
            toolTip = "Collapse all categories",
            image = "frontend/images/collapse-all.svg")
    public void collapseAll() {
        for (ContextDialog.CategoryAndOrder category : categories) {
            category.setCollapsed(true);
        }
//        wCanvas.redraw();
    }

    @GuiToolbarElement(
            root = GUI_PLUGIN_TOOLBAR_PARENT_ID,
            id = TOOLBAR_ITEM_EXPAND_ALL,
            toolTip = "Expand all categories",
            image = "frontend/images/expand-all.svg")
    public void expandAll() {
        for (ContextDialog.CategoryAndOrder category : categories) {
            category.setCollapsed(false);
        }
//        wCanvas.redraw();
    }

    @GuiToolbarElement(
            root = GUI_PLUGIN_TOOLBAR_PARENT_ID,
            id = TOOLBAR_ITEM_ENABLE_CATEGORIES,
            label = "Show categories",
            toolTip = "Enable/Disable categories",
            type = GuiToolbarElementType.CHECKBOX)
    public void enableDisableCategories() {
//        wCanvas.redraw();
//        wSearch.setFocus();
    }

    private Checkbox getCategoriesCheckBox() {
        Checkbox checkboxItem = (Checkbox) toolBarWidgets.findToolItem(TOOLBAR_ITEM_ENABLE_CATEGORIES);
        if (checkboxItem == null) {
            return null;
        }
        return checkboxItem;
    }

    @GuiToolbarElement(
            root = GUI_PLUGIN_TOOLBAR_PARENT_ID,
            id = TOOLBAR_ITEM_CLEAR_SEARCH,
            toolTip = "Clear search filter",
            image = "frontend/images/clear-text.svg",
            separator = true)
    public void clearSearchFilter() {
        /*wSearch.setText("");*/
    }

    /**
     * This is where all the actions are drawn
     *
//     * @param event
     */
    private void onPaint(/*PaintEvent event*/) {
/*
        // Do double buffering to prevent flickering on Windows
        //
        boolean needsDoubleBuffering =
                Const.isWindows() && "GUI".equalsIgnoreCase(Const.getHopPlatformRuntime());

        Image image = null;
        GC gc = event.gc;

        if (needsDoubleBuffering) {
            image = new Image(shell.getDisplay(), event.width, event.height);
            gc = new GC(image);
        }
*/
        boolean useCategories;

        Checkbox categoriesCheckBox = getCategoriesCheckBox();
        if (categoriesCheckBox == null) {
            useCategories = true;
        } else {
            useCategories = categoriesCheckBox.getValue();
        }
        useCategories &= !categories.isEmpty();
        updateToolbar();
/*
        // Fill everything with white...
        //
        gc.setForeground(GuiResource.getInstance().getColorBlack());
        gc.setBackground(GuiResource.getInstance().getColorBackground());
        gc.fillRectangle(0, 0, event.width, event.height);

        // For text and lines...
        //
        gc.setForeground(GuiResource.getInstance().getColorBlack());
        gc.setLineWidth(1);

        // Remember the area owners
        //
        areaOwners = new ArrayList<>();

        // The size of the canvas right now?
        //
        org.eclipse.swt.graphics.Rectangle scrolledCompositeBounds = wScrolledComposite.getBounds();

        // Did we draw before?
        // If so we might have a maximum height and a scrollbar selection
        //
        if (totalContentHeight > 0) {
            ScrollBar verticalBar = wScrolledComposite.getVerticalBar();

            if (totalContentHeight > scrolledCompositeBounds.height) {
                heightOffSet =
                        (int)
                                Math.floor(
                                        (float) totalContentHeight
                                                * verticalBar.getSelection()
                                                / (100 - verticalBar.getThumb()));
            } else {
                heightOffSet = 0;
            }

            // System.out.println("Bar="+verticalBar.getSelection()+"%  thumb="+verticalBar.getThumb()+"%
            // offset="+heightOffSet+"  total="+totalContentHeight);
        }

        // Draw all actions
        // Loop over the categories, if any...
        //
        int height = 0; // should always be about the same
        int categoryNr = 0;
        int x = margin;
        int y = margin - heightOffSet;

        firstShownItem = null;

        while ((useCategories && categoryNr < categories.size())
                || (!useCategories || categories.isEmpty()) && (categoryNr == 0)) {

            ContextDialog.CategoryAndOrder categoryAndOrder;
            if (!useCategories || categories.isEmpty()) {
                categoryAndOrder = null;
            } else {
                categoryAndOrder = categories.get(categoryNr);
            }

            // Get the list of actions for the given categoryAndOrder
            //
            List<ContextDialog.Item> itemsToPaint = findItemsForCategory(categoryAndOrder);

            if (!itemsToPaint.isEmpty()) {
                if (categoryAndOrder != null) {
                    // Draw the category header
                    //
                    gc.setFont(headerFont);
                    if (categoryAndOrder.isCollapsed()) {
                        gc.setForeground(GuiResource.getInstance().getColorDarkGray());
                    } else {
                        gc.setForeground(GuiResource.getInstance().getColorBlack());
                    }
                    org.eclipse.swt.graphics.Point categoryExtent = gc.textExtent(categoryAndOrder.category);
                    // gc.drawLine( margin, y-1, scrolledCompositeBounds.width - xMargin, y-1 );
                    gc.drawText(categoryAndOrder.category, x, y);
                    areaOwners.add(
                            new AreaOwner<>(
                                    AreaOwner.AreaType.CUSTOM,
                                    x,
                                    y + heightOffSet,
                                    categoryExtent.x,
                                    categoryExtent.y,
                                    new Point(0, heightOffSet),
                                    ContextDialog.OwnerType.CATEGORY,
                                    categoryAndOrder));
                    y += categoryExtent.y + yMargin;
                    gc.setLineWidth(1);
                    gc.drawLine(margin, y - yMargin, scrolledCompositeBounds.width - xMargin, y - yMargin);
                }

                gc.setForeground(GuiResource.getInstance().getColorBlack());
                gc.setFont(itemsFont);

                if (categoryAndOrder == null || !categoryAndOrder.isCollapsed()) {

                    // Paint the action items
                    //
                    for (ContextDialog.Item item : itemsToPaint) {

                        lastShownItem = item;
                        if (firstShownItem == null) {
                            firstShownItem = item;
                        }

                        String name = Const.NVL(item.action.getName(), item.action.getId());

                        org.eclipse.swt.graphics.Rectangle imageBounds = item.image.getBounds();
                        org.eclipse.swt.graphics.Point nameExtent = gc.textExtent(name);

                        int width = Math.max(nameExtent.x, imageBounds.width);
                        height = nameExtent.y + margin + imageBounds.height;

                        if (x + width + xMargin > scrolledCompositeBounds.width) {
                            x = margin;
                            y += height + yMargin;
                        }

                        if (item.isSelected()) {
                            gc.setLineWidth(2);
                            gc.setBackground(highlightColor);
                            gc.fillRoundRectangle(
                                    x - xMargin / 2,
                                    y - yMargin / 2,
                                    width + xMargin,
                                    height + yMargin,
                                    margin,
                                    margin);
                        }

                        // So we draw the icon in the centre of the name text...
                        //
                        gc.drawImage(item.image, x + nameExtent.x / 2 - imageBounds.width / 2, y);

                        // Then we draw the text underneath
                        //
                        gc.drawText(name, x, y + imageBounds.height + margin);

                        // Reset the background color
                        //
                        gc.setLineWidth(1);
                        gc.setBackground(GuiResource.getInstance().getColorBackground());

                        // The drawn area is the complete rectangle
                        //
                        AreaOwner<ContextDialog.OwnerType, Object> areaOwner =
                                new AreaOwner(
                                        AreaOwner.AreaType.CUSTOM,
                                        x,
                                        y + heightOffSet,
                                        width,
                                        height,
                                        new Point(0, heightOffSet),
                                        ContextDialog.OwnerType.ITEM,
                                        item);
                        areaOwners.add(areaOwner);
                        item.setAreaOwner(areaOwner);

                        // Now we advance x and y to where we want to draw the next one...
                        //
                        x += width + xMargin;
                        if (x > scrolledCompositeBounds.width) {
                            x = margin;
                            y += height + yMargin;
                        }
                    }

                    // Back to the left on a next line to draw the next category (if any)
                    //
                    x = margin;
                    y += height + yMargin;
                } else {
                    y -= yMargin; // tighter together when collapsed
                }
            }

            // Pick the next category
            //
            categoryNr++;
            if (!itemsToPaint.isEmpty()) {
                y += yMargin;
            }
        }

        totalContentHeight = y + heightOffSet;

        if (previousTotalContentHeight != totalContentHeight) {
            previousTotalContentHeight = totalContentHeight;
            wCanvas.setSize(wScrolledComposite.getClientArea().width, totalContentHeight);
            updateVerticalBar();
        }

        if (needsDoubleBuffering) {
            // Draw the image onto the canvas and get rid of the resources
            //
            event.gc.drawImage(image, 0, 0);
            gc.dispose();
            image.dispose();
        }
*/
    }

    private void updateToolbar() {
        Checkbox categoriesCheckBox = getCategoriesCheckBox();
        boolean categoriesEnabled = categoriesCheckBox != null && categoriesCheckBox.getValue();
        toolBarWidgets.enableToolbarItem(TOOLBAR_ITEM_COLLAPSE_ALL, categoriesEnabled);
        toolBarWidgets.enableToolbarItem(TOOLBAR_ITEM_EXPAND_ALL, categoriesEnabled);
    }

    private List<ContextDialog.Item> findItemsForCategory(ContextDialog.CategoryAndOrder categoryAndOrder) {
        List<ContextDialog.Item> list = new ArrayList<>();
        for (ContextDialog.Item filteredItem : filteredItems) {
            if (categoryAndOrder == null
                    || categoryAndOrder.category.equalsIgnoreCase(filteredItem.action.getCategory())) {
                list.add(filteredItem);
            } else if (CATEGORY_OTHER.equals(categoryAndOrder.category)
                    && StringUtils.isEmpty(filteredItem.action.getCategory())) {
                list.add(filteredItem);
            }
        }
        return list;
    }

    private void selectItem(ContextDialog.Item selectedItem, boolean scroll) {

        for (ContextDialog.Item item : items) {
            item.setSelected(false);
        }

        if (selectedItem == null) {
            wlTooltip.setValue("");
        } else {
            this.selectedItem = selectedItem;
            wlTooltip.setValue(Const.NVL(selectedItem.getAction().getTooltip(), ""));
            selectedItem.setSelected(true);

            // See if we need to show the selected item.
            //
            if (scroll && totalContentHeight > 0) {
/*
                org.eclipse.swt.graphics.Rectangle scrolledCompositeBounds = wScrolledComposite.getBounds();
                Rectangle area = selectedItem.getAreaOwner().getArea();
                ScrollBar verticalBar = wScrolledComposite.getVerticalBar();
                if (area.y + area.height + 2 * yMargin > scrolledCompositeBounds.height) {
                    verticalBar.setSelection(
                            Math.min(
                                    verticalBar.getSelection() + verticalBar.getPageIncrement(),
                                    100 - verticalBar.getThumb()));
                } else if (area.y < 0) {
                    verticalBar.setSelection(
                            Math.max(verticalBar.getSelection() - verticalBar.getPageIncrement(), 0));
                }
*/
            }
        }

//        wCanvas.redraw();
    }

    /**
     * Gets the search text widget
     *
     * @return the search text widget
     */
//    public Text getSearchTextWidget() {
//        return wSearch;
//    }

    public void filter(String text) {

        if (text == null) {
            text = "";
        }

        String[] filters = text.split(",");
        for (int i = 0; i < filters.length; i++) {
            filters[i] = Const.trim(filters[i]);
        }

        filteredItems.clear();
        for (ContextDialog.Item item : items) {
            GuiAction action = item.getAction();

            if (StringUtils.isEmpty(text) || action.containsFilterStrings(filters)) {
                filteredItems.add(item);
            }
        }

        if (filteredItems.isEmpty()) {
            selectItem(null, false);
        }

        // if selected item is exclude, change to a new default selection: first in the list
        //
        else if (!filteredItems.contains(selectedItem)) {
            selectItem(filteredItems.get(0), false);
        }
        // Update vertical bar
        //
        this.updateVerticalBar();

//        wCanvas.redraw();
    }

    private void onFocusLost() {
        focusLost = true;

        dispose();
    }

    private void onModifySearch() {
//        String text = wSearch.getText();
//        this.filter(text);
    }

/*
    private synchronized void onKeyPressed(KeyEvent event) {

        if (filteredItems.isEmpty()) {
            return;
        }
        if (shell.isDisposed() || !shell.isVisible()) {
            return;
        }

        // Which item area are we currently using as a base...
        //
        org.apache.hop.core.gui.Rectangle area = null;
        ScrollBar verticalBar = wScrolledComposite.getVerticalBar();

        if (selectedItem == null) {
            // Select the first shown item
            if (firstShownItem != null) {
                area = firstShownItem.getAreaOwner().getArea();
            }
        } else {
            if (selectedItem.getAreaOwner() != null) {
                area = selectedItem.getAreaOwner().getArea();
            }
        }

        switch (event.keyCode) {
            case SWT.ARROW_DOWN:
                // Find the next item down...
                //
                selectItemDown(area);
                break;
            case SWT.ARROW_UP:
                selectItemUp(area);
                break;
            case SWT.PAGE_UP:
                break;
            case SWT.PAGE_DOWN:
                break;
            case SWT.ARROW_LEFT:
                selectItemLeft(area);
                break;
            case SWT.ARROW_RIGHT:
                selectItemRight(area);
                break;
            case SWT.HOME:
                verticalBar.setSelection(0);
                selectItem(firstShownItem, true);
                break;
            case SWT.END:
                Rectangle lastArea = lastShownItem.getAreaOwner().getArea();
                int bottomY = lastArea.y + lastArea.height + yMargin;
                int percentage =
                        (int) ((100 - verticalBar.getThumb()) * ((double) bottomY / totalContentHeight));
                verticalBar.setSelection(percentage - verticalBar.getThumb() / 2);
                selectItem(lastShownItem, true);
                break;
        }
    }
*/

/*
    private void selectClosest(Rectangle area, List<AreaOwner> areas) {
        // Sort by distance...
        //
        areas.sort((o1, o2) -> (int) (o1.getArea().distance(area) - o2.getArea().distance(area)));

        if (!areas.isEmpty()) {
            ContextDialog.Item item = (ContextDialog.Item) areas.get(0).getOwner();
            selectItem(item, true);
        }
    }
*/

    /**
     * Find an area owner directly to the right of the area
     *
     * @param area
     */
    private void selectItemRight(Rectangle area) {
        List<AreaOwner> rightAreas = new ArrayList<>();
        for (AreaOwner areaOwner : areaOwners) {
            if (areaOwner.getOwner() instanceof ContextDialog.Item) {
                // Only keep the items to the left
                //
                Rectangle r = areaOwner.getArea();
                if (r.x > area.x + area.width) {
                    if (r.y - 2 * yMargin < area.y && r.y + 2 * yMargin > area.y) {
                        rightAreas.add(areaOwner);
                    }
                }
            }
        }
//        selectClosest(area, rightAreas);
    }

    /**
     * Find an area owner directly to the left of the area
     *
     * @param area
     */
    private void selectItemLeft(Rectangle area) {
        List<AreaOwner> leftAreas = new ArrayList<>();
        for (AreaOwner areaOwner : areaOwners) {
            if (areaOwner.getOwner() instanceof ContextDialog.Item) {
                // Only keep the items to the left
                //
                Rectangle r = areaOwner.getArea();
                if (r.x < area.x) {

                    // Select only in the same band of items
                    //
                    if (r.y - 2 * yMargin < area.y && r.y + 2 * yMargin > area.y) {
                        leftAreas.add(areaOwner);
                    }
                }
            }
        }
//        selectClosest(area, leftAreas);
    }

    /**
     * Find an area owner directly to the top of the area
     *
     * @param area
     */
    private void selectItemUp(Rectangle area) {
        List<AreaOwner> topAreas = new ArrayList<>();
        for (AreaOwner areaOwner : areaOwners) {
            if (areaOwner.getOwner() instanceof ContextDialog.Item) {
                // Only keep the items to the left
                //
                if (areaOwner.getArea().y < area.y) {
                    topAreas.add(areaOwner);
                }
            }
        }
//        selectClosest(area, topAreas);
    }

    private void selectItemDown(Rectangle area) {
        List<AreaOwner> bottomAreas = new ArrayList<>();
        for (AreaOwner areaOwner : areaOwners) {
            if (areaOwner.getOwner() instanceof ContextDialog.Item) {
                // Only keep the items to the left
                //
                Rectangle r = areaOwner.getArea();
                if (r.y > area.y + area.height) {
                    bottomAreas.add(areaOwner);
                }
            }
        }
//        selectClosest(area, bottomAreas);
    }

    private void updateVerticalBar() {
/*
        ScrollBar verticalBar = wScrolledComposite.getVerticalBar();
        org.eclipse.swt.graphics.Rectangle scrolledCompositeBounds = wScrolledComposite.getBounds();

        if (totalContentHeight < scrolledCompositeBounds.height) {
            verticalBar.setEnabled(false);
            verticalBar.setVisible(false);
        } else {
            verticalBar.setEnabled(true);
            verticalBar.setVisible(true);

            verticalBar.setMinimum(0);
            verticalBar.setMaximum(100);

            // How much can we show in percentage?
            // That's the size of the thumb
            //
            int percentage = (int) ((double) 100 * scrolledCompositeBounds.height / totalContentHeight);
            verticalBar.setThumb(percentage);
            if (!EnvironmentUtils.getInstance().isWeb()) {
                verticalBar.setPageIncrement(percentage / 2);
                verticalBar.setIncrement(percentage / 10);
            }

            // Set the selection as well...
            //
            int selection =
                    Math.max(
                            0,
                            (int)
                                    ((double) 100
                                            * (heightOffSet - scrolledCompositeBounds.height)
                                            / totalContentHeight));
            verticalBar.setSelection(selection);
        }
*/
    }

    private ContextDialog.Item findItem(int x, int y) {

        for (AreaOwner areaOwner : areaOwners) {
            if (areaOwner.contains(x, y)) {
                if (areaOwner.getOwner() instanceof ContextDialog.Item) {
                    return (ContextDialog.Item) areaOwner.getOwner();
                }
            }
        }

        return null;
    }

    /**
     * Gets shiftClicked
     *
     * @return value of shiftClicked
     */
    public boolean isShiftClicked() {
        return shiftClicked;
    }

    /** @param shiftClicked The shiftClicked to set */
    public void setShiftClicked(boolean shiftClicked) {
        this.shiftClicked = shiftClicked;
    }

    /**
     * Gets ctrlClicked
     *
     * @return value of ctrlClicked
     */
    public boolean isCtrlClicked() {
        return ctrlClicked;
    }

    /** @param ctrlClicked The ctrlClicked to set */
    public void setCtrlClicked(boolean ctrlClicked) {
        this.ctrlClicked = ctrlClicked;
    }

    /**
     * Gets focusLost
     *
     * @return value of focusLost
     */
    public boolean isFocusLost() {
        return focusLost;
    }

    /** @param focusLost The focusLost to set */
    public void setFocusLost(boolean focusLost) {
        this.focusLost = focusLost;
    }
}
