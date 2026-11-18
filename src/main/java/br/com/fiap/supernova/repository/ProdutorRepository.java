package br.com.fiap.supernova.repository;

import br.com.fiap.supernova.model.Produtor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutorRepository extends JpaRepository<Produtor, Long> {

    boolean existsByCpf(String cpf);

    Page<Produtor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
