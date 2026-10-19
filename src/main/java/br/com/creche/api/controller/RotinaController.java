package br.com.creche.api.controller;

import br.com.creche.api.dto.PetsRotinaHojeDTO;
import br.com.creche.api.dto.RotinaDetalheDTO;
import br.com.creche.api.entity.Cliente;
import br.com.creche.api.entity.Matricula;
import br.com.creche.api.entity.Notificacao;
import br.com.creche.api.entity.Pet;
import br.com.creche.api.entity.Rotina;
import br.com.creche.api.repository.MatriculaRepository;
import br.com.creche.api.repository.NotificacaoRepository;
import br.com.creche.api.repository.PetRepository;
import br.com.creche.api.service.RotinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/rotinas")
public class RotinaController {

    @Autowired
    private RotinaService rotinaService;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private PetRepository petRepository;

    @PostMapping
    public ResponseEntity<Rotina> registrarRotina(@RequestBody Rotina rotina){
        Matricula matriculaBanco = null;
        Pet petBanco = null;
        String nomePetRecebido = (rotina.getPet() != null) ? rotina.getPet().getNome() : null;

        if (rotina.getMatricula() != null && rotina.getMatricula().getId() != null) {
            matriculaBanco = matriculaRepository.findById(rotina.getMatricula().getId()).orElse(null);
            rotina.setMatricula(matriculaBanco);
        }

        if (rotina.getPet() != null && rotina.getPet().getId() != null) {
            petBanco = petRepository.findById(rotina.getPet().getId()).orElse(null);
        }

        if (petBanco == null && nomePetRecebido != null && !nomePetRecebido.trim().isEmpty()) {
            if (matriculaBanco != null && matriculaBanco.getPets() != null) {
                petBanco = matriculaBanco.getPets().stream()
                        .filter(p -> p.getNome() != null && p.getNome().equalsIgnoreCase(nomePetRecebido.trim()))
                        .findFirst()
                        .orElse(null);
            }
            if (petBanco == null) {
                petBanco = petRepository.findAll().stream()
                        .filter(p -> p.getNome() != null && p.getNome().equalsIgnoreCase(nomePetRecebido.trim()))
                        .findFirst()
                        .orElse(null);
            }
        }

        rotina.setPet(petBanco);

        Rotina rotinaSalvo = rotinaService.registrarRotina(rotina);

        if (matriculaBanco != null) {
            String nomeDoPet;
            if (petBanco != null && petBanco.getNome() != null) {
                nomeDoPet = petBanco.getNome();
            } else if (nomePetRecebido != null && !nomePetRecebido.trim().isEmpty()) {
                nomeDoPet = nomePetRecebido.trim();
            } else if (!matriculaBanco.getPets().isEmpty()) {
                nomeDoPet = matriculaBanco.getPets().get(0).getNome();
            } else {
                nomeDoPet = "seu pet";
            }

            String descricaoCompleta = rotinaSalvo.getDescricao() != null ? rotinaSalvo.getDescricao() : "";
            String tituloAtividade = "Nova atividade";

            if (descricaoCompleta.contains("|||")) {
                tituloAtividade = descricaoCompleta.split("\\|\\|\\|")[0];
            } else if (descricaoCompleta.contains(" - ")) {
                tituloAtividade = descricaoCompleta.split(" - ")[0];
            }

            Cliente donoDoPet = matriculaBanco.getCliente();

            if (donoDoPet == null && petBanco != null) {
                donoDoPet = petBanco.getCliente();
            }
            if (donoDoPet == null && !matriculaBanco.getPets().isEmpty()) {
                donoDoPet = matriculaBanco.getPets().get(0).getCliente();
            }

            if (donoDoPet != null) {
                Notificacao aviso = new Notificacao();
                aviso.setTipo("rotina");
                aviso.setTexto("Nova atividade registrada para " + nomeDoPet + ": " + tituloAtividade);
                aviso.setCliente(donoDoPet);

                notificacaoRepository.save(aviso);
                System.out.println("SUCESSO: Notificação gerada para o tutor!");
            } else {
                System.out.println("ERRO CRÍTICO: Nem a matrícula nem o cão têm um tutor associado na base de dados!");
            }
        }

        return ResponseEntity.status(201).body(rotinaSalvo);
    }

    @GetMapping("/painel-hoje")
    public ResponseEntity<List<PetsRotinaHojeDTO>> listarPetsParaRotina() {
        List<Matricula> matriculasAtivas = matriculaRepository.findAll().stream()
                .filter(m -> m.getStatus() != null && m.getStatus())
                .toList();

        DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDate hoje = LocalDate.now();

        List<PetsRotinaHojeDTO> resposta = matriculasAtivas.stream().map(matricula -> {
            String nomeTutor = matricula.getCliente() != null ? matricula.getCliente().getNome() : "Sem Tutor";
            String nomesPets = matricula.getPets() != null ? matricula.getPets().stream().map(p -> p.getNome()).collect(Collectors.joining(", ")) : "Sem Pets";

            List<RotinaDetalheDTO> rotinasHoje = matricula.getRotinas() != null ?
                    matricula.getRotinas().stream()
                            .filter(r -> r.getHorario() != null && r.getHorario().toLocalDate().equals(hoje))
                            .map(r -> new RotinaDetalheDTO(r.getId(), r.getHorario().format(horaFormatter), r.getDescricao()))
                            .toList()
                    : List.of();

            return new PetsRotinaHojeDTO(matricula.getId(), nomesPets, nomeTutor, rotinasHoje);

        }).toList();

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<RotinaDetalheDTO>> buscarRotinasDoPetNaData( @PathVariable Long petId, @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<Matricula> matriculas = matriculaRepository.findAll().stream()
                .filter(m -> m.getStatus() != null && m.getStatus())
                .filter(m -> m.getPets() != null && m.getPets().stream().anyMatch(p -> p.getId().equals(petId)))
                .toList();

        List<RotinaDetalheDTO> rotinasDoDia = matriculas.stream()
                .filter(m -> m.getRotinas() != null)
                .flatMap(m -> m.getRotinas().stream()
                        .filter(r -> r.getHorario() != null && r.getHorario().toLocalDate().equals(data))
                        .filter(r -> r.getPet() != null
                                ? r.getPet().getId().equals(petId)
                                : (m.getPets() != null && m.getPets().size() == 1))
                )
                .map(r -> new RotinaDetalheDTO(
                        r.getId(),
                        r.getHorario().format(horaFormatter),
                        r.getDescricao()
                ))
                .toList();

        return ResponseEntity.ok(rotinasDoDia);
    }
}