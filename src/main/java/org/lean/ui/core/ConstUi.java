package org.lean.ui.core;


import org.apache.hop.i18n.BaseMessages;

import java.lang.annotation.Documented;

public class ConstUi {

    private static final Class<?> PKG = ConstUi.class; // For Translator

    /** Release Type */
    public enum ReleaseType {
        RELEASE_CANDIDATE {
            public String getMessage() {
                return BaseMessages.getString(PKG, "Const.PreviewRelease.HelpAboutText");
            }
        },
        MILESTONE {
            public String getMessage() {
                return BaseMessages.getString(PKG, "Const.Candidate.HelpAboutText");
            }
        },
        PREVIEW {
            public String getMessage() {
                return BaseMessages.getString(PKG, "Const.Milestone.HelpAboutText");
            }
        },
        GA {
            public String getMessage() {
                return BaseMessages.getString(PKG, "Const.GA.HelpAboutText");
            }
        };

        public abstract String getMessage();
    }

    /** What is the default frontend folder? */
    public static final String LEAN_FRONTEND_FOLDER = "./frontend/images/";

    /** What is the path to the Lean logo? */
    public static final String LEAN_LOGO_PATH = LEAN_FRONTEND_FOLDER + "lean-logo.svg";

    /** What is the default Lean Logo alt text? */
    public static final String LEAN_LOGO_ALT = "Lean - Lean Enterprise ANalytics";

    /** What is the default height for horizontal headers, footers and toolbars? */
    public static final String HBAR_HEIGHT = "4vh";

    /** What is the default width for vertical toolbars? */
    public static final String VBAR_WIDTH = "2.5vw";

    /** What's the file systems file separator on this operating system? */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /** What's the path separator on this operating system? */
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");

    /** CR: operating systems specific Carriage Return */
    public static final String CR = System.getProperty("line.separator");

    /**
     * Default icon size
     */
    public static final int ICON_SIZE =32;
    public static final String ICON_SIZE_PX = Integer.toString(ICON_SIZE) + "px";

    public static final int LARGE_ICON_SIZE = 48;
    public static final String LARGE_ICON_SIZE_PX = Integer.toString(LARGE_ICON_SIZE) + "px";

    public static final int SMALL_ICON_SIZE = 16;
    public static final String SMALL_ICON_SIZE_PX = Integer.toString(SMALL_ICON_SIZE) + "px";

    public static final int MEDIUM_ICON_SIZE = 24;
    public static final String MEDIUM_ICON_SIZE_PX = Integer.toString(MEDIUM_ICON_SIZE) + "px";

    public static final int DOCUMENTATION_ICON_SIZE = 14;
    public static final String DOCUMENTATION_ICON_SIZE_PX = Integer.toString(DOCUMENTATION_ICON_SIZE) + "px";

    /**
     * Default checkbox width
     */
    public static final int CHECKBOX_WIDTH = 20;

}
