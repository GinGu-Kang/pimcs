package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.TestUtils;
import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class MatServiceIntegrationTest {

    @Autowired
    MatRepository matRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    MatService matService;
    @Autowired
    ProductService productService;


//
//
//    TestUtils testUtils = new TestUtils();
//
//
//    @Test
//    public void creatMat(){
//        /**
//         *  1. 매트 정상등록 되었는지 검증(O)
//         *  2. 생상된 매트인지 체크(X)
//         *  3. 매트 중복체크(O)
//         */
//
//        //given
//        Company company = testUtils.createCompantObject();
//        Product product = testUtils.createProductObject(company);
//        Mat mat = testUtils.createMatObject(product);
//
//        //when
//        companyRepository.save(company);
//        productService.createProduct(product);
//        String serialNum = matService.createMat(mat);
//
//        //then
//
//        //정상등록 되었는 검증
//        Optional<Mat> matOpt = matRepository.findBySerialNumber(serialNum);
//        if(!matOpt.isPresent()) Assertions.fail("정상등록되 않았습니다.");
//
//    }
//
//    @Test
//    public void readMatTest(){
//        List<Mat> mats = matService.readMat();
//        mats.forEach(mat -> {
//            System.out.println(
//                    "serialNum: " + mat.getSerialNumber() +", "+
//                    "상품명: " +mat.getProduct()
//            );
//        });
//    }
//
//
//    @Test
//    public void updateMatTest(){
//        /**
//         * 1. 정상수정 되어있는지 검증(O)
//         */
//
//        //given
//        List<Mat> matList = matRepository.findAll();
//        if(matList.isEmpty()) Assertions.fail("테이블에 데이터가 없습니다.");
//        //배터리 정보 100으로 수정
//        Mat mat = matList.get(0);
//        mat.setBattery(100);
//
//        //when
//        String serialNum = matService.updateMat(mat);
//
//        //then
//        //정상 수정되었는지 체크
//        Optional<Mat> matOpt = matRepository.findBySerialNumber(serialNum);
//        matOpt.ifPresent(mat1 -> {
//            Assertions.assertThat(mat1.getBattery()).isEqualTo(mat.getBattery());
//        });
//
//    }
//
//    @Test
//    public void checkMatSerialNumberServiceTest(){
//        /**
//         *  case 1. 입력으로 테이블에 존재하지 넣었을때 result true인지 체크(O)
//         *  case 2. 입력으로 테이블에 존재하는 넣었을때 result false인지 체크(O)
//         */
//        //given
//        List<Mat> matList = matRepository.findAll();
//        if(matList.isEmpty()) Assertions.fail("테이블에 데이터가 없습니다.");
//        Mat mat = matList.get(0);
//
//        //when & then
//        //case 1
//        Assertions.assertThat(
//                (boolean)matService.checkMatSerialNumberService("abcdjiofjo").get("result")
//        ).isEqualTo(true);
//        //case 2
//        Assertions.assertThat(
//                (boolean)matService.checkMatSerialNumberService(mat.getSerialNumber()).get("result")
//        ).isEqualTo(false);
//
//
//
//    }
//
//    @Test
//    public void deleteMatTest(){
//        /**
//         * 1. 매트정상삭제 되는지
//         */
//        //given
//        List<Mat> matList = matRepository.findAll();
//        if(matList.isEmpty()) Assertions.fail("테이블에 데이터가 없습니다.");
//        Mat mat = matList.get(0);
//
//        //when
//        String serialNum = matService.deleteMat(mat);
//        //then
//        Optional<Mat> findMat = matRepository.findBySerialNumber(serialNum);
//        if(findMat.isPresent()){
//            Assertions.fail("삭제실패");
//        }
//
//
//    }
//
//    @Test
//    public void searchMat(){
//        /**
//         * 검색(serialNumber,matLocation,productCode,matVersion) 서비스
//         * case 1: 시리얼번호 (w1%, %23%) 사이즈 2개
//         *
//         */
//
//
//        //given
//        //시리얼번호
//        SearchForm searchSerialNumber1 = new SearchForm("serialNumber","w1");
//        SearchForm searchSerialNumber2 = new SearchForm("serialNumber","23");
//
//
//        //매트위치
//        SearchForm locSearchForm1 = new SearchForm("matLocation","창고");
//        SearchForm locSearchForm2 = new SearchForm("matLocation","화상실");
//        //상품코드
//        SearchForm prodCodeForm1 = new SearchForm("productCode","1642661076618");
//
//
//        //when
//        //시리얼번호
//        Assertions.assertThat(matService.searchMat(searchSerialNumber1).size()).isEqualTo(2);
//        Assertions.assertThat(matService.searchMat(searchSerialNumber2).size()).isEqualTo(2);
//
//        //매트위치
//        Assertions.assertThat(matService.searchMat(locSearchForm1).size()).isEqualTo(4);
//        Assertions.assertThat(matService.searchMat(locSearchForm2).size()).isEqualTo(1);
//        //상품코드
//        Assertions.assertThat(matService.searchMat(prodCodeForm1).size()).isEqualTo(1);
//
//
//
//    }
//
//

    @Test
    @Commit
    public void change(){
        User user = userRepository.findByEmail("ryongho1997@gmail.com").get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); //비밀번호 암호화
        user.setPassword(encoder.encode("1234"));
    }


    @Test
    @Commit
    public void preparePaginationData(){
        User user = userRepository.findByEmail("ryongho1997@gmail.com").get();
        Company company = user.getCompany();
        Product product = productRepository.findById(2).get();

        TestUtils testUtils = new TestUtils();

        for(int i=0; i<1317; i++){
            matRepository.save(testUtils.createMatObject(product, company));
        }
    }

    @Test
    @Commit
    public void initMatTest(){
        /**
         *  1. 회사 생성
         *  2. 사용자 생성
         *  3. 권한 생성
         *  4. 사용자 권한 생성
         *  5. 상품 카테고리 생성
         *  6. 상품 생성
         *  7. 매트 생성
         */
        TestUtils testUtils = new TestUtils();

        Company company = testUtils.createCompantObject();
        companyRepository.save(company);

        User user = testUtils.createUserObject(company);
        userRepository.save(user);

        List<Role> roles = roleRepository.findAll();
        for(Role role : roles){
//            roleRepository.save(role);
            UserRole userRole = testUtils.createUserRoleObject(user,role);
            userRoleRepository.save(userRole);
        }

//        ProductCategory productCategory = testUtils.createProductCategory(company);
//        productCategoryRepository.save(productCategory);
//
//        Product product = testUtils.createProductObject(company,productCategory);
//        productRepository.save(product);

    }
}
