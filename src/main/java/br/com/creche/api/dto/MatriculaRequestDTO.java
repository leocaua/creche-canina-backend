package br.com.creche.api.dto;

import java.util.List;

public class MatriculaRequestDTO {

    private Long planoId;
    private List<Long> petIds;
    private String diasSemana;
    private Integer frequencia;

    public Long getPlanoId() { return planoId; }
    public void setPlanoId(Long planoId) { this.planoId = planoId; }

    public List<Long> getPetIds() { return petIds; }
    public void setPetIds(List<Long> petIds) { this.petIds = petIds; }

    public String getDiasSemana() { return diasSemana; }
    public void setDiasSemana(String diasSemana) { this.diasSemana = diasSemana; }

    public Integer getFrequencia() { return frequencia; }
    public void setFrequencia(Integer frequencia) { this.frequencia = frequencia; }
}
