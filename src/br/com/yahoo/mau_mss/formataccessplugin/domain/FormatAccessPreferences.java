package br.com.yahoo.mau_mss.formataccessplugin.domain;

import br.com.yahoo.mau_mss.formataccessplugin.FormatAccess;
import org.openide.util.NbPreferences;

/**
 * Title: FormatAccessPreferences
 * Description:
 * Date: Oct 31, 2015, 12:00:20 PM
 *
 * @author Mauricio Soares da Silva (Mau)
 */
public class FormatAccessPreferences {
  private boolean enableOnSave;
  private boolean addThisForNSFields;
  private boolean removeThisForNSMethods;
  private boolean addQualifierForSFields;
  private boolean addQualifierForSMethods;

  /**
   * Create a new instance of <code>FormatAccessPreferences</code>.
   */
  public FormatAccessPreferences() {
  }

  public FormatAccessPreferences(boolean enableOnSave, boolean addThisForNSFields,
      boolean removeThisForNSMethods, boolean addQualifierForSFields, boolean addQualifierForSMethods) {
    this.enableOnSave = enableOnSave;
    this.addThisForNSFields = addThisForNSFields;
    this.removeThisForNSMethods = removeThisForNSMethods;
    this.addQualifierForSFields = addQualifierForSFields;
    this.addQualifierForSMethods = addQualifierForSMethods;
  }

  public boolean isEnableOnSave() {
    return enableOnSave;
  }

  public void setEnableOnSave(boolean enableOnSave) {
    this.enableOnSave = enableOnSave;
  }

  public boolean isAddThisForNSFields() {
    return addThisForNSFields;
  }

  public void setAddThisForNSFields(boolean addThisForNSFields) {
    this.addThisForNSFields = addThisForNSFields;
  }

  public boolean isRemoveThisForNSMethods() {
    return removeThisForNSMethods;
  }

  public void setRemoveThisForNSMethods(boolean removeThisForNSMethods) {
    this.removeThisForNSMethods = removeThisForNSMethods;
  }

  public boolean isAddQualifierForSFields() {
    return addQualifierForSFields;
  }

  public void setAddQualifierForSFields(boolean addQualifierForSFields) {
    this.addQualifierForSFields = addQualifierForSFields;
  }

  public boolean isAddQualifierForSMethods() {
    return addQualifierForSMethods;
  }

  public void setAddQualifierForSMethods(boolean addQualifierForSMethods) {
    this.addQualifierForSMethods = addQualifierForSMethods;
  }

  public boolean hasNothingToDo() {
    return !isAddThisForNSFields() && !isRemoveThisForNSMethods() && !isAddQualifierForSFields()
        && !isAddQualifierForSMethods();
  }

  /**
   * Salva as preferências do usuário
   *
   * @param formatAccessesPreferences
   */
  public static void store(FormatAccessPreferences formatAccessesPreferences) {
    if (formatAccessesPreferences == null) {
      return;
    }
    NbPreferences.forModule(FormatAccess.class).putBoolean("enableOnSave", formatAccessesPreferences.isEnableOnSave());
    NbPreferences.forModule(FormatAccess.class).putBoolean("addThisForNSFields", formatAccessesPreferences.isAddThisForNSFields());
    NbPreferences.forModule(FormatAccess.class).putBoolean("removeThisForNSMethods", formatAccessesPreferences.isRemoveThisForNSMethods());
    NbPreferences.forModule(FormatAccess.class).putBoolean("addQualifierForSFields", formatAccessesPreferences.isAddQualifierForSFields());
    NbPreferences.forModule(FormatAccess.class).putBoolean("addQualifierForSMethods", formatAccessesPreferences.isAddQualifierForSMethods());
  }

  /**
   * Carrega as preferências do usuário. EnableOnSave será false por default, enquanto os outros
   * atributos serão true or default.
   *
   * @return
   */
  public static FormatAccessPreferences load() {
    FormatAccessPreferences formatAccessPreferences = new FormatAccessPreferences();
    formatAccessPreferences.setEnableOnSave(NbPreferences.forModule(FormatAccess.class).getBoolean("enableOnSave", false));
    formatAccessPreferences.setAddThisForNSFields(NbPreferences.forModule(FormatAccess.class).getBoolean("addThisForNSFields", true));
    formatAccessPreferences.setRemoveThisForNSMethods(NbPreferences.forModule(FormatAccess.class).getBoolean("removeThisForNSMethods", true));
    formatAccessPreferences.setAddQualifierForSFields(NbPreferences.forModule(FormatAccess.class).getBoolean("addQualifierForSFields", true));
    formatAccessPreferences.setAddQualifierForSMethods(NbPreferences.forModule(FormatAccess.class).getBoolean("addQualifierForSMethods", true));
    return formatAccessPreferences;
  }
}
