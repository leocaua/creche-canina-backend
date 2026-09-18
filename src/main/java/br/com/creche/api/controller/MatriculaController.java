package br.com.creche.api.controller;

import br.com.creche.api.dto.MatriculaRequestDTO;
import br.com.creche.api.entity.Matricula;
import br.com.creche.api.service.MatriculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    @Autowired
    private MatriculaService matriculaService;

    @PostMapping
    public ResponseEntity<Matricula> criarMatricula(@RequestBody MatriculaRequestDTO dto){
        Matricula novaMatricula = matriculaService.criarMatricula(dto);
        return
                ResponseEntity.status(201).body(novaMatricula);
    }

    @GetMapping
    public ResponseEntity<List<Matricula>> listarMatriculas(){
        return
                ResponseEntity.ok(matriculaService.listarTodasMatriculas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Matricula> atualizarMatricula(@PathVariable Long id, @RequestBody Matricula matricula){
        Matricula matriculaAtualizado = matriculaService.atualizarMatricula(id, matricula);
        return
                ResponseEntity.status(200).body(matriculaAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMatricula(@PathVariable Long id){
        matriculaService.deletarMatricula(id);
        return
                ResponseEntity.noContent().build();
    }

    @GetMapping("/vagas/{data}")
    public ResponseEntity<Long> consultarVagasDisponivel(@PathVariable @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate data ){
        Long livre = matriculaService.consultarVagasDisponiveis(data);
        return
                ResponseEntity.status(200).body(livre);
    }

    @GetMapping("/dia/{data}")
    public ResponseEntity<List<Matricula>> listarPorDataMatricula(@PathVariable @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate data){
        List<Matricula> listaEncontrada = matriculaService.listarPorData(data);
        return
                ResponseEntity.status(200).body(listaEncontrada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matricula> buscarPorId(@PathVariable Long id){
        Matricula buscaRealizada = matriculaService.buscarPorId(id);
        return
                ResponseEntity.status(200).body(buscaRealizada);
    }
}
