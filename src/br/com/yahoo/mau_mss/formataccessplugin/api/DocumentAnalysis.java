package br.com.yahoo.mau_mss.formataccessplugin.api;

import org.openide.filesystems.FileObject;

/**
 * Title: DocumentAnalysis
 * Description: Examina um documento .java
 * Date: Oct 30, 2015, 6:54:41 PM
 *
 * @author Mauricio Soares da Silva (Mau)
 */
public interface DocumentAnalysis {

  void processJavaSource(FileObject fileObject);

}
