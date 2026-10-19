package br.com.creche.api.service;

import br.com.creche.api.entity.Notificacao;
import br.com.creche.api.repository.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    public List<Notificacao> buscarPorClienteId(Long clienteId) {
        return notificacaoRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId).stream()
                .filter(n -> !Boolean.TRUE.equals(n.getExcluidaCliente()))
                .toList();
    }

    public Notificacao marcarComoLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada!"));

        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities() != null &&
                auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (isAdmin) {
            notificacao.setLidaAdmin(true);
        } else {
            notificacao.setLida(true);
        }
        return notificacaoRepository.save(notificacao);
    }

    public List<Notificacao> buscarTodasParaAdmin() {
        return notificacaoRepository.findAllByOrderByDataCriacaoDesc().stream()
                .filter(n -> !Boolean.TRUE.equals(n.getExcluidaAdmin()))
                .toList();
    }
}