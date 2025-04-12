package proyecto.dh.resources.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.ProductDTO;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.entity.ProductCategory;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProductSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public List<ProductDTO> searchProducts(String searchText, Long categoryId) throws NotFoundException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);
        Join<Product, ProductCategory> category = product.join("category", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (searchText != null && !searchText.isEmpty()) {
            String normalizedSearchText = Normalizer.normalize(searchText, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .replaceAll("\\s", "");

            Predicate namePredicate = cb.like(
                    cb.function("unaccent", String.class, cb.lower(product.get("name"))),
                    "%" + normalizedSearchText.toLowerCase() + "%"
            );
            Predicate descriptionPredicate = cb.like(
                    cb.function("unaccent", String.class, cb.lower(product.get("description"))),
                    "%" + normalizedSearchText.toLowerCase() + "%"
            );
            Predicate categoryNamePredicate = cb.like(
                    cb.function("unaccent", String.class, cb.lower(category.get("name"))),
                    "%" + normalizedSearchText.toLowerCase() + "%"
            );

            predicates.add(cb.or(namePredicate, descriptionPredicate, categoryNamePredicate));
        }

        if (categoryId != null) {
            if (!productCategoryRepository.existsById(categoryId)) {
                throw new NotFoundException("Categoría no encontrada");
            }
            Predicate categoryPredicate = cb.equal(
                    product.get("category").get("id"),
                    categoryId
            );
            predicates.add(categoryPredicate);
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Product> results = entityManager.createQuery(query).getResultList();
        return results.stream()
                .map(productEntity -> modelMapper.map(productEntity, ProductDTO.class))
                .collect(Collectors.toList());
    }

    public List<String> findSuggestionsByPartialName(String partialName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Product> product = query.from(Product.class);
        Join<Product, ProductCategory> category = product.join("category", JoinType.LEFT);

        String normalizedPartialName = Normalizer.normalize(partialName, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("\\s", "");

        Predicate namePredicate = cb.like(
                cb.function("unaccent", String.class, cb.lower(product.get("name"))),
                "%" + normalizedPartialName.toLowerCase() + "%"
        );
        Predicate descriptionPredicate = cb.like(
                cb.function("unaccent", String.class, cb.lower(product.get("description"))),
                "%" + normalizedPartialName.toLowerCase() + "%"
        );
        Predicate categoryNamePredicate = cb.like(
                cb.function("unaccent", String.class, cb.lower(category.get("name"))),
                "%" + normalizedPartialName.toLowerCase() + "%"
        );

        query.multiselect(
                product.get("name"),
                product.get("description"),
                category.get("name")
        ).where(cb.or(namePredicate, descriptionPredicate, categoryNamePredicate));

        List<Object[]> results = entityManager.createQuery(query).getResultList();

        Set<String> suggestions = new HashSet<>();
        for (Object[] result : results) {
            suggestions.addAll(extractWords((String) result[0], normalizedPartialName));
            suggestions.addAll(extractWords((String) result[1], normalizedPartialName));
            suggestions.addAll(extractWords((String) result[2], normalizedPartialName));
        }

        return new ArrayList<>(suggestions);
    }

    private Set<String> extractWords(String text, String normalizedPartialName) {
        if (text == null || text.isEmpty()) {
            return Collections.emptySet();
        }

        return Arrays.stream(text.split("\\s+"))
                .map(word -> Normalizer.normalize(word, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                        .replaceAll("\\s", ""))
                .filter(word -> word.toLowerCase().contains(normalizedPartialName.toLowerCase()))
                .collect(Collectors.toSet());
    }
}
