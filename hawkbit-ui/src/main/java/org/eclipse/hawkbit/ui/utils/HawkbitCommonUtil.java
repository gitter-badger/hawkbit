/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.eclipse.hawkbit.im.authentication.UserPrincipal;
import org.eclipse.hawkbit.repository.SoftwareManagement;
import org.eclipse.hawkbit.repository.model.DistributionSetIdName;
import org.eclipse.hawkbit.repository.model.DistributionSetTagAssigmentResult;
import org.eclipse.hawkbit.repository.model.SoftwareModule;
import org.eclipse.hawkbit.repository.model.SoftwareModuleType;
import org.eclipse.hawkbit.repository.model.TargetIdName;
import org.eclipse.hawkbit.repository.model.TargetTagAssigmentResult;
import org.eclipse.hawkbit.repository.model.TargetInfo.PollStatus;
import org.eclipse.hawkbit.repository.model.TargetUpdateStatus;
import org.eclipse.hawkbit.ui.management.dstable.DistributionTable;
import org.eclipse.hawkbit.ui.management.targettable.TargetTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

/**
 * Common util class.
 * 
 *
 *
 */
public final class HawkbitCommonUtil {

    /**
     * Define spaced string.
     */
    public static final String SP_STRING_SPACE = " ";
    /**
     * Define empty string.
     */
    public static final String SP_STRING_EMPTY = "";

    /**
     * Html span.
     */
    public static final String SPAN_CLOSE = "</span>";

    private static final Logger LOG = LoggerFactory.getLogger(HawkbitCommonUtil.class);

    private static final String JS_DRAG_COUNT_REM_CHILD = " if(x) { document.head.removeChild(x); } ";

    private static final String DIV_DESCRIPTION = "<div id=\"desc-length\"><p id=\"desciption-p\">";

    private static final String DIV_CLOSE = "</p></div>";

    private static final String DRAG_COUNT_ELEMENT = "var x = document.getElementById('sp-drag-count'); ";
    private static final String CLOSE_BRACE = "\"; }';";
    private static final String CLOSE_BRACE_NOSEMICOLON = "\"; } ";
    private static final String COUNT_STYLE = " countStyle = document.createElement('style'); ";
    private static final String COUNT_STYLE_ID = " countStyle.id=\"sp-drag-count\"; ";
    private static final String APPEND_CHILD = " document.head.appendChild(countStyle);";
    private static final String HEADER_VERSION = "header.version";
    private static final String HEADER_NAME = "header.name";
    private static final String SM_HIGHLIGHT_CREATE_SCRIPT = "smHighlight = document.createElement('style'); smHighlight.id=\"sm-table-highlight\";  document.head.appendChild(smHighlight); ";
    private static final String SM_HIGHLIGHT_REMOVE_SCRIPT = "var y = document.getElementById('sm-table-highlight'); if(y) { document.head.removeChild(y); } ";
    private static final String SM_HIGHLIGHT_RESET_SCRIPT = SM_HIGHLIGHT_REMOVE_SCRIPT + SM_HIGHLIGHT_CREATE_SCRIPT
            + "document.getElementById('sm-table-highlight').innerHTML =\"\"; ";
    private static final String SM_HIGHLIGHT_SCRIPT_CURRENT = "var smHighlightStyle = document.getElementById('sm-table-highlight').innerHTML; ";
    private static final String SM_HIGHLIGHT_SCRIPT_APPEND = "document.getElementById('sm-table-highlight').innerHTML = smHighlightStyle;";
    private static final String NEW_PREVIEW_COLOR_CREATE_SCRIPT = "newColorPreview = document.createElement('style'); newColorPreview.id=\"new-color-preview\";  document.head.appendChild(newColorPreview); ";
    private static final String NEW_PREVIEW_COLOR_REMOVE_SCRIPT = "var z = document.getElementById('new-color-preview'); if(z) { document.head.removeChild(z); } ";
    private static final String NEW_PREVIEW_COLOR_SET_STYLE_SCRIPT = "document.getElementById('new-color-preview').innerHTML = newColorPreviewStyle;";
    private static final String PREVIEW_BUTTON_COLOR_CREATE_SCRIPT = "tagColorPreview = document.createElement('style'); tagColorPreview.id=\"tag-color-preview\";  document.head.appendChild(tagColorPreview); ";
    private static final String PREVIEW_BUTTON_COLOR_REMOVE_SCRIPT = "var a = document.getElementById('tag-color-preview'); if(a) { document.head.removeChild(a); } ";
    private static final String PREVIEW_BUTTON_COLOR_SET_STYLE_SCRIPT = "document.getElementById('tag-color-preview').innerHTML = tagColorPreviewStyle;";
    private static final String TARGET_TAG_DROP_CREATE_SCRIPT = "var p = document.getElementById('show-filter-drop-hint'); if(p) { } else { showTargetTagDrop = document.createElement('style'); showTargetTagDrop.id=\"show-filter-drop-hint\";  document.head.appendChild(showTargetTagDrop); }";
    private static final String TARGET_TAG_DROP_REMOVE_SCRIPT = "var m = document.getElementById('show-filter-drop-hint'); if(m) { document.head.removeChild(m); } ";
    private static final String DELETE_DROP_CREATE_SCRIPT = "var q = document.getElementById('show-delete-drop-hint'); if(q) { } else { showDeleteDrop = document.createElement('style'); showDeleteDrop.id=\"show-delete-drop-hint\";  document.head.appendChild(showDeleteDrop); }";
    private static final String DELETE_TAG_DROP_REMOVE_SCRIPT = "var o = document.getElementById('show-delete-drop-hint'); if(o) { document.head.removeChild(o); } ";

    /**
     * Define empty string.
     */
    /**
     * Private Constructor.
     */
    private HawkbitCommonUtil() {

    }

    /**
     * Check Map is valid.
     * 
     * @param mapCheck
     *            as Map
     * @return boolean as flag
     */
    public static boolean mapCheckObjKey(final Map<Object, Object> mapCheck) {
        boolean validMap = false;
        if (mapCheck != null && !mapCheck.isEmpty()) {
            validMap = true;
        }

        return validMap;
    }

    /**
     * Check Map is valid.
     * 
     * @param mapCheck
     *            as Map
     * @return boolean as flag
     */
    public static boolean mapCheckStrKey(final Map<String, Object> mapCheck) {
        boolean validMap = false;
        if (mapCheck != null && !mapCheck.isEmpty()) {
            validMap = true;
        }

        return validMap;
    }

    /**
     * Check Map is valid.
     * 
     * @param mapCheck
     *            as Map
     * @return boolean as flag
     */
    public static boolean mapCheckStrings(final Map<String, String> mapCheck) {
        boolean validMap = false;
        if (mapCheck != null && !mapCheck.isEmpty()) {
            validMap = true;
        }

        return validMap;
    }

    /**
     * Check List is valid.
     * 
     * @param listCheck
     *            as List
     * @return boolean as flag
     */
    public static boolean listCheckObj(final List<Object> listCheck) {
        boolean validList = false;
        if (listCheck != null && !listCheck.isEmpty()) {
            validList = true;
        }

        return validList;
    }

    /**
     * Check Boolean Array is valid.
     * 
     * @param bolArray
     *            as List
     * @return boolean as flag
     */
    public static boolean checkBolArray(final boolean[] bolArray) {
        boolean validBolArray = false;
        if (bolArray != null && bolArray.length > 0) {
            validBolArray = true;
        }

        return validBolArray;
    }

    /**
     * Check String null, return empty.
     * 
     * @param nString
     *            as String
     * @return String
     */
    public static String getEmptyString(final String nString) {
        String emptyStr = SP_STRING_SPACE;
        if (nString != null && nString.length() > 0) {
            emptyStr = nString;
        }
        return emptyStr;
    }

    /**
     * Check valid String.
     * 
     * @param nString
     *            as String
     * @return boolean as flag
     */
    public static boolean checkValidString(final String nString) {
        boolean strValid = false;
        if (nString != null && nString.length() > 0) {
            strValid = true;
        }
        return strValid;
    }

    /**
     * Trim the text and convert into null in case of empty string.
     * 
     * @param text
     *            as text to be trimed
     * @return null if the text is null or if the text is blank, text.trim() if
     *         the text is not empty.
     */
    public static String trimAndNullIfEmpty(final String text) {
        String emptyStr = null;
        if (null != text && !text.trim().isEmpty()) {
            emptyStr = text.trim();
        }
        return emptyStr;
    }

    /**
     * Concatenate the given text all the string arguments with the given
     * delimiter.
     * 
     * @param delimiter
     *            the delimiter text to be used while concatenation.
     * @param texts
     *            all these string values will be concatenated with the given
     *            delimiter.
     * @return null in case no text arguments to be compared. just concatenation
     *         of all texts arguments if "delimiter" is null or empty.
     *         concatenation of all texts arguments with "delimiter" if it not
     *         null.
     */
    public static String concatStrings(final String delimiter, final String... texts) {
        final String delim = delimiter == null ? SP_STRING_EMPTY : delimiter;
        final StringBuilder conCatStrBldr = new StringBuilder();
        String conCatedStr = null;
        if (null != texts) {
            for (final String text : texts) {
                conCatStrBldr.append(delim);
                conCatStrBldr.append(text);
            }
        }
        conCatedStr = conCatStrBldr.toString();
        return delim.length() > 0 && conCatedStr.startsWith(delim) ? conCatedStr.substring(1) : conCatedStr;
    }

    /**
     * Returns the input text within html bold tag <b>..</b>.
     * 
     * @param text
     *            is the text to be converted in to Bold
     * @return null if the input text param is null returns text with <b>...</b>
     *         tags.
     */
    public static String getBoldHTMLText(final String text) {
        String boldStr = null;
        if (text != null) {
            final StringBuilder updatedMsg = new StringBuilder("<b>");
            updatedMsg.append(text);
            updatedMsg.append("</b>");
            boldStr = updatedMsg.toString();
        }
        return boldStr;
    }

    /**
     * Get target label Id.
     * 
     * @param controllerId
     *            as String
     * @return String as label name
     */
    public static String getTargetLabelId(final String controllerId) {
        final StringBuilder labelId = new StringBuilder("target");
        labelId.append('.');
        labelId.append(controllerId);
        return labelId.toString();
    }

    /**
     * Get distribution table cell id.
     *
     * @param name
     *            distribution name
     * @param version
     *            distribution version
     * @return String distribution label id
     */
    public static String getDistributionLabelId(final String name, final String version) {
        final StringBuilder labelId = new StringBuilder("dist");
        labelId.append('.');
        labelId.append(name);
        labelId.append('.');
        labelId.append(version);
        return labelId.toString();
    }

    /**
     * Get software module label id.
     *
     * @param name
     *            software module name
     * @param version
     *            software module version
     * @return String software module label id
     */
    public static String getSwModuleLabelId(final String name, final String version) {
        final StringBuilder labelId = new StringBuilder("swModule");
        labelId.append('.');
        labelId.append(name);
        labelId.append('.');
        labelId.append(version);
        return labelId.toString();
    }

    /**
     * Check if two objects are same or not.
     *
     * @param obj1
     *            as reference
     * @param obj2
     *            as reference
     * @return true if both obj1 & obj2 are null (or) if the .equals() method
     *         return true. false if only either one of obj1 & obj2 is null (or)
     *         if .equals() method return false.
     */
    public static boolean bothSame(final Object obj1, final Object obj2) {
        boolean result = false;
        if (obj1 == null && obj2 == null) {
            result = true;
        } else if (obj1 == null && obj2 != null) {
            result = false;
        } else if (obj1 != null && obj2 == null) {
            result = false;
        } else {
            result = obj2.equals(obj1);
        }
        return result;
    }

    /**
     * Get label with software module name and description.
     *
     * @param name
     *            software module name
     * @param desc
     *            software module description
     * @return String
     */
    public static String getSwModuleNameDescLabel(final String name, final String desc) {
        final StringBuilder labelDesc = new StringBuilder();
        labelDesc.append(DIV_DESCRIPTION + getBoldHTMLText(getFormattedName(name)) + "<br>" + getFormattedName(desc));
        labelDesc.append(DIV_CLOSE);
        return labelDesc.toString();
    }

    /**
     * Get Label for Artifact Details.
     * 
     * @param name
     * @return
     */
    public static String getArtifactoryDetailsLabelId(final String name) {
        final StringBuilder labelDesc = new StringBuilder();
        labelDesc.append(DIV_DESCRIPTION + "Artifact Details of " + getBoldHTMLText(getFormattedName(name)));
        labelDesc.append(DIV_CLOSE);
        return labelDesc.toString();
    }

    /**
     * Get Label for Artifact Details.
     * 
     * @param caption
     *            as caption of the details
     * @param name
     *            as name
     * @return
     */
    public static String getSoftwareModuleName(final String caption, final String name) {
        final StringBuilder labelDesc = new StringBuilder();
        labelDesc.append(DIV_DESCRIPTION + caption + " : " + getBoldHTMLText(getFormattedName(name)));
        labelDesc.append(DIV_CLOSE);
        return labelDesc.toString();
    }

    /**
     * Get Label for Action History Details.
     * 
     * @param name
     * @return
     */
    public static String getActionHistoryLabelId(final String name) {
        final StringBuilder labelDesc = new StringBuilder();
        labelDesc.append(DIV_DESCRIPTION + "Action History For " + getBoldHTMLText(getFormattedName(name)));
        labelDesc.append(DIV_CLOSE);
        return labelDesc.toString();
    }

    /**
     * Get tool tip for Poll status.
     * 
     * @param pollStatus
     * @param i18N
     * @return
     */
    public static String getPollStatusToolTip(final PollStatus pollStatus, final I18N i18N) {
        if (pollStatus != null && pollStatus.getLastPollDate() != null && pollStatus.isOverdue()) {
            final TimeZone tz = SPDateTimeUtil.getBrowserTimeZone();
            return "Overdue for "
                    + SPDateTimeUtil.getDurationFormattedString(
                            pollStatus.getOverdueDate().atZone(SPDateTimeUtil.getTimeZoneId(tz)).toInstant()
                                    .toEpochMilli(),
                            pollStatus.getCurrentDate().atZone(SPDateTimeUtil.getTimeZoneId(tz)).toInstant()
                                    .toEpochMilli(), i18N);
        }
        return null;
    }

    /**
     * Null check for text.
     *
     * @param orgText
     *            text to be formatted
     * @return String formatted text
     */
    public static String getFormattedName(final String orgText) {
        return trimAndNullIfEmpty(orgText) == null ? SPUIDefinitions.SPACE : orgText;
    }

    /**
     * Format the lengthy text.
     *
     * @param orgText
     *            text to be formatted
     * @return String formatted text
     */
    public static String getFormattedText(final String orgText) {
        String formtdTxt = orgText == null ? "" : orgText;
        final int txtLengthAllowed = SPUIDefinitions.NAME_DESCRIPTION_LENGTH;
        if (formtdTxt.length() > txtLengthAllowed) {
            formtdTxt = new StringBuilder(orgText.substring(0, txtLengthAllowed)).append("...").toString();
        }

        return formtdTxt;
    }

    /**
     * Find extra height required to increase by all the components to utilize
     * the full height of browser for the responsive UI.
     * 
     * @param newBrowserHeight
     *            as current browser height.
     * @return extra height required to increase.
     */
    public static float findRequiredExtraHeight(final float newBrowserHeight) {
        return newBrowserHeight > SPUIDefinitions.REQ_MIN_BROWSER_HEIGHT ? newBrowserHeight
                - SPUIDefinitions.REQ_MIN_BROWSER_HEIGHT : 0;
    }

    /**
     * Find required extra height of software module.
     *
     * @param newBrowserHeight
     *            new browser height
     * @return float heigth of software module table
     */
    public static float findRequiredSwModuleExtraHeight(final float newBrowserHeight) {
        return newBrowserHeight > SPUIDefinitions.REQ_MIN_UPLOAD_BROWSER_HEIGHT ? newBrowserHeight
                - SPUIDefinitions.REQ_MIN_UPLOAD_BROWSER_HEIGHT : 0;
    }

    /**
     * Find required extra width of software module.
     *
     * @param newBrowserWidth
     *            new browser width
     * @return float width of software module table
     */
    public static float findRequiredSwModuleExtraWidth(final float newBrowserWidth) {
        return newBrowserWidth > SPUIDefinitions.REQ_MIN_UPLOAD_BROWSER_WIDTH ? newBrowserWidth
                - SPUIDefinitions.REQ_MIN_UPLOAD_BROWSER_WIDTH : 0;
    }

    /**
     * Get artifact upload pop up width.
     *
     * @param newBrowserWidth
     *            new browser width
     * @param minPopupWidth
     *            minimum popup width
     * @return float new pop up width
     */
    public static float getArtifactUploadPopupWidth(final float newBrowserWidth, final int minPopupWidth) {
        final float extraWidth = findRequiredSwModuleExtraWidth(newBrowserWidth);
        if (extraWidth + minPopupWidth > SPUIDefinitions.MAX_UPLOAD_CONFIRMATION_POPUP_WIDTH) {
            return SPUIDefinitions.MAX_UPLOAD_CONFIRMATION_POPUP_WIDTH;
        }
        return extraWidth + minPopupWidth;
    }

    /**
     *
     *
     * @param newBrowserHeight
     *            new browser height
     * @param minPopupHeight
     *            minimum pop up height
     * @return float new pop up height
     */
    public static float getArtifactUploadPopupHeight(final float newBrowserHeight, final int minPopupHeight) {
        final float extraWidth = findRequiredSwModuleExtraWidth(newBrowserHeight);
        if (extraWidth + minPopupHeight > SPUIDefinitions.MAX_UPLOAD_CONFIRMATION_POPUP_HEIGHT) {
            return SPUIDefinitions.MAX_UPLOAD_CONFIRMATION_POPUP_HEIGHT;
        }
        return extraWidth + minPopupHeight;
    }

    /**
     * Find extra width required to increase by all the components to utilize
     * the full width of browser for the responsive UI.
     *
     * @param newBrowserWidth
     *            as current browser width.
     * @return extra width required to be increased.
     */
    public static float findRequiredExtraWidth(final float newBrowserWidth) {
        float width = 0;
        if (newBrowserWidth < SPUIDefinitions.REQ_MIN_BROWSER_WIDTH) {
            width = SPUIDefinitions.REQ_MIN_BROWSER_WIDTH - newBrowserWidth;
        }
        return width;
    }

    /**
     * Find extra width required to increase by all the components to utilize
     * the full width of browser for the responsive UI.
     *
     * @param newBrowserWidth
     *            as current browser width.
     * @return extra width required to be increased.
     */
    public static float findExtraWidth(final float newBrowserWidth) {
        return newBrowserWidth > SPUIDefinitions.REQ_MIN_BROWSER_WIDTH ? newBrowserWidth
                - SPUIDefinitions.REQ_MIN_BROWSER_WIDTH : 0;
    }

    /**
     * Get target table width based on screen width.
     *
     * @param newBrowserWidth
     *            new browser width.
     * @param minTargetTableLength
     *            minimum target table width.
     * @param minDistTableLength
     *            minimum distribution table width.
     * @return float as table width
     */
    public static float getTargetTableWidth(final float newBrowserWidth, final float minTargetTableLength,
            final float minDistTableLength) {
        float width = 0;
        final float requiredExtraWidth = findRequiredExtraWidth(newBrowserWidth);
        // adjusting the target table width if distribution table width has
        // reached the maximum width
        if (requiredExtraWidth + minDistTableLength > SPUIDefinitions.MAX_DIST_TABLE_WIDTH) {
            width = requiredExtraWidth + minDistTableLength - SPUIDefinitions.MAX_DIST_TABLE_WIDTH;
        }
        if (width + minTargetTableLength + requiredExtraWidth > SPUIDefinitions.MAX_TARGET_TABLE_WIDTH) {
            return SPUIDefinitions.MAX_TARGET_TABLE_WIDTH;
        }
        return width + minTargetTableLength + requiredExtraWidth;
    }

    /**
     * Get distribution table width based on screen width.
     *
     * @param newBrowserWidth
     *            new browser width.
     * @param minTableWidth
     *            min distribution table width.
     * @return float as distribution table width.
     */
    public static float getDistTableWidth(final float newBrowserWidth, final float minTableWidth) {
        final float requiredExtraWidth = findExtraWidth(newBrowserWidth);
        float expectedDistWidth = minTableWidth;
        if (requiredExtraWidth > 0) {
            expectedDistWidth = expectedDistWidth + Math.round(requiredExtraWidth * 0.5f);
        }
        return expectedDistWidth;
    }

    /**
     * Get software module table width.
     *
     * @param newBrowserWidth
     * @param minTableWidth
     * @return
     */
    public static float getSoftwareModuleTableWidth(final float newBrowserWidth, final float minTableWidth) {
        final float requiredExtraWidth = findRequiredExtraWidth(newBrowserWidth);
        if (requiredExtraWidth + minTableWidth > SPUIDefinitions.MAX_UPLOAD_SW_MODULE_TABLE_WIDTH) {
            return SPUIDefinitions.MAX_UPLOAD_SW_MODULE_TABLE_WIDTH;
        }
        return requiredExtraWidth + minTableWidth;
    }

    /**
     * get formatted name - lastname,firstname.
     *
     * @param user
     *            user name
     * @return String formatted name
     */
    public static String getFormattedName(final UserDetails user) {
        final StringBuilder formattedName = new StringBuilder();
        if (user instanceof UserPrincipal) {
            if (trimAndNullIfEmpty(((UserPrincipal) user).getLastname()) != null) {
                formattedName.append(((UserPrincipal) user).getLastname());
            }
            if (trimAndNullIfEmpty(((UserPrincipal) user).getFirstname()) != null) {
                if (formattedName.length() > 0) {
                    formattedName.append(", ");
                }
                formattedName.append(((UserPrincipal) user).getFirstname());
            }
        } else if (user != null) {
            formattedName.append(user.getUsername());
        }
        return formattedName.toString();
    }

    /**
     * get the Last sequence of string which is after last dot in String.
     *
     * @param name
     *            dotted String name
     * @return String name
     */
    public static String getLastSequenceBySplitByDot(final String name) {
        String lastSequence = null;
        if (null != name) {
            final String[] strArray = name.split("\\.");
            if (strArray.length > 0) {
                lastSequence = strArray[strArray.length - 1];
            }
        }
        return lastSequence;
    }

    /**
     * Remove the prefix from text.
     *
     * @param text
     *            name
     * @param prefix
     *            text to be removed
     * @return String name
     */
    public static String removePrefix(final String text, final String prefix) {
        String str = null;
        if (null != text) {
            str = text.replaceFirst(prefix, "");
        }
        return str;
    }

    /**
     * Create javascript to display number of targets or distributions your are
     * dragging in the drag image.
     * 
     * @param count
     * @return
     */
    public static String getDragRowCountJavaScript(final int count) {
        final StringBuilder exeJS = new StringBuilder(DRAG_COUNT_ELEMENT).append(JS_DRAG_COUNT_REM_CHILD);
        final String currentTheme = UI.getCurrent().getTheme();
        if (count > 1) {
            exeJS.append(COUNT_STYLE)
                    .append(COUNT_STYLE_ID)
                    .append(" countStyle.innerHTML = '." + currentTheme + " tbody.v-drag-element tr:after { content:\""
                            + count + "\";top:-15px } ." + currentTheme + " tr.v-drag-element:after { content:\""
                            + count + CLOSE_BRACE_NOSEMICOLON + "." + currentTheme
                            + " table.v-drag-element:after{ content:\"" + count + CLOSE_BRACE).append(APPEND_CHILD);
        }
        return exeJS.toString();
    }

    /**
     * Get IM User for user UUID.
     * 
     * @param uuid
     * @return imReslovedUser user details
     */
    public static String getIMUser(final String uuid) {
        // Get modifed user
        String imReslovedUser = HawkbitCommonUtil.SP_STRING_SPACE;
        if (HawkbitCommonUtil.trimAndNullIfEmpty(uuid) != null) {
            final UserDetailsService idManagement = SpringContextHelper.getBean(UserDetailsService.class);
            try {
                imReslovedUser = HawkbitCommonUtil.getFormattedName(idManagement.loadUserByUsername(uuid));
            } catch (final UsernameNotFoundException e) {
                // nope not need to handle
            }
            // If Null display the UID
            if (HawkbitCommonUtil.trimAndNullIfEmpty(imReslovedUser) == null) {
                imReslovedUser = uuid;
            }
        }
        return imReslovedUser;
    }

    /**
     * Get formatted label.Appends ellipses if content does not fit the label.
     * 
     * @param labelContent
     *            content
     * @return Label
     */
    public static Label getFormatedLabel(final String labelContent) {
        final Label labelValue = new Label(labelContent, ContentMode.HTML);
        labelValue.setSizeFull();
        labelValue.addStyleName(SPUIDefinitions.TEXT_STYLE);
        labelValue.addStyleName("label-style");

        return labelValue;
    }

    /**
     * Get concatenated string of software module name and version.
     *
     * @param name
     * @param version
     * @return String concatenated string
     */
    public static String getFormattedNameVersion(final String name, final String version) {
        return name + ":" + version;
    }

    /**
     * Set height of artifact details table and drop area layout.
     *
     * @param dropLayout
     *            drop area layout
     * @param artifactDetailsLayout
     *            artifact details table
     * @param newHeight
     *            new browser height
     */
    public static void setArtifactDetailsLayoutHeight(final Component artifactDetailsLayout, final float newHeight) {
        final float extraBrowserHeight = HawkbitCommonUtil.findRequiredSwModuleExtraHeight(newHeight);
        final float tableHeight = SPUIDefinitions.MIN_UPLOAD_ARTIFACT_TABLE_HEIGHT + extraBrowserHeight;
        artifactDetailsLayout.setHeight(tableHeight, Unit.PIXELS);

    }

    /**
     * Set height of artifact details table and drop area layout.
     *
     * @param dropLayout
     *            drop area layout
     * @param artifactDetailsLayout
     *            artifact details table
     * @param newHeight
     *            new browser height
     */
    public static void setManageDistArtifactDetailsLayoutHeight(final Component artifactDetailsLayout,
            final float newHeight) {
        final float tableHeight = SPUIDefinitions.MIN_TARGET_DIST_TABLE_HEIGHT
                + HawkbitCommonUtil.findRequiredExtraHeight(newHeight) + 62;
        artifactDetailsLayout.setHeight(tableHeight, Unit.PIXELS);

    }

    /**
     * Close output stream.
     *
     * @param fos
     *            out stream to be closed
     */
    public static void closeOutoutStream(final FileOutputStream fos) {
        try {
            if (fos != null) {
                fos.close();
            }
        } catch (final IOException ioe) {
            LOG.error("Colud not close output stream", ioe);
        }
    }

    /**
     * Duplicate check - Unique Key.
     * 
     * @param name
     *            as string
     * @param version
     *            as string
     * @return boolean as flag
     */
    public static boolean isDuplicate(final String name, final String version) {
        final SoftwareManagement swMgmtService = SpringContextHelper.getBean(SoftwareManagement.class);
        final List<SoftwareModule> swModulesList = swMgmtService.findSoftwareModuleByNameAndVersion(name, version);
        boolean duplicate = false;
        if (swModulesList != null && !swModulesList.isEmpty()) {
            duplicate = true;
        }
        return duplicate;
    }

    /**
     * Add new base software module.
     * 
     * @param bsname
     *            base software module name
     * @param bsversion
     *            base software module version
     * @param bsvendor
     *            base software module vendor
     * @param bstype
     *            base software module type
     * @param description
     *            base software module description
     * @return BaseSoftwareModule new base software module
     */
    public static SoftwareModule addNewBaseSoftware(final String bsname, final String bsversion, final String bsvendor,
            final SoftwareModuleType bstype, final String description) {
        final SoftwareManagement swMgmtService = SpringContextHelper.getBean(SoftwareManagement.class);
        SoftwareModule newSWModule = new SoftwareModule();
        newSWModule.setType(bstype);
        newSWModule.setName(bsname);
        newSWModule.setVersion(bsversion);
        newSWModule.setVendor(bsvendor);
        newSWModule.setDescription(description);
        newSWModule = swMgmtService.createSoftwareModule(newSWModule);
        return newSWModule;
    }

    public static void setTargetVisibleColumns(final Table targTable) {
        final List<Object> targColumnIds = new ArrayList<>();
        final List<String> targColumnLabels = new ArrayList<>();
        targColumnIds.add(SPUIDefinitions.TARGET_TABLE_VISIBILE_COLUMN_NAME);
        targColumnLabels.add(SPUIDefinitions.TARGET_TABLE_VISIBILE_COLUMN_NAME);
        targTable.setVisibleColumns(targColumnIds.toArray());
        targTable.setColumnHeaders(targColumnLabels.toArray(new String[0]));
    }

    /**
     * @param distName
     * @param distVersion
     * @return
     */
    public static String getDistributionNameAndVersion(final String distName, final String distVersion) {
        final StringBuilder stringBuilder = new StringBuilder(distName);
        stringBuilder.append(':').append(distVersion);
        return stringBuilder.toString();
    }

    /**
     * Display Target Tag action message.
     * 
     * @param targTagName
     *            as tag name
     * @param result
     *            as TargetTagAssigmentResult
     * @param tagsClickedList
     *            as clicked tag list
     * @param i18n
     *            I18N
     * @return message
     */
    public static String getTargetTagAssigmentMsg(final String targTagName, final TargetTagAssigmentResult result,
            final I18N i18n) {
        final StringBuilder formMsg = new StringBuilder();
        final int assignedCount = result.getAssigned();
        final int alreadyAssignedCount = result.getAlreadyAssigned();
        final int unassignedCount = result.getUnassigned();

        if (assignedCount == 1) {
            formMsg.append(
                    i18n.get("message.target.assigned.one", new Object[] {
                            result.getAssignedTargets().get(0).getName(), targTagName })).append("<br>");

        } else if (assignedCount > 1) {
            formMsg.append(i18n.get("message.target.assigned.many", new Object[] { assignedCount, targTagName }))
                    .append("<br>");

            if (alreadyAssignedCount > 0) {
                final String alreadyAssigned = i18n.get("message.target.alreadyAssigned",
                        new Object[] { alreadyAssignedCount });
                formMsg.append(alreadyAssigned).append("<br>");
            }
        }

        if (unassignedCount == 1) {
            formMsg.append(
                    i18n.get("message.target.unassigned.one", new Object[] {
                            result.getUnassignedTargets().get(0).getName(), targTagName })).append("<br>");

        } else if (unassignedCount > 1) {
            formMsg.append(i18n.get("message.target.unassigned.many", new Object[] { unassignedCount, targTagName }))
                    .append("<br>");
        }
        return formMsg.toString();
    }

    /**
     * Get message to be displayed after distribution tag assignment.
     * 
     * @param targTagName
     *            tag name
     * @param result
     *            DistributionSetTagAssigmentResult
     * @param tagsClickedList
     *            list of clicked tags
     * @param i18n
     *            I18N
     * @return message
     */
    public static String getDistributionTagAssignmentMsg(final String targTagName,
            final DistributionSetTagAssigmentResult result, final I18N i18n) {
        final StringBuilder formMsg = new StringBuilder();
        final int assignedCount = result.getAssigned();
        final int alreadyAssignedCount = result.getAlreadyAssigned();
        final int unassignedCount = result.getUnassigned();

        if (assignedCount == 1) {
            formMsg.append(
                    i18n.get("message.target.assigned.one", new Object[] { result.getAssignedDs().get(0).getName(),
                            targTagName })).append("<br>");

        } else if (assignedCount > 1) {
            formMsg.append(i18n.get("message.target.assigned.many", new Object[] { assignedCount, targTagName }))
                    .append("<br>");

            if (alreadyAssignedCount > 0) {
                final String alreadyAssigned = i18n.get("message.target.alreadyAssigned",
                        new Object[] { alreadyAssignedCount });
                formMsg.append(alreadyAssigned).append("<br>");
            }
        }

        if (unassignedCount == 1) {
            formMsg.append(
                    i18n.get("message.target.unassigned.one", new Object[] { result.getUnassignedDs().get(0).getName(),
                            targTagName })).append("<br>");
        } else if (unassignedCount > 1) {
            formMsg.append(i18n.get("message.target.unassigned.many", new Object[] { unassignedCount, targTagName }))
                    .append("<br>");
        }
        return formMsg.toString();
    }

    /**
     * Create a lazy query container for the given query bean factory with empty
     * configurations.
     * 
     * @param queryFactory
     *            is reference of {@link BeanQueryFactory<? extends
     *            AbstractBeanQuery>} on which lazy container should create.
     * @return instance of {@link LazyQueryContainer}.
     */
    public static LazyQueryContainer createLazyQueryContainer(
            final BeanQueryFactory<? extends AbstractBeanQuery> queryFactory) {
        final Map<String, Object> queryConfig = new HashMap<String, Object>();
        queryFactory.setQueryConfiguration(queryConfig);
        final LazyQueryContainer typeContainer = new LazyQueryContainer(new LazyQueryDefinition(true, 20,
                SPUILabelDefinitions.VAR_NAME), queryFactory);
        return typeContainer;
    }

    /**
     * 
     * Create lazy query container for DS type.
     * 
     * @param queryFactory
     * @return LazyQueryContainer
     */
    public static LazyQueryContainer createDSLazyQueryContainer(
            final BeanQueryFactory<? extends AbstractBeanQuery> queryFactory) {
        final Map<String, Object> queryConfig = new HashMap<String, Object>();
        queryFactory.setQueryConfiguration(queryConfig);
        final LazyQueryContainer typeContainer = new LazyQueryContainer(new LazyQueryDefinition(true, 20, "tagIdName"),
                queryFactory);
        return typeContainer;
    }

    /**
     * Set distribution table column properties.
     * 
     * @param container
     *            table container
     */
    public static void getDsTableColumnProperties(final Container container) {
        final LazyQueryContainer lqc = (LazyQueryContainer) container;
        lqc.addContainerProperty(SPUILabelDefinitions.VAR_ID, Long.class, null, false, false);
        lqc.addContainerProperty(SPUILabelDefinitions.VAR_DESC, String.class, "", false, true);
        lqc.addContainerProperty(SPUILabelDefinitions.VAR_VERSION, String.class, null, false, false);
        lqc.addContainerProperty(SPUILabelDefinitions.VAR_NAME, String.class, null, false, true);
        lqc.addContainerProperty(SPUILabelDefinitions.VAR_CREATED_BY, String.class, null, false, true);
        lqc.addContainerProperty(SPUILabelDefinitions.VAR_LAST_MODIFIED_BY, String.class, null, false, true);
        lqc.addContainerProperty(SPUILabelDefinitions.VAR_CREATED_DATE, String.class, null, false, true);
        lqc.addContainerProperty(SPUILabelDefinitions.VAR_LAST_MODIFIED_DATE, String.class, null, false, true);
    }

    /**
     * Get visible columns in table.
     * 
     * @param isMaximized
     *            true if table is maximized
     * @param isShowPinColumn
     *            if true pin column will be displayed.
     * @param i18n
     *            I18N
     * @return List<TableColumn> list of columns to be displayed.
     */
    public static List<TableColumn> getTableVisibleColumns(final Boolean isMaximized, final Boolean isShowPinColumn,
            final I18N i18n) {
        final List<TableColumn> columnList = new ArrayList<TableColumn>();
        if (isMaximized) {
            columnList.add(new TableColumn(SPUILabelDefinitions.VAR_NAME, i18n.get(HEADER_NAME), 0.2f));
            columnList.add(new TableColumn(SPUILabelDefinitions.VAR_VERSION, i18n.get(HEADER_VERSION), 0.1f));
            columnList.add(new TableColumn(SPUILabelDefinitions.VAR_CREATED_BY, i18n.get("header.createdBy"), 0.1f));
            columnList
                    .add(new TableColumn(SPUILabelDefinitions.VAR_CREATED_DATE, i18n.get("header.createdDate"), 0.1f));
            columnList.add(new TableColumn(SPUILabelDefinitions.VAR_LAST_MODIFIED_BY, i18n.get("header.modifiedBy"),
                    0.1f));
            columnList.add(new TableColumn(SPUILabelDefinitions.VAR_LAST_MODIFIED_DATE,
                    i18n.get("header.modifiedDate"), 0.1f));
            columnList.add(new TableColumn(SPUILabelDefinitions.VAR_DESC, i18n.get("header.description"), 0.2f));
        } else if (isShowPinColumn) {
            columnList.add(new TableColumn(SPUILabelDefinitions.VAR_NAME, i18n.get(HEADER_NAME), 0.7f));
            columnList.add(new TableColumn(SPUILabelDefinitions.VAR_VERSION, i18n.get(HEADER_VERSION), 0.2f));
            columnList.add(new TableColumn(SPUILabelDefinitions.PIN_COLUMN, SP_STRING_EMPTY, 0.1f));
        } else {
            columnList.add(new TableColumn(SPUILabelDefinitions.VAR_NAME, i18n.get(HEADER_NAME), 0.8f));
            columnList.add(new TableColumn(SPUILabelDefinitions.VAR_VERSION, i18n.get(HEADER_VERSION), 0.2f));
        }
        return columnList;

    }

    /**
     * Reset the software module table rows highlight css.
     * 
     * @return javascript to rest software module table rows highlight css.
     */
    public static String getScriptSMHighlightReset() {
        return SM_HIGHLIGHT_RESET_SCRIPT;
    }

    /**
     * Highlight software module rows with the color of sw-type.
     * 
     * @param colorCSS
     *            color to generate the css script.
     * @return javascript to append software module table rows with highlighted
     *         color.
     */
    public static String getScriptSMHighlightWithColor(final String colorCSS) {
        final StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append(SM_HIGHLIGHT_SCRIPT_CURRENT).append("smHighlightStyle = smHighlightStyle + \"")
                .append(colorCSS).append("\";").append(SM_HIGHLIGHT_SCRIPT_APPEND);
        return scriptBuilder.toString();
    }

    /**
     * Get javascript to reflect new color selection in color picker preview for
     * name and description fields .
     * 
     * @param colorPickedPreview
     *            changed color
     * @return javascript for the selected color.
     */
    public static String changeToNewSelectedPreviewColor(final String colorPickedPreview) {
        final StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder
                .append(NEW_PREVIEW_COLOR_REMOVE_SCRIPT)
                .append(NEW_PREVIEW_COLOR_CREATE_SCRIPT)
                .append("var newColorPreviewStyle = \".v-app .new-tag-name{ border: solid 3px ")
                .append(colorPickedPreview)
                .append(" !important; width:138px; margin-left:2px !important; box-shadow:none !important; } \"; ")
                .append("newColorPreviewStyle = newColorPreviewStyle + \".v-app .new-tag-desc{ border: solid 3px ")
                .append(colorPickedPreview)
                .append(" !important; width:138px; height:75px !important; margin-top:4px !important; margin-left:2px !important;;box-shadow:none !important;} \"; ")
                .append(NEW_PREVIEW_COLOR_SET_STYLE_SCRIPT);
        return scriptBuilder.toString();
    }

    /**
     * Get javascript to reflect new color selection for preview button.
     * 
     * @param color
     *            changed color
     * @return javascript for the selected color.
     */
    public static String getPreviewButtonColorScript(final String color) {
        final StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder
                .append(PREVIEW_BUTTON_COLOR_REMOVE_SCRIPT)
                .append(PREVIEW_BUTTON_COLOR_CREATE_SCRIPT)
                .append("var tagColorPreviewStyle = \".v-app .tag-color-preview{ height: 15px !important; padding: 0 10px !important; border: 0px !important; margin-left:12px !important;  margin-top: 4px !important; border-width: 0 !important; background: ")
                .append(color)
                .append(" } .v-app .tag-color-preview:after{ border-color: none !important; box-shadow:none !important;} \"; ")
                .append(PREVIEW_BUTTON_COLOR_SET_STYLE_SCRIPT);
        return scriptBuilder.toString();
    }

    /**
     * Java script to display drop hints for tags.
     * 
     * @return javascript
     */
    public static String dispTargetTagsDropHintScript() {
        final String targetDropStyle = "document.getElementById('show-filter-drop-hint').innerHTML = '."
                + UI.getCurrent().getTheme() + " .target-tag-drop-hint { border: 1px dashed #26547a !important; }';";
        final StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append(TARGET_TAG_DROP_CREATE_SCRIPT).append(targetDropStyle);
        return scriptBuilder.toString();
    }

    /**
     * Java script to hide drop hints for tags.
     * 
     * @return javascript
     */
    public static String hideTargetTagsDropHintScript() {
        return TARGET_TAG_DROP_REMOVE_SCRIPT;
    }

    /**
     * Java script to display drop hint for Delete button.
     * 
     * @return javascript
     */
    public static String dispDeleteDropHintScript() {
        final String deleteTagDropStyle = "document.getElementById('show-delete-drop-hint').innerHTML = '."
                + UI.getCurrent().getTheme() + " .show-delete-drop-hint { border: 1px dashed #26547a !important; }';";
        final StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append(DELETE_DROP_CREATE_SCRIPT).append(deleteTagDropStyle);
        return scriptBuilder.toString();
    }

    /**
     * Java script to hide drop hint for delete button.
     * 
     * @return javascript
     */
    public static String hideDeleteDropHintScript() {
        return DELETE_TAG_DROP_REMOVE_SCRIPT;
    }

    /**
     * Get the details of selected rows of {@link TargetTable}.
     * 
     * @param sourceTable
     * @return set of {@link TargetIdName}
     */
    public static Set<TargetIdName> getSelectedTargetDetails(final Table sourceTable) {
        Set<TargetIdName> targetSelected = null;
        if (sourceTable.getValue() != null) {
            targetSelected = new LinkedHashSet<>((Set) sourceTable.getValue());
            targetSelected.remove(null);
        }
        return targetSelected;
    }

    /**
     * Get the details of selected rows of {@link DistributionTable}.
     * 
     * @param sourceTable
     * @return set of {@link DistributionSetIdName}
     */
    public static Set<DistributionSetIdName> getSelectedDSDetails(final Table sourceTable) {
        Set<DistributionSetIdName> distSelected = null;
        if (sourceTable.getValue() != null) {
            distSelected = new LinkedHashSet<>((Set) sourceTable.getValue());
            distSelected.remove(null);
        }
        return distSelected;
    }

    /**
     * 
     * Add target table container properties.
     * 
     * @param container
     *            table container
     */
    public static void addTargetTableContainerProperties(final Container container) {
        final LazyQueryContainer targetTableContainer = (LazyQueryContainer) container;
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.VAR_CONT_ID, String.class, "", false, false);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.VAR_NAME, String.class, "", false, true);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.VAR_TARGET_STATUS, TargetUpdateStatus.class,
                TargetUpdateStatus.UNKNOWN, false, false);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.ASSIGNED_DISTRIBUTION_ID, Long.class, null,
                false, false);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.INSTALLED_DISTRIBUTION_ID, Long.class, null,
                false, false);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.ASSIGNED_DISTRIBUTION_NAME_VER, String.class,
                "", false, true);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.INSTALLED_DISTRIBUTION_NAME_VER, String.class,
                "", false, true);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.LAST_QUERY_DATE, Date.class, null, false, false);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.VAR_CREATED_BY, String.class, null, false, true);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.VAR_LAST_MODIFIED_BY, String.class, null, false,
                true);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.VAR_CREATED_DATE, String.class, null, false,
                true);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.VAR_LAST_MODIFIED_DATE, String.class, null,
                false, true);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.VAR_POLL_STATUS_TOOL_TIP, String.class, null,
                false, true);
        targetTableContainer.addContainerProperty(SPUILabelDefinitions.VAR_DESC, String.class, "", false, true);
    }

    /**
     * 
     * Apply style for status label in target table.
     * 
     * @param targetTable
     *            target table
     * @param pinBtn
     *            pin button used for status display and pin on mouse over
     * @param itemId
     *            id of the tabel row
     */
    public static void applyStatusLblStyle(final Table targetTable, final Button pinBtn, final Object itemId) {
        final Item item = targetTable.getItem(itemId);
        if (item != null) {
            final TargetUpdateStatus updateStatus = (TargetUpdateStatus) item.getItemProperty(
                    SPUILabelDefinitions.VAR_TARGET_STATUS).getValue();
            pinBtn.removeStyleName("statusIconRed statusIconBlue statusIconGreen statusIconYellow statusIconLightBlue");
            if (updateStatus == TargetUpdateStatus.ERROR) {
                pinBtn.addStyleName("statusIconRed");
            } else if (updateStatus == TargetUpdateStatus.UNKNOWN) {
                pinBtn.addStyleName("statusIconBlue");
            } else if (updateStatus == TargetUpdateStatus.IN_SYNC) {
                pinBtn.addStyleName("statusIconGreen");
            } else if (updateStatus == TargetUpdateStatus.PENDING) {
                pinBtn.addStyleName("statusIconYellow");
            } else if (updateStatus == TargetUpdateStatus.REGISTERED) {
                pinBtn.addStyleName("statusIconLightBlue");
            }
        }
    }

}
