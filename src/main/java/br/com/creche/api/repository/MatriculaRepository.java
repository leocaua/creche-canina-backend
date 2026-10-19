package br.com.creche.api.repository;

import br.com.creche.api.entity.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findByDataReserva(LocalDate dataReserva);
    List<Matricula> findByPetsClienteId(Long clienteId);

    boolean existsByCliente_IdAndStatusTrue(Long id);
    boolean existsByCliente_Id(Long id);
    boolean existsByPets_Id(Long petId);

    @Query("SELECT SUM(m.valor) FROM Matricula m WHERE m.status = true")
    BigDecimal somarReceitaAtiva();
}
