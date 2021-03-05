package org.lean.ui.core.dialog;

public interface IDirectoryDialog {
    void setText(String text);

    void open();

    void setMessage(String message);

    void setFilterPath(String filterPath);

    String getFilterPath();
}
