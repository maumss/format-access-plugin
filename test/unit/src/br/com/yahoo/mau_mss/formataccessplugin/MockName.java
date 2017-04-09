package br.com.yahoo.mau_mss.formataccessplugin;

import javax.lang.model.element.Name;

/**
 * Title: MockName
 * Description:
 * Date: Feb 1, 2016, 7:56:39 PM
 *
 * @author Mauricio Soares da Silva (Mau)
 */
public class MockName implements Name {
  private String name;

  /**
   * Create a new instance of <code>MockName</code>.
   */
  public MockName() {
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean contentEquals(CharSequence cs) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int length() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public char charAt(int index) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String toString() {
    return this.name;
  }
}
