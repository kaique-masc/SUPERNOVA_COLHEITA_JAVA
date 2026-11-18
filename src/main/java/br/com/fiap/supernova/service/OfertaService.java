package br.com.fiap.supernova.service;

import br.com.fiap.supernova.dto.OfertaRequest;
import br.com.fiap.supernova.dto.OfertaResponse;
import br.com.fiap.supernova.exception.AcessoNegadoException;
import br.com.fiap.supernova.exception.RecursoNaoEncontradoException;
import br.com.fiap.supernova.exception.RegraNegocioException;
import br.com.fiap.supernova.model.*;
import br.com.fiap.supernova.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final ProdutoService produtoService;

    @Transactional(readOnly = true)
    public Page<OfertaResponse> listar(StatusOferta status, Long produtoId, Long produtorId, String uf,
                                       Pageable pageable) {
        return ofertaRepository.buscar(status, produtoId, produtorId, uf, pageable).map(OfertaResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<OfertaResponse> listarDoProdutor(Usuario usuario, Pageable pageable) {
        Produtor produtor = exigirProdutor(usuario);
        return ofertaRepository.buscar(null, null, produtor.getId(), null, pageable).map(OfertaResponse::from);
    }

    @Transactional(readOnly = true)
    public OfertaResponse buscar(Long id) {
        return OfertaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public OfertaResponse criar(Usuario usuario, OfertaRequest request) {
        Produtor produtor = exigirProdutor(usuario);
        if (!produtor.isAtivo()) {
            throw new RegraNegocioException("Produtor inativo nao pode cadastrar ofertas.");
        }
        validarDatas(request);
        if (request.dataValidade().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("Nao e possivel cadastrar uma oferta ja vencida.");
        }

        Oferta oferta = new Oferta();
        oferta.setProdutor(produtor);
        preencher(oferta, request);
        return OfertaResponse.from(ofertaRepository.save(oferta));
    }

    @Transactional
    public OfertaResponse atualizar(Usuario usuario, Long id, OfertaRequest request) {
        Oferta oferta = buscarEntidade(id);
        validarDono(usuario, oferta);
        if (oferta.getStatus() == StatusOferta.CANCELADA) {
            throw new RegraNegocioException("Oferta cancelada nao pode ser alterada.");
        }
        validarDatas(request);
        preencher(oferta, request);
        return OfertaResponse.from(oferta);
    }

    // a oferta nao e apagada porque pode estar em pedidos antigos
    @Transactional
    public void cancelar(Usuario usuario, Long id) {
        Oferta oferta = buscarEntidade(id);
        validarDono(usuario, oferta);
        oferta.setStatus(StatusOferta.CANCELADA);
    }

    private void preencher(Oferta oferta, OfertaRequest request) {
        oferta.setProduto(produtoService.buscarEntidade(request.produtoId()));
        oferta.setPrecoUnitario(request.precoUnitario());
        oferta.setQtdDisponivel(request.qtdDisponivel());
        oferta.setDataColheita(request.dataColheita());
        oferta.setDataValidade(request.dataValidade());

        if (request.qtdDisponivel().signum() == 0) {
            oferta.setStatus(StatusOferta.ESGOTADA);
        } else if (oferta.isVencida(LocalDate.now())) {
            oferta.setStatus(StatusOferta.VENCIDA);
        } else {
            oferta.setStatus(StatusOferta.DISPONIVEL);
        }
    }

    private void validarDatas(OfertaRequest request) {
        if (request.dataValidade().isBefore(request.dataColheita())) {
            throw new RegraNegocioException("A validade nao pode ser anterior a data de colheita.");
        }
    }

    private void validarDono(Usuario usuario, Oferta oferta) {
        if (usuario.getRole() == Role.GESTOR) {
            return;
        }
        Produtor produtor = usuario.getProdutor();
        if (produtor == null || !produtor.getId().equals(oferta.getProdutor().getId())) {
            throw new AcessoNegadoException("Voce so pode alterar as suas proprias ofertas.");
        }
    }

    private Produtor exigirProdutor(Usuario usuario) {
        if (usuario.getProdutor() == null) {
            throw new AcessoNegadoException("Apenas produtores podem realizar esta operacao.");
        }
        return usuario.getProdutor();
    }

    Oferta buscarEntidade(Long id) {
        return ofertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oferta", id));
    }
}
