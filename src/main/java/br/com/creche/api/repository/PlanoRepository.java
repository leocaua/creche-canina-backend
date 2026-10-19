package br.com.creche.api.repository;

import br.com.creche.api.entity.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Long> {
}
