package br.com.creche.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String raca;
    private String cor;
    private String sexo;
    private Integer idade;
    private String porte;
    private Double peso;

    private Boolean vacinado;
    private Boolean castrado;

    @Column(columnDefinition = "TEXT")
    private String foto;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Cliente cliente;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRaca() {
        return raca;
    }
    public void setRaca(String raca) {
        this.raca = raca;
    }

    public Integer getIdade() {
        return idade;
    }
    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public Boolean getVacinado() {
        return vacinado;
    }
    public void setVacinado(Boolean vacinado) {
        this.vacinado = vacinado;
    }

    public Boolean getCastrado() {
        return castrado;
    }
    public void setCastrado(Boolean castrado) {
        this.castrado = castrado;
    }

    public String getTutorNome() {
        return cliente != null ? cliente.getNome() : null;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getFoto() {
        return foto;
    }
    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCor() {
        return cor;
    }
    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getSexo() {
        return sexo;
    }
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPorte() {
        return porte;
    }
    public void setPorte(String porte) {
        this.porte = porte;
    }

    public Double getPeso() {
        return peso;
    }
    public void setPeso(Double peso) {
        this.peso = peso;
    }
}
