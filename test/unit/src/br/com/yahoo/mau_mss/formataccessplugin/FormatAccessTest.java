package br.com.yahoo.mau_mss.formataccessplugin;


import br.com.yahoo.mau_mss.formataccessplugin.domain.ElementScan;
import br.com.yahoo.mau_mss.formataccessplugin.domain.FormatAccessPreferences;
import com.sun.source.tree.MethodTree;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.ElementKind;

/**
 * <p>Title: FormatAccessTest</p>
 * <p>Description:  </p>
 * <p>Date: Oct 18, 2015, 12:25:15 PM</p>
 * @author Mauricio Soares da Silva (Mau)
 */
public class FormatAccessTest {

    public FormatAccessTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

  /**
   * Testa a condição do método
   * {@link FormatAccess.SourceModifierTask#isInQuotes(String, int)} e
   * {@link FormatAccess.SourceModifierTask#isPrefixedByDot(String, int)}}
   */
  @Test
  public void testAddFieldPrefixes() {
    System.out.println("\naddFieldPrefixes - testar entre aspas e precedido de ponto");
    String expResult = "this.atributo1 = \"atributo1\"";
    String result = callAddFieldPrefixes("atributo1 = \"atributo1\"");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  /**
   * Testa a condição do método
   * {@link FormatAccess.SourceModifierTask#isAutoAssign(String, String)}
   */
  @Test
  public void testAddFieldPrefixes2() {
    System.out.println("\naddFieldPrefixes 2 - testar construtor padrão");
    String expResult = "this.atributo1 = atributo1";
    String result = callAddFieldPrefixes("this.atributo1 = atributo1");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  /**
   * Testa a condição do método
   * {@link FormatAccess.SourceModifierTask#isSuffixedByValidChar(String, int)}
   */
  @Test
  public void testAddFieldPrefixes3() {
    System.out.println("\naddFieldPrefixes 3 - testar sucedido por nome");
    String expResult = "atributo1_ab = \"nada\";";
    String result = callAddFieldPrefixes("atributo1_ab = \"nada\";");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  /**
   * Testa a condição do método
   * {@link FormatAccess.SourceModifierTask#isPrefixedByValidChar(String, int)}
   */
  @Test
  public void testAddFieldPrefixes4() {
    System.out.println("\naddFieldPrefixes 4 - testar precedido por nome");
    String expResult = "_atributo1 = \"nada\";";
    String result = callAddFieldPrefixes("_atributo1 = \"nada\";");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  /**
   * Testa a condição do método
   * {@link FormatAccess.SourceModifierTask#isNonStaticFieldInOtherClass(ElementScan, ElementScan)}
   */
  @Test
  public void testAddFieldPrefixes5() {
    System.out.println("\naddFieldPrefixes 5 - testar em inner class");
    String expResult = "atributo1 = \"nada\";";
    String result = callAddFieldPrefixes("atributo1 = \"nada\";", "Bar", null);
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  /**
   * Testa a condição do método
   * {@link FormatAccess.SourceModifierTask#hasInnerClassInsideMethodBody(ElementScan)}
   */
  @Test
  public void testAddFieldPrefixes6() {
    System.out.println("\naddFieldPrefixes 6 - testar com inner class dentro do método");
    String expResult = "atributo1 = \"nada\";";
    String result = callAddFieldPrefixes("atributo1 = \"nada\";", "Foo",
        createMethod("Runnable runnable = new Runnable() {\n  @Override\npublic void run() {  \natributo1 = \"nada\";\n  }\n  \n}"));
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  /**
   * Testa a condição do método
   * {@link FormatAccess.SourceModifierTask#isInLineComment(String, int)}
   */
  @Test
  public void testAddFieldPrefixes7() {
    System.out.println("\naddFieldPrefixes 7 - testar com comentário na linha");
    String expResult = "String str = \"nada\"; /* atributo1 */";
    String result = callAddFieldPrefixes("String str = \"nada\"; /* atributo1 */");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  /**
   * Testa a condição do método
   * {@link FormatAccess.SourceModifierTask#isInQuotes(String, int)}
   */
  @Test
  public void testAddFieldPrefixes8() {
    System.out.println("\naddFieldPrefixes 8 - testar com concatenação de strings");
    String expResult = "String str = \"resultado [\" + this.atributo1 + \"].\";";
    String result = callAddFieldPrefixes("String str = \"resultado [\" + atributo1 + \"].\";");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  @Test
  public void testAddFieldPrefixes9() {
    System.out.println("\naddFieldPrefixes 9 -  testar dois comandos na mesma linha");
    String expResult = "String str = \"nada\"; this.atributo1 = \"nada\";";
    String result = callAddFieldPrefixes("String str = \"nada\"; atributo1 = \"nada\";");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  /**
   * Testa a condição do método
   * {@link FormatAccess.SourceModifierTask#verifyVariables(ElementScan, Pattern, List<String>)}
   */
  @Test
  public void testAddFieldPrefixes10() {
    System.out.println("\naddFieldPrefixes 10 - testar variável local homônima de atributo");
    String expResult = "atributo1 = \"nada\";";
    String result = callAddFieldPrefixes("atributo1 = \"nada\";", "Foo",
        createMethod("Bar atributo1 = new Bar();\n  atributo1 = \"nada\";"));
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  @Test
  public void testAddFieldPrefixes11() {
    System.out.println("\naddFieldPrefixes 11 - testar retorno do atributo");
    String expResult = "return this.atributo1;";
    String result = callAddFieldPrefixes("return atributo1;", "Foo", createMethod("return atributo1;"));
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  @Test
  public void testAddFieldPrefixes12() {
    System.out.println("\naddFieldPrefixes 12 - atribuição de um atributo estático");
    String expResult = "Foo.atributo2 = atributo2";
    String result = callAddFieldPrefixes("Foo.atributo2 = atributo2");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  @Test
  public void testAddFieldPrefixes13() {
    System.out.println("\naddFieldPrefixes 13 - testar como parâmetro de uma nova classe");
    String expResult = "loader.setControllerFactory(new SpringJavaFXCallback(this.atributo1));";
    String result = callAddFieldPrefixes("loader.setControllerFactory(new SpringJavaFXCallback(atributo1));");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  /**
   * Testa a condição do método
   * {@link FormatAccess.SourceModifierTask#isAutoAssign(String, String)}
   */
  @Test
  public void testAddFieldPrefixes14() {
    System.out.println("\naddFieldPrefixes 14 - testar parâmetro igual ao atributo");
    String expResult = "if (atributo1 != null) {\n";
    String result = callAddFieldPrefixes("if (atributo1 != null) {\n", "Foo",
        createMethod(Arrays.asList("atributo1"), "if (atributo1 != null) {\n  return = \"nada\";\n}"));
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  private MethodTree createMethod(String body) {
    return createMethod(null, body);
  }

  private MethodTree createMethod(List<String> parameters, String body) {
    MockBlockTree blockTree = new MockBlockTree();
    blockTree.setBody(body);
    MockMethodTree methodTree = new MockMethodTree();
    methodTree.setBody(blockTree);
    if (parameters != null && !parameters.isEmpty()) {
      List<MockVariableTree> paramTree = new ArrayList<>();
      for (String param : parameters) {
        MockName name = new MockName();
        name.setName(param);
        MockVariableTree variableTree = new MockVariableTree();
        variableTree.setName(name);
        paramTree.add(variableTree);
      }
      methodTree.setParameters(paramTree);
    }
    return methodTree;
  }

  /**
   * Cria uma estrutura com dois atributos e um método da Classe Foo e examina o texto que estaria
   * teoricamente dentro do método
   *
   * @param text texto a ser examinado
   * @return texto modificado
   */
  private String callAddFieldPrefixes(String text) {
    return callAddFieldPrefixes(text, "Foo", null);
  }

  /**
   * Cria uma estrutura com dois atributos da classe X e um método da classe Foo e examina o texto
   * que estaria teoricamente dentro do método
   *
   * @param text texto a ser examinado
   * @param className classe dos atributos
   * @param methodBody corpo do método a ser examinado
   * @return texto modificado
   */
  private String callAddFieldPrefixes(String text, String className, MethodTree methodBody) {
    String result = "";
    Class<?>[] classlist = FormatAccess.class.getDeclaredClasses();
    FormatAccess instance = new FormatAccess(null);
    try {
      for (Class<?> clazz : classlist) {
        if (!clazz.getSimpleName().equals("SourceModifierTask")) {
          continue;
        }
        Constructor<?> constr = clazz.getDeclaredConstructor(FormatAccess.class);
        constr.setAccessible(true);
        Object inner = constr.newInstance(instance);
        // cria a lista de elementos
        List<ElementScan> elementScans = new ArrayList<>();
        // cria um field na linha 1
        elementScans.add(new ElementScan("atributo1", className, "", "", ElementKind.FIELD, false, 1, 1, null));
        // cria um field static na linha 2
        elementScans.add(new ElementScan("atributo2", className, "", "", ElementKind.FIELD, true, 2, 2, null));
        // cria um method da linha 10 a linha 15
        elementScans.add(new ElementScan("metodo1", "Foo", "", "void", ElementKind.METHOD, false, 10, 15, methodBody));
        Field field = FormatAccess.class.getDeclaredField("elementScans");
        field.setAccessible(true);
        field.set(instance, elementScans);
        // cria as preferências de execução
        FormatAccessPreferences formatAccessPreferences = new FormatAccessPreferences(false, true, true, true, true);
        Field field2 = clazz.getDeclaredField("formatAccessPreferences");
        field2.setAccessible(true);
        field2.set(inner, formatAccessPreferences);
        // chama o método {@link FormatAccess#scanMethodBodies()}
        Method meth1 = clazz.getDeclaredMethod("scanMethodBodies");
        meth1.setAccessible(true);
        meth1.invoke(inner);
        // chama o método {@link FormatAccess#addFieldPrefixes(String, ElementScan)}
        Method meth2 = clazz.getDeclaredMethod("addFieldPrefixes", String.class, ElementScan.class); // recebia int.class anteriormente
        meth2.setAccessible(true);
        result = (String) meth2.invoke(inner, text, elementScans.get(2));
      }
    } catch (NoSuchMethodException | SecurityException | InstantiationException |
        IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  @Test
  public void testAddStaticMethodPrefixes() {
    System.out.println("\naddStaticMethodPrefixes");
    String expResult = "Foo.metodo1();";
    String result = callAddMethodPrevixes("metodo1();", "addStaticMethodPrefixes");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  @Test
  public void testRemoveMethodPrefixes() {
    System.out.println("\nremoveMethodPrefixes - com vários espaços em branco antes do parênteses");
    String expResult = "metodo2  ();";
    String result = callAddMethodPrevixes("metodo2  ();", "removeMethodPrefixes");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  @Test
  public void testRemoveMethodPrefixes2() {
    System.out.println("\nremoveMethodPrefixes2 - sem espaços em branco antes do parênteses");
    String expResult = "metodo2();";
    String result = callAddMethodPrevixes("metodo2();", "removeMethodPrefixes");
    System.out.println("ExpResult: " + expResult);
    System.out.println("Result: " + result);
    Assert.assertEquals(expResult, result);
  }

  private String callAddMethodPrevixes(String text, String methodName) {
    String result = "";
    Class<?>[] classlist = FormatAccess.class.getDeclaredClasses();
    FormatAccess instance = new FormatAccess(null);
    try {
      for (Class<?> clazz : classlist) {
        if (!clazz.getSimpleName().equals("SourceModifierTask")) {
          continue;
        }
        Constructor<?> constr = clazz.getDeclaredConstructor(FormatAccess.class);
        constr.setAccessible(true);
        Object inner = constr.newInstance(instance);
        // cria a lista de elementos
        List<ElementScan> elementScans = new ArrayList<>();
        elementScans.add(new ElementScan("metodo1", "Foo", "", "void", ElementKind.METHOD, true, 1, 1, null));
        elementScans.add(new ElementScan("metodo2", "Foo", "", "void", ElementKind.METHOD, false, 2, 2, null));
        Field field = FormatAccess.class.getDeclaredField("elementScans");
        field.setAccessible(true);
        field.set(instance, elementScans);
        // chama o método {@link FormatAccess#methodName(String)}
        Method meth = clazz.getDeclaredMethod(methodName, String.class);
        meth.setAccessible(true);
        result = (String) meth.invoke(inner, text);
      }
    } catch (NoSuchMethodException | SecurityException | InstantiationException |
        IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

}