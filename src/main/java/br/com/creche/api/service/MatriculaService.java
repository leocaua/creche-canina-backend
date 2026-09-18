package br.com.creche.api.service;

import br.com.creche.api.dto.MatriculaRequestDTO;
import br.com.creche.api.entity.Matricula;
import br.com.creche.api.entity.Pet;
import br.com.creche.api.entity.Plano;
import br.com.creche.api.repository.MatriculaRepository;
import br.com.creche.api.repository.PetRepository;
import br.com.creche.api.repository.PlanoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MatriculaService {

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private PlanoRepository planoRepository;

    @Autowired
    PetRepository petRepository;

    public Matricula criarMatricula(MatriculaRequestDTO dto) {
        Matricula matricula = new Matricula();

        Plano plano = planoRepository.findById(dto.getPlanoId())
                .orElseThrow(() -> new RuntimeException("Plano não Encontrado!"));
        matricula.setPlano(plano);

        List<Pet> pets = petRepository.findAllById(dto.getPetIds());
        matricula.setPets(pets);

        matricula.setDiasSemana(dto.getDiasSemana());
        matricula.setFrequencia(dto.getFrequencia());
        matricula.setDataReserva(LocalDate.now());
        matricula.setStatus(true);

        BigDecimal valorTotal = plano.getPreco()
                .multiply(new BigDecimal(dto.getFrequencia()))
                .multiply(new BigDecimal(pets.size()));
        matricula.setValor(valorTotal);

        return matriculaRepository.save(matricula);
    }

    public List<Matricula> listarTodasMatriculas() {
        return matriculaRepository.findAll();
    }

    public Matricula atualizarMatricula(Long id, Matricula matriculaAtualizada) {

        Matricula matricula = buscarPorId(id);

        matricula.setDataReserva(matriculaAtualizada.getDataReserva());
        matricula.setStatus(matriculaAtualizada.getStatus());
        matricula.setValor(matriculaAtualizada.getValor());
        matricula.setDiasSemana(matriculaAtualizada.getDiasSemana());
        matricula.setFrequencia(matriculaAtualizada.getFrequencia());
        matricula.setPets(matriculaAtualizada.getPets());
        matricula.setPlano(matriculaAtualizada.getPlano());

        return matriculaRepository.save(matricula);

    }

    public void deletarMatricula(Long id){
        matriculaRepository.deleteById(id);
    }

    public Matricula buscarPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada!"));
    }

    public List<Matricula> listarPorData(LocalDate data) {
        return matriculaRepository.findByDataReserva(data);
    }

    public Long consultarVagasDisponiveis(LocalDate data) {

        Long matriculasNoDia = (long) matriculaRepository.findByDataReserva(data).size();
        Long capacidadeMaxima = 20L;

        return capacidadeMaxima - matriculasNoDia;
    }
}
