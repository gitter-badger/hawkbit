/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.decorators;

import org.eclipse.hawkbit.ui.utils.SPUIButtonDefinitions;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Style for button: Tag.
 * 
 *
 * 
 *
 * 
 *
 */
public class SPUITagButtonStyle implements SPUIButtonDecorator {
    /**
     * Decorate Button and return.
     * 
     * @param button
     *            as Button
     * @param style
     *            as String
     * @param setStyle
     *            as String
     * @param icon
     *            as resource
     * @return Button
     */
    public Button decorate(final Button button, final String style, final boolean setStyle, final Resource icon) {

        /**
         * Add ... for long name
         */
        final String buttonCaption = button.getCaption();
        if (buttonCaption != null && buttonCaption.length() > SPUIButtonDefinitions.BUTTON_CAPTION_LENGTH) {
            button.setCaption(buttonCaption.substring(0, SPUIButtonDefinitions.BUTTON_CAPTION_LENGTH) + "...");
        }
        button.setImmediate(true);
        button.addStyleName("button-no-border" + " " + ValoTheme.BUTTON_BORDERLESS + " " + ValoTheme.BUTTON_TINY + " "
                + "button-tag-no-border");
        // Set Style
        if (null != style) {
            if (setStyle) {
                button.setStyleName(style);
            } else {
                button.addStyleName(style);
            }
        }
        // Set icon
        if (null != icon) {
            button.setIcon(icon);
        }
        return button;
    }
}
