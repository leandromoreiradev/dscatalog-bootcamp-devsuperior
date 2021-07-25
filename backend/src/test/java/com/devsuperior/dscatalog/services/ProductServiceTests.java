package com.devsuperior.dscatalog.services;

import static org.assertj.core.api.Assertions.offset;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith({SpringExtension.class})
public class ProductServiceTests {

    @InjectMocks // Mock do servico a ser testado
    private ProductService service;

    @Mock // Mock da dependencia do ProductService (servico a ser testado)
    private ProductRepository repository;

    //Id existente
    private long existingId;

    //Id Inexistente
    private long nonExistingId;
    
    //id que tem dependencia relacional, *(
    private long dependentId;
    
    //PageImpl é o tipo concreto que representa uma pagina de dados(Usado nos testes)
    private PageImpl<Product> page;
    
    private Product product;

    //Preparação antes de cada teste da classe
    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        //page sendo instanciado e já recebendo uma lista de product
        page = new PageImpl<>(List.of(product));
        
        //Dica: Quando há retorno começamos com When
        //Quando chmar o findAll passando qualquer argumento então retorne uma pagina
        Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
        
        //Quando chamar reposotory.findById passando id existente então retorne um Optional que tem um produto la dentro 
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        
        //Quando chamar reposotory.findById passando id inexistente então retorne um Optional vazio (não tem nada la dentro)
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        
        //Quando chamar repository.save passando qualquer obj então retorne um product
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        //Configurando os comportamentos simulado do repository mockado
        //Não faz nada = [doNothing()] quando = [When()] chamar o metodo deleteById para deletar o id que existe
        Mockito.doNothing().when(repository).deleteById(existingId);

        //Lanca exeption (Tipo:EmptyResultDataAccessException.class) quando tenta deletar um Id que não existe
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        
      //Lanca exeption (Tipo:DataIntegrityViolationException.class) quando tenta deletar um Id que tem associação dependente
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }
    
    
    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIdDoesNotExists() {

        //Deve lançar exception quando tentar deletar id que associação dependente
        Assertions.assertThrows(DataBaseException.class, ()->{
            //tentando deletar id que tem associação dependente
            service.delete(dependentId);
        });
        //Verifica se o metodo deleteById foi chamado na acao acima
        //Mockito.times quantidade de vezes que foi chamado
        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
    }
    
    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        //Deve lançar exception quando tentar deletar id que não existe
        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            //tentando deletar id que não existe
            service.delete(nonExistingId);
        });
        //Verifica se o metodo deleteById foi chamado na acao acima
        //Mockito.times quantidade de vezes que foi chamado
        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNotthingWhenIdExists() {

        //Não lanca nenhuma exception na condição
        Assertions.assertDoesNotThrow(()->{
            //Deletando
            service.delete(existingId);
        });
        //Verifica se o metodo deleteById foi chamado na acao acima
        //Mockito.times quantidade de vezes que foi chamado
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

}
