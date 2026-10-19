package br.com.creche.api.controller;

import br.com.creche.api.entity.Notificacao;
import br.com.creche.api.repository.NotificacaoRepository;
import br.com.creche.api.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Notificacao>> buscarPorClienteId(@PathVariable Long clienteId) {

        return ResponseEntity.ok(notificacaoService.buscarPorClienteId(clienteId));
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<Notificacao> marcarComoLida(@PathVariable Long id) {
        Notificacao notificacaoAtualizada = notificacaoService.marcarComoLida(id);

        return ResponseEntity.ok(notificacaoAtualizada);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Notificacao>> buscarTodasAdmin() {
        return ResponseEntity.ok(notificacaoService.buscarTodasParaAdmin());
    }

    @DeleteMapping("/admin/limpar")
    public ResponseEntity<?> limparTodasNotificacoesAdmin() {
        List<Notificacao> todas = notificacaoRepository.findAll();
        todas.forEach(n -> n.setExcluidaAdmin(true));
        notificacaoRepository.saveAll(todas);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cliente/{clienteId}/limpar")
    public ResponseEntity<?> limparNotificacoesCliente(@PathVariable Long clienteId) {
        List<Notificacao> notificacoes = notificacaoRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId);
        notificacoes.forEach(n -> n.setExcluidaCliente(true));
        notificacaoRepository.saveAll(notificacoes);

        return ResponseEntity.ok().build();
    }
}