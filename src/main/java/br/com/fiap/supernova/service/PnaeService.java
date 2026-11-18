package br.com.fiap.supernova.service;

import br.com.fiap.supernova.dto.MetaPnaeResponse;
import br.com.fiap.supernova.exception.AcessoNegadoException;
import br.com.fiap.supernova.exception.RegraNegocioException;
import br.com.fiap.supernova.model.Comprador;
import br.com.fiap.supernova.model.StatusPedido;
import br.com.fiap.supernova.model.TipoComprador;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.repository.CompradorRepository;
import br.com.fiap.supernova.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PnaeService {

    static final BigDecimal META_PERCENTUAL = new BigDecimal("30");
    private static final BigDecimal CEM = new BigDecimal("100");

    private final CompradorRepository compradorRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public List<MetaPnaeResponse> metasDasEscolas(int ano) {
        return compradorRepository.findByTipoOrderByNome(TipoComprador.ESCOLA).stream()
                .map(escola -> calcularMeta(escola, ano))
                .toList();
    }

    @Transactional(readOnly = true)
    public MetaPnaeResponse minhaMeta(Usuario usuario, int ano) {
        Comprador comprador = usuario.getComprador();
        if (comprador == null || !comprador.isEscola()) {
            throw new AcessoNegadoException("Apenas escolas acompanham a meta do PNAE.");
        }
        return calcularMeta(comprador, ano);
    }

    MetaPnaeResponse calcularMeta(Comprador escola, int ano) {
        BigDecimal verba = escola.getVerbaPnaeAnual();
        if (verba == null || verba.signum() == 0) {
            throw new RegraNegocioException("A escola " + escola.getNome() + " nao tem verba do PNAE cadastrada.");
        }

        BigDecimal comprado = pedidoRepository.somarPorCompradorEStatus(escola.getId(), StatusPedido.ENTREGUE,
                java.time.LocalDate.of(ano, 1, 1).atStartOfDay(),
                java.time.LocalDate.of(ano + 1, 1, 1).atStartOfDay());

        BigDecimal percentual = comprado.multiply(CEM).divide(verba, 2, RoundingMode.HALF_UP);
        BigDecimal valorMeta = verba.multiply(META_PERCENTUAL).divide(CEM, 2, RoundingMode.HALF_UP);
        BigDecimal falta = valorMeta.subtract(comprado).max(BigDecimal.ZERO);

        return new MetaPnaeResponse(escola.getId(), escola.getNome(), escola.getMunicipio(), ano, verba,
                comprado, percentual, META_PERCENTUAL, falta, situacao(percentual));
    }

    static String situacao(BigDecimal percentual) {
        if (percentual.compareTo(META_PERCENTUAL) >= 0) {
            return "META ATINGIDA";
        }
        if (percentual.compareTo(META_PERCENTUAL.divide(new BigDecimal("2"))) >= 0) {
            return "EM ANDAMENTO";
        }
        return "CRITICO";
    }
}
