package br.com.fiap.supernova.controller;

import br.com.fiap.supernova.dto.MetaPnaeResponse;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.service.PnaeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/pnae")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "PNAE", description = "Acompanhamento da meta de 30% de compras da agricultura familiar")
public class PnaeController {

    private final PnaeService pnaeService;

    @GetMapping("/metas")
    @Operation(summary = "Situacao da meta de todas as escolas no ano (GESTOR)")
    public List<MetaPnaeResponse> metas(@RequestParam(required = false) Integer ano) {
        return pnaeService.metasDasEscolas(ano != null ? ano : Year.now().getValue());
    }

    @GetMapping("/metas/minha")
    @PreAuthorize("hasRole('COMPRADOR')")
    @Operation(summary = "Situacao da meta da escola logada")
    public MetaPnaeResponse minhaMeta(@AuthenticationPrincipal Usuario usuario,
                                      @RequestParam(required = false) Integer ano) {
        return pnaeService.minhaMeta(usuario, ano != null ? ano : Year.now().getValue());
    }
}
