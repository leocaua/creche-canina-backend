package br.com.creche.api.controller;

import br.com.creche.api.entity.Cliente;
import br.com.creche.api.repository.ClienteRepository;
import br.com.creche.api.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/clientes")

public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> cadastrar(@RequestBody Cliente cliente){
        Cliente clienteSalvo = clienteService.cadastrarCliente(cliente);

        return ResponseEntity.status(201).body(clienteSalvo);

    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos(){
        List<Cliente> clientes = clienteService.listarTodos();

        return ResponseEntity.status(200).body(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id){
        Cliente buscaRealizada = clienteService.buscarPorId(id);

        return ResponseEntity.status(200).body(buscaRealizada);
    }

    @PutMapping("/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@RequestBody Cliente dados) {

        try {
            clienteService.redefinirSenha(dados.getEmail(), dados.getSenha());
            return ResponseEntity.ok("Senha alterada com sucesso!");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCliente(@PathVariable Long id) {
        try {
            clienteService.deletarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}

