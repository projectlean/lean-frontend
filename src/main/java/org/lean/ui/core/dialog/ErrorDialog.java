package org.lean.ui.core.dialog;

import com.google.common.annotations.VisibleForTesting;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.hop.core.Const;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.LogChannel;
import org.apache.hop.i18n.BaseMessages;
import org.lean.ui.core.ConstUi;
import org.lean.ui.core.PropsUi;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

public class ErrorDialog extends Dialog {

    private static final Class<?> PKG = ErrorDialog.class; // For Translator

    private Label wlDesc;
    private Text wDesc;
    private Button wOk, wDetails, wCancel;

    private PropsUi props;

    private boolean cancelled;
    private Function<String, String> exMsgFunction = Function.identity();

     private ILogChannel log;

    private VerticalLayout errorDialogLayout, bodyLayout;
    private HorizontalLayout headerLayout, buttonsLayout;

    public ErrorDialog(){
        errorDialogLayout = new VerticalLayout();
        headerLayout = new HorizontalLayout();
        headerLayout.setPadding(false);
        headerLayout.setMargin(false);
        headerLayout.setSpacing(false);
        headerLayout.setHeight(ConstUi.HBAR_HEIGHT);
        headerLayout.setWidthFull();
        headerLayout.setId("error-header");

        bodyLayout = new VerticalLayout();
        bodyLayout.setPadding(false);
        bodyLayout.setMargin(false);
        bodyLayout.setSpacing(false);
        bodyLayout.setSizeFull();
        bodyLayout.setId("error-body");

        buttonsLayout = new HorizontalLayout();
        buttonsLayout.setPadding(false);
        buttonsLayout.setMargin(false);
        buttonsLayout.setSpacing(false);
        buttonsLayout.setHeight(ConstUi.HBAR_HEIGHT);
        buttonsLayout.setWidthFull();
        buttonsLayout.setId("error-buttons");

        errorDialogLayout.add(headerLayout, bodyLayout, buttonsLayout);
        add(errorDialogLayout);

    }
    public ErrorDialog(String title, String message, Throwable throwable) {
        this(title, message, throwable, Function.identity());
    }

    public ErrorDialog(
            String title,
            String message,
            Throwable throwable,
            Function<String, String> exMsgFunction) {
        super();
        this.exMsgFunction = exMsgFunction;

        throwable.printStackTrace();

         this.log = new LogChannel("ErrorDialog");
         log.logError(message, throwable);

        if (throwable instanceof Exception) {
            showErrorDialog(title, message, (Exception) throwable, false);
        } else {
            // not optimal, but better then nothing
            showErrorDialog(
                    title, message + Const.CR + Const.getStackTracker(throwable), null, false);
        }
    }

    public ErrorDialog(String title, String message, Exception exception) {
        this();
        showErrorDialog(title, message, exception, false);
    }

    public ErrorDialog(
            String title, String message, Exception exception, boolean showCancelButton) {
        this();
        showErrorDialog(title, message, exception, showCancelButton);
    }

    private void showErrorDialog(String title, String message, Exception exception, boolean showCancelButton) {
        this.props = PropsUi.getInstance();

        int margin = props.getMargin();

        // From transform line
        wlDesc = new Label();


        final StringBuilder text = new StringBuilder();
        final StringBuilder details = new StringBuilder();

        if (exception != null) {
            handleException(message, exception, text, details);
            wDesc = new Text(exMsgFunction.apply(text.toString()));
        } else {
            text.append(message);
            wDesc = new Text(exMsgFunction.apply(text.toString()));
        }
        bodyLayout.add(wDesc);

        wOk = new Button();
        wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        if (showCancelButton) {
            wCancel = new Button();
            wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
        }
        wDetails = new Button();
        wDetails.setText(BaseMessages.getString(PKG, "System.Button.Details"));

        Button[] buttons;
        if (showCancelButton) {
            buttons =
                    new Button[] {
                            wOk, wCancel, wDetails,
                    };
        } else {
            buttons =
                    new Button[] {
                            wOk, wDetails,
                    };
        }

        FlexLayout buttonWrapper = new FlexLayout(buttons);
        buttonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonsLayout.add(buttonWrapper);

        // Add listeners
        wOk.addClickListener(e -> ok());
        if (showCancelButton) {
            wCancel.addClickListener(e -> cancel());
        }
        wDetails.addClickListener(e -> showDetails(details.toString()));

        this.open();
    }

    @VisibleForTesting
    protected void handleException(
            String message, Exception exception, StringBuilder text, StringBuilder details) {
        text.append(Const.getSimpleStackTrace(exception));
        text.append(Const.CR);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        details.append(sw.getBuffer());
    }

    protected void showDetails(String details) {
        EnterTextDialog dialog =
                new EnterTextDialog(
                        BaseMessages.getString(PKG, "ErrorDialog.ShowDetails.Title"),
                        BaseMessages.getString(PKG, "ErrorDialog.ShowDetails.Message"),
                        details);
        dialog.setReadOnly();
        dialog.open();
    }

    private void ok() {

    }

    private void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
