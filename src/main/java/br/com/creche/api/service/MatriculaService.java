package br.com.creche.api.service;

import br.com.creche.api.dto.MatriculaAdminDTO;
import br.com.creche.api.dto.MatriculaRequestDTO;
import br.com.creche.api.entity.*;
import br.com.creche.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatriculaService {

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private PlanoRepository planoRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

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
        matricula.setStatus(false);

        BigDecimal valorTotal = plano.getPreco()
                .multiply(new BigDecimal(dto.getFrequencia()))
                .multiply(new BigDecimal(pets.size()));
        matricula.setValor(valorTotal);

        Cliente donoDoPet = pets.isEmpty() ? null : pets.get(0).getCliente();
        matricula.setCliente(donoDoPet);

        Matricula salva = matriculaRepository.save(matricula);

        String nomesPets = pets.stream().map(Pet::getNome).collect(Collectors.joining(", "));
        String nomeTutor = (donoDoPet != null && donoDoPet.getNome() != null) ? donoDoPet.getNome() : "Tutor";

        Notificacao aviso = new Notificacao();
        aviso.setTipo("MATRICULA");
        aviso.setTexto("Nova matrícula solicitada para " + nomesPets + " (" + nomeTutor + ") — aguardando aprovação.");
        aviso.setCliente(donoDoPet);
        notificacaoRepository.save(aviso);

        return salva;
    }

    public Matricula criarMatriculaAdministrativa(MatriculaAdminDTO dto) {
        Matricula matricula = new Matricula();

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado no sistema!"));
        matricula.setCliente(cliente);

        Plano plano = planoRepository.findAll().stream()
                .filter(p -> dto.planoNome().toUpperCase().contains(p.getNome().toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Plano selecionado não encontrado!"));
        matricula.setPlano(plano);

        List<String> petsFrontUpper = dto.nomesPets().stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        List<Pet> petsSelecionados = petRepository.findAll().stream()
                .filter(p -> p.getCliente() != null && p.getCliente().getId().equals(cliente.getId()))
                .filter(p -> petsFrontUpper.contains(p.getNome().toUpperCase()))
                .collect(Collectors.toList());

        if (petsSelecionados.isEmpty()) {
            throw new RuntimeException("Nenhum pet válido encontrado para este cliente.");
        }
        matricula.setPets(petsSelecionados);

        matricula.setDiasSemana(String.join(", ", dto.dias()));
        matricula.setFrequencia(dto.dias().size());
        matricula.setDataReserva(LocalDate.now());

        boolean isAtivo = dto.statusPagamento() != null &&
                (dto.statusPagamento().equalsIgnoreCase("pago") || dto.statusPagamento().equalsIgnoreCase("ativo"));
        matricula.setStatus(isAtivo);

        BigDecimal valorTotal = plano.getPreco()
                .multiply(new BigDecimal(dto.dias().size()))
                .multiply(new BigDecimal(petsSelecionados.size()));
        matricula.setValor(valorTotal);

        Notificacao aviso = new Notificacao();
        aviso.setTipo("PAGAMENTO");
        aviso.setTexto("Nova Matrícula criada - Tutor " + cliente.getNome());
        aviso.setCliente(cliente);
        notificacaoRepository.save(aviso);

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

    public List<Matricula> buscarPorClienteId(Long clienteId) {
        return matriculaRepository.findByPetsClienteId(clienteId);
    }
}
