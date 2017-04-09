/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.yahoo.mau_mss.formataccessplugin.ui;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Controla o sub-painel Options -> Editor -> Member Acesses
 *
 * @author Mau
 * @see https://platform.netbeans.org/tutorials/nbm-options.html#secondary
 */
@OptionsPanelController.SubRegistration(
    location = "Editor",
    displayName = "#AdvancedOption_DisplayName_MemberAccesses",
    keywords = "#AdvancedOption_Keywords_MemberAccesses",
    keywordsCategory = "Editor/MemberAccesses"
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_MemberAccesses=Member Accesses",
  "AdvancedOption_Keywords_MemberAccesses=Qualifiers"})
public class MemberAccessesOptionsPanelController extends OptionsPanelController {
  private MemberAccessesPanel panel;
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private boolean changed;

  @Override
  public void update() {
    getPanel().load();
    changed = false;
  }

  @Override
  public void applyChanges() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        getPanel().store();
        changed = false;
      }
    });
  }

  @Override
  public void cancel() {
    // need not do anything special, if no changes have been persisted yet
  }

  @Override
  public boolean isValid() {
    return getPanel().valid();
  }

  @Override
  public boolean isChanged() {
    return changed;
  }

  @Override
  public HelpCtx getHelpCtx() {
    return null; // new HelpCtx("...ID") if you have a help set
  }

  @Override
  public JComponent getComponent(Lookup masterLookup) {
    return getPanel();
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener l) {
    pcs.addPropertyChangeListener(l);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener l) {
    pcs.removePropertyChangeListener(l);
  }

  private MemberAccessesPanel getPanel() {
    if (panel == null) {
      panel = new MemberAccessesPanel(this);
    }
    return panel;
  }

  void changed() {
    if (!changed) {
      changed = true;
      pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
    }
    pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
  }

}
