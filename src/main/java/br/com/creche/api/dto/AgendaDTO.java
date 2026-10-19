package br.com.creche.api.dto;

public record AgendaDTO(
        Long id,
        String petNome,
        String tutorNome,
        String foto,
        String porte,
        String tipo,
        String status
) {}
