package br.com.yahoo.mau_mss.formataccessplugin;

import br.com.yahoo.mau_mss.formataccessplugin.api.ChangeAccesses;
import br.com.yahoo.mau_mss.formataccessplugin.api.DocumentAnalysis;
import br.com.yahoo.mau_mss.formataccessplugin.domain.ElementScan;
import br.com.yahoo.mau_mss.formataccessplugin.domain.FormatAccessPreferences;
import br.com.yahoo.mau_mss.formataccessplugin.domain.SourceScan;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePathScanner;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

/**
 * Title: FormatAccess
 * Description: Formata o código fonte para colocar a partícula this nos atributos e o nome da
 * classe
 * nos atributos e métodos estáticos
 * Date: Jul 08, 2015, 8:48:24 AM
 *
 * @author Mauricio Soares da Silva (mauricio.soares)
 */
@ActionID(
        category = "Source",
        id = "br.com.yahoo.mau_mss.formataccessplugin.FormatAccess"
)
@ActionRegistration(
    iconBase = "resources/checklist.png",
    displayName = "#CTL_FormatAccess"
)
@ActionReferences({
  @ActionReference(path = "Menu/Source", position = 2468),
  @ActionReference(path = "Loaders/text/x-java/Actions", position = 9990),
  @ActionReference(path = "Editors/text/x-java/Popup", position = 1590)
})
@Messages("CTL_FormatAccess=Format Accesses")
public class FormatAccess implements ActionListener, DocumentAnalysis {
  private final DataObject context;
  private List<ElementScan> elementScans;
  private StyledDocument styledDocument;
  private int countChanges;
  private static final Logger logger = Logger.getLogger(FormatAccess.class.getName());
  private static final Pattern patternClassInsideMethod;

  /**
   * Cria a expressão regex {@code (?s).*?(new)(\s+)(\w*)(\s*)(\().*?(\))(\s*)(\{) }
   * para identificar inner class dentro de métodos
   */
  static {
    StringBuilder sb = new StringBuilder();
    sb.append(".*?");	// Non-greedy match on filler
    sb.append("(new)");	// palavra new
    sb.append("(\\s+)");	// ao menos um espaço em branco
    sb.append("(\\w*)");	// Word 2
    sb.append("(\\s*)");	// espaço em branco ou não
    sb.append("(\\()");	// abre parênteses
    sb.append(".*?");	// Non-greedy match on filler
    sb.append("(\\))");	// fecha parênteses
    sb.append("(\\s*)");	// espaço em branco ou não
    sb.append("(\\{)");	// fecha chaves
    patternClassInsideMethod = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
  }

  public FormatAccess(DataObject context) {
    this.context = context;
  }

  /**
   * Método de entrada da aplicação disparado por um menu de contexto {@code source}
   *
   * @param ev o evento disparado
   */
  @Override
  public void actionPerformed(ActionEvent ev) {
    FormatAccessPreferences formatAccessPreferences = FormatAccessPreferences.load();
    if (formatAccessPreferences.hasNothingToDo()) {
      return;
    }
    long beginEvent = System.currentTimeMillis();
    FileObject fileObject = this.context.getPrimaryFile();
    List<FileObject> fileObjects = groupFileObjects(fileObject);
    for (FileObject fo : fileObjects) {
      FormatAccess.logger.log(Level.INFO, "Examinando o arquivo {0}", fo.getPath());
      processJavaSource(fo);
    }
    long endEvent = System.currentTimeMillis();
    long totalTime = endEvent - beginEvent;
    if (totalTime > 1000) {
      FormatAccess.logger.log(Level.WARNING, "A V I S O ! Detectado lentidão elevada no processo.");
    }
    FormatAccess.logger.log(Level.INFO, "Tempo total para executar o FormatAccess: {0}ms.", (endEvent - beginEvent));
  }

  /**
   * Agrupa a árvore de arquivos a serem analisados
   *
   * @param fileObject o objeto selecionado
   * @return uma lista de objetos
   */
  private List<FileObject> groupFileObjects(FileObject fileObject) {
    if (fileObject == null) {
      return Collections.<FileObject>emptyList();
    }
    String pathToConvert = fileObject.getPath();
    StatusDisplayer.getDefault().setStatusText("Path to convert: " + pathToConvert);
    if (FormatAccess.isEmpty(pathToConvert)) {
      FormatAccess.logger.log(Level.WARNING, "Nenhum arquivo foi selecionado");
      return Collections.<FileObject>emptyList();
    }
    List<FileObject> fileObjects = new ArrayList<>();
    if (fileObject.isFolder()) {
      NotifyDescriptor d
          = new NotifyDescriptor.Confirmation("Recursivelly format the selected files and folders?",
              "Format Recursively", NotifyDescriptor.OK_CANCEL_OPTION);
      if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.CANCEL_OPTION) {
        StatusDisplayer.getDefault().setStatusText("Operation cancelled");
        return Collections.<FileObject>emptyList();
      }
      searchJavaFiles(fileObjects, fileObject);
    } else {
      fileObjects.add(fileObject);
    }
    return fileObjects;
  }

  private void searchJavaFiles(List<FileObject> fileObjects, FileObject fileObject) {
    if (fileObject == null) {
      return;
    }
    if (fileObject.isFolder()) {
      for (FileObject fo : fileObject.getChildren()) {
        searchJavaFiles(fileObjects, fo);
      }
    }
    if (fileObject.getPath().endsWith(".java")) {
      fileObjects.add(fileObject);
    }
  }

  /**
   * Dispara o processamento em cada arquivo {@code .java}
   *
   * @param fileObject o objeto a ser analisado
   */
  @Override
  public void processJavaSource(FileObject fileObject) {
    JavaSource javaSource = JavaSource.forFileObject(fileObject);
    if (javaSource == null) {
      StatusDisplayer.getDefault().setStatusText("The file is not a Java source file!");
      return;
    }
    if (isFormGeneratedClass(fileObject.getPath())) {
      StatusDisplayer.getDefault().setStatusText("The file is a Netbeans guarded generated class!");
      return;
    }
    try {
      parseJavaDocument(javaSource, fileObject);
    } catch (IOException ex) {
      FormatAccess.logger.log(Level.SEVERE, "Erro ao examinar métodos.", ex);
      Exceptions.printStackTrace(ex);
      return;
    }
    logElements();
    try {
      javaSource.runUserActionTask(new SourceModifierTask(), true);
      if (this.countChanges == 0) {
        StatusDisplayer.getDefault().setStatusText("No element was changed");
      } else {
        StatusDisplayer.getDefault().setStatusText(String.format("Number elements changed: %d.", this.countChanges));
      }
    } catch (IOException e) {
      FormatAccess.logger.log(Level.SEVERE, "Erro ao modificar código fonte.", e);
      Exceptions.printStackTrace(e);
    }
  }

  /**
   * Avalia se o código foi gerado pelo Netbeans pela presença do arquivo de mesmo nome e extensão
   * .form
   *
   * @param filePath o caminho completo do arquivo sendo analisado
   * @return verdade se encontrar um form relacionado ao arquivo examinado
   */
  private boolean isFormGeneratedClass(String filePath) {
    if (filePath == null || !filePath.endsWith(".java")) {
      return false;
    }
    int javaPos = filePath.lastIndexOf(".java");
    String formPath = filePath.substring(0, javaPos) + ".form";
    File formFile = new File(formPath);
    return formFile.exists();
  }

  private void parseJavaDocument(JavaSource javaSource, FileObject fileObject) throws IOException {
    defineCurrentDocument(fileObject);
    javaSource.runUserActionTask(new FormaterTask(), true);
  }

  private void defineCurrentDocument(FileObject fileObject) throws IOException {
    EditorCookie editorCookie;
    try {
      DataObject dataObject = DataObject.find(fileObject);
      editorCookie = dataObject.getLookup().lookup(EditorCookie.class);
    } catch (DataObjectNotFoundException ex) {
      Exceptions.printStackTrace(ex);
      return;
    }
    this.styledDocument = editorCookie.openDocument();
  }

  private void logElements() {
    StringBuilder sb = new StringBuilder();
    sb.append("Elementos encontrados:");
    for (ElementScan es : this.elementScans) {
      sb.append("\n").append(es.toString());
    }
    FormatAccess.logger.log(Level.FINE, sb.toString());
  }

  /**
   * Tarefa responsável por buscar os elementos de código dentro de um documento
   */
  private class FormaterTask implements CancellableTask<CompilationController> {

    @Override
    public void cancel() {
    }

    /**
     * Dispara uma análise sintática em cada código fonte
     *
     * @param compilationController o controlador de compilação
     * @throws Exception caso não consiga analisar o código
     */
    @Override
    public void run(CompilationController compilationController) throws Exception {
      compilationController.toPhase(Phase.ELEMENTS_RESOLVED);
      Document document = compilationController.getDocument();
      if (document == null) {
        FormatAccess.logger.info("O documento não pode ser verificado!");
        return;
      }
      new MemberVisitor(compilationController).scan(compilationController.getCompilationUnit(), null);
      if (Thread.interrupted()) {
        FormatAccess.logger.log(Level.INFO, "A tarefa foi cancelada");
        showMessage("The format has been cancelled!", NotifyDescriptor.DEFAULT_OPTION);
      }
    }

    private void showMessage(String msg, int type) {
      NotifyDescriptor descriptor = new NotifyDescriptor.Message(msg, type);
      DialogDisplayer.getDefault().notify(descriptor);
    }
  }

  /**
   * Realiza um parse na classe para determinar seus atributos e métodos
   */
  private class MemberVisitor extends TreePathScanner<Void, Void> {
    private final CompilationInfo info;

    MemberVisitor(CompilationInfo info) {
      this.info = info;
    }

    /**
     * Visita a árvore de elmentos do código Java para determinar cada variável e método
     *
     * @param classTree a árvore da classe
     * @param v um argumento vazio do tipo {@code void}
     * @return {@code void}
     */
    @Override
    public Void visitClass(ClassTree classTree, Void v) {
      Element el = this.info.getTrees().getElement(getCurrentPath());
      if (el == null) {
        FormatAccess.logger.info("Não é possível resolver a classe!");
        return null;
      }
      TypeElement te = (TypeElement) el;
      @SuppressWarnings("unchecked")
      List<Element> enclosedElements = (List<Element>) te.getEnclosedElements();
      SourcePositions sourcePositions = this.info.getTrees().getSourcePositions();
      elementScans = new ArrayList<>();
      String className = el.getSimpleName().toString();
      scanElements(enclosedElements, sourcePositions, "", className);
      return null;
    }

    /**
     * Localiza elementos dentro do código como, innerClass, atributos e métodos
     *
     * @param enclosedElements lista da árvore de elementos internos da classe
     * @param sourcePositions posição offset do elemento analisado
     * @param parentClassName nome da classe avô do elemento
     * @param className nome da classe pai do elemento
     * @see https://blogs.oracle.com/geertjan/entry/org_netbeans_editor_sidebarfactory
     */
    @SuppressWarnings("unchecked")
    private void scanElements(List<Element> enclosedElements, SourcePositions sourcePositions,
        String parentClassName, String className) {
      int i = 0;
      for (Element enclosedElement : enclosedElements) {
        String name = enclosedElement.getSimpleName().toString();
        FormatAccess.logger.log(Level.FINEST, "Elemento {0} = {1}", new Object[]{i, name});
        i++;
        int start = (int) sourcePositions.getStartPosition(this.info.getCompilationUnit(),
            this.info.getTrees().getTree(enclosedElement));
        int end = (int) sourcePositions.getEndPosition(this.info.getCompilationUnit(),
            this.info.getTrees().getTree(enclosedElement));
        int startLine = NbDocument.findLineNumber(styledDocument, start) + 1;
        int endLine = NbDocument.findLineNumber(styledDocument, end) + 1;
        if (enclosedElement.getKind() == ElementKind.CLASS) {
          scanElements((List<Element>) enclosedElement.getEnclosedElements(), sourcePositions, className, name);
        }
        if (enclosedElement.getKind() == ElementKind.FIELD) {
          VariableTree variableTree = (VariableTree) this.info.getTrees().getTree(enclosedElement);
          if (variableTree == null) {
            continue;
          }
          elementScans.add(new ElementScan(name, className, parentClassName, "", enclosedElement.getKind(),
              isStatic(variableTree.getModifiers().getFlags()), startLine, endLine, null));
        }
        if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
          MethodTree methodTree = (MethodTree) this.info.getTrees().getTree(enclosedElement);
          if (methodTree == null) {
            continue;
          }
          elementScans.add(new ElementScan(name,
              className, parentClassName, "", enclosedElement.getKind(),
              isStatic(methodTree.getModifiers().getFlags()), startLine, endLine, methodTree));
        }
        if (enclosedElement.getKind() == ElementKind.METHOD) {
          MethodTree methodTree = (MethodTree) this.info.getTrees().getTree(enclosedElement);
          if (methodTree == null) {
            continue;
          }
          elementScans.add(new ElementScan(name, className, parentClassName,
              methodTree.getReturnType().toString(), enclosedElement.getKind(),
              isStatic(methodTree.getModifiers().getFlags()), startLine, endLine, methodTree));
        }
      }
    }

    private boolean isStatic(Set<Modifier> flags) {
      return flags.contains(Modifier.STATIC);
    }
  }

  /**
   * Tarefa responsável por modificar o código fonte em um documento
   */
  private class SourceModifierTask implements CancellableTask<CompilationController>, ChangeAccesses {
    private FormatAccessPreferences formatAccessPreferences;

    @Override
    public void cancel() {
    }

    /**
     * Dispara uma análise a nível textual do código fonte
     *
     * @param compilationController o controlador de compilação
     * @throws Exception caso não seja possível analisar o arquivo
     */
    @Override
    public void run(CompilationController compilationController) throws Exception {
      compilationController.toPhase(Phase.ELEMENTS_RESOLVED);
      Document document = compilationController.getDocument();
      if (document == null) {
        return;
      }
      List<SourceScan> sourceScans = loadSourceCode(document);
      if (sourceScans.isEmpty()) {
        return;
      }
      logSourceScan(sourceScans);
      this.formatAccessPreferences = FormatAccessPreferences.load();
      fixAccess(document, sourceScans);
    }

    private void logSourceScan(List<SourceScan> sourceScans) {
      StringBuilder sb = new StringBuilder();
      sb.append("Fonte analisada:");
      int i = 1;
      for (SourceScan sourceScan : sourceScans) {
        sb.append("\n").append(String.format("linha[%d]: offset [%d], tamanho [%d], locked [%s], texto [%s]", i,
            sourceScan.getOffset(), sourceScan.getText().length(), sourceScan.isLocked(), sourceScan.getText()));
        i++;
      }
      FormatAccess.logger.log(Level.FINE, sb.toString());
    }

    /**
     * Armazena cada linha lida em um objeto do tipo {@link SourceScan}
     *
     * @param document o documento analisado
     * @return uma lista de beans relativos ao código
     * @throws BadLocationException caso não encontre o arquivo
     */
    private List<SourceScan> loadSourceCode(Document document)
        throws BadLocationException {
      List<SourceScan> sourceScans = new ArrayList<>();
      javax.swing.text.Element rootElement = document.getDefaultRootElement();
      int numberOfLines = document.getDefaultRootElement().getElementCount();
      for (int i = 0; i < numberOfLines; i++) {
        javax.swing.text.Element lineElement = rootElement.getElement(i);
        int lineStartOffset = lineElement.getStartOffset();
        int lineEndOffset = lineElement.getEndOffset();
        String linha = document.getText(lineStartOffset, (lineEndOffset - lineStartOffset));
        int lineNumber = i + 1;
        sourceScans.add(new SourceScan(linha, isLocked(linha, lineNumber), lineStartOffset, findOwnMethodOrConstructor(lineNumber)));
      }
      updateLockBeforeMethodStart(sourceScans);
      updateLockCommentBlockInsidemethod(sourceScans);
      return sourceScans;
    }

    /**
     * Determina o nome do método que está sendo lido
     *
     * @param lineNumber o número da linha analisada
     * @return o elemento representante do método ou {@code null}
     */
    private ElementScan findOwnMethodOrConstructor(int lineNumber) {
      ElementScan methodOrConstructor = null;
      for (ElementScan elementScan : elementScans) {
        if (elementScan.isField()) {
          continue;
        }
        if (lineNumber >= elementScan.getStartLine() && lineNumber <= elementScan.getEndLine()) {
          methodOrConstructor = elementScan;
          break;
        }
      }
      return methodOrConstructor;
    }

    /**
     * Identifica se a linha não deve sofrer alteração (ex. linha comentada ou fora de um método ou
     * marcada com NOFORMATACCESS).
     *
     * @param line a linha a ser examinada
     * @param lineNumber o número da linha
     * @return {@code true} caso a linha deva ser restrita
     */
    private boolean isLocked(String line, int lineNumber) {
      if (elementScans.isEmpty()) {
        return true;
      }
      if (line != null && line.contains("NOFORMATACCESS")) {
        return true;
      }
      for (ElementScan elementScan : elementScans) {
        if (!elementScan.isConstructor() && !elementScan.isMethod()) {
          continue;
        }
        if (lineNumber > elementScan.getStartLine() && lineNumber < elementScan.getEndLine()) {
          return false;
        }
      }
      return true;
    }

    /**
     * Bloqueia todas as linhas antes do começo do corpo do método
     *
     * @param sourceScans o objeto com o código analisado
     */
    private void updateLockBeforeMethodStart(List<SourceScan> sourceScans) {
      if (elementScans.isEmpty() || sourceScans.isEmpty()) {
        return;
      }
      for (ElementScan elementScan : elementScans) {
        if (!elementScan.isConstructor() && !elementScan.isMethod()) {
          continue;
        }
        int methodStart = elementScan.getStartLine();
        while (methodStart < sourceScans.size() && methodStart < elementScan.getEndLine()) {
          SourceScan sourceScan = sourceScans.get(methodStart - 1);
          sourceScan.setLocked(true);
          if (sourceScan.getText().contains("{")) {
            break;
          }
          methodStart++;
        }
      }
    }

    /**
     * Bloqueia os comentários de bloco dentro do método
     *
     * @param sourceScans o objeto com o código analisado
     */
    private void updateLockCommentBlockInsidemethod(List<SourceScan> sourceScans) {
      if (elementScans.isEmpty() || sourceScans.isEmpty()) {
        return;
      }
      for (ElementScan elementScan : elementScans) {
        if (!elementScan.isConstructor() && !elementScan.isMethod()) {
          continue;
        }
        int methodStart = elementScan.getStartLine();
        boolean opennedComment = false;
        while (methodStart < sourceScans.size() && methodStart < elementScan.getEndLine()) {
          SourceScan sourceScan = sourceScans.get(methodStart - 1);
          String text = removeBlanks(sourceScan.getText());
          if (text.startsWith("//")) {
            sourceScan.setLocked(true);
          }
          int beginComment = text.lastIndexOf("/*");
          int endComment = text.lastIndexOf("*/");
          if (opennedComment && (endComment < 0 || endComment == text.length() - 2)) {
            sourceScan.setLocked(true);
          }
          if (beginComment == 0 && (endComment < 0 || endComment == text.length() - 2)) {
            sourceScan.setLocked(true);
          }
          if (beginComment >= 0 && endComment < 0) {
            opennedComment = true;
          }
          if (endComment >= 0) {
            opennedComment = false;
          }
          methodStart++;
        }
      }
    }

    /**
     * Processa a alteração do código fonte
     *
     * @param document o documento a ser alterado
     * @param sourceScans os beans relativos ao código fonte
     * @throws BadLocationException caso não encontre o elemento manipulado
     */
    private void fixAccess(Document document, List<SourceScan> sourceScans) throws BadLocationException {
      FormatAccess.logger.info("Alteração do texto em progresso...");
      if (this.formatAccessPreferences.isAddThisForNSFields() || this.formatAccessPreferences.isAddQualifierForSFields()) {
        scanMethodBodies();
      }
      for (int i = sourceScans.size() - 1; i > -1; i--) {
        SourceScan sourceScan = sourceScans.get(i);
        if (sourceScan.isLocked()) {
          continue;
        }
        String text = sourceScan.getText();
        if (text == null) {
          continue;
        }
        FormatAccess.logger.log(Level.FINE, "Analisando linha {0}", (i + 1));
        String textChanged = text;
        if (this.formatAccessPreferences.isAddThisForNSFields() || this.formatAccessPreferences.isAddQualifierForSFields()) {
          textChanged = addFieldPrefixes(text, sourceScan.getMethod());
        }
        if (this.formatAccessPreferences.isRemoveThisForNSMethods()) {
          textChanged = removeMethodPrefixes(textChanged);
        }
        if (this.formatAccessPreferences.isAddQualifierForSMethods()) {
          textChanged = addStaticMethodPrefixes(textChanged);
        }
        if (text.equals(textChanged)) {
          continue;
        }
        document.remove(sourceScan.getOffset(), sourceScan.getText().length());
        document.insertString(sourceScan.getOffset(), textChanged, null);
      }
      FormatAccess.logger.info("Alteração do texto terminada.");
    }

    private void scanMethodBodies() {
      List<String> fields = searchFieldNames();
      Pattern patternLocalVariablesWithNameField = mountPatternLocalVariablesWithNameField(fields);
      for (ElementScan elementScan : elementScans) {
        if (elementScan.isMethod()) {
          elementScan.setMethodBodyWithInnerClass(hasInnerClassInsideMethodBody(elementScan));
          verifyVariables(elementScan, patternLocalVariablesWithNameField, fields);
        }
      }
    }

    private List<String> searchFieldNames() {
      List<String> fields = new ArrayList<>();
      for (ElementScan elementScan : elementScans) {
        if (elementScan.isField()) {
          fields.add(elementScan.getName());
        }
      }
      return fields;
    }

    /**
     * Cria um pattern com a expressão regex @{code [A-Z_$][\w_$]*\s+NAME\s*[;|=] } para
     * buscar algo que comece com uma letra maiúscula, seguido de zero ou mais caracteres,
     * seguido de um ou mais brancos, seguido do nome procurado, seguido de zero ou mais brancos,
     * seguido de ; ou =
     *
     * @param fieldNames lista de campos no código fonte
     * @return {@code pattern} para buscar todos so campos que são variáveis locais em um texto
     */
    private Pattern mountPatternLocalVariablesWithNameField(List<String> fieldNames) {
      StringBuilder regex = new StringBuilder();
      for (String name : fieldNames) {
        if (regex.length() > 0) {
          regex.append("|");
        }
        regex.append("([A-Z_$][\\w_$]*\\s+").append(name).append("\\s*[;|=])");
      }
      if (regex.length() > 0) {
        FormatAccess.logger.info(String.format("Regex montado para variáveis locais [%s]", regex.toString()));
        return Pattern.compile(regex.toString());
      }
      return null;
    }

    /**
     * Monta uma expressão regular regex identificar a criação de uma classe inline.
     * Esta reconhece formatos como, {@code new Clazz() { }
     *
     * @param method elemento escaneado no {@link MemberVisitor#visitClass(com.sun.source.tree.ClassTree, java.lang.Void)
     * }
     * @return verdadeiro se encontrar uma classe inline no método
     */
    private boolean hasInnerClassInsideMethodBody(ElementScan method) {
      Matcher matcher = FormatAccess.patternClassInsideMethod.matcher(method.getMethodBody());
      return matcher.find();
    }

    /**
     * Verifica se existem variáveis internas ao método com o mesmo nome do campo da classe
     *
     * @param method o método analisado
     * @param patternLocalVariablesWithNameField o pattern relativo a busca das variáveis
     * @param fields a lista de campos da classe
     */
    private void verifyVariables(ElementScan method, Pattern patternLocalVariablesWithNameField, List<String> fields) {
      if (patternLocalVariablesWithNameField == null || fields.isEmpty()) {
        return;
      }
      // se o método já estiver comprometido não adianta checar pelos campos
      if (method.isMethodBodyWithInnerClass()) {
        method.getLocalVariablesWithFieldNames().addAll(fields);
        return;
      }
      Matcher matcher = patternLocalVariablesWithNameField.matcher(method.getMethodBody());
      if (matcher.find()) {
        FormatAccess.logger.finer(String.format("Encontrou o match [%s].", matcher.toString()));
        for (int i = 0; i < fields.size(); i++) {
          if (matcher.group(i + 1) != null && !matcher.group(i + 1).isEmpty()) {
            FormatAccess.logger.info(String.format("Grupo %d (do campo [%s]): %s", i + 1, fields.get(i), matcher.group(i + 1)));
            method.getLocalVariablesWithFieldNames().add(fields.get(i));
          }
        }
      }
    }

    /**
     * Inclui o prefixo nos campos da classe
     *
     * @param text o texto a ser alterado
     * @param method o método sendo manipulado
     * @return o texto alterado
     */
    @Override
    public String addFieldPrefixes(String text, ElementScan method) {
      String result = text;
      if (FormatAccess.isEmpty(text)) {
        return result;
      }
      for (ElementScan field : elementScans) {
        if (!field.isField()
            || (field.isStaticType() && !this.formatAccessPreferences.isAddQualifierForSFields())
            || (!field.isStaticType() && !this.formatAccessPreferences.isAddThisForNSFields())) {
          continue;
        }
        if ((method != null && method.isMethodBodyWithInnerClass())
            || (method != null && method.getLocalVariablesWithFieldNames().contains(field.getName()))
            || !text.contains(field.getName())
            || (method != null && method.getMethodParameterNames().contains(field.getName()))
            || isAutoAssign(field.getName(), text)
            || isNonStaticFieldInOtherClass(field, method)) {
          continue;
        }
        String prefix = getPrefix(field);
        int numberOfMatches = countMatches(result, field.getName());
        for (int i = 0; i < numberOfMatches; i++) {
          int pos = posFieldToChange(result, field.getName(), i);
          if (pos > -1) {
            countChanges++;
            result = appendPrefix(result, prefix, pos);
          }
        }
      }
      return result;
    }

    /**
     * Cria a expressão regex @{code NAME\s*=\s*(\w*\.)?NAME } para identificar um setter default
     * Pega patterns como:
     * this.att1 = att1;
     * att1 = this.att1;
     * Foo.att1 = att1;
     * att1 = Foo.att1;
     *
     * @param name o nome do campo
     * @param text o texto a ser alterado
     * @return {@code true} se encontrar um match para expressão regular procurada
     */
    private boolean isAutoAssign(String name, String text) {
      Pattern patternLocalVariablesWithNameField = Pattern.compile(name + "\\s*=\\s*(\\w*\\.)?" + name);
      Matcher matcher = patternLocalVariablesWithNameField.matcher(text);
      return matcher.find();
    }

    private boolean isNonStaticFieldInOtherClass(ElementScan elementScan, ElementScan methodOrConstructor) {
      if (elementScan.isStaticType()) {
        return false;
      }
      return methodOrConstructor != null
          && !elementScan.getClassName().equals(methodOrConstructor.getClassName());
    }

    private String getPrefix(ElementScan elementScan) {
      if (elementScan.isStaticType()) {
        return elementScan.getClassName() + ".";
      }
      if (elementScan.isField()) {
        return "this.";
      }
      return "";
    }

    /**
     * Cria a expressão regex @{code this\.([\w$_]+\s*\() } para remover o prefixo {@code this.} do
     * nome do método
     *
     * @param text o texto a ser alterado
     * @return o texto com métodos sem prefixo
     */
    @Override
    public String removeMethodPrefixes(String text) {
      String result = text;
      if (FormatAccess.isEmpty(text) || text.contains(".this")) {
        return result;
      }
      String pattern = "this\\.([\\w$_]+\\s*\\()";
      result = result.replaceAll(pattern, "$1");
      if (!text.equals(result)) {
        countChanges++;
      }
      return result;
    }

    /**
     * Adiciona o prefixo aos métodos estáticos
     *
     * @param text o texto a ser manipulado
     * @return o texto alterado
     */
    @Override
    public String addStaticMethodPrefixes(String text) {
      String result = text;
      if (FormatAccess.isEmpty(text)) {
        return result;
      }
      for (ElementScan elementScan : elementScans) {
        if (elementScan.isField() || !elementScan.isStaticType()) {
          continue;
        }
        String name = elementScan.getName();
        String prefix = elementScan.getClassName() + ".";
        int numberOfMatches = countMatches(result, name);
        for (int i = 0; i < numberOfMatches; i++) {
          int pos = posFieldToChange(result, name, i);
          if (pos > -1) {
            countChanges++;
            result = appendPrefix(result, prefix, pos);
          }
        }
      }
      return result;
    }

    private int countMatches(String line, String word) {
      return indexesOf(line, word).size();
    }

    private List<Integer> indexesOf(String line, String word) {
      List<Integer> indexes = new ArrayList<>();
      int index = line.indexOf(word);
      while (index >= 0) {
        indexes.add(index);
        index = line.indexOf(word, index + 1);
      }
      return indexes;
    }

    /**
     * Verifica qual a posição a ser alterada.
     * Esta não deve ser precedida de ponto, não deve estar entre caracteres válidos, não deve estar
     * entre aspas ou em uma linha comentada.
     *
     * @param line a linha de texto a ser examinada
     * @param name o nome do campo ou método a ser procurado
     * @param guess o número do palpite
     * @return a posição a ser alterada ou -1 se não encontrar uma posição válida
     */
    private int posFieldToChange(String line, String name, int guess) {
      List<Integer> indexes = indexesOf(line, name);
      if (indexes.isEmpty() || indexes.size() < guess) {
        return -1;
      }
      Integer indexMatch = indexes.get(guess);
      if (!isPrefixedByDot(line, indexMatch)
          && !isPrefixedByValidChar(line, indexMatch)
          && !isSuffixedByValidChar(line, indexMatch + name.length() - 1)
          && !isInQuotes(line, indexMatch)
          && !isInLineComment(line, indexMatch)) {
        return indexMatch;
      }
      return -1;
    }

    private boolean isPrefixedByDot(String word, int pos) {
      return pos > 0 && word.substring(pos - 1).startsWith(".");
    }

    private boolean isPrefixedByValidChar(String word, int pos) {
      return pos > 0 && isValidVariableChar(word.charAt(pos - 1));
    }

    private boolean isSuffixedByValidChar(String word, int pos) {
      return pos + 1 < word.length() && isValidVariableChar(word.charAt(pos + 1));
    }

    private boolean isValidVariableChar(char value) {
      return Character.isDigit(value) || Character.isLetter(value) || value == '_' || value == '$';
    }

    private boolean isInQuotes(String line, int pos) {
      int startQuotes = firstPositionBefore(line, '"', pos);
      if (startQuotes < 0) {
        return false;
      }
      int startPlus = firstPositionBefore(line, '+', pos);
      int startSemicolon = firstPositionBefore(line, ';', pos);
      boolean hasQuotesBeforeWord = (startQuotes >= 0);
      boolean hasPlusBetweenStartQuotesAndWord = hasQuotesBeforeWord && (startPlus >= 0) && startPlus > startQuotes;
      boolean hasPlusBetweenStartSemicolonAndWord = hasQuotesBeforeWord && (startSemicolon >= 0) && startSemicolon > startQuotes;
      return (hasQuotesBeforeWord) && (!hasPlusBetweenStartQuotesAndWord) && (!hasPlusBetweenStartSemicolonAndWord);
    }

    /**
     * Acha a posição mais próxima a esquerda contendo a letra procurada
     *
     * @param line texto a ser escaneado
     * @param letter letra a ser pesquisada
     * @param pos posição inicial para busca
     * @return posição da letra encontrada ou -1 caso não encontre nada
     */
    private int firstPositionBefore(String line, char letter, int pos) {
      if (line.isEmpty() || pos < 1 || line.indexOf(letter) < 0) {
        return -1;
      }
      for (int i = pos - 1; i > -1; i--) {
        if (line.charAt(i) == letter) {
          return i;
        }
      }
      return -1;
    }

    private boolean isInLineComment(String line, int pos) {
      int lineComm = line.lastIndexOf("//");
      int blockCommStart = line.lastIndexOf("/*");
      int blockCommEnd = line.lastIndexOf("*/");
      return (lineComm >= 0 && pos > lineComm) || (blockCommStart >= 0 && blockCommEnd >= 0 && pos > blockCommStart && pos < blockCommEnd);
    }

    private String appendPrefix(String text, String particle, int pos) {
      return text.substring(0, pos) + particle + text.substring(pos);
    }

    /**
     * Cria a expressão regular {@code \s } para subtrair espaços em branco
     *
     * @param text o texto a ser alterado
     * @return o texto sem espaços em branco
     */
    private String removeBlanks(String text) {
      if (text == null) {
        return "";
      }
      return text.replaceAll("\\s", "");
    }
  }

  private static boolean isEmpty(String text) {
    return text == null || text.isEmpty();
  }
}
