package br.com.creche.api.controller;

import br.com.creche.api.entity.PagamentoPix;
import br.com.creche.api.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public ResponseEntity<List<PagamentoPix>> listarPagamentos() {
        return ResponseEntity.ok(pagamentoService.listarPagamentoPix());
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<PagamentoPix> atualizarPagamento(@PathVariable Long id) {
        PagamentoPix pagamentoAtualizado = pagamentoService.atualizarPagamento(id);

        return ResponseEntity.ok(pagamentoAtualizado);
    }
}
