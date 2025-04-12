package proyecto.dh.resources.product.service;

import com.amazonaws.services.kms.model.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import proyecto.dh.resources.product.dto.CategoryPolicyDTO;
import proyecto.dh.resources.product.entity.CategoryPolicy;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.repository.CategoryPolicyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private final CategoryPolicyRepository policyRepository;
    private final ModelMapper modelMapper;

    public PolicyService(CategoryPolicyRepository policyRepository, ModelMapper modelMapper) {
        this.policyRepository = policyRepository;
        this.modelMapper = modelMapper;
    }

    public List<CategoryPolicyDTO> findAll() {
        return policyRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public CategoryPolicyDTO findById(Long id) throws NotFoundException {
        CategoryPolicy policy = findByIdEntity(id).orElseThrow(() -> new NotFoundException("Política con ID " + id + " no encontrada"));
        return convertToDTO(policy);
    }

    private Optional<CategoryPolicy> findByIdEntity(Long id) {
        return policyRepository.findById(id);
    }

    private CategoryPolicyDTO convertToDTO(CategoryPolicy policy) {
        CategoryPolicyDTO policyDTO = modelMapper.map(policy, CategoryPolicyDTO.class);
        policyDTO.setCategoryIds(policy.getCategories().stream().map(ProductCategory::getId).collect(Collectors.toList()));
        return policyDTO;
    }
}
