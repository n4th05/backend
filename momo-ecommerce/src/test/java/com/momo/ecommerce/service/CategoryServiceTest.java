package com.momo.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import com.momo.ecommerce.model.Category;
import com.momo.ecommerce.model.Product;
import com.momo.ecommerce.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Autowired
    @InjectMocks
    private CategoryService categoryService;


    @Test
    @DisplayName("Deve Listar Todas as Categorias")
    public void findAll() {

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Eletrônicos");
        category1.setDescription("Teste description");
        category1.setActive(false);
        category1.setCreatedAt(LocalDateTime.now());
        category1.setUpdatedAt(LocalDateTime.now());

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Maquiagem");
        category2.setDescription("Teste description");
        category2.setActive(false);
        category2.setCreatedAt(LocalDateTime.now());
        category2.setUpdatedAt(LocalDateTime.now());

        Category category3 = new Category();
        category3.setId(3L);
        category3.setName("Mecânica");
        category3.setDescription("Teste description");
        category3.setActive(false);
        category3.setCreatedAt(LocalDateTime.now());
        category3.setUpdatedAt(LocalDateTime.now());

        List<Category> listaMockada = List.of(category1, category2, category3);

        when(categoryRepository.findAll())
                .thenReturn(listaMockada);

        List<Category> resultado = categoryService.findAll();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());

        assertEquals("Eletrônicos", resultado.get(0).getName());
        assertEquals("Maquiagem", resultado.get(1).getName());
        assertEquals("Mecânica", resultado.get(2).getName());

        verify(categoryRepository).findAll();

        System.out.println("TODAS AS CATEGORIAS: " + resultado);
    }

    @Test
    @DisplayName("Deve Listar Por Id Uma Categoria")
    public void findById() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Eletrônicos");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        Category resultado = categoryService.findById(1L);
        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Eletrônicos", resultado.getName());

        System.out.println(resultado);
    }

    @Test
    @DisplayName("Deve Listar Por Id Uma Categoria Com Produto")
    public void findByIdWithProducts() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Celular");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Notebook");

        List<Product> listaProductsMockada = List.of(product1, product2);

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Eletrônicos");
        category1.setDescription("Teste description");
        category1.setActive(false);
        category1.setCreatedAt(LocalDateTime.now());
        category1.setUpdatedAt(LocalDateTime.now());
        category1.setProducts(listaProductsMockada);

        when(categoryRepository.findByWithProducts(1L))
                .thenReturn(Optional.of(category1));

        Category resultado = categoryService.findByIdWithProducts(1L);

        assertNotNull(resultado);
        assertEquals(category1, resultado);

        verify(categoryRepository).findByWithProducts(1L);

        System.out.println(resultado);
    }

    @Test
    @DisplayName("Deve Criar Categoria")
    public void create() {

        Category category = new Category();
        category.setName("Eletrodomésticos");
        category.setDescription("Eletrônicos que facilitam sua vida em casa!");

        when(categoryRepository.existsByNameIgnoreCase(category.getName()))
            .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
            .thenReturn(category);

        Category categoryCreated = categoryService.create(category);

        assertNotNull(category);
        assertEquals(category, categoryCreated);

        verify(categoryRepository).existsByNameIgnoreCase("Eletrodomésticos");

    }

    @Test
    @DisplayName("Deve Atualizar Uma Categoria")
    public void update() {
        Category category = new Category();

        category.setName("Elétroooo atualizado!");
        category.setDescription("Teste descrição atualizada!");
        category.setActive(false);

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Eletrônicos");
        existingCategory.setDescription("Descrição antiga");
        existingCategory.setActive(true);


        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Category categoryUpdated = categoryService.update(1L, category);

        assertNotNull(categoryUpdated);
        assertEquals("Elétroooo atualizado!", categoryUpdated.getName());

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));

    }

    @Test
    public void deveDeletarUmaCategoria() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Eletrônicos");
        category.setDescription("Teste description");
        category.setActive(false);

        when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));
                
        categoryService.delete(1L);

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(category);

    }
    
}
