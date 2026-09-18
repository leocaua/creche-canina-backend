package br.com.creche.api.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Rotina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private LocalDateTime horario;

    @ManyToOne
    @JoinColumn(name = "agendamento_id")
    private Matricula matricula;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getHorario() {
        return horario;
    }
    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public Matricula getAgendamento() {
        return matricula;
    }
    public void setAgendamento(Matricula matricula) {
        this.matricula = matricula;
    }
}
