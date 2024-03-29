package com.maquinadebusca.app.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "TermoDocumento")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class TermoDocumentoModel implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String texto;

    private Long n;

    @OneToMany(
            mappedBy = "termo", // Nome do atributo na classe IndiceInvertido. 
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<IndiceInvertidoModel> indiceInvertido;

    public TermoDocumentoModel() {
        indiceInvertido = new LinkedList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Long getN() {
        return n;
    }

    public void setN(Long n) {
        this.n = n;
    }

    public List<IndiceInvertidoModel> getIndiceInvertido() {
        return indiceInvertido;
    }

    public void setIndiceInvertido(List<IndiceInvertidoModel> indiceInvertido) {
        this.indiceInvertido = indiceInvertido;
    }

    public void inserirEntradaIndiceInvertido(DocumentoModel documento, int frequencia) {
        IndiceInvertidoModel entradaIndiceInvertido = new IndiceInvertidoModel(this, documento, frequencia); // Cria uma nova entrada para o índice invertido com o termo corrente, o documento informado como parâmetro e a frequencia do termo no documento.
        this.indiceInvertido.add(entradaIndiceInvertido); // Insere a nova entrada no índice invertido do termo corrente.
        documento.getIndiceInvertido().add(entradaIndiceInvertido); // Insere a nova entrada no índice invertido do documento que foi informado como parâmetro.
    }

    public void removeDocumento(DocumentoModel documento) {
        Iterator<IndiceInvertidoModel> iterator = this.indiceInvertido.iterator();
        while (iterator.hasNext()) {
            IndiceInvertidoModel entradaIndiceInvertido = iterator.next();
            if (entradaIndiceInvertido.getTermo().equals(this) && entradaIndiceInvertido.getDocumento().equals(documento)) {
                iterator.remove(); // Remoção no Banco de Dados a partir da tabela TermoDocumento.
                entradaIndiceInvertido.getDocumento().getIndiceInvertido().remove(entradaIndiceInvertido); // Remoção no Banco de Dados a partir da tabela Documento.
                entradaIndiceInvertido.setDocumento(null); // Remoção na memória RAM.
                entradaIndiceInvertido.setTermo(null); // Remoção na memória RAM.
            }
        }
    }

    public void setFrequencia(int frequencia, DocumentoModel documento) {
        Iterator<IndiceInvertidoModel> iterator = this.indiceInvertido.iterator();
        while (iterator.hasNext()) {
            IndiceInvertidoModel entradaIndiceInvertido = iterator.next();
            if (entradaIndiceInvertido.getTermo().equals(this) && entradaIndiceInvertido.getDocumento().equals(documento)) {
                entradaIndiceInvertido.setFrequencia(frequencia);
                break;
            }
        }
    }

    public void setFrequenciaNormalizada(DocumentoModel documento) {
        Iterator<IndiceInvertidoModel> iterator = this.indiceInvertido.iterator();
        while (iterator.hasNext()) {
            IndiceInvertidoModel entradaIndiceInvertido = iterator.next();
            if (entradaIndiceInvertido.getTermo().equals(this) && entradaIndiceInvertido.getDocumento().equals(documento)) {
                entradaIndiceInvertido.setFrequenciaNormalizada(entradaIndiceInvertido.getFrequencia() / documento.getFrequenciaMaxima());
                break;
            }
        }
    }

    public void setPeso(double peso, DocumentoModel documento) {
        Iterator<IndiceInvertidoModel> iterator = this.indiceInvertido.iterator();
        while (iterator.hasNext()) {
            IndiceInvertidoModel entradaIndiceInvertido = iterator.next();
            if (entradaIndiceInvertido.getTermo().equals(this) && entradaIndiceInvertido.getDocumento().equals(documento)) {
                entradaIndiceInvertido.setPeso(peso);
                break;
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.id);
        hash = 23 * hash + Objects.hashCode(this.texto);
        return hash;
    }

    @Override
    public boolean equals(Object obj
    ) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TermoDocumentoModel other = (TermoDocumentoModel) obj;
        if (!Objects.equals(this.texto, other.texto)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
}
