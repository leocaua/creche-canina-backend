package br.com.creche.api.repository;

import br.com.creche.api.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet,Long> {

    List<Pet> findByClienteId(Long clienteId);
}
