/*******************************************************************************
 * Copyright 2018, Techblue Software Pvt Ltd. All Rights Reserved.
 * No part of this content may be used without Techblue's express consent.
 ******************************************************************************/
package uk.co.techblue.docusign.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The Class EnvelopeNotificationInfo.
 */
@JsonSerialize
@JsonInclude(value = Include.NON_NULL)
public class EnvelopeNotificationInfo extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5805391762394797959L;

    /** The use account defaults. */
    @JsonProperty
    private Boolean useAccountDefaults = Boolean.TRUE;

    /** The expirations. */
    @JsonProperty
    private EnvelopeExpiration expirations;

    /** The reminders. */
    @JsonProperty
    private EnvelopeReminder reminders;

    /**
     * Checks if is use account defaults.
     *
     * @return true, if is use account defaults
     */
    public boolean isUseAccountDefaults() {
        return useAccountDefaults;
    }

    /**
     * Sets the use account defaults.
     *
     * @param useAccountDefaults the new use account defaults
     */
    public void setUseAccountDefaults(boolean useAccountDefaults) {
        this.useAccountDefaults = useAccountDefaults;
    }

    /**
     * Gets the expirations.
     * 
     * @return the expirations
     */
    public EnvelopeExpiration getExpirations() {
        return expirations;
    }

    /**
     * Sets the expirations.
     * 
     * @param expirations the new expirations
     */
    public void setExpirations(EnvelopeExpiration expirations) {
        this.expirations = expirations;
    }

    /**
     * Gets the reminders.
     * 
     * @return the reminders
     */
    public EnvelopeReminder getReminders() {
        return reminders;
    }

    /**
     * Sets the reminders.
     * 
     * @param reminders the new reminders
     */
    public void setReminders(EnvelopeReminder reminders) {
        this.reminders = reminders;
    }
}
