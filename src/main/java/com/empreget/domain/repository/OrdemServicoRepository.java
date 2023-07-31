package com.empreget.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.empreget.domain.model.Cliente;
import com.empreget.domain.model.OrdemServico;
import com.empreget.domain.model.Prestador;
import com.empreget.domain.model.enums.StatusOrdemServico;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long>{

	boolean existsByPrestadorAndDataServico(Prestador prestador, LocalDate dataServico);
			
	//buscar uma lista de OS relacionadas ao email do prestador
	List<OrdemServico> findByPrestadorUsuarioEmail(String email);
	
	//buscar uma lista de OS relacionadas ao email do cliente 
	List<OrdemServico> findByClienteUsuarioEmail(String email);
	
	@Query("SELECT os.cliente.usuario.email FROM OrdemServico os WHERE os.cliente.usuario.email = :email")
	Optional<String> findClienteEmailByOS(@Param("email") String email);
	
	@Query("SELECT os.prestador.usuario.email FROM OrdemServico os WHERE os.prestador.usuario.email = :email")
	Optional<String> findPrestadorEmailByOS(@Param("email") String email);


	@Query("SELECT COUNT(os) > 0 FROM OrdemServico os " +
	           "WHERE os.cliente = :cliente " +
	           "AND os.dataServico = :dataServico " +
	           "AND os.statusOrdemServico NOT IN :status")
	boolean clinteJaSolicitouOSNaData(@Param("cliente")Cliente cliente, 
										@Param("dataServico")LocalDate dataServico, 
										@Param("status")List<StatusOrdemServico> status);
}
