package proyecto.dh.resources.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import proyecto.dh.resources.attachment.entity.Attachment;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "categories")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(nullable = false, unique = true)
    private String slug;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<Product> products;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "categories_features", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "feature_id"))
    private Set<CategoryFeature> categoryFeatures = new LinkedHashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "categories_policies", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "policy_id"))
    private Set<CategoryPolicy> categoryPolicies = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;
}
