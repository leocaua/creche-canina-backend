package br.com.creche.api.controller;

import br.com.creche.api.dto.DadosAutenticacao;
import br.com.creche.api.entity.Cliente;
import br.com.creche.api.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<String> efetuarLogin(@RequestBody DadosAutenticacao dados) {
        var tokenAutenticacao = new
                UsernamePasswordAuthenticationToken(dados.login(), dados.senha());

        var authentication = authenticationManager.authenticate(tokenAutenticacao);

        var tokenJWT = tokenService.gerarToken((Cliente) authentication.getPrincipal());
        return
                ResponseEntity.ok(tokenJWT);
    }
}
