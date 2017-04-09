package br.com.yahoo.mau_mss.formataccessplugin.domain;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.ElementKind;

/**
 * Title: ElementScan
 * Description: Representa os atributos e métodos escaneados
 * Date: Jul 8, 2015, 12:49:44 PM
 * @author Mauricio Soares da Silva (mauricio.soares)
 */
public class ElementScan {
  private String name;
  private String className;
  private String classParentName;
  private String returnType;
  private ElementKind kind;
  private boolean staticType;
  private int startLine;
  private int endLine;
  private MethodTree methodTree;
  private boolean methodBodyWithInnerClass;
  private List<String> localVariablesWithFieldNames;

  /**
   * Create a new instance of <code>ElementScan</code>.
   */
  public ElementScan() {
    this.localVariablesWithFieldNames = new ArrayList<>();
  }

  public ElementScan(String name, String className, String classParentName, String returnType,
      ElementKind kind, boolean staticType, int startLine, int endLine, MethodTree methodTree) {
    this.name = name;
    this.className = className;
    this.classParentName = classParentName;
    this.returnType = returnType;
    this.kind = kind;
    this.staticType = staticType;
    this.startLine = startLine;
    this.endLine = endLine;
    this.methodTree = methodTree;
    this.localVariablesWithFieldNames = new ArrayList<>();
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getClassName() {
    return this.className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getClassParentName() {
    return this.classParentName;
  }

  public void setClassParentName(String classParentName) {
    this.classParentName = classParentName;
  }

  public String getReturnType() {
    return this.returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public ElementKind getKind() {
    return this.kind;
  }

  public void setKind(ElementKind kind) {
    this.kind = kind;
  }

  public boolean isStaticType() {
    return this.staticType;
  }

  public void setStaticType(boolean staticType) {
    this.staticType = staticType;
  }

  public int getStartLine() {
    return this.startLine;
  }

  public void setStartLine(int startLine) {
    this.startLine = startLine;
  }

  public int getEndLine() {
    return this.endLine;
  }

  public void setEndLine(int endLine) {
    this.endLine = endLine;
  }

  public MethodTree getMethodTree() {
    return this.methodTree;
  }

  public String getMethodBody() {
    if (this.methodTree == null || this.methodTree.getBody() == null) {
      return "";
    }
    return this.methodTree.getBody().toString();
  }

  public List<String> getMethodParameterNames() {
    if (this.methodTree == null) {
      return Collections.<String>emptyList();
    }
    List<? extends VariableTree> params = this.methodTree.getParameters();
    if (params == null) {
      return Collections.<String>emptyList();
    }
    List<String> parameterNames = new ArrayList<>();
    for (VariableTree vt : params) {
      parameterNames.add(vt.getName().toString());
    }
    return parameterNames;
  }

  public boolean isMethodBodyWithInnerClass() {
    return this.methodBodyWithInnerClass;
  }

  public void setMethodBodyWithInnerClass(boolean methodBodyWithInnerClass) {
    this.methodBodyWithInnerClass = methodBodyWithInnerClass;
  }

  public List<String> getLocalVariablesWithFieldNames() {
    return this.localVariablesWithFieldNames;
  }

  public void setLocalVariablesWithFieldNames(List<String> localVariablesWithFieldNames) {
    this.localVariablesWithFieldNames = localVariablesWithFieldNames;
  }

  public boolean isConstructor() {
    return getKind() == ElementKind.CONSTRUCTOR;
  }

  public boolean isMethod() {
    return getKind() == ElementKind.METHOD;
  }

  public boolean isField() {
    return getKind() == ElementKind.FIELD;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("linha[").append(getStartLine()).append("-").append(getEndLine()).append("]: ");
    if (this.staticType) {
      sb.append("static ");
    }
    if (this.classParentName != null && !this.classParentName.isEmpty()) {
      sb.append(this.classParentName).append(".");
    }
    sb.append(this.className).append(".").append(this.name);
    if (getKind() != ElementKind.FIELD) {
      sb.append("()");
    }
    return sb.toString();
  }
}
