package com.daimlertrucksasia.it.dsc.localization.localization;

import org.springframework.stereotype.Component;

/**
 * Defines a centralized collection of message template keys used for localization.
 * <p>
 * These constants represent message codes that are used to look up localized strings
 * from the localization service or database. They serve as stable identifiers that
 * decouple application logic from translatable content.
 * </p>
 *
 * <p>This class is annotated with {@link Component} to allow Spring to manage it
 * as a bean if runtime injection is needed, although all members are static.</p>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>
 *     String messageKey = LocalizationMsgTemplates.STAFF_NOT_FOUND;
 *     // Pass to localization resolver or service
 * </pre>
 *
 * @since 1.0
 */
@Component
public class LocalizationMsgTemplates {

    /** Message key for "Staff not found by user ID". */
    public static final String STAFF_NOT_FOUND = "staff.not.found.by.user.id";

    /** Message key for "Staff not found by username". */
    public static final String STAFF_NOT_FOUNT_BY_USER_NAME = "staff.not.found.by.user.name";

    /** Message key for "Invalid staff ID". */
    public static final String INVALID_STAFF_ID = "staff.not.valid";

    /** Message key for "Duplicate staff found". */
    public static final String DUPLICATE_STAFF_FOUND = "staff.duplicate.found";

    /** Message key for "Duplicate staff found by username". */
    public static final String STAFF_USERNAME_DUPLICATE = "staff.duplicate.found.by.user.name";

    /** Message key for "Duplicate staff found by email". */
    public static final String STAFF_EMAIL_DUPLICATE = "staff.duplicate.found.by.email";

    /** Generic message key for duplicate staff detection. */
    public static final String STAFF_DUPLICATE_GENERIC = "staff.duplicate.generic";
}
