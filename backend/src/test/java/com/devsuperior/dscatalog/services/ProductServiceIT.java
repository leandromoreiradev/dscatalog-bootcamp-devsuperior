package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional //Garante que cada um dos tests vai rodar e fazer rollback no banco para que cada teste pegue o banco no seu estado incial
public class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;

    }

    @Test
    public void  deleteShouldDeleteResourceWhenIdExists() {
        //Testa se deleta um obj quando tenta deletar um obj com id que existe
        service.delete(existingId);
        //Compara o total de produtos depois de deletar
        Assertions.assertEquals(countTotalProducts - 1, repository.count() ); //repository.count() retorna quantidade total

    }

    @Test
    public void  deleteShouldDeleteThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        //Testa se retorna um ResourceNotFoundException quando tenta deletar um obj com id que não existe
        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            service.delete(nonExistingId);
        });
    }

    @Test
    public void findAllPagedShouldReturnPageWhemPageZeroSizeTen() {

        //Mockando uma pagina
        PageRequest pageRequest = PageRequest.of(0,10);
        //Chamando service.findAllPaged(pageRequest);
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        //Verifica se tem obj / se não é vazio, ou seja se tiver, deve retornar false
        Assertions.assertFalse(result.isEmpty());
        //Verifica se a page é 0
        Assertions.assertEquals(0,result.getNumber());
        //Verifica se retorna 10 elementos
        Assertions.assertEquals(10, result.getSize());
        //Verifica se o total de elementos é igual a  countTotalProducts = 25L;
        Assertions.assertEquals(countTotalProducts,result.getTotalElements());
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhemPageDoesNotExist() {

        //Mockando uma pagina
        PageRequest pageRequest = PageRequest.of(50,10);
        //Chamando service.findAllPaged(pageRequest);
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        //Verifica se o resultado é vazio
        Assertions.assertTrue(result.isEmpty());

    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhemSortByName() {

        //Mockando uma pagina com numero da page, qtde de elementos e ordenação
        PageRequest pageRequest = PageRequest.of(0,10, Sort.by("name"));
        //Chamando service.findAllPaged(pageRequest);
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        //Verifica se tem obj / se não é vazio, ou seja se tiver, deve retornar false
        Assertions.assertFalse(result.isEmpty());
        //Verifica se o nome do primeiro elemento da lista é igual a "Macbook Pro"
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName()); //getContent() pega a lista de elementos
        //Verifica se o nome do segundo elemento da lista é igual a "PC Gamer"
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        //Verifica se o nome do terceiro elemento da lista é igual a "PC Gamer Alfa"
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());

    }
}
