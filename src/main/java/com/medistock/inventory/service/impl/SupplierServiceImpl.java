package com.medistock.inventory.service.impl;

import com.medistock.inventory.model.Supplier;
import com.medistock.inventory.repository.SupplierRepository;
import com.medistock.inventory.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public Supplier saveSupplier(Supplier supplier) {

        return supplierRepository.save(supplier);
    }

    @Override
    public List<Supplier> getAllSuppliers() {

        return supplierRepository.findAll();
    }

    @Override
    public Optional<Supplier> getSupplierById(Long id) {

        return supplierRepository.findById(id);
    }

    @Override
    public void deleteSupplier(Long id) {

        supplierRepository.deleteById(id);
    }
}