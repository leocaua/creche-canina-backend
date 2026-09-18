package br.com.creche.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Table(name = "matricula")
@Entity
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dataReserva;
    private Boolean status;
    private BigDecimal valor;
    private String diasSemana;
    private Integer frequencia;

    @ManyToMany
    @JoinTable
            (name = "matricula_pet",
            joinColumns = @JoinColumn(name = "matricula_id"),
                    inverseJoinColumns = @JoinColumn(name = "pet_id"))
    private List<Pet> pets;

    @ManyToOne
    @JoinColumn(name = "plano_id")
    private Plano plano;

    @OneToMany(mappedBy = "matricula")
    @JsonIgnoreProperties("matricula")
    private List<Rotina> rotinas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDate dataReserva) { this.dataReserva = dataReserva; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getDiasSemana() { return diasSemana; }
    public void setDiasSemana(String diasSemana) { this.diasSemana = diasSemana; }

    public Integer getFrequencia() { return frequencia; }
    public void setFrequencia(Integer frequencia) { this.frequencia = frequencia; }

    public List<Pet> getPets() { return pets; }
    public void setPets(List<Pet> pets) { this.pets = pets; }

    public Plano getPlano() { return plano; }
    public void setPlano(Plano plano) { this.plano = plano; }

    public List<Rotina> getRotinas() { return rotinas; }
    public void setRotinas(List<Rotina> rotinas) { this.rotinas = rotinas; }
}
