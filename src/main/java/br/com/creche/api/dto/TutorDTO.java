package br.com.creche.api.dto;

import java.util.List;

public record TutorDTO(
        Long id,
        String letraInicial,
        String nome,
        String email,
        String contato,
        String plano,
        List<String> pets,
        int agendamentos
) {}
