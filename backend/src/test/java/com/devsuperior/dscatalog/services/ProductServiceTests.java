package com.devsuperior.dscatalog.services;

import static org.assertj.core.api.Assertions.offset;

import java.util.List;
import java.util.Optional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import org.checkerframework.checker.units.qual.A;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    @Mock
    private CategoryRepository categoryRepository;

    //Id existente
    private long existingId;

    //Id Inexistente
    private long nonExistingId;
    
    //id que tem dependencia relacional, *(
    private long dependentId;
    
    //PageImpl é o tipo concreto que representa uma pagina de dados(Usado nos testes)
    private PageImpl<Product> page;
    
    private Product product;
    private ProductDTO productDTO;

    //Preparação antes de cada teste da classe
    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        productDTO = Factory.createProductDTO();
        //page sendo instanciado e já recebendo uma lista de product
        page = new PageImpl<>(List.of(product));
        
        //*****CONFIGURANDO OS COMPORTAMENTOS SIMULADO DO REPOSITORY MOCKADO*******//
        
        //Dica: Quando há retorno começamos com When
        //Quando chmar o findAll passando qualquer argumento então retorne uma pagina
        Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
        
        //Quando chamar reposotory.findById passando id existente então retorne um Optional que tem um produto la dentro 
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        
        //Quando chamar reposotory.findById passando id inexistente então retorne um Optional vazio (não tem nada la dentro)
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getOne(existingId)).thenReturn(product);
        
        Mockito.when(categoryRepository.getOne(existingId)).thenReturn(new Category(1L, "Eletronics"));

        
        
        //Quando chamar repository.save passando qualquer obj então retorne um product
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        //Não faz nada = [doNothing()] quando = [When()] chamar o metodo deleteById para deletar o id que existe
        Mockito.doNothing().when(repository).deleteById(existingId);

        //Lanca exeption (Tipo:EmptyResultDataAccessException.class) quando tenta deletar um Id que não existe
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        
      //Lanca exeption (Tipo:DataIntegrityViolationException.class) quando tenta deletar um Id que tem associação dependente
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }
    

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);
        Assertions.assertNotNull(result);

        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
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

    @Test
    public void shouldReturnProductDTOWhenIdExists() {
        ProductDTO productDTOExpected = Factory.createProductDTO();
        ProductDTO productDTOActual = this.service.findById(existingId);

        Assertions.assertNotNull(productDTOActual);
        Assertions.assertEquals(productDTOExpected.getId(), productDTOActual.getId());

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void shouldTrhowResourseNotFoundWhenNotIdExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () ->{
            this.service.findById(nonExistingId);
        });
        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void shouldUpdateProductDTOWhenIdExists() {
        ProductDTO productDTOExpected = Factory.createProductDTO();
        productDTOExpected.setDescription("Galaxy");
        ProductDTO productDTOActual = this.service.update(existingId, productDTOExpected);
        Assertions.assertNotNull(productDTOActual);
        Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
    }

    @Test
    public void shouldTrhowResourseNotFoundWhenUpdateNotIdExist() {
        Mockito.when(repository.getOne(nonExistingId)).thenThrow(ResourceNotFoundException.class);
        ProductDTO productDTOExpected = Factory.createProductDTO();
        productDTOExpected.setDescription("Galaxy");
        Assertions.assertThrows(ResourceNotFoundException.class, () ->{
            this.service.update(nonExistingId, productDTOExpected);
        });
        Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
    }

}
