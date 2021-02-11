package org.lean.ui.core.dialog;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import org.apache.hop.core.Const;
import org.apache.hop.i18n.BaseMessages;
import org.lean.ui.core.ConstUi;
import org.lean.ui.core.PropsUi;

import java.util.*;
import java.util.List;

public class ShowMessageDialog extends Dialog {

    private static final Class<?> PKG = org.apache.hop.ui.core.dialog.ShowMessageDialog.class; // For Translator

    private static final Map<Integer, String> buttonTextByFlagDefaults = new LinkedHashMap<>();

    private static int OK = 0;
    private static int CANCEL = 1;
    private static int YES = 2;
    private static int NO = 3;

    static {
        buttonTextByFlagDefaults.put(OK, BaseMessages.getString(PKG, "System.Button.OK"));
        buttonTextByFlagDefaults.put(CANCEL, BaseMessages.getString(PKG, "System.Button.Cancel"));
        buttonTextByFlagDefaults.put(YES, BaseMessages.getString(PKG, "System.Button.Yes"));
        buttonTextByFlagDefaults.put(NO, BaseMessages.getString(PKG, "System.Button.No"));
    }

    private String title, message;

    private PropsUi props;

    private int flags;
    private Map<Integer, String> buttonTextByFlag = null;

    private int returnValue;
    private int type;

    private boolean scroll;
    private boolean hasIcon;

    /** Timeout of dialog in seconds */
    private int timeOut;

    private List<Button> buttons;

    private Label wIcon, titleLabel;

    private TextArea wlDesc;

    /**
     * Dialog to allow someone to show a text with an icon in front
     *
     * @param flags the icon to show using SWT flags: SWT.ICON_WARNING, SWT.ICON_ERROR, ... Also
     *     SWT.OK, SWT.CANCEL is allowed.
     * @param title The dialog title
     * @param message The message to display
     */
    public ShowMessageDialog(int flags, String title, String message) {
        this(flags, title, message, false);
    }

    /**
     * Dialog to allow someone to show a text with an icon in front
     *
     * @param flags the icon to show using SWT flags: SWT.ICON_WARNING, SWT.ICON_ERROR, ... Also
     *     SWT.OK, SWT.CANCEL is allowed.
     * @param title The dialog title
     * @param message The message to display
     * @param scroll Set the dialog to a default size and enable scrolling
     */
    public ShowMessageDialog(int flags, String title, String message, boolean scroll) {
        this(flags, buttonTextByFlagDefaults, title, message, scroll);

    }

    /**
     * Dialog to allow someone to show a text with an icon in front
     *
     * @param flags the icon to show using SWT flags: SWT.ICON_WARNING, SWT.ICON_ERROR, ... Also
     *     SWT.OK, SWT.CANCEL is allowed.
     * @param buttonTextByFlag Custom text to display for each button by flag i.e. key: SWT.OK, value:
     *     "Custom OK" Note - controls button order, use an ordered map to maintain button order.
     * @param title The dialog title
     * @param message The message to display
     * @param scroll Set the dialog to a default size and enable scrolling
     */
    public ShowMessageDialog(
            int flags,
            Map<Integer, String> buttonTextByFlag,
            String title,
            String message,
            boolean scroll) {
        super();
        this.buttonTextByFlag = buttonTextByFlag;
        this.flags = flags;
        this.title = title;
        this.message = message;
        this.scroll = scroll;

        props = PropsUi.getInstance();

        this.setId("show-message-dialog");
        this.setWidth("50%");
        this.setHeight("50%");

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setHeight(ConstUi.HBAR_HEIGHT);
        headerLayout.setWidthFull();
        Image leanLogo = new Image(ConstUi.LEAN_LOGO_PATH, "");
        leanLogo.setWidth(ConstUi.SMALL_ICON_SIZE_PX);
        leanLogo.setHeight(ConstUi.SMALL_ICON_SIZE_PX);

        Label titleLabel = new Label(title);

        headerLayout.add(leanLogo, titleLabel);

        HorizontalLayout bodyLayout = new HorizontalLayout();

        hasIcon =
                (flags & VaadinIcon.WARNING.ordinal()) != 0
                        || (flags & VaadinIcon.INFO.ordinal()) != 0
                        || (flags & VaadinIcon.QUESTION.ordinal()) != 0
                        || (flags & VaadinIcon.BOMB.ordinal()) != 0
                        || (flags & VaadinIcon.CHECK.ordinal()) != 0;

        Icon icon = null;
        if ((flags & VaadinIcon.WARNING.ordinal()) != 0) {
            icon = VaadinIcon.WARNING.create();
        }
        if ((flags & VaadinIcon.INFO.ordinal()) != 0) {
            icon = VaadinIcon.INFO.create();
        }
        if ((flags & VaadinIcon.QUESTION.ordinal()) != 0) {
            icon = VaadinIcon.QUESTION.create();
        }
        if ((flags & VaadinIcon.BOMB.ordinal()) != 0) {
            icon = VaadinIcon.BOMB.create();
        }
        if ((flags & VaadinIcon.CHECK.ordinal()) != 0) {
            icon = VaadinIcon.CHECK.create();
        }

        hasIcon = hasIcon && icon != null;
        wIcon = null;

        if (hasIcon) {
            wIcon = new Label("");
            wIcon.add(icon);
        }
        wIcon.setHeight(ConstUi.SMALL_ICON_SIZE_PX);
        wIcon.setWidth(ConstUi.SMALL_ICON_SIZE_PX);
        wIcon.getStyle().set("margin-right", ConstUi.SMALL_ICON_SIZE_PX);

        wlDesc = new TextArea("Connection result");
        wlDesc.setValue(message);
        wlDesc.setWidthFull();
        wlDesc.setReadOnly(true);

        bodyLayout.add(wIcon, wlDesc);

        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setHeight(ConstUi.HBAR_HEIGHT);
        footerLayout.setWidthFull();

        buttons = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : buttonTextByFlag.entrySet()) {
            Integer buttonFlag = entry.getKey();
            if ((flags & buttonFlag) != 0) {
                Button button = new Button();
                button.setText(entry.getValue());
                button.addClickListener(e -> quit(buttonFlag));
                buttons.add(button);
            }
        }

        for(Button button : buttons){
            footerLayout.add(button);
        }

        final Button button = buttons.get(0);
        final String ok = button.getText();
        long startTime = new Date().getTime();

        add(headerLayout, bodyLayout, footerLayout);

    }

    private void cancel(int returnValue) {
        this.returnValue = returnValue;
        this.close();
    }

    private void quit(int returnValue) {
        this.returnValue = returnValue;
        this.close();
    }

    /** @return the timeOut */
    public int getTimeOut() {
        return timeOut;
    }

    /** @param timeOut the timeOut to set */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public void setType(int type) {
        this.type = type;
    }

}
