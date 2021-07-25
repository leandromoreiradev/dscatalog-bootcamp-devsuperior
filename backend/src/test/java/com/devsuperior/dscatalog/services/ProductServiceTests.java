package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    //Preparação antes de cada teste da classe
    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000;

        //Configurando os comportamentos simulado do repository mockado
        //Não faz nada = [doNothing()] quando = [When()] chamar o metodo deleteById para deletar o id que existe
        Mockito.doNothing().when(repository).deleteById(existingId);

        //Lanca exeption (Tipo:EmptyResultDataAccessException.class) quando tenta deletar um Id que não existe
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
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
