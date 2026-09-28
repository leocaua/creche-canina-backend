package br.com.creche.api.controller;

import br.com.creche.api.entity.Notificacao;
import br.com.creche.api.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Notificacao>> buscarPorClienteId(@PathVariable Long clienteId) {

        return ResponseEntity.ok(notificacaoService.buscarPorClienteId(clienteId));
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<Notificacao> marcarComoLida(@PathVariable Long id) {
        Notificacao notificacaoAtualizada = notificacaoService.marcarComoLida(id);

        return ResponseEntity.ok(notificacaoAtualizada);
    }
}