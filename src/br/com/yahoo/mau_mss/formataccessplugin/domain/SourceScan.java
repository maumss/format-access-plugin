package br.com.yahoo.mau_mss.formataccessplugin.domain;

/**
 * Title: SourceScan
 * Description: Cria um resumo de cada linha dentro do código fonte analisado
 * Date: Jul 11, 2015, 7:25:29 PM
 *
 * @author Mauricio Soares da Silva (Mau)
 */
public class SourceScan {
  private String text;
  private boolean locked;
  private int offset;
  private ElementScan method;

  /**
   * Create a new instance of <code>SourceScan</code>.
   */
  public SourceScan() {
  }

  public SourceScan(String text, boolean locked, int offset, ElementScan method) {
    this.text = text;
    this.locked = locked;
    this.offset = offset;
    this.method = method;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public boolean isLocked() {
    return this.locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public int getOffset() {
    return this.offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public ElementScan getMethod() {
    return this.method;
  }

  public void setMethod(ElementScan method) {
    this.method = method;
  }

}
