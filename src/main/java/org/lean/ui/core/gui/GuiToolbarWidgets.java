package org.lean.ui.core.gui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.Const;
import org.lean.core.gui.plugin.GuiRegistry;
import org.lean.core.gui.plugin.toolbar.GuiToolbarElementType;
import org.lean.core.gui.plugin.toolbar.GuiToolbarItem;
import org.lean.ui.core.ConstUi;
import org.lean.ui.core.PropsUi;
import org.lean.ui.core.gui.vaadin.components.toolbar.LeanToolbar;

import java.util.*;
import java.util.List;

/** This class contains the widgets for the GUI elements of a GUI Plugin */
@VaadinSessionScope
public class GuiToolbarWidgets extends BaseGuiWidgets {

    private Map<String, GuiToolbarItem> guiToolBarMap;
    private Map<String, Component> widgetsMap;
    private Map<String, Component> toolItemMap;

    public GuiToolbarWidgets(String leanGuiLayoutId) {
        super(UUID.randomUUID().toString(), leanGuiLayoutId);
        guiToolBarMap = new HashMap<>();
        widgetsMap = new HashMap<>();
        toolItemMap = new HashMap<>();
    }

    public void createToolbarWidgets(LeanToolbar toolBar, String root) {

        // Find the GUI Elements for the given toolbar root...
        //
        List<GuiToolbarItem> toolbarItems = GuiRegistry.getInstance().findGuiToolbarItems(root);
        if (toolbarItems.isEmpty()) {
            System.err.println("Create widgets: no GUI toolbar items found for root: " + root);
            return;
        }

        Collections.sort(toolbarItems);

        // Loop over the toolbar items, create and remember the widgets...
        //
        for (GuiToolbarItem toolbarItem : toolbarItems) {
            addToolbarWidgets(toolBar, toolbarItem);
        }

        // Force re-layout
        //
        //parent.layout(true, true);

        // Clean up when the parent is disposed
        //
//        addDeRegisterGuiPluginObjectListener();
    }

    private void addToolbarWidgets(LeanToolbar toolBar, GuiToolbarItem toolbarItem) {

        if (toolbarItem.isIgnored()) {
            return;
        }

        toolBar.getContent().setId("lean-gui-toolbar-widgets");

        // We might need it later...
        //
        guiToolBarMap.put(toolbarItem.getId(), toolbarItem);

/*
        if (!(parent instanceof LeanToolbar)) {
            throw new RuntimeException(
                    "We can only add toolbar items to a toolbar, not class " + parent.getClass().getName());
        }
*/
//        LeanToolbar toolBar = (LeanToolbar) parent;

        PropsUi props = PropsUi.getInstance();

        // We want to add a separator if the annotation asked for it
        // We also want to add a separator in case the toolbar element type isn't a button
        //
        if (toolbarItem.isAddingSeparator() || toolbarItem.getType() != GuiToolbarElementType.BUTTON) {
            toolBar.getContent().add(new Hr());
        }

        // Add a label in front of the item
        //
        if (toolbarItem.getType() != GuiToolbarElementType.LABEL
                && toolbarItem.getType() != GuiToolbarElementType.CHECKBOX
                && StringUtils.isNotEmpty(toolbarItem.getLabel())) {

            Hr labelSeparator = new Hr();
            Label label = new Label(toolbarItem.getLabel());
            label.setTitle(Const.NVL(toolbarItem.getToolTip(), ""));
            // TODO: background, width
            toolBar.add(labelSeparator, label);

        }

        // Add the GUI element
        //
        switch (toolbarItem.getType()) {
            case LABEL:
                Hr labelSeparator = new Hr();
                Label label = new Label();
                label.setTitle(toolbarItem.getToolTip());
                label.setText(Const.NVL(toolbarItem.getLabel(), ""));
                toolItemMap.put(toolbarItem.getId(), labelSeparator);
                widgetsMap.put(toolbarItem.getId(), label);
                toolBar.add(labelSeparator, label);
                break;

            case BUTTON:
                Button button = new Button();
                if(StringUtils.isNotEmpty(toolbarItem.getImage())){
                    Image buttonIcon = new Image(toolbarItem.getImage(), "");
                    buttonIcon.setHeight(ConstUi.SMALL_ICON_SIZE_PX);
                    buttonIcon.setWidth(ConstUi.SMALL_ICON_SIZE_PX);
                    button.setIcon(buttonIcon);
                }else{
                    button.setText(toolbarItem.getLabel());
                }

                button.getElement().setProperty("title", toolbarItem.getToolTip());
                ComponentEventListener listener = getListener(toolbarItem.getClassLoader(), toolbarItem.getListenerClass(), toolbarItem.getListenerMethod());
                button.addClickListener(listener);
                toolItemMap.put(toolbarItem.getId(), button);
                toolBar.add(button);
                break;

            case COMBO:
                Hr comboSeparator = new Hr();
                ComboBox<String[]> combo = new ComboBox();
                // TODO: check if there is a possibility/need to set tooltips on combo boxes in Vaadin
                combo.setItems(getComboItems(toolbarItem));
                // TODO: implement combobox width
//                comboSeparator.setWidth(
//                        calculateComboWidth(combo)
//                                + toolbarItem.getExtraWidth()); // extra room for widget decorations
//                comboSeparator.setControl(combo);
//                listener =
//                        getListener(
//                                toolbarItem.getClassLoader(),
//                                toolbarItem.getListenerClass(),
//                                toolbarItem.getListenerMethod());
//                combo.addListener(SWT.Selection, listener);
                toolItemMap.put(toolbarItem.getId(), comboSeparator);
                widgetsMap.put(toolbarItem.getId(), combo);
                toolBar.add(comboSeparator, combo);
                break;

            case CHECKBOX:
                Hr checkboxSeparator = new Hr();
                Checkbox checkbox = new Checkbox();
                checkbox.getElement().setProperty("title", toolbarItem.getToolTip());
/*
                ToolItem checkboxSeparator = new ToolItem(toolBar, SWT.SEPARATOR);
                Button checkbox =
                        new Button(parent, SWT.CHECK | (toolbarItem.isAlignRight() ? SWT.RIGHT : SWT.LEFT));
                checkbox.setToolTipText(Const.NVL(toolbarItem.getToolTip(), ""));
                checkbox.setText(Const.NVL(toolbarItem.getLabel(), ""));
                checkbox.setBackground(toolBar.getBackground());
                checkbox.pack();
                checkboxSeparator.setWidth(
                        checkbox.getSize().x
                                + toolbarItem.getExtraWidth()); // extra room for widget decorations
                checkboxSeparator.setControl(checkbox);
                listener =
                        getListener(
                                toolbarItem.getClassLoader(),
                                toolbarItem.getListenerClass(),
                                toolbarItem.getListenerMethod());
                checkbox.addListener(SWT.Selection, listener);
*/
                toolItemMap.put(toolbarItem.getId(), checkboxSeparator);
                widgetsMap.put(toolbarItem.getId(), checkbox);
                toolBar.add(checkboxSeparator, checkbox);
                break;

            default:
                break;
        }
    }

/*
    private int calculateComboWidth(ComboBox<String[]> combo) {
        String maxWidth = combo.getMaxWidth();
        for (String item : combo.getItems()) {
            int width = TextSizeUtilFacade.textExtent(item).x;
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }
*/

/*
    public void enableToolbarItem(String id, boolean enabled) {
        ToolItem toolItem = toolItemMap.get(id);
        if (toolItem == null || toolItem.isDisposed()) {
            return;
        }
        if (enabled != toolItem.isEnabled()) {
            toolItem.setEnabled(enabled);
        }
    }
*/

    /**
     * Find the toolbar item with the given ID. Check the capability in the given file type Enable or
     * disable accordingly.
     *
     * @param fileType
     * @param id The ID of the widget to look for
     * @param permission
     * @return The toolbar item or null if nothing is found
     */
/*
    public ToolItem enableToolbarItem(IHopFileType fileType, String id, String permission) {
        return enableToolbarItem(fileType, id, permission, true);
    }
*/

    /**
     * Find the toolbar item with the given ID. Check the capability in the given file type Enable or
     * disable accordingly.
     *
     * @param fileType
     * @param id The ID of the widget to look for
     * @param permission
     * @param active The state if the permission is available
     * @return The toolbar item or null if nothing is found
     */
/*
    public ToolItem enableToolbarItem(
            IHopFileType fileType, String id, String permission, boolean active) {
        ToolItem item = findToolItem(id);
        if (item == null || item.isDisposed()) {
            return null;
        }
        boolean hasCapability = fileType.hasCapability(permission);
        item.setEnabled(hasCapability && active);
        return item;
    }
*/

/*
    public ToolItem findToolItem(String id) {
        return toolItemMap.get(id);
    }

    public void refreshComboItemList(String id) {
        GuiToolbarItem item = guiToolBarMap.get(id);
        if (item != null) {
            Control control = widgetsMap.get(id);
            if (control != null) {
                if (control instanceof Combo) {
                    Combo combo = (Combo) control;
                    combo.setItems(getComboItems(item));
                } else {
                    System.err.println("toolbar item with id '" + id + "' : widget not of instance Combo");
                }
            } else {
                System.err.println(
                        "toolbar item with id '" + id + "' : control not found when refreshing combo");
            }
        } else {
            System.err.println("toolbar item with id '" + id + "' : not found when refreshing combo");
        }
    }

    public void selectComboItem(String id, String string) {
        GuiToolbarItem item = guiToolBarMap.get(id);
        if (item != null) {
            Control control = widgetsMap.get(id);
            if (control != null) {
                if (control instanceof Combo) {
                    Combo combo = (Combo) control;
                    combo.setText(Const.NVL(string, ""));
                    int index = Const.indexOfString(string, combo.getItems());
                    if (index >= 0) {
                        combo.select(index);
                    }
                }
            }
        }
    }
*/

    /**
     * Gets widgetsMap
     *
     * @return value of widgetsMap
     */
/*
    public Map<String, Control> getWidgetsMap() {
        return widgetsMap;
    }
*/

    /** @param widgetsMap The widgetsMap to set */
/*
    public void setWidgetsMap(Map<String, Control> widgetsMap) {
        this.widgetsMap = widgetsMap;
    }
*/

    /**
     * Gets toolItemMap
     *
     * @return value of toolItemMap
     */
/*
    public Map<String, ToolItem> getToolItemMap() {
        return toolItemMap;
    }
*/

    /** @param toolItemMap The toolItemMap to set */
/*
    public void setToolItemMap(Map<String, ToolItem> toolItemMap) {
        this.toolItemMap = toolItemMap;
    }
*/

    /**
     * Gets guiToolBarMap
     *
     * @return value of guiToolBarMap
     */
    public Map<String, GuiToolbarItem> getGuiToolBarMap() {
        return guiToolBarMap;
    }

    /** @param guiToolBarMap The guiToolBarMap to set */
    public void setGuiToolBarMap(Map<String, GuiToolbarItem> guiToolBarMap) {
        this.guiToolBarMap = guiToolBarMap;
    }
}
