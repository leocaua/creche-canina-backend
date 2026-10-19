package br.com.creche.api.controller;

import br.com.creche.api.dto.MatriculaAdminDTO;
import br.com.creche.api.dto.MatriculaRequestDTO;
import br.com.creche.api.entity.Cliente;
import br.com.creche.api.entity.Matricula;
import br.com.creche.api.entity.Notificacao;
import br.com.creche.api.entity.Pet;
import br.com.creche.api.repository.MatriculaRepository;
import br.com.creche.api.repository.NotificacaoRepository;
import br.com.creche.api.service.MatriculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    @Autowired
    private MatriculaService matriculaService;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @PostMapping
    public ResponseEntity<Matricula> criarMatricula(@RequestBody MatriculaRequestDTO dto){
        Matricula novaMatricula = matriculaService.criarMatricula(dto);

        return ResponseEntity.status(201).body(novaMatricula);
    }

    @GetMapping
    public ResponseEntity<List<Matricula>> listarMatriculas(){

        return ResponseEntity.ok(matriculaService.listarTodasMatriculas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Matricula> atualizarMatricula(@PathVariable Long id, @RequestBody Matricula matricula){
        Matricula matriculaAtualizado = matriculaService.atualizarMatricula(id, matricula);

        return ResponseEntity.status(200).body(matriculaAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMatricula(@PathVariable Long id){
        matriculaService.deletarMatricula(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vagas/{data}")
    public ResponseEntity<Long> consultarVagasDisponivel(@PathVariable @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate data ){
        Long livre = matriculaService.consultarVagasDisponiveis(data);

        return ResponseEntity.status(200).body(livre);
    }

    @GetMapping("/dia/{data}")
    public ResponseEntity<List<Matricula>> listarPorDataMatricula(@PathVariable @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate data){
        List<Matricula> listaEncontrada = matriculaService.listarPorData(data);

        return ResponseEntity.status(200).body(listaEncontrada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matricula> buscarPorId(@PathVariable Long id){
        Matricula buscaRealizada = matriculaService.buscarPorId(id);

        return ResponseEntity.status(200).body(buscaRealizada);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Matricula>> buscarPorClienteId(@PathVariable Long clienteId){
        List<Matricula> matriculasDoCliente = matriculaService.buscarPorClienteId(clienteId);

        return ResponseEntity.status(200).body(matriculasDoCliente);
    }

    @PostMapping("/admin")
    public ResponseEntity<?> criarMatriculaAdministrativa(@RequestBody MatriculaAdminDTO dto) {
        System.out.println("Recebido do painel Admin: Cliente " + dto.clienteId() + " | Status: " + dto.statusPagamento());
        matriculaService.criarMatriculaAdministrativa(dto);

        Notificacao aviso = new Notificacao();
        aviso.setTipo("MATRICULA");
        aviso.setTexto("Nova matrícula gerada com sucesso pela recepção!");

        Cliente cliente = new Cliente();
        cliente.setId(dto.clienteId());
        aviso.setCliente(cliente);
        notificacaoRepository.save(aviso);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovarMatricula(@PathVariable Long id) {
        Matricula matricula = matriculaRepository.findById(id).orElse(null);

        if (matricula != null) {
            matricula.setStatus(true);
            String nomesPets = matricula.getPets().isEmpty()
                    ? "Pet"
                    : matricula.getPets().stream().map(Pet::getNome).collect(Collectors.joining(", "));

            Notificacao aviso = new Notificacao();
            aviso.setTipo("MATRICULA");
            aviso.setTexto("Matrícula de " + nomesPets + " aprovada com sucesso!");
            Cliente donoDoPet = matricula.getPets().isEmpty() ? null : matricula.getPets().get(0).getCliente();
            aviso.setCliente(donoDoPet);

            notificacaoRepository.save(aviso);

            matriculaRepository.save(matricula);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/recusar")
    public ResponseEntity<?> recusarMatricula(@PathVariable Long id) {
        Matricula matricula = matriculaRepository.findById(id).orElse(null);

        if (matricula != null) {
            String nomesPets = matricula.getPets().isEmpty()
                    ? "Pet"
                    : matricula.getPets().stream().map(Pet::getNome).collect(Collectors.joining(", "));

            Notificacao aviso = new Notificacao();
            aviso.setTipo("AUSENCIA");
            aviso.setTexto("Matrícula pendente de " + nomesPets + " foi recusada.");
            Cliente donoDoPet = matricula.getPets().isEmpty() ? null : matricula.getPets().get(0).getCliente();
            aviso.setCliente(donoDoPet);

            notificacaoRepository.save(aviso);

            matriculaRepository.delete(matricula);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/receita-total")
    public ResponseEntity<BigDecimal> obterReceitaTotal() {
        BigDecimal receita = matriculaRepository.somarReceitaAtiva();

        if (receita == null) {
            receita = BigDecimal.ZERO;
        }
        return ResponseEntity.ok(receita);
    }
}