package br.com.fiap.supernova.controller;

import br.com.fiap.supernova.dto.ProdutoRequest;
import br.com.fiap.supernova.dto.ProdutoResponse;
import br.com.fiap.supernova.model.CategoriaProduto;
import br.com.fiap.supernova.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Produtos", description = "Catalogo de produtos (alteracoes somente pelo gestor)")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    @Operation(summary = "Lista produtos com paginacao, opcionalmente por categoria")
    public Page<ProdutoResponse> listar(@RequestParam(required = false) CategoriaProduto categoria,
                                        @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return produtoService.listar(categoria, pageable);
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable Long id) {
        return produtoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra produto (GESTOR)")
    public ProdutoResponse criar(@RequestBody @Valid ProdutoRequest request) {
        return produtoService.criar(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza produto (GESTOR)")
    public ProdutoResponse atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoRequest request) {
        return produtoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui produto sem ofertas (GESTOR)")
    public void excluir(@PathVariable Long id) {
        produtoService.excluir(id);
    }
}
