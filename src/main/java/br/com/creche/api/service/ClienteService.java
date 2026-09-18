package br.com.creche.api.service;

import br.com.creche.api.entity.Cliente;
import br.com.creche.api.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Cliente cadastrarCliente(Cliente cliente){

        Optional<Cliente> clienteExistente = clienteRepository.findByEmail(cliente.getEmail());
        if(clienteExistente.isPresent()){
            throw new RuntimeException("Este e-mail já está em uso na creche!");
        }

        String senhaCriptografada = passwordEncoder.encode(cliente.getSenha());
        cliente.setSenha(senhaCriptografada);

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarTodos(){

        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id){
        clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id não encontrado"));

        return clienteRepository.findById(id).get();
    }

    public void redefinirSenha(String email, String novaSenha) {

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado!"));

        String senhaCriptografada = passwordEncoder.encode(novaSenha);
        cliente.setSenha(senhaCriptografada);

        clienteRepository.save(cliente);
    }
}
