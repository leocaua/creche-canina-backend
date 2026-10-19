package br.com.creche.api.dto;

public record RotinaDetalheDTO(
        Long id,
        String horario,
        String descricao
) {}