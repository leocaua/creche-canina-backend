package br.com.creche.api.controller;

import br.com.creche.api.dto.TutorDTO;
import br.com.creche.api.entity.Cliente;
import br.com.creche.api.entity.Matricula;
import br.com.creche.api.entity.Pet;
import br.com.creche.api.repository.ClienteRepository;
import br.com.creche.api.repository.MatriculaRepository;
import br.com.creche.api.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tutores-painel")
public class TutorController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @GetMapping
    public ResponseEntity<List<TutorDTO>> listarTutoresPainel() {
        List<Cliente> clientes = clienteRepository.findAll();
        List<TutorDTO> resposta = new ArrayList<>();

        for (Cliente c : clientes) {
            List<Pet> pets = petRepository.findByClienteId(c.getId());
            List<String> nomesPets = pets.stream().map(Pet::getNome).toList();

            String planoNome = "Sem plano";
            int totalAgendamentos = 0;

            List<Matricula> matriculas = matriculaRepository.findByPetsClienteId(c.getId());
            for (Matricula m : matriculas) {
                if (m.getStatus() != null && m.getStatus()) {
                    if (m.getPlano() != null) {
                        planoNome = m.getPlano().getNome();
                    }
                    totalAgendamentos++;
                }
            }
            String inicial = c.getNome() != null && !c.getNome().isEmpty() ? c.getNome().substring(0, 1).toUpperCase() : "?";

            resposta.add(new TutorDTO(
                    c.getId(),
                    inicial,
                    c.getNome(),
                    c.getEmail(),
                    c.getContato(),
                    planoNome,
                    nomesPets,
                    totalAgendamentos
            ));
        }
        return ResponseEntity.ok(resposta);
    }
}