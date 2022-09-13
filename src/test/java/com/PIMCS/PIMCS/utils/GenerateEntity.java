package com.PIMCS.PIMCS.utils;

import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.repository.*;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class GenerateEntity {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private BusinessCategoryRepository businessCategoryRepository;

    @Autowired
    private MatCategoryRepository matCategoryRepository;

    public MatCategory createMatCategory(String serialNumber,boolean isSave){
        Random random = new Random();
        MatCategory matCategory = new MatCategory();
        matCategory.setMatCategoryName(UUID.randomUUID().toString().substring(0,30));
        matCategory.setMappingSerialCode((serialNumber != null) ? serialNumber : UUID.randomUUID().toString().substring(0,30));
        matCategory.setMatPrice(random.nextInt(30));
        matCategory.setMatInformation(UUID.randomUUID().toString());
        matCategory.setMaxWeight(random.nextInt(50));
        if(isSave) matCategoryRepository.save(matCategory);
        return matCategory;
    }


    public BusinessCategory createBusinessCategory(boolean isSave){
        BusinessCategory businessCategory = new BusinessCategory();
        businessCategory.setCategoryName(UUID.randomUUID().toString().substring(0,20));
        if(isSave) businessCategoryRepository.save(businessCategory);
        return businessCategory;
    }

    public Company createCompany(BusinessCategory businessCategory, boolean isSave){

        Company company = new Company();
        company.setCompanyCode(String.valueOf(UUID.randomUUID()).substring(0,29));
        company.setBusinessCategoryId((businessCategory != null) ? businessCategory : createBusinessCategory(isSave));
        company.setCompanyName(String.valueOf(UUID.randomUUID()));
        company.setCompanyAddress("abcd");
        company.setContactPhone("1234");
        company.setCeoEmail("test@pimcs.com");
        company.setCeoName("jsiojio");
        if(isSave) companyRepository.save(company);

        return company;
    }

    public ProductCategory createProductCategory(Company company, boolean isSave){

        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName(UUID.randomUUID().toString().substring(0,30));
        if(company != null)
            productCategory.setCompany(company);
        else
            productCategory.setCompany(createCompany(null,isSave));

        if(isSave) productCategoryRepository.save(productCategory);
        return productCategory;
    }


    public User createUser(Company company, boolean isSave){
        User user = new User();
        user.setEmail(UUID.randomUUID()+"@pimcs.com");
        user.setCompany((company != null) ? company : createCompany(null,isSave));
        user.setPassword(String.valueOf(UUID.randomUUID()));
        user.setName("test");
        user.setPhone("1234");
        user.setDepartment("sw");
        user.setEnabled(true);
        if(isSave) userRepository.save(user);

        return user;
    }


    public List<UserRole> createUserRoles(User user){
        List<UserRole> userRoleList = new ArrayList<>();

        for (Role role:roleRepository.findAll()
        ) {
            if(!role.getName().equals("ChiefOfPimcs")){
                UserRole userRole=new UserRole();
                userRole.setUser(user);
                userRole.setRole(role);
                userRoleList.add(userRole);
                /*userRoleRepository.save(userRole);*/
            }
        }
        userRoleRepository.saveAll(userRoleList);
        return userRoleList;

    }


    public Product createProduct(Company company,  boolean isSave){

        Product product = new Product();
        product.setProductCode(String.valueOf(UUID.randomUUID()).substring(0,30));
        product.setProductCategory(null);
        product.setCompany((company == null) ? createCompany(null,isSave) : company);
        product.setProductImage(null);
        product.setProductWeight(2);
        product.setProductName(String.valueOf(UUID.randomUUID()).substring(0,30));

        if(isSave) productRepository.save(product);

        return product;

    }
}
