package com.PIMCS.PIMCS;

import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TestUtils {
//    @Autowired
//    CompanyRepository companyRepository;


    public Mat createMatObject(Product product, Company company){
        Mat mat = new Mat();
        mat.setSerialNumber("serial_" + UUID.randomUUID().toString());
//        mat.setSerialNumber("1642661076618");
        mat.setProduct(product);
        mat.setCompany(company);
        mat.setCalcMethod(1);
        mat.setThreshold(5);
        mat.setInventoryWeight(0);
        mat.setRecentlyNoticeDate(new Timestamp(System.currentTimeMillis()));
        mat.setIsSendEmail(0);
        mat.setMatLocation("화상실");
        mat.setProductOrderCnt(2);
        mat.setBoxWeight(5);
        mat.setBattery((int) (Math.random()*(100-0)) + 0);
        return mat;
    }

    public Product createProductObject(Company company, ProductCategory productCategory){

            Product product = new Product();
            product.setProductCode("code_" + System.currentTimeMillis());
            product.setCompany(company);
            product.setProductCategory(productCategory);
            product.setProductImage(null);
            product.setProductWeight(10);
            product.setProductName("컴퓨터"+System.currentTimeMillis());
            return product;

        }

    public ProductCategory createProductCategory(Company company){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName("컴퓨터");
        productCategory.setCompany(company);
        return productCategory;
    }

    public List<Role> createRoleObject(){

        List<Role> roles = new ArrayList<>();
        Role role1 = new Role();
        role1.setName("User");

        Role role2 = new Role();
        role2.setName("UserManagement");

        Role role3 = new Role();
        role3.setName("MatManagement");

        roles.add(role1);
        roles.add(role2);
        roles.add(role3);
        return roles;
    }


    public Company createCompantObject(){
        Company company = new Company();
        company.setCompanyCode(System.currentTimeMillis()+"");
        company.setCompanyName("sw");
        company.setCompanyAddress("서울시");
        company.setContactPhone("010xxxx");
        company.setCeoEmail("2543817958@qq.com");
        return company;
    }

    public User createUserObject(Company company){
        User user = new User();
        user.setEmail("ryongho1997@gmail.com");
        user.setCompany(company);
        user.setPassword("1234");
        user.setName("name " + System.currentTimeMillis());
        user.setDepartment("컴퓨터");
        user.setEnabled(true);
        return user;
    }

    public UserRole createUserRoleObject(User user, Role role){
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        return userRole;
    }
}
