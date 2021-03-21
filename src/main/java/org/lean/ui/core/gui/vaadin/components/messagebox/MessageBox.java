package org.lean.ui.core.gui.vaadin.components.messagebox;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MessageBox extends Dialog {

    private VerticalLayout mbLayout;
    private HorizontalLayout bodyLayout, headerLayout, footerLayout;
    private Label iconLabel;
    private Span titleSpan, messageSpan;

    private Button confirmButton, cancelButton;

    private static final String BAR_HEIGHT = "50px";

    public enum MessageType {
        CONFIRM,
        ERROR,
        INFO,
        QUESTION,
        WARN
    }

    public static class ConfirmEvent extends ComponentEvent<MessageBox>{
        public ConfirmEvent(MessageBox source, boolean fromClient){
            super(source, fromClient);
        }
    }

    public static class CancelEvent extends ComponentEvent<MessageBox>{
        public CancelEvent(MessageBox source, boolean fromClient){
            super(source, fromClient);
        }
    }

    public MessageBox(MessageType type, String title, String message, String confirmText, ComponentEventListener<ConfirmEvent> confirmListener){

        mbLayout = new VerticalLayout();

        headerLayout = new HorizontalLayout();
        headerLayout.setHeight(BAR_HEIGHT);
        headerLayout.setWidthFull();
        titleSpan = new Span();
        titleSpan.setText(title);
        headerLayout.add(titleSpan);

        bodyLayout = new HorizontalLayout();
        bodyLayout.setSizeFull();

        iconLabel = new Label();
        iconLabel.setWidth("50px");
        iconLabel.setHeight("50px");
        switch (type){
            case CONFIRM:
                iconLabel.add(new Image("frontend/images/confirm.svg", ""));
                break;
            case ERROR:
                iconLabel.add(new Image("frontend/images/error.svg", ""));
                break;
            case INFO:
                iconLabel.add(new Image("frontend/images/info.svg", ""));
                break;
            case QUESTION:
                iconLabel.add(new Image("frontend/images/question.svg", ""));
                break;
            case WARN:
                iconLabel.add(new Image("frontend/images/warning.svg", ""));
                break;
            default:
                iconLabel.add(new Image("frontend/images/confirm.svg", ""));
                break;
        }

        messageSpan = new Span();
        messageSpan.setText(message);
        messageSpan.setSizeFull();
        bodyLayout.add(iconLabel, messageSpan);

        footerLayout = new HorizontalLayout();
        footerLayout.setHeight(BAR_HEIGHT);
        footerLayout.setWidthFull();

        confirmButton = new Button(confirmText);
        ComponentUtil.addListener(confirmButton, ConfirmEvent.class, confirmListener);
        footerLayout.add(confirmButton);

        mbLayout.add(headerLayout, bodyLayout, footerLayout);
        add(mbLayout);

    }

    public MessageBox(MessageType type, String title, String message, String confirmText, ComponentEventListener<ConfirmEvent> confirmListener, String cancelText, ComponentEventListener<CancelEvent> cancelListener){
        this(type, title, message, confirmText, confirmListener);

    }

    public void setCancelButton(String cancelText, ComponentEventListener<CancelEvent> cancelListener){
        cancelButton = new Button(cancelText);
        ComponentUtil.addListener(cancelButton, CancelEvent.class, cancelListener);
    }

}
