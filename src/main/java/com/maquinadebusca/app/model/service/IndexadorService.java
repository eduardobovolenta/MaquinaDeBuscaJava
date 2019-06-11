package com.maquinadebusca.app.model.service;

import com.maquinadebusca.app.model.DocumentoModel;
import com.maquinadebusca.app.model.TermoDocumentoModel;
import com.maquinadebusca.app.model.repository.DocumentoRepository;
import com.maquinadebusca.app.model.repository.TermoRepository;
import java.util.Hashtable;
import org.springframework.stereotype.Service;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndexadorService {

  private Hashtable hashTermos;

  @Autowired
  DocumentoRepository dr;

  @Autowired
  TermoRepository tr;

  public IndexadorService () {
    this.hashTermos = new Hashtable ();
  }

  @Transactional
  public boolean criarIndice () {
    List<DocumentoModel> documentos = this.getDocumentos ();
    for (DocumentoModel documento : documentos) {
      documento.setFrequenciaMaxima (0L);
      documento.setSomaQuadradosPesos (0L);
      documento = dr.save (documento);
      this.indexar(documento);
    }

    return true;
  }

  public void indexar (DocumentoModel documento) {
    int i;

    String visaoDocumento = documento.getVisao ();
    String[] termos = visaoDocumento.split (" ");
    for (String termo: termos) {
      if (!termo.equals ("")) {
        TermoDocumentoModel termoDocumento = this.getTermo (termo);
        int f = this.frequencia (termoDocumento.getTexto (), termos);
        if (f > documento.getFrequenciaMaxima ()) {
          documento.setFrequenciaMaxima (f);
        }
        termoDocumento.inserirEntradaIndiceInvertido (documento, f);
      }
    }
  }

  public TermoDocumentoModel getTermo (String texto) {
    TermoDocumentoModel termo;

    if (this.hashTermos.containsKey (texto)) {
      termo = (TermoDocumentoModel) this.hashTermos.get (texto);
    } else {
      termo = new TermoDocumentoModel ();
      termo.setTexto (texto);
      termo.setN (0L);
      termo = tr.save (termo);
      this.hashTermos.put (texto, termo);
    }

    return termo;
  }

  public int frequencia (String termo, String[] termos) {
    int i, contador = 0;

    for (i = 0; i < termos.length; i++) {
      if (!termos[i].equals ("")) {
        if (termos[i].equalsIgnoreCase (termo)) {
          contador++;
          termos[i] = "";
        }
      }
    }

    return contador;
  }

  public List<DocumentoModel> getDocumentos () {
    DocumentoModel documento;
    List<DocumentoModel> documentos = new LinkedList ();
    
    documentos = dr.findAll();

    return documentos;
  }

}
