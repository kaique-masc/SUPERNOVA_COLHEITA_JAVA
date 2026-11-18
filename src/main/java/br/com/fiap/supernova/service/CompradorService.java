package br.com.fiap.supernova.service;

import br.com.fiap.supernova.dto.CompradorRequest;
import br.com.fiap.supernova.dto.CompradorResponse;
import br.com.fiap.supernova.exception.AcessoNegadoException;
import br.com.fiap.supernova.exception.RecursoNaoEncontradoException;
import br.com.fiap.supernova.exception.RegraNegocioException;
import br.com.fiap.supernova.model.Comprador;
import br.com.fiap.supernova.model.Role;
import br.com.fiap.supernova.model.TipoComprador;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.repository.CompradorRepository;
import br.com.fiap.supernova.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompradorService {

    private final CompradorRepository compradorRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public Page<CompradorResponse> listar(TipoComprador tipo, Pageable pageable) {
        Page<Comprador> pagina = tipo == null
                ? compradorRepository.findAll(pageable)
                : compradorRepository.findByTipo(tipo, pageable);
        return pagina.map(CompradorResponse::from);
    }

    @Transactional(readOnly = true)
    public CompradorResponse buscar(Long id) {
        return CompradorResponse.from(buscarEntidade(id));
    }

    @Transactional
    public CompradorResponse criar(CompradorRequest request) {
        return CompradorResponse.from(salvarNovo(request));
    }

    @Transactional
    public CompradorResponse atualizar(Long id, CompradorRequest request, Usuario usuario) {
        Comprador comprador = buscarEntidade(id);
        boolean dono = usuario.getComprador() != null && usuario.getComprador().getId().equals(id);
        if (usuario.getRole() != Role.GESTOR && !dono) {
            throw new AcessoNegadoException("Voce so pode alterar o seu proprio cadastro.");
        }
        if (!comprador.getCnpj().equals(request.cnpj()) && compradorRepository.existsByCnpj(request.cnpj())) {
            throw new RegraNegocioException("Ja existe um comprador com este CNPJ.");
        }
        preencher(comprador, request);
        return CompradorResponse.from(comprador);
    }

    @Transactional
    public void excluir(Long id) {
        Comprador comprador = buscarEntidade(id);
        if (pedidoRepository.existsByCompradorId(id)) {
            throw new RegraNegocioException("Comprador possui pedidos e nao pode ser excluido.");
        }
        compradorRepository.delete(comprador);
    }

    Comprador salvarNovo(CompradorRequest request) {
        if (compradorRepository.existsByCnpj(request.cnpj())) {
            throw new RegraNegocioException("Ja existe um comprador com este CNPJ.");
        }
        Comprador comprador = new Comprador();
        preencher(comprador, request);
        return compradorRepository.save(comprador);
    }

    Comprador buscarEntidade(Long id) {
        return compradorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Comprador", id));
    }

    private void preencher(Comprador comprador, CompradorRequest request) {
        if (request.tipo() == TipoComprador.ESCOLA && request.verbaPnaeAnual() == null) {
            throw new RegraNegocioException("Escolas precisam informar a verba anual do PNAE.");
        }
        comprador.setNome(request.nome());
        comprador.setTipo(request.tipo());
        comprador.setCnpj(request.cnpj());
        comprador.setMunicipio(request.municipio());
        comprador.setUf(request.uf());
        comprador.setVerbaPnaeAnual(request.tipo() == TipoComprador.ESCOLA ? request.verbaPnaeAnual() : null);
        comprador.setEmail(request.email());
    }
}
