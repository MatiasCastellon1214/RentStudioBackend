package proyecto.dh.resources.product.service;

import com.amazonaws.services.kms.model.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import proyecto.dh.resources.product.dto.CategoryFeatureDTO;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.entity.CategoryFeature;
import proyecto.dh.resources.product.repository.CategoryFeatureRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeatureService {

    private final CategoryFeatureRepository featureRepository;
    private final ModelMapper modelMapper;

    public FeatureService(CategoryFeatureRepository featureRepository, ModelMapper modelMapper) {
        this.featureRepository = featureRepository;
        this.modelMapper = modelMapper;
    }

    public List<CategoryFeatureDTO> findAll() {
        return featureRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public CategoryFeatureDTO findById(Long id) throws NotFoundException {
        CategoryFeature feature = findByIdEntity(id).orElseThrow(() -> new NotFoundException("Caracteristica con ID " + id + " no encontrada"));
        return convertToDTO(feature);
    }

    private Optional<CategoryFeature> findByIdEntity(Long id) {
        return featureRepository.findById(id);
    }


    private CategoryFeatureDTO convertToDTO(CategoryFeature feature) {
        CategoryFeatureDTO featureDTO = modelMapper.map(feature, CategoryFeatureDTO.class);
        featureDTO.setCategoryIds(feature.getCategories().stream().map(ProductCategory::getId).collect(Collectors.toList()));
        return featureDTO;
    }
}
