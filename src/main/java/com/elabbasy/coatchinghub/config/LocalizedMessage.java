package com.elabbasy.coatchinghub.config;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocalizedMessage {

    private static final String BUNDEL_NAME = "messages";

    public static String getArabicMessage(String key) {
        return getMessage(key, new Locale("ar"));
    }
    public static String getEnglishMessage(String key) {
        return getMessage(key , Locale.ENGLISH);
    }

    private static String getMessage(String key, Locale locale) {
        try {
            ResourceBundle messsages = ResourceBundle.getBundle(BUNDEL_NAME , locale);
            return messsages.getString(key);
        } catch (MissingResourceException exception) {
            throw new BusinessException(ErrorMessage.GENERAL_ERROR);
        }

    }
}
