package br.com.creche.api.controller;

import br.com.creche.api.dto.AgendaDTO;
import br.com.creche.api.entity.Cliente;
import br.com.creche.api.entity.Matricula;
import br.com.creche.api.entity.Notificacao;
import br.com.creche.api.entity.Pet;
import br.com.creche.api.repository.MatriculaRepository;
import br.com.creche.api.repository.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/agenda")
public class AgendaController {

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusDiario(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        System.out.println("---> RECEBIDO PEDIDO DE STATUS <---");
        System.out.println("ID da Matricula: " + id);
        System.out.println("Payload recebido: " + payload);

        String acao = payload.get("acao");

        Matricula matricula = matriculaRepository.findById(id).orElse(null);
        if (matricula == null) {
            System.out.println("ERRO: Matricula não encontrada!");

            return ResponseEntity.notFound().build();
        }

        String nomeDoPet = matricula.getPets().isEmpty()
                ? "Pet"
                : matricula.getPets().stream().map(Pet::getNome).collect(Collectors.joining(", "));
        boolean maisDeUmPet = matricula.getPets().size() > 1;
        Cliente donoDoPet = matricula.getPets().isEmpty() ? matricula.getCliente() : matricula.getPets().get(0).getCliente();

        if ("CHECKIN".equalsIgnoreCase(acao)) {
            matricula.setStatusDiario("NA_CRECHE");
            matricula.setDataStatusDiario(LocalDate.now());
            matriculaRepository.save(matricula);

            Notificacao aviso = new Notificacao();
            aviso.setTipo("CHECKIN");
            String verboEntrada = maisDeUmPet ? " deram entrada na creche (Check-in realizado)." : " deu entrada na creche (Check-in realizado).";
            aviso.setTexto(nomeDoPet + verboEntrada);
            aviso.setCliente(donoDoPet);

            notificacaoRepository.save(aviso);

            System.out.println("SUCESSO: Status alterado para NA_CRECHE com a data " + LocalDate.now());

        } else if ("CHECKOUT".equalsIgnoreCase(acao)) {
            matricula.setStatusDiario("FINALIZADO");
            matricula.setDataStatusDiario(LocalDate.now());
            matriculaRepository.save(matricula);

            Notificacao aviso = new Notificacao();
            aviso.setTipo("AUSENCIA");
            String verboSaida = maisDeUmPet ? " saíram da creche (Check-out realizado)." : " saiu da creche (Check-out realizado).";
            aviso.setTexto(nomeDoPet + verboSaida);
            aviso.setCliente(donoDoPet);

            notificacaoRepository.save(aviso);

            System.out.println("SUCESSO: Status alterado para FINALIZADO");

        } else if ("AUSENCIA".equalsIgnoreCase(acao)) {
            String dataString = payload.get("data");
            LocalDate dataAusencia = (dataString != null) ? LocalDate.parse(dataString) : LocalDate.now();

            matricula.setStatusDiario("AUSENTE");
            matricula.setDataStatusDiario(dataAusencia);
            matriculaRepository.save(matricula);

            Notificacao aviso = new Notificacao();
            aviso.setTipo("AUSENCIA");
            aviso.setTexto("Ausência programada para " + nomeDoPet + " no dia " + dataAusencia + ". Vaga liberada!");
            aviso.setCliente(donoDoPet);
            notificacaoRepository.save(aviso);

            System.out.println("SUCESSO: Vaga liberada (AUSENTE) para a data " + dataAusencia);

        } else {
            System.out.println("AVISO: A ação recebida não é válida: " + acao);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AgendaDTO>> buscarAgendaDoDia(@RequestParam("data") String data) {
        LocalDate dataBusca = LocalDate.parse(data);
        String siglaDia = obterSiglaDiaSemana(dataBusca.getDayOfWeek());

        List<Matricula> todasMatriculas = matriculaRepository.findAll();
        List<AgendaDTO> agendaDoDia = new ArrayList<>();

        for (Matricula m : todasMatriculas) {
            int mesesDuracao = 1;

            if (m.getPlano() != null && m.getPlano().getNome() != null) {
                String nomePlano = m.getPlano().getNome().toUpperCase();

                if (nomePlano.contains("TRIMESTRAL") || nomePlano.contains("3")) {
                    mesesDuracao = 3;
                } else if (nomePlano.contains("SEMESTRAL") || nomePlano.contains("6")) {
                    mesesDuracao = 6;
                }
            }
            LocalDate dataExpiracao = null;
            if (m.getDataReserva() != null) {
                dataExpiracao = m.getDataReserva().plusMonths(mesesDuracao);
            }

            boolean isPendente = (m.getStatus() == null || !m.getStatus());
            boolean isHoje = dataBusca.equals(LocalDate.now());

            boolean temDiasFixos = m.getDiasSemana() != null && !m.getDiasSemana().trim().isEmpty();
            String diasNormalizados = temDiasFixos ? m.getDiasSemana().toUpperCase().replace("Á", "A") : "";

            boolean diaSemanaBate = temDiasFixos
                    && diasNormalizados.contains(siglaDia)
                    && (m.getDataReserva() == null || dataBusca.isAfter(m.getDataReserva()))
                    && (dataExpiracao == null || !dataBusca.isAfter(dataExpiracao));

            boolean dataBate = !temDiasFixos && m.getDataReserva() != null && m.getDataReserva().equals(dataBusca);

            if (dataBate || diaSemanaBate || (isPendente && isHoje)) {
                String statusReal = "AGENDADO";

                if (isPendente) {
                    statusReal = "PENDENTE";
                } else if (m.getStatusDiario() != null && m.getDataStatusDiario() != null) {
                    if (m.getDataStatusDiario().equals(dataBusca)) {
                        statusReal = m.getStatusDiario();
                    }
                }

                for (Pet pet : m.getPets()) {
                    String tutorNome = (pet.getCliente() != null) ? pet.getCliente().getNome() : "Não informado";
                    String porte = (pet.getPorte() != null && !pet.getPorte().trim().isEmpty()) ? pet.getPorte() : "P";

                    agendaDoDia.add(new AgendaDTO(
                            m.getId(),
                            pet.getNome(),
                            tutorNome,
                            pet.getFoto(),
                            porte,
                            "CRECHE",
                            statusReal
                    ));
                }
            }
        }
        return ResponseEntity.ok(agendaDoDia);
    }

    private String obterSiglaDiaSemana(java.time.DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "SEG";
            case TUESDAY -> "TER";
            case WEDNESDAY -> "QUA";
            case THURSDAY -> "QUI";
            case FRIDAY -> "SEX";
            case SATURDAY -> "SAB";
            case SUNDAY -> "DOM";
        };
    }
}