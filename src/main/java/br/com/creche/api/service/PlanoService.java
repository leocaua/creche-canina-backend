package br.com.creche.api.service;

import br.com.creche.api.entity.Plano;
import br.com.creche.api.repository.PlanoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanoService {

    @Autowired
    private PlanoRepository planoRepository;

    public Plano criarPlano(Plano plano){
        return planoRepository.save(plano);
    }

    public List<Plano> listarPlanos(){
        return planoRepository.findAll();
    }

    public Plano atualizarPlano(Long id, Plano planoAtualizado){
        Plano planoExistente = planoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));

        planoExistente.setPreco(planoAtualizado.getPreco());

        return planoRepository.save(planoExistente);
    }

    public void deletarPlano(Long id){
        planoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não  encontrado"));

        planoRepository.deleteById(id);
    }

    public Plano buscarPorId(Long id){
        planoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id não encontrado"));

        return planoRepository.save(planoRepository.findById(id).get());
    }
}
