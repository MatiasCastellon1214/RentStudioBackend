package proyecto.dh.resources.product.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;
import proyecto.dh.resources.favorite.dto.ProductFavoriteSaveDTO;
import proyecto.dh.resources.favorite.entity.ProductFavorite;
import proyecto.dh.resources.favorite.service.FavoriteService;
import proyecto.dh.resources.product.dto.AvailabilityDTO;
import proyecto.dh.resources.product.dto.CategoryFeatureDTO;
import proyecto.dh.resources.product.dto.CategoryPolicyDTO;
import proyecto.dh.resources.product.dto.ProductDTO;
import proyecto.dh.resources.product.dto.ProductSaveDTO;
import proyecto.dh.resources.product.dto.ProductUpdateDTO;
import proyecto.dh.resources.product.entity.CategoryFeature;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.repository.CategoryFeatureRepository;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;
import proyecto.dh.resources.product.repository.ProductRepository;
import proyecto.dh.resources.product.repository.ProductSearchRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final CategoryFeatureRepository featureRepository;
    private final AttachmentService attachmentService;
    private final FavoriteService favoriteService;
    private final ModelMapper modelMapper;
    private final ProductSearchRepository productSearchRepository;

    public ProductService(ProductRepository productRepository, ProductCategoryRepository categoryRepository, CategoryFeatureRepository featureRepository, AttachmentService attachmentService, FavoriteService favoriteService, ModelMapper modelMapper, ProductSearchRepository productSearchRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.featureRepository = featureRepository;
        this.attachmentService = attachmentService;
        this.favoriteService = favoriteService;
        this.modelMapper = modelMapper;
        this.productSearchRepository = productSearchRepository;
    }

    /**
     * Guarda un nuevo producto.
     *
     * @param productSaveDTO DTO con la información del producto a guardar.
     * @return El producto guardado convertido a DTO.
     * @throws NotFoundException   Si no se encuentra la categoría.
     * @throws BadRequestException Si el nombre del producto ya existe.
     */
    @Transactional
    public ProductDTO save(ProductSaveDTO productSaveDTO) throws NotFoundException, BadRequestException {
        if (productRepository.existsByName(productSaveDTO.getName())) {
            throw new BadRequestException("Producto con nombre '" + productSaveDTO.getName() + "' ya existe");
        }
        Product product = convertToEntity(productSaveDTO);
        setProductCategory(product, productSaveDTO.getCategoryId());
        setProductFeatures(product, productSaveDTO.getFeatureIds());

        setProductAttachments(product, productSaveDTO.getAttachments());
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    /**
     * Actualiza un producto existente.
     *
     * @param id               ID del producto a actualizar.
     * @param productUpdateDTO DTO con la información del producto a actualizar.
     * @return El producto actualizado convertido a DTO.
     * @throws NotFoundException   Si no se encuentra el producto o la categoría.
     * @throws BadRequestException Si hay algún problema con las características o los archivos adjuntos.
     */
    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO) throws NotFoundException, BadRequestException {
        Product existingProduct = findByIdEntity(id);
        modelMapper.map(productUpdateDTO, existingProduct);

        updateCategory(existingProduct, productUpdateDTO.getCategoryId());
        updateFeatures(existingProduct, productUpdateDTO.getFeatureIds());
        updateAttachments(existingProduct, productUpdateDTO.getAttachmentsIds());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    /**
     * Elimina un producto por su ID.
     *
     * @param id ID del producto a eliminar.
     * @throws NotFoundException Si no se encuentra el producto.
     */
    @Transactional
    public void delete(Long id) throws NotFoundException {
        Product findProduct = findByIdEntity(id);
        attachmentService.deleteAttachmentsByEntities(findProduct.getAttachments());
        productRepository.delete(findProduct);
    }

    @Transactional(readOnly = true)
    public AvailabilityDTO getProductAvailability(Long productId) throws NotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        availabilityDTO.setProductId(product.getId());

        if (product.getStock() <= 1) {
            List<AvailabilityDTO.DateRange> occupiedDateRanges = product.getReservations().stream()
                    .map(reservation -> {
                        AvailabilityDTO.DateRange dateRange = new AvailabilityDTO.DateRange();
                        dateRange.setStartDate(reservation.getStartDate());
                        dateRange.setEndDate(reservation.getEndDate());
                        return dateRange;
                    })
                    .collect(Collectors.toList());

            availabilityDTO.setOccupiedDates(occupiedDateRanges);
        } else {
            availabilityDTO.setOccupiedDates(new ArrayList<>());
        }

        return availabilityDTO;
    }

    /**
     * Obtiene todos los productos.
     *
     * @return Lista de productos convertidos a DTO.
     */
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Busca un producto por su ID.
     *
     * @param id ID del producto a buscar.
     * @return El producto encontrado convertido a DTO.
     * @throws NotFoundException Si no se encuentra el producto.
     */
    public ProductDTO findById(Long id) throws NotFoundException {
        Product productSearched = findByIdEntity(id);
        return convertToDTO(productSearched);
    }

    /**
     * Busca productos por texto y categoría.
     *
     * @param searchText Texto a buscar.
     * @param categoryId ID de la categoría a buscar.
     * @return Lista de productos encontrados convertidos a DTO.
     * @throws NotFoundException Si no se encuentran productos.
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String searchText, Long categoryId) throws NotFoundException {
        return productSearchRepository.searchProducts(searchText, categoryId);
    }

    /**
     * Obtiene sugerencias de nombres de productos basadas en un nombre parcial.
     *
     * @param partialName Nombre parcial del producto.
     * @return Lista de sugerencias de nombres de productos.
     */
    public List<String> getSuggestions(String partialName) {
        return productSearchRepository.findSuggestionsByPartialName(partialName);
    }

    private void setProductCategory(Product product, Long categoryId) throws NotFoundException {
        ProductCategory category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
        product.setCategory(category);
    }

    private void setProductAttachments(Product product, List<Long> attachmentsIds) throws BadRequestException {
        if (attachmentsIds != null) {
            List<Attachment> attachments = new ArrayList<>();
            for (Long attachmentId : attachmentsIds) {
                Attachment attachment = attachmentService.findById(attachmentId);
                attachmentService.validateFileTypeImages(attachment);
                attachment.setProduct(product);
                attachments.add(attachment);
            }
            product.setAttachments(attachments);
        }
    }

    private void setProductFeatures(Product product, List<Long> featureIds) throws NotFoundException {
        if (featureIds != null) {
            List<CategoryFeature> featureList = featureRepository.findAllById(featureIds);
            Set<CategoryFeature> features = new HashSet<>(featureList);
            product.setFeatures(features);
        }
    }

    private void setProductFavorites(Product product, List<ProductFavoriteSaveDTO> favoriteSaveDtos) {
        // Se añade para manejar los valores nulos
        Set<ProductFavorite> favoriteSet = Optional.ofNullable(favoriteSaveDtos).orElseGet(Collections::emptyList).stream().map(favoriteSaveDTO -> modelMapper.map(favoriteSaveDTO, ProductFavorite.class)).peek(favorite -> {
            if (favorite.getProduct() == null) {
                favorite.setProduct(Set.of(product));
            } else {
                favorite.getProduct().add(product);
            }
        }).collect(Collectors.toSet());

        product.setFavorites(favoriteSet);
    }


    private void updateCategory(Product product, Long categoryId) throws NotFoundException {
        if (categoryId != null) {
            ProductCategory category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
            product.setCategory(category);
        }
    }

    @Transactional
    public void updateAttachments(Product product, List<Long> attachmentsIds) throws BadRequestException {
        if (attachmentsIds != null) {
            for (Attachment attachment : new ArrayList<>(product.getAttachments())) {
                product.removeAttachment(attachment);
            }
            setProductAttachments(product, attachmentsIds);
        }
    }

    private void updateFeatures(Product product, List<Long> featureIds) throws NotFoundException {
        if (featureIds != null) {
            List<CategoryFeature> featureList = featureRepository.findAllById(featureIds);
            Set<CategoryFeature> features = new HashSet<>(featureList);
            product.setFeatures(features);
        }
    }


    private Product findByIdEntity(Long id) throws NotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("El producto no existe."));
    }

    public ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

        if (product.getCategory().getCategoryFeatures() != null) {
            List<CategoryFeatureDTO> features = product.getCategory().getCategoryFeatures().stream().map(feature -> modelMapper.map(feature, CategoryFeatureDTO.class)).collect(Collectors.toList());
            productDTO.getCategory().setFeatures(features);
        }

        if (product.getCategory().getCategoryPolicies() != null) {
            List<CategoryPolicyDTO> policies = product.getCategory().getCategoryPolicies().stream().map(policy -> modelMapper.map(policy, CategoryPolicyDTO.class)).collect(Collectors.toList());
            productDTO.getCategory().setPolicies(policies);
        }

        if (product.getAttachments() != null) {
            List<AttachmentDTO> attachments = product.getAttachments().stream().map(attachment -> modelMapper.map(attachment, AttachmentDTO.class)).collect(Collectors.toList());
            productDTO.setAttachments(attachments);
        }

        if (product.getFeatures() != null) {
            List<CategoryFeatureDTO> features = product.getFeatures().stream().map(feature -> modelMapper.map(feature, CategoryFeatureDTO.class)).collect(Collectors.toList());
            productDTO.setFeatures(features);
        }

        return productDTO;
    }

    public Product convertToEntity(ProductSaveDTO productSaveDTO) {
        return modelMapper.map(productSaveDTO, Product.class);
    }
}
