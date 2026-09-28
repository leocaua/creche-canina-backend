package br.com.creche.api.repository;

import br.com.creche.api.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByClienteIdOrderByDataCriacaoDesc(Long clienteId);
}