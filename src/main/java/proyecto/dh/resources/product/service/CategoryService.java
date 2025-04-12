package proyecto.dh.resources.product.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.product.dto.*;
import proyecto.dh.resources.product.entity.CategoryFeature;
import proyecto.dh.resources.product.entity.CategoryPolicy;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.repository.CategoryFeatureRepository;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private CategoryFeatureRepository featureRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public CategoryDTO save(CategorySaveDTO categorySaveDTO) throws BadRequestException, NotFoundException {
        validateSlug(categorySaveDTO.getSlug());
        checkCategoryExistence(categorySaveDTO.getName(), categorySaveDTO.getSlug());

        ProductCategory category = convertToEntity(categorySaveDTO);

        setCategoryFeatures(category, categorySaveDTO.getFeatures());
        setCategoryPolicies(category, categorySaveDTO.getPolicies());
        handleAttachment(category, categorySaveDTO.getAttachmentId());

        ProductCategory savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategorySaveDTO categorySaveDTO) throws NotFoundException, BadRequestException {
        ProductCategory existingCategory = findByIdEntity(id).orElseThrow(() -> new NotFoundException("Categoría con ID " + id + " no encontrada."));
        validateSlug(categorySaveDTO.getSlug());
        checkCategoryExistenceForUpdate(categorySaveDTO.getName(), categorySaveDTO.getSlug(), existingCategory);

        updateFeatures(existingCategory, categorySaveDTO.getFeatures());
        updatePolicies(existingCategory, categorySaveDTO.getPolicies());

        modelMapper.map(categorySaveDTO, existingCategory);
        handleAttachment(existingCategory, categorySaveDTO.getAttachmentId());

        ProductCategory savedCategory = categoryRepository.save(existingCategory);
        return convertToDTO(savedCategory);
    }

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) throws BadRequestException, NotFoundException {
        ProductCategory category = findByIdEntity(id).orElseThrow(() -> new NotFoundException("Categoría con ID " + id + " no encontrada."));

        // Verificar si la categoría tiene productos asociados
        if (categoryRepository.existsProductsByCategoryId(id)) {
            throw new BadRequestException("No se puede eliminar la categoría con ID " + id + " porque tiene productos asociados.");
        }

        categoryRepository.deleteById(id);
    }

    public CategoryDTO findById(Long id) throws NotFoundException {
        ProductCategory category = findByIdEntity(id).orElseThrow(() -> new NotFoundException("Categoría con ID " + id + " no encontrada."));
        return convertToDTO(category);
    }

    private Optional<ProductCategory> findByIdEntity(Long id) {
        return categoryRepository.findById(id);
    }

    private void validateSlug(String slug) throws BadRequestException {
        if (slug == null || !slug.equals(slug.toLowerCase()) || slug.contains(" ")) {
            throw new BadRequestException("El slug debe estar en minúsculas y no debe contener espacios");
        }
    }

    private CategoryDTO convertToDTO(ProductCategory category) {
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        if (category.getCategoryFeatures() != null) {
            List<CategoryFeatureDTO> features = category.getCategoryFeatures().stream().map(feature -> modelMapper.map(feature, CategoryFeatureDTO.class)).collect(Collectors.toList());
            categoryDTO.setFeatures(features);
        }

        if (category.getCategoryPolicies() != null) {
            List<CategoryPolicyDTO> policies = category.getCategoryPolicies().stream().map(policy -> modelMapper.map(policy, CategoryPolicyDTO.class)).collect(Collectors.toList());
            categoryDTO.setPolicies(policies);
        }

        return categoryDTO;

    }

    private ProductCategory convertToEntity(CategorySaveDTO categorySaveDTO) {
        return modelMapper.map(categorySaveDTO, ProductCategory.class);
    }

    private void checkCategoryExistence(String name, String slug) throws BadRequestException {
        if (categoryRepository.existsByName(name)) {
            throw new BadRequestException("Categoría con el nombre '" + name + "' ya existe");
        }
        if (categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Categoría con el slug '" + slug + "' ya existe");
        }
    }

    private void checkCategoryExistenceForUpdate(String name, String slug, ProductCategory existingCategory) throws BadRequestException {
        if (name != null && categoryRepository.existsByName(name) && !existingCategory.getName().equals(name)) {
            throw new BadRequestException("Categoría con el nombre '" + name + "' ya existe");
        }
        if (slug != null && categoryRepository.existsBySlug(slug) && !existingCategory.getSlug().equals(slug)) {
            throw new BadRequestException("Categoría con el slug '" + slug + "' ya existe");
        }
    }

    private void handleAttachment(ProductCategory category, Long attachmentId) throws NotFoundException, BadRequestException {
        if (attachmentId != null) {
            Attachment attachment = attachmentService.findById(attachmentId);
            attachmentService.validateFileTypeImages(attachment);
            category.setAttachment(attachment);
        }
    }


/*
    private void setCategoryFeatures(ProductCategory category, List<CategoryFeatureSaveDTO> features) {

        // Se añade para manejar los valores nulos
        Set<CategoryFeature> featureSet = Optional.ofNullable(features).orElseGet(Collections::emptyList).stream().map(featureSaveDTO -> modelMapper.map(featureSaveDTO, CategoryFeature.class)).peek(feature -> {
            if (feature.getCategories() == null) {
                feature.setCategories(Set.of(category));
            } else {
                feature.getCategories().add(category);
            }
        }).collect(Collectors.toSet());

        category.setCategoryFeatures(featureSet);
    }
*/

    private void setCategoryFeatures(ProductCategory category, List<CategoryFeatureSaveDTO> featureDTOs) {
        Set<CategoryFeature> featureSet = Optional.ofNullable(featureDTOs)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(featureDTO -> {
                    CategoryFeature feature = modelMapper.map(featureDTO, CategoryFeature.class);
                    feature.setCategories(new HashSet<>(Collections.singletonList(category)));
                    return feature;
                })
                .collect(Collectors.toSet());

        category.setCategoryFeatures(featureSet);
    }

    private void setCategoryPolicies(ProductCategory category, List<CategoryPolicySaveDTO> policyDTOs) {
        Set<CategoryPolicy> policySet = Optional.ofNullable(policyDTOs)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(policyDTO -> {
                    CategoryPolicy policy = modelMapper.map(policyDTO, CategoryPolicy.class);
                    policy.setCategories(new HashSet<>(Collections.singletonList(category)));
                    return policy;
                })
                .collect(Collectors.toSet());

        category.setCategoryPolicies(policySet);
    }

    private void updateFeatures(ProductCategory category, List<CategoryFeatureSaveDTO> features) throws BadRequestException {
        // Obtener o inicializar el conjunto de características actuales de la categoría
        Set<CategoryFeature> currentFeatures = category.getCategoryFeatures();
        if (currentFeatures == null) {
            currentFeatures = new HashSet<>();
            category.setCategoryFeatures(currentFeatures);
        }

        // Mapa para almacenar las características existentes por nombre para búsqueda eficiente
        Map<String, CategoryFeature> existingFeatureMap = currentFeatures.stream().collect(Collectors.toMap(CategoryFeature::getName, f -> f));

        // Validar si hay características con nombres duplicados en la lista de entrada
        Set<String> duplicateNames = features.stream().collect(Collectors.groupingBy(CategoryFeatureSaveDTO::getName, Collectors.counting())).entrySet().stream().filter(entry -> entry.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toSet());

        if (!duplicateNames.isEmpty()) {
            throw new BadRequestException("Existen características con nombres duplicados: " + duplicateNames);
        }

        // Lista para almacenar las nuevas características que se agregarán a la categoría
        List<CategoryFeature> newFeaturesToAdd = new ArrayList<>();

        // Procesar cada DTO de características en la lista proporcionada
        for (CategoryFeatureSaveDTO featureDTO : features) {
            // Buscar la característica existente por su nombre en el mapa
            CategoryFeature existingFeature = existingFeatureMap.get(featureDTO.getName());

            if (existingFeature != null) {
                // Si la característica existe, actualizar sus campos con los valores del DTO
                existingFeature.setDescription(featureDTO.getDescription());
                existingFeature.setIcon(featureDTO.getIcon());
            } else {
                // Si la característica no existe, crear una nueva instancia y configurarla
                CategoryFeature newFeature = modelMapper.map(featureDTO, CategoryFeature.class);
                newFeature.setCategories(new HashSet<>(Collections.singletonList(category))); // Asignar categoría
                newFeaturesToAdd.add(newFeature); // Agregar la nueva característica a la lista de añadidos
            }
        }

        // Eliminar las características que no están presentes en la lista de DTOs
        currentFeatures.removeIf(f -> features.stream().noneMatch(fDTO -> fDTO.getName().equals(f.getName())));

        // Agregar las nuevas características a la categoría
        currentFeatures.addAll(newFeaturesToAdd);
    }

    private void updatePolicies(ProductCategory category, List<CategoryPolicySaveDTO> policyDTOs) throws BadRequestException {
        // Obtener o inicializar el conjunto de políticas actuales de la categoría
        Set<CategoryPolicy> currentPolicies = Optional.ofNullable(category.getCategoryPolicies())
                .orElseGet(HashSet::new);

        // Crear un mapa para almacenar las políticas existentes por título para una búsqueda eficiente
        Map<String, CategoryPolicy> existingPolicyMap = currentPolicies.stream()
                .collect(Collectors.toMap(CategoryPolicy::getTitle, policy -> policy));

        // Validar si hay políticas con títulos duplicados en la lista de entrada
        Set<String> duplicateTitles = policyDTOs.stream()
                .collect(Collectors.groupingBy(CategoryPolicySaveDTO::getTitle, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // Si hay títulos duplicados, lanzar una excepción de solicitud incorrecta
        if (!duplicateTitles.isEmpty()) {
            throw new BadRequestException("Existen políticas con títulos duplicados: " + duplicateTitles);
        }

        // Lista para almacenar las nuevas políticas que se agregarán a la categoría
        List<CategoryPolicy> newPoliciesToAdd = new ArrayList<>();

        // Procesar cada DTO de políticas en la lista proporcionada
        for (CategoryPolicySaveDTO policyDTO : policyDTOs) {
            // Buscar la política existente por su título en el mapa
            CategoryPolicy existingPolicy = existingPolicyMap.get(policyDTO.getTitle());

            if (existingPolicy != null) {
                // Si la política existe, actualizar sus campos con los valores del DTO
                existingPolicy.setDescription(policyDTO.getDescription());
            } else {
                // Si la política no existe, crear una nueva instancia y configurarla
                CategoryPolicy newPolicy = modelMapper.map(policyDTO, CategoryPolicy.class);
                newPolicy.setCategories(new HashSet<>(Collections.singletonList(category))); // Asignar categoría
                newPoliciesToAdd.add(newPolicy); // Agregar la nueva política a la lista de añadidos
            }
        }

        // Eliminar las políticas que no están presentes en la lista de DTOs
        currentPolicies.removeIf(policy -> policyDTOs.stream().noneMatch(dto -> dto.getTitle().equals(policy.getTitle())));

        // Agregar las nuevas políticas a la categoría
        currentPolicies.addAll(newPoliciesToAdd);

        // Asignar el conjunto actualizado de políticas a la categoría
        category.setCategoryPolicies(currentPolicies);
    }

}