package br.com.creche.api.controller;

import br.com.creche.api.entity.Plano;
import br.com.creche.api.service.PlanoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planos")
public class PlanoController {

    @Autowired
    private PlanoService planoService;

    @PostMapping
    public ResponseEntity<Plano> criarPlano(@RequestBody Plano plano) {
        Plano novoPlano = planoService.criarPlano(plano);

        return ResponseEntity.status(201).body(novoPlano);
    }

    @GetMapping
    public ResponseEntity<List<Plano>> listarPlanos(){
        return ResponseEntity.status(200).body(planoService.listarPlanos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plano> atualizarPlano(@PathVariable Long id, @RequestBody Plano plano) {
        Plano planoAtualizado = planoService.atualizarPlano(id, plano);

        return ResponseEntity.status(200).body(planoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPlano(@PathVariable Long id){
        planoService.deletarPlano(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plano> buscarPlanoPorId(@PathVariable Long id){
        Plano buscaRealizada = planoService.buscarPorId(id);

        return ResponseEntity.status(200).body(buscaRealizada);
    }
}
