package br.com.creche.api.dto;

import java.util.List;

public record PetsRotinaHojeDTO(
        Long idMatricula,
        String nomesPets,
        String tutor,
        List<RotinaDetalheDTO> rotinas
) {}