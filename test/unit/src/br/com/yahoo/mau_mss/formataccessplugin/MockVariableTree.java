package br.com.yahoo.mau_mss.formataccessplugin;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.VariableTree;

import javax.lang.model.element.Name;

/**
 * Title: MockVariableTree
 * Description:
 * Date: Feb 1, 2016, 7:37:48 PM
 *
 * @author Mauricio Soares da Silva (Mau)
 */
public class MockVariableTree implements VariableTree {
  private Name name;

  /**
   * Create a new instance of <code>MockVariableTree</code>.
   */
  public MockVariableTree() {
  }

  @Override
  public ModifiersTree getModifiers() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public void setName(Name name) {
    this.name = name;
  }

  @Override
  public Name getName() {
    return this.name;
  }

  @Override
  public ExpressionTree getNameExpression() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Tree getType() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public ExpressionTree getInitializer() {
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
}
