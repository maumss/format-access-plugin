package br.com.yahoo.mau_mss.formataccessplugin;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;

import java.util.List;

import javax.lang.model.element.Name;

/**
 * Title: MockMethodTree
 * Description:
 * Date: Feb 1, 2016, 7:30:50 PM
 *
 * @author Mauricio Soares da Silva (Mau)
 */
public class MockMethodTree implements MethodTree {
  private BlockTree body;
  private List<? extends VariableTree> parameters;

  /**
   * Create a new instance of <code>MockMethodTree</code>.
   */
  public MockMethodTree() {
  }

  @Override
  public ModifiersTree getModifiers() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Name getName() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Tree getReturnType() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<? extends TypeParameterTree> getTypeParameters() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public void setParameters(List<? extends VariableTree> parameters) {
    this.parameters = parameters;
  }

  @Override
  public List<? extends VariableTree> getParameters() {
    return this.parameters;
  }

  @Override
  public VariableTree getReceiverParameter() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<? extends ExpressionTree> getThrows() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public void setBody(BlockTree body) {
    this.body = body;
  }

  @Override
  public BlockTree getBody() {
    return this.body;
  }

  @Override
  public Tree getDefaultValue() {
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
