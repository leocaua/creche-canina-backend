package br.com.creche.api.dto;

import java.util.List;

public record MatriculaAdminDTO(
        Long clienteId,
        List<String> nomesPets,
        String planoNome,
        List<String> dias,
        String statusPagamento
) {}