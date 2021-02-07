package org.lean.ui.core.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import org.apache.hop.core.Const;
import org.apache.hop.core.IDescription;
import org.apache.hop.core.Props;
import org.apache.hop.i18n.BaseMessages;
import org.lean.ui.core.PropsUi;

public class EnterTextDialog extends Dialog {

    private static final Class<?> PKG = org.apache.hop.ui.core.dialog.EnterTextDialog.class; // For Translator

    private String title, message;

    private Label wlDesc;
//    private Text wDesc;
//    private FormData fdlDesc, fdDesc;
    private Button wOk, wCancel;
//    private Listener lsOk, lsCancel;
//    private Shell parent, shell;
//    private SelectionAdapter lsDef;
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
//        this.parent = parent;
        props = PropsUi.getInstance();
        this.title = title;
        this.message = message;
        this.text = text;
        fixed = false;
        readonly = false;
        singleLine = false;
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

//    public String open() {
//
//        int margin = props.getMargin();

        // From transform line
/*
        wlDesc = new Label(shell, SWT.NONE);
        wlDesc.setText(message);
        props.setLook(wlDesc);
        fdlDesc = new FormData();
        fdlDesc.left = new FormAttachment(0, 0);
        fdlDesc.top = new FormAttachment(0, margin);
        wlDesc.setLayoutData(fdlDesc);

        if (singleLine) {
            wDesc = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        } else {
            wDesc = new Text(shell, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        }

        wDesc.setText("");
        if (fixed) {
            props.setLook(wDesc, Props.WIDGET_STYLE_FIXED);
        } else {
            props.setLook(wDesc);
        }
        fdDesc = new FormData();
        fdDesc.left = new FormAttachment(0, 0);
        fdDesc.top = new FormAttachment(wlDesc, margin);
        fdDesc.right = new FormAttachment(100, 0);
        fdDesc.bottom = new FormAttachment(100, -50);
        wDesc.setLayoutData(fdDesc);
        wDesc.setEditable(!readonly);

        // Some buttons
        if (!readonly) {
            wOk = new Button(shell, SWT.PUSH);
            wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
            wCancel = new Button(shell, SWT.PUSH);
            wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

            BaseTransformDialog.positionBottomButtons(shell, new Button[] {wOk, wCancel}, margin, null);

            // Add listeners
            lsCancel = e -> cancel();
            lsOk = e -> ok();

            wOk.addListener(SWT.Selection, lsOk);
            wCancel.addListener(SWT.Selection, lsCancel);
        } else {
            wOk = new Button(shell, SWT.PUSH);
            wOk.setText(BaseMessages.getString(PKG, "System.Button.Close"));

            BaseTransformDialog.positionBottomButtons(shell, new Button[] {wOk}, margin, null);

            // Add listeners
            lsOk = e -> ok();
            wOk.addListener(SWT.Selection, lsOk);
        }

        lsDef =
                new SelectionAdapter() {
                    public void widgetDefaultSelected(SelectionEvent e) {
                        ok();
                    }
                };
        wDesc.addSelectionListener(lsDef);

        // Detect [X] or ALT-F4 or something that kills this window...
        shell.addShellListener(
                new ShellAdapter() {
                    public void shellClosed(ShellEvent e) {
                        checkCancel(e);
                    }
                });

        origText = text;
        getData();

        BaseTransformDialog.setSize(shell);

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
*/
//        return text;
//    }

/*
    public void dispose() {
        props.setScreen(new WindowProperty(shell));
        shell.dispose();
    }
*/

    public void getData() {
/*
        if (text != null) {
            wDesc.setText(text);
        }

        if (readonly) {
            wOk.setFocus();
        } else {
            wDesc.setFocus();
        }
*/
    }

/*
    public void checkCancel(ShellEvent e) {
        String newText = wDesc.getText();
        if (!newText.equals(origText)) {
            int save = HopGuiWorkflowGraph.showChangedWarning(shell, title);
            if (save == SWT.CANCEL) {
                e.doit = false;
            } else if (save == SWT.YES) {
                ok();
            } else {
                cancel();
            }
        } else {
            cancel();
        }
    }
*/

/*
    private void cancel() {
        text = null;
        dispose();
    }

    private void ok() {
        text = wDesc.getText();
        dispose();
    }
*/

/*
    public static final void editDescription(
            Shell shell, IDescription IDescription, String shellText, String message) {
        org.apache.hop.ui.core.dialog.EnterTextDialog textDialog =
                new org.apache.hop.ui.core.dialog.EnterTextDialog(shell, shellText, message, IDescription.getDescription());
        String description = textDialog.open();
        if (description != null) {
            IDescription.setDescription(description);
        }
    }
*/

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
}
