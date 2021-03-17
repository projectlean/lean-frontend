package org.lean.ui.core.dialog;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.hop.core.Const;
import org.apache.hop.core.IDescription;
import org.apache.hop.core.Props;
import org.apache.hop.i18n.BaseMessages;
import org.lean.ui.core.PropsUi;

public class EnterTextDialog extends Dialog {

    private static final Class<?> PKG = EnterTextDialog.class; // For Translator

    private String title, message;

    private Label wlDesc;
    private Text wDesc;
    private Button wOk, wCancel;
    private PropsUi props;
    private String text;
    private boolean fixed;
    private boolean readonly, modal, singleLine;
    private String origText;

    /**
     * Dialog to allow someone to show or enter a text
     *
     * @param parent The parent shell to use
     * @param title The dialog title
     * @param message The message to display
     * @param text The text to display or edit
     * @param fixed true if you want the font to be in fixed-width
     */
    public EnterTextDialog(String title, String message, String text, boolean fixed) {
        this(title, message, text);
        this.fixed = fixed;
    }

    /**
     * Dialog to allow someone to show or enter a text in variable width font
     *
     * @param parent The parent shell to use
     * @param title The dialog title
     * @param message The message to display
     * @param text The text to display or edit
     */
    public EnterTextDialog(String title, String message, String text) {
        super();
        props = PropsUi.getInstance();
        this.title = title;
        this.message = message;
        this.text = text;
        fixed = false;
        readonly = false;
        singleLine = false;

        wlDesc = new Label(message);
        wDesc = new Text(text);

        add(new HorizontalLayout(wlDesc, wDesc));
    }

    public void setReadOnly() {
        readonly = true;
    }

    public void setModal() {
        modal = true;
    }

    public void setSingleLine() {
        singleLine = true;
    }

    public void getData() {
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
}
