package br.com.creche.api.service;

import br.com.creche.api.entity.Cliente;
import br.com.creche.api.entity.Pet;
import br.com.creche.api.repository.ClienteRepository;
import br.com.creche.api.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    public Pet cadastrarPet(Pet pet){

        Long idDoCliente = pet.getCliente().getId();
        Cliente clienteCompleto = clienteRepository.findById(idDoCliente).get();
        pet.setCliente(clienteCompleto);

        if(!pet.getVacinado()){
            throw new RuntimeException("A creche não aceita cães sem vacina!");
        }
        return petRepository.save(pet);
    }

    public List<Pet> listarTodosPets(){
        return petRepository.findAll();
    }

    public Pet atualizarPet(Long id, Pet petComDadosNovos){
        Pet petExistente = petRepository.findById(id).get();

        petExistente.setNome(petComDadosNovos.getNome());
        petExistente.setIdade(petComDadosNovos.getIdade());
        petExistente.setRaca(petComDadosNovos.getRaca());
        petExistente.setVacinado(petComDadosNovos.getVacinado());
        petExistente.setCastrado(petComDadosNovos.getCastrado());
        petExistente.setCor(petComDadosNovos.getCor());
        petExistente.setSexo(petComDadosNovos.getSexo());
        petExistente.setPorte(petComDadosNovos.getPorte());
        petExistente.setPeso(petComDadosNovos.getPeso());
        petExistente.setFoto(petComDadosNovos.getFoto());

        return petRepository.save(petExistente);
    }

    public void deletarPet(Long id){
        petRepository.deleteById(id);
    }

    public Pet buscarPorId(Long id){
        petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id não encontrado"));

        return petRepository.save(petRepository.findById(id).get());
    }

    public List<Pet> buscarPorClienteId(Long clienteId) {
        return petRepository.findByClienteId(clienteId);
    }
}
