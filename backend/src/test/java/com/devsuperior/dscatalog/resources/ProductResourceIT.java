package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc //Para teste de integração camada Web, carrega o contexto e trata requisições sem subir o server
@Transactional // para que a cada test seja dado um rollback no banco de dados
public class ProductResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; //Transforma um obj java em JSON

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
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));
                //Verifica se o retorno é 200
                result.andExpect(status().isOk());
                //verifica se o valor de "$.totalElements" é igual a countTotalProducts
                result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
                //Verifica se existe o content
                result.andExpect(jsonPath("$.content").exists());
                //Verifica se o content na posição 0 que é o name, se é igual a "Macbook Pro"
                result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
                //Verifica se o content na posição 0 que é o name, se é igual a "PC Gamer"
                result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
                //Verifica se o content na posição 0 que é o name, se é igual a "PC Gamer Alfa"
                result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));

    }


    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        ProductDTO productDTO = Factory.createProductDTO();
        //Tranformando um obj para json
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();


        //Quando chamar PUT "/products/{id}" passando um id que existe  deve retornar productDTO
        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody) // Conteudo JSON que será mandado no body da REQ
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteudo que vai no body da REQ
                        .accept(MediaType.APPLICATION_JSON));// Tipo de conteudo aceito
        //Assertions
        result.andExpect(status().isOk()); //Verifica se o status é (200)
        result.andExpect(jsonPath("$.id").value(existingId)); //Verifica se o id é igual ao existingId
        result.andExpect(jsonPath("$.name").value(expectedName)); //Verifica se o valor de name é o esperado
        result.andExpect(jsonPath("$.description").value(expectedDescription)); //Verifica se o valor de description é o esperado

    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        ProductDTO productDTO = Factory.createProductDTO();
        //Tranformando um obj para json
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        //Quando chamar PUT "/products/{id}" passando um id que existe  deve retornar productDTO
        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody) // Conteudo JSON que será mandado no body da REQ
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteudo que vai no body da REQ
                        .accept(MediaType.APPLICATION_JSON));// Tipo de conteudo aceito
        //Assertions
        result.andExpect(status().isNotFound()); //Verifica se o status é (404)
    }

}
