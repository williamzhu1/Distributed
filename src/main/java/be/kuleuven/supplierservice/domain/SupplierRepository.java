package be.kuleuven.supplierservice.domain;

import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class SupplierRepository {
    private Supplier self;

    @PostConstruct
    public void initData(){
        Supplier supplier = new Supplier("sRZJMHGkZDUMReAXigb9","Nike","https://firebasestorage.googleapis.com/v0/b/dapp2024-service.appspot.com/o/nike_logo.webp?alt=media&token=1a682680-8336-43a1-9720-63990d37d8a6");
        this.self = supplier;
    }
}
