package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest //Testa Repositorios
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach //Antes de cada teste execute
    void setUp() throws Exception{
        /*
        * Aqui ficam variaveis ou metodos a serem executados antes de cada teste
        * */
         existingId = 1L;
         nonExistingId = 1000;
         countTotalProducts = 25; //Total de products no db e ultimo ID
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull(){
        Product product = Factory.createProduct();//Fabrica de product
        product.setId(null);
        product = repository.save(product);//Salvando no DB sem ID pois será auto increment

        Assertions.assertNotNull(product.getId());//Verifica se o id não é nulo
        Assertions.assertEquals(countTotalProducts + 1, product.getId());//Compara se a qtde de products é = 25 + 1 (26)
    }

    @Test
    public  void deleteShouldDeleteObjectWhenIdExists(){
        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);
        //isPresent testa se existe um objeto dentro do optional
        Assertions.assertFalse(result.isPresent());

    }

    @Test
    public  void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(EmptyResultDataAccessException.class, () ->{
            repository.deleteById(nonExistingId);
        });
    }

    @Test
    public void findByIdShouldReturnOptionalNotEmptyWhenIdExist(){
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void findByIdShouldReturnOptionalEmptyWhenNotIdExist(){
        Optional<Product> result = repository.findById(nonExistingId);
        Assertions.assertTrue(result.isEmpty());
    }
}
