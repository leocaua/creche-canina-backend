package br.com.creche.api.service;

import br.com.creche.api.entity.Notificacao;
import br.com.creche.api.repository.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    public List<Notificacao> buscarPorClienteId(Long clienteId) {
        return notificacaoRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId);
    }

    public Notificacao marcarComoLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada!"));

        notificacao.setLida(true);
        return notificacaoRepository.save(notificacao);
    }
}