package br.com.yahoo.mau_mss.formataccessplugin.api;

import br.com.yahoo.mau_mss.formataccessplugin.domain.ElementScan;

/**
 * Title: ChangeAccesses
 * Description: Serviços executados após a análise do código
 * Date: Oct 31, 2015, 10:47:03 AM
 *
 * @author Mauricio Soares da Silva (Mau)
 */
public interface ChangeAccesses {

  String addFieldPrefixes(String text, ElementScan method);

  String addStaticMethodPrefixes(String text);

  String removeMethodPrefixes(String text);
}
