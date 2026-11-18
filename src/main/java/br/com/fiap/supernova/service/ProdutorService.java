package br.com.fiap.supernova.service;

import br.com.fiap.supernova.dto.ProdutorRequest;
import br.com.fiap.supernova.dto.ProdutorResponse;
import br.com.fiap.supernova.exception.AcessoNegadoException;
import br.com.fiap.supernova.exception.RecursoNaoEncontradoException;
import br.com.fiap.supernova.exception.RegraNegocioException;
import br.com.fiap.supernova.model.Produtor;
import br.com.fiap.supernova.model.Role;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.repository.ProdutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutorService {

    private final ProdutorRepository produtorRepository;

    @Transactional(readOnly = true)
    public Page<ProdutorResponse> listar(String nome, Pageable pageable) {
        Page<Produtor> pagina = (nome == null || nome.isBlank())
                ? produtorRepository.findAll(pageable)
                : produtorRepository.findByNomeContainingIgnoreCase(nome, pageable);
        return pagina.map(ProdutorResponse::from);
    }

    @Transactional(readOnly = true)
    public ProdutorResponse buscar(Long id) {
        return ProdutorResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ProdutorResponse criar(ProdutorRequest request) {
        return ProdutorResponse.from(salvarNovo(request));
    }

    @Transactional
    public ProdutorResponse atualizar(Long id, ProdutorRequest request, Usuario usuario) {
        Produtor produtor = buscarEntidade(id);
        boolean dono = usuario.getProdutor() != null && usuario.getProdutor().getId().equals(id);
        if (usuario.getRole() != Role.GESTOR && !dono) {
            throw new AcessoNegadoException("Voce so pode alterar o seu proprio cadastro.");
        }
        if (!produtor.getCpf().equals(request.cpf()) && produtorRepository.existsByCpf(request.cpf())) {
            throw new RegraNegocioException("Ja existe um produtor com este CPF.");
        }
        preencher(produtor, request);
        return ProdutorResponse.from(produtor);
    }

    // produtor nao e apagado para manter o historico de ofertas e pedidos
    @Transactional
    public void inativar(Long id) {
        buscarEntidade(id).setAtivo(false);
    }

    Produtor salvarNovo(ProdutorRequest request) {
        if (produtorRepository.existsByCpf(request.cpf())) {
            throw new RegraNegocioException("Ja existe um produtor com este CPF.");
        }
        Produtor produtor = new Produtor();
        preencher(produtor, request);
        return produtorRepository.save(produtor);
    }

    Produtor buscarEntidade(Long id) {
        return produtorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produtor", id));
    }

    private void preencher(Produtor produtor, ProdutorRequest request) {
        if ((request.numeroCaf() == null) != (request.validadeCaf() == null)) {
            throw new RegraNegocioException("Informe o numero do CAF junto com a validade.");
        }
        produtor.setNome(request.nome());
        produtor.setCpf(request.cpf());
        produtor.setNumeroCaf(request.numeroCaf());
        produtor.setValidadeCaf(request.validadeCaf());
        produtor.setMunicipio(request.municipio());
        produtor.setUf(request.uf());
        produtor.setTelefone(request.telefone());
        produtor.setEmail(request.email());
    }
}
