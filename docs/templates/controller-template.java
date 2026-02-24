package com.momo.ecommerce.controller;

import com.momo.ecommerce.dto.EntityDTO;
import com.momo.ecommerce.dto.CreateEntityRequest;
import com.momo.ecommerce.dto.UpdateEntityRequest;
import com.momo.ecommerce.model.Entity;
import com.momo.ecommerce.service.EntityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller template.
 * 
 * RESPONSABILIDADES:
 * - Receber requisições HTTP
 * - Validar entrada (Bean Validation)
 * - Chamar service apropriado
 * - Converter Entity <-> DTO
 * - Retornar resposta HTTP
 * 
 * NÃO DEVE:
 * - Ter lógica de negócio
 * - Acessar repository diretamente
 * - Retornar entidades JPA (sempre usar DTOs)
 * 
 * @author Seu Nome
 */
@RestController  // @Controller + @ResponseBody (retorna JSON)
@RequestMapping("/api/entities")  // Base URL para todos os endpoints
@RequiredArgsConstructor  // Lombok: gera construtor com campos final (DI)
@Slf4j  // Lombok: cria logger automaticamente
public class EntityController {

    private final EntityService entityService;

    // ═══════════════════════════════════════════════════════════
    // ENDPOINTS DE LEITURA (GET)
    // ═══════════════════════════════════════════════════════════

    /**
     * Lista todas as entidades com paginação.
     * 
     * URL: GET /api/entities?page=0&size=10&sort=name,asc
     * 
     * Query Params:
     * - page: número da página (default: 0)
     * - size: tamanho da página (default: 10)
     * - sort: campo e direção de ordenação (default: name,asc)
     * 
     * Resposta: 200 OK + Page<EntityDTO>
     */
    @GetMapping
    public ResponseEntity<Page<EntityDTO>> listAll(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.info("GET /api/entities - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Entity> entities = entityService.findAll(pageable);
        Page<EntityDTO> dtos = entities.map(this::convertToDTO);
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Busca entidade por ID.
     * 
     * URL: GET /api/entities/{id}
     * 
     * Path Variable:
     * - id: identificador da entidade
     * 
     * Resposta:
     * - 200 OK + EntityDTO (se encontrado)
     * - 404 Not Found (se não encontrado)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityDTO> getById(@PathVariable Long id) {
        log.info("GET /api/entities/{}", id);
        
        Entity entity = entityService.findById(id);
        EntityDTO dto = convertToDTO(entity);
        
        return ResponseEntity.ok(dto);
    }

    /**
     * Busca entidades por filtro (query parameter).
     * 
     * URL: GET /api/entities/search?name=laptop&active=true
     * 
     * Query Params:
     * - name: filtro por nome (opcional)
     * - active: filtro por status (opcional)
     * 
     * Resposta: 200 OK + List<EntityDTO>
     */
    @GetMapping("/search")
    public ResponseEntity<List<EntityDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active) {
        
        log.info("GET /api/entities/search - name: {}, active: {}", name, active);
        
        List<Entity> entities = entityService.search(name, active);
        List<EntityDTO> dtos = entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    // ═══════════════════════════════════════════════════════════
    // ENDPOINTS DE CRIAÇÃO (POST)
    // ═══════════════════════════════════════════════════════════

    /**
     * Cria nova entidade.
     * 
     * URL: POST /api/entities
     * Body: JSON com dados da entidade
     * 
     * Request Body:
     * {
     *   "name": "Nome",
     *   "description": "Descrição",
     *   "value": 100.00
     * }
     * 
     * Validações:
     * - @Valid ativa Bean Validation no request body
     * - Se inválido, retorna 400 Bad Request automaticamente
     * 
     * Resposta:
     * - 201 Created + EntityDTO
     * - Location header com URL do recurso criado
     */
    @PostMapping
    public ResponseEntity<EntityDTO> create(@Valid @RequestBody CreateEntityRequest request) {
        log.info("POST /api/entities - {}", request);
        
        // Converter DTO Request -> Entity
        Entity entity = convertToEntity(request);
        
        // Salvar via service
        Entity created = entityService.create(entity);
        
        // Converter Entity -> DTO Response
        EntityDTO dto = convertToDTO(created);
        
        // Retornar 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // ═══════════════════════════════════════════════════════════
    // ENDPOINTS DE ATUALIZAÇÃO (PUT/PATCH)
    // ═══════════════════════════════════════════════════════════

    /**
     * Atualiza entidade existente (substitui completamente).
     * 
     * URL: PUT /api/entities/{id}
     * Path Variable: id da entidade
     * Body: JSON com TODOS os dados atualizados
     * 
     * PUT = substituição completa do recurso
     * 
     * Resposta:
     * - 200 OK + EntityDTO (se atualizado)
     * - 404 Not Found (se não encontrado)
     */
    @PutMapping("/{id}")
    public ResponseEntity<EntityDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEntityRequest request) {
        
        log.info("PUT /api/entities/{} - {}", id, request);
        
        Entity entity = convertToEntity(request);
        Entity updated = entityService.update(id, entity);
        EntityDTO dto = convertToDTO(updated);
        
        return ResponseEntity.ok(dto);
    }

    /**
     * Atualiza parcialmente entidade (só campos enviados).
     * 
     * URL: PATCH /api/entities/{id}
     * Body: JSON com campos a atualizar (parcial)
     * 
     * PATCH = atualização parcial
     * 
     * Exemplo:
     * {
     *   "name": "Novo Nome"  // Só atualiza nome, mantém resto
     * }
     */
    @PatchMapping("/{id}")
    public ResponseEntity<EntityDTO> partialUpdate(
            @PathVariable Long id,
            @RequestBody UpdateEntityRequest request) {
        
        log.info("PATCH /api/entities/{} - {}", id, request);
        
        Entity updated = entityService.partialUpdate(id, request);
        EntityDTO dto = convertToDTO(updated);
        
        return ResponseEntity.ok(dto);
    }

    // ═══════════════════════════════════════════════════════════
    // ENDPOINTS DE DELEÇÃO (DELETE)
    // ═══════════════════════════════════════════════════════════

    /**
     * Deleta entidade.
     * 
     * URL: DELETE /api/entities/{id}
     * 
     * Resposta:
     * - 204 No Content (sucesso, sem corpo na resposta)
     * - 404 Not Found (se não encontrado)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/entities/{}", id);
        
        entityService.delete(id);
        
        // 204 No Content = sucesso sem resposta
        return ResponseEntity.noContent().build();
    }

    /**
     * Soft delete (desativa ao invés de deletar).
     * 
     * URL: DELETE /api/entities/{id}/deactivate
     * 
     * Útil quando não quer deletar fisicamente o registro.
     */
    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<EntityDTO> deactivate(@PathVariable Long id) {
        log.info("DELETE /api/entities/{}/deactivate", id);
        
        Entity deactivated = entityService.deactivate(id);
        EntityDTO dto = convertToDTO(deactivated);
        
        return ResponseEntity.ok(dto);
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES (CONVERSÃO DTO <-> ENTITY)
    // ═══════════════════════════════════════════════════════════

    /**
     * Converte Entity -> DTO (para resposta).
     * 
     * IMPORTANTE:
     * - Nunca retornar entidades JPA diretamente!
     * - DTOs evitam lazy loading exceptions
     * - DTOs controlam exatamente o que é exposto na API
     */
    private EntityDTO convertToDTO(Entity entity) {
        return EntityDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .value(entity.getValue())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                // Relacionamentos: só IDs ou campos essenciais
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .build();
    }

    /**
     * Converte DTO Request -> Entity (para criação).
     */
    private Entity convertToEntity(CreateEntityRequest request) {
        return Entity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .value(request.getValue())
                .build();
    }

    /**
     * Converte DTO Request -> Entity (para atualização).
     */
    private Entity convertToEntity(UpdateEntityRequest request) {
        return Entity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .value(request.getValue())
                .active(request.getActive())
                .build();
    }
}

/**
 * ═══════════════════════════════════════════════════════════
 * DICAS E BOAS PRÁTICAS
 * ═══════════════════════════════════════════════════════════
 * 
 * 1. MÉTODOS HTTP:
 *    - GET: buscar dados (não modifica)
 *    - POST: criar novo recurso
 *    - PUT: substituir recurso completo
 *    - PATCH: atualizar parcialmente
 *    - DELETE: remover recurso
 * 
 * 2. CÓDIGOS DE STATUS HTTP:
 *    - 200 OK: sucesso com resposta
 *    - 201 Created: criação bem-sucedida
 *    - 204 No Content: sucesso sem resposta
 *    - 400 Bad Request: dados inválidos
 *    - 404 Not Found: recurso não encontrado
 *    - 500 Internal Server Error: erro no servidor
 * 
 * 3. VALIDAÇÃO:
 *    - Use @Valid para ativar Bean Validation
 *    - Validações simples: DTOs com annotations
 *    - Validações complexas: service layer
 * 
 * 4. LOGGING:
 *    - Log INFO: operações importantes
 *    - Log DEBUG: detalhes para debug
 *    - Log ERROR: erros e exceções
 *    - Não logar dados sensíveis (senhas, tokens)
 * 
 * 5. RESPONSABILIDADE DO CONTROLLER:
 *    - Receber request
 *    - Validar entrada básica
 *    - Chamar service
 *    - Converter response
 *    - Retornar HTTP
 *    - NÃO ter lógica de negócio!
 * 
 * 6. PAGINAÇÃO:
 *    - Sempre paginar listagens grandes
 *    - Use Pageable do Spring Data
 *    - Cliente controla page, size e sort
 * 
 * 7. DTOs vs ENTITIES:
 *    - Request DTO: dados que cliente envia
 *    - Response DTO: dados que enviamos ao cliente
 *    - Entity: NUNCA expor diretamente!
 * 
 * ═══════════════════════════════════════════════════════════
 */
