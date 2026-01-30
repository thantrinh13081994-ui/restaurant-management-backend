package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Bill;

import java.util.List;

public interface BillService {
    List<Bill> getAllBills();

    Bill getBillById(Long id);

    Bill createBill(Bill bill);

    Bill updateBill(Long id, Bill bill);

    void deleteBill(Long id);

    byte[] exportBillToPdf(Long id);
}
