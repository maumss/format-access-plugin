package br.com.yahoo.mau_mss.formataccessplugin;

import br.com.yahoo.mau_mss.formataccessplugin.api.DocumentAnalysis;
import br.com.yahoo.mau_mss.formataccessplugin.domain.FormatAccessPreferences;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

import javax.swing.text.Document;

/**
 * Title: SaveAction
 * Description: Executa a rotina ao salvar um documento
 * Date: Oct 30, 2015, 6:43:01 PM
 *
 * @author Mauricio Soares da Silva (Mau)
 */
//@ServiceProvider(service = DocumentAnalysis.class)
public class SaveAction implements OnSaveTask, DocumentAnalysis {
  private Document document;
  private DataObject context;

  /**
   * Create a new instance of <code>SaveAction</code>.
   */
  public SaveAction() {
  }

  public SaveAction(Document document) {
    this.document = document;
  }

  @Override
  public void performTask() {
    if (this.document == null) {
      return;
    }
    FormatAccessPreferences formatAccessPreferences = FormatAccessPreferences.load();
    if (!formatAccessPreferences.isEnableOnSave() || formatAccessPreferences.hasNothingToDo()) {
      return;
    }
    this.context = NbEditorUtilities.getDataObject(this.document);
    if (this.context == null) {
      return;
    }
    FileObject fileObject = this.context.getPrimaryFile();
    if (isJavaSource(fileObject)) {
      processJavaSource(fileObject);
    }
  }

  private boolean isJavaSource(FileObject fileObject) {
    JavaSource javaSource = JavaSource.forFileObject(fileObject);
    return javaSource != null;
  }

  @Override
  public void processJavaSource(FileObject fileObject) {
    FormatAccess formatAccess = new FormatAccess(this.context);
    formatAccess.processJavaSource(fileObject);
  }

  @Override
  public void runLocked(Runnable r) {
    r.run();
  }

  @Override
  public boolean cancel() {
    return true;
  }

  @MimeRegistration(mimeType = "", service = OnSaveTask.Factory.class, position = 1500)
  public static class FactoryImpl implements Factory {

    @Override
    public OnSaveTask createTask(Context context) {
      return new SaveAction(context.getDocument());
    }
  }
}
