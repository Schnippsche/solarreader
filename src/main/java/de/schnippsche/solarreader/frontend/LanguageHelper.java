package de.schnippsche.solarreader.frontend;

import org.tinylog.Logger;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageHelper
{
  private static final Pattern pattern = Pattern.compile("\\{([\\w.]+)\\}");
  private ResourceBundle labels;
  private String currentLanguage;

  public LanguageHelper()
  {
    setLanguage("de");
  }

  public void setLanguage(String language)
  {
    if (!language.equals(currentLanguage))
    {
      Logger.info("switch to language " + language);
      Locale locale = new Locale(language);
      currentLanguage = language;
      // properties files must be ISO-8859-1 in java 8!!
      labels = ResourceBundle.getBundle("i18n.Translate", locale);
    }
  }

  public String getCurrentLanguage()
  {
    return currentLanguage;
  }

  /**
   * replace all occurrences of language placeholders in the current language terms.
   *
   * @param source the source String with placeholders
   * @return the translated String, all placeholders are replaced
   */
  public String replacePlaceholder(String source)
  {
    Matcher matcher = pattern.matcher(source);
    String result = source;
    while (matcher.find())
    {
      String group = matcher.group();
      String search = matcher.group(1);
      result = result.replace(group, getLocaleString(search));
    }
    return result;
  }

  /**
   * get a localized string.
   *
   * <p>this method does <b>NOT</b> throw an Exception if the key is not found.
   *
   * @param resourceKey a resource key for the language resource bundle
   * @return the translated string from the language bundle or blank if it is not found
   */
  public String getLocaleString(String resourceKey)
  {
    try
    {
      return labels.getString(resourceKey);
    } catch (MissingResourceException e)
    {
      return "{" + resourceKey + "}";
    }
  }

}
