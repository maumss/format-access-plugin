package br.com.yahoo.mau_mss.formataccessplugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.TreeVisitor;

import java.util.List;

/**
 * Title: MockBlockTree
 * Description:
 * Date: Feb 1, 2016, 7:33:01 PM
 *
 * @author Mauricio Soares da Silva (Mau)
 */
public class MockBlockTree implements BlockTree {
  private String body;

  /**
   * Create a new instance of <code>MockBlockTree</code>.
   */
  public MockBlockTree() {
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public boolean isStatic() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<? extends StatementTree> getStatements() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Kind getKind() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public <R, D> R accept(TreeVisitor<R, D> tv, D d) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String toString() {
    return this.getBody();
  }
}
